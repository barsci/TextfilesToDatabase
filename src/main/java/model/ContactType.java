package model;

public enum ContactType {
    UNKNOWN(0),
    EMAIL(1),
    PHONE(2),
    JABBER(3);

    private int numVal;

    ContactType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
