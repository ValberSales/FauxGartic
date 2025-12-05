import sys
import threading
import grpc
import pygame

# Importa os módulos gerados pelo gRPC (certifique-se de que foram gerados!)
import fauxgartic_pb2
import fauxgartic_pb2_grpc

# --- Configurações do Jogo ---
SERVER_ADDRESS = 'localhost:50051'
WINDOW_WIDTH = 800
WINDOW_HEIGHT = 600
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
GRAY = (200, 200, 200)

class FauxGarticClient:
    def __init__(self):
        self.channel = None
        self.stub = None
        self.jogador_id = None
        self.nome_jogador = ""
        
        # Estado do Jogo
        self.sou_desenhista = False
        self.desenhista_atual = ""
        self.palavra_atual = ""
        self.running = True
        
        # PyGame
        pygame.init()
        self.screen = pygame.display.set_mode((WINDOW_WIDTH, WINDOW_HEIGHT))
        pygame.display.set_caption("FauxGartic (Cliente Python)")
        self.clock = pygame.time.Clock()
        self.font = pygame.font.SysFont('Arial', 24)
        
        # Surface para o desenho (persistente)
        self.canvas = pygame.Surface((WINDOW_WIDTH, WINDOW_HEIGHT - 100))
        self.canvas.fill(WHITE)
        
        # Variáveis de desenho local
        self.desenhando = False
        self.last_pos = None

    def conectar(self):
        """Conecta ao servidor gRPC e inicia o stream."""
        print(f"Conectando a {SERVER_ADDRESS}...")
        self.channel = grpc.insecure_channel(SERVER_ADDRESS)
        self.stub = fauxgartic_pb2_grpc.FauxGarticServiceStub(self.channel)

        # 1. Login (EntrarNoJogo)
        # Pede o nome via terminal (simplificação)
        self.nome_jogador = input("Digite seu nome: ") or "JogadorPy"
        
        # Monta a mensagem Jogador
        request = fauxgartic_pb2.Jogador(nome=self.nome_jogador)
        
        try:
            # Chama o método remoto
            estado_inicial = self.stub.EntrarNoJogo(request)
            
            # O servidor não retorna o ID gerado na resposta do EntrarNoJogo no nosso proto atual,
            # ele retorna o Estado. O ID deve ser gerado pelo cliente ou o proto ajustado.
            # *AJUSTE TÉCNICO*: No Java, geramos o ID mas não devolvemos no 'Jogador' de resposta.
            # Vamos gerar um ID temporário aqui para o stream funcionar, 
            # mas o ideal seria o servidor devolver o ID.
            # Para este exemplo funcionar sem mudar o proto de novo, vamos usar o NOME como ID
            # (assumindo nomes únicos por enquanto).
            self.jogador_id = self.nome_jogador 
            
            self.sou_desenhista = estado_inicial.sou_o_desenhista
            self.desenhista_atual = estado_inicial.desenhista_atual
            if self.sou_desenhista:
                self.palavra_atual = estado_inicial.palavra_atual
            
            print(f"Conectado! Desenhista: {self.desenhista_atual}")
            
            # 2. Inicia a Thread de Recebimento (Stream)
            threading.Thread(target=self.receber_eventos, daemon=True).start()
            
        except grpc.RpcError as e:
            print(f"Erro ao conectar: {e}")
            self.running = False

    def receber_eventos(self):
        """Loop que fica ouvindo o servidor (Stream)."""
        request = fauxgartic_pb2.Jogador(id=self.jogador_id, nome=self.nome_jogador)
        
        try:
            # Abre o stream
            eventos = self.stub.ReceberEventos(request)
            
            for evento in eventos:
                if not self.running: break
                
                # --- Tipo: Desenho ---
                if evento.HasField('desenho'):
                    traco = evento.desenho
                    self.desenhar_remoto(traco)
                
                # --- Tipo: Chat ---
                elif evento.HasField('mensagem_chat'):
                    print(f"[CHAT] {evento.mensagem_chat}")
                
                # --- Tipo: Mudança de Rodada ---
                elif evento.HasField('mudanca_rodada'):
                    rodada = evento.mudanca_rodada
                    self.desenhista_atual = rodada.nome_desenhista
                    self.sou_desenhista = (self.desenhista_atual == self.nome_jogador)
                    
                    print(f"\n>>> NOVA RODADA! Desenhista: {self.desenhista_atual}")
                    if self.sou_desenhista:
                        self.palavra_atual = rodada.palavra_secreta
                        print(f">>> SUA VEZ! DESENHE: {self.palavra_atual}")
                    else:
                        self.palavra_atual = ""
                        print(">>> Adivinhe o desenho!")
                        
                # --- Tipo: Limpar Tela ---
                elif evento.limpar_tela:
                    self.canvas.fill(WHITE)

        except grpc.RpcError as e:
            print(f"Erro no stream: {e}")
            self.running = False

    def desenhar_remoto(self, traco):
        """Desenha algo que veio do servidor."""
        # Se for 'novo_traco' (clique), não desenhamos linha, apenas guardamos
        # Se for arrasto, desenhamos linha
        color = BLACK # Simplificação, poderia converter traco.cor
        
        # A lógica aqui depende de como o servidor manda. 
        # Vamos desenhar um círculo simples para cada ponto recebido para simplificar
        pygame.draw.circle(self.canvas, color, (int(traco.x), int(traco.y)), 3)

    def enviar_traco(self, x, y, novo_traco=False):
        """Envia um traço para o servidor."""
        if not self.sou_desenhista: return

        traco = fauxgartic_pb2.Traco(x=x, y=y, cor="BLACK", novo_traco=novo_traco)
        acao = fauxgartic_pb2.AcaoJogador(
            jogador=fauxgartic_pb2.Jogador(id=self.jogador_id),
            traco=traco
        )
        self.stub.EnviarAcao(acao)
        
        # Desenha localmente também para não ter lag
        pygame.draw.circle(self.canvas, BLACK, (int(x), int(y)), 3)

    def enviar_palpite(self, texto):
        """Envia um palpite (chat)."""
        acao = fauxgartic_pb2.AcaoJogador(
            jogador=fauxgartic_pb2.Jogador(id=self.jogador_id),
            palpite=texto
        )
        self.stub.EnviarAcao(acao)

    def enviar_limpar(self):
        """Pede para limpar a tela."""
        if not self.sou_desenhista: return
        acao = fauxgartic_pb2.AcaoJogador(
            jogador=fauxgartic_pb2.Jogador(id=self.jogador_id),
            limpar_tela=True
        )
        self.stub.EnviarAcao(acao)
        self.canvas.fill(WHITE)

    def run(self):
        """Loop principal do PyGame."""
        self.conectar()
        
        input_text = ""
        
        while self.running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    self.running = False
                
                # --- Inputs do Mouse (Desenho) ---
                if self.sou_desenhista:
                    if event.type == pygame.MOUSEBUTTONDOWN:
                        if event.pos[1] < 500: # Se estiver na área de desenho
                            self.desenhando = True
                            self.enviar_traco(event.pos[0], event.pos[1], novo_traco=True)
                    
                    elif event.type == pygame.MOUSEBUTTONUP:
                        self.desenhando = False
                    
                    elif event.type == pygame.MOUSEMOTION:
                        if self.desenhando and event.pos[1] < 500:
                            self.enviar_traco(event.pos[0], event.pos[1], novo_traco=False)
                            
                    elif event.type == pygame.KEYDOWN:
                        if event.key == pygame.K_c: # Tecla 'C' limpa a tela
                            self.enviar_limpar()

                # --- Inputs de Teclado (Chat/Palpite) ---
                if not self.sou_desenhista:
                    if event.type == pygame.KEYDOWN:
                        if event.key == pygame.K_RETURN:
                            if input_text:
                                self.enviar_palpite(input_text)
                                input_text = ""
                        elif event.key == pygame.K_BACKSPACE:
                            input_text = input_text[:-1]
                        else:
                            input_text += event.unicode

            # --- Renderização ---
            self.screen.fill(GRAY)
            
            # 1. Desenha o Canvas
            self.screen.blit(self.canvas, (0, 0))
            
            # 2. Desenha a Interface (HUD)
            # Barra inferior
            pygame.draw.rect(self.screen, GRAY, (0, 500, 800, 100))
            
            # Info do jogo
            status_text = f"Desenhista: {self.desenhista_atual}"
            if self.sou_desenhista:
                status_text += f" | PALAVRA: {self.palavra_atual} (Desenhe!)"
            else:
                status_text += " | Adivinhe!"
                
            text_surf = self.font.render(status_text, True, BLACK)
            self.screen.blit(text_surf, (10, 510))
            
            # Input de Chat (só se não for desenhista)
            if not self.sou_desenhista:
                input_surf = self.font.render(f"Palpite: {input_text}", True, (0, 0, 150))
                self.screen.blit(input_surf, (10, 550))
            else:
                help_surf = self.font.render("Aperte 'C' para limpar a tela", True, (100, 100, 100))
                self.screen.blit(help_surf, (10, 550))

            pygame.display.flip()
            self.clock.tick(60)

        pygame.quit()
        if self.channel: self.channel.close()
        sys.exit()

if __name__ == "__main__":
    client = FauxGarticClient()
    client.run()