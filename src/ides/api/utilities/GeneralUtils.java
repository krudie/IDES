package ides.api.utilities;

public class GeneralUtils
{
	/**
	 * Truncates to 10 lines and every line to 250 chars. Ellipses (...) are
	 * appended to denote truncation.
	 * 
	 * @param msg
	 *            original message
	 * @return truncated message
	 */
	public static String truncateMessage(String msg)
	{
		if (msg == null)
		{
			return null;
		}
		String[] lines = msg.split("\n");
		String ret = "";
		for (int i = 0; i < Math.min(10, lines.length); ++i)
		{
			lines[i] = lines[i].substring(0, Math.min(250, lines[i].length()))
					+ (lines[i].length() > 250 ? "..." : "");
			ret += lines[i] + "\n";
		}
		if (lines.length > 10)
		{
			ret += "...";
		}
		return ret;
	}
}
