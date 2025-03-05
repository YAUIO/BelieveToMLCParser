package Parsers;

import Entities.BelieveDBEntry;
import Entities.BelieveEntity;
import Entities.Init;
import Entities.Person;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FixSameTNameAuthorDifferentArtist {
    public static ArrayList<BelieveEntity> fix(ArrayList<BelieveEntity> source) {
        HashMap<String, ArrayList<BelieveEntity>> map = new HashMap<>();
        ArrayList<BelieveEntity> ret = new ArrayList<>();
        HashSet<String> autosave = plineCheckGet();
        File f = new File("autosave.csv");
        if (!f.exists()) {
            try {
                createPLINE();
            } catch (IOException _) {
            }
        }
        int inputSize = source.size();

        for (BelieveEntity ent : source) {
            String s = generateKey(ent);
            BelieveDBEntry dbe = Init.getEntityManager().createQuery("SELECT e from BelieveDBEntry e WHERE e.keys=:key", BelieveDBEntry.class).setParameter("key", s).getSingleResultOrNull();
            if (dbe == null || dbe.composer_artist.isEmpty() || !dbe.composer_artist.contains(generatePair(ent))) {
                map.computeIfAbsent(s, k -> new ArrayList<>());
                map.get(s).add(ent);
            } else {
                if (dbe.composer_artist.size() == 1) {
                    String pair = dbe.composer_artist.iterator().next();
                    ent.Track_composer = pair.substring(0, pair.indexOf(":"));
                    ent.Track_author = pair.substring(pair.indexOf(":") + 1);
                }
                ent.Track_Featuring = null;
                ret.add(ent);
            }
        }

        int c = 0;
        ArrayList<ArrayList<BelieveEntity>> raiseLists = new ArrayList<>();
        for (ArrayList<BelieveEntity> list : map.values()) {
            if (list.size() > 1) {
                HashMap<String, BelieveEntity> check = new HashMap<>();
                for (BelieveEntity e : list) {
                    check.put(generateCheckKey(e), e);
                }
                if (check.keySet().size() > 1) {
                    list.clear();
                    list.addAll(check.values());
                    if (autosave.contains(list.getFirst().Track_P_Line.toLowerCase())) {
                        c++;
                    } else {
                        raiseLists.add(new ArrayList<>(list));
                    }
                } else {
                    ret.add(list.getFirst());
                }
            } else {
                ret.add(list.getFirst());
            }
        }
        System.out.println("Autosaved according to cfg: " + c + ", source data size was: " + inputSize);

        int i = 1;
        for (ArrayList<BelieveEntity> list : raiseLists) {
            System.out.println("Manual author check: " + i + "/" + raiseLists.size() + 1);
            raiseQuestion(list, ret);
            i++;
        }

        writeToDB(ret);

        return ret;
    }

    private static void createPLINE() throws IOException {
        File src = new File("autosave.csv");
        if (src.exists()) return;
        src.createNewFile();

        FileOutputStream fos = new FileOutputStream(src);
        PrintWriter out = new PrintWriter(fos, true);
        out.println("//Place here \"Track P Line\" values from table to automatically skip with save on them");
    }


    private static HashSet<String> plineCheckGet() {
        File src = new File("autosave.csv");
        if (!src.exists()) return new HashSet<>();

        try (FileInputStream fis = new FileInputStream(src);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            HashSet<String> ret = new HashSet<>();
            boolean checked = false;
            while (br.ready()) {
                String s = br.readLine();
                if (!checked || s.equals("//Place here \"Track P Line\" values from table to automatically skip with save on them")) {
                    checked = true;
                    continue;
                }
                ret.add(s.toLowerCase());
            }
            return ret;
        } catch (Exception _) {
            System.err.println("Error occured while reading autosave.csv...");
        }
        return new HashSet<>();
    }

    private static void writeToDB(ArrayList<BelieveEntity> ret) {
        HashMap<String, BelieveEntity> parseMap = new HashMap<>();

        for (BelieveEntity e : ret) {
            if (parseMap.containsKey(generateKey(e))) {
                writeEnt(e);
                continue;
            }
            parseMap.put(generateKey(e), e);
        }

        for (BelieveEntity e : parseMap.values()) {
            writeEnt(e);
        }
    }

    private static void writeEnt(BelieveEntity e) {
        EntityManager em = Init.getEntityManager();
        if (em.createQuery("SELECT e from BelieveDBEntry e WHERE e.keys=:key").setParameter("key", generateKey(e)).getSingleResultOrNull() == null) {
            em.getTransaction().begin();
            BelieveDBEntry entry = new BelieveDBEntry();
            entry.keys = generateKey(e);
            entry.composer_artist.add(generatePair(e));
            System.out.println(e.Track_title);
            System.out.println(generatePair(e) + " " + generatePair(e).length());
            System.out.println();
            em.persist(entry);
            em.getTransaction().commit();
        } else {
            BelieveDBEntry ent = em.createQuery("SELECT e from BelieveDBEntry e WHERE e.keys=:key", BelieveDBEntry.class).setParameter("key", generateKey(e)).getSingleResult();
            em.getTransaction().begin();
            ent.composer_artist.add(generatePair(e));
            em.persist(ent);
            em.getTransaction().commit();
        }
    }

    private static void raiseQuestion(ArrayList<BelieveEntity> list, ArrayList<BelieveEntity> ret) {
        JDialog dial = new JDialog();
        Person.addListeners(dial);
        dial.setLayout(new BorderLayout());
        dial.setSize(new Dimension(1080, 640));
        dial.setPreferredSize(new Dimension(1080, 640));
        JLabel jl = new JLabel("Choose which info to preserve for: " + generateKey(list.getFirst()) + " (Artist:Title:Version)");
        JPanel line = new JPanel(new GridLayout(2, 3));
        JTextArea ja = new JTextArea("");
        JTextArea jc = new JTextArea("");
        JButton submit = new JButton("SUBMIT");
        line.add(new JLabel("Type Custom Artist(s) below"));
        line.add(new JLabel("Type Custom Composer(s) below"));
        JButton skip = new JButton("Skip");
        line.add(skip);
        line.add(ja);
        line.add(jc);
        line.add(submit);
        dial.add(line, BorderLayout.SOUTH);
        JScrollPane sp = new JScrollPane();
        JPanel viewport = new JPanel(new BorderLayout());
        JPanel jp = new JPanel(new GridLayout(1, list.size() + 1));
        viewport.add(jp, BorderLayout.CENTER);

        sp.setViewportView(viewport);
        Object[][] fields = new Object[fieldNames.length][1];
        for (int i = 0; i < fields.length; i++) {
            fields[i][0] = fieldNames[i];
        }
        ArrayList<JTable> tables = new ArrayList<>();
        JTable table = new JTable(new DefaultTableModel(fields, new Object[]{"Fields"})) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tables.add(table);
        jp.add(table);
        for (BelieveEntity one : list) {
            JTable jt1 = new JTable(new DefaultTableModel(formatEnt(one), new Object[]{""})) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tables.add(jt1);
            jp.add(jt1);
        }

        setVFX(tables, list);

        Thread current = Thread.currentThread();

        JPanel jp1 = new JPanel(new GridLayout(1, list.size()));
        viewport.add(jp1, BorderLayout.SOUTH);

        jp1.add(new JLabel());
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

        skip.addActionListener(e -> {
            JDialog jd = new JDialog();
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point point = pointerInfo.getLocation();
            jd.setLocation(point);
            jd.setLayout(new GridLayout(2, 1));
            JButton wosave = new JButton("DON'T SAVE TO DB");
            JButton wsave = new JButton("SAVE TO DB");
            wsave.addActionListener(ex -> {
                ret.addAll(list);
                writeToDB(ret);
                jd.dispose();
                synchronized (current) {
                    current.interrupt();
                }
                dial.dispose();
            });

            wosave.addActionListener(ex -> {
                System.out.println("Attention: this entry won't be shown in the final .xlsx file! Skipping...");
                jd.dispose();
                synchronized (current) {
                    current.interrupt();
                }
                dial.dispose();
            });

            jd.add(wsave);
            jd.add(wosave);
            jd.pack();
            jd.setVisible(true);
        });

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

    private static String generatePair(BelieveEntity ent) {
        return ent.Track_composer + ":" + ent.Track_author;
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

    private static void setVFX(ArrayList<JTable> tables, ArrayList<BelieveEntity> ents) {
        HashMap<Integer, HashSet<String>> check = new HashMap<>();
        for (BelieveEntity ent : ents) {
            String[] arr = getStringArr(ent);
            for (int i = 0; i < arr.length; i++) {
                check.computeIfAbsent(i, k -> new HashSet<>());
                check.get(i).add(arr[i]);
            }
        }

        for (JTable table : tables) {
            table.setDefaultRenderer(Object.class, new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    JLabel label = new JLabel(value.toString());
                    label.setOpaque(true);
                    if (check.get(row).size() == ents.size()) {
                        label.setBackground(Color.PINK);
                    } else {
                        label.setBackground(Color.WHITE);
                    }

                    if (isSelected) {
                        label.setBackground(Color.LIGHT_GRAY);
                        for (JTable t : tables) {
                            if (t.equals(table)) continue;
                            if (t.getSelectedRow() != row) {
                                t.setRowSelectionInterval(row, row);
                                t.setColumnSelectionInterval(column, column);
                            }
                        }
                    }


                    return label;
                }
            });
        }
    }

    private static String[] getStringArr(BelieveEntity ent) {
        return (ent.Track_artist_name + "\n" +
                ent.Track_title + "\n" +
                ent.Track_version + "\n" +
                ent.Track_author + "\n" +
                ent.Track_composer + "\n" +
                ent.Release_status + "\n" +
                ent.Title + "\n" +
                ent.Version + "\n" +
                ent.Release_type + "\n" +
                ent.Artist + "\n" +
                ent.Digital_release_date + "\n" +
                ent.Explicit_content + "\n" +
                ent.Product_language + "\n" +
                ent.Product_type + "\n" +
                ent.Production_year + "\n" +
                ent.Track_C_Line + "\n" +
                ent.Track_Featuring + "\n" +
                ent.Track_primary_genre + "\n" +
                ent.Track_label + "\n" +
                ent.Track_lyrics_language + "\n" +
                ent.Track_metadata_language + "\n" +
                ent.Track_P_Line + "\n" +
                ent.Track_preview_start_index + "\n" +
                ent.Track_Producer + "\n" +
                ent.Track_productionYear + "\n" +
                ent.Track_remixer + "\n" +
                ent.Track_support_number + "\n" +
                ent.Track_track_number + "\n" +
                ent.ISRC + "\n" +
                ent.UPC).split("\n");
    }

    private static Object[][] formatEnt(BelieveEntity ent) {
        String[] arr = getStringArr(ent);
        Object[][] ret = new Object[arr.length][1];
        for (int i = 0; i < ret.length; i++) {
            ret[i][0] = arr[i];
        }

        return ret;
    }

    private static final String[] fieldNames = new String[]{
            "Track Artist",
            "Track Title",
            "Track Version",
            "Track Author",
            "Track Composer",
            "Release Status",
            "Title",
            "Version",
            "Release Type",
            "Artist",
            "Digital Release Date",
            "Explicit Content",
            "Product Language",
            "Product Type",
            "Production Year",
            "Track C Line",
            "Track Featuring",
            "Track Primary Genre",
            "Track Label",
            "Track Lyrics Language",
            "Track Metadata Language",
            "Track P Line",
            "Track Preview Start Index",
            "Track Producer",
            "Track Production Year",
            "Track Remixer",
            "Track Support Number",
            "Track Track Number",
            "ISRC",
            "UPC"
    };
}
