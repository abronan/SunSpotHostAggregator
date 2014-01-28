/*
 * SunSpotHostApplication.java
 *
 * Created on 17 avr. 2012 15:40:51;
 */

package org.sunspotworld.heatsensors;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.sunspotworld.heatsensors.utils.ApplicationSetup;
import org.sunspotworld.heatsensors.utils.SwingLookAndFeel;
import org.sunspotworld.heatsensors.utils.XMLUtilities;
import org.sunspotworld.heatsensors.views.DataPanel;
import org.sunspotworld.heatsensors.views.TemperatureMonitor;


/**
 * Sample Sun SPOT host application for monitoring temperatures over a SPOT network.
 * 
 * @author Alexandre
 */
public class HeatSensorsHostNetwork implements PacketTypes {
    
    /** Send Broadcast data connection. */
    RadiogramConnection sendbc = null;
    Radiogram dg = null;

    TemperatureMonitor monitor;         // The main window of the host application
    private DataPanel dataPanel;        // Panel with graphics
    long ourAddr;                       // Our address un long format
    
    /**
     * Print out our radio address.
     */
    public void startMonitoring() {
        ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
        
        ApplicationSetup setup = null;
        try {
            setup = XMLUtilities.getSetup();
        } catch(Exception e) {
            e.printStackTrace(); // debug
            System.out.println("Failed to load configuration..");
        }
        
        TopologyManager manager;
        if(setup != null){
            manager = new TopologyManager(monitor, setup);
        } else {
            manager = new TopologyManager(monitor);
        }
        
        /** Starting broadcast receiver service. */
        BroadcastListener rcvbc = new BroadcastListener(manager);
        rcvbc.start();
        
        /** Starting unicast receiver service. */
        UnicastListener rcvuni = new UnicastListener(manager);
        rcvuni.start();
        
        Utils.sleep(2000);
        
        manager.sendBroadcast();
    }

    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        
        final HeatSensorsHostNetwork app = new HeatSensorsHostNetwork();
        app.setup();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    SwingLookAndFeel.setNativeLookAndFeel();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Look and Feel failed to initialize");
                }
                
                ApplicationSetup setup = null;
                TemperatureMonitor monitor;
                try {
                    setup = XMLUtilities.getSetup();
                } catch(Exception e) {
                    System.out.println("Failed to load configuration..");
                    Logger.getLogger(HeatSensorsHostNetwork.class.getName()).log(Level.SEVERE, null, e);
                }
                if(setup != null) {
                    monitor = new TemperatureMonitor(app, app.getDataPanel(), setup);
                } else {
                    monitor = new TemperatureMonitor(app, app.getDataPanel());
                }
                monitor.setVisible(true);
            }
        });
    }
    
    private void setup() {
        dataPanel = new DataPanel();
        dataPanel.validate();
    }
    
    public void setMonitor(TemperatureMonitor frame){
        this.monitor = frame;
    }
    
    public TemperatureMonitor getMonitor(){
        return this.monitor;
    }
    
    public DataPanel getDataPanel(){
        return this.dataPanel;
    }
    
}
