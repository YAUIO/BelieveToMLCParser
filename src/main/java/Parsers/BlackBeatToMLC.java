package Parsers;

import Entities.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class BlackBeatToMLC {
    public static ArrayList<MLCEntry> convert(ArrayList<BlackBeatEntry> sourceData) {
        System.out.println("Started converting to MLC table");
        ArrayList<MLCEntry> outputData = new ArrayList<>();
        Field[] fields = MLCEntry.class.getDeclaredFields();
        PersonList list = null;
        boolean doNotAdd = false;
        int debugCounter = 0;
        int lastListSize = 0;
        for (BlackBeatEntry bb : sourceData) {
            System.out.println("Converting entry " + debugCounter + "/" + sourceData.size());

            if (debugCounter%20 == 0) {
                try {
                    if (Init.getEntityManager().createQuery("SELECT h from Human h").getResultList().size() > lastListSize) {
                        lastListSize = Init.getEntityManager().createQuery("SELECT h from Human h").getResultList().size();
                        DBtoXLSX.write(new File("db.xlsx"));
                        System.out.println("| Successfully wrote out DB to XLSX |");
                    }
                } catch (Exception e) {
                    System.out.println("Exception while writing db " + e.getMessage());
                }
            }

            debugCounter++;

            list = new PersonList();

            String[] splitS = bb.track_composer.split(",");
            if (bb.track_composer.split("\\.").length > splitS.length) {
                splitS = bb.track_composer.split("\\.");
            }
            if (bb.track_composer.split("/").length > splitS.length) {
                splitS = bb.track_composer.split("/");
            }

            for (String s : splitS) {
                list.add(new Person(s, "C"));
            }

            splitS = bb.track_author.split(",");
            if (bb.track_author.split("\\.").length > splitS.length) {
                splitS = bb.track_author.split("\\.");
            }
            if (bb.track_author.split("/").length > splitS.length) {
                splitS = bb.track_author.split("/");
            }

            for (String s : splitS) {
                list.add(new Person(s, "A"));
            }

            for (int i = 0; i < list.size(); i++) {
                MLCEntry entry = new MLCEntry();
                if (i == 0) {
                    for (Field f : fields) {
                        Object obj = switch (f.getName()) {
                            case "PRIMARY_TITLE", "RECORDING_TITLE" -> bb.track_title;
                            case "WRITER_LAST_NAME" -> list.get(i).LastName;
                            case "WRITER_FIRST_NAME" -> list.get(i).FirstName;
                            case "WRITER_ROLE_CODE" -> list.get(i).code;
                            case "MLC_PUBLISHER_NUMBER" -> "P359J1";
                            case "PUBLISHER_NAME" -> "INFINITY MUSIC";
                            case "PUBLISHER_IPI_NUMBER" -> 1234433973L;
                            case "COLLECTION_SHARE" -> bb.publishing_share;
                            case "RECORDING_ARTIST_NAME" -> bb.track_artist;
                            case "RECORDING_ISRC" -> bb.track_isrc;
                            case "RECORDING_LABEL" -> bb.label;
                            default -> null;
                        };

                        try {
                            f.setAccessible(true);
                            f.set(entry, obj);
                        } catch (IllegalAccessException e) {

                        }
                    }
                } else {
                    for (Field f : fields) {
                        Object obj = switch (f.getName()) {
                            case "WRITER_LAST_NAME" -> list.get(i).LastName;
                            case "WRITER_FIRST_NAME" -> list.get(i).FirstName;
                            case "WRITER_ROLE_CODE" -> list.get(i).code;
                            default -> null;
                        };

                        if (f.getName().equals("WRITER_LAST_NAME") && (list.get(i).LastName == null || list.get(i).LastName.isEmpty() || list.get(i).LastName.isBlank() || list.get(i).LastName.equals("-"))) {
                            doNotAdd = true;
                            break;
                        }

                        try {
                            f.setAccessible(true);
                            f.set(entry, obj);
                        } catch (IllegalAccessException e) {
                        }
                    }
                }
                if (!doNotAdd) {
                    outputData.add(entry);
                } else {
                    doNotAdd = false;
                }
            }
        }
        System.out.println("Finished converting to MLC table\n");
        return outputData;
    }
}
