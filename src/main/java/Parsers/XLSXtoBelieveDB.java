package Parsers;

import Entities.BelieveDBEntry;
import Entities.Init;
import jakarta.persistence.EntityManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XLSXtoBelieveDB {
    public static void load(File f) {
        try (FileInputStream fileIn = new FileInputStream(f); Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheet("DB");

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                BelieveDBEntry h = new BelieveDBEntry();
                if (row.getCell(1).getStringCellValue() == null) break;
                h.keys = row.getCell(1).getStringCellValue();
                h.composer_artist = parseCACell(row.getCell(2).getStringCellValue());
                EntityManager em = Init.getEntityManager();
                em.getTransaction().begin();
                if (em.createQuery("SELECT e from BelieveDBEntry e WHERE e.keys=:key").setParameter("key",h.keys).getSingleResultOrNull() != null) {
                    em.merge(h);
                } else {
                    em.persist(h);
                }
                em.getTransaction().commit();
            }

            System.out.println("Excel file read successfully: " + f.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error reading Excel file: " + e.getMessage());
        }
    }

    private static Set<String> parseCACell (String s) {
        return new HashSet<>(List.of(s.split("\\|")));
    }
}
