import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class DBLPGraph {

	private Graph graph;
	private HashMap<String, ArrayList<Article>> keysToArticles;
	private HashMap<String, ArrayList<Article>> yearToArticles;
	
	public DBLPGraph(int nbYears, String firstYear)
	{
		graph = new SingleGraph("DBLPGraph");
		keysToArticles = new HashMap<String, ArrayList<Article>>();
		yearToArticles = new HashMap<String, ArrayList<Article>>();
		
		initYears(nbYears, firstYear);
	}
	

	private void initYears(int nbYears, String firstYear) {
		
		int k = Integer.parseInt(firstYear);
		for(int i = k; i < (k + nbYears); i++)
		{
			yearToArticles.put(Integer.toString(i), new ArrayList<Article>());
			
			graph.addNode(Integer.toString(i));
		}
	}
	
	public void readData(String[] data)
	{
		// Cr�er un nouvel article 
		
		// Ajouter l'article � chaque liste de ses MC dans la hashmap (si le MC n'existe pas, le cr�er)
		
		// Ajouter l'article � la liste de son ann�e de publi dans la hashmap
		
		// Cr�er un noeud pour chaque MC s'ils existent pas 
		
		// Cr�er l'ar�te MC-ann�e. Si elle existe d�j�, incr�menter le poids
	}
}

