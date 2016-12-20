package crawler;

/**
 * Simple link structure: URL + current depth
 */
class Link {
    private final String url;
    private final int depth;

    public Link(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }
}
