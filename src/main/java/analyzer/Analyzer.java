package analyzer;


import crawler.WebPageAsyncHandler;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Analyzer implements WebPageAsyncHandler {

    private final Executor<ParseHTMLpageTask> executor;
    private String folderPath;

    public Analyzer(Integer numThreads, String folderPath) {
        executor = new Executor<ParseHTMLpageTask>(numThreads);
        this.folderPath = folderPath;
    }

    public void start() throws SecurityException {
        File folder = new File(folderPath);

        Logger logger = Logger.getLogger("Analyzer");
        logger.setLevel(Level.INFO);
        logger.fine( "Creating folder " + folderPath);

        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch(SecurityException se){
                logger.info("Cannot create folder " + folderPath);
                throw se;
            }
        }

        executor.start();
    }

    public void interrupt() {
        executor.interruptSoft();
    }

    @Override
    public synchronized void handle(String savedPageFilePath) {
        Logger logger = Logger.getLogger("Analyzer");
        logger.setLevel(Level.INFO);
        logger.fine("savedPageFilePath " + savedPageFilePath);
        String fileName = Paths.get(savedPageFilePath).getFileName().toString();

        String pathFileOut = Paths.get(folderPath, fileName).toString() + ".txt";

        logger.setLevel(Level.INFO);
        logger.fine(pathFileOut);
        ParseHTMLpageTask task = new ParseHTMLpageTask(savedPageFilePath, pathFileOut);
        executor.execute(task);
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
