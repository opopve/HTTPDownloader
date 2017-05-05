package opopve.olexee;

import java.util.HashMap;

/**
 * Class that collects data from other classes and writes useful information to console.
 */
public class Statistic {
    private String size;
    private String totalSize;
    private float kBPerSecond;
    private StringBuilder toLogFile = new StringBuilder();
    private String outputFolder;
    private int h;
    private int m;
    private int s;
    private int nrOfFilesToDwnl = 0;
    private int nrOfFilesDownloaded = 0;
    private int bytesSavedOnDisc = 0;
    private int bytesDownloaded = 0;
    private int bytesToDownload = 0;
    private HashMap<String, Integer> bytesAvailable = new HashMap<>();
    private boolean areDownloadsFinished = false;
    private long startTime;

    /**
     * Constructor
     *
     * @param outputFolder - path to log file
     */
    public Statistic(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * gets start time
     *
     * @param startTime - start time from main thread
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
        showInfo();
    }

    /**
     * returns status of downloads
     *
     * @return true if all threads {@link Downloader} finished.
     */
    public boolean getDwnlStatus() {
        return areDownloadsFinished;
    }

    public void setDwnlStatus(boolean areDownloadsFinished) {
        this.areDownloadsFinished = areDownloadsFinished;
        if (areDownloadsFinished) {
            System.out.println("\nDownloads completed. See 'details.log' for details.");
            createLog();
        }
    }

    /**
     * The method converts milliseconds to hours, minutes, seconds. Calculates speed (in Mb/s or Kb/s) from size and
     * time. Creates row that shows size in MBytes or KBytes.
     *
     * @param millis - milliseconds for converting
     */
    private void msToUsefulData(long millis) {
        int seconds = (int) (millis / 1000L);
        size = (bytesDownloaded < 1048576 ? bytesDownloaded / 1024 + " Kb" : bytesDownloaded / 1048576 + " Mb");
        totalSize = (bytesDownloaded < 1048576 ? bytesToDownload / 1024 + " Kb" : bytesToDownload / 1048576 + " Mb");
        kBPerSecond = (seconds == 0 ? 0 : ((float) bytesDownloaded / 1024 / seconds));
        h = seconds / 3600;
        m = seconds % 3600 / 60;
        s = seconds % 3600 % 60;
    }

    /**
     * Writes to console number of files to download and last file downloaded, size to download and downloaded size,
     * average speed of download, time of downloads and progress bar.
     */
    private void showInfo() {
        long currTime = System.currentTimeMillis() - startTime;
        msToUsefulData(currTime);
        String statusBar;
        float percent = (float) bytesDownloaded * 100 / bytesToDownload;
        StringBuilder show = new StringBuilder("%d of %d files (%s of %s) downloaded on average speed %.2f Kb/s for %d h., %d m. and %d s. %.0f%% ");
        if (bytesDownloaded == 0) {
            statusBar = "[ _ _ _ _ _ _ _ _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload / 10) {
            statusBar = "[ X _ _ _ _ _ _ _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 2 / 10) {
            statusBar = "[ X X _ _ _ _ _ _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 3 / 10) {
            statusBar = "[ X X X _ _ _ _ _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 4 / 10) {
            statusBar = "[ X X X X _ _ _ _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 5 / 10) {
            statusBar = "[ X X X X X _ _ _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 6 / 10) {
            statusBar = "[ X X X X X X _ _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 7 / 10) {
            statusBar = "[ X X X X X X X _ _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 8 / 10) {
            statusBar = "[ X X X X X X X X _ _ ]";
        } else if (bytesDownloaded <= bytesToDownload * 9 / 10) {
            statusBar = "[ X X X X X X X X X _ ]";
        } else {
            statusBar = "[ X X X X X X X X X X ]";
        }
        System.out.print("\r");
        System.out.print("\r");
        System.out.printf(show.append(statusBar).toString(), nrOfFilesDownloaded, nrOfFilesToDwnl, size, totalSize, kBPerSecond, h, m, s, percent);
    }

    /**
     * @param nrOfFilesToDwnl - sets number of files to download
     */
    public void setNrOfFilesToDwnl(int nrOfFilesToDwnl) {
        this.nrOfFilesToDwnl = nrOfFilesToDwnl;
    }

    /**
     * Calculates total size and number of files to download.
     *
     * @param linksSource - instance of class {@link LinksQueue} from which method gets links to estimate total size.
     */
    public void setSizeAndQuantityToDownload(LinksQueue linksSource) {
        this.nrOfFilesToDwnl = linksSource.size();
        for (Pair p : linksSource) {
            String link = p.getLink();
            int size = FileUtils.fileSizeOnHTTP(link);
            bytesToDownload += size;
            this.bytesAvailable.put(link, size);
        }
    }

    /**
     * The method writes error description to log file.
     *
     * @param httpLink  - possible trouble HTTP link
     * @param localPath - possible trouble local link
     * @param excpMsg   - error message
     */
    public synchronized void onError(String httpLink, String localPath, String excpMsg) {
        toLogFile.append(FileUtils.getDateTime(true))
                .append("\tError: Can't download link '")
                .append(httpLink)
                .append("' to '")
                .append(localPath)
                .append("'. ")
                .append(excpMsg)
                .append("\n");
    }

    /**
     * The method writes information to console and to log file.
     *
     * @param bytesDownloaded - size of downloaded file
     * @param httpLink        - source link
     * @param localPath       - local link - destination
     * @param nrOfFiles       -
     */
    public void onDownload(int bytesDownloaded, String httpLink, String localPath, int nrOfFiles) {
        nrOfFilesDownloaded++;
        this.bytesDownloaded += bytesDownloaded;
        toLogFile.append(FileUtils.getDateTime(true))
                .append("\tSuccess: ")
                .append(bytesDownloaded)
                .append(" of ")
                .append(bytesAvailable.get(httpLink))
                .append(" Bytes downloaded from link '")
                .append(httpLink)
                .append("' to '")
                .append(localPath)
                .append(".\n");
        showInfo();
    }

    /**
     * Creates log file
     */
    private void createLog() {
        FileUtils.makeLogFile(outputFolder, toLogFile);
    }
}