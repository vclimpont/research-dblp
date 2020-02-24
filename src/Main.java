import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

public class Main {

	public static HashMap<String, Article> articles;
	public static Stack<ApiThread> runningThread;
	
	public static void main(String[] args) throws Exception {
		new Main();
		
		DBLPGraph dblpg = new DBLPGraph();
		dblpg.readArticles();
    }
	
	private void setDataFromDblp() {
		for(int i = 0 ; i < 10 ; i ++) {
			ApiThread at = new ApiThread(1000, 1000 * runningThread.size());
			runningThread.add(at);
			at.start();
		}
	}
	
	public Main() {

		articles = new HashMap<String, Article>();
		runningThread = new Stack<>();
		
		setDataFromDblp();
		
		while(!runningThread.empty()) {
			try {
				Thread.sleep(1000);
				System.out.println(runningThread.size());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	    try {
		    BufferedWriter writer = new BufferedWriter(new FileWriter("publication.txt"));
			for (Article p : articles.values()) {
				writer.write(p.getTitle()+"\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
