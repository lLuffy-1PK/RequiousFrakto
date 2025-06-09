package com.bordlistian.requious.tile;

import com.bordlistian.requious.entity.EntitySpark;
import com.bordlistian.requious.entity.ISparkValue;

public interface ISparkAcceptor {
    void receive(EntitySpark spark);

    boolean canAccept(ISparkValue value);
}
