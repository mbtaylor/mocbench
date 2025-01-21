
import cds.moc.SMoc;

public class SMocBench extends MocBench {

    MocBuilder createBuilder( int mocOrder ) {
        SMoc smoc = new SMoc( mocOrder );
        // Using a non-default buffer size here helps, but it's still slow.
        // smoc.bufferOn();
        smoc.bufferOn( 500_000 );
        return new MocBuilder() {
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
        };
    }

    public static void main( String[] args ) {
        new SMocBench().runBench();
    }
}
