/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld.heatsensors;

/**
 *
 * @author Alexandre
 */
public class Temperature {
    /** The date when the temperature was sent. **/
    public long date;
    /** If the value has already been sent. */
    public boolean received;
    /** Temperature value. */
    public Double value;
    /** Number of nodes affected by the temperature value (subtree node number). */
    public int coeff;
    
    public Temperature(){}
    
    public Temperature(long date, double value, int coeff){
        this.date = date;
        this.value = new Double(value);
        this.coeff = coeff;
        received = false;
    }
}
