package xmoc;

import java.util.Arrays;
import java.util.stream.LongStream;

public class ArraysMoc {

    final long[][] sortedIndexArrays_;

    public ArraysMoc( long[][] sortedIndexArrays ) {
        sortedIndexArrays_ = sortedIndexArrays;
    }

    public int getLevelCount() {
        return sortedIndexArrays_.length - 1;
    }

    public long[] getSortedIndices( int ilevel ) {
        return sortedIndexArrays_[ ilevel ];
    }

    public long getPixelCount() {
        return Arrays.stream( sortedIndexArrays_ )
                     .mapToLong( a -> (long) a.length )
                     .sum();
    }

    public double getCoverage() {
        double cov = 0;
        for ( int ilev = 0; ilev < sortedIndexArrays_.length; ilev++ ) {
            cov += sortedIndexArrays_[ ilev ].length * 1. / ( 12L << 2 * ilev );
        }
        return cov;
    }

    public LongStream nuniqStream() {
        LongStream stream = LongStream.empty();
        for ( int ilev = 0; ilev < sortedIndexArrays_.length; ilev++ ) {
            long[] array = sortedIndexArrays_[ ilev ];
            int ilev0 = ilev;
            stream = LongStream.concat( stream,
                                        Arrays.stream( array )
                                              .map( l -> nuniq( ilev0, l ) ) );
        }
        return stream;
    }

    public static long nuniq( int ilevel, long lindex ) {
        return ( 4L << ( 2 * ilevel ) ) + lindex;
    }
}
