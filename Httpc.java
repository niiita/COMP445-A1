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
// import org.json.JSONArray;


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

        while (patternCheck != true) {
            value = console.readLine("Enter string (0 to exit the application): ");

            //Exit if the value entered is 0
            if (value.equals("0")) {
                System.exit(0);
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
            } else {
                System.out.println("The input was incorrect. Please try again. Enter '0' to exit");
            }
        }
    }

    public static void httpGetRequest(String host, String path) throws Exception {

        try {
            //Initialize the socket
            Socket socket = new Socket(host, DEFAULT_PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            //https://stackoverflow.com/questions/2214308/add-header-in-http-request-in-java

            //Define the request
            String request = "";
            if (path == "" || path == null) {
                request = "GET / HTTP/1.0\r\nHost: " + host + "\r\n\r\n";
            } else {
                request = "GET " + path + " HTTP/1.0";
            }
			System.out.println("host:"+ host);
			System.out.println("path"+path);
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
			System.out.println("respo:\n" + response);

            //Format output as needed
            // formattedOutputResponse(isVerbose, response);

            //Close everything
            bufRead.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //NEEDS TO BE FIXED
    public static void httpPostRequest(String host, String path, File file, boolean data) {


        try {
            //Initialize the socket
            Socket socket = new Socket(host, DEFAULT_PORT);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			String body = "";
			
			
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

            if (file != null) {

				BufferedReader in = new BufferedReader(new FileReader(file));
				String line = "";
				StringBuilder StringBuilder = new StringBuilder();

                while ((line = in .readLine()) != null) {
					String test = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");
					System.out.println(test);
					String[] headersArray = test.split(",");
                    for (int i = 0; i < headersArray.length; i++) {
						StringBuilder.append(headersArray[i]+",");
					}
					System.out.println("Stringbuilder:" + StringBuilder);
				
					body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";

                } in .close();
			}else{
				//Must refactor to get data passed in query
					body = "{"
							+ "\"Assignment\":1,"
							+ "\"Course\":Networking"
							+ "}";
						}
			
			String request = "POST /post?info=info HTTP/1.0\r\n"
							+ "Content-Type:application/json\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;
			
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