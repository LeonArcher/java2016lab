package crawler;

/**
 * Interface for Crawler results asynchronous processing
 */
public interface WebPageAsyncHandler {
    void handle(String savedPageFilePath);
}
