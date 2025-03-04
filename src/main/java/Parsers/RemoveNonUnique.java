package Parsers;

import Entities.MLCEntry;

import java.util.ArrayList;
import java.util.HashSet;

public class RemoveNonUnique {
    public static ArrayList<MLCEntry> call (ArrayList<MLCEntry> original, ArrayList<MLCEntry> written) {
        HashSet<String> originalISRCs = new HashSet<>();
        for (MLCEntry m : original) {
            originalISRCs.add(m.RECORDING_ISRC);
            if (m.RECORDING_ISRC == null) {
                System.err.println("NULL VALUE DETECTED");
            }
        }

        HashSet<String> writtenISRCs = new HashSet<>();
        for (MLCEntry m : written) {
            writtenISRCs.add(m.RECORDING_ISRC);
        }

        ArrayList<MLCEntry> out = new ArrayList<>();

        originalISRCs.removeAll(writtenISRCs);

        for (MLCEntry m : original) {
            if (originalISRCs.contains(m.RECORDING_ISRC)) {
                out.add(m);
            }
        }

        return out;
    }
}
