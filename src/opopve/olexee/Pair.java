package opopve.olexee;

import java.util.ArrayList;

/**
 * Type consists of HTTP link to download FROM and local link(s) to save file TO.
 */
public class Pair {
    private String from;
    private ArrayList<String> to = new ArrayList<>();


    Pair(String httpLink, String localLink) {
        from = httpLink;
        to.add(localLink);
    }

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
