package Entities;

import java.util.ArrayList;

public class PersonList {
    private final ArrayList<Person> list;

    public PersonList() {
        list = new ArrayList<>();
    }

    public void add (Person p) {
        if (list.contains(p)) {
            Person t = list.get(list.indexOf(p));
            if (t.code.equals("C") && p.code.equals("A")) {
                t.code = "CA";
            } else if (t.code.equals("A") && p.code.equals("C")) {
                t.code = "CA";
            }
            list.set(list.indexOf(p),t);
        } else {
            list.add(p);
        }
    }

    public Person get(int i) {
        return list.get(i);
    }

    public int size() {
        return list.size();
    }
}
