import Entities.*;
import Parsers.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static File inputData;
    private static List<File> inputMLC;

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

        try {
            selectDataFiles();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ArrayList<BelieveEntity> sourceData = XLSXBelieveToList.parse(inputData);

        ArrayList<MLCEntry> writtenMlc = new ArrayList<>();
        for (File file : inputMLC) {
            writtenMlc.addAll(XLSXMLCToList.parse(file));
        }

        ArrayList<MLCEntry> outData = BelieveToMLC.convert(sourceData);

        System.out.println("Total MLC: " + outData.size());
        outData = RemoveNonUnique.call(outData, writtenMlc);
        System.out.println("Unsubmitted MLC: " + outData.size());

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

    private static void selectDataFiles() throws InterruptedException {
        Runnable task = () -> {
            File dir = new File("");
            JFileChooser jfc = new JFileChooser(dir.getAbsolutePath());
            jfc.setMultiSelectionEnabled(true);
            JDialog dial = new JDialog();
            dial.setTitle("Choose input MLC files");
            jfc.setDialogTitle("Choose input MLC files");
            Person.addListeners(dial);
            dial.setContentPane(jfc);
            dial.pack();
            dial.setVisible(true);

            Thread current = Thread.currentThread();

            jfc.addActionListener(e -> {
                if (e.getActionCommand().equals("ApproveSelection")) {
                    inputMLC = List.of(jfc.getSelectedFiles());
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
