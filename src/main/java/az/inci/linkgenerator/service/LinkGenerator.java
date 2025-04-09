package az.inci.linkgenerator.service;

import az.inci.linkgenerator.data.InvItem;
import az.inci.linkgenerator.util.Logger;
import az.inci.linkgenerator.util.UIInteraction;

import java.util.Comparator;
import java.util.List;

public class LinkGenerator {
    private final UIInteraction uiInteraction;
    private final Logger logger;
    private final InventoryService inventoryService;
    private final FileService fileService;
    private final FtpService ftpService;

    public LinkGenerator(UIInteraction uiInteraction,
                         Logger logger,
                         InventoryService inventoryService,
                         FileService fileService,
                         FtpService ftpService) {
        this.uiInteraction = uiInteraction;
        this.logger = logger;
        this.inventoryService = inventoryService;
        this.fileService = fileService;
        this.ftpService = ftpService;
    }

    public void generateForSelected(List<String> selectedCodeList) {
        List<InvItem> invItemList = inventoryService.getSelection(selectedCodeList);
        if (invItemList.isEmpty()) {
            logger.logWarning("Daxil edilən kodlara uyğun mal tapılmadı.");
            uiInteraction.focusOnInvCodeList();
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
