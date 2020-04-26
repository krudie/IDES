package services.latex;

import ides.api.core.Hub;

public class LatexMessages {
    /**
     * Display a notice that LaTeX won't render font sizes larger than 25
     */
    public static void fontSizeTooBig() {
        Hub.getNoticeManager().postWarningTemporary(Hub.string("errorLatexFontSizeDigest"),
                Hub.string("errorLatexFontSizeFull"));
    }
}
