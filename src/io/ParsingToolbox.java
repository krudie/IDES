package io;

/**
 * This class contains various methods helpful while parsing.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class ParsingToolbox{

    /**
     * removes the last ".*" of a string.
     * @param s the string that shall have ".*" removed
     * @return the a string without ".*"
     */
    public static String removeFileType(String s){
        if(s == null) return null;
        String[] sa = s.split("\\.");
        String r = sa[0];
        for(int i = 1; i < sa.length - 1; i++){
            r += "." + sa[i];
        }
        return r;
    }
    
    public static String getFileType(String s){
    	if(s==null)
    	{
    		return null;
    	}
    	if(s.indexOf(".")<0)
    	{
    		return "";
    	}
    	return s.substring(s.lastIndexOf(".")+1,s.length());
    }
    

}
