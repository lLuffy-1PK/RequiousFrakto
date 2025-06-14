package com.bordlistian.requious.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import crafttweaker.annotations.ZenRegister;
import com.bordlistian.requious.compat.crafttweaker.ColorCT;
import com.bordlistian.requious.item.ItemBattery;
import com.bordlistian.requious.util.color.ICustomColor;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.requious.Battery")
public class BatteryData extends ItemData {
    @SerializedName("capacity")
    public int capacity;
    @SerializedName("minInput")
    public int minInput = 0;
    @SerializedName("maxInput")
    public int maxInput = Integer.MAX_VALUE;
    @SerializedName("minOutput")
    public int minOutput = 0;
    @SerializedName("maxOutput")
    public int maxOutput = Integer.MAX_VALUE;
    @SerializedName("showTooltip")
    public boolean showToolip = true;

    @Expose(serialize = false, deserialize = false)
    public transient ICustomColor barColor;

    @Expose(serialize = false, deserialize = false)
    private transient ItemBattery item;

    public BatteryData() {

    }

    public ItemBattery getItem() {
        return item;
    }

    public void setItem(ItemBattery item) {
        this.item = item;
    }

    @ZenMethod
    public void showBar(ColorCT color) {
        this.barColor = color.get();
    }

    public boolean hasBar() {
        return barColor != null;
    }
}
