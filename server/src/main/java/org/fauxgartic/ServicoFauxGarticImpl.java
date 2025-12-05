package org.fauxgartic;

import io.grpc.stub.StreamObserver;
import org.fauxgartic.grpc.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServicoFauxGarticImpl extends FauxGarticServiceGrpc.FauxGarticServiceImplBase {

    // --- Estado do Jogo ---
    private final List<String> bancoDePalavras = Arrays.asList(
            "Gato", "Cachorro", "Elefante", "Leão", "Boi", "Cama", "Caneta", "Casa", "Banco", "Peixe",
            "Avião", "Carro", "Computador", "Banana", "Abacaxi", "Sol", "Lua", "Estrela"
    );
    private String palavraAtual = "";
    private String idDesenhistaAtual = "";

    // Mapeamentos
    private Map<String, String> mapNomes = new ConcurrentHashMap<>();
    private Map<String, StreamObserver<EventoDeJogo>> mapObservadores = new ConcurrentHashMap<>();

    // MUDANÇA: Lista para garantir a ordem da fila (Circular)
    private final List<String> filaJogadores = new ArrayList<>();

    public ServicoFauxGarticImpl() {
        sortearNovaPalavra();
    }

    @Override
    public void entrarNoJogo(Jogador request, StreamObserver<EstadoDoJogo> responseObserver) {
        String idJogador = request.getId().isEmpty() ? request.getNome() : request.getId(); // Fallback simples
        String nome = request.getNome();

        // Registra jogador
        mapNomes.put(idJogador, nome);

        // Adiciona na fila de forma thread-safe
        synchronized (filaJogadores) {
            if (!filaJogadores.contains(idJogador)) {
                filaJogadores.add(idJogador);
                System.out.println("Adicionado à fila: " + nome + ". Total: " + filaJogadores.size());
            }
        }

        // Se for o primeiro, já assume como desenhista
        if (filaJogadores.size() == 1) {
            idDesenhistaAtual = idJogador;
        }

        boolean souDesenhista = idJogador.equals(idDesenhistaAtual);

        EstadoDoJogo estado = EstadoDoJogo.newBuilder()
                .setSouODesenhista(souDesenhista)
                .setDesenhistaAtual(mapNomes.getOrDefault(idDesenhistaAtual, "Carregando..."))
                .setPalavraAtual(souDesenhista ? palavraAtual : "")
                .build();

        responseObserver.onNext(estado);
        responseObserver.onCompleted();

        transmitirMensagemChat("SERVER", nome + " entrou na sala!");
    }

    @Override
    public void receberEventos(Jogador request, StreamObserver<EventoDeJogo> responseObserver) {
        String id = request.getId().isEmpty() ? request.getNome() : request.getId();
        mapObservadores.put(id, responseObserver);
        System.out.println("Stream conectado: " + id);
    }

    @Override
    public void enviarAcao(AcaoJogador request, StreamObserver<Vazio> responseObserver) {
        String idJogador = request.getJogador().getId();
        String nomeJogador = mapNomes.getOrDefault(idJogador, request.getJogador().getNome());

        // 1. DESENHO
        if (request.hasTraco()) {
            EventoDeJogo evento = EventoDeJogo.newBuilder().setDesenho(request.getTraco()).build();
            transmitirEvento(evento);
        }

        // 2. PALPITE / CHAT
        else if (request.hasPalpite()) {
            String texto = request.getPalpite();

            // Verifica vitória (Case insensitive)
            if (idJogador.equals(idDesenhistaAtual)) {
                // Desenhista não pode adivinhar, mas pode falar coisas que não sejam a palavra
                transmitirMensagemChat(nomeJogador, texto);
            }
            else if (texto.equalsIgnoreCase(palavraAtual)) {
                transmitirMensagemChat("SERVER", ">>> " + nomeJogador + " ACERTOU! A palavra era: " + palavraAtual.toUpperCase());
                iniciarNovaRodada();
            } else {
                transmitirMensagemChat(nomeJogador, texto);
            }
        }

        // 3. LIMPAR TELA
        else if (request.getLimparTela()) {
            EventoDeJogo evento = EventoDeJogo.newBuilder().setLimparTela(true).build();
            transmitirEvento(evento);
        }

        responseObserver.onNext(Vazio.newBuilder().build());
        responseObserver.onCompleted();
    }

    private void iniciarNovaRodada() {
        sortearNovaPalavra();

        // MUDANÇA: Lógica de Fila Circular Robusta
        synchronized (filaJogadores) {
            if (filaJogadores.isEmpty()) return;

            int indexAtual = filaJogadores.indexOf(idDesenhistaAtual);

            // Se o desenhista atual saiu ou bugou, começa do zero, senão pega o próximo
            if (indexAtual == -1) indexAtual = -1;

            // Tenta achar o próximo jogador válido (loop para pular desconectados)
            String novoDesenhistaId = null;
            int tentativas = 0;
            int proximoIndex = indexAtual;

            while (tentativas < filaJogadores.size()) {
                proximoIndex = (proximoIndex + 1) % filaJogadores.size();
                String candidatoId = filaJogadores.get(proximoIndex);

                // Verifica se o jogador ainda está no mapa de nomes/observadores (online)
                if (mapObservadores.containsKey(candidatoId)) {
                    novoDesenhistaId = candidatoId;
                    break;
                }
                tentativas++;
            }

            if (novoDesenhistaId != null) {
                idDesenhistaAtual = novoDesenhistaId;
            } else {
                // Se ninguém estiver online, mantém ou reseta
                System.out.println("Nenhum jogador apto para a próxima rodada.");
            }
        }

        String nomeDesenhista = mapNomes.getOrDefault(idDesenhistaAtual, "Desconhecido");

        Rodada rodada = Rodada.newBuilder()
                .setNomeDesenhista(nomeDesenhista)
                .setPalavraSecreta(palavraAtual)
                .build();

        // 1. Avisa mudança de rodada
        EventoDeJogo evtRodada = EventoDeJogo.newBuilder().setMudancaRodada(rodada).build();
        transmitirEvento(evtRodada);

        // 2. Limpa a tela para o novo desenho
        EventoDeJogo evtLimpar = EventoDeJogo.newBuilder().setLimparTela(true).build();
        transmitirEvento(evtLimpar);

        transmitirMensagemChat("SERVER", "--- VEZ DE: " + nomeDesenhista + " ---");
    }

    private void sortearNovaPalavra() {
        palavraAtual = bancoDePalavras.get(new Random().nextInt(bancoDePalavras.size()));
    }

    private void transmitirMensagemChat(String autor, String msg) {
        String formatada = autor.equals("SERVER") ? msg : (autor + ": " + msg);
        EventoDeJogo evento = EventoDeJogo.newBuilder().setMensagemChat(formatada).build();
        transmitirEvento(evento);
    }

    private void transmitirEvento(EventoDeJogo evento) {
        Set<String> keys = new HashSet<>(mapObservadores.keySet());

        for (String id : keys) {
            StreamObserver<EventoDeJogo> obs = mapObservadores.get(id);
            if (obs == null) continue;

            synchronized (obs) {
                try {
                    obs.onNext(evento);
                } catch (Exception e) {
                    System.out.println("Removendo cliente inativo: " + id);
                    mapObservadores.remove(id);
                    mapNomes.remove(id);
                    synchronized (filaJogadores) {
                        filaJogadores.remove(id);
                    }
                }
            }
        }
    }
}