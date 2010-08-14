package ides.api.plugin.presentation;

/**
 * An interface for presentations that support the ability to change the font
 * size. Such a presentation needs to update the {@link FontSizeSelector} via
 * Hub .getUserInterface().getFontSelector().setFontSize(float fontSize) of any
 * font size for it to be displayed properly, otherwise the default is size 12.
 * 
 * @author Valerie Sugarman
 */
public interface GlobalFontSizePresentation
{
	/**
	 * Sets the font size for the presentation.
	 * 
	 * @param fs
	 *            the font size
	 */
	public void setFontSize(float fs);
}
