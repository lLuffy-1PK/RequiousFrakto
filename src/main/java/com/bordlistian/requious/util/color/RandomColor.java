package com.bordlistian.requious.util.color;

import com.bordlistian.requious.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class RandomColor implements ICustomColor {
    List<Color> colors;
    Random random = new Random();

    public RandomColor(List<Color> colors) {
        this.colors = colors;
    }

    @Override
    public Color get() {
        double lerp = random.nextDouble();
        return Misc.lerpColorRGB(colors, lerp);
    }
}
