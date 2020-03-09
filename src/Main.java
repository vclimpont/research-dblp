import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	// IO & API variables
	public static final String SAVE_FILE = "articleSave.dblpsave";
	private final int QUERY_H = 1000;
	private ArrayList<String> searchedWords; 

	// Data variables
	public static HashMap<String, Article> articles;
	public static Stack<ApiThread> runningThread;
	
	// View variables
	private Stage primaryStage;
	private FxViewPanel panelGraph;
	private FxViewer viewerGraph;
	
	private FileOutputStream fileOut;
	private ObjectOutputStream objectOut;
	
	public static void main(String[] args) throws Exception {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		// launch the application
		launch(args);
    }
	
	private void parseXml(String q, int h, int f) {
		try {
    		String xmlString = getXmlFromUrl("https://dblp.org/search/publ/api?q="+q+"&h="+h+"&f="+f);
    		if(xmlString == null) {
    			Main.runningThread.removeElement(this);
    			return;
    		}
			SAXParserFactory factory = SAXParserFactory.newInstance();
		  	SAXParser saxParser;
  			saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlString)), new ArticleHandler(objectOut));
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
			// If there is an exception, delete the save file (because it's empty, or contains wrong objects)
			File f = new File(Main.SAVE_FILE);
			f.delete();
			e.printStackTrace();
			return null;
		}
 
		return sb.toString();
	}
	
	/**
	 * Create threads which will get XMLs from DBLP and will add articles in the HashMap "articles"
	 * @throws IOException 
	 */
	private void setDataFromDblp() throws IOException {

		fileOut = new FileOutputStream (Main.SAVE_FILE);
		objectOut = new ObjectOutputStream(fileOut);
		
		for(String aWord : searchedWords) {
			for(int i = 0 ; i < 10 ; i ++) {
				// Create a Thread
				//	1st parameter refer to the h param in the query url, 2nd parameter refer to the f param
				// 	See https://dblp.uni-trier.de/faq/13501473 to understand query url parameter
				/*ApiThread at = new ApiThread(aWord, QUERY_H, QUERY_H * runningThread.size(), objectOut);
				runningThread.add(at);
				at.start();*/
				parseXml(aWord, QUERY_H, QUERY_H * i);
			}
		}
		

		// Wait until all the thread have been stopped
		while(!runningThread.empty()) {
			try {
				// Wait a second
				Thread.sleep(1000);
				System.out.println("threads : " + runningThread.size());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	    objectOut.close();
        fileOut.close();
	}
	
	/**
	 * Create threads which will get articles from save files if a save exists
	 */
	private void setDataFromSaveFile() {
		if(isSaveFile()) {
			try {
				FileInputStream fis = new FileInputStream(SAVE_FILE);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Article newArticle;
				while((newArticle = (Article)ois.readObject()) != null) {
					articles.put(newArticle.getId(), newArticle);
				}
				ois.close();
				fis.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return true if a save file exist, false otherwise
	 */
	private boolean isSaveFile() {
		return getSaveFile().isFile();
	}
	
	/**
	 * @return The file which contains articles
	 */
	private File getSaveFile() {
		return new File(SAVE_FILE);
	}

	/**
	 * Initialize a graph with the articles get previously
	 */
	private void initGraph() {
		DBLPGraph dblpg = new DBLPGraph();
		dblpg.readArticles();

		// Create a graph viewer, which will contains the graph
		viewerGraph = new FxViewer(dblpg.getGraph(), FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);		
		// Let graphStream manage the placement of the nodes
		viewerGraph.enableAutoLayout();

	}

	/**
	 * Setting up a panels, scene and stage for a first window
	 */
	private void initStage() {
		panelGraph = (FxViewPanel) viewerGraph.addDefaultView(false, new FxGraphRenderer());
		
		BorderPane bp = new BorderPane();
		bp.setCenter(panelGraph);

		Scene scene = new Scene(bp);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}

	@Override
	public void start(Stage _primaryStage) throws Exception {
		this.primaryStage = _primaryStage;
        this.primaryStage.setTitle("Recherche DBLP");
		
		articles = new HashMap<String, Article>();
		runningThread = new Stack<>();
		searchedWords = new ArrayList<String>();
		
		// TODO : Noter les mots à rechercher dans un txt, les extraire et les entrer dans le ArrayList
		// En attendant :
		searchedWords.add("e");
		
		if(isSaveFile())
			setDataFromSaveFile();
		else
			setDataFromDblp();
		

		initGraph();
		initStage();
		

 		// Force the application to quit after closing the window
 		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
 		    @Override
 		    public void handle(WindowEvent t) {
 		    	quit();
 		    }
 		});
		
	    /* Write titles in file
	     * try {
		    BufferedWriter writer = new BufferedWriter(new FileWriter("publication.txt"));
			for (Article p : articles.values()) {
				writer.write(p.getTitle()+"\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public void quit() {
		primaryStage.close();
        Platform.exit();
        System.exit(0);
	}
}
