import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class Httpc {

	public static void main(String[] args) {

		httpc("/status/418", "httpbin.org", "POST", null);

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
            if(type == "GET"){
                if(query == null) {
                    request = "GET " + path + " HTTP/1.0\r\n\r\n";
                } else {
                    request = "GET " + path + "?" +query +" HTTP/1.0\r\n\r\n";
                }
            }else if(type == "POST"){
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