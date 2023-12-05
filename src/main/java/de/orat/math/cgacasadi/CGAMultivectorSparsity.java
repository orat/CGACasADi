package de.orat.math.cgacasadi;

import static de.orat.math.cgacasadi.CGACayleyTable.CGABasisBladeNames;
import de.orat.math.sparsematrix.ColumnVectorSparsity;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAMultivectorSparsity extends ColumnVectorSparsity {
    
    /**
     * It is allowed to have row indizes corresponding to different grades.
     * 
     * @param n_row
     * @param row 
     */
    public CGAMultivectorSparsity(int n_row, int[] row) {
        super(n_row, row);
    }
    
    public static CGAKVectorSparsity createSparsity(int[] nonzeros){
        return new CGAKVectorSparsity(CGABasisBladeNames.length, nonzeros);
    }
}
