package opopve.olexee;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class with static methods which allow to create and edit files.
 */
public class FileUtils {
    /**
     * The method generates new file with local links from original file, that contains only HTTP links. For example:
     * original file contains link http:/example.com/path/file.test. The method will create new file with row:
     * http:/example.com/path/file.test pathForContent/path/file.test.
     * @param pathToLinks - link to text file with HTTP link.
     * @param pathForContent - addition to local links
     * @throws IOException If an I/O error occurs
     */
    public static void httpToLocal(String pathToLinks, String pathForContent) throws IOException {
        File fileFrom = new File(pathToLinks);
        File fileTo = new File(fileFrom.getParent() + "\\mod_" + fileFrom.getName());
        fileTo.createNewFile();
        String line;
        String line2;

        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(fileFrom));
            bw = new BufferedWriter(new FileWriter(fileTo));
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    StringBuilder sb = new StringBuilder(line);
                    sb.append(" ").append(pathForContent).append(new URL(line).getFile().replace('/', '\\').substring(1));
                    bw.append(sb);
                    bw.newLine();
                }
            }
        } finally {
            br.close();
            bw.close();
        }
    }

    public static void fileCopy(String sourcePath, String destinationPath) throws IOException {
        byte[] buffer = new byte[10240];
        int count;
        BufferedInputStream localSource = null;
        BufferedOutputStream localOutput = null;
        try {
            localSource = new BufferedInputStream(new FileInputStream(sourcePath));
            localOutput = new BufferedOutputStream(new FileOutputStream(destinationPath));
            while ((count = localSource.read(buffer, 0, buffer.length)) != -1) {
                localOutput.write(buffer, 0, count);
            }
        } finally {
            localOutput.close();
            localSource.close();
        }
    }

    public static void fileCopy2(String sourcePath, String destinationPath) throws IOException {
        long start = System.nanoTime();
        byte[] buffer = new byte[1024];
        int count;
        FileInputStream localSource = null;
        FileOutputStream localOutput = null;
        try {
            localSource = new FileInputStream(sourcePath);
            localOutput = new FileOutputStream(destinationPath);
            while ((count = localSource.read(buffer)) != -1) {
                localOutput.write(buffer, 0, count);
            }
        } finally {
            localOutput.close();
            localSource.close();
            System.out.println((System.nanoTime() - start));
        }
    }

    public static void makeFolders(ArrayList<String> localPaths) throws IOException {
        StringBuilder sb;
        File f;
        for (String path : localPaths) {
            sb = new StringBuilder(path);
            f = new File(sb.substring(0, sb.lastIndexOf("\\")));
            f.mkdirs();
        }
    }

    public static LinksQueue readLinks(String outputFolder, String fileWithLinks) throws IOException {
        LinksQueue queue = new LinksQueue();
        File fileFrom = new File(fileWithLinks);
        String line;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileFrom));
            while ((line = br.readLine()) != null) {
                String[] links = line.split(" ");
                if (!line.equals("")) queue.add(links[0], outputFolder + links[1]);
            }
        } finally {
            br.close();
        }
        return queue;
    }

    public static void downloadFile(String httpLink, ArrayList<String> localPaths, Statistic stat) {
        int count;
        int bytesDownloaded = 0;
        int bytesAvailable = -1;
        byte[] buffer = new byte[1024];

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(httpLink).openStream());
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localPaths.get(0)))
        ) {
            HttpURLConnection connection = (HttpURLConnection) (new URL(httpLink)).openConnection();
            bytesAvailable = connection.getContentLength();
            connection.disconnect();
            while ((count = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, count);
                bytesDownloaded += count;
            }
            int nrOfFiles = 1;
            StringBuilder localPathsToString = new StringBuilder(localPaths.get(0));
            if (localPaths.size() > 1) {
                for (int j = 1; j < localPaths.size(); j++) {
                    localPathsToString.append(", ").append(localPaths.get(j));
                    try {
                        FileUtils.fileCopy(localPaths.get(0), localPaths.get(j));
                        nrOfFiles = j;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            stat.onDownload(bytesDownloaded, bytesAvailable, httpLink, localPathsToString.toString(), nrOfFiles);
        } catch (IOException e) {
            stat.onError(httpLink, localPaths.get(0), e.getLocalizedMessage());
        }
    }

    public static void makeLogFile(String pathToLinks, StringBuilder toLogFile) {
        try (FileWriter fw = new FileWriter(pathToLinks + "\\details" + getDateTime(false) + ".log", false)) {
            fw.write(toLogFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDateTime(boolean showDelimiters) {
        SimpleDateFormat sdf = new SimpleDateFormat(showDelimiters ? "yyyy.MM.dd HH:mm:ss" : "yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
