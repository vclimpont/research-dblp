import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class DBLPGraph {

	private Graph graph; // Main graph with dates and keywords as nodes and number of articles as edges
	private HashMap<String, ArrayList<Article>> keysToArticles; // Associate a key word to a list of articles
	private HashMap<String, ArrayList<Article>> yearToArticles; // Associate a date to a list of articles
	
	public DBLPGraph()
	{
		graph = new SingleGraph("DBLPGraph");
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
				graph.addNode(art.getYear());
			}
			 
			// for each keyword of this article
			for(String key : art.getKeywords())
			{
				// add this article in hashmap at the given keyword
				addArticleToKey(key, art);
				
				// create the node of the keyword if it does not exist
				if(graph.getNode(key) == null)
				{
					graph.addNode(key);
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
			keysToArticles.put(year, newArticles);
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
	
	public void displayGraph()
	{
		graph.display();
	}
}

