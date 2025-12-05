package org.fauxgartic;

import io.grpc.stub.StreamObserver;
import org.fauxgartic.grpc.*; // Importa as classes geradas
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// CORREÇÃO AQUI: Mudamos de GarticServiceGrpc para FauxGarticServiceGrpc
public class ServicoFauxGarticImpl extends FauxGarticServiceGrpc.FauxGarticServiceImplBase {

    // --- Estado do Jogo ---
    private final List<String> bancoDePalavras = Arrays.asList(
            "Gato", "Cachorro", "Elefante", "Leão", "Boi", "Cama", "Caneta", "Casa", "Banco", "Peixe"
    );
    private String palavraAtual = "";
    private String idDesenhistaAtual = "";

    // Lista de Jogadores (ID -> Nome)
    private Map<String, String> jogadores = new ConcurrentHashMap<>();

    // Canais de comunicação com os clientes (ID -> StreamObserver)
    private Map<String, StreamObserver<EventoDeJogo>> observadoresClientes = new ConcurrentHashMap<>();

    public ServicoFauxGarticImpl() {
        // Inicia o jogo sem ninguém
        sortearNovaPalavra();
    }

    /**
     * Cliente chama isso ao abrir o jogo para se registrar.
     */
    @Override
    public void entrarNoJogo(Jogador request, StreamObserver<EstadoDoJogo> responseObserver) {
        String idJogador = UUID.randomUUID().toString();
        String nome = request.getNome();

        jogadores.put(idJogador, nome);
        System.out.println("Novo jogador entrou: " + nome + " (ID: " + idJogador + ")");

        // Se for o primeiro jogador, ele vira o desenhista
        if (jogadores.size() == 1) {
            idDesenhistaAtual = idJogador;
            System.out.println("Ele é o primeiro, então será o Desenhista.");
        }

        boolean souDesenhista = idJogador.equals(idDesenhistaAtual);

        // Retorna o estado inicial para o cliente
        EstadoDoJogo estado = EstadoDoJogo.newBuilder()
                .setSouODesenhista(souDesenhista)
                .setDesenhistaAtual(jogadores.getOrDefault(idDesenhistaAtual, "Ninguém"))
                .setPalavraAtual(souDesenhista ? palavraAtual : "") // Só manda a palavra se for o desenhista
                .build();

        responseObserver.onNext(estado);
        responseObserver.onCompleted();

        // Avisa os outros que alguém entrou
        transmitirMensagemChat("Servidor: " + nome + " entrou na sala!");
    }

    /**
     * Cliente chama isso para começar a ESCUTAR eventos.
     */
    @Override
    public void receberEventos(Jogador request, StreamObserver<EventoDeJogo> responseObserver) {
        observadoresClientes.put(request.getId(), responseObserver);
    }

    /**
     * Cliente manda uma ação (Desenho ou Palpite).
     */
    @Override
    public void enviarAcao(AcaoJogador request, StreamObserver<Vazio> responseObserver) {
        String idJogador = request.getJogador().getId();
        String nomeJogador = jogadores.get(idJogador);

        // --- Se for um TRAÇO (Desenho) ---
        if (request.hasTraco()) {
            if (idJogador.equals(idDesenhistaAtual)) {
                EventoDeJogo evento = EventoDeJogo.newBuilder()
                        .setDesenho(request.getTraco())
                        .build();
                transmitirEvento(evento);
            }
        }

        // --- Se for um PALPITE (Chat) ---
        else if (request.hasPalpite()) {
            String palpite = request.getPalpite();
            System.out.println("Palpite de " + nomeJogador + ": " + palpite);

            if (palpite.equalsIgnoreCase(palavraAtual)) {
                // ACERTOU!
                transmitirMensagemChat("**********************************");
                transmitirMensagemChat(nomeJogador + " ACERTOU A PALAVRA!");
                transmitirMensagemChat("A palavra era: " + palavraAtual);
                transmitirMensagemChat("**********************************");

                iniciarNovaRodada();
            } else {
                // ERROU
                transmitirMensagemChat(nomeJogador + ": " + palpite);
            }
        }

        // --- Se for LIMPAR TELA ---
        else if (request.getLimparTela()) {
            if (idJogador.equals(idDesenhistaAtual)) {
                EventoDeJogo evento = EventoDeJogo.newBuilder().setLimparTela(true).build();
                transmitirEvento(evento);
            }
        }

        // Confirma recebimento
        responseObserver.onNext(Vazio.newBuilder().build());
        responseObserver.onCompleted();
    }

    // --- Métodos Auxiliares ---

    private void iniciarNovaRodada() {
        sortearNovaPalavra();

        List<String> ids = new ArrayList<>(jogadores.keySet());
        if (!ids.isEmpty()) {
            int indiceAtual = ids.indexOf(idDesenhistaAtual);
            int proximoIndice = (indiceAtual + 1) % ids.size();
            idDesenhistaAtual = ids.get(proximoIndice);
        }

        String nomeDesenhista = jogadores.get(idDesenhistaAtual);
        System.out.println("Nova rodada! Desenhista: " + nomeDesenhista + " | Palavra: " + palavraAtual);

        Rodada rodada = Rodada.newBuilder()
                .setNomeDesenhista(nomeDesenhista)
                .setPalavraSecreta(palavraAtual)
                .build();

        EventoDeJogo evento = EventoDeJogo.newBuilder()
                .setMudancaRodada(rodada)
                .build();

        transmitirEvento(evento);
        transmitirMensagemChat("--- NOVA RODADA! O desenhista é " + nomeDesenhista + " ---");
        transmitirEvento(EventoDeJogo.newBuilder().setLimparTela(true).build());
    }

    private void sortearNovaPalavra() {
        palavraAtual = bancoDePalavras.get(new Random().nextInt(bancoDePalavras.size()));
    }

    private void transmitirMensagemChat(String msg) {
        EventoDeJogo evento = EventoDeJogo.newBuilder()
                .setMensagemChat(msg)
                .build();
        transmitirEvento(evento);
    }

    private void transmitirEvento(EventoDeJogo evento) {
        for (StreamObserver<EventoDeJogo> observer : observadoresClientes.values()) {
            try {
                observer.onNext(evento);
            } catch (Exception e) {
                // Cliente desconectou
            }
        }
    }
}