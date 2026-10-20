package client;

import facade.FachadaSessaoAutenticada;
import facade.ResumoSessaoDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InterfaceGraficaFacade extends JFrame {

    private final FachadaSessaoAutenticada fachada = new FachadaSessaoAutenticada();

    private final JTextField txtEmail = new JTextField("pedro@unb.br");
    private final JTextField txtIp = new JTextField("192.168.1.10");
    private final JTextField txtFingerprint = new JTextField("CHROME_LINUX_NIXOS");
    private final JComboBox<String> cbCanal = new JComboBox<>(new String[]{"WEB_DESKTOP", "MOBILE_APP", "SMART_TV"});
    private final JCheckBox chkReplicarSSO = new JCheckBox("Ativar POST /session/replicate (Mercado Pago - L05)", true);
    private final JCheckBox chkLembrarDisp = new JCheckBox("Vincular Dispositivo Confiável (Bypass 2FA - L03)", true);

    private final JLabel cardJwt = criarCardSubsistema("1. GeradorTokenJWT (L04)", "Aguardando...");
    private final JLabel cardSSO = criarCardSubsistema("2. ReplicadorSessaoSSO (L05)", "Aguardando...");
    private final JLabel cardDisp = criarCardSubsistema("3. RegistroDispositivo (L03)", "Aguardando...");
    private final JLabel cardAuditoria = criarCardSubsistema("4. AuditoriaSeguranca", "Aguardando...");

    private final JTextArea txtConsole = new JTextArea(8, 50);

    public InterfaceGraficaFacade() {
        super("Padrão GoF Estrutural: Facade - Subsistema de Sessão e SSO");
        configurarJanela();
        inicializarComponentes();
        setVisible(true);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel painelForm = new JPanel(new GridLayout(6, 2, 8, 8));
        painelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Parâmetros de Entrada (Client -> Facade)", TitledBorder.LEFT, TitledBorder.TOP));

        painelForm.add(new JLabel("E-mail Autenticado:"));
        painelForm.add(txtEmail);
        painelForm.add(new JLabel("IP de Origem:"));
        painelForm.add(txtIp);
        painelForm.add(new JLabel("Device Fingerprint:"));
        painelForm.add(txtFingerprint);
        painelForm.add(new JLabel("Canal de Acesso:"));
        painelForm.add(cbCanal);
        painelForm.add(new JLabel("Política de SSO:"));
        painelForm.add(chkReplicarSSO);
        painelForm.add(new JLabel("Política de Dispositivo:"));
        painelForm.add(chkLembrarDisp);

        JButton btnAcionarFacade = new JButton("Acionar Fachada: iniciarSessaoEcossistema() [1 Chamada -> 4 Subsistemas]");
        btnAcionarFacade.setBackground(new Color(0, 102, 204));
        btnAcionarFacade.setForeground(Color.WHITE);
        btnAcionarFacade.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAcionarFacade.setPreferredSize(new Dimension(750, 40));
        btnAcionarFacade.addActionListener(e -> executarFacade());

        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));
        painelTopo.add(painelForm, BorderLayout.CENTER);
        painelTopo.add(btnAcionarFacade, BorderLayout.SOUTH);

        JPanel painelCards = new JPanel(new GridLayout(2, 2, 10, 10));
        painelCards.setBorder(BorderFactory.createTitledBorder("Subsistemas Ocultos Orquestrados pela Fachada"));
        painelCards.add(cardJwt);
        painelCards.add(cardSSO);
        painelCards.add(cardDisp);
        painelCards.add(cardAuditoria);

        txtConsole.setEditable(false);
        txtConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtConsole.setBackground(new Color(30, 30, 30));
        txtConsole.setForeground(new Color(0, 255, 127));
        JScrollPane scrollConsole = new JScrollPane(txtConsole);
        scrollConsole.setBorder(BorderFactory.createTitledBorder("Resumo Entregue ao Cliente (ResumoSessaoDTO)"));

        add(painelTopo, BorderLayout.NORTH);
        add(painelCards, BorderLayout.CENTER);
        add(scrollConsole, BorderLayout.SOUTH);
    }

    private JLabel criarCardSubsistema(String titulo, String textoInicial) {
        JLabel lbl = new JLabel("<html><b>" + titulo + "</b><br/>" + textoInicial + "</html>");
        lbl.setOpaque(true);
        lbl.setBackground(new Color(235, 235, 235));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return lbl;
    }

    private void atualizarCard(JLabel card, String titulo, String valor, Color corFundo) {
        card.setText("<html><b>" + titulo + "</b><br/>" + valor + "</html>");
        card.setBackground(corFundo);
    }

    private void executarFacade() {
        ResumoSessaoDTO dto = fachada.iniciarSessaoEcossistema(
                txtEmail.getText(),
                txtIp.getText(),
                txtFingerprint.getText(),
                cbCanal.getSelectedItem().toString(),
                chkReplicarSSO.isSelected(),
                chkLembrarDisp.isSelected()
        );

        Color verdeClaro = new Color(200, 247, 197);
        Color amareloClaro = new Color(255, 243, 205);

        atualizarCard(cardJwt, "1. GeradorTokenJWT (L04)", dto.getTokenJwt(), verdeClaro);
        atualizarCard(cardSSO, "2. ReplicadorSessaoSSO (L05)", dto.getStatusReplicacaoSSO(),
                chkReplicarSSO.isSelected() ? verdeClaro : amareloClaro);
        atualizarCard(cardDisp, "3. RegistroDispositivo (L03)", dto.getStatusDispositivo(),
                chkLembrarDisp.isSelected() ? verdeClaro : amareloClaro);
        atualizarCard(cardAuditoria, "4. AuditoriaSeguranca", dto.getProtocoloAuditoria(), verdeClaro);

        txtConsole.append(">>> [Client -> Facade] Chamada unica a iniciarSessaoEcossistema() concluida:\n");
        txtConsole.append("  * Token JWT     : " + dto.getTokenJwt() + "\n");
        txtConsole.append("  * Status SSO    : " + dto.getStatusReplicacaoSSO() + "\n");
        txtConsole.append("  * Dispositivo   : " + dto.getStatusDispositivo() + "\n");
        txtConsole.append("  * Auditoria     : " + dto.getProtocoloAuditoria() + "\n");
        txtConsole.append("----------------------------------------------------------------------\n");
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceGraficaFacade::new);
    }
}
