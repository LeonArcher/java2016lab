package crawler;

import java.util.List;

/**
 * Crawler with 2 crawl modes: depth-based and list-based
 */
public interface Crawler {
    void crawl(String url, int depth);
    void crawl(List<String> urls);
}
