
import java.util.Random;

public abstract class MocBench {

    abstract MocBuilder createBuilder( int order ) throws Exception;

    void runBench() {
        runBench( 10, 10_000_000, new Random( 9999L ), false );
    }

    void runBench( int order, long np, Random rnd, boolean writeFits ) {
        long prog = Math.max( np / 60, 1 );
        int maxval = 12 << ( 2 * order );
        long start = System.currentTimeMillis();
        final MocBuilder builder;
        try {
            builder = createBuilder( order );
            for ( int ip = 0; ip < np; ip++ ) {
                if ( ip % prog == 0 ) System.out.print( '.' );
                long val = Math.abs( rnd.nextLong() ) % maxval;
                builder.add( order, val );
            }
            System.out.println();
            builder.end();
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        System.out.println( getClass().getSimpleName() );
        System.out.println( "   Order: " + order + "; "
                          + "Points: " + np );
        System.out.println( "   Coverage: " + builder.getCoverage() + "; "
                          + "Pixels: " + builder.getPixelCount() );
        System.out.println( "   Time: "
                          + ( System.currentTimeMillis() - start ) );
        if ( writeFits ) {
            String fname = getClass().getSimpleName() + ".fits";
            System.out.println( "   Result at: " + fname );
            try {
                builder.writeFits( fname );
            }
            catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
    }

    interface MocBuilder {
        public void add( int order, long val ) throws Exception;
        public void end() throws Exception;
        public double getCoverage();
        public long getPixelCount();
        public void writeFits( String filename ) throws Exception;
    }
}
