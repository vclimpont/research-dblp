import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Article {

	private String title; 
	private ArrayList<String> keys;
	private URL url;
	private String year;
	
	public Article(String _title, String _keys, String _url, String _year)
	{
		title = _title; 
		year = _year; 
		
		try {
			url = new URL(_url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		keys = separateKeys(_keys);
	}

	private ArrayList<String> separateKeys(String _keys) {
		
		ArrayList<String> k = new ArrayList<String>();
		k.add("dm");
		k.add("ai");
		k.add("bi");
		
		return k;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<String> getKeys() {
		return keys;
	}

	public void setKeys(ArrayList<String> keys) {
		this.keys = keys;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
}
