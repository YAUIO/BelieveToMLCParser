package Entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class BelieveDBEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, unique = true)
    public String keys;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "BelieveDBEntry_id"))
    @MapKeyColumn()
    @Column(nullable = true, unique = false, length = 512)
    public Set<String> composer_artist = new HashSet<>();

    @Override
    public String toString() {
        return "BelieveDBEntry{" +
                "id=" + id +
                ", keys='" + keys + '\'' +
                ", fisur=" + composer_artist +
                '}';
    }
}
