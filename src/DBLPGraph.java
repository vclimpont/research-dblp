import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class DBLPGraph {

	public static final String NODE_TYPE_YEAR = "year";
	public static final String NODE_TYPE_KEYWORD = "keyword";
	private Graph graph; // Main graph with dates and keywords as nodes and number of articles as edges
	private HashMap<String, ArrayList<Article>> keysToArticles; // Associate a key word to a list of articles
	private HashMap<String, ArrayList<Article>> yearToArticles; // Associate a date to a list of articles
	
	public DBLPGraph()
	{
		graph = new SingleGraph("DBLPGraph");
		graph.setAttribute("ui.stylesheet", "url('file://.//src//style.css')");
		keysToArticles = new HashMap<String, ArrayList<Article>>();
		yearToArticles = new HashMap<String, ArrayList<Article>>();
	}
	
/*
	private void initYears(int nbYears, String firstYear) {
		
		int k = Integer.parseInt(firstYear);
		for(int i = k; i < (k + nbYears); i++)
		{
			yearToArticles.put(Integer.toString(i), new ArrayList<Article>());
			
			graph.addNode(Integer.toString(i));
		}
	}
	*/
	
	public void readArticles()
	{
		for(String id : Main.articles.keySet())
		{
			Article art = Main.articles.get(id);

			// add this article in hashmap at the given year
			addArticleToYear(art.getYear(), art);
			
			// create the node of the year if it does not exist 
			if(graph.getNode(art.getYear()) == null)
			{
				Node n = graph.addNode(art.getYear());
				n.setAttribute("ui.class", NODE_TYPE_YEAR);
			}
			 
			// for each keyword of this article
			for(String key : art.getKeywords())
			{
				// add this article in hashmap at the given keyword
				addArticleToKey(key, art);
				
				// create the node of the keyword if it does not exist
				if(graph.getNode(key) == null)
				{
					Node n = graph.addNode(key);
					n.setAttribute("ui.class", NODE_TYPE_KEYWORD);
				}
				
				// create an edge between keyword and year if it does not exist / otherwhise increment the weight
				if(graph.getEdge(key + art.getYear()) == null)
				{
					Edge e = graph.addEdge(key + art.getYear(), key, art.getYear(), false);
					e.setAttribute("weight", 1);
				}
				else
				{
					Edge e = graph.getEdge(key + art.getYear());
					double w = e.getNumber("weight");
					e.setAttribute("weight", w + 1);
				}
			}
		}
	}


	private void addArticleToYear(String year, Article art) {
		
		ArrayList<Article> articles = yearToArticles.get(year);
		if(articles == null)
		{
			ArrayList<Article> newArticles = new ArrayList<Article>();
			newArticles.add(art);
			yearToArticles.put(year, newArticles);
		}
		else
		{
			articles.add(art);
		}
	}


	private void addArticleToKey(String key, Article art) {
		
		ArrayList<Article> articles = keysToArticles.get(key);
		if(articles == null)
		{
			ArrayList<Article> newArticles = new ArrayList<Article>();
			newArticles.add(art);
			keysToArticles.put(key, newArticles);
		}
		else
		{
			articles.add(art);
		}
	}
	
	public void displayGraph() {
		//graph.display();
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public HashMap<String, ArrayList<Article>> getKeysToArticles() {
		return keysToArticles;
	}

	public void setKeysToArticles(HashMap<String, ArrayList<Article>> keysToArticles) {
		this.keysToArticles = keysToArticles;
	}

	public HashMap<String, ArrayList<Article>> getYearToArticles() {
		return yearToArticles;
	}

	public void setYearToArticles(HashMap<String, ArrayList<Article>> yearToArticles) {
		this.yearToArticles = yearToArticles;
	}
}

