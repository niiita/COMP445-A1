import java.io.Console;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;  



public class Httpc {
	
	private static Boolean patternCheck = false;
	private final static String httpMethodGet = "GET";
	private final static String httpMethodPost = "POST"; 
	private final static String fileOption = "-f";
	private final static String dataOption = "-d";

	public static void main(String[] args) {
		
		String value;
		Console console = System.console();
		if(console == null)
		{
			System.out.println("No console available");
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}
		
		while(patternCheck != true) {
			value = console.readLine("Enter string (0 to exit the application): ");
			
			//Exit if the value entered is 0
			if(value.equals("0"))
			{
				System.exit(0);
			}
			
			Pattern pattern = Pattern.compile("httpc(\\s+(get|post))(\\s+-v)?(\\s+-h\\s.+:.+)?(\\s+-d\\s+'.+')?(\\s+-f\\s+.+)?(\\s+'.+'*)");	
			
			// Now create matcher object.
			Matcher m = pattern.matcher(value); 
			
			if(m.find()) {

				patternCheck = true;
				/*
				 * Group 2: Get or Post					m.group(2)
				 * Group 3: verbose -v					m.group(3)
				 * Group 4: data -d						m.group(4)
				 * Group 5: header -h					m.group(5)
				 * Group 6: file -f						m.group(6)
				 * Group 7: URL							m.group(7)
				*/
				
				String type = m.group(2).toUpperCase();
				String host = m.group(7).replaceAll("'", "").trim();
				
				//Check if -d
				Boolean isData = m.group(4) != null? true:false;
				//Check if -f
				Boolean isFile = m.group(6) != null? true: false;
				
				
				if(type.equals(httpMethodGet) && (isData || isFile)){
					 System.out.println("The GET request cannot be combined with the -f or -d options.");
					 patternCheck = false;
					 continue;
				}
				
				if(type.equals(httpMethodPost) && isData && isFile){
					System.out.println("The POST request cannot be combined with the -f and the -d options.");
					patternCheck = false;
					continue;
				}
				
				httpc("/status/418", host, type, null);
			}
			else {
				 System.out.println("The input was incorrect. Please try again. Enter '0' to exit");
			}
		}
	}
	
	public static void httpc(String path, String host, String type, String query) {
		try {
            if(host == null){
                Socket socket = new Socket("google.com", 80);
                InputStream inputStream = socket.getInputStream();
			    OutputStream outputStream = socket.getOutputStream();
			
			    String request = "GET / HTTP/1.0\r\nHost: www.google.com\r\n\r\n";
			
                outputStream.write(request.getBytes());
                outputStream.flush();
                
                StringBuilder response = new StringBuilder();
                
                int data = inputStream.read();
                
                while(data != -1) {
                    response.append((char) data);
                    data = inputStream.read();
                }
                
                System.out.println("redirect response: " + response);
                socket.close();

            }else{

			Socket socket = new Socket(host, 80);
			InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            
            System.out.println("aver");
            System.out.println(path);
            System.out.println(host);
            System.out.println(query);

            String request = "";
            if(type.equals("GET")){
                if(query == null) {
                    request = "GET " + path + " HTTP/1.0\r\n\r\n";
                } else {
                    request = "GET " + path + "?" +query +" HTTP/1.0\r\n\r\n";
                }
            }else if(type.equals("POST")){
                //NOT COMPLETE - must be able to output json from .txt file (?)
                String body = "key1=value1&key2=value2";
                request = "POST /post HTTP/1.0\r\n"
							+ "Content-Type:application/x-www-form-urlencoded\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;
            }
			

			//http://postman-echo.com:100/wikypedia/article/1?key=value
			
			outputStream.write(request.getBytes());
			outputStream.flush();
			
			StringBuilder response = new StringBuilder();
			
			int data = inputStream.read();
			
			while(data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}
			
			System.out.println("request: " + request);
			System.out.println("response: " + response);
            socket.close();
        }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
}