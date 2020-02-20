import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Main {

	private HashMap<String, Publication> publications;
	
	public static void main(String[] args) throws Exception {
		
		new Main();

    }
	
	public Main() {

		publications = new HashMap<String, Publication>();
		
		String xmlString = getXmlFromUrl("https://dblp.org/search/publ/api?q=e&h=10");
	    		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlString)), new PublicationHandler(this));

		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		for (Publication p : publications.values()) {
		    System.out.println(p);
		}
		
	}
	
	public String getXmlFromUrl(String myURL) {
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
	public HashMap<String, Publication> getPublications() {
		return publications;
	}
	public void setPublications(HashMap<String, Publication> publications) {
		this.publications = publications;
	}
}
