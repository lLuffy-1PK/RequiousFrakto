package com.bordlistian.requious.util;

public class RatioConversion implements IConversion {
    long valueOfBase;
    long valueOfUnit;

    public RatioConversion(long valueOfBase, long valueOfUnit) {
        this.valueOfBase = valueOfBase;
        this.valueOfUnit = valueOfUnit;
    }

    @Override
    public long getUnit(long base) {
        return (base * valueOfUnit) / valueOfBase;
    }

    @Override
    public long getBase(long unit) {
        return (unit * valueOfBase) / valueOfUnit;
    }
}
