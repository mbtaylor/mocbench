package xmoc;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;

public class ArraysMoc {

    final long[][] sortedIndexArrays_;

    public ArraysMoc( long[][] sortedIndexArrays ) {
        sortedIndexArrays_ = sortedIndexArrays;
    }

    public int getMaxLevel() {
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

    public LongStream uniqStream() {
        LongStream stream = LongStream.empty();
        for ( int ilev = 0; ilev < sortedIndexArrays_.length; ilev++ ) {
            long[] array = sortedIndexArrays_[ ilev ];
            int ilev0 = ilev;
            stream = LongStream.concat( stream,
                                        Arrays.stream( array )
                                              .map( l -> uniq( ilev0, l ) ) );
        }
        return stream;
    }

    public PrimitiveIterator.OfLong uniqIterator() {
        int ilev = 0;
        return new PrimitiveIterator.OfLong() {
            long[] array_;
            int ilev_;
            int ipix_;
            boolean done_;
            long nextUniq_;
            /* Constructor */ {
                array_ = new long[ 0 ];
                ilev_ = -1;
                calculateNext();
            }
            public boolean hasNext() {
                return ! done_;
            }
            public long nextLong() {
                if ( hasNext() ) {
                    long nextUniq = nextUniq_;
                    calculateNext();
                    return nextUniq;
                }
                else {
                    throw new NoSuchElementException();
                }
            }
            private void calculateNext() {
                while ( ipix_ >= array_.length ) {
                    if ( ilev_ + 1 < sortedIndexArrays_.length ) {
                        array_ = sortedIndexArrays_[ ++ilev_ ];
                        ipix_ = 0;
                    }
                    else {
                        done_ = true;
                        return;
                    }
                }
                nextUniq_ = uniq( ilev_, array_[ ipix_++ ] );
            }
        };
    }

    public static long uniq( int ilevel, long lindex ) {
        return ( 4L << ( 2 * ilevel ) ) + lindex;
    }
}
