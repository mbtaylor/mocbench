package xmoc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.LongStream;

public class LongBag implements IndexBag {

    private final Set<Long> longSet_;
    private final int setmax_;
    private long[] sortedLongs_;

    public LongBag( long size, int setmax ) {
        longSet_ = new HashSet<Long>( setmax );
        setmax_ = setmax;
        sortedLongs_ = new long[ 0 ];
    }

    public boolean hasIndex( long lval ) {
        return longSet_.contains( Long.valueOf( lval ) )
            || Arrays.binarySearch( sortedLongs_, lval ) >= 0;
    }

    public void addIndex( long lval ) {
        if ( Arrays.binarySearch( sortedLongs_, lval ) < 0 ) {
            longSet_.add( Long.valueOf( lval ) );
            if ( longSet_.size() > setmax_ ) {
                drainSet();
            }
        }
    }

    public LongStream sortedLongs() {
        drainSet();
        return Arrays.stream( sortedLongs_ );
    }

    private void drainSet() {
        long[] array0 = sortedLongs_;
        long[] array1 = new long[ array0.length + longSet_.size() ];
        System.arraycopy( array0, 0, array1, 0, array0.length );
        int ix = array0.length;
        for ( Long lval : longSet_ ) {
            array1[ ix++ ] = lval.longValue();
        }
        longSet_.clear();
        Arrays.sort( array1 );
        sortedLongs_ = array1;
    }
}
