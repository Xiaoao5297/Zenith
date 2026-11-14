package io.netty.channel.nio;

import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Iterator;

final class SelectedSelectionKeySet extends AbstractSet<SelectionKey> {

    private SelectionKey[] keysA = new SelectionKey[1024];
    private int keysASize;
    private SelectionKey[] keysB;
    private int keysBSize;
    private boolean isA = true;

    SelectedSelectionKeySet() {
        this.keysB = (SelectionKey[]) this.keysA.clone();
    }

    public boolean add(SelectionKey selectionkey) {
        if (selectionkey == null) {
            return false;
        } else {
            int i;

            if (this.isA) {
                i = this.keysASize;
                this.keysA[i++] = selectionkey;
                this.keysASize = i;
                if (i == this.keysA.length) {
                    this.doubleCapacityA();
                }
            } else {
                i = this.keysBSize;
                this.keysB[i++] = selectionkey;
                this.keysBSize = i;
                if (i == this.keysB.length) {
                    this.doubleCapacityB();
                }
            }

            return true;
        }
    }

    private void doubleCapacityA() {
        SelectionKey[] aselectionkey = new SelectionKey[this.keysA.length << 1];

        System.arraycopy(this.keysA, 0, aselectionkey, 0, this.keysASize);
        this.keysA = aselectionkey;
    }

    private void doubleCapacityB() {
        SelectionKey[] aselectionkey = new SelectionKey[this.keysB.length << 1];

        System.arraycopy(this.keysB, 0, aselectionkey, 0, this.keysBSize);
        this.keysB = aselectionkey;
    }

    SelectionKey[] flip() {
        if (this.isA) {
            this.isA = false;
            this.keysA[this.keysASize] = null;
            this.keysBSize = 0;
            return this.keysA;
        } else {
            this.isA = true;
            this.keysB[this.keysBSize] = null;
            this.keysASize = 0;
            return this.keysB;
        }
    }

    public int size() {
        return this.isA ? this.keysASize : this.keysBSize;
    }

    public boolean remove(Object object) {
        return false;
    }

    public boolean contains(Object object) {
        return false;
    }

    public Iterator<SelectionKey> iterator() {
        throw new UnsupportedOperationException();
    }
}
