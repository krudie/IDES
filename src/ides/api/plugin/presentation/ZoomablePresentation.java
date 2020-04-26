package ides.api.plugin.presentation;

/**
 * An interface for {@link Presentation}s that support the ability to zoom. Such
 * a presentation may also update the scaling factor via
 * Hub.getUserInterface().getUserInterface().getZoomControl().setZoom(float
 * scaleFactor), e.g., to restore a saved zoom level.
 * 
 * @author Valerie Sugarman
 */
public interface ZoomablePresentation extends Presentation {
    /**
     * Sets the scale factor for the presentation.
     * 
     * @param sf the scale factor
     */
    public void setScaleFactor(float sf);
}
