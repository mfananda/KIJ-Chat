/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kij_chat_client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import sun.misc.BASE64Encoder;

/**
 *
 * @author santen-suru
 */
public class Write implements Runnable {
    
	private Scanner chat;
        private PrintWriter out;
        boolean keepGoing = true;
        ArrayList<String> log;
	
	public Write(Scanner chat, PrintWriter out, ArrayList<String> log)
	{
		this.chat = chat;
                this.out = out;
                this.log = log;
	}
/*public static void enkrip(String args) throws Exception
   {
       Date date = new Date();
       DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
       String tanggal = dateFormat.format(date);
       //System.out.println("Date converted to String: " + strDate);
        byte [] key = tanggal.getBytes();

      String clearText = args;

      Cipher rc4 = Cipher.getInstance("RC4");
      SecretKeySpec rc4Key = new SecretKeySpec(key, "RC4");
      rc4.init(Cipher.ENCRYPT_MODE, rc4Key);

      byte [] cipherText = rc4.update(clearText.getBytes("ASCII"));

      System.out.println("clear (ascii)        " + clearText);
      System.out.println("clear (hex)          " + DatatypeConverter.printHexBinary(clearText.getBytes()));
      System.out.println("cipher (hex) is      " + DatatypeConverter.printHexBinary(cipherText));

      Cipher rc4Decrypt = Cipher.getInstance("RC4");
      rc4Decrypt.init(Cipher.DECRYPT_MODE, rc4Key);
      byte [] clearText2 = rc4Decrypt.update(cipherText);

      System.out.println("decrypted (clear) is " + new String(clearText2, "ASCII"));
   }
*/	
	@Override
	public void run()//INHERIT THE RUN METHOD FROM THE Runnable INTERFACE
	{
		try
		{
			while (keepGoing)//WHILE THE PROGRAM IS RUNNING
			{						
				String input = chat.nextLine();	//SET NEW VARIABLE input TO THE VALUE OF WHAT THE CLIENT TYPED IN
                                if (input.split(" ")[0].toLowerCase().equals("pm") == true)
                                {
                                    String huruf="";
                                     String[] pisah = input.split(" ");
                                     for (int j = 2; j<pisah.length; j++) {
                                             huruf+= pisah[j] + " "; //ngambil kata-katanya             
                                         }
                                     //enkrip(huruf);
                                   System.out.println(huruf);
                                     
                                   Date date = new Date();
                                     DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
                                     String tanggal = dateFormat.format(date);
                                     //System.out.println("Date converted to String: " + strDate);
                                     byte [] key = tanggal.getBytes();
                                     Cipher rc4 = Cipher.getInstance("RC4");
                                     SecretKeySpec rc4Key = new SecretKeySpec(key, "RC4");
                                     rc4.init(Cipher.ENCRYPT_MODE, rc4Key);

                                     byte [] cipherText = rc4.update(huruf.getBytes());
                                     Cipher rc4Decrypt = Cipher.getInstance("RC4");
                                     rc4Decrypt.init(Cipher.DECRYPT_MODE, rc4Key);
                                     byte [] clearText2 = rc4Decrypt.update(cipherText);
                                     System.out.println("decrypted (clear) is " + new String(clearText2, "ASCII"));
                                     String encoded = new BASE64Encoder().encode(cipherText);
                                     input = pisah[0] + " " + pisah[1] + " " + encoded;
                                }
				out.println(input);//SEND IT TO THE SERVER
				out.flush();//FLUSH THE STREAM
                                
                                
                                
                                if (input.contains("logout")) {
                                    if (log.contains("true"))
                                        keepGoing = false;
                                    
                                }
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY WONT BE AN ERROR, GOOD PRACTICE TO CATCH THOUGH
		} 
	}

}
