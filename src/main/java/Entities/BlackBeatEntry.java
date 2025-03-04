package Entities;

import Annotations.AlternateTitle;

import java.util.Date;
import java.sql.Time;

public class BlackBeatEntry {
    public Date date_claim;
    public String licensor;
    public String track_title;
    public String track_artist;
    public String track_composer;
    public String track_author;
    public String label;
    public String track_isrc;
    public String track_upc;
    public Integer publishing_share;
    public String territory;
    public Time Duration;
    public Date effective_date_start;
    public Date effective_date_end;
    public String Indificator;

    @AlternateTitle("YouTube +")
    public String YouTube;

    @AlternateTitle("The MLC")
    public String TheMLC;

    public String Docs;

    @Override
    public String toString() {
        return "BlackBeatEntry{" +
                "date_claim=" + date_claim +
                ", licensor='" + licensor + '\'' +
                ", track_title='" + track_title + '\'' +
                ", track_artist='" + track_artist + '\'' +
                ", track_composer='" + track_composer + '\'' +
                ", track_author='" + track_author + '\'' +
                ", label='" + label + '\'' +
                ", track_isrc='" + track_isrc + '\'' +
                ", track_upc='" + track_upc + '\'' +
                ", publishing_share=" + publishing_share +
                ", territory='" + territory + '\'' +
                ", Duration=" + Duration +
                ", effective_date_start=" + effective_date_start +
                ", effective_date_end=" + effective_date_end +
                ", Indificator='" + Indificator + '\'' +
                ", YouTube='" + YouTube + '\'' +
                ", TheMLC='" + TheMLC + '\'' +
                ", Docs='" + Docs + '\'' +
                '}';
    }
}
