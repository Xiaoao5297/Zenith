package io.netty.buffer;

public interface ByteBufProcessor {

    ByteBufProcessor FIND_NUL = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 != 0;
        }
    };
    ByteBufProcessor FIND_NON_NUL = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 == 0;
        }
    };
    ByteBufProcessor FIND_CR = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 != 13;
        }
    };
    ByteBufProcessor FIND_NON_CR = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 == 13;
        }
    };
    ByteBufProcessor FIND_LF = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 != 10;
        }
    };
    ByteBufProcessor FIND_NON_LF = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 == 10;
        }
    };
    ByteBufProcessor FIND_CRLF = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 != 13 && b0 != 10;
        }
    };
    ByteBufProcessor FIND_NON_CRLF = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 == 13 || b0 == 10;
        }
    };
    ByteBufProcessor FIND_LINEAR_WHITESPACE = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 != 32 && b0 != 9;
        }
    };
    ByteBufProcessor FIND_NON_LINEAR_WHITESPACE = new ByteBufProcessor() {
        @Override
        public boolean process(byte b0) throws Exception {
            return b0 == 32 || b0 == 9;
        }
    };

    boolean process(byte b0) throws Exception;
}
