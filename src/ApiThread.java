import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * PLUS UTILISEE
 *
 */

public class ApiThread extends Thread implements Runnable {

	private String q;
	private Main appli;
	
	public ApiThread(Main _appli, String _q) {
		appli = _appli;
		q = _q;
	}
	
	private void removeThread() {
		
		System.out.println("END THREAD " + q + " - nb : " + appli.runningThread.size());

		appli.runningThread.removeElement(this);
		if(appli.runningThread.size() == 0) {
			try {
				appli.getFileOut().close();
				appli.getObjectOut().close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			appli.updateGraph();
		}
		
	}

	@Override
	public void run(){
		for(int i = 0 ; i < 10 ; i ++) {
			try {
	            //System.out.print(q + ", ");
	    		String xmlString = getXmlFromUrl("https://dblp.org/search/publ/api?q="+q+"&h="+appli.QUERY_H +"&f=" + appli.QUERY_H * i);
	    		if(xmlString == null) {
	    			//removeThread(true);
	    			continue;
	    		}
				SAXParserFactory factory = SAXParserFactory.newInstance();
			  	SAXParser saxParser;
	  			saxParser = factory.newSAXParser();
				saxParser.parse(new InputSource(new StringReader(xmlString)), new ArticleHandler(appli));
				//removeThread(false);
				
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
				//removeThread(true);
			}
		}

		removeThread();
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
			// If there is an exception, delete the save file (because it's empty, or contains wrong objects)
			File f = new File(Main.SAVE_FILE);
			f.delete();
			e.printStackTrace();
			return null;
		}
 
		return sb.toString();
	}
}
