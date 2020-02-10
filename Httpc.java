import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;



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
    private static boolean isHeader = false;

    private static String dataString = "";
    private static String headerString = "";
	private static File filename;
	

    public static void main(String[] args) {

        String value;
        Console console = System.console();
        if (console == null) {
            System.out.println("No console available");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.exit(0);
        }
        
        System.out.println("COMP 445 - Assignment #1 - Tse-Chun Lau (29676279), Joo Yeon Lee (25612950)\n");
        
        //HTTPC
        while (patternCheck != true) {
        	
        	System.out.println("MAIN MENU");
        	System.out.println("1- Enter \"1\" to submit an httpc request");
        	System.out.println("2- Enter \"2\" to open the help menu");
        	System.out.println("Enter anything else to exit the application");
        	String option = console.readLine();
        	
        	if(option.equals("1")) {

	            value = console.readLine("Enter string (0 to return to the main menu): ");
	       
	            //Exit if the value entered is 0
	            if (value.equals("0")) {
	                continue;
	            }
	
	            //Regex pattern; separate entities grouped within parenthesis
	            Pattern pattern = Pattern.compile("httpc(\\s+(get|post))((\\s+-v)?(\\s+-h\\s+([^\\s]+))?(\\s+-d\\s+('.+'))?(\\s+-f\\s+([^\\s]+))?)(\\s+'((http[s]?:\\/\\/www\\.|http[s]?:\\/\\/|www\\.)?([^\\/]+)(\\/.+)?)'*)");
	
	            // Now create matcher object.
	            Matcher m = pattern.matcher(value);
	
	            if (m.find()) {
	
	                patternCheck = true;
	                /*
	                 * Group 2: Get or Post				m.group(2)
	                 * Group 4: verbose -v				m.group(4)
	                 * Group 5: header -h				m.group(5)
	                 * Group 6: Header content 			m.group(6)
	                 * Group 7: data -d					m.group(7)
	                 * Group 8: Data content			m.group(8)
	                 * Group 9: file -f					m.group(9)
	                 * Group 10: File content			m.group(10)
	                 * Group 12: URL					m.group(12)
	                 * Group 14: Host					m.group(14)
	                 * Group 15: Path					m.group(15)
	                 */
	
	                /* 
	                 * To print out the different groups from Regex
	                for(int i = 0; i < 15; i++) {
	                		System.out.println("Group " + i + ": " + m.group(i));
	                }
	                **/
	
	                //POST or GET to upper case
	                String type = m.group(2).toUpperCase();
	
	                //Trim the host
	                String host = m.group(14).replaceAll("'", "").trim();
	
	                //Assign the path if not empty
	                String path = "";
	
	                if (m.group(15) != null) {
	                    path = m.group(15).replaceAll("'", "").trim();
	                }
	
	
	                //Check if -v
	                isVerbose = m.group(4) != null ? true : false;
	
	                //THIS MIGHT NEED TO BE MODIFIED FOR POST
	                //Check if -h
	                isHeader = m.group(5) != null ? true : false;
	                if (isHeader) {
	                    headerString = m.group(6);
	                }
	
	                //Check if -d
	                isData = m.group(7) != null ? true : false;
	                if (isData) {
	                    dataString = m.group(8);
	                }
	
	                //Check if -f
	                isFile = m.group(9) != null ? true : false;
	                if (isFile) {
	                    filename = new File(m.group(10));
	                }
	
	                //Additional check GET method for cURL
	                if (type.equals(HTTP_METHOD_GET) && (isData || isFile)) {
	                    System.out.println("The GET request cannot be combined with the -f or -d options.");
	                    patternCheck = false;
	                    continue;
	                }
	
	                //Additional check on POST method for cURL
	                if (type.equals(HTTP_METHOD_POST) && isData && isFile) {
	                    System.out.println("The POST request cannot be combined with the -f and the -d options.");
	                    patternCheck = false;
	                    continue;
	                }
	
					httpc(path, host, type, null, isData, isFile, isVerbose, filename);
					//if we can get redirect working
					// bonusRedirect();
	            } else {
	                System.out.println("The input was incorrect. Please try again. Enter '0' to exit");
	            }
        	}
        	
        	//Help
        	else if (option.equals("2")) {
        		
        		System.out.println("Enter one of the following: ");
        		System.out.println("1- Enter \"1\" to display the general help");
        		System.out.println("2- Enter \"2\" to display the get help");
        		System.out.println("3- Enter \"3\" to display the post help");
        		System.out.println("Enter anything else to return to the main menu");
        		String helpOption = console.readLine();
        		
        		if(helpOption.equals("1") || helpOption.equals("2") || helpOption.equals("3")) {
        			helpMenu(helpOption);
        			System.out.println("Enter any key to return to the main menu: ");
        			console.readLine();
        		}
        		else {
        			continue;
        		}
        	}
        	
        	//Exit
        	else  {
        		System.exit(0);
        	}
        }
    }
    
    //Displays the help menu
    public static void helpMenu(String helpOption) {
    	
    	if(helpOption.equals("1")) {
    		System.out.println("httpc help" + "\n");
			System.out.println("httpc is a curl-like application but supports HTTP protocol only.");
			System.out.println("Usage:");   
			System.out.println("\t" + "httpc command [arguments]");
			System.out.println("The commands are:");
			System.out.println("\t" + "get" + "\t" + "executes a HTTP GET request and prints the response.");
			System.out.println("\t" + "post" + "\t" + "executes a HTTP POST request and prints the response.");
			System.out.println("\t" + "help" + "\t" + "prints this screen." + "\n");
			System.out.println("Use \"httpc help [command]\" for more information about a command.");
    	}

    	else if (helpOption.equals("2")) {
    		System.out.println("httpc help get" + "\n");
			System.out.println("usage: httpc get [-v] [-h key:value] URL" + "\n");
			System.out.println("Get executes a HTTP GET request for a given URL." + "\n");
			System.out.println("\t" + "-v" + "\t\t" + "Prints the detail of the response such as protocol, status, and headers.");
			System.out.println("\t" + "-h key:value" + "\t" + "Associates headers to HTTP Request with the format 'key:value'.");
    	}
    	else {
    		System.out.println("httpc help post" + "\n");
    		System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL" + "\n");
			System.out.println("Post executes a HTTP POST request for a given URL with inline data or from file." + "\n");
			System.out.println("\t" + "-v" + "\t\t" + "Prints the detail of the response such as protocol, status, and headers.");
			System.out.println("\t" + "-h key:value" + "\t" + "Associates headers to HTTP Request with the format 'key:value'.");
			System.out.println("\t" + "-d string" + "\t" + "Associates an inline data to the body HTTP POST request.");
			System.out.println("\t" + "-f file" + "\t\t" + "Associates the content of a file to the body HTTP POST request." + "\n");
			System.out.println("Either [-d] or [-f] can be used but not both.");
    	}	
    }

    public static void httpGetRequest(String host, String path) throws Exception {

        try {
            //Initialize the socket
            Socket socket = new Socket(host, DEFAULT_PORT);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			String fileName = null;

            //https://stackoverflow.com/questions/2214308/add-header-in-http-request-in-java

			//Check if output file requested			
			if(path.contains("-o")){
				fileName = path.substring(path.indexOf("-o")+3, path.length());
				path = path.substring(0, path.indexOf("-o"));
			}
			
			//Define the request		
			String request = "";
            if (path == "" || path == null) {
				request = "GET / HTTP/1.0\r\nHost: " + host + "\r\n\r\n";
            } else {
                request = "GET " + path + " HTTP/1.0";
            }
			writer.println(request);

            if (headerString != "") {
                String[] headersArray = headerString.split(" ");
                for (int i = 0; i < headersArray.length; i++) {
                    writer.println(headersArray[i]);
                }

                //Modify the string if necessary
                for (String header: headersArray) {
                    if (header.contains("=")) {
                        writer.println(header.split("=")[0] + ":" + header.split("=")[1]);
                    }
				}
            }


            writer.println("");
            writer.flush();

            BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String outStr;
            String response = "";

            while ((outStr = bufRead.readLine()) != null) {
				response += outStr + "\n";
			}

            // Format output as needed
			formattedOutputResponse(isVerbose, response);
			
			//BONUS: updating cURL command line to output to textfile.
			if(fileName != null){
			try {
						PrintWriter extWriter = new PrintWriter(fileName);
						System.out.println("resp:" +response);
						extWriter.write(response);
						extWriter.close();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
            //Close everything
            bufRead.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Needed: getting body in json for -d or default
    public static void httpPostRequest(String host, String path, File file, boolean data) {


        try {
            //Initialize the socket
            Socket socket = new Socket(host, DEFAULT_PORT);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			String body = "";
			String request = "";
			
			//If -h
            if (headerString != "") {
                String[] headersArray = headerString.split(" ");
                for (int i = 0; i < headersArray.length; i++) {
                    writer.println(headersArray[i]);
                }
                
                //Modify the string if necessary
                for (String header: headersArray) {
                    if (header.contains("=")) {
                        writer.println(header.split("=")[0] + ":" + header.split("=")[1]);
                    }
                }
			}
			
            //If -d
			if(data){
				System.out.println(dataString);
				System.out.println(dataString.substring(1, dataString.length() - 1));
				request = "POST /post?info=info HTTP/1.0\r\n"
						+ "Content-Type:application/json\r\n"
						+ "Content-Length: " + dataString.length() +"\r\n"
						+ "\r\n"
						+ dataString.substring(1, dataString.length() - 1);
			}
			
			//If -f
			else if (file != null) {

				BufferedReader in = new BufferedReader(new FileReader(file));
				String line = "";
				StringBuilder StringBuilder = new StringBuilder();

                while ((line = in .readLine()) != null) {
					String formattedLine = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");

					String[] linesArray = formattedLine.split(",");
                    for (int i = 0; i < linesArray.length; i++) {
						StringBuilder.append(linesArray[i]+",");
					}				
					body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";
					request = "POST /post?info=info HTTP/1.0\r\n"
							+ "Content-Type:application/json\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;

				} in .close();
				
			}else{
				//Must refactor to get data passed in query
					body = "{"
							+ "\"DefaultAssignment\":1,"
							+ "\"DefaultCourse\": \"Networking\""
							+ "}";
					
					request = "POST /post?info=info HTTP/1.0\r\n"
					+ "Content-Type:application/json\r\n"
					+ "Content-Length: " + body.length() +"\r\n"
					+ "\r\n"
					+ body;
				}
			
			outputStream.write(request.getBytes());
			outputStream.flush();


            writer.println("");
			writer.flush();

            BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String outStr;
            String response = "";

            while ((outStr = bufRead.readLine()) != null) {
                response += outStr + "\n";
            }

            //Format output as needed
            formattedOutputResponse(isVerbose, response);

            //Close everything
            bufRead.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

       
    }

    public static void httpc(String path, String host, String type, String query, boolean isData, boolean isFile, boolean isVerbose, File file) {
        try {
            if (host == null || host.equals("")) {
                host = "google.com";
            }

            //https://stackoverflow.com/questions/2214308/add-header-in-http-request-in-java
            if (type.equals("GET")) {
                httpGetRequest(host, path);
            } else if (type.equals("POST")) {
                httpPostRequest(host, path, file, isData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	public static void bonusRedirect() {
		try {
			Socket socket = new Socket("https://duckduckgo.com/", 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			String request = "GET / HTTP/1.0\r\nHost: https://duckduckgo.com/\r\n\r\n";
			
			outputStream.write(request.getBytes());
			outputStream.flush();
			
			StringBuilder response = new StringBuilder();
			
			int data = inputStream.read();
			
			while(data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}
			
			System.out.println(response);
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private static void formattedOutputResponse(boolean isVerbose, String response) {

        if (isVerbose) {
            System.out.println(response);
        } else {
            String[] responseFormatted = response.split("\n\n");

            for (int i = 1; i < responseFormatted.length; i++)
                System.out.println(responseFormatted[i]);
        }
    }

    //https://stackoverflow.com/questions/2271800/how-to-read-the-parameters-and-value-from-the-query-string-using-java
    public static Map < String, String > getQueryMap(String query) {
        String[] params = query.split("&");
        Map < String, String > map = new HashMap < String, String > ();
        for (String param: params) {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1) {
                String value = p[1];
                map.put(name, value);
            }
        }
        return map;
    }


}