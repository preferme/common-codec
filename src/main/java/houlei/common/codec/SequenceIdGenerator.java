package houlei.common.codec;

import java.util.concurrent.atomic.AtomicInteger;

public interface SequenceIdGenerator {

    int generate();

    SequenceIdGenerator DEFAULT = new SequenceIdGenerator() {
        private final AtomicInteger value = new AtomicInteger(0);
        @Override
        public int generate() {
            value.compareAndSet(Integer.MAX_VALUE, 0);
            return value.incrementAndGet();
        }
    };

}
