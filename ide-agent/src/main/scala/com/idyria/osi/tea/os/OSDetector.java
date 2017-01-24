/**
 * 
 */
package com.idyria.osi.tea.os;

/**
 * @author rtek
 *
 */
public class OSDetector {
 
	/**
	 * 
	 */
	public OSDetector() {
		// TODO Auto-generated constructor stub
	}
	
	public static enum OS {
		UNKNOWN,LINUX,WINXP,WINXP_64,VISTA,VISTA_64,WIN7,WIN8,WIN10
	}
	
	
	public static OS getOS() {
		String osname = System.getProperty("os.name");
		if (osname.contains("Windows Vista"))
			return OS.VISTA;
		else if (osname.contains("Windows XP"))
			return OS.WINXP;
		else if (osname.contains("Windows 7"))
			return OS.WIN7;
		else if (osname.contains("Windows 8"))
			return OS.WIN8;
		else if (osname.contains("Windows 10"))
			return OS.WIN10;
		else if (osname.contains("Linux"))
			return OS.LINUX;
		else
			return OS.UNKNOWN;
	}
	
	public static boolean  isWindows() {
		String osname = System.getProperty("os.name");
		if (osname.contains("Windows")) {
			return true;
		} else {
			return false;
		}
	}

}
