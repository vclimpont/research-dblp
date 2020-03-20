import java.io.NotActiveException;
import java.util.Scanner;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ArticleHandler extends DefaultHandler {

	public static final String tag_publication = "hit";	
	public static final String tag_title = "title";
	public static final String tag_year = "year";
	public static final String tag_url = "ee";
	
	private Stack<String> elementStack;
	private Stack<Article> publicationStack;
	
	//private ObjectOutputStream objectOut;
	private Main appli;
	private ApiThread thread;

	public ArticleHandler(Main _appli, ApiThread t) {
		elementStack = new Stack<String>();
		publicationStack = new Stack<Article>();
		appli = _appli;
		thread = t;
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
		//Push it in element stack
        this.elementStack.push(qName);

        if (qName.equals(tag_publication)) {
        	this.publicationStack.add(new Article(attributes.getValue("id")));
        }
    }
 
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	//Remove last added element
        this.elementStack.pop();
        
        if (qName.equals(tag_publication)) {
        	if(!publicationStack.empty()) {
        		thread.articlesTmp.put(publicationStack.peek().getId(), publicationStack.peek());
        		
        		writeArticleInFile(publicationStack.peek());
                this.publicationStack.pop();
        	}
        }
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
    	if(!publicationStack.empty() && !elementStack.empty()) {
    		if(elementStack.peek().equals(tag_title)) {
        		publicationStack.peek().setTitle(new String(ch, start, length));
        	} else if(elementStack.peek().equals(tag_year)) {
        		publicationStack.peek().setYear(new String(ch, start, length));
        	} else if(elementStack.peek().equals(tag_url)) {
        		publicationStack.peek().setUrl(new String(ch, start, length));
        	}
    	}
    }
    
    private void writeArticleInFile(Article article) {
    	try {
    		//appli.getObjectOut().writeObject(article);
        //} catch(NotActiveException nae) {
        //	System.err.println("NotActiveException");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
