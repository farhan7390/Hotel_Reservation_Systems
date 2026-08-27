package util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AppIcon {

    public static void setFrameIcon(JFrame frame, String resourcePath) {
        try {
            URL iconURL = AppIcon.class.getResource(resourcePath);
            if (iconURL != null) {
                Image icon = new ImageIcon(iconURL).getImage();
                frame.setIconImage(icon);

                if (Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    Taskbar.getTaskbar().setIconImage(icon);
                }
            } else {
                System.err.println("Icon resource not found at: " + resourcePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}