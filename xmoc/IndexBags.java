package xmoc;

public class IndexBags {
    private static final int SET_SIZE_THRESHOLD = 100_000;
    public static IndexBag createSizeBag( long size ) {
        if ( size < 0 ) {
            throw new IllegalArgumentException();
        }
        else if ( size < 8 * 2_000_000 ) {
            return new BitSetBag( (int) size );
        }
        else if ( size < Integer.MAX_VALUE ) {
            return new IntegerBag( (int) size, SET_SIZE_THRESHOLD ); 
        }
        else {
            return new LongBag( size, SET_SIZE_THRESHOLD );
        }
    }
}
