/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld.heatsensors.utils;

/**
 *
 * @author Alexandre
 */
public class ApplicationSetup {
    
    /** The threshold value diffused in the network. */
    private Double threshold = new Double(0);
    
    public ApplicationSetup(){}
    
    public ApplicationSetup(Double threshold){
        this.threshold = threshold;
    }
    
    public void setThreshold(Double value){
        this.threshold = value;
    }
    
    public Double getThreshold(){
        return this.threshold;
    }
    
}
