package ides.api.plugin.presentation;

/**
 * An interface for presentations that support the ability to zoom. Such a
 * presentation needs to update the {@link ZoomControl} via Hub
 * .getUserInterface().getZoomControl().setZoom(float scaleFactor) of any saved
 * scale factor for it to be displayed properly, otherwise the default is 1.
 * 
 * @author Valerie Sugarman
 */
public interface ZoomablePresentation
{
	/**
	 * Sets the scale factor for the presentation.
	 * 
	 * @param sf
	 *            the scale factor
	 */
	public void setScaleFactor(float sf);
}
