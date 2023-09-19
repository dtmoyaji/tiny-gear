package org.tiny.gear;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
            ArrayList<String> headerNames = new ArrayList<>();
            int colNum = 0;

            Cell headerCell = row.getCell(colNum);
            while (headerCell != null) {
                if (headerCell.getCellType() != CellType.BLANK) {
                    headerNames.add(new DataFormatter().formatCellValue(row.getCell(colNum)));
                    colNum++;
                    headerCell = row.getCell(colNum);
                }else{
                    headerCell = null;
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
                    for (int cellid = 0; cellid < headerNames.size(); cellid++) {
                        Column col = this.targetTable.get(headerNames.get(cellid));
                        Cell cell = row.getCell(cellid);
                        String cellValue = formatter.formatCellValue(cell);
                        if(cellValue!=null){
                            if(cellValue.length() < 1 && col.isNullable()){
                                cellValue = null;
                            }
                        }
                        col.setValue(cellValue);
                    }
                    this.targetTable.setDebugMode(true);
                    this.targetTable.merge();
                    this.targetTable.setDebugMode(false);
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
                if (row.getCell(i).getCellType() != CellType.BLANK) {
                    rvalue = true;
                    break;
                }
            }
        }
        return rvalue;
    }
}
