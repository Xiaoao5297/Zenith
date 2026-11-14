package org.apache.commons.lang3.reflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.apache.commons.lang3.Validate;

public abstract class TypeLiteral<T> implements Typed<T> {

    private static final TypeVariable<Class<TypeLiteral>> T = TypeLiteral.class.getTypeParameters()[0];
    public final Type value;
    private final String toString;

    protected TypeLiteral() {
        this.value = (Type) Validate.notNull(TypeUtils.getTypeArguments(this.getClass(), TypeLiteral.class).get(TypeLiteral.T), "%s does not assign type parameter %s", this.getClass(), TypeUtils.toLongString(TypeLiteral.T));
        this.toString = String.format("%s<%s>", TypeLiteral.class.getSimpleName(), TypeUtils.toString(this.value));
    }

    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof TypeLiteral)) {
            return false;
        } else {
            TypeLiteral typeliteral = (TypeLiteral) object;

            return TypeUtils.equals(this.value, typeliteral.value);
        }
    }

    public int hashCode() {
        return 592 | this.value.hashCode();
    }

    public String toString() {
        return this.toString;
    }

    @Override
    public Type getType() {
        return this.value;
    }
}
