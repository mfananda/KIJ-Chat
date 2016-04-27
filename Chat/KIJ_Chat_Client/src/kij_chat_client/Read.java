/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kij_chat_client;

/*import java.net.Socket;*/
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import sun.misc.BASE64Decoder;

/**
 *
 * @author santen-suru
 */
public class Read implements Runnable {
        
        private Scanner in;//MAKE SOCKET INSTANCE VARIABLE
        String input;
        
        boolean keepGoing = true;
        ArrayList<String> log;
	
	public Read(Scanner in, ArrayList<String> log)
	{
		this.in = in;
                this.log = log;
	}
    
        @Override
	public void run()//INHERIT THE RUN METHOD FROM THE Runnable INTERFACE
	{
		try
		{
			while (keepGoing)//WHILE THE PROGRAM IS RUNNING
			{						
				if(this.in.hasNext()) {
                                        String input = in.nextLine();
                                        
                                           // input = this.in.nextLine();
                                            String vals[] = input.split(" ");
                                            byte[] decoded = new BASE64Decoder().decodeBuffer(vals[1]);
                                            //System.out.println(cipher);
                                            Date date = new Date();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
                                            String tanggal = dateFormat.format(date);
                                            //System.out.println("Date converted to String: " + strDate);
                                            byte [] key = tanggal.getBytes();
                                            Cipher rc4 = Cipher.getInstance("RC4");
                                            SecretKeySpec rc4Key = new SecretKeySpec(key, "RC4");                                      
                                            Cipher rc4Decrypt = Cipher.getInstance("RC4");
                                            rc4Decrypt.init(Cipher.DECRYPT_MODE, rc4Key);
                                            byte[] clearText2 = rc4Decrypt.update(decoded);
                                            String jadi = new String(clearText2);
                                            System.out.println(jadi);
                                            //System.out.println("decrypted (clear) is " + new String(clearText2, "ASCII"));
                                            //System.out.println(new String(clearText2, "ASCII"));//PRINT IT OUT
                                            if (input.split(" ")[0].toLowerCase().equals("success")) {
                                                if (input.split(" ")[1].toLowerCase().equals("logout")) {
                                                    keepGoing = false;
                                                } else if (input.split(" ")[1].toLowerCase().equals("login")) {
                                                    log.clear();
                                                    log.add("true");
                                                }
                                            
                                        }
                                }
                                
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY WONT BE AN ERROR, GOOD PRACTICE TO CATCH THOUGH
		} 
	}
}
