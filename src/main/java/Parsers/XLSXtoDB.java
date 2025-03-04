package Parsers;

import Entities.Human;
import Entities.Init;
import jakarta.persistence.EntityManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XLSXtoDB {
    public static void load(File f) {
        try (FileInputStream fileIn = new FileInputStream(f); Workbook workbook =  new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheet("DB");

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                Human h = new Human();
                if (row.getCell(1).getStringCellValue() == null) break;
                h.name = row.getCell(1).getStringCellValue();
                if (row.getCell(2).getStringCellValue().equals("LINK")) {
                    String addr = row.getCell(2).getHyperlink().getAddress();
                    addr = addr.substring(1, addr.lastIndexOf("'"));
                    h.fisur = readMap(workbook.getSheet(addr));
                }

                if (Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name").setParameter("name",h.name).getSingleResultOrNull() == null) {
                    EntityManager em = Init.getEntityManager();
                    em.getTransaction().begin();
                    em.persist(h);
                    em.getTransaction().commit();
                } else {
                    Human rec = Init.getEntityManager().createQuery("SELECT h from Human h WHERE h.name = :name", Human.class).setParameter("name",h.name).getSingleResult();
                    rec.fisur.putAll(h.fisur);
                    EntityManager em = Init.getEntityManager();
                    em.getTransaction().begin();
                    em.merge(rec);
                    em.getTransaction().commit();
                }
            }

            System.out.println("Excel file read successfully: " + f.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error reading Excel file: " + e.getMessage());
        }
    }

    private static Map<String,String> readMap(Sheet sheet) {
        Map<String,String> map = new HashMap<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row.getCell(0).getStringCellValue() == null) break;
            map.put(row.getCell(0).getStringCellValue(),row.getCell(1).getStringCellValue());
        }
        return map;
    }
}
