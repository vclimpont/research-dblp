import java.io.BufferedReader;
import java.io.File;
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

import javafx.concurrent.Task;

/**
 * PLUS UTILISEE
 *
 */

public class ApiThread extends Task<Void> {

	private Main appli;
	private String q;
	private int h, f;
	public HashMap<String, Article> articlesTmp;
	
	public ApiThread(Main _appli, String _q, int _h, int _f) {
		appli = _appli;
		q = _q;
		h = _h;
		f = _f;
	}
	
	private void removeThread() {
		appli.updateGraph();
		
		appli.articles.putAll(articlesTmp); 
		
		System.out.println("END THREAD ");

		appli.runningThread.removeElement(this);
		
		if(appli.runningThread.size() == 0) {
			System.out.println("Close streams");
			try {
				appli.getFileOut().close();
				appli.getObjectOut().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*@Override
	public void run(){
		this.articlesTmp = new HashMap<String, Article>();
		try {
            
    		String xmlString = getXmlFromUrl("https://dblp.org/search/publ/api?q="+q+"&h="+h+"&f="+f);
    		if(xmlString == null) {
    			removeThread();
    			return;
    		}
			SAXParserFactory factory = SAXParserFactory.newInstance();
		  	SAXParser saxParser;
  			saxParser = factory.newSAXParser();

			saxParser.parse(new InputSource(new StringReader(xmlString)), new ArticleHandler(appli, this));

			removeThread();
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			removeThread();
		}
		
	}*/

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
			// If there is an exception, delete the save file (because it's empty, or contains wrong objects)
			File f = new File(Main.SAVE_FILE);
			f.delete();
			e.printStackTrace();
			return null;
		}
 
		return sb.toString();
	}

	@Override
	protected Void call() throws Exception {
		this.articlesTmp = new HashMap<String, Article>();
		try {
            
    		String xmlString = getXmlFromUrl("https://dblp.org/search/publ/api?q="+q+"&h="+h+"&f="+f);
    		if(xmlString == null) {
    			removeThread();
    			return null;
    		}
			SAXParserFactory factory = SAXParserFactory.newInstance();
		  	SAXParser saxParser;
  			saxParser = factory.newSAXParser();

			saxParser.parse(new InputSource(new StringReader(xmlString)), new ArticleHandler(appli, this));

			removeThread();
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			removeThread();
		}
		return null;
	}
}
