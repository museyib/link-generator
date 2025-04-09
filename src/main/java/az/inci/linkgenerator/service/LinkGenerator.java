package az.inci.linkgenerator.service;

import az.inci.linkgenerator.controller.MainWindowController;
import az.inci.linkgenerator.data.InvItem;
import az.inci.linkgenerator.util.Logger;
import javafx.application.Platform;

import java.util.Comparator;
import java.util.List;

public class LinkGenerator {

    private final MainWindowController controller;
    private final Logger logger;
    private final InventoryService inventoryService;
    private final FileService fileService;
    private final FtpService ftpService;

    public LinkGenerator(MainWindowController controller, Logger logger) {
        this.controller = controller;
        this.logger = logger;
        this.inventoryService = new InventoryService(logger);
        this.fileService = new FileService(logger);
        this.ftpService = new FtpService(logger);
    }

    public void generateForSelected(List<String> selectedCodeList) {
        List<InvItem> invItemList = inventoryService.getSelection(selectedCodeList);
        if (invItemList.isEmpty()) {
            logger.logWarning("Daxil edilən kodlara uyğun mal tapılmadı.");
            Platform.runLater(controller::focusOnInvCodeList);
        }
        else {
            invItemList.sort(Comparator.comparing(InvItem::getInvCode));
            List<String> codeList = ftpService.listImageCodes();
            assignLinkToItems(codeList, invItemList);
        }
    }

    public void generateForAll() {
        List<InvItem> invItemList = inventoryService.getAllInvItems();
        invItemList.sort(Comparator.comparing(InvItem::getInvCode));
        List<String> codeList = ftpService.listImageCodes();
        assignLinkToItems(codeList, invItemList);
    }

    private void assignLinkToItems(List<String> codeList, List<InvItem> invItemList) {
        logger.logInfo("Linklər uyğun kodlara mənimsədilir...");
        for (InvItem invItem : invItemList)
            invItem.hasLink(codeList.contains(invItem.getInvCode()));

        fileService.writeToExcel(invItemList);
    }
}
