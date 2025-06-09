package com.bordlistian.requious.compat.crafttweaker;

import com.bordlistian.requious.item.Shape;

public class ShapeBase implements IShape {
    Shape shape;

    public ShapeBase(Shape shape) {
        this.shape = shape;
    }

    @Override
    public Shape getShape() {
        return shape;
    }
}
