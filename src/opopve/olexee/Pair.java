package opopve.olexee;

import java.util.ArrayList;

/**
 * Type consists of HTTP link to download FROM and local link(s) to save file TO.
 */
public class Pair {
    private String from;
    private ArrayList<String> to = new ArrayList<>();

    /**
     * Constructor
     *
     * @param httpLink  - HTTP link to file source
     * @param localLink - local link for output
     */
    Pair(String httpLink, String localLink) {
        from = httpLink;
        to.add(localLink);
    }

    /**
     * The method adds additional local link to List.
     *
     * @param localLink - local link for output
     */
    public void addDestination(String localLink) {
        to.add(localLink);
    }

    public String getLink() {
        return from;
    }

    public ArrayList<String> getSaveAs() {
        return to;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(from).append(" will be downloaded to: ");
        for (String s : to) {
            sb.append("\n\t\t--> " + s);
        }
        return sb.toString();
    }
}
