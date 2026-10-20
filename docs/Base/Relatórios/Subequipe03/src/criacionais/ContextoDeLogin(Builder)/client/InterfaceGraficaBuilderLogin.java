package client;

import builder.DiretorContextoLogin;
import builder.RequisicaoLoginBuilder;
import builder.RequisicaoLoginConcretoBuilder;
import product.RequisicaoLogin;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InterfaceGraficaBuilderLogin extends JFrame {

    private RequisicaoLoginBuilder builder;
    private DiretorContextoLogin diretor;

    // Componentes de Seleção de Estratégia
    private JRadioButton rbReceitaBaixoRisco;
    private JRadioButton rbReceitaAltoRisco;
    private JRadioButton rbReceitaBot;
    private JRadioButton rbCustomizado;

    // Componentes de Entrada Customizada
    private JTextField txtEmail;
    private JTextField txtSenha;
    private JTextField txtToken;
    private JComboBox<String> cbRisco;
    private JTextField txtCanal2FA;
    private JTextField txtCodigo2FA;
    private JCheckBox chkReplicar;

    // Saída
    private JTextArea txtConsole;

    public InterfaceGraficaBuilderLogin() {
        super("Padrão Criacional: Builder - Contexto de Login");
        this.builder = new RequisicaoLoginConcretoBuilder();
        this.diretor = new DiretorContextoLogin(builder);

        configurarJanela();
        inicializarComponentes();
        this.setVisible(true);
    }

    private void configurarJanela() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(750, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // Painel Superior: Escolha do Diretor vs Customizado
        JPanel painelEstrategia = new JPanel(new GridLayout(2, 2, 5, 5));
        painelEstrategia.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Modo de Construção (Director vs Builder)", TitledBorder.LEFT, TitledBorder.TOP));

        ButtonGroup grupoEstrategia = new ButtonGroup();
        rbReceitaBaixoRisco = new JRadioButton("Diretor: Web Baixo Risco (L01, L03, L05)");
        rbReceitaAltoRisco = new JRadioButton("Diretor: Dispositivo Novo com 2FA");
        rbReceitaBot = new JRadioButton("Diretor: Tentativa Bot (Sem Captcha)");
        rbCustomizado = new JRadioButton("Builder: Customizado (Forçar Invariantes)", true);

        grupoEstrategia.add(rbReceitaBaixoRisco);
        grupoEstrategia.add(rbReceitaAltoRisco);
        grupoEstrategia.add(rbReceitaBot);
        grupoEstrategia.add(rbCustomizado);

        painelEstrategia.add(rbReceitaBaixoRisco);
        painelEstrategia.add(rbReceitaAltoRisco);
        painelEstrategia.add(rbReceitaBot);
        painelEstrategia.add(rbCustomizado);

        // Painel Central: Inputs
        JPanel painelFormulario = new JPanel(new GridLayout(7, 2, 5, 5));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Parâmetros do Builder Customizado"));

        painelFormulario.add(new JLabel("E-mail:"));
        txtEmail = new JTextField("usuario@unb.br");
        painelFormulario.add(txtEmail);

        painelFormulario.add(new JLabel("Senha Hash:"));
        txtSenha = new JTextField("HASH_1234");
        painelFormulario.add(txtSenha);

        painelFormulario.add(new JLabel("Token Captcha:"));
        txtToken = new JTextField("CAPTCHA_MANUAL_99");
        painelFormulario.add(txtToken);

        painelFormulario.add(new JLabel("Nível de Risco (L03):"));
        cbRisco = new JComboBox<>(new String[]{"BAIXO", "ALTO"});
        painelFormulario.add(cbRisco);

        painelFormulario.add(new JLabel("Canal 2FA (Ex: SMS):"));
        txtCanal2FA = new JTextField("");
        painelFormulario.add(txtCanal2FA);

        painelFormulario.add(new JLabel("Código 2FA:"));
        txtCodigo2FA = new JTextField("");
        painelFormulario.add(txtCodigo2FA);

        painelFormulario.add(new JLabel("Replicar Sessão (SSO L05):"));
        chkReplicar = new JCheckBox("Ativar POST /session/replicate");
        painelFormulario.add(chkReplicar);

        // Ações de Habilitar/Desabilitar campos baseados no RadioButton
        rbCustomizado.addActionListener(e -> alternarCamposCustomizados(true));
        rbReceitaBaixoRisco.addActionListener(e -> alternarCamposCustomizados(false));
        rbReceitaAltoRisco.addActionListener(e -> alternarCamposCustomizados(false));
        rbReceitaBot.addActionListener(e -> alternarCamposCustomizados(false));

        // Botão de Construção
        JButton btnConstruir = new JButton("Construir Objeto RequisicaoLogin");
        btnConstruir.setBackground(new Color(0, 120, 215));
        btnConstruir.setForeground(Color.WHITE);
        btnConstruir.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConstruir.addActionListener(e -> construirProduto());

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.add(painelFormulario, BorderLayout.CENTER);
        painelCentro.add(btnConstruir, BorderLayout.SOUTH);

        // Painel Inferior: Console de Saída
        txtConsole = new JTextArea(12, 50);
        txtConsole.setEditable(false);
        txtConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtConsole.setBackground(new Color(30, 30, 30));
        txtConsole.setForeground(new Color(0, 255, 0));
        JScrollPane scrollConsole = new JScrollPane(txtConsole);
        scrollConsole.setBorder(BorderFactory.createTitledBorder("Produto Final (Imutável)"));

        this.add(painelEstrategia, BorderLayout.NORTH);
        this.add(painelCentro, BorderLayout.CENTER);
        this.add(scrollConsole, BorderLayout.SOUTH);
    }

    private void alternarCamposCustomizados(boolean habilitar) {
        txtEmail.setEnabled(habilitar);
        txtSenha.setEnabled(habilitar);
        txtToken.setEnabled(habilitar);
        cbRisco.setEnabled(habilitar);
        txtCanal2FA.setEnabled(habilitar);
        txtCodigo2FA.setEnabled(habilitar);
        chkReplicar.setEnabled(habilitar);
    }

    private void construirProduto() {
        try {
            RequisicaoLogin produtoPronto = null;

            if (rbReceitaBaixoRisco.isSelected()) {
                produtoPronto = diretor.construirLoginWebBaixoRisco("diretor@unb.br", "HASH_DIRETOR");
                log("[Director] Receita Baixo Risco acionada.");
            } else if (rbReceitaAltoRisco.isSelected()) {
                produtoPronto = diretor.construirLoginDispositivoNovoAltoRisco("diretor@unb.br", "HASH_DIRETOR", "889922");
                log("[Director] Receita Alto Risco c/ 2FA acionada.");
            } else if (rbReceitaBot.isSelected()) {
                produtoPronto = diretor.construirTentativaBotSemCaptcha("bot@malicioso.com", "HASH_ROUBADO");
                log("[Director] Receita Bot (Sem Captcha) acionada.");
            } else {
                // Modo Customizado usand//i//iio Builder Fluente
                log("[Builder] Montagem fluente customizada acionada...");
                produtoPronto = builder.resetar()
                        .comCredenciais(txtEmail.getText(), txtSenha.getText())
                        .comTokenCaptcha(txtToken.getText().isEmpty() ? null : txtToken.getText())
                        .comTelemetriaDispositivo("127.0.0.1", "SWING_UI_FINGERPRINT", cbRisco.getSelectedItem().toString())
                        .comDesafio2FA(txtCanal2FA.getText(), txtCodigo2FA.getText())
                        .comReplicacaoEcossistema(chkReplicar.isSelected())
                        .build();
            }

            imprimirResumoNaTela(produtoPronto);

        } catch (IllegalStateException ex) {
            log("[ERRO - BUILDER FAIL-FAST] " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Invariante do Domínio Violada",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void log(String mensagem) {
        txtConsole.append(mensagem + "\n");
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }

    private void imprimirResumoNaTela(RequisicaoLogin req) {
        txtConsole.append("\n======================================================\n");
        txtConsole.append("[Produto Construído: RequisicaoLogin]\n");
        txtConsole.append("  * E-mail       : " + req.getEmail() + "\n");
        txtConsole.append("  * Token Captcha: " + (req.getTokenCaptchaEnterprise() != null ? req.getTokenCaptchaEnterprise() : "[AUSENTE]") + "\n");
        txtConsole.append("  * Nível Risco  : " + req.getNivelRisco() + "\n");
        txtConsole.append("  * Exige 2FA?   : " + (req.isExige2FA() ? "SIM (Canal: " + req.getCanal2FA() + " | Código: " + req.getCodigo2FA() + ")" : "NÃO (Bypass Silencioso)") + "\n");
        txtConsole.append("  * Replicação   : " + (req.isReplicarSessaoEcossistema() ? "ATIVO" : "INATIVO") + "\n");
        txtConsole.append("======================================================\n\n");
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceGraficaBuilderLogin::new);
    }
}
