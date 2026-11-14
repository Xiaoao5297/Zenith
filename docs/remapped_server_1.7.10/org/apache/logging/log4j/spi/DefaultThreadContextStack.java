package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DefaultThreadContextStack implements ThreadContextStack {

    private static final long serialVersionUID = 5050501L;
    private static ThreadLocal<List<String>> stack = new ThreadLocal();
    private final boolean useStack;

    public DefaultThreadContextStack(boolean flag) {
        this.useStack = flag;
    }

    @Override
    public String pop() {
        if (!this.useStack) {
            return "";
        } else {
            List list = (List) DefaultThreadContextStack.stack.get();

            if (list != null && list.size() != 0) {
                ArrayList arraylist = new ArrayList(list);
                int i = arraylist.size() - 1;
                String s = (String) arraylist.remove(i);

                DefaultThreadContextStack.stack.set(Collections.unmodifiableList(arraylist));
                return s;
            } else {
                throw new NoSuchElementException("The ThreadContext stack is empty");
            }
        }
    }

    @Override
    public String peek() {
        List list = (List) DefaultThreadContextStack.stack.get();

        if (list != null && list.size() != 0) {
            int i = list.size() - 1;

            return (String) list.get(i);
        } else {
            return null;
        }
    }

    @Override
    public void push(String s) {
        if (this.useStack) {
            this.add(s);
        }
    }

    @Override
    public int getDepth() {
        List list = (List) DefaultThreadContextStack.stack.get();

        return list == null ? 0 : list.size();
    }

    @Override
    public List<String> asList() {
        List list = (List) DefaultThreadContextStack.stack.get();

        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public void trim(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Maximum stack depth cannot be negative");
        } else {
            List list = (List) DefaultThreadContextStack.stack.get();

            if (list != null) {
                ArrayList arraylist = new ArrayList();
                int j = Math.min(i, list.size());

                for (int k = 0; k < j; ++k) {
                    arraylist.add(list.get(k));
                }

                DefaultThreadContextStack.stack.set(arraylist);
            }
        }
    }

    @Override
    public ThreadContextStack copy() {
        List list = null;

        return this.useStack && (list = (List) DefaultThreadContextStack.stack.get()) != null ? new MutableThreadContextStack(list) : new MutableThreadContextStack(new ArrayList());
    }

    @Override
    public void clear() {
        DefaultThreadContextStack.stack.remove();
    }

    public int size() {
        List list = (List) DefaultThreadContextStack.stack.get();

        return list == null ? 0 : list.size();
    }

    public boolean isEmpty() {
        List list = (List) DefaultThreadContextStack.stack.get();

        return list == null || list.isEmpty();
    }

    public boolean contains(Object object) {
        List list = (List) DefaultThreadContextStack.stack.get();

        return list != null && list.contains(object);
    }

    public Iterator<String> iterator() {
        List list = (List) DefaultThreadContextStack.stack.get();

        if (list == null) {
            List list1 = Collections.emptyList();

            return list1.iterator();
        } else {
            return list.iterator();
        }
    }

    public Object[] toArray() {
        List list = (List) DefaultThreadContextStack.stack.get();

        return (Object[]) (list == null ? new String[0] : list.toArray(new Object[list.size()]));
    }

    public <T> T[] toArray(T[] at) {
        List list = (List) DefaultThreadContextStack.stack.get();

        if (list == null) {
            if (at.length > 0) {
                at[0] = null;
            }

            return at;
        } else {
            return list.toArray(at);
        }
    }

    public boolean add(String s) {
        if (!this.useStack) {
            return false;
        } else {
            List list = (List) DefaultThreadContextStack.stack.get();
            ArrayList arraylist = list == null ? new ArrayList() : new ArrayList(list);

            arraylist.add(s);
            DefaultThreadContextStack.stack.set(Collections.unmodifiableList(arraylist));
            return true;
        }
    }

    public boolean remove(Object object) {
        if (!this.useStack) {
            return false;
        } else {
            List list = (List) DefaultThreadContextStack.stack.get();

            if (list != null && list.size() != 0) {
                ArrayList arraylist = new ArrayList(list);
                boolean flag = arraylist.remove(object);

                DefaultThreadContextStack.stack.set(Collections.unmodifiableList(arraylist));
                return flag;
            } else {
                return false;
            }
        }
    }

    public boolean containsAll(Collection<?> collection) {
        if (collection.isEmpty()) {
            return true;
        } else {
            List list = (List) DefaultThreadContextStack.stack.get();

            return list != null && list.containsAll(collection);
        }
    }

    public boolean addAll(Collection<? extends String> collection) {
        if (this.useStack && !collection.isEmpty()) {
            List list = (List) DefaultThreadContextStack.stack.get();
            ArrayList arraylist = list == null ? new ArrayList() : new ArrayList(list);

            arraylist.addAll(collection);
            DefaultThreadContextStack.stack.set(Collections.unmodifiableList(arraylist));
            return true;
        } else {
            return false;
        }
    }

    public boolean removeAll(Collection<?> collection) {
        if (this.useStack && !collection.isEmpty()) {
            List list = (List) DefaultThreadContextStack.stack.get();

            if (list != null && !list.isEmpty()) {
                ArrayList arraylist = new ArrayList(list);
                boolean flag = arraylist.removeAll(collection);

                DefaultThreadContextStack.stack.set(Collections.unmodifiableList(arraylist));
                return flag;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean retainAll(Collection<?> collection) {
        if (this.useStack && !collection.isEmpty()) {
            List list = (List) DefaultThreadContextStack.stack.get();

            if (list != null && !list.isEmpty()) {
                ArrayList arraylist = new ArrayList(list);
                boolean flag = arraylist.retainAll(collection);

                DefaultThreadContextStack.stack.set(Collections.unmodifiableList(arraylist));
                return flag;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        List list = (List) DefaultThreadContextStack.stack.get();

        return list == null ? "[]" : list.toString();
    }
}
