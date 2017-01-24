/**
 * 
 */
package com.idyria.osi.tea.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Scanner;


/**
 * @author rtek
 *
 */
public class TeaIOUtils {

	/**
	 * Buffer size used in I/O Operations
	 */
	public static Integer BUFF_SIZE = 2048;
	
	/**
	 * 
	 */
	public TeaIOUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static String waitForNewLineConsoleInput() {
		// get scanner
		Scanner scan = new Scanner(System.in);
		return scan.nextLine();
		
	}
	
	public static String waitForNewCharacterConsoleInput() {
		// get scanner
		Scanner scan = new Scanner(System.in);
		return scan.next();
		
	}
	
	
	/**
	 * Returns a byte array containing the totality of the InputStream.
	 * @param is The available size should return the complete size of the stream
	 * @return null if stream is not readable
	 */
	public static byte[] swallow(InputStream is) {
		
		
		try {
			// Create
			byte[] res = new byte[is.available()];
			int buffsize = BUFF_SIZE;
			byte[] buff = new byte[buffsize];
			int sizeRead = 0;
			int position = 0;
			// Swallow
			while ( (sizeRead = is.read(buff))!=-1  ) {
				
				// Copy
				System.arraycopy(buff, 0, res, position, sizeRead);
				position+=sizeRead;
				
			}
			return res;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	/**
	 * Returns a byte array containing the totality of the InputStream.
	 * @param is The Output byte array will be extended
	 * @return if stream is not readable
	 */
	public static byte[] swallowStream(InputStream is) {
		
		try {
			// Create
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int buffsize = BUFF_SIZE;
			byte[] buff = new byte[buffsize];
			int sizeRead = 0;
			int position = 0;
			// Swallow
			while ( (sizeRead = is.read(buff))!=-1  ) {
				
				// Copy
				os.write(buff,0,sizeRead);
				position+=sizeRead;
				
			}
			is.close();
			return os.toByteArray();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	
	
	
	/**
	 * Extract the specified resource to temp directory.
	 * Uses Thread.currentThread().getContextClassLoader() to extract resourcePath
	 * WARNING: Goes through RAM first. Don't use on big datas
	 * @param applicationPath
	 * @return null if no resource path or not succeeded
	 */
	public static File extractToTemp(String resourcePath) {
		
		// In/out
		File res = null;
		File temp = null;
		File resourcePathFile = new File(resourcePath);
		
		// Find IO Dir
		String tempdir = System.getProperty("java.io.tmpdir");
		if (tempdir!=null) {
			temp = new File(tempdir);
			if (!temp.isDirectory())
				temp=null;
		}
		if (tempdir==null)
			return null;
		// Load resource
		URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
		if (resource!=null) {
			// Found, open stream
			try {
				InputStream is = resource.openStream();
				
				// Swallow
				byte[] inBytes = TeaIOUtils.swallow(is);
				
				// Create output file
				res = new File(temp.getAbsolutePath()+File.separator+resourcePathFile.getName());
				res.deleteOnExit();
				
				// Write on it
				FileOutputStream os = new FileOutputStream(res);
				os.write(inBytes);
				os.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return res;
	}

	/**
	 * Writes the provided content string complete to target file using file Writer
	 * @param file
	 * @param content
	 * @throws IOException 
	 */
	public static void writeToFile(File file , String content) throws IOException {
		TeaIOUtils.writeToFile(file, content, true);
	}
	
	public static void writeToFile(File file , String content,boolean overwrite) throws IOException {
		
		//-- If exists and nor overwrite, don'T do
		if (file.exists() && !overwrite) {
			
		} else {
		
			//-- Write
			FileWriter out = new FileWriter(file);
			out.write(content);
			out.close();
		
		}
	}
	
	/**
	 * Writes the provided input stream bufferto target file using nio mapping
	 * @param file
	 * @param content
	 * @throws IOException 
	 */
	public static void writeToFile(File file , InputStream content) throws IOException {

		
		//-- Use Java 1.7 Nio
		Files.copy(content, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
		
		/*
		//-- Source Channel
		ReadableByteChannel contentChannel = Channels.newChannel(content);
		
		//-- Write output
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().write(contentChannel.)
		*/
		
	}
	
	
}
