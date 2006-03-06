/*
 * Created on Jun 22, 2004
 */
package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This class displays a fixed image on the centre of the screen.
 * 
 * @author Michael Wood
 */
public class Splash {
    private Shell shell = null;

    private GC gc = null;

    private Image image = null;

    public Splash(Display display) {
        String path = "/images/graphics/splash.gif";
        try {
            shell = new Shell(display, SWT.NO_TRIM);
            ImageData splash_data = new ImageData(getClass()
                    .getResourceAsStream(path));
            image = new Image(display, splash_data);
            shell.setSize(splash_data.width, splash_data.height);
            Rectangle splash_rectangle = display.getBounds();
            shell.setLocation(
                    ((splash_rectangle.width - splash_data.width) / 2),
                    ((splash_rectangle.height - splash_data.height) / 2));
            shell.open();
            GC gc = new GC(shell);
            gc.drawImage(image, 0, 0);
        } catch (Exception e) {
            System.out.println("The splash image [" + path + "] did not load.");
        }
    }

    public void dispose() {
        if (shell != null) {
            shell.dispose();
        }
        if (gc != null) {
            gc.dispose();
        }
        if (image != null) {
            image.dispose();
        }
    }
}
