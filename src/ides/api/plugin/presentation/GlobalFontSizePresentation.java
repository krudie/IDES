package ides.api.plugin.presentation;

/**
 * An interface for {@link Presentation}s that support the ability to change the
 * font size. Such a presentation may also update the font size via
 * Hub.getUserInterface().getFontSelector().setFontSize(float fontSize), e.g.,
 * to restore a saved font size.
 * 
 * @author Valerie Sugarman
 */
public interface GlobalFontSizePresentation extends Presentation {
    /**
     * Sets the font size for the presentation.
     * 
     * @param fs the font size
     */
    public void setFontSize(float fs);
}
