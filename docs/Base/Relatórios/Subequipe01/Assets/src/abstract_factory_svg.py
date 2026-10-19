#!/usr/bin/env python3
"""Gera os dois SVGs do GoF Criacional (Abstract Factory) da Subequipe 01.

    AbstractFactoryGenerico.svg  -- a estrutura do padrao (GAMMA et al., 1995)
    AbstractFactoryAplicado.svg  -- o padrao aplicado aos meios de pagamento

Layout fixo por coordenada. Paleta e tipografia iguais as dos SVGs do Decorator,
para que as quatro imagens da entrega leiam como um conjunto so.

Autor: Patrick Anderson Carvalho dos Santos (modelagem do criacional).

    python3 abstract_factory_svg.py   ->  ../AbstractFactory*.svg
"""

import os
from xml.sax.saxutils import escape

FILL, LINE, INK, SUB = "#F8FAFC", "#334155", "#000000", "#475569"
NOTE_FILL, NOTE_LINE, LEG_FILL = "#FFFBEB", "#92400E", "#DDDDDD"
PEND_FILL = "#F1F5F9"
FONT = "Arial"
CH = 0.55          # largura media de caractere, fracao do corpo


def tw(txt, size):
    return len(txt) * size * CH


def text(x, y, s, size=12, bold=False, fill=INK, anchor="start", italic=False):
    a = {"start": "", "middle": ' text-anchor="middle"', "end": ' text-anchor="end"'}[anchor]
    b = ' font-weight="bold"' if bold else ""
    i = ' font-style="italic"' if italic else ""
    return (f'<text x="{x}" y="{y}" font-family="{FONT}" font-size="{size}"'
            f' fill="{fill}"{b}{i}{a}>{escape(s)}</text>')


class Caixa:
    """Classificador UML: estereotipo opcional, nome e compartimentos."""

    def __init__(self, x, y, nome, membros=(), estereotipo=None, largura=None,
                 fill=FILL, dash=False, nota=None):
        self.x, self.y, self.nome = x, y, nome
        self.membros = list(membros)
        self.estereotipo = estereotipo
        self.fill, self.dash, self.nota = fill, dash, nota
        precisa = max([tw(nome, 12.5)] + [tw(m, 11) for m in self.membros]
                      + ([tw(estereotipo, 11)] if estereotipo else [0])) + 24
        self.w = largura or max(150, int(precisa))
        self.h = 24 + (15 if estereotipo else 0) + (8 + 14 * len(self.membros) if self.membros else 0)
        if nota:
            self.h += 14

    @property
    def cx(self):
        return self.x + self.w / 2

    @property
    def cy(self):
        return self.y + self.h / 2

    def svg(self):
        sd = ' stroke-dasharray="5,4"' if self.dash else ""
        out = [f'<rect x="{self.x}" y="{self.y}" width="{self.w}" height="{self.h}" rx="2.5" '
               f'fill="{self.fill}" stroke="{LINE}" stroke-width="0.6"{sd}/>']
        y = self.y + 15
        if self.estereotipo:
            out.append(text(self.cx, y, f"«{self.estereotipo}»", 11, False, SUB, "middle"))
            y += 15
        out.append(text(self.cx, y, self.nome, 12.5, True, INK, "middle"))
        y += 7
        if self.membros:
            out.append(f'<line x1="{self.x}" x2="{self.x + self.w}" y1="{y}" y2="{y}" '
                       f'stroke="{LINE}" stroke-width="0.6"/>')
            y += 14
            for m in self.membros:
                out.append(text(self.x + 7, y, m, 11, False, INK))
                y += 14
            y -= 14
        if self.nota:
            out.append(text(self.cx, y + 13, self.nota, 10.5, False, SUB, "middle"))
        return out


def borda(a, b):
    """Ponto de saida de a na direcao de b, preso ao retangulo."""
    dx, dy = b.cx - a.cx, b.cy - a.cy
    if dx == 0 and dy == 0:
        return a.cx, a.cy
    ex = a.w / 2 / abs(dx) if dx else float("inf")
    ey = a.h / 2 / abs(dy) if dy else float("inf")
    t = min(ex, ey)
    return a.cx + dx * t, a.cy + dy * t


def seta(a, b, tipo="assoc", rotulo=None, rx=None, ry=None):
    """tipo: assoc (cheia, aberta) | realiza (tracejada, triangulo) | usa (tracejada, aberta)."""
    x1, y1 = borda(a, b)
    x2, y2 = borda(b, a)
    dx, dy = x2 - x1, y2 - y1
    n = (dx * dx + dy * dy) ** 0.5 or 1
    ux, uy = dx / n, dy / n
    px, py = -uy, ux
    tracejada = tipo in ("realiza", "usa")
    sd = ' stroke-dasharray="6,5"' if tracejada else ""
    out = [f'<path d="M{x1:.1f},{y1:.1f} L{x2:.1f},{y2:.1f}" fill="none" '
           f'stroke="{LINE}" stroke-width="1"{sd}/>']
    if tipo == "realiza":
        L, W = 13.0, 6.5
        p1 = (x2 - L * ux + W * px, y2 - L * uy + W * py)
        p2 = (x2 - L * ux - W * px, y2 - L * uy - W * py)
        out.append(f'<polygon points="{x2:.1f},{y2:.1f} {p1[0]:.1f},{p1[1]:.1f} '
                   f'{p2[0]:.1f},{p2[1]:.1f}" fill="#FFFFFF" stroke="{LINE}" stroke-width="1"/>')
    else:
        L, W = 11.0, 4.5
        p1 = (x2 - L * ux + W * px, y2 - L * uy + W * py)
        p2 = (x2 - L * ux - W * px, y2 - L * uy - W * py)
        out.append(f'<path d="M{p1[0]:.1f},{p1[1]:.1f} L{x2:.1f},{y2:.1f} L{p2[0]:.1f},{p2[1]:.1f}" '
                   f'fill="none" stroke="{LINE}" stroke-width="1"/>')
    if rotulo:
        mx = rx if rx is not None else (x1 + x2) / 2
        my = ry if ry is not None else (y1 + y2) / 2 - 5
        out.append(text(mx, my, rotulo, 10.5, False, SUB, "middle"))
    return out


def nota(x, y, w, linhas):
    h = 12 + 13 * len(linhas)
    d = 9
    out = [f'<path d="M{x},{y} L{x+w-d},{y} L{x+w},{y+d} L{x+w},{y+h} L{x},{y+h} Z" '
           f'fill="{NOTE_FILL}" stroke="{NOTE_LINE}" stroke-width="0.6"/>',
           f'<path d="M{x+w-d},{y} L{x+w-d},{y+d} L{x+w},{y+d}" fill="none" '
           f'stroke="{NOTE_LINE}" stroke-width="0.6"/>']
    ty = y + 16
    for l in linhas:
        out.append(text(x + 8, ty, l, 11))
        ty += 13
    return out, h


def legenda(x, y, w, linhas, col2):
    rh = 18
    out = [f'<rect x="{x}" y="{y}" width="{w}" height="{rh*len(linhas)}" rx="6" '
           f'fill="{LEG_FILL}" stroke="{INK}" stroke-width="0.9"/>']
    for i, (a, b) in enumerate(linhas):
        yy = y + rh * i + 13
        out += [text(x + 9, yy, a, 11.5, i == 0), text(x + col2, yy, b, 11.5, i == 0)]
        if i:
            out.append(f'<line x1="{x}" x2="{x+w}" y1="{y+rh*i}" y2="{y+rh*i}" '
                       f'stroke="{INK}" stroke-width="0.5"/>')
    out.append(f'<line x1="{x+col2-10}" x2="{x+col2-10}" y1="{y}" y2="{y+rh*len(linhas)}" '
               f'stroke="{INK}" stroke-width="0.5"/>')
    return out, rh * len(linhas)


def documento(w, h, titulo, subtitulo, corpo):
    b = ['<?xml version="1.0" encoding="UTF-8" standalone="no"?>',
         f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" '
         f'viewBox="0 0 {w} {h}" style="background:#FFFFFF">',
         f'<rect width="{w}" height="{h}" fill="#FFFFFF"/>',
         text(w / 2, 25, titulo, 14, True, INK, "middle"),
         text(w / 2, 42, subtitulo, 11, True, SUB, "middle")]
    b += corpo
    b.append('</svg>')
    return "\n".join(b)


def cruza_caixa(a, b, caixas, nome):
    """Uma aresta nao pode atravessar nenhum classificador que nao seja a sua ponta."""
    x1, y1 = borda(a, b)
    x2, y2 = borda(b, a)
    for c in caixas:
        if c is a or c is b:
            continue
        for k in range(1, 100):
            t = k / 100.0
            x = x1 + (x2 - x1) * t
            y = y1 + (y2 - y1) * t
            if c.x < x < c.x + c.w and c.y < y < c.y + c.h:
                raise SystemExit(f"{nome}: aresta {a.nome} -> {b.nome} atravessa {c.nome}")


def conferir_nota(caixas, x, y, w, h, nome):
    """Uma nota nao pode cobrir nenhum classificador."""
    for c in caixas:
        if x < c.x + c.w and c.x < x + w and y < c.y + c.h and c.y < y + h:
            raise SystemExit(f"{nome}: nota sobrepoe {c.nome}")


def conferir(caixas, w, h, nome):
    for i in range(len(caixas)):
        for j in range(i + 1, len(caixas)):
            a, c = caixas[i], caixas[j]
            if (a.x < c.x + c.w and c.x < a.x + a.w
                    and a.y < c.y + c.h and c.y < a.y + a.h):
                raise SystemExit(f"{nome}: {a.nome} sobrepoe {c.nome}")
        a = caixas[i]
        if a.x < 0 or a.y < 0 or a.x + a.w > w or a.y + a.h > h:
            raise SystemExit(f"{nome}: {a.nome} fora do canvas")


# ----------------------------------------------------------------- generico
def generico():
    W, H = 1400, 790
    # interfaces em cima, concretas lado a lado: nenhuma seta de realizacao
    # precisa atravessar outra caixa da mesma coluna.
    fab = Caixa(290, 70, "AbstractFactory", ["+criarProdutoA(): ProdutoA",
                                             "+criarProdutoB(): ProdutoB"], "interface")
    pa = Caixa(700, 70, "AbstractProductA", ["+operacao()"], "interface")
    pb = Caixa(1020, 70, "AbstractProductB", ["+operacao()"], "interface")

    cliente = Caixa(40, 250, "Client", ["-fabrica: AbstractFactory"])
    cf1 = Caixa(230, 250, "ConcreteFactory1", ["+criarProdutoA()", "+criarProdutoB()"])
    cf2 = Caixa(430, 250, "ConcreteFactory2", ["+criarProdutoA()", "+criarProdutoB()"])

    a1 = Caixa(640, 430, "ProductA1", ())
    a2 = Caixa(830, 430, "ProductA2", ())
    b1 = Caixa(1020, 430, "ProductB1", ())
    b2 = Caixa(1210, 430, "ProductB2", ())

    caixas = [fab, pa, pb, cliente, cf1, cf2, a1, a2, b1, b2]
    conferir(caixas, W, H, "generico")

    corpo = []
    for c in caixas:
        corpo += c.svg()

    arestas = [(cf1, fab, "realiza", None), (cf2, fab, "realiza", None),
               (a1, pa, "realiza", None), (a2, pa, "realiza", None),
               (b1, pb, "realiza", None), (b2, pb, "realiza", None),
               (cliente, fab, "assoc", "usa"),
               (cf1, a1, "usa", "cria"), (cf2, a2, "usa", "cria")]
    for a, b, tipo, rot in arestas:
        cruza_caixa(a, b, caixas, "generico")
        corpo += seta(a, b, tipo, rot)

    NOTA_X, NOTA_Y, NOTA_W = 40, 420, 460
    n, nh = nota(NOTA_X, NOTA_Y, NOTA_W,
                 ["Uma fabrica concreta por familia. O Client recebe a fabrica,",
                  "chama criarProdutoA e criarProdutoB e nunca escreve o nome",
                  "de uma classe concreta: por isso nao ha como combinar",
                  "ProductA1 com ProductB2. A coerencia entre os produtos nao",
                  "e validada em tempo de execucao - ela e inalcancavel."])
    conferir_nota(caixas, NOTA_X, NOTA_Y, NOTA_W, nh, "generico")
    corpo += n

    leg, _ = legenda(40, 600, 1320, [
        ("Papel no padrao", "Quem e no G1_ProjetoComercioEletronico"),
        ("AbstractFactory", "FabricaMeioPagamento"),
        ("ConcreteFactory", "FabricaCartao, FabricaPix, FabricaBoleto, FabricaSaldoEmConta"),
        ("AbstractProduct", "Autorizador, Comprovante, PoliticaEstorno"),
        ("ConcreteProduct", "AutorizadorCartao, ComprovanteCartao, EstornoCartao, e o trio de cada familia"),
        ("Client", "ServicoDeCheckout"),
    ], 300)
    corpo += leg
    return documento(W, H, "Padrao Abstract Factory \u2014 estrutura generica",
                     "GAMMA et al., 1995 \u00b7 Subequipe 01", corpo)


# ----------------------------------------------------------------- aplicado
def aplicado():
    W, H = 1340, 900
    fab = Caixa(500, 60, "FabricaMeioPagamento",
                ["+meio(): MeioPagamento", "+criarAutorizador()",
                 "+criarComprovante()", "+criarPoliticaEstorno()"], "interface")
    cli = Caixa(900, 60, "ServicoDeCheckout",
                ["-autorizador: Autorizador", "-comprovante: Comprovante",
                 "-politicaEstorno: PoliticaEstorno", "+pagar(pedido, valor)",
                 "+reembolsar(pedido, valor, aut)"], "Client")

    # Fabricas lado a lado: as quatro setas de realizacao sobem sem se cruzar.
    fc = Caixa(90, 240, "FabricaCartao", ())
    fp = Caixa(270, 240, "FabricaPix", ())
    fb = Caixa(450, 240, "FabricaBoleto", (), None, None, PEND_FILL, True, "a entregar")
    fs = Caixa(630, 240, "FabricaSaldoEmConta", (), None, None, PEND_FILL, True, "a entregar")

    # Um bloco por produto abstrato: a interface em cima, as concretas abaixo.
    aut = Caixa(110, 430, "Autorizador",
                ["+autorizar(pedido, valor)", "+prazoLimiteEmMinutos()",
                 "+ehAssincrono()"], "interface")
    ac = Caixa(40, 610, "AutorizadorCartao", (), None, None, FILL, False, "3 desfechos")
    ap = Caixa(230, 610, "AutorizadorPix", (), None, None, FILL, False, "imediato")

    com = Caixa(530, 430, "Comprovante", ["+gerar(...)", "+formato()"], "interface")
    cc = Caixa(460, 610, "ComprovanteCartao", (), None, None, FILL, False, "parcelamento")
    cp = Caixa(650, 610, "ComprovantePix", (), None, None, FILL, False, "QR Code")

    pol = Caixa(930, 430, "PoliticaEstorno",
                ["+podeEstornar(aut)", "+estornar(...)",
                 "+prazoDeDevolucaoEmDias()"], "interface")
    ec = Caixa(880, 610, "EstornoCartao", (), None, None, FILL, False, "pela fatura")
    ep = Caixa(1070, 610, "EstornoPix", (), None, None, FILL, False, "conta de origem")

    caixas = [fab, cli, fc, fp, fb, fs, aut, ac, ap, com, cc, cp, pol, ec, ep]
    conferir(caixas, W, H, "aplicado")

    corpo = []
    for c in caixas:
        corpo += c.svg()

    arestas = ([(f, fab, "realiza", None) for f in (fc, fp, fb, fs)]
               + [(cli, fab, "assoc", "recebe")]
               + [(p, i, "realiza", None) for p, i in
                  ((ac, aut), (ap, aut), (cc, com), (cp, com), (ec, pol), (ep, pol))])
    for a, b, tipo, rot in arestas:
        cruza_caixa(a, b, caixas, "aplicado")
        corpo += seta(a, b, tipo, rot)

    NX, NY, NW = 900, 240, 400
    n, nh = nota(NX, NY, NW,
                 ["Cada fabrica cria o trio inteiro da sua familia:",
                  "autorizador, comprovante e politica de estorno.",
                  "A relacao «cria» esta desenhada no diagrama",
                  "generico; aqui ela foi omitida porque as quatro",
                  "fabricas criam doze produtos, e as setas",
                  "cobririam o desenho.",
                  "O ServicoDeCheckout cria os tres de uma unica",
                  "fabrica, no construtor, e nao ha caminho no",
                  "codigo para obter um produto isolado."])
    conferir_nota(caixas, NX, NY, NW, nh, "aplicado")
    corpo += n

    leg, lh = legenda(40, 700, 1260, [
        ("Elemento", "Origem nas entregas anteriores"),
        ("MeioPagamento", "Enumeracao do Diagrama de Classes (Entrega 2)"),
        ("Autorizador", "Tarefa 'Autorizar pagamento' do BPMN 1 (Entrega 1)"),
        ("Os tres desfechos do cartao", "Eventos de borda do BPMN 1; estados Autorizando, Pago, Recusado e Expirado"),
        ("PoliticaEstorno", "'Estornar pagamento via operadora' (Diagrama de Atividades, Entrega 2)"),
        ("ServicoDeCheckout", "Servico de Checkout e Pedidos, que requer IPagamento (Diagrama de Componentes)"),
    ], 230)
    corpo += leg
    corpo.append(text(40, 700 + lh + 20,
                      "Tracejado e cinza: familia ainda nao entregue no repositorio.",
                      11, False, SUB))
    return documento(W, H, "Abstract Factory aplicado \u2014 meios de pagamento",
                     "G1_ProjetoComercioEletronico \u00b7 Subequipe 01, Entrega 3", corpo)


base = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
for nome, conteudo in (("AbstractFactoryGenerico.svg", generico()),
                       ("AbstractFactoryAplicado.svg", aplicado())):
    caminho = os.path.normpath(os.path.join(base, nome))
    with open(caminho, "w", encoding="utf-8") as f:
        f.write(conteudo)
    print("gerado:", nome, f"({len(conteudo)} bytes)")
