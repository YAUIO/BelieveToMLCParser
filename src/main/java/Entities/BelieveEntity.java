package Entities;

import Annotations.AlternateTitle;

import java.util.Date;

public class BelieveEntity {
    @AlternateTitle("Release status")
    public String Release_status;
    public String Title;
    public String Version;

    @AlternateTitle("Release type")
    public String Release_type;
    public String Artist;

    @AlternateTitle("Digital release date")
    public Date Digital_release_date;

    @AlternateTitle("Explicit content")
    public String Explicit_content;

    @AlternateTitle("Product language")
    public String Product_language;

    @AlternateTitle("Product type")
    public String Product_type;

    @AlternateTitle("Production year")
    public Integer Production_year;

    @AlternateTitle("Track artist name")
    public String Track_artist_name;

    @AlternateTitle("Track author")
    public String Track_author;

    @AlternateTitle("Track C Line")
    public String Track_C_Line;

    @AlternateTitle("Track composer")
    public String Track_composer;

    @AlternateTitle("Track Featuring")
    public String Track_Featuring;

    @AlternateTitle("Track primary genre")
    public String Track_primary_genre;
    public String ISRC;

    @AlternateTitle("Track label")
    public String Track_label;

    @AlternateTitle("Track lyrics language")
    public String Track_lyrics_language;

    @AlternateTitle("Track metadata language")
    public String Track_metadata_language;

    @AlternateTitle("Track P Line")
    public String Track_P_Line;

    @AlternateTitle("Track preview start index")
    public String Track_preview_start_index;

    @AlternateTitle("Track Producer")
    public String Track_Producer;

    @AlternateTitle("Track productionYear")
    public Integer Track_productionYear;

    @AlternateTitle("Track remixer")
    public String Track_remixer;

    @AlternateTitle("Track title")
    public String Track_title;

    @AlternateTitle("Track support number")
    public Integer Track_support_number;

    @AlternateTitle("Track track number")
    public Integer Track_track_number;

    @AlternateTitle("Track version")
    public Integer Track_version;
    public String UPC;

    @Override
    public String toString() {
        return "BelieveEntity{" +
                "Release_status='" + Release_status + '\'' +
                ", Title='" + Title + '\'' +
                ", Version='" + Version + '\'' +
                ", Release_type='" + Release_type + '\'' +
                ", Artist='" + Artist + '\'' +
                ", Digital_release_date=" + Digital_release_date +
                ", Explicit_content='" + Explicit_content + '\'' +
                ", Product_language='" + Product_language + '\'' +
                ", Product_type='" + Product_type + '\'' +
                ", Production_year=" + Production_year +
                ", Track_artist_name='" + Track_artist_name + '\'' +
                ", Track_author='" + Track_author + '\'' +
                ", Track_C_Line='" + Track_C_Line + '\'' +
                ", Track_composer='" + Track_composer + '\'' +
                ", Track_Featuring='" + Track_Featuring + '\'' +
                ", Track_primary_genre='" + Track_primary_genre + '\'' +
                ", ISRC='" + ISRC + '\'' +
                ", Track_label='" + Track_label + '\'' +
                ", Track_lyrics_language='" + Track_lyrics_language + '\'' +
                ", Track_metadata_language='" + Track_metadata_language + '\'' +
                ", Track_P_Line='" + Track_P_Line + '\'' +
                ", Track_preview_start_index='" + Track_preview_start_index + '\'' +
                ", Track_Producer='" + Track_Producer + '\'' +
                ", Track_productionYear=" + Track_productionYear +
                ", Track_remixer='" + Track_remixer + '\'' +
                ", Track_title='" + Track_title + '\'' +
                ", Track_support_number=" + Track_support_number +
                ", Track_track_number=" + Track_track_number +
                ", Track_version=" + Track_version +
                ", UPC='" + UPC + '\'' +
                '}';
    }
}