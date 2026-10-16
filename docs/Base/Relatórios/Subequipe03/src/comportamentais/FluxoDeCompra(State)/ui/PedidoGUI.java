package ui;

import context.Pedido;
import context.StatusPedido;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * Interface gráfica (Swing) para o sistema de Pedido.
 *
 * Esta classe NÃO conhece as regras de transição: ela só chama
 * pedido.confirmarPagamento(), pedido.avancar() e pedido.cancelar().
 * Quem decide se a operação é válida continua sendo o State atual.
 */
public class PedidoGUI extends JFrame {

    private static final Color COR_INATIVO = new Color(0xE0E0E0);
    private static final Color COR_ATUAL = new Color(0x4CAF50);
    private static final Color COR_CANCELADO = new Color(0xE53935);

    private Pedido pedido;
    private int contador = 1;

    private final Map<StatusPedido, JLabel> etapas = new EnumMap<>(StatusPedido.class);
    private final JLabel lblPedido = new JLabel();
    private final JTextArea log = new JTextArea(10, 45);

    public PedidoGUI() {
        super("Ciclo de vida do Pedido — padrão State");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---- Topo: identificação do pedido + botão "Novo pedido" ----
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        lblPedido.setFont(lblPedido.getFont().deriveFont(Font.BOLD, 16f));
        JButton btnNovo = new JButton("Novo pedido");
        btnNovo.addActionListener(e -> novoPedido());
        topo.add(lblPedido, BorderLayout.WEST);
        topo.add(btnNovo, BorderLayout.EAST);

        // ---- Centro: etapas do fluxo + botões de ação ----
        JPanel painelEtapas = new JPanel(new GridLayout(1, 0, 6, 0));
        for (StatusPedido s : StatusPedido.values()) {
            JLabel l = new JLabel(s.name().replace('_', ' '), SwingConstants.CENTER);
            l.setOpaque(true);
            l.setBorder(BorderFactory.createEmptyBorder(14, 6, 14, 6));
            etapas.put(s, l);
            painelEtapas.add(l);
        }

        JButton btnPagar = new JButton("Confirmar pagamento");
        JButton btnAvancar = new JButton("Avançar");
        JButton btnCancelar = new JButton("Cancelar");
        btnPagar.addActionListener(e -> executar("Confirmar pagamento", () -> pedido.confirmarPagamento()));
        btnAvancar.addActionListener(e -> executar("Avançar", () -> pedido.avancar()));
        btnCancelar.addActionListener(e -> executar("Cancelar", () -> pedido.cancelar()));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.add(btnPagar);
        painelBotoes.add(btnAvancar);
        painelBotoes.add(btnCancelar);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centro.add(painelEtapas, BorderLayout.NORTH);
        centro.add(painelBotoes, BorderLayout.CENTER);

        // ---- Sul: histórico de operações ----
        log.setEditable(false);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createTitledBorder("Histórico"));

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        novoPedido();
        pack();
        setLocationRelativeTo(null); // centraliza na tela
    }

    private void novoPedido() {
        pedido = new Pedido("PED-" + String.format("%03d", contador++));
        log.append("--- " + pedido.getId() + " criado ---\n");
        atualizar();
    }

    /**
     * Executa uma operação do Pedido e registra o resultado.
     * Se o State atual não permitir, ele lança IllegalStateException
     * e nós apenas mostramos a mensagem ao usuário.
     */
    private void executar(String nome, Runnable operacao) {
        StatusPedido antes = pedido.getStatus();
        try {
            operacao.run();
            log.append(nome + ": " + antes + " -> " + pedido.getStatus() + "\n");
        } catch (IllegalStateException ex) {
            log.append("ERRO em '" + nome + "' (" + antes + "): " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Operação inválida", JOptionPane.WARNING_MESSAGE);
        }
        atualizar();
    }

    /** Redesenha o painel de etapas conforme o status atual. */
    private void atualizar() {
        StatusPedido atual = pedido.getStatus();
        lblPedido.setText("Pedido " + pedido.getId() + " — " + atual);
        etapas.forEach((status, label) -> {
            boolean ativo = status == atual;
            label.setBackground(!ativo ? COR_INATIVO
                    : status == StatusPedido.CANCELADO ? COR_CANCELADO : COR_ATUAL);
            label.setForeground(ativo ? Color.WHITE : Color.DARK_GRAY);
            label.setFont(label.getFont().deriveFont(ativo ? Font.BOLD : Font.PLAIN, 11f));
        });
        log.setCaretPosition(log.getDocument().getLength());
    }

    public static void main(String[] args) {
        // Swing deve ser criado/alterado na Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new PedidoGUI().setVisible(true));
    }
}