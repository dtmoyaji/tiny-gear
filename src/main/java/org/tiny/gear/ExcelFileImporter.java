package org.tiny.gear;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;

/**
 *
 * @author dtmoyaji
 */
public class ExcelFileImporter {

    private Table targetTable;

    public ExcelFileImporter() {

    }

    public boolean importTableData(Table targetTable, File xlsxFile) {
        boolean rvalue = true;
        this.targetTable = targetTable;
        try (Workbook book = WorkbookFactory.create(xlsxFile)) {
            Sheet sheet = book.getSheetAt(0);

            boolean verify = true;
            Row row = sheet.getRow(0);
            for (int i = 0; i < this.targetTable.size(); i++) {
                Cell cell = row.getCell(i);
                if (!this.targetTable.get(i).getSplitedName().equals(cell.getStringCellValue())) {
                    verify = false;
                    break;
                }
            }

            if (!verify) {
                rvalue = false;
            } else {
                int rowid = 1;
                DataFormatter formatter = new DataFormatter();
                
                while (this.hasData(sheet.getRow(rowid))) {
                    row = sheet.getRow(rowid);
                    this.targetTable.clearValues();
                    for (int cellid = 0; cellid < this.targetTable.size(); cellid++) {
                        Column col = this.targetTable.get(cellid);
                        Cell cell = row.getCell(cellid);
                        String cellValue = formatter.formatCellValue(cell);
                        col.setValue(cellValue);
                    }
                    this.targetTable.merge();
                    rowid++;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelFileImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcelFileImporter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rvalue;
    }

    public boolean hasData(Row row) {
        boolean rvalue = false;
        if (row != null) {
            for (int i = 0; i < this.targetTable.size(); i++) {
                if (row.getCell(i).getStringCellValue().length() > 0) {
                    rvalue = true;
                    break;
                }
            }
        }
        return rvalue;
    }
}
