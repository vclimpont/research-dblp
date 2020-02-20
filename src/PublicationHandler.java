import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PublicationHandler extends DefaultHandler {

	public static final String tag_publication = "hit";
	public static final String tag_info = "info";
	public static final String tag_title = "title";
	public static final String tag_year = "year";
	public static final String tag_keywords = "key";
	
	private Main main;
	private Stack<String> elementStack;
	private Stack<Publication> publicationStack;
	
	public PublicationHandler(Main m) {
		this.main = m;
		elementStack = new Stack<String>();
		publicationStack = new Stack<Publication>();
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException
    {
		//Push it in element stack
        this.elementStack.push(qName);

        if (qName.equals(tag_publication)) {
        	this.publicationStack.add(new Publication(attributes.getValue("id")));
        }
    }
 
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	//Remove last added element
        this.elementStack.pop();
        
        if (qName.equals(tag_publication)) {
        	if(!publicationStack.empty()) {
        		this.main.getPublications().put(publicationStack.peek().getId(), publicationStack.peek());
                this.publicationStack.pop();
        	}
        }
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {

    	if(!publicationStack.empty()) {
    		if(!elementStack.empty() && elementStack.peek().equals(tag_title)) {
        		publicationStack.peek().setTitle(new String(ch, start, length));
        	}
    	}
    }
}
