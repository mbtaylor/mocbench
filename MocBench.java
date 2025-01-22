
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class MocBench {

    abstract MocAssembler createAssembler( int order ) throws Exception;

    void runBench( String[] args ) {
        List<String> argList = new ArrayList<String>( Arrays.asList( args ) );
        int order = 10;
        long count = 10_000_000;
        boolean writeFits = false;
        String usage = new StringBuffer()
           .append( getClass().getSimpleName() )
           .append( " [-help]" )
           .append( " [-order <n>]" )
           .append( " [-count <n>]" )
           .append( " [-fits]" )
           .toString();
        for ( Iterator<String> argIt = argList.iterator(); argIt.hasNext(); ) {
            String arg = argIt.next();
            if ( arg.startsWith( "-h" ) ) {
                argIt.remove();
                System.err.println( usage );
                return;
            }
            else if ( "-order".equalsIgnoreCase( arg ) && argIt.hasNext() ) {
                argIt.remove();
                order = Integer.parseInt( argIt.next().replaceAll( "_", "" ) );
                argIt.remove();
            }
            else if ( "-count".equalsIgnoreCase( arg ) && argIt.hasNext() ) {
                argIt.remove();
                count = Long.parseLong( argIt.next().replaceAll( "_", "" ) );
                argIt.remove();
            }
            else if ( "-fits".equalsIgnoreCase( arg ) ) {
                argIt.remove();
                writeFits = true;
            }
            else {
                System.err.println( usage );
                return;
            }
        }
        if ( ! argList.isEmpty() ) {
            System.err.println( usage );
            return;
        }
        Random rnd = new Random( 9999L );
        runBench( order, count, rnd, writeFits );
    }

    void runBench() {
        runBench( 10, 10_000_000, new Random( 9999L ), false );
    }

    void runBench( int order, long np, Random rnd, boolean writeFits ) {
        long prog = Math.max( np / 60, 1 );
        int maxval = 12 << ( 2 * order );
        long start = System.currentTimeMillis();
        final MocAssembler assembler;
        try {
            assembler = createAssembler( order );
            for ( int ip = 0; ip < np; ip++ ) {
                if ( ip % prog == 0 ) System.out.print( '.' );
                long val = Math.abs( rnd.nextLong() ) % maxval;
                assembler.add( order, val );
            }
            System.out.println();
            assembler.end();
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        System.out.println( getClass().getSimpleName() );
        System.out.println( "   Order: " + order + "; "
                          + "Points: " + np );
        System.out.println( "   Coverage: " + assembler.getCoverage() + "; "
                          + "Pixels: " + assembler.getPixelCount() );
        System.out.println( "   Time: "
                          + ( System.currentTimeMillis() - start ) );
        if ( writeFits ) {
            String fname = getClass().getSimpleName() + ".fits";
            System.out.println( "   Result at: " + fname );
            try {
                assembler.writeFits( fname );
            }
            catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
    }

    interface MocAssembler {
        public void add( int order, long val ) throws Exception;
        public void end() throws Exception;
        public double getCoverage();
        public long getPixelCount();
        public void writeFits( String filename ) throws Exception;
    }
}
