package org.tiny.gear;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileSystemResourceStream;
import org.tiny.datawrapper.Condition;
import org.tiny.gear.panels.crud.DataTableView;
import wicket.util.file.File;

/**
 * エクセルファイルを生成する。
 *
 * @author dtmoyaji
 */
public class ExcelFileGenerator {

    public ExcelFileGenerator() {
    }

    public void generate(DataTableView src, Condition... conditions) {
        
        String excelFilePath = this.getFileName(src);
        ResourceStreamRequestHandler rvalue = null;

        try (ResultSet rs = src.getTable().select(conditions); Workbook book = new XSSFWorkbook()) {

            Sheet sheet = book.createSheet();
            book.setSheetName(0, "結果");

            // 枠線の扱い
            CellStyle cellStyle = book.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);

            int rowoffset = 0; // 出力開始行

            // 先頭行の作成
            ResultSetMetaData rsmeta = rs.getMetaData();
            Row row = sheet.createRow(rowoffset);
            for (int i = 0; i < rsmeta.getColumnCount(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(rsmeta.getColumnName(i + 1));
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(i, 6000);
            }
            rowoffset++;

            // データの生成
            while (rs.next()) {
                row = sheet.createRow(rowoffset);
                for (int i = 0; i < rsmeta.getColumnCount(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(rs.getString(i + 1));
                    cell.setCellStyle(cellStyle);
                }
                rowoffset++;
            }
            FileOutputStream fos = new FileOutputStream(excelFilePath);
            book.write(fos);
            File file = new File(excelFilePath);
            FileSystemResourceStream frs = new FileSystemResourceStream(file.toPath());
            rvalue = new ResourceStreamRequestHandler(
                    frs,
                    excelFilePath
            );
            src.getRequestCycle().scheduleRequestHandlerAfterCurrent(rvalue);

        } catch (SQLException | IOException ex) {
            Logger.getLogger(ExcelFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getFileName(DataTableView src) {
        String template = "%s/%s_%s.xlsx";
        String className = src.getTable().getClass().getSimpleName();
        String approute = src.getGearApplication().getRealPath("files");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String rvalue = String.format(template, approute, className, timestamp);
        return rvalue;
    }

}
