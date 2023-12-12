package de.orat.math.cgacasadi;

import de.orat.math.ga.matrix.utils.CayleyTable;
import de.orat.math.sparsematrix.DenseDoubleMatrix;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import util.LinearOperators;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAOperatorMatrixUtils extends LinearOperators {
    
    private final CayleyTable cayleyTable;
    private static SparseDoubleMatrix reversionOperatorMatrix;
    
    public CGAOperatorMatrixUtils(CayleyTable cayleyTable){
        this.cayleyTable = cayleyTable;
    }
    
    public SparseDoubleMatrix getReversionOperatorMatrix(){
        if (reversionOperatorMatrix == null){
            reversionOperatorMatrix = createReversionOperatorMatrix(cayleyTable);
        }
        return reversionOperatorMatrix;
    }
}
