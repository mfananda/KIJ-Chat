package kij_chat_server;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

/** original ->http://www.dreamincode.net/forums/topic/262304-simple-client-and-server-chat-program/
 * 
 * @author santen-suru
 */


public class Client implements Runnable{

	private Socket socket;//SOCKET INSTANCE VARIABLE
        private String username;
        private boolean login = false;
        
        private ArrayList<Pair<Socket,String>> _loginlist;
        private ArrayList<Pair<String,String>> _userlist;
        private ArrayList<Pair<String,String>> _grouplist;
	
	public Client(Socket s, ArrayList<Pair<Socket,String>> _loginlist, ArrayList<Pair<String,String>> _userlist, ArrayList<Pair<String,String>> _grouplist)
	{
		socket = s;//INSTANTIATE THE SOCKET)
                this._loginlist = _loginlist;
                this._userlist = _userlist;
                this._grouplist = _grouplist;
	}
	
	@Override
	public void run() //(IMPLEMENTED FROM THE RUNNABLE INTERFACE)
	{
		try //HAVE TO HAVE THIS FOR THE in AND out VARIABLES
		{
			Scanner in = new Scanner(socket.getInputStream());//GET THE SOCKETS INPUT STREAM (THE STREAM THAT YOU WILL GET WHAT THEY TYPE FROM)
			PrintWriter out = new PrintWriter(socket.getOutputStream());//GET THE SOCKETS OUTPUT STREAM (THE STREAM YOU WILL SEND INFORMATION TO THEM FROM)
			
			while (true)//WHILE THE PROGRAM IS RUNNING
			{		
				if (in.hasNext())
				{
					String input = in.nextLine();//IF THERE IS INPUT THEN MAKE A NEW VARIABLE input AND READ WHAT THEY TYPED
//					System.out.println("Client Said: " + input);//PRINT IT OUT TO THE SCREEN
//					out.println("You Said: " + input);//RESEND IT TO THE CLIENT
//					out.flush();//FLUSH THE STREAM
                                        if (input.split(" ")[0].toLowerCase().equals("login") == true) {
                                            String[] vals = input.split(" ");
                                            
                                            //System.out.print("coba1");
                                            //System.out.print(this.socket);
                                            //Socket s = new Socket("localhost", 7777);
                                            
                                            //System.out.print("coba2");
                                            String key = vals[1];
                                            byte[] pass = vals[2].getBytes("UTF-8");
                                            
                                            String keyData = String.format("%-16s",key).replace(' ','0');//username+ zeropadding 16 char
                                            byte[] key_fix = keyData.getBytes("UTF-8");
                                            
                                            String plain_IV = new StringBuffer(key).reverse().toString();
                                            //System.out.print(plain_IV);
                                            String ivData = String.format("%-16s",plain_IV).replace(' ','0');//reverse username + zeropadding 16 char
                                            byte[] iv = ivData.getBytes("UTF-8");
                                            
                                            SecretKeySpec skeySpec=new SecretKeySpec(key_fix,"AES");
                                            IvParameterSpec ivSpec = new IvParameterSpec(iv);
                                            Cipher AESCipher = Cipher.getInstance("AES/CFB/NoPadding");
                                            AESCipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
                                            decordedValue = new BASE64Decoder().decodeBuffer(vals[2]);
                                            byte[] decrypt = AESCipher.doFinal(decordedValue); //have to do this to save the byte[]
                                            String decryptedValue = new String(decrypt);
                                            
//                                            System.out.println(key_fix);
//                                            System.out.println(iv);
//                                            System.out.println(skeySpec);
//                                            System.out.println(ivSpec);
//                                            System.out.println(decryptedValue);
                                            //System.out.println(decrypt.toString());
                                        // param LOGIN <userName> <pass>
                                        if (this._userlist.contains(new Pair(vals[1], decryptedValue)) == true) { //edit bug
                                                if (this.login == false) {
                                                    this._loginlist.add(new Pair(this.socket, vals[1]));
                                                    this.username = vals[1];
                                                    this.login = true;
                                                    System.out.println("Users count: " + this._loginlist.size());
                                                    //System.out.println(this.username);
                                                    out.println("SUCCESS login");
                                                    out.flush();
                                                } else {
                                                    out.println("FAIL login");
                                                    out.flush();
                                                }
                                            } else {
                                                out.println("FAIL login");
                                                out.flush();
                                            }
                                        }
                                        
                                        // param LOGOUT
                                        if (input.split(" ")[0].toLowerCase().equals("logout") == true) {
                                            String[] vals = input.split(" ");
                                            
                                            if (this._loginlist.contains(new Pair(this.socket, this.username)) == true) {
                                                this._loginlist.remove(new Pair(this.socket, this.username));
                                                System.out.println(this._loginlist.size());
                                                out.println("SUCCESS logout");
                                                out.flush();
                                                this.socket.close();
                                                break;
                                            } else {
                                                out.println("FAIL logout");
                                                out.flush();
                                            }
                                        }
                                        
                                        // param PM <userName dst> <message>
                                        if (input.split(" ")[0].toLowerCase().equals("pm") == true) {
                                            String[] vals = input.split(" ");
                                            
                                            boolean exist = false;
                                            
                                            for(Pair<Socket, String> cur : _loginlist) {
                                                if (cur.getSecond().equals(vals[1])) {
                                                    PrintWriter outDest = new PrintWriter(cur.getFirst().getOutputStream());
                                                    String messageOut = "";
                                                    for (int j = 2; j<vals.length; j++) {
                                                        messageOut += vals[j] + " ";
                                                    }
                                                    System.out.println(this.username + " to " + vals[1] + " : " + messageOut);
                                                    outDest.println(this.username + ": " + messageOut);
                                                    outDest.flush();
                                                    exist = true;
                                                }
                                            }
                                            
                                            if (exist == false) {
                                                System.out.println("pm to " + vals[1] + " by " + this.username + " failed.");
                                                out.println("FAIL pm");
                                                out.flush();
                                            }
                                        }
                                        
                                        // param CG <groupName>
                                        if (input.split(" ")[0].toLowerCase().equals("cg") == true) {
                                            String[] vals = input.split(" ");
                                            
                                            boolean exist = false;
                                            
                                            for(Pair<String, String> selGroup : _grouplist) {
                                                if (selGroup.getFirst().equals(vals[1])) {
                                                    exist = true;
                                                }
                                            }
                                            
                                            if(exist == false) {
                                                Group group = new Group();
                                                int total = group.updateGroup(vals[1], this.username, _grouplist);
                                                System.out.println("total group: " + total);
                                                System.out.println("cg " + vals[1] + " by " + this.username + " successed.");
                                                out.println("SUCCESS cg");
                                                out.flush();
                                            } else {
                                                System.out.println("cg " + vals[1] + " by " + this.username + " failed.");
                                                out.println("FAIL cg");
                                                out.flush();
                                            }
                                        }
                                        
                                        // param GM <groupName> <message>
                                        if (input.split(" ")[0].toLowerCase().equals("gm") == true) {
                                            String[] vals = input.split(" ");
                                            
                                            String messageOut = "";
                                            for (int j = 2; j<vals.length; j++) {
                                                messageOut += vals[j] + " ";
                                            }
                                                
                                            Date date = new Date();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
                                            String tanggal = dateFormat.format(date);
                                            String key_coy = String.format("%-16s",tanggal).replace(' ','0');
                                            byte [] key1 = key_coy.getBytes();
                                            //System.out.print(key_coy);
                                            
                                            SecretKeySpec skeySpec=new SecretKeySpec(key1,"AES");
                                            Cipher AESCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                                            AESCipher.init(Cipher.DECRYPT_MODE, skeySpec);
                                            decordedValue = new BASE64Decoder().decodeBuffer(messageOut);
                                            byte[] decrypt = AESCipher.doFinal(decordedValue);
                                            String decryptedValue = new String(decrypt);
                                            
                                            //System.out.println(decryptedValue);
                                            
                                            
                                            boolean exist = false;
                                            
                                            for(Pair<String, String> selGroup : _grouplist) {
                                                if (selGroup.getSecond().equals(vals[1])) {
                                                    exist = true;
                                                }
                                            }
                                            
                                            if (exist == true) {
                                                System.out.println(this.username + " to " + vals[1] + " group: " + decryptedValue);
                                                for(Pair<String, String> selGroup : _grouplist) {
                                                    if (selGroup.getFirst().equals(vals[1])) {
                                                        
                                                        for(Pair<Socket, String> cur : _loginlist) {
                                                            if (cur.getSecond().equals(selGroup.getSecond())) {
                                                                
//                                                                String messageOut = "";
//                                                                for (int j = 2; j<vals.length; j++) {
//                                                                    messageOut += vals[j] + " ";
//                                                                }
                                                                PrintWriter outDest = new PrintWriter(cur.getFirst().getOutputStream());
                                                                outDest.println(this.username + " @ " + vals[1] + " group: " + decryptedValue);
                                                                outDest.flush();
                                                                
                                                                
                                                            }
                                                              
                                                        }
                                                    }
                                                }
                                            } else {
                                                System.out.println("gm to " + vals[1] + " by " + this.username + " failed.");
                                                out.println("FAIL gm");
                                                out.flush();
                                            }
                                        }
                                        
                                        // param BM <message>
                                        if (input.split(" ")[0].toLowerCase().equals("bm") == true) {
                                            String[] vals = input.split(" ");
                                            
                                            for(Pair<Socket, String> cur : _loginlist) {
                                                if (!cur.getFirst().equals(socket)) {
                                                    PrintWriter outDest = new PrintWriter(cur.getFirst().getOutputStream());
                                                    String messageOut = "";
                                                    for (int j = 1; j<vals.length; j++) {
                                                        messageOut += vals[j] + " ";
                                                    }
                                                    System.out.println(this.username + " to alls: " + messageOut);
                                                    outDest.println(this.username + " <BROADCAST>: " + messageOut);
                                                    outDest.flush();
                                                }
                                            }
                                        }
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY THERE WONT BE AN ERROR BUT ITS GOOD TO CATCH
		}	
	}

}


