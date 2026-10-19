package cliente;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import dominio.Dinheiro;
import dominio.MeioPagamento;
import fabrica.FabricaCartao;
import fabrica.FabricaMeioPagamento;
import produto.Autorizacao;

/**
 * Demonstracao grafica do Abstract Factory aplicado aos meios de pagamento.
 *
 * A tela desenha a estrutura do padrao: a fabrica escolhida no alto, ligada por
 * tres curvas aos tres produtos que ela criou. Trocar a familia troca a caixa da
 * fabrica, os tres cartoes e a cor de identidade de todos eles ao mesmo tempo -
 * que e exatamente a garantia do padrao, e o que uma saida em texto nao mostra.
 *
 * A janela nunca nomeia uma classe concreta de produto: entrega a fabrica ao
 * ServicoDeCheckout e pergunta a ele o que recebeu.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (dono do artefato criacional).
 *
 *     make gui       janela interativa
 *     make filmar    pinta o roteiro quadro a quadro e monta o video
 */
public final class Demonstracao {

    public static void main(String[] args) throws Exception {
        if (args.length >= 2 && "--filmar".equals(args[0])) {
            filmar(new File(args[1]));
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame janela = new JFrame("Abstract Factory - meios de pagamento");
                janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                janela.setContentPane(new Painel());
                janela.pack();
                janela.setLocationRelativeTo(null);
                janela.setVisible(true);
            }
        });
    }

    /**
     * Percorre o roteiro e grava um PNG por estado.
     *
     * A janela e montada de verdade e passada por pack(), para que os gerentes
     * de layout distribuam o espaco exatamente como na execucao interativa; ela
     * so nao e exibida. Cada estado e pintado em memoria.
     */
    private static void filmar(final File destino) throws Exception {
        destino.mkdirs();
        final List<String> tempos = new ArrayList<String>();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Painel p = new Painel();
                JFrame janela = new JFrame();
                janela.setContentPane(p);
                janela.pack();
                int n = 0;
                for (Passo passo : Painel.roteiro()) {
                    passo.aplicar(p);
                    p.revalidate();
                    janela.validate();
                    p.rolarConsoleAoFim();
                    janela.validate();
                    gravar(p, new File(destino, String.format("g%03d.png", n)));
                    tempos.add(String.valueOf(passo.segundos));
                    n++;
                }
                janela.dispose();
            }
        });
        StringBuilder sb = new StringBuilder();
        for (String t : tempos) {
            sb.append(t).append('\n');
        }
        java.io.PrintWriter w = new java.io.PrintWriter(new File(destino, "tempos.txt"), "UTF-8");
        w.print(sb);
        w.close();
        System.out.println("quadros gravados: " + tempos.size());
        System.exit(0);
    }

    /** Pinta o componente em memoria e grava como PNG. */
    private static void gravar(JComponent c, File arquivo) {
        BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        Pintura.suavizar(g);
        c.paint(g);
        g.dispose();
        try {
            ImageIO.write(img, "png", arquivo);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Um estado do roteiro: o que fazer no painel e por quantos segundos segurar. */
    abstract static class Passo {
        final double segundos;

        Passo(double segundos) {
            this.segundos = segundos;
        }

        abstract void aplicar(Painel p);
    }
}

// ---------------------------------------------------------------- aparencia

/** Paleta. Cada familia tem uma cor de identidade que tinge o trio inteiro. */
final class Cores {

    static final Color FUNDO = new Color(0xEF, 0xF2, 0xF7);
    static final Color SUPERFICIE = Color.WHITE;
    static final Color TINTA = new Color(0x0F, 0x17, 0x2A);
    static final Color FRACO = new Color(0x64, 0x74, 0x8B);
    static final Color TENUE = new Color(0x94, 0xA3, 0xB8);
    static final Color BORDA = new Color(0xE2, 0xE8, 0xF0);
    static final Color CONSOLE = new Color(0x0F, 0x17, 0x2A);
    static final Color CONSOLE_TEXTO = new Color(0xCB, 0xD5, 0xE1);

    static final Color APROVADO = new Color(0x15, 0x80, 0x3D);
    static final Color RECUSADO = new Color(0xDC, 0x26, 0x26);
    static final Color EXPIRADO = new Color(0xEA, 0x58, 0x0C);

    private Cores() {
    }

    static Color da(MeioPagamento m) {
        if (m == null) {
            return TENUE;
        }
        switch (m) {
            case CARTAO:         return new Color(0x6D, 0x28, 0xD9);
            case PIX:            return new Color(0x0D, 0x94, 0x88);
            case BOLETO:         return new Color(0xB4, 0x53, 0x09);
            case SALDO_EM_CONTA: return new Color(0x1D, 0x4E, 0xD8);
            default:             return TENUE;
        }
    }

    static Color clara(Color c, double f) {
        return new Color(
                (int) (c.getRed() + (255 - c.getRed()) * f),
                (int) (c.getGreen() + (255 - c.getGreen()) * f),
                (int) (c.getBlue() + (255 - c.getBlue()) * f));
    }
}

/** Fontes, com cadeia de alternativas para a janela abrir igual em outra maquina. */
final class Tipos {

    private static final List<String> FAMILIAS =
            Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames());

    private static String escolher(String[] preferidas, String padrao) {
        for (int i = 0; i < preferidas.length; i++) {
            if (FAMILIAS.contains(preferidas[i])) {
                return preferidas[i];
            }
        }
        return padrao;
    }

    private static final String TEXTO = escolher(
            new String[] { "Inter", "Avenir Next", "Helvetica Neue", "Segoe UI" }, "SansSerif");
    private static final String CODIGO = escolher(
            new String[] { "Menlo", "Consolas", "DejaVu Sans Mono", "Monaco" }, "Monospaced");

    static final Font TITULO  = new Font(TEXTO, Font.BOLD, 21);
    static final Font SUB     = new Font(TEXTO, Font.PLAIN, 13);
    static final Font SECAO   = new Font(TEXTO, Font.BOLD, 10);
    static final Font ROTULO  = new Font(TEXTO, Font.BOLD, 12);
    static final Font CORPO   = new Font(TEXTO, Font.PLAIN, 12);
    static final Font BANNER  = new Font(TEXTO, Font.BOLD, 13);
    static final Font VALOR   = new Font(TEXTO, Font.BOLD, 19);
    static final Font CLASSE  = new Font(CODIGO, Font.BOLD, 15);
    static final Font CHAMADA = new Font(CODIGO, Font.PLAIN, 11);
    static final Font MONO    = new Font(CODIGO, Font.PLAIN, 11);

    private Tipos() {
    }
}

/** Primitivas de desenho reaproveitadas pelos componentes. */
final class Pintura {

    private Pintura() {
    }

    static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** Superficie arredondada com borda fina e sombra discreta. */
    static void cartao(Graphics2D g, int x, int y, int l, int a, int raio, Color fundo, Color borda) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
        g.setColor(new Color(0x0F, 0x17, 0x2A));
        g.fill(new RoundRectangle2D.Float(x + 1, y + 3, l - 2, a - 2, raio, raio));
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(fundo);
        g.fill(new RoundRectangle2D.Float(x, y, l, a, raio, raio));
        if (borda != null) {
            g.setColor(borda);
            g.setStroke(new BasicStroke(1f));
            g.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, l - 1, a - 1, raio, raio));
        }
    }

    /** Rotulo de secao em caixa alta, com as letras espacadas. */
    static void secao(Graphics2D g, String texto, int x, int y, Color cor) {
        g.setFont(Tipos.SECAO);
        g.setColor(cor);
        String alto = texto.toUpperCase();
        int cx = x;
        for (int i = 0; i < alto.length(); i++) {
            String ch = String.valueOf(alto.charAt(i));
            g.drawString(ch, cx, y);
            cx += g.getFontMetrics().stringWidth(ch) + 1;
        }
    }

    /** Corta o texto com reticencias quando nao cabe na largura dada. */
    static String caber(Graphics2D g, String texto, int largura) {
        if (g.getFontMetrics().stringWidth(texto) <= largura) {
            return texto;
        }
        String t = texto;
        while (t.length() > 1 && g.getFontMetrics().stringWidth(t + "...") > largura) {
            t = t.substring(0, t.length() - 1);
        }
        return t + "...";
    }

    /** Quebra o texto em linhas que cabem na largura, respeitando os \n ja existentes. */
    static List<String> quebrar(Graphics2D g, String texto, int largura) {
        List<String> linhas = new ArrayList<String>();
        String[] brutas = texto.split("\n");
        for (int i = 0; i < brutas.length; i++) {
            String resto = brutas[i];
            if (resto.isEmpty()) {
                linhas.add("");
                continue;
            }
            while (g.getFontMetrics().stringWidth(resto) > largura) {
                int corte = resto.length();
                while (corte > 1
                        && g.getFontMetrics().stringWidth(resto.substring(0, corte)) > largura) {
                    corte--;
                }
                int espaco = resto.lastIndexOf(' ', corte);
                if (espaco > corte / 2) {
                    corte = espaco;
                }
                linhas.add(resto.substring(0, corte));
                resto = resto.substring(corte).trim();
            }
            linhas.add(resto);
        }
        return linhas;
    }
}

// --------------------------------------------------------------- componentes

/** Botao de familia: pilula que se preenche com a cor da familia quando ativa. */
final class Pilula extends JComponent {

    private static final long serialVersionUID = 1L;

    private final transient MeioPagamento meio;
    private final transient List<ActionListener> ouvintes = new ArrayList<ActionListener>();
    private boolean ativa;

    Pilula(MeioPagamento meio) {
        this.meio = meio;
        setPreferredSize(new Dimension(230, 36));
        setMaximumSize(new Dimension(400, 36));
        setAlignmentX(LEFT_ALIGNMENT);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                for (ActionListener l : ouvintes) {
                    l.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "escolher"));
                }
            }
        });
    }

    void aoClicar(ActionListener l) {
        ouvintes.add(l);
    }

    void definirAtiva(boolean v) {
        this.ativa = v;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Pintura.suavizar(g);
        Color cor = Cores.da(meio);
        int l = getWidth();
        int a = getHeight();
        if (ativa) {
            g.setPaint(new GradientPaint(0, 0, cor, 0, a, cor.darker()));
            g.fill(new RoundRectangle2D.Float(0, 0, l, a, a, a));
        } else {
            g.setColor(Cores.SUPERFICIE);
            g.fill(new RoundRectangle2D.Float(0, 0, l, a, a, a));
            g.setColor(Cores.BORDA);
            g.setStroke(new BasicStroke(1f));
            g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, l - 1, a - 1, a, a));
        }
        int d = 9;
        g.setColor(ativa ? Color.WHITE : cor);
        g.fill(new Ellipse2D.Float(15, a / 2f - d / 2f, d, d));
        g.setFont(Tipos.ROTULO);
        g.setColor(ativa ? Color.WHITE : Cores.TINTA);
        g.drawString(meio.rotulo(), 15 + d + 10, a / 2 + g.getFontMetrics().getAscent() / 2 - 1);
        g.dispose();
    }
}

/** Botao plano, retangulo arredondado. */
final class BotaoPlano extends JComponent {

    private static final long serialVersionUID = 1L;

    private final String texto;
    private final boolean destaque;
    private final transient Runnable acao;

    BotaoPlano(String texto, boolean destaque, Runnable acao) {
        this.texto = texto;
        this.destaque = destaque;
        this.acao = acao;
        setPreferredSize(new Dimension(230, 34));
        setMaximumSize(new Dimension(400, 34));
        setAlignmentX(LEFT_ALIGNMENT);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (acao != null) {
                    acao.run();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Pintura.suavizar(g);
        int l = getWidth();
        int a = getHeight();
        if (destaque) {
            g.setColor(Cores.TINTA);
            g.fill(new RoundRectangle2D.Float(0, 0, l, a, 9, 9));
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Cores.SUPERFICIE);
            g.fill(new RoundRectangle2D.Float(0, 0, l, a, 9, 9));
            g.setColor(Cores.BORDA);
            g.setStroke(new BasicStroke(1f));
            g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, l - 1, a - 1, 9, 9));
            g.setColor(Cores.TINTA);
        }
        g.setFont(Tipos.ROTULO);
        g.drawString(texto, (l - g.getFontMetrics().stringWidth(texto)) / 2,
                a / 2 + g.getFontMetrics().getAscent() / 2 - 1);
        g.dispose();
    }
}

/** A caixa da fabrica escolhida, no alto do palco. */
final class CaixaFabrica extends JComponent {

    private static final long serialVersionUID = 1L;

    private String nome = "nenhuma fabrica";
    private String resumo = "escolha uma familia ao lado para o cliente nascer";
    private transient MeioPagamento meio;

    CaixaFabrica() {
        setPreferredSize(new Dimension(520, 82));
    }

    void definir(MeioPagamento meio, String nome, String resumo) {
        this.meio = meio;
        this.nome = nome;
        this.resumo = resumo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Pintura.suavizar(g);
        Color cor = Cores.da(meio);
        int l = getWidth();
        int a = getHeight();
        Pintura.cartao(g, 0, 0, l, a, 12,
                meio == null ? Cores.SUPERFICIE : Cores.clara(cor, 0.9), Cores.BORDA);
        g.setColor(cor);
        g.fill(new RoundRectangle2D.Float(1, 1, 5, a - 2, 5, 5));

        Pintura.secao(g, "a fabrica escolhida", 22, 26, meio == null ? Cores.TENUE : cor);
        g.setFont(Tipos.CLASSE);
        g.setColor(meio == null ? Cores.TENUE : cor);
        g.drawString(nome, 22, 52);
        g.setFont(Tipos.CORPO);
        g.setColor(Cores.FRACO);
        g.drawString(Pintura.caber(g, resumo, l - 44), 22, 70);
        g.dispose();
    }
}

/** Um cartao de produto: o papel no padrao, a classe entregue e o que ela produziu. */
final class CartaoProduto extends JComponent {

    private static final long serialVersionUID = 1L;

    private final String papel;
    private final String chamada;
    private String classe = "—";
    private String propriedade = "aguardando uma familia";
    private String saida = "";
    private transient MeioPagamento meio;
    private transient Color realce;

    CartaoProduto(String papel, String chamada) {
        this.papel = papel;
        this.chamada = chamada;
        setPreferredSize(new Dimension(265, 330));
    }

    void definir(MeioPagamento meio, String classe, String propriedade, String saida, Color realce) {
        this.meio = meio;
        this.classe = classe;
        this.propriedade = propriedade;
        this.saida = saida == null ? "" : saida;
        this.realce = realce;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Pintura.suavizar(g);
        Color cor = Cores.da(meio);
        int l = getWidth();
        int a = getHeight();

        Pintura.cartao(g, 0, 0, l, a, 12, Cores.SUPERFICIE, Cores.BORDA);
        g.setColor(cor);
        g.fill(new RoundRectangle2D.Float(1, 1, l - 2, 4, 4, 4));

        Pintura.secao(g, papel, 18, 32, Cores.FRACO);
        g.setFont(Tipos.CHAMADA);
        g.setColor(Cores.TENUE);
        g.drawString(chamada, 18, 48);

        g.setFont(Tipos.CLASSE);
        g.setColor(meio == null ? Cores.TENUE : cor);
        g.drawString(Pintura.caber(g, classe, l - 36), 18, 76);

        g.setFont(Tipos.CORPO);
        g.setColor(Cores.FRACO);
        g.drawString(Pintura.caber(g, propriedade, l - 36), 18, 94);

        g.setColor(Cores.BORDA);
        g.drawLine(18, 108, l - 18, 108);

        if (!saida.isEmpty()) {
            int y = 130;
            String corpo = saida;
            // A primeira linha pode ser o desfecho em destaque; o resto e saida
            // crua do produto. A medicao tem de usar a MESMA fonte do desenho,
            // senao a quebra erra a largura e o texto vaza do cartao.
            int nl = saida.indexOf('\n');
            if (realce != null && nl > 0) {
                // status na primeira linha, detalhe nas seguintes
                g.setFont(Tipos.BANNER);
                g.setColor(realce);
                g.drawString(Pintura.caber(g, saida.substring(0, nl), l - 36), 18, y);
                corpo = saida.substring(nl + 1);
                y += 21;
            }
            if (!corpo.isEmpty()) {
                g.setFont(Tipos.MONO);
                g.setColor(realce != null && nl < 0 ? realce : Cores.TINTA);
                for (String linha : Pintura.quebrar(g, corpo, l - 36)) {
                    if (y > a - 16) {
                        break;
                    }
                    g.drawString(linha, 18, y);
                    y += 15;
                }
            }
        }
        g.dispose();
    }
}

/** O palco: a fabrica no alto, ligada por curvas aos tres produtos embaixo. */
final class Palco extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient CaixaFabrica fabrica;
    private final transient CartaoProduto[] cartoes;
    private transient MeioPagamento meio;

    Palco(CaixaFabrica fabrica, CartaoProduto[] cartoes) {
        this.fabrica = fabrica;
        this.cartoes = cartoes;
        setOpaque(false);
        setLayout(null);
        add(fabrica);
        for (int i = 0; i < cartoes.length; i++) {
            add(cartoes[i]);
        }
    }

    void definirMeio(MeioPagamento meio) {
        this.meio = meio;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(880, 82 + 66 + 330);
    }

    @Override
    public void doLayout() {
        int l = getWidth();
        int lf = Math.min(520, l - 40);
        fabrica.setBounds((l - lf) / 2, 0, lf, 82);
        int vao = 16;
        int lc = (l - vao * (cartoes.length - 1)) / cartoes.length;
        int y = 82 + 66;
        for (int i = 0; i < cartoes.length; i++) {
            cartoes[i].setBounds(i * (lc + vao), y, lc, getHeight() - y);
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        Pintura.suavizar(g);
        Color cor = Cores.da(meio);
        Rectangle f = fabrica.getBounds();
        int xo = f.x + f.width / 2;
        int yo = f.y + f.height;
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < cartoes.length; i++) {
            Rectangle c = cartoes[i].getBounds();
            int xd = c.x + c.width / 2;
            int yd = c.y;
            g.setColor(meio == null ? Cores.BORDA : Cores.clara(cor, 0.4));
            g.draw(new CubicCurve2D.Float(xo, yo + 2, xo, yo + 38, xd, yd - 38, xd, yd - 9));
            g.setColor(meio == null ? Cores.TENUE : cor);
            g.fill(new Ellipse2D.Float(xd - 4.5f, yd - 13, 9, 9));
        }
        g.dispose();
    }
}

/** Faixa de estado, abaixo dos cartoes. */
final class Banner extends JComponent {

    private static final long serialVersionUID = 1L;

    private String texto = "Nenhuma familia escolhida.";
    private transient Color cor = Cores.FRACO;

    Banner() {
        setPreferredSize(new Dimension(880, 44));
        setMaximumSize(new Dimension(4000, 44));
    }

    void definir(String texto, Color cor) {
        this.texto = texto;
        this.cor = cor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Pintura.suavizar(g);
        int l = getWidth();
        int a = getHeight();
        g.setColor(Cores.clara(cor, 0.9));
        g.fill(new RoundRectangle2D.Float(0, 0, l, a, 10, 10));
        g.setColor(cor);
        g.fill(new RoundRectangle2D.Float(0, 0, 4, a, 4, 4));
        g.setFont(Tipos.BANNER);
        g.drawString(Pintura.caber(g, texto, l - 36), 20,
                a / 2 + g.getFontMetrics().getAscent() / 2 - 1);
        g.dispose();
    }
}

// -------------------------------------------------------------------- janela

/** A janela inteira. Nao conhece nenhuma classe concreta de produto. */
final class Painel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String PEDIDO = "PED-2026-0001";

    private final transient Pilula[] pilulas = new Pilula[MeioPagamento.values().length];
    private final transient CaixaFabrica caixa = new CaixaFabrica();
    private final transient CartaoProduto cAutorizador =
            new CartaoProduto("Autorizador", "criarAutorizador()");
    private final transient CartaoProduto cComprovante =
            new CartaoProduto("Comprovante", "criarComprovante()");
    private final transient CartaoProduto cEstorno =
            new CartaoProduto("Politica de estorno", "criarPoliticaEstorno()");
    private final transient Palco palco =
            new Palco(caixa, new CartaoProduto[] { cAutorizador, cComprovante, cEstorno });
    private final transient Banner banner = new Banner();
    private final JTextArea console = new JTextArea(8, 20);
    private final JScrollPane rolagem;

    private transient ServicoDeCheckout checkout;
    private transient MeioPagamento meioAtual;
    private transient Autorizacao ultima;
    String valorTexto = "299,90";

    Painel() {
        setLayout(new BorderLayout(20, 14));
        setBackground(Cores.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 18, 24));
        setPreferredSize(new Dimension(1240, 812));

        add(cabecalho(), BorderLayout.NORTH);
        add(trilho(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.add(palco, BorderLayout.CENTER);
        centro.add(banner, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        console.setFont(Tipos.MONO);
        console.setEditable(false);
        console.setBackground(Cores.CONSOLE);
        console.setForeground(Cores.CONSOLE_TEXTO);
        console.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        rolagem = new JScrollPane(console);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getViewport().setBackground(Cores.CONSOLE);
        rolagem.setPreferredSize(new Dimension(100, 150));
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        rodape.add(rolagem, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        registrar("// o ServicoDeCheckout ainda nao existe: ele so nasce");
        registrar("// quando recebe uma FabricaMeioPagamento.");
    }

    private JComponent cabecalho() {
        JComponent c = new JComponent() {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Pintura.suavizar(g);
                g.setFont(Tipos.TITULO);
                g.setColor(Cores.TINTA);
                g.drawString("Abstract Factory aplicado aos meios de pagamento", 0, 22);
                g.setFont(Tipos.SUB);
                g.setColor(Cores.FRACO);
                g.drawString("Uma escolha de familia produz tres produtos coerentes. A janela nunca "
                        + "nomeia uma classe concreta: entrega a fabrica ao cliente e pergunta a ele "
                        + "o que recebeu.", 0, 44);
                g.dispose();
            }
        };
        c.setPreferredSize(new Dimension(100, 56));
        return c;
    }

    private JComponent trilho() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setPreferredSize(new Dimension(246, 10));

        col.add(rotulo("o pedido"));
        col.add(Box.createVerticalStrut(8));
        col.add(fichaPedido());
        col.add(Box.createVerticalStrut(22));

        col.add(rotulo("escolha a familia"));
        col.add(Box.createVerticalStrut(8));
        MeioPagamento[] meios = MeioPagamento.values();
        for (int i = 0; i < meios.length; i++) {
            final MeioPagamento m = meios[i];
            Pilula p = new Pilula(m);
            p.aoClicar(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    escolher(m, 0);
                }
            });
            pilulas[i] = p;
            col.add(p);
            col.add(Box.createVerticalStrut(7));
        }
        col.add(Box.createVerticalStrut(15));

        col.add(rotulo("executar"));
        col.add(Box.createVerticalStrut(8));
        col.add(new BotaoPlano("Autorizar", true, new Runnable() {
            public void run() {
                autorizar();
            }
        }));
        col.add(Box.createVerticalStrut(7));
        col.add(new BotaoPlano("Estornar", false, new Runnable() {
            public void run() {
                estornar();
            }
        }));
        col.add(Box.createVerticalStrut(22));

        col.add(rotulo("desfechos do cartao"));
        col.add(Box.createVerticalStrut(8));
        col.add(new BotaoPlano("Valor acima do limite", false, new Runnable() {
            public void run() {
                valorTexto = "9000,00";
                escolher(MeioPagamento.CARTAO, 0);
                autorizar();
            }
        }));
        col.add(Box.createVerticalStrut(7));
        col.add(new BotaoPlano("Fora da janela (45 min)", false, new Runnable() {
            public void run() {
                valorTexto = "299,90";
                escolher(MeioPagamento.CARTAO, 45);
                autorizar();
            }
        }));
        col.add(Box.createVerticalStrut(22));

        col.add(new BotaoPlano("Tentar misturar familias", false, new Runnable() {
            public void run() {
                misturar();
            }
        }));
        col.add(Box.createVerticalGlue());
        return col;
    }

    private JComponent rotulo(final String texto) {
        JComponent c = new JComponent() {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Pintura.suavizar(g);
                Pintura.secao(g, texto, 2, 12, Cores.TENUE);
                g.dispose();
            }
        };
        c.setPreferredSize(new Dimension(230, 16));
        c.setMaximumSize(new Dimension(400, 16));
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    private JComponent fichaPedido() {
        JComponent c = new JComponent() {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Pintura.suavizar(g);
                Pintura.cartao(g, 0, 0, getWidth(), getHeight(), 10, Cores.SUPERFICIE, Cores.BORDA);
                g.setFont(Tipos.CHAMADA);
                g.setColor(Cores.TENUE);
                g.drawString(PEDIDO, 16, 26);
                g.setFont(Tipos.VALOR);
                g.setColor(Cores.TINTA);
                g.drawString("R$ " + valorTexto, 16, 54);
                g.dispose();
            }
        };
        c.setPreferredSize(new Dimension(230, 68));
        c.setMaximumSize(new Dimension(400, 68));
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    // -------------------------------------------------------------- acoes

    void escolher(MeioPagamento meio, int minutos) {
        FabricaMeioPagamento fabrica = (meio == MeioPagamento.CARTAO && minutos > 0)
                ? FabricaCartao.aposMinutos(minutos)
                : CatalogoDeFabricas.para(meio);

        // A unica linha que importa: o cliente recebe a fabrica e cria o trio.
        checkout = new ServicoDeCheckout(fabrica);
        meioAtual = meio;
        ultima = null;

        for (int i = 0; i < pilulas.length; i++) {
            pilulas[i].definirAtiva(MeioPagamento.values()[i] == meio);
        }
        String[] classes = checkout.classesDoTrio();
        caixa.definir(meio, fabrica.getClass().getSimpleName(),
                minutos > 0 ? "tentativa " + minutos + " min apos o inicio do checkout"
                            : "criada pelo CatalogoDeFabricas, a partir do meio escolhido");
        palco.definirMeio(meio);
        cAutorizador.definir(meio, classes[0], checkout.descricaoDoAutorizador(), "", null);
        cComprovante.definir(meio, classes[1], checkout.descricaoDoComprovante(), "", null);
        cEstorno.definir(meio, classes[2], checkout.descricaoDoEstorno(), "", null);
        banner.definir("Os tres produtos trocaram juntos: " + classes[0] + ", " + classes[1]
                + " e " + classes[2] + ".", Cores.da(meio));

        registrar("");
        registrar("new ServicoDeCheckout(" + fabrica.getClass().getSimpleName() + ")");
        registrar("  -> " + classes[0] + ", " + classes[1] + ", " + classes[2]);
        registrar("  " + checkout.resumoDaFamilia());
        repaint();
    }

    void autorizar() {
        if (checkout == null) {
            return;
        }
        Dinheiro valor = lerValor();
        ultima = checkout.pagar(PEDIDO, valor);
        Color cor = corDe(ultima);
        String[] classes = checkout.classesDoTrio();

        cAutorizador.definir(meioAtual, classes[0], checkout.descricaoDoAutorizador(),
                ultima.status() + "\n" + ultima.detalhe(), cor);
        cComprovante.definir(meioAtual, classes[1], checkout.descricaoDoComprovante(),
                checkout.comprovanteDe(PEDIDO, valor, ultima), null);
        cEstorno.definir(meioAtual, classes[2], checkout.descricaoDoEstorno(),
                "aguardando acao", null);
        banner.definir(meioAtual.rotulo() + " - " + ultima.status() + ": " + ultima.detalhe(), cor);

        registrar("");
        registrar("pagar(" + PEDIDO + ", R$ " + formatar(valor) + ") -> " + ultima);
        repaint();
    }

    void estornar() {
        if (checkout == null || ultima == null) {
            return;
        }
        String resposta = checkout.reembolsar(PEDIDO, lerValor(), ultima);
        boolean recusado = resposta.startsWith("Nada a estornar");
        Color cor = recusado ? Cores.FRACO : Cores.da(meioAtual);
        cEstorno.definir(meioAtual, checkout.classesDoTrio()[2], checkout.descricaoDoEstorno(),
                resposta, cor);
        banner.definir(recusado
                ? "A politica de estorno e da mesma familia do autorizador e enxerga o desfecho dele."
                : "Estorno pela politica da propria familia, no prazo dela.", cor);
        registrar("reembolsar(...) -> " + resposta);
        repaint();
    }

    void misturar() {
        banner.definir("Nao ha o que clicar: o cliente nao expoe caminho para criar um "
                + "produto isolado.", Cores.RECUSADO);
        registrar("");
        registrar("// tentar combinar o comprovante do Pix com o autorizador do Cartao:");
        registrar("//   new ComprovantePix()  -> inalcancavel a partir do cliente");
        registrar("// os produtos so nascem dentro de uma fabrica, e a fabrica entrega os tres.");
        repaint();
    }

    private Dinheiro lerValor() {
        String t = valorTexto.trim().replace(".", "").replace(",", "");
        try {
            return Dinheiro.emReais(Long.parseLong(t));
        } catch (NumberFormatException e) {
            return Dinheiro.emReais(29990L);
        }
    }

    private static String formatar(Dinheiro d) {
        return (d.centavos() / 100) + "," + String.format("%02d", d.centavos() % 100);
    }

    private static Color corDe(Autorizacao a) {
        if (a.status() == Autorizacao.Status.APROVADO) {
            return Cores.APROVADO;
        }
        return a.status() == Autorizacao.Status.RECUSADO ? Cores.RECUSADO : Cores.EXPIRADO;
    }

    /**
     * Leva o console ao fim. Em uso interativo o proprio caret resolve; na
     * filmagem a janela nao esta na tela, entao a barra precisa ser movida
     * na mao depois que o layout rodou.
     */
    void rolarConsoleAoFim() {
        javax.swing.JScrollBar barra = rolagem.getVerticalScrollBar();
        barra.setValue(barra.getMaximum());
    }

    private void registrar(String linha) {
        console.append(linha + "\n");
        console.setCaretPosition(console.getDocument().getLength());
    }

    // ------------------------------------------------------------- roteiro

    static List<Demonstracao.Passo> roteiro() {
        List<Demonstracao.Passo> r = new ArrayList<Demonstracao.Passo>();
        r.add(new Demonstracao.Passo(3.0) {
            void aplicar(Painel p) {
            }
        });
        MeioPagamento[] ordem = { MeioPagamento.CARTAO, MeioPagamento.PIX,
                                  MeioPagamento.BOLETO, MeioPagamento.SALDO_EM_CONTA };
        for (int i = 0; i < ordem.length; i++) {
            final MeioPagamento m = ordem[i];
            r.add(new Demonstracao.Passo(i == 0 ? 3.6 : 3.0) {
                void aplicar(Painel p) {
                    p.escolher(m, 0);
                }
            });
            r.add(new Demonstracao.Passo(3.4) {
                void aplicar(Painel p) {
                    p.autorizar();
                }
            });
            r.add(new Demonstracao.Passo(2.8) {
                void aplicar(Painel p) {
                    p.estornar();
                }
            });
        }
        r.add(new Demonstracao.Passo(3.2) {
            void aplicar(Painel p) {
                p.valorTexto = "9000,00";
                p.escolher(MeioPagamento.CARTAO, 0);
                p.autorizar();
            }
        });
        r.add(new Demonstracao.Passo(2.8) {
            void aplicar(Painel p) {
                p.estornar();
            }
        });
        r.add(new Demonstracao.Passo(3.2) {
            void aplicar(Painel p) {
                p.valorTexto = "299,90";
                p.escolher(MeioPagamento.CARTAO, 45);
                p.autorizar();
            }
        });
        r.add(new Demonstracao.Passo(2.8) {
            void aplicar(Painel p) {
                p.estornar();
            }
        });
        r.add(new Demonstracao.Passo(4.5) {
            void aplicar(Painel p) {
                p.misturar();
            }
        });
        return r;
    }
}
