package lung3.siren;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.xml.sax.SAXException;

public class IndexNT {
  
  /**
   * URL of SIREn index
   */
  private final String INDEX_URL = "http://localhost:8080/siren";
  
  private final SolrServer server;
  
  public IndexNT(final String solrServerUrl) throws SolrServerException, IOException {
    server = new HttpSolrServer(solrServerUrl == null ? INDEX_URL : solrServerUrl);
  }
  
/**
 * Index all NT data contained in the files of the source directory.
 * @param source directory of data to index
 * @throws SolrServerException
 * @throws IOException
 */
  public void index(String source) throws SolrServerException, IOException {  
	File dir = new File(source);

	if (!FileIO.isValidDirectory(dir))
		return;

	for (File currNT : dir.listFiles()) {
		if (!FileIO.isValidFile(currNT))
			continue;
		
		System.out.println("Indexing file: " + currNT);
		BufferedReader reader = new BufferedReader(new FileReader(currNT));
		String line;
		
		while ((line = reader.readLine()) != null) {
			int urlPos = line.indexOf('>');
			String url = line.substring(1, urlPos);
			String allNtriples = FileIO.getAllLines(line, reader);
		
			SolrInputDocument doc = constructDoc(url, allNtriples);
	    	add(doc);
		}
    	reader.close();
	}
    
    commit();
  }
  
  /**
   * Construct a SolrInputDocument based on the given url and associate ntriples.
   * @param url of the document
   * @param ntriples associated with the url
   * @return the created SolrInputDocument
   */
  private SolrInputDocument constructDoc(String url, String ntriples) {
	  SolrInputDocument doc = new SolrInputDocument();
	  doc.addField("url", url);
	  doc.addField("ntriple", ntriples);
	  
	  return doc;
  }
  
  /**
   * Add a {@link SolrInputDocument}.
   */
  public void add(final SolrInputDocument doc)
  throws SolrServerException, IOException {
    final UpdateRequest request = new UpdateRequest();
    request.add(doc);
    request.process(server);
  }
  
  /**
   * Commit all documents that have been submitted
   */
  public void commit()
  throws SolrServerException, IOException {
    server.commit();
  }
  
  /**
   * Delete all the documents
   */
  public void clearIndex() throws SolrServerException, IOException {
    server.deleteByQuery("*:*");
    commit();
  }
  
  public static void main(String[] args)
  throws FileNotFoundException, IOException, SolrServerException, ParserConfigurationException, SAXException {
    final IndexNT ntriple = new IndexNT(args.length == 1 ? args[0] : null);
    ntriple.clearIndex();
    
    ntriple.index("cwm-1.2.1/data/cleanOCLC/merged");
    
    System.out.println("Done indexing!");
  }
  
}
