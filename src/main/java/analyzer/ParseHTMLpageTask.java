package analyzer;

/**
 * Task for exexutor which, load .html file from filePathIn, parse it and save to .txt file in filePathOut.
 * @author Kmike
 */
class ParseHTMLpageTask implements Task {

    private final String filePathIn;
    private final String filePathOut;

    ParseHTMLpageTask(String filePathIn, String filePathOut) {
        this.filePathIn = filePathIn;
        this.filePathOut = filePathOut;
    }

    /**
     * Load .html file from filePathIn, parse it and save to .txt file in filePathOut.
     */
    @Override
    public void doWork() {
        String page = Utils.loadPage(filePathIn);
        String pageText = Parser.parseHTMLString(page);
        Utils.saveText(filePathOut, pageText);
    }
}



