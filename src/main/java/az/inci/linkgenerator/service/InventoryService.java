package az.inci.linkgenerator.service;

import az.inci.linkgenerator.data.InvItem;

import java.util.List;

public interface InventoryService {
    List<InvItem> getAllInvItems();
    List<InvItem> getSelection(List<String> codeList);
}
