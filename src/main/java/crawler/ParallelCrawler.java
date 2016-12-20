package crawler;

import com.sun.istack.internal.Nullable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Multi-threaded crawler class
 */
public class ParallelCrawler implements Crawler {
    public static final String LOG_TAG = "Crawler";
    public static final int MAX_FILENAME_LENGTH = 100;

    private MyPoolExecutor<CrawlTask> executor;
    private Deque<Link> linkQueue;
    private Set<String> visitedUrls;

    private volatile int linksInProgressCounter = 0;
    private volatile boolean isFinished = true;

    WebPageAsyncHandler resultHandler;

    /**
     * Crawler implementation with pool executor support
     * @param nThreads number of parallel threads (non-positive values equal to 1)
     * @param resultHandler asynchronous handler
     */
    public ParallelCrawler(int nThreads, @Nullable WebPageAsyncHandler resultHandler) {
        // validate number of threads
        if (nThreads < 1) {
            Logger.getLogger(LOG_TAG).info("Get negative nThreads, setting nThreads to 1");
            nThreads = 1;
        }

        executor = new MyPoolExecutor<>(nThreads);
        linkQueue = new ArrayDeque<>();
        visitedUrls = new HashSet<>();

        // in case of null handler create an empty one
        if (resultHandler == null) {
            Logger.getLogger(LOG_TAG).info("Get null resultHandler, using empty handler");
            this.resultHandler = new WebPageAsyncHandler() {
                @Override
                public void handle(String savedPageFilePath) {
                }
            };

        } else {
            this.resultHandler = resultHandler;
        }
    }

    /**
     * Depth-based crawler algorithm, uses synchronized queue for links
     * @param url root URL
     * @param depth levels of depth (0 - only root)
     */
    @Override
    public synchronized void crawl(String url, int depth) {
        // validate input arguments
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        if (depth < 0) {
            throw new IllegalArgumentException("depth cannot be negative");
        }

        // initial values for class parameters
        linksInProgressCounter = 1;
        isFinished = false;
        visitedUrls.clear();

        // run initial zero-level task
        executor.execute(new CrawlTask(this, url, depth));

        while (true) {

            // wait until new links available or crawl algorithm finished
            try {
                while (linkQueue.isEmpty() && !isFinished) {
                    wait();
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            if (isFinished) {
                return;
            }

            // process the whole queue of links
            while (!linkQueue.isEmpty()) {
                Link link = linkQueue.poll();

                // if URL already visited, decrease links left counter and go to next one
                if (!visitedUrls.add(link.getUrl())) {
                    updateLinksInProgressCounter(-1);
                    continue;
                }

                // create task for link processing
                executor.execute(new CrawlTask(this, link.getUrl(), link.getDepth()));
            }
        }
    }

    /**
     * Simple list-based crawler algorithm
     * @param urls list of URL strings (list cannot be empty, each single URL can)
     */
    @Override
    public synchronized void crawl(List<String> urls) {
        // validate input arguments
        if (urls == null || urls.isEmpty()) {
            throw new IllegalArgumentException("list of URLs cannot be empty");
        }

        // initial values for class parameters
        linksInProgressCounter = urls.size();
        isFinished = false;

        // run crawl task for each URL in list
        for (String url : urls) {
            executor.execute(new CrawlTask(this, url, 0));
        }

        // wait until finished
        try {
            while (!isFinished) {
                wait();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Update the counter of total links to process. NotifyAll in case of all links processed.
     * @param delta update value
     */
    synchronized void updateLinksInProgressCounter(int delta) {

        linksInProgressCounter += delta;

        if (linksInProgressCounter == 0) {
            isFinished = true;
            notifyAll();
        }
    }

    /**
     * Add list of links to the queue and notify threads
     * @param links list of links
     */
    synchronized void putLinksInQueue(List<Link> links) {
        linkQueue.addAll(links);
        notifyAll();
    }
}
