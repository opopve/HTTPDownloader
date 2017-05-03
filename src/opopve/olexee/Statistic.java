package opopve.olexee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private boolean areDownloadsFinished = false;
    private long startTime;

    public Statistic(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        showInfo();
    }

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

    private void msToUsefulData(long millis) {
        int seconds = (int) (millis / 1000L);
        size = (bytesDownloaded < 1048576 ? bytesDownloaded / 1024 + " Kb" : bytesDownloaded / 1048576 + " Mb");
        totalSize = (bytesDownloaded < 1048576 ? bytesToDownload / 1024 + " Kb" : bytesToDownload / 1048576 + " Mb");
//        size = (totalBytesDownloaded < 1048576 ? totalBytesDownloaded / 1024 + " Kb" : (float) totalBytesDownloaded / 1048576 + " Mb");
        kBPerSecond = (seconds == 0 ? 0 : ((float) bytesDownloaded / 1024 / seconds));
        h = seconds / 3600;
        m = seconds % 3600 / 60;
        s = seconds % 3600 % 60;
    }

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
        System.out.printf(show.append(statusBar).toString(), nrOfFilesDownloaded, nrOfFilesToDwnl, size, totalSize, kBPerSecond, h, m, s, percent);
    }

    public void setNrOfFilesToDwnl(int nrOfFilesToDwnl) {
        this.nrOfFilesToDwnl = nrOfFilesToDwnl;
    }

    public void setSizeAndQuantityToDownload(LinksQueue linksSource) {
        this.nrOfFilesToDwnl = linksSource.size();
        for (Pair p : linksSource) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) (new URL(p.getLink())).openConnection();
                bytesToDownload += connection.getContentLength();
                connection.disconnect();
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

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

    public void onDownload(int bytesDownloaded, int bytesAvailable, String httpLink, String localPath, int nrOfFiles) {
        nrOfFilesDownloaded++;
        this.bytesDownloaded += bytesDownloaded;
        toLogFile.append(FileUtils.getDateTime(true))
                .append("\tSuccess: ")
                .append(bytesDownloaded)
                .append(" of ")
                .append(bytesAvailable)
                .append(" Bytes downloaded from link '")
                .append(httpLink)
                .append("' to '")
                .append(localPath);
        if (nrOfFiles == 1) toLogFile.append("'.");
        else toLogFile.append("' and in ")
                .append(nrOfFiles - 1)
                .append(" other files.");
        toLogFile.append("\n");
        showInfo();
    }

    private void createLog() {
        FileUtils.makeLogFile(outputFolder, toLogFile);
    }
}