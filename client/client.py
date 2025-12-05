import sys
import threading
import grpc
import pygame

import fauxgartic_pb2
import fauxgartic_pb2_grpc

# --- CONSTANTES VISUAIS ---
SERVER_ADDRESS = 'localhost:50051'
WIDTH, HEIGHT = 1024, 768  # Aumentei a tela para caber melhor
COLOR_WHITE = (255, 255, 255)
COLOR_BLACK = (0, 0, 0)
COLOR_GRAY_BG = (50, 50, 50)     # Fundo Geral
COLOR_CANVAS_BG = (255, 255, 255)
COLOR_CHAT_BG = (230, 230, 230)
COLOR_ACCENT = (70, 130, 180)    # Azul suave
COLOR_BTN_SEND = (0, 180, 0)     # Verde para botão enviar
COLOR_BTN_HOVER = (0, 220, 0)

# --- CLASSE BOTÃO ---
class Button:
    def __init__(self, x, y, w, h, text, font, color=COLOR_BTN_SEND):
        self.rect = pygame.Rect(x, y, w, h)
        self.text = text
        self.font = font
        self.color = color
        self.hover_color = COLOR_BTN_HOVER
        self.is_hovered = False

    def draw(self, screen):
        col = self.hover_color if self.is_hovered else self.color
        pygame.draw.rect(screen, col, self.rect, border_radius=5)
        pygame.draw.rect(screen, COLOR_BLACK, self.rect, 2, border_radius=5) # Borda
        
        txt_surf = self.font.render(self.text, True, COLOR_WHITE)
        # Centraliza texto no botão
        text_x = self.rect.x + (self.rect.width - txt_surf.get_width()) // 2
        text_y = self.rect.y + (self.rect.height - txt_surf.get_height()) // 2
        screen.blit(txt_surf, (text_x, text_y))

    def handle_event(self, event):
        if event.type == pygame.MOUSEMOTION:
            self.is_hovered = self.rect.collidepoint(event.pos)
        
        if event.type == pygame.MOUSEBUTTONDOWN:
            if self.is_hovered:
                return True
        return False

# --- CLASSE INPUT BOX ---
class InputBox:
    def __init__(self, x, y, w, h, text='', font=None):
        self.rect = pygame.Rect(x, y, w, h)
        self.color_inactive = (200, 200, 200)
        self.color_active = (70, 130, 180) # Borda azul quando foca
        self.color_bg = COLOR_WHITE        # Fundo Branco (Correção Solicitada)
        self.color_text = COLOR_BLACK      # Texto Preto (Correção Solicitada)
        
        self.text = text
        self.font = font or pygame.font.Font(None, 32)
        self.txt_surface = self.font.render(text, True, self.color_text)
        self.active = False

    def handle_event(self, event):
        if event.type == pygame.MOUSEBUTTONDOWN:
            if self.rect.collidepoint(event.pos):
                self.active = not self.active
            else:
                self.active = False
        
        if event.type == pygame.KEYDOWN:
            if self.active:
                if event.key == pygame.K_RETURN:
                    return self.text
                elif event.key == pygame.K_BACKSPACE:
                    self.text = self.text[:-1]
                else:
                    self.text += event.unicode
                self.txt_surface = self.font.render(self.text, True, self.color_text)
        return None

    def draw(self, screen):
        # Desenha Fundo
        pygame.draw.rect(screen, self.color_bg, self.rect)
        # Desenha Texto
        screen.blit(self.txt_surface, (self.rect.x+5, self.rect.y+10)) # +10 para centralizar vert
        # Desenha Borda
        color = self.color_active if self.active else self.color_inactive
        pygame.draw.rect(screen, color, self.rect, 2)
        
    def clear(self):
        self.text = ""
        self.txt_surface = self.font.render("", True, self.color_text)

# --- CLIENTE PRINCIPAL ---
class FauxGarticClient:
    def __init__(self):
        pygame.init()
        self.screen = pygame.display.set_mode((WIDTH, HEIGHT))
        pygame.display.set_caption("FauxGartic Melhorado")
        self.clock = pygame.time.Clock()
        
        # FONTES MAIORES
        self.font = pygame.font.SysFont('Verdana', 20)      # Chat
        self.font_big = pygame.font.SysFont('Verdana', 28)  # Títulos
        self.font_bold = pygame.font.SysFont('Verdana', 20, bold=True)

        self.state = "LOGIN"
        self.running = True
        
        # gRPC
        self.channel = None
        self.stub = None
        self.nome_jogador = ""
        self.id_jogador = ""
        
        # Jogo
        self.sou_desenhista = False
        self.desenhista_atual = ""
        self.palavra_atual = ""
        
        # Layout Calculado
        CHAT_WIDTH = 300
        CANVAS_WIDTH = WIDTH - CHAT_WIDTH - 30
        
        # Canvas
        self.canvas_rect = pygame.Rect(10, 80, CANVAS_WIDTH, HEIGHT - 100)
        self.canvas_surf = pygame.Surface((self.canvas_rect.width, self.canvas_rect.height))
        self.canvas_surf.fill(COLOR_CANVAS_BG)
        
        # Chat
        self.chat_history = []
        self.chat_scroll_offset = 0
        self.chat_rect = pygame.Rect(WIDTH - CHAT_WIDTH - 10, 80, CHAT_WIDTH, HEIGHT - 150)
        
        # UI Componentes
        # Login Box
        self.input_login = InputBox(WIDTH//2 - 150, HEIGHT//2, 300, 50, font=self.font_big)
        
        # Chat Input Box
        self.input_chat = InputBox(self.chat_rect.x, HEIGHT - 60, CHAT_WIDTH - 90, 50, font=self.font)
        
        # Botão Enviar
        self.btn_send = Button(self.chat_rect.x + CHAT_WIDTH - 85, HEIGHT - 60, 85, 50, "Enviar", self.font_bold)
        
        # Controle de Desenho
        self.drawing_local = False
        self.last_pos_local = None
        self.last_pos_remote = None 

    def connect_server(self, nome):
        try:
            print(f"Conectando como {nome}...")
            self.channel = grpc.insecure_channel(SERVER_ADDRESS)
            self.stub = fauxgartic_pb2_grpc.FauxGarticServiceStub(self.channel)
            
            self.nome_jogador = nome
            self.id_jogador = nome 
            
            req = fauxgartic_pb2.Jogador(nome=nome, id=self.id_jogador)
            estado = self.stub.EntrarNoJogo(req)
            
            self.sou_desenhista = estado.sou_o_desenhista
            self.desenhista_atual = estado.desenhista_atual
            if self.sou_desenhista:
                self.palavra_atual = estado.palavra_atual
            
            threading.Thread(target=self.listen_stream, daemon=True).start()
            self.state = "GAME"
            
        except Exception as e:
            print(f"Erro: {e}")

    def listen_stream(self):
        try:
            req = fauxgartic_pb2.Jogador(id=self.id_jogador, nome=self.nome_jogador)
            stream = self.stub.ReceberEventos(req)
            
            for evento in stream:
                if not self.running: break
                
                if evento.HasField('desenho'):
                    self.processar_desenho_remoto(evento.desenho)
                    
                elif evento.HasField('mensagem_chat'):
                    self.adicionar_chat(evento.mensagem_chat)
                    
                elif evento.HasField('mudanca_rodada'):
                    r = evento.mudanca_rodada
                    self.desenhista_atual = r.nome_desenhista
                    self.sou_desenhista = (self.desenhista_atual == self.nome_jogador)
                    self.palavra_atual = r.palavra_secreta if self.sou_desenhista else ""
                    self.adicionar_chat(f"--- NOVA RODADA: {self.desenhista_atual} ---")
                    
                elif evento.limpar_tela:
                    self.canvas_surf.fill(COLOR_CANVAS_BG)
                    self.last_pos_remote = None
                    self.last_pos_local = None

        except Exception as e:
            print(f"Stream error: {e}")
            self.running = False

    def processar_desenho_remoto(self, traco):
        x, y = int(traco.x), int(traco.y)
        if traco.novo_traco:
            self.last_pos_remote = (x, y)
            pygame.draw.circle(self.canvas_surf, COLOR_BLACK, (x, y), 3)
        else:
            if self.last_pos_remote:
                pygame.draw.line(self.canvas_surf, COLOR_BLACK, self.last_pos_remote, (x, y), 6)
            self.last_pos_remote = (x, y)

    def enviar_traco(self, x, y, novo=False):
        if not self.sou_desenhista: return
        t = fauxgartic_pb2.Traco(x=float(x), y=float(y), cor="BLACK", novo_traco=novo)
        acao = fauxgartic_pb2.AcaoJogador(jogador=fauxgartic_pb2.Jogador(id=self.id_jogador), traco=t)
        self.stub.EnviarAcao(acao)

    def enviar_chat(self, texto):
        if not texto.strip(): return
        acao = fauxgartic_pb2.AcaoJogador(
            jogador=fauxgartic_pb2.Jogador(id=self.id_jogador, nome=self.nome_jogador),
            palpite=texto
        )
        self.stub.EnviarAcao(acao)

    def enviar_limpar(self):
        if not self.sou_desenhista: return
        acao = fauxgartic_pb2.AcaoJogador(jogador=fauxgartic_pb2.Jogador(id=self.id_jogador), limpar_tela=True)
        self.stub.EnviarAcao(acao)

    def adicionar_chat(self, msg):
        self.chat_history.append(msg)
        if len(self.chat_history) > 18:
            self.chat_scroll_offset = len(self.chat_history) - 18

    def processar_envio_mensagem(self):
        """Pega o texto da caixa e envia"""
        msg = self.input_chat.text
        if msg:
            self.enviar_chat(msg)
            self.input_chat.clear()

    def draw_ui_login(self):
        self.screen.fill(COLOR_GRAY_BG)
        title = self.font_big.render("FAUX GARTIC - LOGIN", True, COLOR_WHITE)
        self.screen.blit(title, (WIDTH//2 - title.get_width()//2, HEIGHT//2 - 80))
        
        self.input_login.draw(self.screen)
        
        msg = self.font.render("Digite seu nome e tecle ENTER", True, (200, 200, 200))
        self.screen.blit(msg, (WIDTH//2 - msg.get_width()//2, HEIGHT//2 + 60))

    def draw_ui_game(self):
        self.screen.fill(COLOR_GRAY_BG)
        
        # 1. Barra Superior
        str_status = f"EU: {self.nome_jogador}  |  DESENHISTA: {self.desenhista_atual}"
        lbl_status = self.font_big.render(str_status, True, COLOR_WHITE)
        self.screen.blit(lbl_status, (15, 15))
        
        # Palavra ou Dica
        if self.sou_desenhista:
            str_word = f"DESENHE: {self.palavra_atual}"
            color_word = (255, 200, 0) # Ouro
        else:
            str_word = "ADIVINHE O DESENHO!"
            color_word = (100, 255, 100) # Verde claro
            
        lbl_word = self.font_bold.render(str_word, True, color_word)
        self.screen.blit(lbl_word, (15, 50))
        
        if self.sou_desenhista:
            help_c = self.font.render("[TECLE 'C' PARA LIMPAR]", True, (200, 200, 200))
            self.screen.blit(help_c, (WIDTH - 280, 50))
        
        # 2. Canvas
        self.screen.blit(self.canvas_surf, self.canvas_rect)
        pygame.draw.rect(self.screen, COLOR_BLACK, self.canvas_rect, 3) 
        
        # 3. Chat Background
        pygame.draw.rect(self.screen, COLOR_CHAT_BG, self.chat_rect)
        pygame.draw.rect(self.screen, COLOR_BLACK, self.chat_rect, 2)
        
        # 4. Mensagens do Chat
        visible_lines = 18
        start_idx = max(0, self.chat_scroll_offset)
        msgs_slice = self.chat_history[start_idx : start_idx + visible_lines]
        
        line_h = 25
        y_text = self.chat_rect.y + 10
        for m in msgs_slice:
            # Cores Condicionais
            c = COLOR_BLACK
            if "SERVER" in m: c = (150, 0, 0)
            if "ACERTOU" in m: c = (0, 120, 0)
            if "VEZ DE" in m: c = (0, 0, 150)
            
            surf = self.font.render(m, True, c)
            self.screen.blit(surf, (self.chat_rect.x + 8, y_text))
            y_text += line_h
            
        # 5. Área de Input
        if not self.sou_desenhista:
            self.input_chat.draw(self.screen)
            self.btn_send.draw(self.screen)
        else:
            # Mostra mensagem bloqueada
            r = pygame.Rect(self.input_chat.rect)
            r.width = WIDTH - r.x - 10
            pygame.draw.rect(self.screen, (80, 80, 80), r)
            lbl = self.font.render("Você está desenhando!", True, (200, 200, 200))
            self.screen.blit(lbl, (r.x + 20, r.y + 12))

    def run(self):
        while self.running:
            events = pygame.event.get()
            for event in events:
                if event.type == pygame.QUIT:
                    self.running = False
                
                # --- LOGIN ---
                if self.state == "LOGIN":
                    res = self.input_login.handle_event(event)
                    if res: self.connect_server(res)
                        
                # --- GAME ---
                elif self.state == "GAME":
                    if not self.sou_desenhista:
                        # Handle Botão Enviar
                        if self.btn_send.handle_event(event):
                            self.processar_envio_mensagem()
                        
                        # Handle Input Texto (Enter)
                        res = self.input_chat.handle_event(event)
                        if res:
                            self.enviar_chat(res)
                            self.input_chat.clear()
                    
                    # Scroll
                    if event.type == pygame.MOUSEWHEEL:
                        if self.chat_rect.collidepoint(pygame.mouse.get_pos()):
                            self.chat_scroll_offset -= event.y
                            # Clamp
                            max_s = max(0, len(self.chat_history) - 18)
                            self.chat_scroll_offset = max(0, min(self.chat_scroll_offset, max_s))

                    # Tecla Limpar (C)
                    if event.type == pygame.KEYDOWN:
                        if self.sou_desenhista and event.key == pygame.K_c:
                            self.enviar_limpar()

                    # Lógica de Desenho
                    if self.sou_desenhista:
                        mx, my = pygame.mouse.get_pos()
                        rel_x = mx - self.canvas_rect.x
                        rel_y = my - self.canvas_rect.y
                        in_canvas = self.canvas_rect.collidepoint((mx, my))
                        
                        if event.type == pygame.MOUSEBUTTONDOWN and in_canvas:
                            self.drawing_local = True
                            self.last_pos_local = (rel_x, rel_y)
                            pygame.draw.circle(self.canvas_surf, COLOR_BLACK, (rel_x, rel_y), 3)
                            self.enviar_traco(rel_x, rel_y, novo=True)
                        
                        elif event.type == pygame.MOUSEBUTTONUP:
                            self.drawing_local = False
                            self.last_pos_local = None
                        
                        elif event.type == pygame.MOUSEMOTION:
                            if self.drawing_local and in_canvas and self.last_pos_local:
                                pygame.draw.line(self.canvas_surf, COLOR_BLACK, self.last_pos_local, (rel_x, rel_y), 6)
                                self.enviar_traco(rel_x, rel_y, novo=False)
                                self.last_pos_local = (rel_x, rel_y)

            if self.state == "LOGIN":
                self.draw_ui_login()
            else:
                self.draw_ui_game()
                
            pygame.display.flip()
            self.clock.tick(60)

        pygame.quit()
        if self.channel: self.channel.close()
        sys.exit()

if __name__ == "__main__":
    client = FauxGarticClient()
    client.run()