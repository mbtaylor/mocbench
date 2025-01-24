package xmoc;

import java.util.BitSet;
import java.util.stream.LongStream;

public class BitSetBag implements IndexBag {

    private final BitSet bitset_;

    public BitSetBag( int size ) {
        bitset_ = new BitSet( size );
    }

    public void addIndex( long lval ) {
        bitset_.set( (int) lval );
    }

    public void removeIndex( long lval ) {
        bitset_.clear( (int) lval );
    }

    public boolean hasIndex( long lval ) {
        return bitset_.get( (int) lval );
    }

    public LongStream sortedLongs() {
        return bitset_.stream().asLongStream();
    }
}
