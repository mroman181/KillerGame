package killergame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ConnectPanel extends JPanel implements ActionListener {

    private KillerGame kg;

    private JTextField ipNS;
    private JTextField portNS;
    private JTextField ipPS;
    private JTextField portPS;

    private JButton connectNS;
    private JButton connectPS;
    private JButton start;

    private JTextArea infops;
    private JTextArea infons;
    private JTextArea textStart;
    private JTextArea textns;
    private JTextArea textps;
    private JTextArea textPads;
    private JTextArea nPads;

    public ConnectPanel(KillerGame kg) {
        super();

        this.kg = kg;
        this.organizePanel();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == "ConnectNS") {
            System.out.println(e.getActionCommand());
            VisualHandler vh = this.kg.getNextServer();
            this.textns.setText("Trying");

            String ip = this.ipNS.getText();
            String puerto = this.portNS.getText();
            int port = Integer.parseInt(puerto);
            vh.getClient().setChanged(true);
            vh.getClient().setSocket(ip, port);
            vh.closeLink();
        }

        if (e.getActionCommand() == "ConnectPS") {
            System.out.println(e.getActionCommand());
            //this.kg.getPreviousServer().setSocketNull();
            
            VisualHandler vh = this.kg.getPreviousServer();
            this.textps.setText("Trying");

            String ip = this.ipPS.getText();
            String puerto = this.portPS.getText();
            int port = Integer.parseInt(puerto);
            vh.getClient().setChanged(true);
            vh.getClient().setSocket(ip, port);
            vh.closeLink();
        }

        if (e.getActionCommand() == "Start") {
            System.out.println(e.getActionCommand());

            this.kg.ready();
        }

    }

    public String getTextTextps() {
        return this.textps.getText();
    }

    public String getTextTextns() {
        return this.textns.getText();
    }

    public void setTextInfops(String text) {
        this.infops.setText(text);
    }

    public void setTextInfons(String text) {
        this.infons.setText(text);
    }

    public void setTextTextps(String text) {
        this.textps.setText(text);
    }

    public void setTextTextns(String text) {
        this.textns.setText(text);
    }

    public void setTextIpNS(String ip) {
        this.ipNS.setText(ip);
    }

    public void setTextIpPS(String ip) {
        this.ipPS.setText(ip);
    }

    public void setTextPortNS(int port) {
        String sport;
        sport = Integer.toString(port);
        this.portNS.setText(sport);
    }

    public void setTextPortPS(int port) {
        String sport;
        sport = Integer.toString(port);
        this.portPS.setText(sport);
    }
    
    public void setNPads(int n) {
        String sn;
        sn = Integer.toString(n);
        this.nPads.setText(sn);
    }

    public void resetPanel() {
        // this.textps.setText("Not Connected");
        // this.textns.setText("Not Connected");
        this.setVisible(true);
    }

    private void organizePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        this.infons = new JTextArea("Next Server: ");
        constraints = createConstraint(0, 0, 2, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(this.infons, constraints);

        this.ipNS = new JTextField("localhost");
        constraints = createConstraint(2, 0, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(ipNS, constraints);

        this.portNS = new JTextField("1234");
        constraints = createConstraint(3, 0, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(portNS, constraints);

        this.textns = new JTextArea("Waiting connection");
        constraints = createConstraint(4, 0, 2, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(this.textns, constraints);

        this.infons = new JTextArea("Previous Server: ");
        constraints = createConstraint(0, 1, 2, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(this.infons, constraints);

        this.ipPS = new JTextField("localhost");
        constraints = createConstraint(2, 1, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(ipPS, constraints);

        this.portPS = new JTextField("1234");
        constraints = createConstraint(3, 1, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(portPS, constraints);

        this.textps = new JTextArea("Waiting connection");
        constraints = createConstraint(4, 1, 2, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(this.textps, constraints);

        this.connectNS = new JButton("ConnectNS");
        this.connectNS.addActionListener(this);
        constraints = createConstraint(6, 0, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(this.connectNS, constraints);

        this.connectPS = new JButton("ConnectPS");
        this.connectPS.addActionListener(this);
        constraints = createConstraint(6, 1, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(this.connectPS, constraints);
        
        this.textPads = new JTextArea("Pads conectados:");
        constraints = createConstraint(0, 2, 2, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(textPads, constraints);

        this.nPads = new JTextArea("0");
        constraints = createConstraint(2, 2, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(nPads, constraints);

        this.start = new JButton("Start");
        this.start.addActionListener(this);
        constraints = createConstraint(3, 3, 2, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(this.start, constraints);

        this.setLayout(new FlowLayout());
        this.setBackground(Color.BLACK);
        this.add(panel);

        this.kg.getContentPane().add(this);
    }
    
    public void updatePads(){
        this.nPads.setText(Integer.toString(this.kg.getSizePads()));
    }

    private GridBagConstraints createConstraint(int x, int y, int width, int height) {

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        return constraints;
    }
}
