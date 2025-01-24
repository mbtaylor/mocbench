package xmoc;

import java.util.stream.LongStream;

public interface IndexBag {

    // Not necessary
    boolean hasIndex( long index );

    // Value must be in range.
    void addIndex( long index );

    LongStream sortedLongs();
}
