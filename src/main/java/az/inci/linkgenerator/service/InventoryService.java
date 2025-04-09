package az.inci.linkgenerator.service;

import az.inci.linkgenerator.data.DataSource;
import az.inci.linkgenerator.data.InvItem;
import az.inci.linkgenerator.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryService {
    private final Logger logger;
    private final DataSource dataSource;

    public InventoryService(Logger logger) {
        this.logger = logger;
        this.dataSource = new DataSource(logger);
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
            logger.logError(e.toString());
        }

        return list;
    }

    public List<InvItem> getSelection(List<String> codeList) {
        List<InvItem> list = new ArrayList<>();

        String whereClause = String.join(",", Collections.nCopies(codeList.size(), "?"));

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
            logger.logError(e.toString());
        }

        return list;
    }
}
