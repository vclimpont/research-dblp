import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Article implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String title;
	private String year;
	private ArrayList<String> keywords;
	private URL url;
	
	public Article(String id) {
		this.keywords = new ArrayList<String>();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.setKeywordsFromTitle(title);
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public void setKeywordsFromTitle(String keywords) {
		ArrayList<String> keys = new ArrayList<String>();
		for(String word : keywords.split(" ")) {
			if(word.length() > 3)
				keys.add(word);
		}
		this.keywords = keys;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setUrl(String url) {
		try {
			URL u = new URL(url);
			this.url = u;
		} catch (MalformedURLException e) {}
	}
	
	public String toString() {
		return "[" + id + "] " +
				"\n - titre : " + title +
				"\n - année : " + year +
				"\n - mots-clés : " + keywords + 
				"\n - url : " + url;
	}
}
