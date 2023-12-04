package de.orat.math.cgacasadi;

import de.orat.math.sparsematrix.MatrixSparsity;

/**
  * @author Oliver Rettig (Oliver.Rettig@orat.de)
  */
public class CGABasisBladeSparsity extends MatrixSparsity {
    
    /**
     * Creates a sparse definition for a basis blade
     * 
     * @param basisBladeNames 
     * @param index column index of the base blade
     */
    public CGABasisBladeSparsity(String[] basisBladeNames, int index){
        // int n_row, int n_col, int[] colind, int[] row
        super(basisBladeNames.length, 1, new int[]{0,1}, new int[]{index});
    }
}