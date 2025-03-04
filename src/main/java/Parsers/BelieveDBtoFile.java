package Parsers;

import Entities.BelieveDBEntry;
import Entities.Init;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class BelieveDBtoFile {
    public static void write (File f) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(f)) {

            Sheet sheet = workbook.createSheet("DB");
            Field[] fields = BelieveDBEntry.class.getDeclaredFields();

            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < fields.length; c++) {
                headerRow.createCell(c).setCellValue(fields[c].getName());
            }

            int r = 1;
            for (BelieveDBEntry h : Init.getEntityManager().createQuery("SELECT h from BelieveDBEntry h", BelieveDBEntry.class).getResultList()) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < fields.length; c++) {
                    Cell cell = row.createCell(c);
                    fields[c].setAccessible(true);
                    try {
                        cell.setCellValue((String) fields[c].get(h));
                    } catch (Exception _) {
                        try {
                            cell.setCellValue(String.valueOf(fields[c].get(h)));
                        } catch (Exception _) {}
                    }
                }
                r++;
            }

            workbook.write(fileOut);
            System.out.println("Excel file created successfully: " + f.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error creating Excel file: " + e.getMessage());
        }
    }
}
