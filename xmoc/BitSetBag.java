package xmoc;

import java.util.BitSet;

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

    public long[] sortedLongs() {
        long[] array = new long[ bitset_.cardinality() ];
        int j = 0;
        for ( int i = bitset_.nextSetBit( 0 ); i >= 0;
              i = bitset_.nextSetBit( i + 1 ) ) {
            array[ j++ ] = i;
            if ( i == Integer.MAX_VALUE ) {
                break;
            }
        }
        return array;
    }
}
