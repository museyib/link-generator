package az.inci.linkgenerator;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LinkGenerator {

    private final MainWindowController controller;
    private final DataSource dataSource;

    public LinkGenerator(MainWindowController mainWindowController, DataSource dataSource) {
        this.controller = mainWindowController;
        this.dataSource = dataSource;
    }

    public void generateForAll() {
        List<InvItem> invItemList = getAllInvItems();
        invItemList.sort(Comparator.comparing(InvItem::getInvCode));
        List<String> codeList = getExistingInvCodeList();
        assignLinkToItems(codeList, invItemList);
    }

    private List<String> getExistingInvCodeList() {
        List<String> codeList = new ArrayList<>();
        FTPClient client = new FTPConnection(controller).connect();
        FTPFileFilter fileFilter = ftpFile -> ftpFile.getName().endsWith(".jpg");
        try {
            FTPFile[] ftpFiles = client.listFiles("/Inventory Images", fileFilter);

            controller.logInfo("Mövcud şəkil faylları siyahılanır...");
            for (FTPFile ftpFile : ftpFiles) {
                codeList.add(ftpFile.getName().substring(0, ftpFile.getName().indexOf(".")));
            }
        } catch (IOException e) {
            controller.logError(e.toString());
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                controller.logError(e.toString());
            }
        }

        return codeList;
    }

    private void writeToExcel(List<InvItem> invItemList) {
        controller.logInfo("Məlumat fayla yazılır...");
        Workbook workbook = new XSSFWorkbook();
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


        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle linkCellStyle = workbook.createCellStyle();
        linkCellStyle.setBorderBottom(BorderStyle.THIN);
        linkCellStyle.setBorderTop(BorderStyle.THIN);
        linkCellStyle.setBorderLeft(BorderStyle.THIN);
        linkCellStyle.setBorderRight(BorderStyle.THIN);
        linkCellStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle notFoundCellStyle = workbook.createCellStyle();
        notFoundCellStyle.setBorderBottom(BorderStyle.THIN);
        notFoundCellStyle.setBorderTop(BorderStyle.THIN);
        notFoundCellStyle.setBorderLeft(BorderStyle.THIN);
        notFoundCellStyle.setBorderRight(BorderStyle.THIN);
        notFoundCellStyle.setAlignment(HorizontalAlignment.RIGHT);

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
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.setForceFormulaRecalculation(true);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            controller.logInfo("Fayl hazırdır.");
            controller.logInfo("<a href='#' onclick=openFile('" + file.getAbsolutePath().replace("\\", "\\\\") + "')>Faylı aç</a>");
            controller.logInfo("<a href='#' onclick=openFolder('" + file.getAbsolutePath().replace("\\", "\\\\") + "')>Qovluğu aç</a>");
        } catch (IOException e) {
            controller.logError(e.toString());
        }
    }

    private void assignLinkToItems(List<String> codeList, List<InvItem> invItemList) {
        if (!codeList.isEmpty())
        {
            controller.logInfo("Linklər uyğun kodlara mənimsədilir...");
            for (InvItem invItem : invItemList)
                invItem.setHasLink(codeList.contains(invItem.getInvCode()));

            writeToExcel(invItemList);
        }
        else
            controller.logError("Kod siyahısı boşdur. Ən azı 1 kod əlavə edin.");
    }

    public void generateForSelected(List<String> selectedCodeList) {
        List<InvItem> invItemList = getSelection(selectedCodeList);
        invItemList.sort(Comparator.comparing(InvItem::getInvCode));
        List<String> codeList = getExistingInvCodeList();
        assignLinkToItems(codeList, invItemList);
    }


    public List<InvItem> getAllInvItems() {
        List<InvItem> list = new ArrayList<>();
        try (Connection connection = dataSource.connection()) {
            Statement createStatement = connection.createStatement();
            ResultSet resultSet = createStatement.executeQuery("SELECT INV_CODE, INV_NAME FROM INV_MASTER");
            while (resultSet.next()) {
                InvItem invItem = new InvItem();
                invItem.setInvCode(resultSet.getString(1));
                invItem.setInvName(resultSet.getString(2));
                list.add(invItem);
            }
        } catch (SQLException e) {
            controller.logError(e.toString());
        }

        return list;
    }


    public List<InvItem> getSelection(List<String> codeList) {
        List<InvItem> list = new ArrayList<>();

        if (codeList.isEmpty())
            return list;

        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < codeList.size(); i++) {
            whereClause.append("?").append(",");
        }

        whereClause.deleteCharAt(whereClause.length() - 1);

        String sqlString = "SELECT INV_CODE, INV_NAME FROM INV_MASTER WHERE INV_CODE IN (" + whereClause + ")";
        try (Connection connection = dataSource.connection()) {
            PreparedStatement statement = connection.prepareStatement(sqlString);
            for (int i = 0; i < codeList.size(); i++) {
                statement.setString(i + 1, codeList.get(i));
            }
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                InvItem invItem = new InvItem();
                invItem.setInvCode(resultSet.getString(1));
                invItem.setInvName(resultSet.getString(2));
                list.add(invItem);
            }
        } catch (SQLException e) {
            controller.logError(e.toString());
        }

        return list;
    }
}
