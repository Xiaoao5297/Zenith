package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class ConstructorConstructor {

    private final Map<Type, InstanceCreator<?>> instanceCreators;

    public ConstructorConstructor(Map<Type, InstanceCreator<?>> map) {
        this.instanceCreators = map;
    }

    public <T> ObjectConstructor<T> get(TypeToken<T> typetoken) {
        final Type type = typetoken.getType();
        Class oclass = typetoken.getRawType();
        final InstanceCreator instancecreator = (InstanceCreator) this.instanceCreators.get(type);

        if (instancecreator != null) {
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    return instancecreator.createInstance(type);
                }
            };
        } else {
            final InstanceCreator instancecreator1 = (InstanceCreator) this.instanceCreators.get(oclass);

            if (instancecreator1 != null) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return instancecreator1.createInstance(type);
                    }
                };
            } else {
                ObjectConstructor objectconstructor = this.newDefaultConstructor(oclass);

                if (objectconstructor != null) {
                    return objectconstructor;
                } else {
                    ObjectConstructor objectconstructor1 = this.newDefaultImplementationConstructor(type, oclass);

                    return objectconstructor1 != null ? objectconstructor1 : this.newUnsafeAllocator(type, oclass);
                }
            }
        }
    }

    private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> oclass) {
        try {
            final Constructor constructor = oclass.getDeclaredConstructor();

            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    try {
                        Object object = null;

                        return constructor.newInstance((Object[]) object);
                    } catch (InstantiationException instantiationexception) {
                        throw new RuntimeException("Failed to invoke " + constructor + " with no args", instantiationexception);
                    } catch (InvocationTargetException invocationtargetexception) {
                        throw new RuntimeException("Failed to invoke " + constructor + " with no args", invocationtargetexception.getTargetException());
                    } catch (IllegalAccessException illegalaccessexception) {
                        throw new AssertionError(illegalaccessexception);
                    }
                }
            };
        } catch (NoSuchMethodException nosuchmethodexception) {
            return null;
        }
    }

    private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> oclass) {
        return Collection.class.isAssignableFrom(oclass) ? (SortedSet.class.isAssignableFrom(oclass) ? new ObjectConstructor<T>() {
            @Override
            public T construct() {
                return new TreeSet();
            }
        } : (EnumSet.class.isAssignableFrom(oclass) ? new ObjectConstructor<T>() {
            @Override
            public T construct() {
                if (type instanceof ParameterizedType) {
                    Type type1 = ((ParameterizedType) type).getActualTypeArguments()[0];

                    if (type1 instanceof Class) {
                        return EnumSet.noneOf((Class) type1);
                    } else {
                        throw new JsonIOException("Invalid EnumSet type: " + type.toString());
                    }
                } else {
                    throw new JsonIOException("Invalid EnumSet type: " + type.toString());
                }
            }
        } : (Set.class.isAssignableFrom(oclass) ? new ObjectConstructor<T>() {
            @Override
            public T construct() {
                return new LinkedHashSet();
            }
        } : (Queue.class.isAssignableFrom(oclass) ? new ObjectConstructor<T>() {
            @Override
            public T construct() {
                return new LinkedList();
            }
        } : new ObjectConstructor<T>() {
            @Override
            public T construct() {
                return new ArrayList();
            }
        })))) : (Map.class.isAssignableFrom(oclass) ? (SortedMap.class.isAssignableFrom(oclass) ? new ObjectConstructor<T>() {
            @Override
            public T construct() {
                return new TreeMap();
            }
        } : (type instanceof ParameterizedType && !String.class.isAssignableFrom(TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()) ? new ObjectConstructor<T>() {
            @Override
            public T construct() {
                return new LinkedHashMap();
            }
        } : new ObjectConstructor<T>() {
            @Override
            public T construct() {
                return new LinkedTreeMap<>();
            }
        })) : null);
    }

    private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> oclass) {
        return new ObjectConstructor<T>() {
            private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

            @Override
            public T construct() {
                try {
                    Object object = this.unsafeAllocator.newInstance(oclass);

                    return object;
                } catch (Exception exception) {
                    throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", exception);
                }
            }
        };
    }

    public String toString() {
        return this.instanceCreators.toString();
    }
}
