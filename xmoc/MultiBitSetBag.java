package xmoc;

import java.util.BitSet;
import java.util.stream.LongStream;

public class MultiBitSetBag implements IndexBag {

    private final long size_;
    private final int bankSize_;
    private final int nbank_;
    private final BitSet[] banks_;

    public static final int DFLT_BANKSIZE = 1024 * 1024 * 16;

    public MultiBitSetBag( long size ) {
        this( size, DFLT_BANKSIZE );
    }

    public MultiBitSetBag( long size, int bankSize ) {
        size_ = size;
        bankSize_ = bankSize;
        long bankSizeL = bankSize;
        long nb = ( ( size - 1 ) / bankSizeL ) + 1;
        nbank_ = (int) nb;
        if ( nbank_ != nb ) {
            throw new IllegalArgumentException( "Bank count " + nb
                                              + " too high" );
        }
        banks_ = new BitSet[ nbank_ ];
    }

    public void addIndex( long lindex ) {
        getBank( lindex ).set( getOffset( lindex ) );
    }

    public boolean hasIndex( long lindex ) {
        BitSet bank = getBank( lindex );
        return bank != null && bank.get( getOffset( lindex ) );
    }

    public LongStream sortedLongs() {
        LongStream stream = LongStream.empty();
        for ( int ib = 0; ib < nbank_; ib++ ) {
            BitSet bank = banks_[ ib ];
            if ( bank != null ) {
                long offset = ib * bankSize_;
                LongStream s1 = bank.stream().asLongStream()
                               .map( l -> l + offset );
                stream = LongStream
                        .concat( stream,
                                 bank.stream().asLongStream()
                                     .map( l -> l + offset ) );
            }
        }
        return stream;
    }

    private int getBankIndex( long lindex ) {
        return (int) ( lindex / bankSize_ );
    }

    private BitSet getBank( long lindex ) {
        int ibank = getBankIndex( lindex );
        BitSet bank = banks_[ ibank ];
        if ( bank == null ) {
            bank = new BitSet( bankSize_ );
            banks_[ ibank ] = bank;
        }
        return bank;
    }

    private int getOffset( long lindex ) {
        return (int) ( lindex % bankSize_ );
    }
}
