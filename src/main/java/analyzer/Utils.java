package analyzer;


import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Utils for working with file system.
 * @author Kmike
 */
class Utils {
    /**
     * Load .html file.
     * @param filePath path to the file.
     * @return String of loaded file.
     */
    static String loadPage(String filePath) {
        Logger logger = Logger.getLogger("Analyzer");
        logger.setLevel(Level.INFO);
        logger.fine( "Loading file " + filePath);

        String text = "";
        BufferedReader inputFile = null;
        try {
            inputFile = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = inputFile.readLine()) != null) {
                text = new StringBuilder()
                        .append(text)
                        .append(line)
                        .toString();
            }
            inputFile.close();
            return text;
        } catch (IOException e) {
            logger.info("Cannot load file " + filePath);
        } finally {
            try {
                inputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return text;
    }

    /**
     * Save .txt file
     * @param filePath path to the file.
     * @param text String of text.
     */
    static void saveText(String filePath, String text) {
        Logger logger = Logger.getLogger("Analyzer");
        logger.setLevel(Level.INFO);
        logger.fine( "Saving file " + filePath);

        BufferedWriter outputFile = null;
        try {
            outputFile = new BufferedWriter(new FileWriter(filePath));
            outputFile.write(text);
            outputFile.close();
        } catch (IOException e) {
            logger.info("Cannot save file " + filePath);
        } finally {
            try {
                outputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
