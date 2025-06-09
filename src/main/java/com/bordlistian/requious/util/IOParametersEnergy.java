package com.bordlistian.requious.util;

public class IOParametersEnergy {
    public int slot;
    public long size;
    public boolean active;

    public IOParametersEnergy(long size, int slot) {
        this.slot = slot;
        this.size = size;
        this.active = true;
    }

    public IOParametersEnergy(long size) {
        this.slot = -1;
        this.size = size;
        this.active = true;
    }

    public IOParametersEnergy() {
        this.active = false;
    }
}
