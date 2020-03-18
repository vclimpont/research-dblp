import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InfoYearController {

	@FXML
	private Label yearLabel;
	@FXML
	private Label nbKeywordsLabel;
	@FXML
	private Label nbArticlesLabel;
	
	private int nbArticles;
	
	public void initInfoUser(DBLPGraph dblpg, Node n) {
		nbArticles = 0;
		
		yearLabel.setText("@" + n.getId());
		nbKeywordsLabel.setText(Long.toString(n.getDegree()));
		
		for(Object o : n.edges().toArray())
		{
			Edge e = dblpg.getGraph().getEdge(((Edge)o).getId());
			double w = e.getNumber("weight");
			nbArticles += (int) w;
		}
		nbArticlesLabel.setText(Long.toString(nbArticles));
	}
}
