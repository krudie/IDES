package ides2;

/**
 * @author edlund
 *
 */
public class IDES2 {
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SystemVariables sv = new SystemVariables();
		userinterface.Userinterface gui = new userinterface.Userinterface();
		sv.saveValues();
	}

}
