
import cds.moc.Moc;
import cds.moc.MocCell;
import cds.moc.SMoc;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class SMocBench extends MocBench {

    MocAssembler createAssembler( int mocOrder ) {
        SMoc smoc = new SMoc( mocOrder );
        // Using a non-default buffer size here helps, but it's still slow.
        // smoc.bufferOn();
        smoc.bufferOn( 500_000 );
        return new MocAssembler() {
            public void add( int order, long val ) throws Exception {
                smoc.add( order, val );
            }
            public void end() {
                smoc.bufferOff();
            }
            public double getCoverage() {
                return smoc.getCoverage();
            }
            public long getPixelCount() {
                return smoc.getNbCoding();
            }
            public LongStream getUniqs() {
                return StreamSupport
                      .stream( smoc.spliterator(), false )
                      .mapToLong( c -> Moc.hpix2uniq( c.order, c.start ) );
            }
            public PrimitiveIterator.OfLong uniqIterator() {
                Iterator<MocCell> cellIt = smoc.iterator();
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
                smoc.writeFITS( filename );
            }
        };
    }

    public static void main( String[] args ) {
        new SMocBench().runBench( args );
    }
}
