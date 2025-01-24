
import cds.moc.Range;
import cds.moc.SMoc;
import java.util.Arrays;

/**
 * Extends SMoc with a fast multiple addition method supplied by Pierre
 * by email 23 Jan 2025.
 */
public class SMoc1 extends SMoc {

    SMoc1( int mocOrder ) {
        super( mocOrder );
    }

    /**
     * Fast addition of a list of singletons expressed
     * at the specified order
     * These singletons do not need to be sorted, nor to be unique.
     *
     * @param order singleton order
     * @param singletons list of singletons
     * @param size number of singletons
     */
    public void add( int order, long [] singletons, int size) {
        Arrays.sort(singletons,0,size);
        int shift = (maxOrder()-order) * shiftOrder();
        Range r = new Range(size);
        int j;
        for( int i=0; i<size; i++ ) {
            for( j=i; j<size-1 && (singletons[j+1]-singletons[j])<=1; j++ );
            long start = (singletons[i]) << shift;
            long end = (singletons[j]+1L) << shift;
            r.append(start,end);
        }
        range = range.union(r);
        resetCache();
    }
}
