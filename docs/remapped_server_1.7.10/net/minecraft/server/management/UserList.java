package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList {

    protected static final Logger field_152693_a = LogManager.getLogger();
    protected final Gson field_152694_b;
    private final File field_152695_c;
    private final Map field_152696_d = Maps.newHashMap();
    private boolean field_152697_e = true;
    private static final ParameterizedType field_152698_f = new UserList$1();

    public UserList(File file) {
        this.field_152695_c = file;
        GsonBuilder gsonbuilder = (new GsonBuilder()).setPrettyPrinting();

        gsonbuilder.registerTypeHierarchyAdapter(UserListEntry.class, new UserList$Serializer(this, (UserList$1) null));
        this.field_152694_b = gsonbuilder.create();
    }

    public boolean func_152689_b() {
        return this.field_152697_e;
    }

    public void func_152686_a(boolean flag) {
        this.field_152697_e = flag;
    }

    public File func_152691_c() {
        return this.field_152695_c;
    }

    public void func_152687_a(UserListEntry userlistentry) {
        this.field_152696_d.put(this.func_152681_a(userlistentry.func_152640_f()), userlistentry);

        try {
            this.func_152678_f();
        } catch (IOException ioexception) {
            UserList.field_152693_a.warn("Could not save the list after adding a user.", (Throwable) ioexception);
        }

    }

    public UserListEntry func_152683_b(Object object) {
        this.func_152680_h();
        return (UserListEntry) this.field_152696_d.get(this.func_152681_a(object));
    }

    public void func_152684_c(Object object) {
        this.field_152696_d.remove(this.func_152681_a(object));

        try {
            this.func_152678_f();
        } catch (IOException ioexception) {
            UserList.field_152693_a.warn("Could not save the list after removing a user.", (Throwable) ioexception);
        }

    }

    public String[] func_152685_a() {
        return (String[]) this.field_152696_d.keySet().toArray(new String[this.field_152696_d.size()]);
    }

    public boolean func_152690_d() {
        return this.field_152696_d.size() < 1;
    }

    protected String func_152681_a(Object object) {
        return object.toString();
    }

    protected boolean func_152692_d(Object object) {
        return this.field_152696_d.containsKey(this.func_152681_a(object));
    }

    private void func_152680_h() {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.field_152696_d.values().iterator();

        while (iterator.hasNext()) {
            UserListEntry userlistentry = (UserListEntry) iterator.next();

            if (userlistentry.func_73682_e()) {
                arraylist.add(userlistentry.func_152640_f());
            }
        }

        iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            this.field_152696_d.remove(object);
        }

    }

    protected UserListEntry func_152682_a(JsonObject jsonobject) {
        return new UserListEntry((Object) null, jsonobject);
    }

    protected Map func_152688_e() {
        return this.field_152696_d;
    }

    public void func_152678_f() {
        Collection collection = this.field_152696_d.values();
        String s = this.field_152694_b.toJson((Object) collection);
        BufferedWriter bufferedwriter = null;

        try {
            bufferedwriter = Files.newWriter(this.field_152695_c, Charsets.UTF_8);
            bufferedwriter.write(s);
        } finally {
            IOUtils.closeQuietly((Writer) bufferedwriter);
        }

    }

    public void func_152679_g() {
        Collection collection = null;
        BufferedReader bufferedreader = null;

        try {
            bufferedreader = Files.newReader(this.field_152695_c, Charsets.UTF_8);
            collection = (Collection) this.field_152694_b.fromJson((Reader) bufferedreader, (Type) UserList.field_152698_f);
        } finally {
            IOUtils.closeQuietly((Reader) bufferedreader);
        }

        if (collection != null) {
            this.field_152696_d.clear();
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                UserListEntry userlistentry = (UserListEntry) iterator.next();

                if (userlistentry.func_152640_f() != null) {
                    this.field_152696_d.put(this.func_152681_a(userlistentry.func_152640_f()), userlistentry);
                }
            }
        }

    }
}
