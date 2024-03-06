package mainPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlParser {

    private File[] htmlFiles;
    private String outputFolder;
	public static final String outputFolderName = "ParsedFiles";
	public static final String gymUrls = "F:\\@ MAC\\ACC\\ACC_Project\\FitFinder\\src\\main\\java\\Files\\GymContentUrls.txt";
	private Utilities utl;
	private WebCrawler webCrawler = new WebCrawler();
    
    public HtmlParser(File[] htmlFiles, String opFolderPath) {
    	this.htmlFiles = htmlFiles;
    	this.outputFolder = opFolderPath;
    	utl = new Utilities();
    }
    
    public HtmlParser() {
    	utl = new Utilities();
    }
    
    protected void parseWebsites() {
    	List<String> unValidUrls = new ArrayList<>();
    	
    	List<String> urls = webCrawler.crawl("https://blog.goodlifefitness.com/");
    	
    	for (String url : urls) {
    	    if (!utl.isUrlValid(url)) { 
    	        unValidUrls.add(url);
    	    }
    	    else {
    	        parseUsingUrl(url);
    	    }
    	}
    }
   
    protected void parseUsingUrl(String url) {
    	try {
            // Connect to the web page and get the HTML document
            Document document = Jsoup.connect(url).get();

            // Extract text content from the HTML document
            String textContent = document.text();
            
            // replace conflicting characters to be a file name.
            String fileName = url.replace('/', '_');
            fileName = fileName.replace('.', '~');
            fileName = fileName.replace(':', '@');
            
            String fileName_ = fileName + ".txt";
            File outputFile= new File(outputFolderName, fileName_);
            
            // Save the text content to a file
            try (FileWriter fileWriter = new FileWriter(outputFile)) {
            	fileWriter.write(textContent);
            }
            System.out.println("HTML content parsed and saved to " + outputFile);
        } 
    	catch (IOException e) {
			System.out.println("Something went wrong while interacting with file");
        }
    }
}

