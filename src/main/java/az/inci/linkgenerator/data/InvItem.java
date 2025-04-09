package az.inci.linkgenerator.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class InvItem {

    private String invCode;

    private String invName;

    @Accessors(fluent = true)
    private boolean hasLink;
}
