package xmoc;

public interface IndexBag {

    // Not necessary
    boolean hasIndex( long index );

    // Value must be in range.
    void addIndex( long index );

    // Do not write to this array.
    // Maybe replace this with a LongIterator.
    long[] sortedLongs();
}
