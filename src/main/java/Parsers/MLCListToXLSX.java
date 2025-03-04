package Parsers;

import Entities.MLCEntry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MLCListToXLSX {
    public static void record(File f, ArrayList<MLCEntry> data) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(f)) {

            CellStyle yellowCellStyle = workbook.createCellStyle();
            yellowCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            yellowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Sheet sheet = workbook.createSheet("ConvertedToMLC");
            Field[] fields = MLCEntry.class.getDeclaredFields();

            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < fields.length; c++) {
                headerRow.createCell(c).setCellValue(fields[c].getName());
            }

            int r = 1;
            for (MLCEntry entry : data) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < fields.length; c++) {
                    Cell cell = row.createCell(c);
                    fields[c].setAccessible(true);

                    try {
                        if (fields[c].getType() == Integer.class) {
                            cell.setCellValue((Integer) fields[c].get(entry));
                        } else if (fields[c].getType() == Long.class) {
                            cell.setCellValue((Long) fields[c].get(entry));
                        } else if (fields[c].getType() == String.class) {
                            cell.setCellValue((String) fields[c].get(entry));
                            if (cell.getStringCellValue().equals("WRONGDATA")) {
                                cell.setCellStyle(yellowCellStyle);
                                row.getCell(c-1).setCellStyle(yellowCellStyle);
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
}
