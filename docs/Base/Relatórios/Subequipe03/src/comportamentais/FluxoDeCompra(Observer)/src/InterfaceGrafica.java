import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import cliente.GatewayPagamento;
import modelo.Pagamento;
import modelo.StatusPagamento;
import observadores.EmissaoNotaFiscal;
import observadores.LiberacaoEstoque;
import observadores.LiberacaoPedido;
import observadores.NotificacaoComprador;
import observadores.ObservadorDePagamento;

/**
 * Interface grafica para demonstrar o GoF Observer no subsistema de pagamento.
 *
 * Usa as classes reais do projeto (Pagamento, GatewayPagamento e os observers concretos).
 * Cada observer real e registrado dentro de um ObservadorMonitorado, que apenas captura
 * o que o observer imprimiu para mostrar na tela se ele reagiu ou so foi notificado.
 */
public class InterfaceGrafica {

    // Paleta (a mesma ideia de cores do diagrama no Miro)
    private static final Color FUNDO = new Color(0xF7F7F7);
    private static final Color BRANCO = Color.WHITE;
    private static final Color TINTA = new Color(0x1A1A1A);
    private static final Color TINTA_SUAVE = new Color(0x595959);
    private static final Color BORDA = new Color(0xE0E0E0);
    private static final Color AZUL = new Color(0x305BAB);
    private static final Color AZUL_CLARO = new Color(0xE6EEFC);
    private static final Color VERDE = new Color(0x067429);
    private static final Color VERDE_CLARO = new Color(0xE3F6E9);
    private static final Color CINZA_CLARO = new Color(0xEFEFEF);
    private static final Color LARANJA = new Color(0x9B4A08);

    private final GatewayPagamento gateway = new GatewayPagamento();
    private final Map<String, ObservadorMonitorado> observadores = new LinkedHashMap<>();
    private final Map<String, JCheckBox> caixas = new LinkedHashMap<>();

    private Pagamento pagamento;
    private int contador = 0;

    private final JTextField campoValor = new JTextField("249,90", 8);
    private final JLabel rotuloPagamento = new JLabel("Nenhum pagamento criado");
    private final JLabel rotuloStatus = new JLabel(" ");
    private final JTextArea log = new JTextArea();
    private final JPanel painelBotoesStatus = new JPanel(new GridLayout(2, 2, 8, 8));

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignorado) {
                // segue com o visual padrao
            }
            InterfaceGrafica app = new InterfaceGrafica();
            JFrame janela = new JFrame("Observer no Pagamento: G1_ProjetoComercioEletronico");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(app.montarTela());
            janela.setSize(1180, 760);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }

    JPanel montarTela() {
        observadores.put("Comprador", new ObservadorMonitorado("NotificacaoComprador",
                "Reage a: qualquer status", new NotificacaoComprador()));
        observadores.put("Pedido", new ObservadorMonitorado("LiberacaoPedido",
                "Reage a: só APROVADO", new LiberacaoPedido()));
        observadores.put("Fiscal", new ObservadorMonitorado("EmissaoNotaFiscal",
                "Reage a: só APROVADO", new EmissaoNotaFiscal()));
        observadores.put("Estoque", new ObservadorMonitorado("LiberacaoEstoque",
                "Reage a: RECUSADO ou ESTORNADO", new LiberacaoEstoque()));
        observadores.put("SMS", new ObservadorMonitorado("SMS (criado na hora)",
                "Reage a: qualquer status", pag -> System.out.println(
                        "[SMS] Seu pagamento " + pag.getId() + " foi " + pag.getStatus().getDescricao())));

        JPanel raiz = new JPanel(new BorderLayout(16, 16));
        raiz.setBackground(FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        raiz.add(montarCabecalho(), BorderLayout.NORTH);
        raiz.add(montarControles(), BorderLayout.WEST);
        raiz.add(montarCentro(), BorderLayout.CENTER);

        atualizarBotoes();
        registrarLog("Crie um pagamento para começar. Os observers marcados serão registrados nele.");
        return raiz;
    }

    // ---------- Montagem da tela ----------

    private JComponent montarCabecalho() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Observer aplicado ao Pagamento");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titulo.setForeground(TINTA);
        JLabel subtitulo = new JLabel("O Pagamento (Subject) avisa todos os observers registrados a cada "
                + "mudança de status. Cada observer decide sozinho se reage.");
        subtitulo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitulo.setForeground(TINTA_SUAVE);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(subtitulo);
        return painel;
    }

    private JComponent montarControles() {
        JPanel coluna = new JPanel();
        coluna.setOpaque(false);
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));
        coluna.setPreferredSize(new Dimension(300, 0));

        // 1. Criar pagamento
        JPanel criar = cartao("1. Novo pagamento");
        JPanel linhaValor = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaValor.setOpaque(false);
        linhaValor.add(texto("Valor R$ ", 13, TINTA_SUAVE, false));
        linhaValor.add(campoValor);
        linhaValor.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        linhaValor.setMaximumSize(linhaValor.getPreferredSize());
        criar.add(linhaValor);
        criar.add(Box.createVerticalStrut(8));
        JButton botaoCriar = botao("Criar pagamento", AZUL);
        botaoCriar.addActionListener(e -> criarPagamento());
        criar.add(botaoCriar);
        criar.add(Box.createVerticalStrut(10));
        rotuloPagamento.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        rotuloPagamento.setForeground(TINTA);
        rotuloStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        rotuloStatus.setForeground(AZUL);
        rotuloPagamento.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        rotuloStatus.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        criar.add(rotuloPagamento);
        criar.add(rotuloStatus);

        // 2. Gateway muda o status
        JPanel status = cartao("2. Gateway muda o status");
        painelBotoesStatus.setOpaque(false);
        painelBotoesStatus.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        adicionarBotaoStatus("Em análise", StatusPagamento.EM_ANALISE);
        adicionarBotaoStatus("Aprovar", StatusPagamento.APROVADO);
        adicionarBotaoStatus("Recusar", StatusPagamento.RECUSADO);
        adicionarBotaoStatus("Estornar", StatusPagamento.ESTORNADO);
        status.add(painelBotoesStatus);

        // 3. Observers registrados
        JPanel registro = cartao("3. Observers registrados");
        registro.add(texto("<html>Marcar e desmarcar chama adicionarObservador e removerObservador.</html>", 11, TINTA_SUAVE, false));
        registro.add(Box.createVerticalStrut(6));
        for (Map.Entry<String, ObservadorMonitorado> item : observadores.entrySet()) {
            JCheckBox caixa = new JCheckBox(item.getValue().nome, !item.getKey().equals("SMS"));
            caixa.setOpaque(false);
            caixa.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            caixa.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            caixa.addActionListener(e -> alternarObservador(item.getValue(), caixa.isSelected()));
            caixas.put(item.getKey(), caixa);
            registro.add(caixa);
        }

        coluna.add(criar);
        coluna.add(Box.createVerticalStrut(12));
        coluna.add(status);
        coluna.add(Box.createVerticalStrut(12));
        coluna.add(registro);
        coluna.add(Box.createVerticalGlue());
        return coluna;
    }

    private JComponent montarCentro() {
        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);

        JPanel grade = new JPanel(new GridLayout(2, 3, 12, 12));
        grade.setOpaque(false);
        for (ObservadorMonitorado obs : observadores.values()) {
            grade.add(obs.cartao);
        }
        grade.add(legenda());
        centro.add(grade, BorderLayout.CENTER);

        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        log.setForeground(TINTA);
        log.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane rolagem = new JScrollPane(log);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.setPreferredSize(new Dimension(0, 210));
        JPanel blocoLog = new JPanel(new BorderLayout(0, 6));
        blocoLog.setOpaque(false);
        blocoLog.add(texto("Saída do programa", 13, TINTA, true), BorderLayout.NORTH);
        blocoLog.add(rolagem, BorderLayout.CENTER);
        centro.add(blocoLog, BorderLayout.SOUTH);
        return centro;
    }

    private JComponent legenda() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        painel.add(texto("Legenda", 13, TINTA, true));
        painel.add(Box.createVerticalStrut(8));
        painel.add(selo("Reagiu", VERDE_CLARO, VERDE));
        painel.add(Box.createVerticalStrut(6));
        painel.add(selo("Notificado, sem ação", CINZA_CLARO, TINTA_SUAVE));
        painel.add(Box.createVerticalStrut(6));
        painel.add(selo("Não registrado", BRANCO, new Color(0xB0B0B0)));
        return painel;
    }

    // ---------- Ações ----------

    private void criarPagamento() {
        double valor;
        try {
            valor = Double.parseDouble(campoValor.getText().trim().replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            registrarLog("Valor inválido. Use, por exemplo, 249,90.");
            return;
        }
        contador++;
        pagamento = new Pagamento(String.format("PG%03d", contador), valor);
        registrarLog("");
        registrarLog(String.format("=== Novo pagamento %s de R$ %.2f (status: Pendente) ===",
                pagamento.getId(), valor));
        for (Map.Entry<String, ObservadorMonitorado> item : observadores.entrySet()) {
            if (caixas.get(item.getKey()).isSelected()) {
                pagamento.adicionarObservador(item.getValue());
                registrarLog("adicionarObservador(" + item.getValue().nome + ")");
            }
        }
        atualizarCartoes(false);
        atualizarBotoes();
    }

    private void mudarStatus(StatusPagamento novo) {
        if (pagamento == null) {
            return;
        }
        if (pagamento.getStatus() == novo) {
            registrarLog("O status já é " + novo.getDescricao() + ": setStatus não notifica ninguém.");
            return;
        }
        atualizarCartoes(true);
        registrarLog("");
        capturarSaida(() -> gateway.informarResultado(pagamento, novo));
        atualizarBotoes();
    }

    private void alternarObservador(ObservadorMonitorado obs, boolean registrar) {
        if (pagamento != null) {
            if (registrar) {
                pagamento.adicionarObservador(obs);
                registrarLog("adicionarObservador(" + obs.nome + ")");
            } else {
                pagamento.removerObservador(obs);
                registrarLog("removerObservador(" + obs.nome + ")");
            }
        }
        atualizarCartoes(false);
    }

    // ---------- Atualizacao visual ----------

    /** Reseta os cartoes: registrados ficam aguardando, os outros aparecem como nao registrados. */
    private void atualizarCartoes(boolean limparMensagem) {
        for (Map.Entry<String, ObservadorMonitorado> item : observadores.entrySet()) {
            boolean registrado = pagamento != null && caixas.get(item.getKey()).isSelected();
            ObservadorMonitorado obs = item.getValue();
            if (!registrado) {
                obs.cartao.mostrarNaoRegistrado();
            } else if (limparMensagem || obs.cartao.estado == Estado.NAO_REGISTRADO) {
                obs.cartao.mostrarAguardando();
            }
        }
    }

    private void atualizarBotoes() {
        for (java.awt.Component c : painelBotoesStatus.getComponents()) {
            c.setEnabled(pagamento != null);
        }
        if (pagamento == null) {
            rotuloStatus.setText(" ");
            return;
        }
        rotuloPagamento.setText(String.format("%s  |  R$ %.2f", pagamento.getId(), pagamento.getValor()));
        rotuloStatus.setText("Status atual: " + pagamento.getStatus().getDescricao());
        rotuloPagamento.revalidate();
    }

    private void registrarLog(String linha) {
        log.append(linha + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    /** Executa uma acao capturando o que for impresso, e joga no log da tela. */
    private void capturarSaida(Runnable acao) {
        String saida = capturar(acao);
        for (String linha : saida.split("\\R")) {
            if (!linha.isBlank()) {
                registrarLog(linha.stripTrailing());
            }
        }
    }

    private static String capturar(Runnable acao) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            acao.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    // ---------- Observer monitorado ----------

    /**
     * Tambem e um ObservadorDePagamento: e ele que fica registrado no Pagamento.
     * Repassa o aviso para o observer real e mostra na tela se o real reagiu ou nao.
     */
    private final class ObservadorMonitorado implements ObservadorDePagamento {
        final String nome;
        final ObservadorDePagamento real;
        final CartaoObservador cartao;

        ObservadorMonitorado(String nome, String regra, ObservadorDePagamento real) {
            this.nome = nome;
            this.real = real;
            this.cartao = new CartaoObservador(nome, regra);
        }

        @Override
        public void atualizar(Pagamento p) {
            String saida = capturar(() -> real.atualizar(p)).trim();
            if (saida.isEmpty()) {
                cartao.mostrarIgnorou(p.getStatus());
                System.out.println("  [" + nome + "] notificado, sem ação para " + p.getStatus().getDescricao());
            } else {
                cartao.mostrarReagiu(saida.replaceFirst("^\\[[^\\]]*\\]\\s*", ""));
                System.out.println("  " + saida);
            }
        }
    }

    private enum Estado { NAO_REGISTRADO, AGUARDANDO, REAGIU, IGNOROU }

    private final class CartaoObservador extends JPanel {
        Estado estado = Estado.NAO_REGISTRADO;
        private final JLabel nome;
        private final JLabel regra;
        private final JLabel situacao = new JLabel();
        private final JTextArea mensagem = new JTextArea();

        CartaoObservador(String textoNome, String textoRegra) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            nome = texto(textoNome, 14, TINTA, true);
            regra = texto(textoRegra, 12, LARANJA, false);
            situacao.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            situacao.setOpaque(true);
            situacao.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            mensagem.setEditable(false);
            mensagem.setLineWrap(true);
            mensagem.setWrapStyleWord(true);
            mensagem.setOpaque(false);
            mensagem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            mensagem.setAlignmentX(LEFT_ALIGNMENT);
            add(nome);
            add(Box.createVerticalStrut(2));
            add(regra);
            add(Box.createVerticalStrut(10));
            add(situacao);
            add(Box.createVerticalStrut(8));
            add(mensagem);
            mostrarNaoRegistrado();
        }

        void mostrarNaoRegistrado() {
            pintar(Estado.NAO_REGISTRADO, BRANCO, new Color(0xB0B0B0), "Não registrado", "");
        }

        void mostrarAguardando() {
            pintar(Estado.AGUARDANDO, BRANCO, AZUL, "Registrado", "Aguardando mudança de status.");
        }

        void mostrarReagiu(String texto) {
            pintar(Estado.REAGIU, VERDE_CLARO, VERDE, "Reagiu", texto);
        }

        void mostrarIgnorou(StatusPagamento status) {
            pintar(Estado.IGNOROU, CINZA_CLARO, TINTA_SUAVE, "Notificado, sem ação",
                    "Recebeu o aviso de " + status.getDescricao() + ", mas esse status não interessa a ele.");
        }

        private void pintar(Estado novo, Color fundo, Color destaque, String rotulo, String texto) {
            estado = novo;
            setBackground(fundo);
            Border linha = BorderFactory.createLineBorder(novo == Estado.NAO_REGISTRADO ? BORDA : destaque,
                    novo == Estado.REAGIU ? 2 : 1);
            setBorder(BorderFactory.createCompoundBorder(linha, BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            situacao.setText(rotulo);
            situacao.setForeground(destaque);
            situacao.setBackground(novo == Estado.REAGIU ? BRANCO : fundo == BRANCO ? FUNDO : BRANCO);
            mensagem.setText(texto);
            mensagem.setForeground(novo == Estado.NAO_REGISTRADO ? new Color(0xB0B0B0) : TINTA);
            Color corTexto = novo == Estado.NAO_REGISTRADO ? new Color(0xB0B0B0) : TINTA;
            nome.setForeground(corTexto);
            regra.setForeground(novo == Estado.NAO_REGISTRADO ? new Color(0xC8C8C8) : LARANJA);
            revalidate();
            repaint();
        }
    }

    // ---------- Componentes auxiliares ----------

    private void adicionarBotaoStatus(String rotulo, StatusPagamento status) {
        JButton b = botao(rotulo, status == StatusPagamento.APROVADO ? VERDE
                : status == StatusPagamento.EM_ANALISE ? AZUL : new Color(0xBD0A0A));
        b.addActionListener(e -> mudarStatus(status));
        painelBotoesStatus.add(b);
    }

    private static JPanel cartao(String titulo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BRANCO);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)));
        p.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel t = texto(titulo, 14, TINTA, true);
        p.add(t);
        p.add(Box.createVerticalStrut(10));
        return p;
    }

    private static JLabel texto(String conteudo, int tamanho, Color cor, boolean negrito) {
        JLabel l = new JLabel(conteudo);
        l.setFont(new Font(Font.SANS_SERIF, negrito ? Font.BOLD : Font.PLAIN, tamanho));
        l.setForeground(cor);
        l.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return l;
    }

    private static JButton botao(String rotulo, Color cor) {
        JButton b = new JButton(rotulo);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        b.setForeground(BRANCO);
        b.setBackground(cor);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return b;
    }

    private static JLabel selo(String rotulo, Color fundo, Color tinta) {
        JLabel l = new JLabel(rotulo);
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        l.setForeground(tinta);
        l.setBackground(fundo);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(tinta),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        l.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return l;
    }
}
