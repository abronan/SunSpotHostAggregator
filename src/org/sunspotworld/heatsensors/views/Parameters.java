package org.sunspotworld.heatsensors.views;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.sunspotworld.heatsensors.utils.ApplicationSetup;
import org.sunspotworld.heatsensors.utils.XMLUtilities;

/**
 * Window used to configure the threshold and save state into an XML file.
 * Can be opened through {@link TemperatureMonitor}
 * 
 * @author Alexandre
 */
public class Parameters extends JFrame {

    private static final long serialVersionUID = 1L;
    private ApplicationSetup setup;
    private JPanel jContentPane = null;
    private DoubleJSlider thresholdSlider = null;
    private JLabel labelThreshold = null;
    private JButton buttonSave = null;
    private JButton buttonExit = null;
    private JTextField textThreshold = null;

   /**
    * This is the default constructor
    */
    public Parameters() {
        super();
        initialize();
    }
    
    /**
    * This is the default constructor
    */
    public Parameters(ApplicationSetup setup) {
        super();
        this.setup = setup;
        initialize();
    }

   /**
    * This method initializes this
    * 
    * @return void
    */
    private void initialize() {
        this.setSize(494, 302);
        this.setResizable(false);
        this.setContentPane(getJContentPane());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Parameters");
    }

   /**
    * This method initializes jContentPane
    * 
    * @return javax.swing.JPanel
    */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            labelThreshold = new JLabel();
            labelThreshold.setBounds(new Rectangle(18, 15, 69, 16));
            labelThreshold.setText("Threshold :");
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getThresholdSlider(), null);
            jContentPane.add(labelThreshold, null);
            jContentPane.add(getButtonSave(), null);
            jContentPane.add(getButtonExit(), null);
            jContentPane.add(getTextThreshold(), null);
        }
        return jContentPane;
    }

   /**
    * This method initializes thresholdSlider
    * 
    * @return javax.swing.JSlider
    */
    private DoubleJSlider getThresholdSlider() {
        if (thresholdSlider == null) {
            final DecimalFormat df = new DecimalFormat("0.####");
            thresholdSlider = new DoubleJSlider(JSlider.HORIZONTAL, 2000, 0, 100);
            thresholdSlider.setMajorTickSpacing(100);
            thresholdSlider.setMinorTickSpacing(50);
            thresholdSlider.setPaintTicks(true);
            thresholdSlider.setBounds(new Rectangle(13, 47, 459, 43));
            Hashtable labelTable = new Hashtable();
            for(int i = 0; i <= 2000; i += 100) {
                labelTable.put(new Integer(i), new JLabel(String.valueOf(i/100)));
            }
            thresholdSlider.setLabelTable(labelTable);
            thresholdSlider.setPaintLabels(true);
            thresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                @Override
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    textThreshold.setText(df.format(thresholdSlider.getScaledValue()));
                }
            });
        }
        return thresholdSlider;
    }

   /**
    * This method initializes buttonSave	
    * 	
    * @return javax.swing.JButton	
    */
    private JButton getButtonSave() {
        if (buttonSave == null) {
            buttonSave = new JButton();
            buttonSave.setBounds(new Rectangle(269, 241, 93, 22));
            buttonSave.setText("Save");
            buttonSave.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ApplicationSetup setup = new ApplicationSetup(
                        thresholdSlider.getScaledValue()
                    );
                    saveConfig(setup);
                }
            });
        }
        return buttonSave;
    }

   /**
    * This method initializes buttonExit	
    * 	
    * @return javax.swing.JButton	
    */
    private JButton getButtonExit() {
        if (buttonExit == null) {
            buttonExit = new JButton();
            buttonExit.setBounds(new Rectangle(376, 241, 93, 22));
            buttonExit.setText("Exit");
            buttonExit.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeWindow();
                }
            });
        }
        return buttonExit;
    }

   /**
    * This method initializes textThreshold
    * 	
    * @return javax.swing.JTextField	
    */
    private JTextField getTextThreshold() {
        if (textThreshold == null) {
            textThreshold = new JTextField(20);
            textThreshold.setBounds(new Rectangle(95, 14, 53, 20));
            textThreshold.setBackground(Color.WHITE);
            textThreshold.setText("0");
            if(setup != null){
                thresholdSlider.setValue((int)setup.getThreshold().doubleValue());
                textThreshold.setText(setup.getThreshold().toString());
            }
            textThreshold.addKeyListener(new KeyAdapter(){
                @Override
                public void keyReleased(KeyEvent ke) {
                    String typed = textThreshold.getText();
                    thresholdSlider.setValue(0);
                    if(!typed.matches("\\d+(\\.\\d*)?")) {
                        return;
                    }
                    double value = Double.parseDouble(typed)*thresholdSlider.scale;
                    thresholdSlider.setValue((int)value);
                }
            });
        }
        return textThreshold;
    }

   /** This method closes the window and sets the parent component enabled
    * 	
    * @return javax.swing.JPopupMenu	
    */
    public void closeWindow(){
        if(JOptionPane.showConfirmDialog(
                this,
                "Are you sure?",
                "Cancel",
                JOptionPane.OK_CANCEL_OPTION
           ) == JOptionPane.OK_OPTION){
                this.dispose();
        }
    }
    
   /** This method saves the setup configuration into an xml file
    * 	
    * @return javax.swing.JPopupMenu	
    */
    public void saveConfig(ApplicationSetup setup){
        if(JOptionPane.showConfirmDialog(
                this,
                "The configuration will be saved, confirm?",
                "Cancel",
                JOptionPane.OK_CANCEL_OPTION
           ) == JOptionPane.OK_OPTION){
            try {
                XMLUtilities.writeSetup(setup);
            } catch (IOException ex) {
                System.out.println("Failed to write setup into file..");
                Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class DoubleJSlider extends JSlider {
        final int scale;

        public DoubleJSlider(int min, int max, int value, int scale) {
            super(min, max, value);
            this.scale = scale;
        }

        public double getScaledValue() {
            return ((double)super.getValue()) / this.scale;
        }
    }
}