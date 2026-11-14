package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
public final class Parameter implements AnnotatedElement {

    private final Invokable<?, ?> declaration;
    private final int position;
    private final TypeToken<?> type;
    private final ImmutableList<Annotation> annotations;

    Parameter(Invokable<?, ?> invokable, int i, TypeToken<?> typetoken, Annotation[] aannotation) {
        this.declaration = invokable;
        this.position = i;
        this.type = typetoken;
        this.annotations = ImmutableList.copyOf((Object[]) aannotation);
    }

    public TypeToken<?> getType() {
        return this.type;
    }

    public Invokable<?, ?> getDeclaringInvokable() {
        return this.declaration;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> oclass) {
        return this.getAnnotation(oclass) != null;
    }

    @Nullable
    public <A extends Annotation> A getAnnotation(Class<A> oclass) {
        Preconditions.checkNotNull(oclass);
        Iterator iterator = this.annotations.iterator();

        Annotation annotation;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            annotation = (Annotation) iterator.next();
        } while (!oclass.isInstance(annotation));

        return (Annotation) oclass.cast(annotation);
    }

    public Annotation[] getAnnotations() {
        return this.getDeclaredAnnotations();
    }

    public Annotation[] getDeclaredAnnotations() {
        return (Annotation[]) this.annotations.toArray(new Annotation[this.annotations.size()]);
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Parameter)) {
            return false;
        } else {
            Parameter parameter = (Parameter) object;

            return this.position == parameter.position && this.declaration.equals(parameter.declaration);
        }
    }

    public int hashCode() {
        return this.position;
    }

    public String toString() {
        return this.type + " arg" + this.position;
    }
}
