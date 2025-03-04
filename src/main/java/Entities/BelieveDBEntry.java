package Entities;

import jakarta.persistence.*;

@Entity
public class BelieveDBEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, unique = true)
    public String keys;

    @Column(nullable = false)
    public String author;

    @Column(nullable = false)
    public String composer;

    @Override
    public String toString() {
        return "BelieveDBEntry{" +
                "key='" + keys + '\'' +
                ", author='" + author + '\'' +
                ", composer='" + composer + '\'' +
                '}';
    }
}
