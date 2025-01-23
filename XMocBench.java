
import java.io.IOException;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import xmoc.MocBuilder;
import xmoc.ArraysMoc;

public class XMocBench extends MocBench {

    MocAssembler createAssembler( int mocOrder ) {
        MocBuilder builder = new MocBuilder( mocOrder );
        return new MocAssembler() {
            ArraysMoc moc_;
            public void add( int order, long val ) {
                builder.addTile( order, val );
            }
            public void end() {
                moc_ = builder.createMoc();
            }
            public double getCoverage() {
                return moc_.getCoverage();
            }
            public long getPixelCount() {
                return moc_.getPixelCount();
            }
            public void writeFits( String filename ) throws IOException {
                moc_.uniqStream().forEach( l -> System.out.println( l ) );
            }
            public LongStream getUniqs() {
                return moc_.uniqStream();
            }
            public PrimitiveIterator.OfLong uniqIterator() {
                return moc_.uniqIterator();
            }
        };
    }

    public static void main( String[] args ) {
        new XMocBench().runBench( args );
    }
}
