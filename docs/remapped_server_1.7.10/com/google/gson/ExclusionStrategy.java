package com.google.gson;

public interface ExclusionStrategy {

    boolean shouldSkipField(FieldAttributes fieldattributes);

    boolean shouldSkipClass(Class<?> oclass);
}
