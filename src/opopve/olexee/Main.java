package opopve.olexee;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.print("Download initialisation. Please wait... ");
        if (args.length < 10) args = new String[]{"5", "E:\\tmp\\", "L:\\Work\\10.Java\\20170503\\mod_links.txt"};
//        if (args.length < 10) args = new String[]{"5", "E:\\tmp\\", "E:\\Work\\10.Java\\20170503\\20170503\\mod_links.txt"};
        int nrOfThreads = Integer.valueOf(args[0]);
        String outputFolder = args[1];
        String links = args[2];

        LinksQueue queue = null;
        Statistic stat = new Statistic(outputFolder);
        boolean[] thrStatuses = new boolean[nrOfThreads];

        try {
            queue = FileUtils.readLinks(outputFolder, links);
//            stat.setNrOfFilesToDwnl(queue.size());
            stat.setSizeAndQuantityToDownload(queue);
            for (Pair p : queue) {
                FileUtils.makeFolders(p.getSaveAs());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        stat.setStartTime(System.currentTimeMillis());
        for (int i = 0; i < nrOfThreads; i++) {
            thrStatuses[i] = false;
            new Thread(new Downloader(queue, stat, i, thrStatuses)).start();
        }
    }
}
