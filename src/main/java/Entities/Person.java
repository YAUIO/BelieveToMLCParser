package Entities;

import Annotations.AlternateTitle;
import Parsers.BelieveDBtoFile;
import Parsers.DBtoXLSX;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

public class Person {
    @AlternateTitle("WRITER LAST NAME *")
    public String LastName;

    @AlternateTitle("WRITER FIRST NAME")
    public String FirstName;

    @AlternateTitle("WRITER ROLE CODE *")
    public String code;

    private enum role_codes {C, A, CA, ERROR}

    public static void write (MLCEntry entry) {
        String firstName = entry.WRITER_FIRST_NAME;
        String lastName = entry.WRITER_LAST_NAME;
        if (firstName == null || lastName == null) {
            return;
        }
        Human h = new Human();
        h.name = firstName;
        if (Init.getEntityManager().createQuery("SELECT h from Human h where h.name=:name").setParameter("name", h.name).getSingleResultOrNull() == null) {
            try {
                h.fisur.put(firstName + lastName, lastName);
                EntityManager em = Init.getEntityManager();
                em.getTransaction().begin();
                em.persist(h);
                em.getTransaction().commit();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            try {
                Human hum = Init.getEntityManager().createQuery("SELECT h from Human h where h.name=:name", Human.class).setParameter("name", h.name).getSingleResult();
                hum.fisur.put(firstName + lastName, lastName);
                EntityManager em = Init.getEntityManager();
                em.getTransaction().begin();
                em.merge(h);
                em.getTransaction().commit();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Person(String name, String role_code) {

        if (!name.isEmpty() || !name.isBlank()) {
            while (name.charAt(0) == ' ') {
                name = name.substring(1);
                if (name.isEmpty() || name.isBlank()) {
                    break;
                }
            }
        }


        if (!name.isEmpty() && !name.isBlank()) {
            String[] nameArr = name.split(" ");

            switch (nameArr.length) {
                case 2:
                    if (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", nameArr[0]).getSingleResultOrNull() != null) {
                        FirstName = nameArr[0];
                        LastName = nameArr[1];
                    } else if (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", nameArr[1]).getSingleResultOrNull() != null) {
                        FirstName = nameArr[1];
                        LastName = nameArr[0];
                    } else if (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", name).getSingleResultOrNull() != null) {
                        FirstName = name;
                        LastName = "WRONGDATA";
                    } else {
                        JDialog jd = new JDialog();
                        addListeners(jd);
                        jd.setTitle("Need manual intervention");
                        Dimension d = new Dimension(400, 200);
                        jd.setSize(d);
                        jd.setPreferredSize(d);
                        jd.setResizable(false);
                        jd.setLayout(new GridLayout(2, 1));
                        jd.add(new JLabel("Which is name: (1) " + nameArr[0] + " or (2) " + nameArr[1]));
                        JPanel jp = new JPanel();
                        JButton one = new JButton("(1)");
                        JButton two = new JButton("(2)");
                        JButton ignore = new JButton("ignore");
                        jp.add(one);
                        jp.add(two);
                        jp.add(ignore);
                        jd.add(jp);
                        jd.setVisible(true);
                        Thread wait = Thread.currentThread();

                        one.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_1) {
                                    Human h = new Human();
                                    h.name = nameArr[0];
                                    EntityManager em = Init.getEntityManager();
                                    em.getTransaction().begin();
                                    em.persist(h);
                                    em.getTransaction().commit();
                                    FirstName = h.name;
                                    LastName = nameArr[1];
                                    jd.setVisible(false);
                                    jd.dispose();
                                    synchronized (wait) {
                                        wait.interrupt();
                                    }
                                } else if (e.getKeyCode() == KeyEvent.VK_2) {
                                    Human h = new Human();
                                    h.name = nameArr[1];
                                    EntityManager em = Init.getEntityManager();
                                    em.getTransaction().begin();
                                    em.persist(h);
                                    em.getTransaction().commit();
                                    FirstName = h.name;
                                    LastName = nameArr[0];
                                    jd.setVisible(false);
                                    jd.dispose();
                                    synchronized (wait) {
                                        wait.interrupt();
                                    }
                                }
                            }
                        });

                        one.addActionListener(l -> {
                            Human h = new Human();
                            h.name = nameArr[0];
                            if (Init.getEntityManager().createQuery("SELECT h from Human h where h.name=:name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                try {
                                    EntityManager em = Init.getEntityManager();
                                    em.getTransaction().begin();
                                    em.persist(h);
                                    em.getTransaction().commit();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            FirstName = h.name;
                            LastName = nameArr[1];
                            jd.setVisible(false);
                            jd.dispose();
                            synchronized (wait) {
                                wait.interrupt();
                            }
                        });

                        two.addActionListener(l -> {
                            Human h = new Human();
                            h.name = nameArr[1];
                            if (Init.getEntityManager().createQuery("SELECT h from Human h where h.name=:name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                try {
                                    EntityManager em = Init.getEntityManager();
                                    em.getTransaction().begin();
                                    em.persist(h);
                                    em.getTransaction().commit();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            FirstName = h.name;
                            LastName = nameArr[0];
                            jd.setVisible(false);
                            jd.dispose();
                            synchronized (wait) {
                                wait.interrupt();
                            }
                        });

                        String finalName1 = name;
                        ignore.addActionListener(l -> {
                            FirstName = finalName1;
                            LastName = "WRONGDATA";
                            Human h = new Human();
                            h.name = finalName1;
                            h.fisur.put(finalName1, "WRONGDATA");
                            if (Init.getEntityManager().createQuery("SELECT h from Human h JOIN h.fisur f where key(f) = :fis").setParameter("fis", finalName1).getSingleResultOrNull() == null) {
                                try {
                                    EntityManager em = Init.getEntityManager();
                                    em.getTransaction().begin();
                                    em.persist(h);
                                    em.getTransaction().commit();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            jd.setVisible(false);
                            jd.dispose();
                            synchronized (wait) {
                                wait.interrupt();
                            }
                        });

                        try {
                            synchronized (wait) {
                                wait.wait();
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                    break;
                case 1:
                    LastName = nameArr[0];
                    break;
                default:
                    if (nameArr.length > 2) {
                        if (Init.getEntityManager().createQuery("SELECT h FROM Human h JOIN h.fisur f WHERE KEY(f) = :name").setParameter("name", name).getSingleResultOrNull() != null) {
                            Human h = Init.getEntityManager().createQuery("SELECT h FROM Human h JOIN h.fisur f WHERE KEY(f) = :name", Human.class).setParameter("name", name).getSingleResult();
                            FirstName = h.name;
                            LastName = h.fisur.get(name);
                        } else if (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", nameArr[0]).getSingleResultOrNull() != null &&
                                (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name", Human.class).setParameter("name", nameArr[0]).getSingleResult()).fisur.get(name) != null) {
                            Human h = (Human) Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", nameArr[0]).getSingleResult();
                            FirstName = h.name;
                            LastName = h.fisur.get(name);
                        } else if (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", nameArr[1]).getSingleResultOrNull() != null &&
                                (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name", Human.class).setParameter("name", nameArr[1]).getSingleResult()).fisur.get(name) != null) {
                            Human h = Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name", Human.class).setParameter("name", nameArr[1]).getSingleResult();
                            FirstName = h.name;
                            LastName = h.fisur.get(name);
                        } else if (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", nameArr[2]).getSingleResultOrNull() != null &&
                                (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name", Human.class).setParameter("name", nameArr[2]).getSingleResult()).fisur.get(name) != null) {
                            Human h = Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name", Human.class).setParameter("name", nameArr[2]).getSingleResult();
                            FirstName = h.name;
                            LastName = h.fisur.get(name);
                        }
                    }

                    if (FirstName == null && LastName == null) {

                        if (nameArr.length == 3) {

                            if (Init.getEntityManager().createQuery("SELECT b from Human b WHERE b.name=:name").setParameter("name", name).getSingleResultOrNull() != null) {
                                FirstName = name;
                                LastName = "WRONGDATA";
                            } else if (name.toLowerCase().contains("уулу") || name.toLowerCase().contains("uulu") ||name.toLowerCase().contains("кызы") || name.toLowerCase().contains("kyzy")) {
                                FirstName = name;
                                LastName = "WRONGDATA";
                            } else if (nameArr[1].length() == 2 && nameArr[2].length() == 2) {
                                if (nameArr[1].endsWith(".") && nameArr[2].endsWith(".")) {
                                    if (!nameArr[0].endsWith(".")) {
                                        FirstName = nameArr[1];
                                        LastName = nameArr[0];
                                    }
                                }
                            } else {
                                {
                                    JDialog jd = new JDialog();
                                    addListeners(jd);
                                    jd.setTitle("Need manual intervention");
                                    Dimension d = new Dimension(400, 200);
                                    jd.setSize(d);
                                    jd.setPreferredSize(d);
                                    jd.setResizable(false);
                                    jd.setLayout(new GridLayout(2, 1));
                                    jd.add(new JLabel("Which is name: (1) " + nameArr[0] + " or (2) " + nameArr[1] + " or (3) " + nameArr[2]));
                                    JPanel jp = new JPanel();
                                    JButton one = new JButton("(1)");
                                    JButton two = new JButton("(2)");
                                    JButton three = new JButton("(3)");
                                    JButton ignore = new JButton("ignore");
                                    jp.add(one);
                                    jp.add(two);
                                    jp.add(three);
                                    jp.add(ignore);
                                    jd.add(jp);
                                    jd.setVisible(true);
                                    Thread wait = Thread.currentThread();

                                    one.addKeyListener(new KeyAdapter() {
                                        @Override
                                        public void keyPressed(KeyEvent e) {
                                            if (e.getKeyCode() == KeyEvent.VK_1) {
                                                Human h = new Human();
                                                h.name = nameArr[0];
                                                FirstName = h.name;
                                                jd.setVisible(false);
                                                jd.dispose();
                                                synchronized (wait) {
                                                    wait.interrupt();
                                                }
                                            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                                                Human h = new Human();
                                                h.name = nameArr[1];
                                                FirstName = h.name;
                                                jd.setVisible(false);
                                                jd.dispose();
                                                synchronized (wait) {
                                                    wait.interrupt();
                                                }
                                            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                                                Human h = new Human();
                                                h.name = nameArr[2];
                                                FirstName = h.name;
                                                jd.setVisible(false);
                                                jd.dispose();
                                                synchronized (wait) {
                                                    wait.interrupt();
                                                }
                                            }
                                        }
                                    });

                                    one.addActionListener(l -> {
                                        Human h = new Human();
                                        h.name = nameArr[0];
                                        FirstName = h.name;
                                        jd.setVisible(false);
                                        jd.dispose();
                                        synchronized (wait) {
                                            wait.interrupt();
                                        }
                                    });

                                    two.addActionListener(l -> {
                                        Human h = new Human();
                                        h.name = nameArr[1];
                                        FirstName = h.name;
                                        jd.setVisible(false);
                                        jd.dispose();
                                        synchronized (wait) {
                                            wait.interrupt();
                                        }
                                    });

                                    three.addActionListener(l -> {
                                        Human h = new Human();
                                        h.name = nameArr[2];
                                        FirstName = h.name;
                                        jd.setVisible(false);
                                        jd.dispose();
                                        synchronized (wait) {
                                            wait.interrupt();
                                        }
                                    });

                                    String finalName1 = name;
                                    ignore.addActionListener(l -> {
                                        FirstName = finalName1;
                                        LastName = "WRONGDATA";
                                        Human h = new Human();
                                        h.name = finalName1;
                                        h.fisur.put(finalName1, "WRONGDATA");
                                        if (Init.getEntityManager().createQuery("SELECT h from Human h JOIN h.fisur f where key(f) = :fis").setParameter("fis", finalName1).getSingleResultOrNull() == null) {
                                            try {
                                                EntityManager em = Init.getEntityManager();
                                                em.getTransaction().begin();
                                                em.persist(h);
                                                em.getTransaction().commit();
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        }
                                        jd.setVisible(false);
                                        jd.dispose();
                                        synchronized (wait) {
                                            wait.interrupt();
                                        }
                                    });

                                    try {
                                        synchronized (wait) {
                                            wait.wait();
                                        }
                                    } catch (InterruptedException e) {
                                    }
                                }

                                if (LastName == null || !LastName.equals("WRONGDATA")) {
                                    JDialog jd = new JDialog();
                                    addListeners(jd);
                                    jd.setTitle("Need manual intervention");
                                    Dimension d = new Dimension(400, 200);
                                    jd.setSize(d);
                                    jd.setPreferredSize(d);
                                    jd.setResizable(false);
                                    jd.setLayout(new GridLayout(2, 1));
                                    jd.add(new JLabel("Which is SURNAME !! : (1) " + nameArr[0] + " or (2) " + nameArr[1] + " or (3) " + nameArr[2]));
                                    JPanel jp = new JPanel();
                                    JButton one = new JButton("(1)");
                                    JButton two = new JButton("(2)");
                                    JButton three = new JButton("(3)");
                                    jp.add(one);
                                    jp.add(two);
                                    jp.add(three);
                                    jd.add(jp);
                                    jd.setVisible(true);
                                    Thread wait = Thread.currentThread();

                                    String finalName2 = name;
                                    one.addKeyListener(new KeyAdapter() {
                                        @Override
                                        public void keyPressed(KeyEvent e) {
                                            if (e.getKeyCode() == KeyEvent.VK_1) {
                                                Human h = new Human();
                                                h.name = FirstName;
                                                LastName = nameArr[0];
                                                h.fisur.put(finalName2, LastName);
                                                EntityManager em = Init.getEntityManager();
                                                if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                                    em.getTransaction().begin();
                                                    em.persist(h);
                                                    em.getTransaction().commit();
                                                } else if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult().fisur.isEmpty()) {
                                                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                                    rec.fisur = h.fisur;
                                                    em.getTransaction().begin();
                                                    em.merge(rec);
                                                    em.getTransaction().commit();
                                                } else {
                                                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                                    rec.fisur.putAll(h.fisur);
                                                    em.getTransaction().begin();
                                                    em.merge(rec);
                                                    em.getTransaction().commit();
                                                }
                                                jd.setVisible(false);
                                                jd.dispose();
                                                synchronized (wait) {
                                                    wait.interrupt();
                                                }
                                            } else if (e.getKeyCode() == KeyEvent.VK_2) {
                                                Human h = new Human();
                                                h.name = FirstName;
                                                LastName = nameArr[1];
                                                h.fisur.put(finalName2, LastName);
                                                EntityManager em = Init.getEntityManager();
                                                if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                                    em.getTransaction().begin();
                                                    em.persist(h);
                                                    em.getTransaction().commit();
                                                } else if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult().fisur.isEmpty()) {
                                                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                                    rec.fisur = h.fisur;
                                                    em.getTransaction().begin();
                                                    em.merge(rec);
                                                    em.getTransaction().commit();
                                                } else {
                                                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                                    rec.fisur.putAll(h.fisur);
                                                    em.getTransaction().begin();
                                                    em.merge(rec);
                                                    em.getTransaction().commit();
                                                }
                                                jd.setVisible(false);
                                                jd.dispose();
                                                synchronized (wait) {
                                                    wait.interrupt();
                                                }
                                            } else if (e.getKeyCode() == KeyEvent.VK_3) {
                                                Human h = new Human();
                                                h.name = FirstName;
                                                LastName = nameArr[2];
                                                h.fisur.put(finalName2, LastName);
                                                EntityManager em = Init.getEntityManager();
                                                if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                                    em.getTransaction().begin();
                                                    em.persist(h);
                                                    em.getTransaction().commit();
                                                } else if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult().fisur.isEmpty()) {
                                                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                                    rec.fisur = h.fisur;
                                                    em.getTransaction().begin();
                                                    em.merge(rec);
                                                    em.getTransaction().commit();
                                                } else {
                                                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                                    rec.fisur.putAll(h.fisur);
                                                    em.getTransaction().begin();
                                                    em.merge(rec);
                                                    em.getTransaction().commit();
                                                }
                                                jd.setVisible(false);
                                                jd.dispose();
                                                synchronized (wait) {
                                                    wait.interrupt();
                                                }
                                            }
                                        }
                                    });

                                    one.addActionListener(l -> {
                                        Human h = new Human();
                                        h.name = FirstName;
                                        LastName = nameArr[0];
                                        h.fisur.put(finalName2, LastName);
                                        EntityManager em = Init.getEntityManager();
                                        if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                            em.getTransaction().begin();
                                            em.persist(h);
                                            em.getTransaction().commit();
                                        } else {
                                            Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                            rec.fisur.putAll(h.fisur);
                                            em.getTransaction().begin();
                                            em.merge(rec);
                                            em.getTransaction().commit();
                                        }
                                        jd.setVisible(false);
                                        jd.dispose();
                                        synchronized (wait) {
                                            wait.interrupt();
                                        }
                                    });

                                    two.addActionListener(l -> {
                                        Human h = new Human();
                                        h.name = FirstName;
                                        LastName = nameArr[1];
                                        h.fisur.put(finalName2, LastName);
                                        EntityManager em = Init.getEntityManager();
                                        if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                            em.getTransaction().begin();
                                            em.persist(h);
                                            em.getTransaction().commit();
                                        } else {
                                            Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                            rec.fisur.putAll(h.fisur);
                                            em.getTransaction().begin();
                                            em.merge(rec);
                                            em.getTransaction().commit();
                                        }
                                        jd.setVisible(false);
                                        jd.dispose();
                                        synchronized (wait) {
                                            wait.interrupt();
                                        }
                                    });

                                    three.addActionListener(l -> {
                                        Human h = new Human();
                                        h.name = FirstName;
                                        LastName = nameArr[2];
                                        h.fisur.put(finalName2, LastName);
                                        EntityManager em = Init.getEntityManager();
                                        if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                            em.getTransaction().begin();
                                            em.persist(h);
                                            em.getTransaction().commit();
                                        } else {
                                            Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name", h.name).getSingleResult();
                                            rec.fisur.putAll(h.fisur);
                                            em.getTransaction().begin();
                                            em.merge(rec);
                                            em.getTransaction().commit();
                                        }
                                        jd.setVisible(false);
                                        jd.dispose();
                                        synchronized (wait) {
                                            wait.interrupt();
                                        }
                                    });

                                    try {
                                        synchronized (wait) {
                                            wait.wait();
                                        }
                                    } catch (InterruptedException e) {
                                    }
                                }
                            }
                        } else {
                            JDialog jd = new JDialog();
                            addListeners(jd);
                            jd.setTitle("Need manual intervention");
                            Dimension d = new Dimension(400, 200);
                            jd.setSize(d);
                            jd.setPreferredSize(d);
                            jd.setResizable(false);
                            jd.setLayout(new GridLayout(4, 1));
                            JPanel up = new JPanel(new GridLayout(2, 1));
                            up.add(new JLabel(name + " - Please type name and surname"));
                            up.add(new JTextField(name + " (for copy only)"));
                            jd.add(up);
                            JPanel row = new JPanel(new GridLayout(1, 2));
                            row.add(new JLabel("Name", SwingConstants.CENTER));
                            row.add(new JLabel("Surname", SwingConstants.CENTER));
                            jd.add(row);
                            JPanel jp = new JPanel();
                            jp.setLayout(new GridLayout(1, 2));
                            JButton submit = new JButton("submit");
                            JTextField jfn = new JTextField();
                            jfn.setToolTipText("Name");
                            JTextField jfs = new JTextField();
                            jfs.setToolTipText("Surname");
                            JPanel jpb = new JPanel();
                            JButton ignore = new JButton("ignore");
                            jpb.add(submit);
                            jpb.add(ignore);
                            jp.add(jfn);
                            jp.add(jfs);
                            jd.add(jp);
                            jd.add(jpb);
                            jd.setVisible(true);
                            Thread wait = Thread.currentThread();

                            String finalName = name;
                            submit.addActionListener(l -> {
                                Human h = new Human();
                                h.name = jfn.getText();
                                h.fisur.put(finalName, jfs.getText());
                                if (Init.getEntityManager().createQuery("SELECT h from Human h where h.name=:name").setParameter("name", h.name).getSingleResultOrNull() == null) {
                                    try {
                                        EntityManager em = Init.getEntityManager();
                                        em.getTransaction().begin();
                                        em.persist(h);
                                        em.getTransaction().commit();
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                } else {
                                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h where h.name=:name", Human.class).setParameter("name", h.name).getSingleResult();
                                    EntityManager em = Init.getEntityManager();
                                    rec.fisur.put(finalName, jfs.getText());
                                    em.getTransaction().begin();
                                    em.merge(rec);
                                    em.getTransaction().commit();
                                }
                                FirstName = h.name;
                                LastName = h.fisur.get(finalName);
                                jd.setVisible(false);
                                jd.dispose();
                                synchronized (wait) {
                                    wait.interrupt();
                                }
                            });

                            String finalName1 = name;
                            ignore.addActionListener(l -> {
                                FirstName = finalName1;
                                LastName = "WRONGDATA";
                                Human h = new Human();
                                h.name = finalName1;
                                h.fisur.put(finalName1, "WRONGDATA");
                                if (Init.getEntityManager().createQuery("SELECT h from Human h JOIN h.fisur f where key(f) = :fis").setParameter("fis", finalName1).getSingleResultOrNull() == null) {
                                    try {
                                        EntityManager em = Init.getEntityManager();
                                        em.getTransaction().begin();
                                        em.persist(h);
                                        em.getTransaction().commit();
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                                jd.setVisible(false);
                                jd.dispose();
                                synchronized (wait) {
                                    wait.interrupt();
                                }
                            });

                            try {
                                synchronized (wait) {
                                    wait.wait();
                                }
                            } catch (InterruptedException e) {
                            }
                        }
                    }
            }
        }

        try {
            code = role_codes.valueOf(role_code).toString();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse creator role code: " + role_code);
        }

        if (FirstName != null) {
            while (FirstName.charAt(0) == ' ') {
                FirstName = FirstName.substring(1);
                if (FirstName.isEmpty()) break;
            }

            while (FirstName.charAt(FirstName.length() - 1) == ' ') {
                FirstName = FirstName.substring(0, FirstName.length() - 2);
                if (FirstName.isEmpty()) break;
            }
        }

        if (LastName != null) {
            while (LastName.charAt(0) == ' ') {
                LastName = LastName.substring(1);
                if (LastName.isEmpty()) break;
            }

            while (LastName.charAt(LastName.length() - 1) == ' ') {
                LastName = LastName.substring(0, LastName.length() - 2);
                if (LastName.isEmpty()) break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        if (LastName != null && LastName.equals(person.LastName)) {
            if (FirstName != null) {
                return FirstName.equals(person.FirstName);
            }
        }
        return false;
    }

    public static void addListeners(JDialog jd) {
        //jd.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - jd.getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - jd.getHeight()/2);
        jd.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (e.getNewState() == 0) {
                    System.err.println("JDialog closed, saving db and exiting...");
                    File f = new File("db.xlsx");
                    if (f.exists()) if (!f.delete()) System.err.println("Couldn't delete db.xlsx");
                    File believeDB = new File("believeDB.xlsx");
                    if (believeDB.exists()) if (!believeDB.delete()) System.err.println("Couldn't delete believeDB.xlsx");
                    DBtoXLSX.write(f);
                    BelieveDBtoFile.write(believeDB);
                    System.exit(1);
                }
            }
        });
    }
}
