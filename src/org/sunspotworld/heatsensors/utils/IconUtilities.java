package org.sunspotworld.heatsensors.utils;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class IconUtilities {
	
    public static ImageIcon getIcon(String name){
        URL urlImage = ClassLoader.getSystemResource(
            "org/sunspotworld/heatsensors/icon/" + name + ".png"
        );

        ImageIcon img = new ImageIcon(urlImage);

        return new ImageIcon(
            img.getImage().getScaledInstance(16, 16, Image.SCALE_AREA_AVERAGING)
        );
    }

}
