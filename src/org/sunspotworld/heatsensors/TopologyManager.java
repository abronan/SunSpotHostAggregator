/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld.heatsensors;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.service.Task;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sunspotworld.heatsensors.utils.ApplicationSetup;
import org.sunspotworld.heatsensors.utils.PacketTransmitter;
import org.sunspotworld.heatsensors.utils.RadioUtilities;
import org.sunspotworld.heatsensors.views.TemperatureMonitor;

/**
 *
 * @author Alexandre
 */
public class TopologyManager implements PacketTypes {

    SPOTInfo info;                          // Our Information
    BroadcastListener rcvbroad;             // Broadcast receiver service
    UnicastListener rcvuni;                 // Unicast receiver service
    TemperatureMonitor logger;              // Logging Window
    Task ping;                              // Background task for pinging sons
    PacketTransmitter transmitter;          // Transmitter to send data to other SPOTs
    SensorManager sensorManager;            // Used to aggregate data and monitor the temperature sensor
    Map<String, SPOTInfo> neighbors;        // The neighbors at radio distance. <String, SPOTInfo>
    List<String> sons;                      // List of sons in the tree
    boolean checkdone = false;              // If the SPOT has done the CHECK Broadcast or not
    boolean attached = false;               // Indicates if the SPOT is linked to the tree
    int lostCount = 0;                      // LOST diffuse counter when the connection is lost
    
    /**
     * Constructor.
     */
    public TopologyManager(TemperatureMonitor frame){
        /* Get the frame instance for logging info. */
        this.logger = frame;
        neighbors = Collections.synchronizedMap(new HashMap<String, SPOTInfo>());
        sons = new ArrayList<String>();
        transmitter = new PacketTransmitter();
        sensorManager = new SensorManager(this, logger, transmitter);
        info = new SPOTInfo();
    }
    
    /**
     * Alternate constructor.
     */
    public TopologyManager(TemperatureMonitor frame, ApplicationSetup setup){
        /* Get the frame instance for logging info. */
        this.logger = frame;
        neighbors = Collections.synchronizedMap(new HashMap<String, SPOTInfo>());
        sons = new ArrayList<String>();
        transmitter = new PacketTransmitter();
        info = new SPOTInfo();
        info.threshold = setup.getThreshold();
        sensorManager = new SensorManager(this, logger, transmitter);
    }
    
    /**
     * Handle a packet received on a Unicast or Broadcast connection.
     * 
     * @param connectionType The type of the connection. May be BROADCAST or UNICAST.
     * @param dg The received packet
     */
    public void handlePacket(byte connectionType, Radiogram dg){
        try{
            byte messageType = dg.readByte();
            switch(messageType){
                /** HELLO Request Handler. */
                case HELLO :
                    handleHELLO(dg);
                    break;
                /** REPLY Response Handler. */
                case REPLY :
                    handleREPLY(connectionType, dg);
                    break;
                /** LOST Packet Handler. */
                case LOST :
                    handleLOST(connectionType, dg);
                    break;
                /** TIED Packet Handler. */
                case TIED :
                    handleTIED(dg);
                    break;
                /** TEMP Packet Handler. */
                case TEMP :
                    handleTEMP(dg);
                    break;  
                /** PING Packet Handler. */
                case PING:
                    // TODO send reply with temperature
                    break;
                default :
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error in handling packet from : " + dg.getAddress());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle a packet marked as HELLO.
     * These packets are used to build the tree and to discover neighbors.
     * 
     * @param connectionType The type of the connection. May be BROADCAST or UNICAST.
     * @param dg The received packet
     */
    public synchronized void handleHELLO(Radiogram dg) throws IOException {
        String host = dg.getAddress();
        addOrUpdateHost(dg, host);
    }
    
    /**
     * Handle a packet marked as LINK.
     * These packets are used to signal that a SPOT has no father assigned.
     * 
     * @param connectionType The type of the connection. May be BROADCAST or UNICAST.
     * @param dg The received packet
     */
    public void handleREPLY(byte connectionType, Radiogram dg) throws IOException {
        String host = dg.getAddress();
        addOrUpdateHost(dg, host);
        if(!sons.contains(host)){
            addSon(dg, host);
            /* Reply with a CHECK packet if the LINK was Broadcasted */
            if(connectionType == BROADCAST){
                transmitter.send(UNICAST, HELLO, info, host);
            }
        }
    }
    
    /**
     * Handle a packet marked as LOST.
     * These packets are used to signal that a SPOT is in research of a new father.
     * 
     * @param connectionType The type of the connection. May be BROADCAST or UNICAST.
     * @param dg The received packet
     */
    public void handleLOST(byte connectionType, Radiogram dg) throws IOException {
        handleREPLY(connectionType, dg);
    }
    
    /**
     * Handle a packet marked as TIED.
     * These packets are used to signal that a SPOT has already a father.
     * 
     * @param dg The received packet
     */
    public void handleTIED(Radiogram dg) throws IOException {
        String host = dg.getAddress();
        addOrUpdateHost(dg, host);
    }
    
    /**
     * Handle a packet marked as TEMP.
     * These packets are used to send a temperature value.
     * 
     * @param dg The received packet
     */
    public void handleTEMP(Radiogram dg) throws IOException {
        String host = dg.getAddress();
        long date = dg.readLong();
        double value = dg.readDouble();
        int coeff = dg.readInt();
        sensorManager.putTemperature(
                host, 
                new Temperature(date, value, coeff)
        );
    }
    
    /**
     * Creates a new entry for a host in our neighbors list. Update this entry if already added.
     *
     * @param dg The Radiogram with the SPOTInfo fields to extract
     * @param host The host IEEE address
     */
    public void addOrUpdateHost(Radiogram dg, String host) throws IOException{
        /* Creates a new SPOTInfo entry in case of a new host */
        if(!neighbors.containsKey(host)){
            logger.logInfo("Added entry in neighbors for : " + host);
            createInfo(dg, host);
        }
        /* Updates the entry with new informations */
        else {
            logger.logInfo("Updated entry in neighbors for : " + host);
            updateInfo(dg, host);
        }
    }
    
    /**
     * Add a host to sons list. Increment the number of son in SPOTInfo instance.
     * Calls {@link TopologyManager#startPingMonitor()} when the first son is added in the list.
     *
     * @param dg The Radiogram with the SPOTInfo fields to extract
     * @param sonAddr The IEEE address of the host we want to add to sons list
     */
    public synchronized void addSon(Radiogram dg, String sonAddr){
        sons.add(sonAddr);
        sensorManager.putTemperature(
                sonAddr,
                new Temperature()
        );
        logger.logInfo("Son added to sons list : " + sonAddr);
        info.sonNumber++;
        if(info.sonNumber == 1){
            if(ping != null && !ping.isActive()){
                startSonMonitor();
            }
        }
    }
    
    /**
     * Removes a host from sons list. Decrement the number of son in SPOTInfo instance.
     * Calls {@link TopologyManager#stopPingMonitor()} when there are no sons remaining in the list.
     *
     * @param dg The Radiogram with the SPOTInfo fields to extract
     * @param sonAddr The IEEE address of the son we want to remove from the sons list
     */
    public synchronized void removeSon(String sonAddr){
        if(sons.remove(sonAddr)){
            sensorManager.removeTemperature(sonAddr);
            logger.logInfo("Son removed : " + sonAddr);
            if(info.sonNumber != 0){
                info.sonNumber--;
                if(info.sonNumber == 0){
                    if(ping != null && ping.isActive()){
                        stopSonMonitor();
                    }
                }
            }
        }
    }
    
    /**
     * Returns true if a father is assigned, false in the other case.
     */
    public boolean fatherAssigned(){
        return (info.father == null);
    }
    
    /**
     * Returns the link state of the SPOT in the tree
     */
    public boolean isAttached(){
        return this.attached;
    }
    
    /**
     * Creates a SPOTInfo instance for a new discovered neighbor/SPOT.
     * Add the instance into {@link TopologyManager#neighbors}
     *
     * @param dg The Radiograms with SPOT information.
     * @param host The IEEE address of the device from wich the informations are coming.
     */
    public void createInfo(Radiogram dg, String host) throws IOException {
        SPOTInfo hostinfo = new SPOTInfo();
        synchronized(neighbors){
            neighbors.put(host, hostinfo);
            RadioUtilities.putInfo(dg, hostinfo);
        }
    }
    
    /**
     * Updates the info of a SPOTInfo class from a received packet.
     * Updates the instance into {@link TopologyManager#neighbors}
     * 
     * @param dg The Radiograms with SPOT information.
     * @param host The IEEE address of the device from wich the informations are coming.
     */
    public void updateInfo(Radiogram dg, String host) throws IOException {
        SPOTInfo hostinfo = (SPOTInfo)neighbors.get(host);
        synchronized(hostinfo){
            RadioUtilities.putInfo(dg, hostinfo);
        }
    }
    
    /**
     * Ping sons every 60s.
     * Elements are removed from all the lists if we cannot contact them.
     */
    public void doPing(){
        if(!sons.isEmpty()){
            synchronized(sons){
                for(String son : sons){
                    try {
                        transmitter.send(UNICAST, PING, info, son);
                    } catch (IOException exc) {
                        System.out.println("Cannot contact son with address : " + son);
                        logger.logInfo("Cannot contact son with address : " + son);
                        removeSon(son);
                        exc.printStackTrace(); // debug
                    }
                }
            }
        }
    }
    
    /**
     * Start monitoring sons through periodic PING requests (60s).
     */
    public void startSonMonitor(){
        ping = new Task(60 * 1000){
            @Override
            public void doTask() {
                doPing();
            }
        };
        ping.start();
    }
    
    /**
     * Stop monitoring sons. 
     * Used when the sons list becomes empty.
     */
    public void stopSonMonitor(){
        if(ping != null && ping.isActive()){
            ping.stop();
        }
    }
    
    /**
     * HELLO Broadcast at start of network building
     */
    public void sendBroadcast(){
        try {
            transmitter.send(BROADCAST, HELLO, info, null);
        } catch (IOException ex) {
            Logger.getLogger(TopologyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setThreshold(double threshold){
        if(info != null)
            info.threshold = threshold;
    }
}
