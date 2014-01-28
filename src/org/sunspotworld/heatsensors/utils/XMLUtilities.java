package org.sunspotworld.heatsensors.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public abstract class XMLUtilities {
	
    /**
     * Serializes an object into an xml file
     * 
     * @param object object to serialize
     * @param fileName path to file
     */
    public static void encodeToFile(Object object, String fileName) throws FileNotFoundException, IOException {
        XMLEncoder encoder = new XMLEncoder(new FileOutputStream(fileName));
        try {
            encoder.writeObject(object);
            encoder.flush();
        } finally {
            encoder.close();
        }
    }
    
    /**
     * Deserializes an object into from xml file
     * 
     * @param fileName path to ffile
     */
    public static Object decodeFromFile(String fileName) throws FileNotFoundException, IOException {
        Object object = null;
        XMLDecoder decoder = new XMLDecoder(new FileInputStream(fileName));
        try {
            object = decoder.readObject();
        } finally {
            decoder.close();
        }
        return object;
    }
    
    /**
    * Get the configuration of the application
    *
    * @throws IOException Thrown when an unexpected IO error occurs from the deserialization process
    */
    public static ApplicationSetup getSetup() throws Exception {
        String separator = System.getProperty("file.separator");
        File dir = new File(
                System.getProperty("user.home")
                + separator
                + "sensorsnetwork"
                + separator
        );
        File xml = new File(
                System.getProperty("user.home")
                + separator
                + "sensorsnetwork"
                + separator
                + "setup.xml"
        );

        ApplicationSetup setup;

        // Creates the directory
        if(!dir.exists())
                dir.mkdirs();
        
        try {
            return setup = (ApplicationSetup)XMLUtilities.decodeFromFile(
                    System.getProperty("user.home")
                    + separator
                    + "sensorsnetwork"
                    + separator
                    + "setup.xml"
            );
        } catch(FileNotFoundException e) {
            // Creates the file if it does not exist already
            try {
                xml.createNewFile();
            } catch (IOException ex) {
                throw new IOException();
            } catch (Exception exc) {
                throw new Exception();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
   /**
    * Saves the configuration of the application into an XML file
    *
    * @throws IOException Thrown when an unexpected IO error occurs from the deserialization process
    */
    public static void writeSetup(ApplicationSetup setup) throws IOException {
        String separator = System.getProperty("file.separator");
        
        try {
            XMLUtilities.encodeToFile(
                    setup,
                    System.getProperty("user.home")
                    + separator
                    + "sensorsnetwork"
                    + separator
                    + "setup.xml"
            );
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
