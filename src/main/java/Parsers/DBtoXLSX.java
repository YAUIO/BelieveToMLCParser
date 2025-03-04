package Parsers;

import Entities.Human;
import Entities.Init;
import Entities.MLCEntry;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class DBtoXLSX {
    public static void write(File f) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(f)) {

            Sheet sheet = workbook.createSheet("DB");
            Field[] fields = Human.class.getDeclaredFields();

            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < fields.length; c++) {
                headerRow.createCell(c).setCellValue(fields[c].getName());
            }

            int r = 1;
            for (Human h : Init.getEntityManager().createQuery("SELECT h from Human h", Human.class).getResultList()) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < fields.length; c++) {
                    Cell cell = row.createCell(c);
                    fields[c].setAccessible(true);

                    try {
                        if (fields[c].getType() == Integer.class) {
                            cell.setCellValue((Integer) fields[c].get(h));
                        } else if (fields[c].getType() == String.class) {
                            cell.setCellValue((String) fields[c].get(h));
                        } else if (fields[c].getType() == Map.class) {
                            if (!h.fisur.isEmpty()) {
                                Sheet mapSheet = workbook.createSheet(h.name);

                                cell.setCellValue("LINK");

                                // Create a hyperlink
                                CreationHelper createHelper = workbook.getCreationHelper();
                                Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
                                link.setAddress("'" + mapSheet.getSheetName() + "'!A1");
                                cell.setHyperlink(link);

                                // Apply a style to indicate it's a hyperlink
                                CellStyle hlinkStyle = workbook.createCellStyle();
                                Font hlinkFont = workbook.createFont();
                                hlinkFont.setUnderline(Font.U_SINGLE);
                                hlinkFont.setColor(IndexedColors.BLUE.getIndex());
                                hlinkStyle.setFont(hlinkFont);
                                cell.setCellStyle(hlinkStyle);

                                writeMap(h.fisur, mapSheet);
                            } else {
                                cell.setCellValue("NULL");
                            }
                        }
                    } catch (Exception e) {
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

    private static void writeMap(Map<String, String> map, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("FullName");
        headerRow.createCell(1).setCellValue("Surname");

        int r = 1;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Row row = sheet.createRow(r);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
            r++;
        }
    }
}
