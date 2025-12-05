package org.fauxgartic;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class ServerFauxGartic {
    public static void main(String[] args) throws IOException, InterruptedException {
        int porta = 50051; // Porta padrão do gRPC

        // Cria o servidor e adiciona o nosso serviço (lógica do jogo)
        Server servidor = ServerBuilder.forPort(porta)
                // MUDANÇA: Usando a nova classe ServicoFauxGarticImpl
                .addService(new ServicoFauxGarticImpl())
                .build();

        System.out.println("--- Servidor FauxGartic (gRPC) Iniciado ---");
        System.out.println("Escutando na porta " + porta);
        System.out.println("Aguardando jogadores...");

        servidor.start();

        // Mantém o servidor rodando até ser fechado manualmente
        servidor.awaitTermination();
    }
}