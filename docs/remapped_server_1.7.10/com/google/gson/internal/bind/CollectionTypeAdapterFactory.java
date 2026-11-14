package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

public final class CollectionTypeAdapterFactory implements TypeAdapterFactory {

    private final ConstructorConstructor constructorConstructor;

    public CollectionTypeAdapterFactory(ConstructorConstructor constructorconstructor) {
        this.constructorConstructor = constructorconstructor;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
        Type type = typetoken.getType();
        Class oclass = typetoken.getRawType();

        if (!Collection.class.isAssignableFrom(oclass)) {
            return null;
        } else {
            Type type1 = $Gson$Types.getCollectionElementType(type, oclass);
            TypeAdapter typeadapter = gson.getAdapter(TypeToken.get(type1));
            ObjectConstructor objectconstructor = this.constructorConstructor.get(typetoken);
            CollectionTypeAdapterFactory.Adapter collectiontypeadapterfactory_adapter = new CollectionTypeAdapterFactory.Adapter<>(gson, type1, typeadapter, objectconstructor);

            return collectiontypeadapterfactory_adapter;
        }
    }

    private static final class Adapter<E> extends TypeAdapter<Collection<E>> {

        private final TypeAdapter<E> elementTypeAdapter;
        private final ObjectConstructor<? extends Collection<E>> constructor;

        public Adapter(Gson gson, Type type, TypeAdapter<E> typeadapter, ObjectConstructor<? extends Collection<E>> objectconstructor) {
            this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(gson, typeadapter, type);
            this.constructor = objectconstructor;
        }

        @Override
        public Collection<E> read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                Collection collection = (Collection) this.constructor.construct();

                jsonreader.beginArray();

                while (jsonreader.hasNext()) {
                    Object object = this.elementTypeAdapter.read(jsonreader);

                    collection.add(object);
                }

                jsonreader.endArray();
                return collection;
            }
        }

        public void write(JsonWriter jsonwriter, Collection<E> collection) throws IOException {
            if (collection == null) {
                jsonwriter.nullValue();
            } else {
                jsonwriter.beginArray();
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    Object object = iterator.next();

                    this.elementTypeAdapter.write(jsonwriter, object);
                }

                jsonwriter.endArray();
            }
        }
    }
}
