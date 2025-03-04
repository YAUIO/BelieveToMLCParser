package Entities;

import Annotations.AlternateTitle;

public class MLCEntry {
    @AlternateTitle("PRIMARY TITLE *")
    String PRIMARY_TITLE;

    @AlternateTitle("WRITER LAST NAME *")
    String WRITER_LAST_NAME;

    @AlternateTitle("WRITER FIRST NAME")
    String WRITER_FIRST_NAME;

    @AlternateTitle("WRITER ROLE CODE *")
    String WRITER_ROLE_CODE;

    @AlternateTitle("MLC PUBLISHER NUMBER")
    String MLC_PUBLISHER_NUMBER;

    @AlternateTitle("PUBLISHER NAME *")
    String PUBLISHER_NAME;

    @AlternateTitle("PUBLISHER IPI NUMBER *")
    Long PUBLISHER_IPI_NUMBER;

    @AlternateTitle("ADMINISTRATOR MLC PUBLISHER NUMBER")
    String ADMINISTRATOR_MLC_PUBLISHER_NUMBER;

    @AlternateTitle("ADMINISTRATOR NAME †")
    String ADMINISTRATOR_NAME;

    @AlternateTitle("ADMINISTRATOR IPI NUMBER †")
    String ADMINISTRATOR_IPI_NUMBER;

    @AlternateTitle("COLLECTION SHARE *")
    Integer COLLECTION_SHARE;

    @AlternateTitle("RECORDING TITLE †")
    String RECORDING_TITLE;

    @AlternateTitle("RECORDING ARTIST NAME †")
    String RECORDING_ARTIST_NAME;

    @AlternateTitle("RECORDING ISRC")
    String RECORDING_ISRC;

    @AlternateTitle("RECORDING LABEL")
    String RECORDING_LABEL;

    @Override
    public String toString() {
        return "MLCEntry{" +
                "PRIMARY_TITLE='" + PRIMARY_TITLE + '\'' +
                ", WRITER_LAST_NAME='" + WRITER_LAST_NAME + '\'' +
                ", WRITER_FIREST_NAME='" + WRITER_FIRST_NAME + '\'' +
                ", WRITER_ROLE_CODE='" + WRITER_ROLE_CODE + '\'' +
                ", MLC_PUBLISHER_NUMBER='" + MLC_PUBLISHER_NUMBER + '\'' +
                ", PUBLISHER_NAME='" + PUBLISHER_NAME + '\'' +
                ", PUBLISHER_IPI_NUMBER=" + PUBLISHER_IPI_NUMBER +
                ", ADMINISTRATOR_MLC_PUBLISHER_NUMBER='" + ADMINISTRATOR_MLC_PUBLISHER_NUMBER + '\'' +
                ", ADMINISTRATOR_NAME='" + ADMINISTRATOR_NAME + '\'' +
                ", ADMINISTRATOR_IPI_NUMBER='" + ADMINISTRATOR_IPI_NUMBER + '\'' +
                ", COLLECTION_SHARE=" + COLLECTION_SHARE +
                ", RECORDING_TITLE='" + RECORDING_TITLE + '\'' +
                ", RECORDING_ARTIST_NAME='" + RECORDING_ARTIST_NAME + '\'' +
                ", RECORDING_ISRC='" + RECORDING_ISRC + '\'' +
                ", RECORDING_LABEL='" + RECORDING_LABEL + '\'' +
                '}';
    }
}
