import Entities.BlackBeatEntry;
import Entities.Init;
import Entities.MLCEntry;
import Entities.Person;
import Parsers.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;

public class Main {
    private static File inputData;

    public static void main(String[] args) {
        Init.setDB("Names");
        File f = new File("db.xlsx");
        if (f.exists()) {
            XLSXtoDB.load(f);
        }

        try {
            selectDataFile();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ArrayList<BlackBeatEntry> sourceData = BlackBeatToList.parse(inputData);
        ArrayList<MLCEntry> outData = BlackBeatToMLC.convert(sourceData);
        String path = "new.xlsx";
        MLCListToXLSX.record(new File(path), outData);

        if (f.exists()) if (!f.delete()) System.err.println("Couldn't delete db.xlsx");

        DBtoXLSX.write(f);
    }

    private static void selectDataFile() throws InterruptedException {
        Runnable task = () -> {
            File dir = new File("");
            JFileChooser jfc = new JFileChooser(dir.getAbsolutePath());
            JDialog dial = new JDialog();
            dial.setTitle("Choose input data");
            jfc.setDialogTitle("Choose input data");
            Person.addListeners(dial);
            dial.setContentPane(jfc);
            dial.pack();
            dial.setVisible(true);

            Thread current = Thread.currentThread();

            jfc.addActionListener(e -> {
                if (e.getActionCommand().equals("ApproveSelection")) {
                    inputData = jfc.getSelectedFile();
                    synchronized (current) {
                        current.interrupt();
                    }
                    dial.dispose();
                } else if (e.getActionCommand().equals("CancelSelection")) {
                    System.exit(1);
                }
            });

            synchronized (current) {
                try {
                    current.wait();
                } catch (InterruptedException _) {}
            }
        };

        Thread t = new Thread(task);
        t.start();
        t.join();
    }
}
