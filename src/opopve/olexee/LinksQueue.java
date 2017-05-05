package opopve.olexee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Special ArrayList. This queue does not implements {@link java.util.Queue}.
 * {@link opopve.olexee.Pair} added to the Queue comparing to existing Pairs in the Queue.
 * If HTTP link of new Pair does not equal to link of any Pair of the Queue than this new Pair just added to the Queue.
 * If HTTP link of new Pair equals to link of any Pair of the Queue than only link to local file from new Pair will be
 * added to existing Pair in the Queue.
 */
public class LinksQueue implements Iterable<Pair> {
    private ArrayList<Pair> queue = new ArrayList<>();

    @Override
    public Iterator<Pair> iterator() {
        return new LinksQueueIterator(queue);
    }

    /**
     * The method returns length of the queue
     *
     * @return int
     */
    public int size() {
        return queue.size();
    }

    /**
     * The method adds HTTP link to file source and local link for output to instance of class {@link Pair}
     * and then to the queue.
     *
     * @param httpLink  - HTTP link to file source
     * @param localLink - local link for output
     */
    public void add(String httpLink, String localLink) {

        Pair newPair = new Pair(httpLink, localLink.toUpperCase());
        if (!queue.isEmpty()) {
            for (int i = 0; i < queue.size(); i++) {
                Pair curPair = queue.get(i);
                if (curPair.getLink().equals(newPair.getLink())) {
                    if (!curPair.getSaveAs().contains(localLink.toUpperCase()))
                        curPair.addDestination(localLink.toUpperCase());
                    queue.remove(i);
                    newPair = curPair;
                    break;
                }
            }
        }
        queue.add(newPair);
    }

    /**
     * Retrieves, but does not remove, the head of this queue.
     *
     * @return first element of the Queue.
     * @throws NoSuchElementException
     */

    public synchronized Pair poll() throws NoSuchElementException {
        if (queue.isEmpty()) throw new NoSuchElementException();
        else {
            Pair res = queue.get(0);
            queue.remove(0);
            return res;
        }
    }

    /**
     * The method returns instance of the class.
     */

    public ArrayList<Pair> getQueue() {
        return queue;
    }

    /**
     * Writes all elements of the queue to console
     */
    public void queueToDisplay() {
        if (queue.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            for (Pair p : queue) {
                System.out.println(p);
            }
        }
    }

    class LinksQueueIterator implements Iterator<Pair> {
        int i = 0;
        private ArrayList<Pair> queue;

        public LinksQueueIterator(ArrayList<Pair> queue) {
            this.queue = queue;
        }

        @Override
        public boolean hasNext() {
            return i < queue.size();
        }

        @Override
        public Pair next() {
            return queue.get(i++);
        }
    }
}
