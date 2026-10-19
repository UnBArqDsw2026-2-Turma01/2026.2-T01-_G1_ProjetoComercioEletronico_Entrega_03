package view;

import controller.ControladorDeAutenticacao;
import dto.ResultadoAutenticacaoDTO;

import javax.swing.*;
import java.awt.*;

public class InterfaceGraficaLogin extends JFrame {

    private static final Color VERDE = new Color(0x2E7D32);
    private static final Color VERMELHO = new Color(0xC62828);

    private final ControladorDeAutenticacao controlador;

    private final JTextField campoEmail = new JTextField("pedro@unb.br", 20);
    private final JPasswordField campoSenha = new JPasswordField("HASH_SENHA_123", 20);
    private final JComboBox<String> comboCaptcha =
            new JComboBox<>(new String[]{"CAPTCHA_OK_TOKEN_VALIDO", "TOKEN_FALSO"});
    private final JComboBox<String> comboRisco = new JComboBox<>(new String[]{"BAIXO", "ALTO"});
    private final JTextField campoCodigo2FA = new JTextField("123456", 20);
    private final JButton botaoLogin = new JButton("Entrar");
    private final JLabel rotuloStatus = new JLabel("Aguardando autenticação...", SwingConstants.CENTER);

    public InterfaceGraficaLogin(ControladorDeAutenticacao controlador) {
        super("Login - GRASP Controller");
        this.controlador = controlador;
        montarTela();
        botaoLogin.addActionListener(e -> aoClicarBotaoLogin());
    }

    private void montarTela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        form.add(new JLabel("E-mail:"));          form.add(campoEmail);
        form.add(new JLabel("Senha (hash):"));    form.add(campoSenha);
        form.add(new JLabel("Token captcha:"));   form.add(comboCaptcha);
        form.add(new JLabel("Nível de risco:"));  form.add(comboRisco);
        form.add(new JLabel("Código 2FA:"));      form.add(campoCodigo2FA);

        rotuloStatus.setOpaque(true);
        rotuloStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel rodape = new JPanel(new BorderLayout(8, 8));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        rodape.add(botaoLogin, BorderLayout.NORTH);
        rodape.add(rotuloStatus, BorderLayout.CENTER);

        add(form, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    /** Evento de sistema: a View apenas delega ao Controller. */
    private void aoClicarBotaoLogin() {
        ResultadoAutenticacaoDTO resultado = controlador.processarSolicitacaoLogin(
                campoEmail.getText(),
                new String(campoSenha.getPassword()),
                (String) comboCaptcha.getSelectedItem(),
                (String) comboRisco.getSelectedItem(),
                campoCodigo2FA.getText());
        exibir(resultado);
    }

    private void exibir(ResultadoAutenticacaoDTO resultado) {
        rotuloStatus.setText("<html><center>" + resultado.getMensagem() + "</center></html>");
        rotuloStatus.setBackground(resultado.isSucesso() ? VERDE : VERMELHO);
        rotuloStatus.setForeground(Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new InterfaceGraficaLogin(new ControladorDeAutenticacao()).setVisible(true));
    }
}
