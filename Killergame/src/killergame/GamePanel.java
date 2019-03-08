package killergame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GamePanel extends JPanel implements ActionListener {
    
    private KillerGame kg;
    
    private Viewer v;    
    private JButton conf;
    
    
    public GamePanel(KillerGame kg, Viewer v){        
        super();        
        this.kg = kg;
        this.v = v;
        this.organizePanel();
                
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == "Configuracion") {    
            this.kg.getNextServer().sendCommand("final" + this.kg.getId());
            this.kg.resetGame();
        }

    }
        
    private void organizePanel(){
        
        FlowLayout layout = new FlowLayout();
        this.setLayout(layout); // Le ponemos el GridBagLayout
        this.setBackground(Color.BLACK);
        
        GridBagConstraints constraints = new GridBagConstraints();
        
        constraints = createConstraint(1, 1, 10, 10);
       constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;   
        this.add(this.v,constraints);        
        
        this.conf = new JButton("Configuracion");
        this.conf.addActionListener(this);
        constraints = createConstraint(0, 5, 1, 1);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        this.add(this.conf);
              
        this.kg.getContentPane().add(this);
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
