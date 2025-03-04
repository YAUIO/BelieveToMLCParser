package Entities;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Human {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, unique = true)
    public String name;

    @ElementCollection
    @CollectionTable(name = "human_fi_map", joinColumns = @JoinColumn(name = "human_id"))
    @MapKeyColumn(name = "fi_key")
    @Column(name = "fi_value", nullable = true, unique = false)
    public Map<String, String> fisur = new HashMap<>();

    @Override
    public String toString() {
        return "Human{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
