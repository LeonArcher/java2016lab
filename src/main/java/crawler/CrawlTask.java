package crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Task for asynchronous processing of single link
 */
class CrawlTask implements Task {
    private final ParallelCrawler parent;
    private final String url;
    private final int depthLevelsLeft;

    public CrawlTask(ParallelCrawler parent, String url, int depthLevelsLeft) {
        this.parent = parent;
        this.url = url;
        this.depthLevelsLeft = depthLevelsLeft;
    }

    @Override
    public void doWork() {

        Connection connection = Jsoup.connect(url);
        Elements linksRaw;

        // convert URL to safe path
        String savedDocPath = url.replaceAll("[^a-zA-Z0-9-_]", "_");
        savedDocPath = savedDocPath.length() > ParallelCrawler.MAX_FILENAME_LENGTH
                ? savedDocPath.substring(0, ParallelCrawler.MAX_FILENAME_LENGTH)
                : savedDocPath;

        // download document, write to file and extract all links
        try (FileOutputStream fos = new FileOutputStream(savedDocPath)) {

            byte[] bytes = connection.ignoreContentType(true).execute().bodyAsBytes();
            linksRaw = connection.get().select("a[href]");

            fos.write(bytes);

        } catch (IOException | IllegalArgumentException ex) {
            String errStr = "Error processing URL <" + url + ">: \n" + ex.toString();
            Logger.getLogger(ParallelCrawler.LOG_TAG).info(errStr);
            parent.updateLinksInProgressCounter(-1);
            return;
        }

        // notify Analyzer module of new document
        parent.resultHandler.handle(savedDocPath);

        // if last depth level, don't need to process new links
        if (depthLevelsLeft == 0) {
            parent.updateLinksInProgressCounter(-1);
            return;
        }

        // process new links
        List<Link> links = new ArrayList<>();

        for (Element link : linksRaw) {
            links.add(new Link(link.attr("abs:href"), depthLevelsLeft - 1));
        }

        // send new links to the queue and update counter
        parent.updateLinksInProgressCounter(links.size() - 1);
        parent.putLinksInQueue(links);
    }
}
