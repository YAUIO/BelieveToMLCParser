package Parsers;

import Entities.BelieveDBEntry;
import Entities.BelieveEntity;
import Entities.Init;
import Entities.Person;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FixSameTNameAuthorDifferentArtist {
    public static ArrayList<BelieveEntity> fix(ArrayList<BelieveEntity> source) {
        HashMap<String, ArrayList<BelieveEntity>> map = new HashMap<>();
        ArrayList<BelieveEntity> ret = new ArrayList<>();

        for (BelieveEntity ent : source) {
            String s = generateKey(ent);
            BelieveDBEntry dbe = Init.getEntityManager().createQuery("SELECT e from BelieveDBEntry e WHERE e.keys=:key", BelieveDBEntry.class).setParameter("key", s).getSingleResultOrNull();
            if (dbe == null) {
                map.computeIfAbsent(s, k -> new ArrayList<>());
                map.get(s).add(ent);
            } else {
                ent.Track_author = dbe.author;
                ent.Track_composer = dbe.composer;
                ent.Track_Featuring = null;
                ret.add(ent);
            }
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

        writeToDB(ret);

        return ret;
    }

    private static void writeToDB(ArrayList<BelieveEntity> ret) {
        HashMap<String, BelieveEntity> parseMap = new HashMap<>();

        for (BelieveEntity e : ret) {
            parseMap.put(generateKey(e), e);
        }

        for (BelieveEntity e : parseMap.values()) {
            EntityManager em = Init.getEntityManager();
            if (em.createQuery("SELECT e from BelieveDBEntry e WHERE e.keys=:key").setParameter("key", generateKey(e)).getSingleResultOrNull() == null) {
                em.getTransaction().begin();
                BelieveDBEntry entry = new BelieveDBEntry();
                entry.keys = generateKey(e);
                entry.author = e.Track_author;
                entry.composer = e.Track_composer;
                em.persist(entry);
                em.getTransaction().commit();
            }
        }
    }

    private static void raiseQuestion(ArrayList<BelieveEntity> list, ArrayList<BelieveEntity> ret) {
        JDialog dial = new JDialog();
        Person.addListeners(dial);
        dial.setLayout(new BorderLayout());
        dial.setSize(new Dimension(1080, 840));
        dial.setPreferredSize(new Dimension(1080, 840));
        JLabel jl = new JLabel("Choose which info to preserve for: " + generateKey(list.getFirst()) + " (Artist:Title:Version)");
        JPanel line = new JPanel(new GridLayout(2, 3));
        JTextArea ja = new JTextArea("");
        JTextArea jc = new JTextArea("");
        JButton submit = new JButton("SUBMIT");
        line.add(new JLabel("Type Custom Artist(s) below"));
        line.add(new JLabel("Type Custom Composer(s) below"));
        line.add(new JLabel(""));
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
                writeToDB(ret);
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
            if (
                    !jc.getText().isBlank() && !jc.getText().isEmpty()
                    && !ja.getText().isBlank() && !ja.getText().isEmpty()
            ) {
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
                writeToDB(ret);
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
        return ent.Track_artist_name + ":" + ent.Track_title + ":" + ent.Track_version;
    }

    private static String generateCheckKey(BelieveEntity ent) {
        if (ent.Track_author != null) {
            return ent.Track_artist_name + ":" + ent.Track_title + ":" + ent.Track_version + ":" + ent.Track_composer.trim() + ":" + ent.Track_author.trim();
        }
        return ent.Track_artist_name + ":" + ent.Track_title + ":" + ent.Track_version + ":" + ent.Track_composer.trim();
    }

    private static String formatEnt(BelieveEntity ent) {
        return "Short info: \n" +
                "Artist: " + ent.Track_artist_name + "\n" +
                "Title: " + ent.Track_title + "\n" +
                "Version: " + ent.Track_version + "\n" +
                "Author: " + ent.Track_author + "\n" +
                "Composer: " + ent.Track_composer + "\n\n" +
                "Full info: \n" + ent.toString().replaceAll(",", "\n");
    }
}
