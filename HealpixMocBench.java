
import cds.moc.HealpixMoc;

public class HealpixMocBench extends MocBench {

    MocBuilder createBuilder( int mocOrder ) throws Exception {
        HealpixMoc hmoc = new HealpixMoc( mocOrder );
        hmoc.setCheckConsistencyFlag( false );
        return new MocBuilder() {
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
        };
    }

    public static void main( String[] args ) {
        new HealpixMocBench().runBench();
    }
}
