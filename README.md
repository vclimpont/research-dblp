# DBLP Graphical Visualization

Academic project developed in Java in order to visualize the evolution of computer sciences research themes over time.
The database for this project is directly extracted from DBLP : https://dblp.org/.

The idea is to extract around 40 000 random articles from the DBLP database then build a graphical representation using keywords and the publication date of the articles to correlate main research subjects over time.

## Graphical representation

Connections between articles keywords and years are represented as nodes in a heterogeneous graph.

Weighted edges are created between keywords nodes *Kn* and years nodes *Yn* for each article that was published in year *Y* and containing keywords *K*.

The size and color of nodes are based on their "popularity" within the graph :
* A year node's size and color are relative to the number of articles published this year
* A keyword node's size and color are relative to the number of articles containing this keyword.

Each node in the graph can be selected on click : 

* On a year node, it gives the user access to :
  * Every keywords mentioned this year
  * Number of articles published this year

* On a keyword node, it gives the user access to :
  * Every years in which at least one article published contained this keyword
  * Number of articles containing this keyword
