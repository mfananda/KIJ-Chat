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
import javax.crypto.spec.IvParameterSpec;
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
				String encryptedValue =null;
                                String key=null;
                                String command=null;
                                
                                if (input.split(" ")[0].toLowerCase().equals("login") == true) {
                                            String[] vals = input.split(" ");
                                            command = vals[0];
                                            String plain_pass = vals[2];
                                            key = vals[1];      
                                            byte[] plaintext = plain_pass.getBytes("UTF-8");
                                            
                                            String keyData = String.format("%-16s",key).replace(' ','0'); //username+ zeropadding 16 char
                                            byte[] key_fix = keyData.getBytes("UTF-8");
                                            
                                            String plain_IV = new StringBuffer(key).reverse().toString();
                                            //System.out.print(plain_IV);
                                            String ivData = String.format("%-16s",plain_IV).replace(' ','0');//reverse username + zeropadding 16char
                                            byte[] iv = ivData.getBytes("UTF-8");
                                           
                                            
                                            SecretKeySpec skeySpec=new SecretKeySpec(key_fix,"AES");
                                            IvParameterSpec ivSpec = new IvParameterSpec(iv);
                                            Cipher AESCipher = Cipher.getInstance("AES/CFB/NoPadding");
                                            AESCipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
                                            byte[] ciphertext = AESCipher.doFinal(plaintext); 
                                            encryptedValue = new BASE64Encoder().encode(ciphertext); //have to do this to save the byte[]
                                            input = vals[0] + " " + vals[1] + " " + ciphertext ;
                                            //Socket s = new Socket("localhost", 7777);
                                            //DataOutputStream dOut = new DataOutputStream(this.socket.getOutputStream());
                                            //dOut.writeInt(iv.length);
                                            //dOut.write(iv);
//                                            System.out.println(key_fix);
//                                            System.out.println(iv);
//                                            System.out.println(skeySpec);
//                                            System.out.println(ivSpec);
//                                            System.out.println(input);
                                            out.println(command + " " + key + " " + encryptedValue);//SEND IT TO THE SERVER             
                                }
                                else if (input.split(" ")[0].toLowerCase().equals("pm") == true)
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
                                
                                else if(input.split(" ")[0].toLowerCase().equals("gm") == true){
                                    String[] vals=input.split(" ");
                                    command = vals[0];
                                        //String message = vals[1];
                                        //message = String.format("%-16s", vals[1]).replace(' ', '0');
                                        String messageOut = "";
                                        for (int j = 2; j<vals.length; j++) {
                                            messageOut += vals[j] + " ";
                                        }
                                        byte[] plaintext = messageOut.getBytes();
                                        Date date = new Date();
                                        DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
                                        String tanggal = dateFormat.format(date);
                                        String key_coy = String.format("%-16s",tanggal).replace(' ','0');
                                        
                                        //System.out.println("Date converted to String: " + strDate);
                                        byte [] key1 = key_coy.getBytes();

                                        SecretKeySpec skeySpec=new SecretKeySpec(key1,"AES");
                                        Cipher AESCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                                        AESCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                                        byte[] ciphertext = AESCipher.doFinal(plaintext); 
                                        encryptedValue = new BASE64Encoder().encode(ciphertext);
                                        //input = vals[0] + " " + vals[1] + " " + ciphertext ;
                                        //Socket s = new Socket("localhost", 7777);
                                        //DataOutputStream dOut = new DataOutputStream(this.socket.getOutputStream());
                                        //dOut.writeInt(iv.length);
                                        //dOut.write(iv);
//                                            System.out.println(key_fix);
//                                            System.out.println(iv);
//                                            System.out.println(skeySpec);
//                                            System.out.println(ivSpec);
//                                            System.out.println(input);
                                        out.println(command + " " + vals[1] + " " +encryptedValue);//SEND IT TO THE SERVER
                                }
                                else{
                                    command = input;
                                    out.println(command);//SEND IT TO THE SERVER
                                }
                                
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
