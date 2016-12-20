package crawler;

/**
 * Simple handler for testing purposes
 */
public class PrintHandler implements WebPageAsyncHandler {
    @Override
    public void handle(String savedPageFilePath) {
        System.out.println(savedPageFilePath);
    }
}
