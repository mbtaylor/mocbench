package xmoc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IntegerBag implements IndexBag {

    private final Set<Integer> intSet_;
    private final int setmax_;
    private int[] sortedInts_;

    public IntegerBag( long size, int setmax ) {
        intSet_ = new HashSet<Integer>( setmax );
        setmax_ = setmax;
        sortedInts_ = new int[ 0 ];
    }

    public boolean hasIndex( long lval ) {
        int ival = (int) lval;
        return intSet_.contains( Integer.valueOf( ival ) )
            || Arrays.binarySearch( sortedInts_, ival ) >= 0;
    }

    public void addIndex( long lval ) {
        int ival = (int) lval;
        if ( Arrays.binarySearch( sortedInts_, ival ) < 0 ) {
            intSet_.add( Integer.valueOf( ival ) );
            if ( intSet_.size() > setmax_ ) {
                drainSet();
            }
        }
    }

    public long[] sortedLongs() {
        drainSet();
        int n = sortedInts_.length;
        long[] sortedLongs = new long[ n ];
        for ( int i = 0; i < n; i++ ) {
            sortedLongs[ i ] = sortedInts_[ i ];
        }
        return sortedLongs;
    }

    private void drainSet() {
        int[] array0 = sortedInts_;
        int[] array1 = new int[ array0.length + intSet_.size() ];
        System.arraycopy( array0, 0, array1, 0, array0.length );
        int ix = array0.length;
        for ( Integer ival : intSet_ ) {
            array1[ ix++ ] = ival.intValue();
        }
        intSet_.clear();
        Arrays.sort( array1 );
        sortedInts_ = array1;
    }
}
