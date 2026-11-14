package com.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JsonArray extends JsonElement implements Iterable<JsonElement> {

    private final List<JsonElement> elements = new ArrayList();

    public JsonArray() {}

    @Override
    JsonArray deepCopy() {
        JsonArray jsonarray = new JsonArray();
        Iterator iterator = this.elements.iterator();

        while (iterator.hasNext()) {
            JsonElement jsonelement = (JsonElement) iterator.next();

            jsonarray.add(jsonelement.deepCopy());
        }

        return jsonarray;
    }

    public void add(JsonElement jsonelement) {
        if (jsonelement == null) {
            jsonelement = JsonNull.INSTANCE;
        }

        this.elements.add(jsonelement);
    }

    public void addAll(JsonArray jsonarray) {
        this.elements.addAll(jsonarray.elements);
    }

    public int size() {
        return this.elements.size();
    }

    public Iterator<JsonElement> iterator() {
        return this.elements.iterator();
    }

    public JsonElement get(int i) {
        return (JsonElement) this.elements.get(i);
    }

    @Override
    public Number getAsNumber() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsNumber();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAsString() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsString();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public double getAsDouble() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsDouble();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsBigDecimal();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public BigInteger getAsBigInteger() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsBigInteger();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public float getAsFloat() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsFloat();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public long getAsLong() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsLong();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public int getAsInt() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsInt();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public byte getAsByte() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsByte();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public char getAsCharacter() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsCharacter();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public short getAsShort() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsShort();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean getAsBoolean() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsBoolean();
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean equals(Object object) {
        return object == this || object instanceof JsonArray && ((JsonArray) object).elements.equals(this.elements);
    }

    public int hashCode() {
        return this.elements.hashCode();
    }
}
