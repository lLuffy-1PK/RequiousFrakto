package com.bordlistian.requious.compat.crafttweaker;

import crafttweaker.api.item.IItemStack;
import com.bordlistian.requious.item.Shape;

public class ShapeSplit implements IShape {
    Shape shape;

    public ShapeSplit(Shape shape) {
        this.shape = shape;
    }

    @Override
    public Shape getShape() {
        return shape.toLayer();
    }

    @Override
    public IItemStack toItem() {
        return null;
    }
}
