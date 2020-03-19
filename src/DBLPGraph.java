import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class DBLPGraph {

	public static final String NODE_TYPE_YEAR = "year";
	public static final String NODE_TYPE_KEYWORD = "keyword";
	private final String TRANSPARENT = "transparent";
	private Graph graph; // Main graph with dates and keywords as nodes and number of articles as edges
	private HashMap<String, ArrayList<Article>> keysToArticles; // Associate a key word to a list of articles
	private HashMap<String, ArrayList<Article>> yearToArticles; // Associate a date to a list of articles
	private HashMap<String, Integer> keywordsCount;
	private HashMap<String, Integer> yearsCount;
	
	public DBLPGraph()
	{
		graph = new SingleGraph("DBLPGraph");
		graph.setAttribute("ui.stylesheet", "url('file://.//src//style.css')");
		keysToArticles = new HashMap<String, ArrayList<Article>>();
		yearToArticles = new HashMap<String, ArrayList<Article>>();
		keywordsCount = new HashMap<String, Integer>();
		yearsCount = new HashMap<String, Integer>();

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
	
	private void keywordProcessing()
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
	
	private void addWeightToYear(String year)
	{
		Integer count = yearsCount.get(year);
		if(count == null)
		{
			yearsCount.put(year, new Integer(1));
		}
		else
		{
			Integer newCount = new Integer(count + 1);
			yearsCount.put(year, newCount);
		}
	}
	
	public void readArticles()
	{
		keywordProcessing();
		
		for(String id : Main.articles.keySet())
		{
			Article art = Main.articles.get(id);
			 
			// for each keyword of this article
			for(String key : art.getKeywords())
			{
				Integer count = keywordsCount.get(key);
				if(count != null && count > 100 && count < 2500)
				{
					// add this article in hashmap at the given year
					addArticleToYear(art.getYear(), art);
					
					// create the node of the year if it does not exist 
					if(graph.getNode(art.getYear()) == null)
					{
						Node n = graph.addNode(art.getYear());
						n.setAttribute("ui.class", NODE_TYPE_YEAR);
						n.setAttribute("ui.label", art.getYear());
					}
					
					// add this article in hashmap at the given keyword
					addArticleToKey(key, art);
					
					// create the node of the keyword if it does not exist
					if(graph.getNode(key) == null)
					{
						Node n = graph.addNode(key);
						n.setAttribute("ui.class", NODE_TYPE_KEYWORD);
						n.setAttribute("ui.label", key);
					}
					
					// create an edge between keyword and year if it does not exist / otherwhise increment the weight
					if(graph.getEdge(key + art.getYear()) == null)
					{
						Edge e = graph.addEdge(key + art.getYear(), key, art.getYear(), false);
						e.setAttribute("weight", 1);
						addWeightToYear(art.getYear());
					}
					else
					{
						Edge e = graph.getEdge(key + art.getYear());
						double w = e.getNumber("weight");
						e.setAttribute("weight", w + 1);
						addWeightToYear(art.getYear());
					}
				}
			}
		}
		
		setCentrality();
	}
	
	private void setCentrality()
	{
		for(Object o : graph.nodes().toArray())
		{
			Node n = ((Node)o);
			String type = (String) n.getAttribute("ui.class");
			Integer yc = yearsCount.get(n.getId());
			Integer kc = keywordsCount.get(n.getId());
			if(yc != null)
			{
				if(yc < 10)
				{
					n.setAttribute("ui.class", type+",blue");
				}
				else if(yc < 20)
				{
					n.setAttribute("ui.class", type+",green");
				}
				else if(yc < 30)
				{
					n.setAttribute("ui.class", type+",yellow");
				}
				else if(yc < 40)
				{
					n.setAttribute("ui.class", type+",orange");
				}
				else
				{
					n.setAttribute("ui.class", type+",red");
				}
			}
			else if(kc != null)
			{
				if(kc < 50)
				{
					n.setAttribute("ui.class", type+",blue");
				}
				else if(kc < 100)
				{
					n.setAttribute("ui.class", type+",green");
				}
				else if(kc < 300)
				{
					n.setAttribute("ui.class", type+",yellow");
				}
				else if(kc < 1000)
				{
					n.setAttribute("ui.class", type+",orange");
				}
				else
				{
					n.setAttribute("ui.class", type+",red");
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
			elem.setAttribute("ui.class", TRANSPARENT);
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
		String cssType  = ((String)(elem.getAttribute("ui.class"))).split(",")[0];
		String cssColor = ((String)(elem.getAttribute("ui.class"))).split(",")[1].split("_")[0];
		
		if(isTransparent == true) {
			elem.setAttribute("ui.class",  cssType + "," + cssColor + "_" + TRANSPARENT + "," + TRANSPARENT);
		} else {
			elem.setAttribute("ui.class", cssType + "," + cssColor);
		}
	}
	
	public void showEdgesLabel(Node selectedNode) {
		for(Object o : selectedNode.edges().toArray())
		{
			Edge e = graph.getEdge(((Edge)o).getId());
			double w = e.getNumber("weight");
			e.setAttribute("ui.label", ""+(int)w);
		}
	}
	
	public void hideEdgesLabel() {
		for(Object o : graph.edges().toArray())
		{
			Edge e = graph.getEdge(((Edge)o).getId());
			e.removeAttribute("ui.label");
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

	public HashMap<String, Integer> getKeywordsCount() {
		return keywordsCount;
	}

	public void setKeywordsCount(HashMap<String, Integer> keywordsCount) {
		this.keywordsCount = keywordsCount;
	}

	public HashMap<String, Integer> getYearsCount() {
		return yearsCount;
	}

	public void setYearsCount(HashMap<String, Integer> yearsCount) {
		this.yearsCount = yearsCount;
	}
}

