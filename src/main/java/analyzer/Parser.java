package analyzer;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parser for getting text from html page.
 * @author  Kmike
 */
class Parser {

    /**
     * Get text from html page with See <a href="https://jsoup.org/">jsoup</a>.
     * @param htmlString: input html page as string.
     * @return parsed html page as string.
     */
    static String parseHTMLString(String htmlString) {
        Logger logger = Logger.getLogger("Analyzer");
        logger.setLevel(Level.INFO);
        logger.fine( "Parsing string");

        Document doc = Jsoup.parse(htmlString);

        return doc.text();
    }
}
