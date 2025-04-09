package az.inci.linkgenerator.service;

import az.inci.linkgenerator.data.InvItem;
import az.inci.linkgenerator.util.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileServiceImpl implements FileService {
    private final Logger logger;
    private Workbook workbook;

    public FileServiceImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void writeToExcel(List<InvItem> invItemList) {
        logger.logInfo("Məlumat fayla yazılır...");
        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 15000);
        sheet.setColumnWidth(2, 10000);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

        Row header = sheet.createRow(0);

        Cell headerCell = header.createCell(0);
        headerCell.setCellStyle(headerCellStyle);
        headerCell.setCellValue("Mal kodu");

        headerCell = header.createCell(1);
        headerCell.setCellStyle(headerCellStyle);
        headerCell.setCellValue("Mal adı");

        headerCell = header.createCell(2);
        headerCell.setCellStyle(headerCellStyle);
        headerCell.setCellValue("Link");

        CellStyle dataCellStyle = createCellStyle(HorizontalAlignment.LEFT);
        CellStyle linkCellStyle = createCellStyle(HorizontalAlignment.LEFT);
        CellStyle notFoundCellStyle = createCellStyle(HorizontalAlignment.RIGHT);

        Font linkFont = workbook.createFont();
        linkFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        linkFont.setUnderline(Font.U_SINGLE);
        linkCellStyle.setFont(linkFont);

        Font notFoundFont = workbook.createFont();
        notFoundFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        notFoundFont.setBold(true);
        notFoundCellStyle.setFont(notFoundFont);

        for (int i = 0; i < invItemList.size(); i++) {
            InvItem invItem = invItemList.get(i);
            Row row = sheet.createRow(i + 1);
            Cell codeCell = row.createCell(0);
            codeCell.setCellStyle(dataCellStyle);
            codeCell.setCellValue(invItem.getInvCode());

            Cell nameCell = row.createCell(1);
            nameCell.setCellStyle(dataCellStyle);
            nameCell.setCellValue(invItem.getInvName());

            Cell linkCell = row.createCell(2);
            if (invItem.hasLink()) {
                linkCell.setCellStyle(linkCellStyle);
                linkCell.setCellFormula("HYPERLINK(CONCATENATE(\"http://185.129.0.46:8025/\"," + codeCell.getAddress().toString() + ",\".jpg\"))");
            } else {
                linkCell.setCellStyle(notFoundCellStyle);
                linkCell.setCellValue("Şəkil tapılmadı!");
            }
        }


        try {
            File file = new File("Links.xlsx");

            int n = 1;
            while (file.exists()) {
                file = new File("Links_" + n + ".xlsx");
                n++;
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.setForceFormulaRecalculation(true);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            String filename = file.getAbsolutePath().replace("\\", "\\\\");
            logger.logInfo("Fayl hazırdır: <a href='#' onclick=\"openFile('" + filename + "')\">" + filename + "</a>; ");
            logger.logInfo("<a href='#' onclick=\"openFolder('" + filename + "')\">Qovluğu aç</a>");
        } catch (IOException e) {
            logger.logError(e.toString());
        }
    }

    private CellStyle createCellStyle(HorizontalAlignment alignment) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(alignment);

        return cellStyle;
    }
}
