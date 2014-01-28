/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld.heatsensors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.sunspotworld.heatsensors.utils.PacketTransmitter;
import org.sunspotworld.heatsensors.views.TemperatureMonitor;

/**
 *
 * @author Alexandre
 */
class SensorManager {
    
    private Map<String, Temperature> temperatures;  // The temperatures of sons. <String, Temperature>
    TopologyManager topology;                       // Used for the fault tolerance
    TemperatureMonitor logger;                      // Logging Window
    PacketTransmitter transmitter;                  // Transmitter to send data to other SPOTs
    
    /**
     * Constructor.
     */
    public SensorManager(TopologyManager manager, TemperatureMonitor monitor, PacketTransmitter transmitter){
        this.topology = manager;
        this.transmitter = transmitter;
        this.logger = monitor;
        temperatures = Collections.synchronizedMap(new HashMap<String, Temperature>());
    }
    
    /**
     * Add an entry to {@link SensorManager#temperatures}
     * @param host The new son to add for the aggregation process
     * @param temperature Temperature information of this SPOT
     */
    public void putTemperature(String host, Temperature temperature){
        synchronized(temperature){
            temperatures.put(host, temperature);
        }
        if(temperature.value != null){
            logger.logInfo(
                "Data received from host : "
                + host
                + " [Value = " + temperature.value + "]"
                + " [Coefficient = " + temperature.coeff + "]"
            );
            temperature.received = true;
            checkReceivedTemperature();
        }
    }
    
    /**
     * Removes an entry from {@link SensorManager#temperatures}
     * @param host The son to remove
     */
    public void removeTemperature(String host){
        synchronized(temperatures){
            temperatures.remove(host);
        }
    }
    
    /**
     * Check the number of temperature messages received.
     */
    public void checkReceivedTemperature(){
        synchronized(temperatures){
            double avgTemp = 0;
            int coeff = 0;
            int i = 1;
            for(Temperature temp : temperatures.values()){
                if(!temp.received){
                    return;
                }
            }
            for(Temperature temp : temperatures.values()){
                System.out.println(
                        "[AGGR] "
                        + "Temperature " + i
                        + " [Temp = " + temp.value * temp.coeff + "]" 
                        + " [Coeff = " + temp.coeff + "]"
                );
                avgTemp += (temp.value * temp.coeff);
                System.out.println(
                        "[AGGR] "
                        + "Temperature " + i
                        + " [AvgTotal = " + avgTemp + "]"
                );
                coeff += temp.coeff;
                System.out.println(
                        "[AGGR] "
                        + "Temperature " + i
                        + " [CoeffTotal = " + coeff + "]"
                );
                i++;
            }
            System.out.println(
                        "[AGGR] "
                        + "Final Average "
                        + " [Avg = " + avgTemp + "]" 
            );
            avgTemp = avgTemp / coeff;
            System.out.println(
                        "[AGGR] "
                        + "Final Value "
                        + " [Avg = " + avgTemp + "]" 
                        + " [Coeff = " + coeff + "]"
            );
            addToGraph(avgTemp);
            reset();
        }
    }
    
    /**
     * Add a value to the graph {@link TemperatureMonitor#contentGraph}
     * @param value The value to add
     */
    public void addToGraph(double value){
        logger.logInfo(
                "New data added to temperature graph : "
                + "[Value = " + value + "]"
        );
        logger.getDataPanel().addData(System.currentTimeMillis(), (int) value);
    }
    
    /**
     * Places the {@link Temperature#received} at 'false' for all sons entries.
     */
    public void reset(){
        for(Temperature temp : temperatures.values()){
            temp.received = false;
        }
    }
}
