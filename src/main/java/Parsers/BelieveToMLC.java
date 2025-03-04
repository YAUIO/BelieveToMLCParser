package Parsers;

import Entities.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class BelieveToMLC {
    public static ArrayList<MLCEntry> convert(ArrayList<BelieveEntity> sourceData) {
        System.out.println("Started converting to MLC table");
        ArrayList<MLCEntry> outputData = new ArrayList<>();
        Field[] fields = MLCEntry.class.getDeclaredFields();
        PersonList list = null;
        boolean doNotAdd = false;
        int debugCounter = 0;
        int lastListSize = 0;
        for (BelieveEntity bb : sourceData) {
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

            String[] splitS = null;

            list = new PersonList();
            if (bb.Track_author != null) {
                splitS = bb.Track_author.split(",");
                if (bb.Track_author.split("/").length > splitS.length) {
                    splitS = bb.Track_author.split("/");
                }

                for (String s : splitS) {
                    list.add(new Person(s, "A"));
                }
            }

            if (bb.Track_composer != null) {
                splitS = bb.Track_composer.split(",");
                if (bb.Track_composer.split("/").length > splitS.length) {
                    splitS = bb.Track_composer.split("/");
                }

                for (String s : splitS) {
                    list.add(new Person(s, "C"));
                }
            }

            if (list.size() == 0) {
                list.add(new Person("WRONGDATA","E"));
                System.err.println("Track has no author and no composer, writing as WRONGDATA with code ERROR");
            }

            for (int i = 0; i < list.size(); i++) {
                MLCEntry entry = new MLCEntry();
                if (i == 0) {
                    for (Field f : fields) {
                        Object obj = switch (f.getName()) {
                            case "PRIMARY_TITLE", "RECORDING_TITLE" -> bb.Title;
                            case "WRITER_LAST_NAME" -> list.get(i).LastName;
                            case "WRITER_FIRST_NAME" -> list.get(i).FirstName;
                            case "WRITER_ROLE_CODE" -> list.get(i).code;
                            case "MLC_PUBLISHER_NUMBER" -> "P359J1";
                            case "PUBLISHER_NAME" -> "INFINITY MUSIC";
                            case "PUBLISHER_IPI_NUMBER" -> 1234433973L;
                            case "COLLECTION_SHARE" -> 100;
                            case "RECORDING_ARTIST_NAME" -> bb.Artist;
                            case "RECORDING_ISRC" -> bb.ISRC;
                            case "RECORDING_LABEL" -> bb.Track_label;
                            default -> null;
                        };

                        try {
                            f.setAccessible(true);
                            f.set(entry, obj);
                        } catch (IllegalAccessException _) {

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
