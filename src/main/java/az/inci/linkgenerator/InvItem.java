package az.inci.linkgenerator;

public class InvItem {

    private String invCode;

    private String invName;

    private boolean hasLink;

    public String getInvCode() {
        return invCode;
    }

    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public boolean hasLink() {
        return hasLink;
    }

    public void setHasLink(boolean hasLink) {
        this.hasLink = hasLink;
    }

    @Override
    public String toString() {
        return "InvLink{" +
                "invCode='" + invCode + '\'' +
                ", invName='" + invName + '\'' +
                ", link='" + hasLink + '\'' +
                '}';
    }
}
