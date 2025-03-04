package Parsers;

import Entities.BelieveEntity;
import Entities.Person;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FixSameTNameAuthorDifferentArtist {
    public static ArrayList<BelieveEntity> fix(ArrayList<BelieveEntity> source) {
        HashMap<String, ArrayList<BelieveEntity>> map = new HashMap<>();
        ArrayList<BelieveEntity> ret = new ArrayList<>();

        for (BelieveEntity ent : source) {
            String s = generateKey(ent);
            map.computeIfAbsent(s, k -> new ArrayList<>());
            map.get(s).add(ent);
        }

        for (ArrayList<BelieveEntity> list : map.values()) {
            if (list.size() > 1) {
                HashMap<String, BelieveEntity> check = new HashMap<>();
                for (BelieveEntity e : list) {
                    check.put(generateCheckKey(e), e);
                }
                if (check.keySet().size() > 1) {
                    list.clear();
                    list.addAll(check.values());
                    raiseQuestion(list, ret);
                } else {
                    ret.add(list.getFirst());
                }
            } else {
                ret.add(list.getFirst());
            }
        }

        return ret;
    }

    private static void raiseQuestion(ArrayList<BelieveEntity> list, ArrayList<BelieveEntity> ret) {
        JDialog dial = new JDialog();
        Person.addListeners(dial);
        dial.setLayout(new BorderLayout());
        dial.setSize(new Dimension(1080, 800));
        dial.setPreferredSize(new Dimension(1080, 800));
        JLabel jl = new JLabel("Choose which info to preserve for: " + generateKey(list.getFirst()) + " (Artist:Title:Version)");
        JPanel line = new JPanel(new GridLayout(1, 3));
        JTextArea ja = new JTextArea("Type Custom Author Here");
        JTextArea jc = new JTextArea("Type Custom Composer Here");
        JButton submit = new JButton("SUBMIT");
        line.add(ja);
        line.add(jc);
        line.add(submit);
        dial.add(line, BorderLayout.SOUTH);
        JScrollPane sp = new JScrollPane();
        JPanel viewport = new JPanel(new BorderLayout());
        JPanel jp = new JPanel(new GridLayout(1, list.size()));
        viewport.add(jp, BorderLayout.CENTER);
        sp.setViewportView(viewport);
        for (BelieveEntity one : list) {
            JTextArea jt1 = new JTextArea(formatEnt(one));
            jt1.setEditable(false);
            Dimension d = new Dimension(280, 600);
            jt1.setSize(d);
            jt1.setPreferredSize(d);
            jt1.setMinimumSize(d);
            jp.add(jt1);
        }

        Thread current = Thread.currentThread();

        JPanel jp1 = new JPanel(new GridLayout(1, list.size()));
        viewport.add(jp1, BorderLayout.SOUTH);

        for (int i = 0; i < list.size(); i++) {
            BelieveEntity one = list.get(i);
            JButton but = new JButton(String.valueOf(i));
            jp1.add(but);
            but.addActionListener(e -> {
                ret.add(one);
                for (BelieveEntity ent : list) {
                    ent.Track_author = one.Track_author;
                    ent.Track_composer = one.Track_composer;
                    ent.Track_Featuring = one.Track_Featuring;
                    ret.add(ent);
                }
                synchronized (current) {
                    current.interrupt();
                }
                dial.dispose();
            });
            Dimension d = new Dimension(280, 40);
            but.setSize(d);
            but.setPreferredSize(d);
            but.setMinimumSize(d);
        }

        submit.addActionListener(e -> {
            if (!jc.getText().equals("Type Custom Composer Here")
                    && !ja.getText().equals("Type Custom Author Here")
                    && !jc.getText().isBlank() && !jc.getText().isEmpty()
                    && !ja.getText().isBlank() && !ja.getText().isEmpty()) {
                System.out.println("Set author to: \"" + ja.getText() + "\", composer to: \"" + jc.getText() + "\"");
                BelieveEntity one = list.getFirst();
                one.Track_composer = jc.getText();
                one.Track_author = ja.getText();
                ret.add(one);
                for (BelieveEntity ent : list) {
                    ent.Track_author = one.Track_author;
                    ent.Track_composer = one.Track_composer;
                    ent.Track_Featuring = one.Track_Featuring;
                    ret.add(ent);
                }
                synchronized (current) {
                    current.interrupt();
                }
                dial.dispose();
            }
        });

        dial.add(jl, BorderLayout.NORTH);
        dial.add(sp, BorderLayout.CENTER);
        dial.pack();
        dial.setVisible(true);

        synchronized (current) {
            try {
                current.wait();
            } catch (InterruptedException _) {
            }
        }
    }

    private static String generateKey(BelieveEntity ent) {
        return ent.Artist + ":" + ent.Title + ":" + ent.Version;
    }

    private static String generateCheckKey(BelieveEntity ent) {
        if (ent.Track_author != null) {
            return ent.Artist + ":" + ent.Title + ":" + ent.Version + ":" + ent.Track_composer.trim() + ":" + ent.Track_author.trim();
        }
        return ent.Artist + ":" + ent.Title + ":" + ent.Version + ":" + ent.Track_composer.trim();
    }

    private static String formatEnt(BelieveEntity ent) {
        return "Short info: \n" +
                "Artist: " + ent.Artist + "\n" +
                "Title: " + ent.Title + "\n" +
                "Version: " + ent.Version + "\n" +
                "Author: " + ent.Track_author + "\n" +
                "Composer: " + ent.Track_composer + "\n\n" +
                "Full info: \n" + ent.toString().replaceAll(",", "\n");
    }
}
