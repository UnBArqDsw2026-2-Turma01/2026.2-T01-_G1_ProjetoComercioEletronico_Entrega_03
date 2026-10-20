package view;

import controller.ControladorDeAutenticacao;
import dto.ResultadoAutenticacaoDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InterfaceGraficaLogin extends JFrame {

    // A UI conhece apenas o Controlador (GRASP Controller) e os DTOs
    private final ControladorDeAutenticacao controlador;

    // Componentes Visuais
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JTextField txtCaptcha;
    private JComboBox<String> cbRisco;
    private JTextField txtCodigo2FA;
    private JTextArea txtConsole;
    private JPanel painelStatus;

    public InterfaceGraficaLogin() {
        super("Padrão GRASP: Controller - Login");
        this.controlador = new ControladorDeAutenticacao();

        configurarJanela();
        inicializarComponentes();
        this.setVisible(true);
    }

    private void configurarJanela() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // Painel de Formulário
        JPanel painelFormulario = new JPanel(new GridLayout(5, 2, 10, 15));
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dados da Requisição (Camada de Apresentação)", TitledBorder.LEFT, TitledBorder.TOP));

        painelFormulario.add(new JLabel("E-mail:"));
        txtEmail = new JTextField("pedro@unb.br");
        painelFormulario.add(txtEmail);

        painelFormulario.add(new JLabel("Senha Hash:"));
        txtSenha = new JPasswordField("HASH_SENHA_123");
        painelFormulario.add(txtSenha);

        painelFormulario.add(new JLabel("Token Captcha (Achado L01):"));
        txtCaptcha = new JTextField("CAPTCHA_ENTERPRISE_OK_9981");
        painelFormulario.add(txtCaptcha);

        painelFormulario.add(new JLabel("Nível de Risco do Dispositivo:"));
        cbRisco = new JComboBox<>(new String[]{"BAIXO", "ALTO"});
        painelFormulario.add(cbRisco);

        painelFormulario.add(new JLabel("Código 2FA (Se SMS/Email):"));
        txtCodigo2FA = new JTextField("");
        painelFormulario.add(txtCodigo2FA);

        // Painel do Botão
        JPanel painelBotao = new JPanel();
        JButton btnLogin = new JButton("Autenticar (Disparar Evento do Sistema)");
        btnLogin.setBackground(new Color(40, 167, 69));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(400, 40));
        
        // DELEGAÇÃO GRASP: O clique apenas coleta dados e repassa ao controlador
        btnLogin.addActionListener(e -> aoClicarBotaoLogin());
        painelBotao.add(btnLogin);

        // Painel de Saída (Feedback visual)
        painelStatus = new JPanel();
        painelStatus.setBackground(Color.DARK_GRAY);
        painelStatus.setPreferredSize(new Dimension(500, 30));

        txtConsole = new JTextArea(8, 40);
        txtConsole.setEditable(false);
        txtConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollConsole = new JScrollPane(txtConsole);
        scrollConsole.setBorder(BorderFactory.createTitledBorder("Retorno do Controlador (DTO)"));

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(painelStatus, BorderLayout.NORTH);
        painelInferior.add(scrollConsole, BorderLayout.CENTER);

        this.add(painelFormulario, BorderLayout.NORTH);
        this.add(painelBotao, BorderLayout.CENTER);
        this.add(painelInferior, BorderLayout.SOUTH);
    }

    /**
     * Este método prova a aplicação do GRASP Controller.
     * A interface não contém regras de negócio. Ela capta as strings e aciona o controlador.
     */
    private void aoClicarBotaoLogin() {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());
        String tokenCaptcha = txtCaptcha.getText().isEmpty() ? null : txtCaptcha.getText();
        String risco = cbRisco.getSelectedItem().toString();
        String codigo2FA = txtCodigo2FA.getText();

        log("[UI] Enviando evento de sistema para o ControladorDeAutenticacao...");

        // Chama o controlador e recebe o DTO de resposta
        ResultadoAutenticacaoDTO resultado = controlador.processarSolicitacaoLogin(
                email, senha, tokenCaptcha, risco, codigo2FA
        );

        // Atualiza a tela com base no DTO
        atualizarPainelVisual(resultado);
    }

    private void atualizarPainelVisual(ResultadoAutenticacaoDTO resultado) {
        log("[UI] DTO Recebido. Sucesso: " + resultado.isSucesso());
        log("[UI] Mensagem: " + resultado.getMensagem());
        log("------------------------------------------------------\n");

        if (resultado.isSucesso()) {
            painelStatus.setBackground(new Color(40, 167, 69)); // Verde
            JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Autorizado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            painelStatus.setBackground(new Color(220, 53, 69)); // Vermelho
            JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Acesso Negado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void log(String mensagem) {
        txtConsole.append(mensagem + "\n");
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }

    public static void main(String[] args) {
        // Ponto de entrada da aplicação cliente
        SwingUtilities.invokeLater(InterfaceGraficaLogin::new);
    }
}
