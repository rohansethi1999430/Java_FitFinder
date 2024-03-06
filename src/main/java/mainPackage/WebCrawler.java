package mainPackage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebCrawler {

    private final int MAX_DEPTH = 2;
    private static final int MAX_URLS = 100;
    private Utilities utl = new Utilities();

    
    protected List<String> crawl(String startUrl) {
        Set<String> visitedUrlsCNC = new HashSet<>();
        List<String> crawledUrls = new ArrayList<>();
        
        int crawledCount = 0;
        crawlUrl(startUrl, 0, MAX_DEPTH, visitedUrlsCNC, crawledUrls, crawledCount);
        utl.printOnConsole(utl.msg, "Crawling has been done successfully!");
        return crawledUrls;
    }

    private void crawlUrl(String urlCNC, int currentDepth, int maxDepth, Set<String> visitedUrlsCNC, List<String> crawledUrls, int crawledCount) {
        if (crawledCount >= MAX_URLS || currentDepth > maxDepth || visitedUrlsCNC.contains(urlCNC)) {
            return;
        }

        System.out.println("Crawling: " + urlCNC);

        visitedUrlsCNC.add(urlCNC);
        crawledUrls.add(urlCNC);
        crawledCount++;

        try {
            Document document = Jsoup.connect(urlCNC).get();
            Elements linksCNC = document.select("a[href]");

            for (Element linkCNC : linksCNC) {
                String nextUrlCNC = linkCNC.absUrl("href");
                crawlUrl(nextUrlCNC, currentDepth + 1, maxDepth, visitedUrlsCNC, crawledUrls, crawledCount);
            }
        } catch (Exception exctCNC) {
            System.out.println("Error in connection: " + urlCNC);
        }
    }

}

