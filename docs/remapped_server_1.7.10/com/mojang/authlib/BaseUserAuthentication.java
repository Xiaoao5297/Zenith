package com.mojang.authlib;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseUserAuthentication implements UserAuthentication {

    private static final Logger LOGGER = LogManager.getLogger();
    protected static final String STORAGE_KEY_PROFILE_NAME = "displayName";
    protected static final String STORAGE_KEY_PROFILE_ID = "uuid";
    protected static final String STORAGE_KEY_PROFILE_PROPERTIES = "profileProperties";
    protected static final String STORAGE_KEY_USER_NAME = "username";
    protected static final String STORAGE_KEY_USER_ID = "userid";
    protected static final String STORAGE_KEY_USER_PROPERTIES = "userProperties";
    private final AuthenticationService authenticationService;
    private final PropertyMap userProperties = new PropertyMap();
    private String userid;
    private String username;
    private String password;
    private GameProfile selectedProfile;
    private UserType userType;

    protected BaseUserAuthentication(AuthenticationService authenticationservice) {
        Validate.notNull(authenticationservice);
        this.authenticationService = authenticationservice;
    }

    @Override
    public boolean canLogIn() {
        return !this.canPlayOnline() && StringUtils.isNotBlank(this.getUsername()) && StringUtils.isNotBlank(this.getPassword());
    }

    @Override
    public void logOut() {
        this.password = null;
        this.userid = null;
        this.setSelectedProfile((GameProfile) null);
        this.getModifiableUserProperties().clear();
        this.setUserType((UserType) null);
    }

    @Override
    public boolean isLoggedIn() {
        return this.getSelectedProfile() != null;
    }

    @Override
    public void setUsername(String s) {
        if (this.isLoggedIn() && this.canPlayOnline()) {
            throw new IllegalStateException("Cannot change username whilst logged in & online");
        } else {
            this.username = s;
        }
    }

    @Override
    public void setPassword(String s) {
        if (this.isLoggedIn() && this.canPlayOnline() && StringUtils.isNotBlank(s)) {
            throw new IllegalStateException("Cannot set password whilst logged in & online");
        } else {
            this.password = s;
        }
    }

    protected String getUsername() {
        return this.username;
    }

    protected String getPassword() {
        return this.password;
    }

    @Override
    public void loadFromStorage(Map<String, Object> map) {
        this.logOut();
        this.setUsername(String.valueOf(map.get("username")));
        if (map.containsKey("userid")) {
            this.userid = String.valueOf(map.get("userid"));
        } else {
            this.userid = this.username;
        }

        String s;
        String s1;

        if (map.containsKey("userProperties")) {
            try {
                List list = (List) map.get("userProperties");
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Map map1 = (Map) iterator.next();
                    String s2 = (String) map1.get("name");

                    s = (String) map1.get("value");
                    s1 = (String) map1.get("signature");
                    if (s1 == null) {
                        this.getModifiableUserProperties().put(s2, new Property(s2, s));
                    } else {
                        this.getModifiableUserProperties().put(s2, new Property(s2, s, s1));
                    }
                }
            } catch (Throwable throwable) {
                BaseUserAuthentication.LOGGER.warn("Couldn't deserialize user properties", throwable);
            }
        }

        if (map.containsKey("displayName") && map.containsKey("uuid")) {
            GameProfile gameprofile = new GameProfile(UUIDTypeAdapter.fromString(String.valueOf(map.get("uuid"))), String.valueOf(map.get("displayName")));

            if (map.containsKey("profileProperties")) {
                try {
                    List list1 = (List) map.get("profileProperties");
                    Iterator iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        Map map2 = (Map) iterator1.next();

                        s = (String) map2.get("name");
                        s1 = (String) map2.get("value");
                        String s3 = (String) map2.get("signature");

                        if (s3 == null) {
                            gameprofile.getProperties().put(s, new Property(s, s1));
                        } else {
                            gameprofile.getProperties().put(s, new Property(s, s1, s3));
                        }
                    }
                } catch (Throwable throwable1) {
                    BaseUserAuthentication.LOGGER.warn("Couldn't deserialize profile properties", throwable1);
                }
            }

            this.setSelectedProfile(gameprofile);
        }

    }

    @Override
    public Map<String, Object> saveForStorage() {
        HashMap hashmap = new HashMap();

        if (this.getUsername() != null) {
            hashmap.put("username", this.getUsername());
        }

        if (this.getUserID() != null) {
            hashmap.put("userid", this.getUserID());
        } else if (this.getUsername() != null) {
            hashmap.put("username", this.getUsername());
        }

        if (!this.getUserProperties().isEmpty()) {
            ArrayList arraylist = new ArrayList();
            Iterator iterator = this.getUserProperties().values().iterator();

            while (iterator.hasNext()) {
                Property property = (Property) iterator.next();
                HashMap hashmap1 = new HashMap();

                hashmap1.put("name", property.getName());
                hashmap1.put("value", property.getValue());
                hashmap1.put("signature", property.getSignature());
                arraylist.add(hashmap1);
            }

            hashmap.put("userProperties", arraylist);
        }

        GameProfile gameprofile = this.getSelectedProfile();

        if (gameprofile != null) {
            hashmap.put("displayName", gameprofile.getName());
            hashmap.put("uuid", gameprofile.getId());
            ArrayList arraylist1 = new ArrayList();
            Iterator iterator1 = gameprofile.getProperties().values().iterator();

            while (iterator1.hasNext()) {
                Property property1 = (Property) iterator1.next();
                HashMap hashmap2 = new HashMap();

                hashmap2.put("name", property1.getName());
                hashmap2.put("value", property1.getValue());
                hashmap2.put("signature", property1.getSignature());
                arraylist1.add(hashmap2);
            }

            if (!arraylist1.isEmpty()) {
                hashmap.put("profileProperties", arraylist1);
            }
        }

        return hashmap;
    }

    protected void setSelectedProfile(GameProfile gameprofile) {
        this.selectedProfile = gameprofile;
    }

    @Override
    public GameProfile getSelectedProfile() {
        return this.selectedProfile;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append("{");
        if (this.isLoggedIn()) {
            stringbuilder.append("Logged in as ");
            stringbuilder.append(this.getUsername());
            if (this.getSelectedProfile() != null) {
                stringbuilder.append(" / ");
                stringbuilder.append(this.getSelectedProfile());
                stringbuilder.append(" - ");
                if (this.canPlayOnline()) {
                    stringbuilder.append("Online");
                } else {
                    stringbuilder.append("Offline");
                }
            }
        } else {
            stringbuilder.append("Not logged in");
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public AuthenticationService getAuthenticationService() {
        return this.authenticationService;
    }

    @Override
    public String getUserID() {
        return this.userid;
    }

    @Override
    public PropertyMap getUserProperties() {
        if (this.isLoggedIn()) {
            PropertyMap propertymap = new PropertyMap();

            propertymap.putAll(this.getModifiableUserProperties());
            return propertymap;
        } else {
            return new PropertyMap();
        }
    }

    protected PropertyMap getModifiableUserProperties() {
        return this.userProperties;
    }

    @Override
    public UserType getUserType() {
        return this.isLoggedIn() ? (this.userType == null ? UserType.LEGACY : this.userType) : null;
    }

    protected void setUserType(UserType usertype) {
        this.userType = usertype;
    }

    protected void setUserid(String s) {
        this.userid = s;
    }
}
