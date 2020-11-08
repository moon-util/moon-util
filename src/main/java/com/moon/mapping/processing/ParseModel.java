package com.moon.mapping.processing;

/**
 * @author benshaoye
 */
final class ParseModel {

    private String returnType;
    private String returnName;
    private String thisType;
    private String thatType;
    private String thisName;
    private String thatName;
    private String thisCastedName;
    private String thatCastedName;

    public ParseModel() {}

    public String getReturnType() { return returnType; }

    public String getReturnName() { return returnName; }

    public String getThisType() { return thisType; }

    public String getThatType() { return thatType; }

    public String getThisName() { return thisName; }

    public String getThatName() { return thatName; }

    public String getThisCastedName() { return thisCastedName; }

    public String getThatCastedName() { return thatCastedName; }

    final void setReturnType(String returnType) { this.returnType = returnType; }

    final void setReturnName(String returnName) { this.returnName = returnName; }

    final void setThisType(String thisType) { this.thisType = thisType; }

    final void setThatType(String thatType) { this.thatType = thatType; }

    final void setThisName(String thisName) { this.thisName = thisName; }

    final void setThatName(String thatName) { this.thatName = thatName; }

    final void setThisCastedName(String thisCastedName) { this.thisCastedName = thisCastedName; }

    final void setThatCastedName(String thatCastedName) { this.thatCastedName = thatCastedName; }
}
