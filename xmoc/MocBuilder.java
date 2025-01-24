package xmoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PrimitiveIterator;
import uk.ac.starlink.util.LongList;

public class MocBuilder {

    private final int maxLevel_;
    private final IndexBag[] bags_;

    public static final int MAX_LEVEL = 29;

    public MocBuilder() {
        this( MAX_LEVEL );
    }

    public MocBuilder( int maxLevel ) {
        maxLevel_ = Math.min( maxLevel, MAX_LEVEL );
        bags_ = new IndexBag[ maxLevel_ + 1 ];
    }

    public void addTile( int level, long index ) {
        if ( level > maxLevel_ ) {
            index = index >> 2 * ( level - maxLevel_ );
            level = maxLevel_;
        }
        getBag( level ).addIndex( index );
    }

    public ArraysMoc createMoc() {
        List<long[]> arraysList = new ArrayList<>();
        for ( int ilevel = maxLevel_; ilevel >= 0; ilevel-- ) {
            IndexBag bag = bags_[ ilevel ];
            if ( bag != null || ! arraysList.isEmpty() ) {
                if ( bag == null ) {
                    arraysList.add( new long[ 0 ] );
                }
                else {
                    LongList ixList = new LongList();
                    long iquad = -1;
                    long[] quadMembers = new long[ 4 ];
                    int quadCount = 0;
                    for ( PrimitiveIterator.OfLong ixIt =
                              bag.sortedLongs().iterator(); ixIt.hasNext(); ) {
                        long index = ixIt.nextLong();
                        long iq = index >> 2;
                        if ( iq != iquad ) {
                            if ( quadCount == 4 && ilevel > 0 ) {
                                getBag( ilevel - 1 ).addIndex( iquad );
                            }
                            else {
                                for ( int i = 0; i < quadCount; i++ ) {
                                    ixList.add( quadMembers[ i ] );
                                }
                            }
                            iquad = iq;
                            quadCount = 0;
                        }
                        quadMembers[ quadCount++ ] = index;
                    }
                    if ( quadCount == 4 && ilevel > 0 ) {
                        getBag( ilevel - 1 ).addIndex( iquad );
                    }
                    else {
                        for ( int i = 0; i < quadCount; i++ ) {
                            ixList.add( quadMembers[ i ] );
                        }
                    }
                    arraysList.add( ixList.toLongArray() );
                }
            }
        }
        Collections.reverse( arraysList );
        return new ArraysMoc( arraysList.toArray( new long[ 0 ][] ) );
    }

    private IndexBag getBag( int level ) {
        if ( bags_[ level ] == null ) {
            bags_[ level ] = IndexBags.createSizeBag( 12L << 2 * level );
        }
        return bags_[ level ];
    }
}
