package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

@Beta
public abstract class TypeParameter<T> extends TypeCapture<T> {

    final TypeVariable<?> typeVariable;

    protected TypeParameter() {
        Type type = this.capture();

        Preconditions.checkArgument(type instanceof TypeVariable, "%s should be a type variable.", type);
        this.typeVariable = (TypeVariable) type;
    }

    public final int hashCode() {
        return this.typeVariable.hashCode();
    }

    public final boolean equals(@Nullable Object object) {
        if (object instanceof TypeParameter) {
            TypeParameter typeparameter = (TypeParameter) object;

            return this.typeVariable.equals(typeparameter.typeVariable);
        } else {
            return false;
        }
    }

    public String toString() {
        return this.typeVariable.toString();
    }
}
