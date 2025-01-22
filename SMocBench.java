
import cds.moc.Moc;
import cds.moc.SMoc;
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
            public void writeFits( String filename ) throws Exception {
                smoc.writeFITS( filename );
            }
        };
    }

    public static void main( String[] args ) {
        new SMocBench().runBench( args );
    }
}
