package opopve.olexee;

/**
 * Thread downloading a file
 */
public class Downloader implements Runnable {
    private LinksQueue queue;
    private Statistic stat;
    private int myNumber;
    private boolean[] thrStatuses;

    Downloader(LinksQueue queue, Statistic stat, int thrNr, boolean[] thrStatuses) {
        this.queue = queue;
        this.stat = stat;
        this.myNumber = thrNr;
        this.thrStatuses = thrStatuses;
    }

    @Override
    public void run() {
        while (queue.size() > 0) {
            Pair p = queue.poll();
            FileUtils.downloadFile(p.getLink(), p.getSaveAs(), stat);
        }
        thrStatuses[myNumber] = true;
        boolean areThreadsCompleted = true;
        for (boolean status : thrStatuses) {
            areThreadsCompleted = areThreadsCompleted && status;
        }
        stat.setDwnlStatus(areThreadsCompleted);
    }
}