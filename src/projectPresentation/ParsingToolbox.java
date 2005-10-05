/**
 * 
 */
package projectPresentation;

/**
 * @author agmi02
 *
 */
public class ParsingToolbox {

    public static String removeFileType(String s){
        if(s == null) return null;
        String[] sa = s.split("\\.");
        String r = sa[0];
        for(int i = 1; i < sa.length-1; i++){
            r += "."+sa[i];
        }
        return r;
    }

}
