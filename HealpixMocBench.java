
import cds.moc.HealpixMoc;
import cds.moc.MocCell;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class HealpixMocBench extends MocBench {

    MocAssembler createAssembler( int mocOrder ) throws Exception {
        HealpixMoc hmoc = new HealpixMoc( mocOrder );
        hmoc.setCheckConsistencyFlag( false );
        return new MocAssembler() {
            public void add( int order, long val ) throws Exception {
                hmoc.add( order, val );
            }
            public void end() throws Exception {
                hmoc.setCheckConsistencyFlag( true );
                hmoc.checkAndFix();
            }
            public double getCoverage() {
                return hmoc.getCoverage();
            }
            public long getPixelCount() {
                return hmoc.getSize();
            }
            public LongStream getUniqs() {
                return StreamSupport
                      .stream( hmoc.spliterator(), false )
                      .mapToLong( c -> HealpixMoc.hpix2uniq( c.order,
                                                             c.npix ) );

            }
            public PrimitiveIterator.OfLong uniqIterator() {
                Iterator<MocCell> cellIt = hmoc.iterator();
                return new PrimitiveIterator.OfLong() {
                    public boolean hasNext() {
                        return cellIt.hasNext();
                    }
                    public long nextLong() {
                        MocCell cell = cellIt.next();
                        return HealpixMoc.hpix2uniq( cell.order, cell.npix );
                    }
                };
            }
            public void writeFits( String filename ) throws Exception {
                try ( FileOutputStream out = new FileOutputStream( filename ) ){
                    hmoc.writeFits( out );
                }
            }
        };
    }

    public static void main( String[] args ) {
        new HealpixMocBench().runBench( args );
    }
}
