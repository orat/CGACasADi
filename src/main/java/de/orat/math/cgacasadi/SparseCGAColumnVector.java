package de.orat.math.cgacasadi;

import de.orat.math.sparsematrix.SparseDoubleColumnVector;


/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class SparseCGAColumnVector extends SparseDoubleColumnVector {
    
    public SparseCGAColumnVector(CGAMultivectorSparsity sparsity, double[] data) {
        super(sparsity, data);
    }
    
}
