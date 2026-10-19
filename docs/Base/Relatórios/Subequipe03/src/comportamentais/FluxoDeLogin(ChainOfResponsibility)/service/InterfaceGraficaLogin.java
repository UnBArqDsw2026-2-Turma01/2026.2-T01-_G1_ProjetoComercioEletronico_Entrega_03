package service;

import context.RequisicaoLogin;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.io.PrintStream;

public class InterfaceGraficaLogin extends JFrame {

    private JTextField txtEmail, txtSenha, txtCaptcha, txt2FA;
    private JComboBox<String> cbRisco;
    private JTextArea textAreaConsole;
    
    // Painéis que representam os elos da cadeia
    private JPanel panelCaptcha, panelCredenciais, panelRisco, panel2FA;

    public InterfaceGraficaLogin() {
        setTitle("Demonstração: Chain of Responsibility - Login");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Painel Superior (Formulário de Entrada)
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("1. Dados da Requisição (Contexto)"));
        
        panelInput.add(new JLabel("E-mail:"));
        txtEmail = new JTextField("pedro@unb.br");
        panelInput.add(txtEmail);
        
        panelInput.add(new JLabel("Senha Hash:"));
        txtSenha = new JTextField("HASH_SENHA_123");
        panelInput.add(txtSenha);
        
        panelInput.add(new JLabel("Token Captcha (Achado-L01):"));
        txtCaptcha = new JTextField("CAPTCHA_OK_9981");
        panelInput.add(txtCaptcha);
        
        panelInput.add(new JLabel("Metadados do Dispositivo (Achado-L03):"));
        cbRisco = new JComboBox<>(new String[]{"IP_CONHECIDO | RISCO_BAIXO", "NOVO_DISPOSITIVO | RISCO_ALTO", "BOT_DETECTED | RISCO_ALTO"});
        panelInput.add(cbRisco);
        
        panelInput.add(new JLabel("Código 2FA (SMS/E-mail):"));
        txt2FA = new JTextField("123456");
        panelInput.add(txt2FA);

        // 2. Painel Central (Visualização da Cadeia)
        JPanel panelChain = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panelChain.setBorder(BorderFactory.createTitledBorder("2. Cadeia de Responsabilidade (Handlers)"));
        
        panelCaptcha = criarEloCard("1. Captcha");
        panelCredenciais = criarEloCard("2. Credenciais");
        panelRisco = criarEloCard("3. Risco");
        panel2FA = criarEloCard("4. 2FA");

        panelChain.add(panelCaptcha);
        panelChain.add(new JLabel("➔"));
        panelChain.add(panelCredenciais);
        panelChain.add(new JLabel("➔"));
        panelChain.add(panelRisco);
        panelChain.add(new JLabel("➔"));
        panelChain.add(panel2FA);

        // 3. Painel Inferior (Console e Botão)
        JPanel panelBottom = new JPanel(new BorderLayout(5, 5));
        JButton btnExecutar = new JButton("Submeter Login");
        btnExecutar.setFont(new Font("Arial", Font.BOLD, 14));
        btnExecutar.addActionListener(this::executarLogin);
        
        textAreaConsole = new JTextArea(12, 50);
        textAreaConsole.setEditable(false);
        textAreaConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollConsole = new JScrollPane(textAreaConsole);
        scrollConsole.setBorder(BorderFactory.createTitledBorder("3. Saída do Console (Rastreabilidade)"));

        panelBottom.add(btnExecutar, BorderLayout.NORTH);
        panelBottom.add(scrollConsole, BorderLayout.CENTER);

        // Adicionando tudo ao JFrame
        add(panelInput, BorderLayout.NORTH);
        add(panelChain, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        // Redireciona o System.out.println para o JTextArea
        redirecionarConsole();
    }

    private JPanel criarEloCard(String titulo) {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(130, 60));
        p.setBackground(Color.LIGHT_GRAY);
        p.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        p.setLayout(new GridBagLayout());
        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(label);
        return p;
    }

    private void executarLogin(ActionEvent e) {
        // Limpa o console e reseta as cores da cadeia para cinza
        textAreaConsole.setText("");
        resetarCores();

        // Extrai os dados da UI para o objeto de contexto
        RequisicaoLogin req = new RequisicaoLogin(
                txtEmail.getText(),
                txtSenha.getText(),
                txtCaptcha.getText(),
                txt2FA.getText(),
                cbRisco.getSelectedItem()
        );

        // Executa o padrão (O System.out será capturado pelo JTextArea)
        ServicoDeAutenticacao authService = new ServicoDeAutenticacao();
        authService.autorizar(req);

        // Analisa o texto impresso no console para pintar a UI de forma reativa
        colorirCadeiaPeloLog();
    }

    private void resetarCores() {
        Color cinza = Color.LIGHT_GRAY;
        panelCaptcha.setBackground(cinza);
        panelCredenciais.setBackground(cinza);
        panelRisco.setBackground(cinza);
        panel2FA.setBackground(cinza);
    }

    private void colorirCadeiaPeloLog() {
        String log = textAreaConsole.getText();
        Color verde = new Color(144, 238, 144);
        Color vermelho = new Color(255, 153, 153);
        Color amarelo = new Color(255, 255, 153);

        // Lógica visual baseada no comportamento do Chain of Responsibility
        if (log.contains("[1. ValidadorCaptcha]")) {
            panelCaptcha.setBackground(log.contains("invalido ou ausente") ? vermelho : verde);
        }
        if (log.contains("[2. ValidadorCredenciais]")) {
            panelCredenciais.setBackground(log.contains("Credenciais invalidas") ? vermelho : verde);
        }
        if (log.contains("[3. ValidadorRisco]")) {
            panelRisco.setBackground(log.contains("Score de risco elevado") ? amarelo : verde);
        }
        if (log.contains("[4. ValidadorTwoFactor]")) {
            if (log.contains("[BYPASS]")) {
                panel2FA.setBackground(Color.LIGHT_GRAY); // Ignorado silenciosamente
            } else if (log.contains("Codigo 2FA invalido")) {
                panel2FA.setBackground(vermelho);
            } else if (log.contains("validado com sucesso")) {
                panel2FA.setBackground(verde);
            }
        }
    }

    private void redirecionarConsole() {
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                textAreaConsole.append(String.valueOf((char) b));
                textAreaConsole.setCaretPosition(textAreaConsole.getDocument().getLength());
            }
        });
        System.setOut(printStream);
    }

    public static void main(String[] args) {

        try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {

          e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new InterfaceGraficaLogin().setVisible(true);
        });
    }
}
