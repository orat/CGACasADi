package de.orat.math.cgacasadi.impl;

import de.orat.math.cgacasadi.CGACayleyTable;
import de.orat.math.cgacasadi.CGAKVectorSparsity;
import de.orat.math.gacalc.api.MultivectorSymbolic;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory extends ExprGraphFactory {
    
    public MultivectorSymbolic createMultivectorSymbolic(String name, int grade){
        CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
    }
    
    public double[] createRandomCGAMultivector(){
        return createRandomMultivector(CGACayleyTable.CGABasisBladeNames.length);
    }
}
