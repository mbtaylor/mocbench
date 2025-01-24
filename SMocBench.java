
import cds.moc.Moc;
import cds.moc.MocCell;
import cds.moc.Range;
import cds.moc.SMoc;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class SMocBench extends MocBench {

    private int batchSize_;

    SMocBench() {
        batchSize_ = 1;
    }

    MocAssembler createAssembler( int mocOrder ) {
        return new SMocAssembler( mocOrder ) {
            public void add( int order, long val ) throws Exception {
                smoc_.add( order, val );
            }
        };
    }

    private static abstract class SMocAssembler implements MocAssembler {
        final SMoc1 smoc_;
        SMocAssembler( int mocOrder ) {
            smoc_ = new SMoc1( mocOrder );
            // Using a non-default buffer size here helps, but it's still slow.
            // smoc.bufferOn();
            smoc_.bufferOn( 500_000 );
        }
        public void end() {
            smoc_.bufferOff();
        }
        public double getCoverage() {
            return smoc_.getCoverage();
        }
        public long getPixelCount() {
            return smoc_.getNbCoding();
        }
        public LongStream getUniqs() {
            return StreamSupport
                  .stream( smoc_.spliterator(), false )
                  .mapToLong( c -> Moc.hpix2uniq( c.order, c.start ) );
        }
        public PrimitiveIterator.OfLong uniqIterator() {
            Iterator<MocCell> cellIt = smoc_.iterator();
            return new PrimitiveIterator.OfLong() {
                public boolean hasNext() {
                    return cellIt.hasNext();
                }
                public long nextLong() {
                    MocCell cell = cellIt.next();
                    return Moc.hpix2uniq( cell.order, cell.start );
                }
            };
        }
        public void writeFits( String filename ) throws Exception {
            smoc_.writeFITS( filename );
        }
    }

    /**
     * Extends SMoc with a fast multiple addition method supplied by Pierre
     * by email 23 Jan 2025.
     */
    private static class SMoc1 extends SMoc {
        SMoc1( int mocOrder ) {
            super( mocOrder );
        }

        /** Fast addition of a list of singletons expressed
         * at the specified order
         * These singletons do not need to be sorted, nor to be unique.
         * @param order singleton order
         * @param singletons list of singletons
         * @param size number of singletons
         */
        public void add( int order, long [] singletons, int size) {
            Arrays.sort(singletons,0,size);
            int shift = (maxOrder()-order) * shiftOrder();
            Range r = new Range(size);
            int j;
            for( int i=0; i<size; i++ ) {
                for( j=i; j<size-1 && (singletons[j+1]-singletons[j])<=1; j++ );
                long start = (singletons[i]) << shift;
                long end = (singletons[j]+1L) << shift;
                r.append(start,end);
            }
            range = range.union(r);
            resetCache();
        }
    }

    public static void main( String[] args ) {
        new SMocBench().runBench( args );
    }
}
