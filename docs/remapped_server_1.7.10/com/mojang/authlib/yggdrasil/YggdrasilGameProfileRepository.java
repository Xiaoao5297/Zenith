package com.mojang.authlib.yggdrasil;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilGameProfileRepository implements GameProfileRepository {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "https://api.mojang.com/";
    private static final String SEARCH_PAGE_URL = "https://api.mojang.com/profiles/";
    private static final int ENTRIES_PER_PAGE = 2;
    private static final int MAX_FAIL_COUNT = 3;
    private static final int DELAY_BETWEEN_PAGES = 100;
    private static final int DELAY_BETWEEN_FAILURES = 750;
    private final YggdrasilAuthenticationService authenticationService;

    public YggdrasilGameProfileRepository(YggdrasilAuthenticationService yggdrasilauthenticationservice) {
        this.authenticationService = yggdrasilauthenticationservice;
    }

    @Override
    public void findProfilesByNames(String[] astring, Agent agent, ProfileLookupCallback profilelookupcallback) {
        HashSet hashset = Sets.newHashSet();
        String[] astring1 = astring;
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s = astring1[j];

            if (!Strings.isNullOrEmpty(s)) {
                hashset.add(s.toLowerCase());
            }
        }

        byte b0 = 0;
        Iterator iterator = Iterables.partition(hashset, 2).iterator();

        while (iterator.hasNext()) {
            List list = (List) iterator.next();
            int k = 0;

            while (true) {
                boolean flag = false;

                try {
                    ProfileSearchResultsResponse profilesearchresultsresponse = (ProfileSearchResultsResponse) this.authenticationService.makeRequest(HttpAuthenticationService.constantURL("https://api.mojang.com/profiles/" + agent.getName().toLowerCase()), list, ProfileSearchResultsResponse.class);

                    k = 0;
                    YggdrasilGameProfileRepository.LOGGER.debug("Page {} returned {} results, parsing", Integer.valueOf(b0), profilesearchresultsresponse.getProfiles().length);
                    HashSet hashset1 = Sets.newHashSet((Iterable) list);
                    GameProfile[] agameprofile = profilesearchresultsresponse.getProfiles();
                    int l = agameprofile.length;

                    for (int i1 = 0; i1 < l; ++i1) {
                        GameProfile gameprofile = agameprofile[i1];

                        YggdrasilGameProfileRepository.LOGGER.debug("Successfully looked up profile {}", gameprofile);
                        hashset1.remove(gameprofile.getName().toLowerCase());
                        profilelookupcallback.onProfileLookupSucceeded(gameprofile);
                    }

                    Iterator iterator1 = hashset1.iterator();

                    while (iterator1.hasNext()) {
                        String s1 = (String) iterator1.next();

                        YggdrasilGameProfileRepository.LOGGER.debug("Couldn't find profile {}", s1);
                        profilelookupcallback.onProfileLookupFailed(new GameProfile((UUID) null, s1), new ProfileNotFoundException("Server did not find the requested profile"));
                    }

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }
                } catch (AuthenticationException authenticationexception) {
                    AuthenticationException authenticationexception1 = authenticationexception;

                    ++k;
                    if (k == 3) {
                        Iterator iterator2 = list.iterator();

                        while (iterator2.hasNext()) {
                            String s2 = (String) iterator2.next();

                            YggdrasilGameProfileRepository.LOGGER.debug("Couldn't find profile {} because of a server error", s2);
                            profilelookupcallback.onProfileLookupFailed(new GameProfile((UUID) null, s2), authenticationexception1);
                        }
                    } else {
                        try {
                            Thread.sleep(750L);
                        } catch (InterruptedException interruptedexception1) {
                            ;
                        }

                        flag = true;
                    }
                }

                if (!flag) {
                    break;
                }
            }
        }

    }
}
