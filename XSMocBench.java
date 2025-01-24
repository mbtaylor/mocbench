
import cds.moc.Moc;
import cds.moc.MocCell;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import xmoc.ArraysMoc;
import xmoc.MocBuilder;

public class XSMocBench extends MocBench {

    MocAssembler createAssembler( int mocOrder ) {
        MocBuilder builder = new MocBuilder( mocOrder );
        return new MocAssembler() {
            SMoc1 smoc_;
            public void add( int order, long val ) {
                builder.addTile( order, val );
            }
            public void end() {
                ArraysMoc amoc = builder.createMoc();
                smoc_ = new SMoc1( mocOrder );
 // bufferOn/bufferOff?
                for ( int ilev = 0; ilev <= amoc.getMaxLevel(); ilev++ ) {
                    long[] ixs = amoc.getSortedIndices( ilev );
                    if ( ixs != null && ixs.length > 0 ) {
                        smoc_.add( ilev, ixs, ixs.length );
                    }
                }
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
        };
    }

    public static void main( String[] args ) {
        new XSMocBench().runBench( args );
    }
}
