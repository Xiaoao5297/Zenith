package com.mojang.authlib;

public interface GameProfileRepository {

    void findProfilesByNames(String[] astring, Agent agent, ProfileLookupCallback profilelookupcallback);
}
