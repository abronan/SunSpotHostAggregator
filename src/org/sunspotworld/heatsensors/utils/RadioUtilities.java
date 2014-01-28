/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld.heatsensors.utils;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import java.io.EOFException;
import java.io.IOException;
import org.sunspotworld.heatsensors.PacketTypes;
import org.sunspotworld.heatsensors.SPOTInfo;

/**
 *
 * @author Alexandre
 */
public class RadioUtilities implements PacketTypes {
    
    /**
     * Write the common header into a Radiogram.
     *
     * @param rdg the Radiogram to write the header info into
     * @param type the type of data packet to send
     */
    public static void writeHeader(Radiogram rdg, byte type) {
        try {
            rdg.reset();
            rdg.writeByte(type);
        } catch (IOException ex) {
            System.out.println("Error writing header: " + ex);
        }
    }
    
    /**
     * Construct a Radiogram with the SPOTInfo fields for the topology discovery.
     *
     * @param dg The Radiogram to construct.
     * @param type The type of the packet to send.
     * @param ourInfo The SPOT info fields used to fill the Datagram
     */
    public static void fillInfoRadiogram(Radiogram dg, byte type, SPOTInfo ourInfo){
        try {
            RadioUtilities.writeHeader(dg, type);
            dg.writeDouble(ourInfo.date);
            dg.writeByte(ourInfo.nodetype);
            if(ourInfo.father != null)
                dg.writeUTF(ourInfo.father);
            else
                dg.writeUTF("");
            dg.writeInt(ourInfo.sonNumber);
            dg.writeInt(ourInfo.hops);
            dg.writeDouble(ourInfo.threshold);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Construct a Radiogram with temperature information.
     *
     * @param dg The Radiogram to construct.
     */
    public static void fillTemperatureRadiogram(Radiogram dg, double temperature, int coefficient){
        try {
            RadioUtilities.writeHeader(dg, TEMP);
            /** Write the date. **/
            dg.writeLong(System.currentTimeMillis());
            /** Writing the temperature in Celsius. **/
            dg.writeDouble(temperature);
            /** The coefficient representing nodes in the subtree + self. **/
            dg.writeInt(coefficient);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Put the info of a Radiogram in a SPOTInfo class.
     * Only if the date of the recept is more recent than the last recept.
     *
     * @param dg The Radiogram with SPOT information.
     * @param info Structure that takes the informations.
     */
    public static void putInfo(Radiogram dg, SPOTInfo info) throws EOFException, IOException {
        long date = dg.readLong();
        String fatherTest;
        try{
            if(info.date > date){
                info.date = date;
                info.nodetype = dg.readByte();
                fatherTest = dg.readUTF();
                if(!fatherTest.equals("")){
                    info.father = fatherTest;
                }
                info.sonNumber = dg.readInt();
                info.hops = dg.readInt();
                info.threshold = dg.readDouble();
            }
        } catch(EOFException e){
            e.printStackTrace(); // debug
            throw new EOFException();
        } catch(IOException ex){
            ex.printStackTrace(); // debug
            throw new IOException();
        } 
    }
}