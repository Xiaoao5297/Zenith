package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum BoundType {

    OPEN {
        @Override
        BoundType flip() {
            return null.CLOSED;
        }
    },
    CLOSED {
        @Override
        BoundType flip() {
            return null.OPEN;
        }
    };

    private BoundType() {}

    static BoundType forBoolean(boolean flag) {
        return flag ? BoundType.CLOSED : BoundType.OPEN;
    }

    abstract BoundType flip();
}
