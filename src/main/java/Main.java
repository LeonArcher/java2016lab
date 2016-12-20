import analyzer.Analyzer;
import crawler.Crawler;
import crawler.ParallelCrawler;

import java.util.Arrays;


public class Main {
    public static void main(String [] args) throws Exception {
        Analyzer analyzer = new Analyzer(5, System.getProperty("user.dir"));
        Crawler crawler = new ParallelCrawler(5, analyzer);

        analyzer.start();
        crawler.crawl(Arrays.asList("http://www.yandex.ru", "http://lenta.ru"));
        crawler.crawl("http://www.mail.ru", 1);
        crawler.crawl(Arrays.asList("http://www.yandex.ru", "http://lenta.ru"));
        crawler.crawl("http://www.rambler.ru", 1);

        analyzer.interrupt();
    }
}
