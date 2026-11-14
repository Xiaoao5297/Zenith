package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement {

    private static final Class<?>[] PRIMITIVE_TYPES = new Class[]{Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
    private Object value;

    public JsonPrimitive(Boolean obool) {
        this.setValue(obool);
    }

    public JsonPrimitive(Number number) {
        this.setValue(number);
    }

    public JsonPrimitive(String s) {
        this.setValue(s);
    }

    public JsonPrimitive(Character character) {
        this.setValue(character);
    }

    JsonPrimitive(Object object) {
        this.setValue(object);
    }

    @Override
    JsonPrimitive deepCopy() {
        return this;
    }

    void setValue(Object object) {
        if (object instanceof Character) {
            char c0 = (Character) object;

            this.value = String.valueOf(c0);
        } else {
            $Gson$Preconditions.checkArgument(object instanceof Number || isPrimitiveOrString(object));
            this.value = object;
        }

    }

    public boolean isBoolean() {
        return this.value instanceof Boolean;
    }

    @Override
    Boolean getAsBooleanWrapper() {
        return (Boolean) this.value;
    }

    @Override
    public boolean getAsBoolean() {
        return this.isBoolean() ? this.getAsBooleanWrapper() : Boolean.parseBoolean(this.getAsString());
    }

    public boolean isNumber() {
        return this.value instanceof Number;
    }

    @Override
    public Number getAsNumber() {
        return (Number) (this.value instanceof String ? new LazilyParsedNumber((String) this.value) : (Number) this.value);
    }

    public boolean isString() {
        return this.value instanceof String;
    }

    @Override
    public String getAsString() {
        return this.isNumber() ? this.getAsNumber().toString() : (this.isBoolean() ? this.getAsBooleanWrapper().toString() : (String) this.value);
    }

    @Override
    public double getAsDouble() {
        return this.isNumber() ? this.getAsNumber().doubleValue() : Double.parseDouble(this.getAsString());
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return this.value instanceof BigDecimal ? (BigDecimal) this.value : new BigDecimal(this.value.toString());
    }

    @Override
    public BigInteger getAsBigInteger() {
        return this.value instanceof BigInteger ? (BigInteger) this.value : new BigInteger(this.value.toString());
    }

    @Override
    public float getAsFloat() {
        return this.isNumber() ? this.getAsNumber().floatValue() : Float.parseFloat(this.getAsString());
    }

    @Override
    public long getAsLong() {
        return this.isNumber() ? this.getAsNumber().longValue() : Long.parseLong(this.getAsString());
    }

    @Override
    public short getAsShort() {
        return this.isNumber() ? this.getAsNumber().shortValue() : Short.parseShort(this.getAsString());
    }

    @Override
    public int getAsInt() {
        return this.isNumber() ? this.getAsNumber().intValue() : Integer.parseInt(this.getAsString());
    }

    @Override
    public byte getAsByte() {
        return this.isNumber() ? this.getAsNumber().byteValue() : Byte.parseByte(this.getAsString());
    }

    @Override
    public char getAsCharacter() {
        return this.getAsString().charAt(0);
    }

    private static boolean isPrimitiveOrString(Object object) {
        if (object instanceof String) {
            return true;
        } else {
            Class oclass = object.getClass();
            Class[] aclass = JsonPrimitive.PRIMITIVE_TYPES;
            int i = aclass.length;

            for (int j = 0; j < i; ++j) {
                Class oclass1 = aclass[j];

                if (oclass1.isAssignableFrom(oclass)) {
                    return true;
                }
            }

            return false;
        }
    }

    public int hashCode() {
        if (this.value == null) {
            return 31;
        } else {
            long i;

            if (isIntegral(this)) {
                i = this.getAsNumber().longValue();
                return (int) (i ^ i >>> 32);
            } else if (this.value instanceof Number) {
                i = Double.doubleToLongBits(this.getAsNumber().doubleValue());
                return (int) (i ^ i >>> 32);
            } else {
                return this.value.hashCode();
            }
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            JsonPrimitive jsonprimitive = (JsonPrimitive) object;

            if (this.value == null) {
                return jsonprimitive.value == null;
            } else if (isIntegral(this) && isIntegral(jsonprimitive)) {
                return this.getAsNumber().longValue() == jsonprimitive.getAsNumber().longValue();
            } else if (this.value instanceof Number && jsonprimitive.value instanceof Number) {
                double d0 = this.getAsNumber().doubleValue();
                double d1 = jsonprimitive.getAsNumber().doubleValue();

                return d0 == d1 || Double.isNaN(d0) && Double.isNaN(d1);
            } else {
                return this.value.equals(jsonprimitive.value);
            }
        } else {
            return false;
        }
    }

    private static boolean isIntegral(JsonPrimitive jsonprimitive) {
        if (!(jsonprimitive.value instanceof Number)) {
            return false;
        } else {
            Number number = (Number) jsonprimitive.value;

            return number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte;
        }
    }
}
