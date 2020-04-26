/**
 * 
 */
package ides.api.latex;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import ides.api.core.Hub;

/**
 * Provides some methods useful when rendering LaTeX elements.
 * 
 * @author Lenko Grigorov
 */
public class LatexUtils {

    /**
     * Prevent instantiation.
     */
    private LatexUtils() {
    }

    /**
     * Renders a string into a PNG image.
     * 
     * @param s the string to be rendered
     * @return an array of bytes containing the PNG image
     * @throws LatexRenderException when LaTeX rendering fails
     */
    public static byte[] labelStringToImageBytes(String s) throws LatexRenderException {
        try {
            BufferedImage image = null;
            if (s == null || s.equals("")) {
                image = Renderer.getEmptyImage();
            } else {
                image = Hub.getLatexManager().getRenderer().renderString(s);
            }
            // convert to RGB+alpha
            ColorConvertOp conv = new ColorConvertOp(image.getColorModel().getColorSpace(),
                    ColorModel.getRGBdefault().getColorSpace(), null);
            BufferedImage rendered = conv.createCompatibleDestImage(image, ColorModel.getRGBdefault());
            conv.filter(image, rendered);
            // adjust the transparency of the label
            WritableRaster raster = rendered.getAlphaRaster();
            for (int i = 0; i < raster.getWidth(); ++i) {
                for (int j = 0; j < raster.getHeight(); ++j) {
                    if (rendered.getRaster().getSample(i, j, 0) >= 253) {
                        raster.setSample(i, j, 0, 0);
                    } else {
                        raster.setSample(i, j, 0, 255);
                    }
                }
            }
            // save image in byte array as PNG
            ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
            ImageIO.write(rendered, "png", pngStream);
            pngStream.close();
            return pngStream.toByteArray();
        } catch (IOException ex) {
            throw new LatexRenderException(ex);
        }
    }

}
