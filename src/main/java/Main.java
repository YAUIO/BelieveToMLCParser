import Entities.*;
import Parsers.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static File inputData;
    private static List<File> inputMLC;

    public static void main(String[] args) throws InterruptedException {
        Init.setDB("Names");
        File f = new File("db.xlsx");
        if (f.exists()) {
            XLSXtoDB.load(f);
        }

        if (new File("paths.csv").exists() && selectPath()) {
            readPaths();
            System.out.println("Reading from: " + inputData.getAbsolutePath());
            System.out.println("Mlc files: " + inputMLC);
        } else {
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
            writePaths();
            System.out.println("Reading from: " + inputData.getAbsolutePath());
            System.out.println("Mlc files: " + inputMLC);
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

    private static boolean selectPath() throws InterruptedException {
        JDialog dial = new JDialog();
        Person.addListeners(dial);
        dial.setLayout(new GridLayout(2,1));
        dial.setSize(200,100);
        dial.setPreferredSize(new Dimension(200,100));
        JButton but = new JButton("Use previous run files");
        JButton but1 = new JButton("Choose new files");
        dial.add(but);
        dial.add(but1);
        dial.pack();
        dial.setVisible(true);

        File f = new File("paths.csv");

        Thread current = Thread.currentThread();

        but.addActionListener(e -> {
            synchronized (current) {
                current.interrupt();
            }
            dial.dispose();
        });

        but1.addActionListener(e -> {
            if (f.exists()) f.delete();
            dial.dispose();
            synchronized (current) {
                current.interrupt();
            }
        });

        synchronized (current) {
            try {
                current.wait();
            } catch (InterruptedException _) {
            }
        }

        return f.exists();
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
                } catch (InterruptedException _) {
                }
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
                } catch (InterruptedException _) {
                }
            }
        };

        Thread t = new Thread(task);
        t.start();
        t.join();
    }

    private static void writePaths() {
        File outF = new File("paths.csv");
        if (outF.exists()) outF.delete();
        try {
            FileOutputStream fos = new FileOutputStream(outF);
            PrintWriter out = new PrintWriter(fos, true);

            out.println(inputData.getAbsolutePath());
            for (File f : inputMLC) {
                out.println(f.getAbsolutePath());
            }
        } catch (Exception _) {
        }
    }

    private static void readPaths() {
        File outF = new File("paths.csv");

        try {
            FileInputStream fos = new FileInputStream(outF);
            BufferedReader in = new BufferedReader(new InputStreamReader(fos));

            inputData = new File(in.readLine());
            inputMLC = new ArrayList<>();
            while (in.ready()) {
                inputMLC.add(new File(in.readLine()));
            }
        } catch (Exception _) {
        }
    }
}
