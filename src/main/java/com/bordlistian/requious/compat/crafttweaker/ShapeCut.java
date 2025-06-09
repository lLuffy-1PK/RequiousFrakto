package com.bordlistian.requious.compat.crafttweaker;

import com.bordlistian.requious.item.Shape;

public class ShapeCut implements IShape {
    Shape shape;
    Shape.Piece piece;

    public ShapeCut(Shape shape, Shape.Piece piece) {
        this.shape = shape;
        this.piece = piece;
    }

    @Override
    public Shape getShape() {
        Shape cut = new Shape();
        cut.setPart(piece, shape.getPart(piece));
        return cut;
    }
}
