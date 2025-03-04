package Parsers;

import Entities.BelieveEntity;
import Entities.MLCEntry;
import Entities.Person;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class XLSXMLCToList {
    public static Collection<? extends MLCEntry> parse(File source) {
        System.out.println("Started parsing MLC table");
        FileInputStream file = null;
        Workbook sourceBook = null;
        try {
            file = new FileInputStream(source);
            sourceBook = new XSSFWorkbook(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Sheet sourceSheet = sourceBook.getSheetAt(0);
        Row header = sourceSheet.getRow(0);

        Class<?> entryClass = MLCEntry.class;
        Field[] fields = entryClass.getDeclaredFields();

        ArrayList<MLCEntry> data = new ArrayList<>();

        for (int i = 1; i <= sourceSheet.getLastRowNum(); i++) {
            Row row = sourceSheet.getRow(i);
            if (row == null) {
                continue;
            }
            if (row.getCell(0) == null || (row.getCell(0).getCellType() == CellType.STRING && row.getCell(0).getStringCellValue() == null)) {
                break;
            }
            MLCEntry entity = new MLCEntry();
            for (int cell = 0; cell < fields.length; cell++) {
                Cell val = row.getCell(cell);

                Class<?> expectedType = fields[cell].getType();
                Object obj = null;

                if (val != null) {
                    if (expectedType == Date.class) {
                        try {
                            obj = val.getDateCellValue();
                        } catch (Exception e) {
                            try {
                                obj = DateFormat.getDateInstance().parse(val.getStringCellValue());
                            } catch (Exception _) {
                            }
                        }
                    } else if (expectedType == String.class) {
                        try {
                            String s = val.getStringCellValue();
                            if (s.contains("\n")) {
                                int pos = s.lastIndexOf('\n');
                                if (s.length() > pos + 1) {
                                    if (s.charAt(pos - 1) != ' ' || s.charAt(pos + 1) != ' ') {
                                        obj = s.replace('\n', ' ');
                                    } else {
                                        obj = s.substring(0, pos) + s.substring(pos + 1);
                                    }
                                } else {
                                    if (s.charAt(pos - 1) != ' ') {
                                        obj = s.replace('\n', ' ');
                                    } else {
                                        obj = s.substring(0, pos);
                                    }
                                }
                            } else {
                                obj = s;
                            }
                            //System.out.println("DEBUG: Input String: \"" + val.getStringCellValue() + "\", Parsed String: \"" + ((String) obj) + "\"");
                        } catch (Exception e) {
                            if (val.getCellType() == CellType.NUMERIC) {
                                long number = (long) val.getNumericCellValue();
                                obj = String.valueOf(number);
                            }
                        }
                    } else if (expectedType == Integer.class) {
                        try {
                            obj = Double.valueOf(val.getNumericCellValue()).intValue();
                        } catch (Exception e) {
                        }
                    } else if (expectedType == Time.class) {
                        try {
                            obj = new Time(val.getDateCellValue().getTime());
                        } catch (Exception e) {
                        }
                    }
                }

                try {
                    fields[cell].setAccessible(true);
                    fields[cell].set(entity, obj);
                } catch (IllegalAccessException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (entity.RECORDING_ISRC == null || entity.RECORDING_ISRC.isEmpty() || entity.RECORDING_ISRC.isBlank()) {
                continue;
            }
            Person.write(entity);
            data.add(entity);
            //System.out.println("Parsed row " + (i + 1) + "/" + (sourceSheet.getLastRowNum() + 1) + " | " + entity);
        }

        System.out.println("Finished parsing MLC table\n");

        return data;
    }
}
