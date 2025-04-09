package az.inci.linkgenerator.service;

import az.inci.linkgenerator.data.InvItem;

import java.util.List;

public interface FileService {
    void writeToExcel(List<InvItem> invItemList);
}
