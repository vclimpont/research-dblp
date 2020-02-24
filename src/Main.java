
public class Main {

	public static void main(String[] args) {
		//System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		DBLPGraph dblpg = new DBLPGraph(10, "2008");
		
		String[] str = {""};
		dblpg.readData(str);
		
		//dblpg.displayGraph();
	}

}
