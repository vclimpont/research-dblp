import java.util.ArrayList;
import java.util.Arrays;
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
	private HashMap<String, Integer> keywordsCount;
	
	public DBLPGraph()
	{
		graph = new SingleGraph("DBLPGraph");
		graph.setAttribute("ui.stylesheet", "url('file://.//src//style.css')");
		keysToArticles = new HashMap<String, ArrayList<Article>>();
		yearToArticles = new HashMap<String, ArrayList<Article>>();
		keywordsCount = new HashMap<String, Integer>();
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
	
	public void keywordProcessing()
	{
		for(String id : Main.articles.keySet()) 
		{
			Article art = Main.articles.get(id);
			for(String k : art.getKeywords())
			{
				Integer count = keywordsCount.get(k);
				if(count == null)
				{
					keywordsCount.put(k, new Integer(1));
				}
				else
				{
					Integer newCount = new Integer(count + 1);
					keywordsCount.put(k, newCount);
				}
			}
		}
	}
	
	public void readArticles()
	{
		keywordProcessing();
		
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
				Integer count = keywordsCount.get(key);
				System.out.println(key + " " + count);
				if(count != null && count > 100)
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

	/**
	 * Set the edge transparent or not, depending on the 2nd param
	 * @param elem : an Edge
	 * @param isTransparent : should this element be transparent ?
	 */
	public void setEdgeTransparency(Edge elem, boolean isTransparent) {
		if(isTransparent == true) {
			elem.setAttribute("ui.class", "transparent");
		} else {
			elem.removeAttribute("ui.class");
		}
	}

	/**
	 * Set the node transparent or not, depending on the 2nd param
	 * @param elem : a Node
	 * @param isTransparent : should this element be transparent ?
	 */
	public void setNodeTransparency(Node elem, boolean isTransparent) {
		String cssClass = ((String)(elem.getAttribute("ui.class"))).split("_")[0];
		
		if(isTransparent == true) {
			elem.setAttribute("ui.class",  cssClass + "_transparent");
		} else {
			elem.setAttribute("ui.class", cssClass);
		}
	}
	
	public void hideUnselectedNode(Node selectedNode) {
		HashMap<String, Node> neighbours = new HashMap<>();
		
		graph.edges().forEach((Edge e)->{
			setEdgeTransparency(e, true);
		});
		
		// Try to select edges. Can catch a NullPointerException if there are no edges linked to the node
		try {
			// Loop over linked edges
			selectedNode.edges().forEach(e->{
				setEdgeTransparency(e, false);
				if(e.getNode0().getId() == selectedNode.getId()) {
					neighbours.put(e.getNode1().getId(), e.getNode1());
				} else {
					neighbours.put(e.getNode0().getId(), e.getNode0());
				}
			});
		} catch(NullPointerException e) {
			System.err.println("No edges");
		}
		graph.nodes().forEach((Node n)->{
			if(n.getId() == selectedNode.getId() || neighbours.get(n.getId()) != null)
				setNodeTransparency(n, false);
			else
				setNodeTransparency(n, true);
		});
	}

	public void showAllNode() {
		// For each nodes
		graph.nodes().forEach((Node n)->{
			setNodeTransparency(n, false);
		});
		// Remove the class attribute for each edges
		graph.edges().forEach(e->setEdgeTransparency(e, false));
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

