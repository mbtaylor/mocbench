
import cds.moc.Moc;
import cds.moc.MocCell;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class SMocBench extends MocBench {

    private int batchSize_;

    SMocBench( int batchSize ) {
        batchSize_ = batchSize;
    }

    MocAssembler createAssembler( int mocOrder ) {
        if ( batchSize_ == 1 ) {
            return new SMocAssembler( mocOrder ) {
                public void add( int order, long val ) throws Exception {
                    smoc_.add( order, val );
                }
            };
        }
        else {
            return new SMocAssembler( mocOrder ) {
                final int bufsiz_ = batchSize_;
                final long[] buf_ =  new long[ bufsiz_ ];
                int ib_;
                int order_;
		public void add( int order, long val ) throws Exception {
                    if ( order != order_ || ib_ >= bufsiz_ ) {
                        flush();
                        order_ = order;
                    }
                    buf_[ ib_++ ] = val;
                }
                @Override
                public void end() {
                    flush();
                    super.end();
                }
                private void flush() {
                    smoc_.add( order_, buf_, ib_ );
                    ib_ = 0;
                }
            };
        }
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

    public static void main( String[] args ) {
        new SMocBench( 1_000_000 ).runBench( args );
    }
}
