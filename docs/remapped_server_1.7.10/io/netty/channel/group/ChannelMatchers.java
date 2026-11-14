package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ServerChannel;

public final class ChannelMatchers {

    private static final ChannelMatcher ALL_MATCHER = new ChannelMatcher() {
        @Override
        public boolean matches(Channel channel) {
            return true;
        }
    };
    private static final ChannelMatcher SERVER_CHANNEL_MATCHER = isInstanceOf(ServerChannel.class);
    private static final ChannelMatcher NON_SERVER_CHANNEL_MATCHER = isNotInstanceOf(ServerChannel.class);

    private ChannelMatchers() {}

    public static ChannelMatcher all() {
        return ChannelMatchers.ALL_MATCHER;
    }

    public static ChannelMatcher isNot(Channel channel) {
        return invert(is(channel));
    }

    public static ChannelMatcher is(Channel channel) {
        return new ChannelMatchers.InstanceMatcher(channel);
    }

    public static ChannelMatcher isInstanceOf(Class<? extends Channel> oclass) {
        return new ChannelMatchers.ClassMatcher(oclass);
    }

    public static ChannelMatcher isNotInstanceOf(Class<? extends Channel> oclass) {
        return invert(isInstanceOf(oclass));
    }

    public static ChannelMatcher isServerChannel() {
        return ChannelMatchers.SERVER_CHANNEL_MATCHER;
    }

    public static ChannelMatcher isNonServerChannel() {
        return ChannelMatchers.NON_SERVER_CHANNEL_MATCHER;
    }

    public static ChannelMatcher invert(ChannelMatcher channelmatcher) {
        return new ChannelMatchers.InvertMatcher(channelmatcher);
    }

    public static ChannelMatcher compose(ChannelMatcher... achannelmatcher) {
        if (achannelmatcher.length < 1) {
            throw new IllegalArgumentException("matchers must at least contain one element");
        } else {
            return (ChannelMatcher) (achannelmatcher.length == 1 ? achannelmatcher[0] : new ChannelMatchers.CompositeMatcher(achannelmatcher));
        }
    }

    private static final class ClassMatcher implements ChannelMatcher {

        private final Class<? extends Channel> clazz;

        ClassMatcher(Class<? extends Channel> oclass) {
            this.clazz = oclass;
        }

        @Override
        public boolean matches(Channel channel) {
            return this.clazz.isInstance(channel);
        }
    }

    private static final class InstanceMatcher implements ChannelMatcher {

        private final Channel channel;

        InstanceMatcher(Channel channel) {
            this.channel = channel;
        }

        @Override
        public boolean matches(Channel channel) {
            return this.channel == channel;
        }
    }

    private static final class InvertMatcher implements ChannelMatcher {

        private final ChannelMatcher matcher;

        InvertMatcher(ChannelMatcher channelmatcher) {
            this.matcher = channelmatcher;
        }

        @Override
        public boolean matches(Channel channel) {
            return !this.matcher.matches(channel);
        }
    }

    private static final class CompositeMatcher implements ChannelMatcher {

        private final ChannelMatcher[] matchers;

        CompositeMatcher(ChannelMatcher... achannelmatcher) {
            this.matchers = achannelmatcher;
        }

        @Override
        public boolean matches(Channel channel) {
            for (int i = 0; i < this.matchers.length; ++i) {
                if (!this.matchers[i].matches(channel)) {
                    return true;
                }
            }

            return false;
        }
    }
}
