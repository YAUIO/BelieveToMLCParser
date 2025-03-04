package Parsers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;


public class XLSXtoJavaClass {
    public static void main(String[] args) {
        System.out.println("Started generating Java class");
        FileInputStream file = null;
        Workbook sourceBook = null;

        FileOutputStream outStream = null;
        PrintWriter out = null;
        File table = null;
        try {
            file = new FileInputStream(args[0]);
            sourceBook = new XSSFWorkbook(file);

            table = new File(args[0].substring(0,args[0].lastIndexOf(".")) + ".java");
            if (table.exists()) throw new RuntimeException();
            System.out.println("Output: " + table.getAbsolutePath());

            outStream = new FileOutputStream(table);
            out = new PrintWriter(outStream, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Sheet sourceSheet = sourceBook.getSheetAt(0);
        Row header = sourceSheet.getRow(0);

        out.println("public class " + table.getName().substring(0, table.getName().lastIndexOf(".")) + " {");

        int i = 0;
        while (i < header.getPhysicalNumberOfCells() || header.getCell(i) != null) {
            String s = header.getCell(i).getStringCellValue();
            String orig = s;
            boolean alt = false;

            if (s.contains(" ")) {
                s = s.replace(' ', '_');
                alt = true;
            }

            if (alt) {
                out.println();
                out.println("@AlternateTitle(\"" + orig + "\")");
            }
            out.println("public String " + s + ";");
            i++;
        }

        out.println("}");


        System.out.println("Finished writing class");
    }
}
