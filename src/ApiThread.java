import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ApiThread extends Thread implements Runnable {

	private int h, f;
	
	public ApiThread(int _h, int _f) {
		h = _h;
		f = _f;
	}

	@Override
	public void run(){
		try {
			System.out.println(" --- " + getName() + " --- ");
    		String xmlString = getXmlFromUrl("https://dblp.org/search/publ/api?q=e&h="+h+"&f="+f);
    		if(xmlString == null) {
    			Main.runningThread.removeElement(this);
    			return;
    		}
			SAXParserFactory factory = SAXParserFactory.newInstance();
		  	SAXParser saxParser;
  			saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlString)), new PublicationHandler());
			System.out.println("https://dblp.org/search/publ/api?q=e&h="+h+"&f="+f);
			Main.runningThread.removeElement(this);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			Main.runningThread.removeElement(this);
		}
		
	}

	private String getXmlFromUrl(String myURL) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		
		URL url;
		try {
			url = new URL(myURL);
			urlConn = url.openConnection();
			
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
 
		return sb.toString();
	}
}
