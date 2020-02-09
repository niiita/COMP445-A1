
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;  



public class Httpc {
	
	private static boolean patternCheck = false;
	private final static String HTTP_METHOD_GET = "GET";
	private final static String HTTP_METHOD_POST = "POST"; 
	private final static String FILE_OPTION = "-f";
	private final static String DATA_OPTION = "-d";
	private final static String VERBOSE_OPTION = "-v";
	public final static int DEFAULT_PORT = 80;
	
	private static boolean isVerbose = false;
	private static boolean isData = false;
	private static boolean isFile = false;

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
			
			
			Pattern pattern = Pattern.compile("httpc(\\s+(get|post))(\\s+-v)?(\\s+-h\\s.+:[^\\s]+)?(\\s+-d\\s+'.+')?(\\s+-f\\s+[^\\s]+)?(\\s+'((http[s]?:\\/\\/www\\.|http[s]?:\\/\\/|www\\.)?([^\\/]+)(\\/.+)?)'*)");	
			
			// Now create matcher object.
			Matcher m = pattern.matcher(value); 
			
			if(m.find()) {

				patternCheck = true;
				/*
				 * Group 2: Get or Post				m.group(2)
				 * Group 3: verbose -v				m.group(3)
				 * Group 4: header -h				m.group(4)
				 * Group 5: data -d					m.group(5)
				 * Group 6: file -f					m.group(6)
				 * Group 10: Host					m.group(10)
				 * Group 11: Path					m.group(11)
				*/
				
				String type = m.group(2).toUpperCase();
				String host = m.group(10).replaceAll("'", "").trim();
				String path = "";
				
				if(m.group(11) != null)
				{
					path = m.group(11).replaceAll("'", "").trim();
				}
				
				//Check if -d
				isData = m.group(5) != null? true:false;
				//Check if -f
				isFile = m.group(6) != null? true: false;
				//Check if -v
				isVerbose = m.group(3) != null ? true:false;
				
				//Additional check GET method for cURL
				if(type.equals(HTTP_METHOD_GET) && (isData || isFile)){
					 System.out.println("The GET request cannot be combined with the -f or -d options.");
					 patternCheck = false;
					 continue;
				}
				
				//Additional check on POST method for cURL
				if(type.equals(HTTP_METHOD_POST) && isData && isFile){
					System.out.println("The POST request cannot be combined with the -f and the -d options.");
					patternCheck = false;
					continue;
				}
				
				httpc(path, host, type, null, isData, isFile, isVerbose);
			}
			else {
				 System.out.println("The input was incorrect. Please try again. Enter '0' to exit");
			}
		}
	}
	
	//https://stackoverflow.com/questions/2271800/how-to-read-the-parameters-and-value-from-the-query-string-using-java
	 public static Map<String, String> getQueryMap(String query)  
	 {  
	     String[] params = query.split("&");  
	     Map<String, String> map = new HashMap<String, String>();  
	     for (String param : params)  
	     {  String [] p=param.split("=");
	         String name = p[0];  
	       if(p.length>1)  {String value = p[1];  
	         map.put(name, value);
	       }  
	     }  
	     return map;  
	 } 
	 
	 
	 public static void httpGetRequest(String host, String path) throws Exception{
		 
		try {
			//Initialize the socket
			Socket socket = new Socket(host, DEFAULT_PORT);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
	
			//https://stackoverflow.com/questions/2214308/add-header-in-http-request-in-java
			
			//Define the request
			String request = "";
			if(path == "" || path == null) {
				request = "GET / HTTP/1.0\r\nHost: " + host + "\r\n\r\n";
			}
			else {
				request =  "GET " + path + " HTTP/1.0";
			}
			
			
			 writer.println(request);
			 writer.println("");
			 writer.flush();
			 
			 BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
			 String outStr;
			 String response = "";
			 
			 while((outStr = bufRead.readLine()) != null){
			     response += outStr + "\n";
			 }
			 
			 //Format output as needed
			 formattedOutputResponse(isVerbose, response);
			
			 //Close everything
			 bufRead.close();
			 writer.close();
			 socket.close();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	 }
	 
	 
	 public static void httpPostRequest () {
		 
		 //NOT COMPLETE - must be able to output json from .txt file (?)
         String body = "key1=value1&key2=value2";
         String request = "POST /post HTTP/1.0\r\n"
						+ "Content-Type:application/x-www-form-urlencoded\r\n"
						+ "Content-Length: " + body.length() +"\r\n"
						+ "\r\n"
						+ body;
	 }
	
	
	 public static void httpc(String path, String host, String type, String query, boolean isData, boolean isFile, boolean isVerbose) {
		try {
            if(host == null || host.equals("")){
               host = "google.com";
            }
   
			//https://stackoverflow.com/questions/2214308/add-header-in-http-request-in-java
            if(type.equals("GET")){
            	httpGetRequest(host, path);
            }
            else if(type.equals("POST")){
               httpPostRequest();
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	
	private static void formattedOutputResponse(boolean isVerbose, String response) {
		
		if(isVerbose) {
			System.out.println(response);
		}
		
		else {
			String[] responseFormatted = response.split("\n\n");
	        
			for(int i = 1; i < responseFormatted.length; i++)
				System.out.println(responseFormatted[i]);
		}
	}
	
}