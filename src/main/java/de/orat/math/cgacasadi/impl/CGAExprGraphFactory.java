package de.orat.math.cgacasadi.impl;

import de.orat.math.cgacasadi.CGACayleyTable;
import de.orat.math.cgacasadi.CGAKVectorSparsity;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import java.util.List;
import java.util.Random;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory implements iExprGraphFactory {
    
    public MultivectorSymbolic createMultivectorSymbolic(String name, ColumnVectorSparsity sparsity){
         return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
    }
     
    public MultivectorSymbolic createMultivectorSymbolic(String name){
         return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name));
    }
    
    public MultivectorSymbolic createMultivectorSymbolic(String name, int grade){
        CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
    }
    
    public double[] createRandomCGAMultivector(){
        return createRandomMultivector(CGACayleyTable.CGABasisBladeNames.length);
    }
    
    /**
     * Create a numeric multivector. Sparsity is created from zero values. 
     * 
     * @param values
     * @return 
     */
    public MultivectorNumeric createMultivectorNumeric(double[] values){
        SparseCGANumericMultivector impl = new SparseCGANumericMultivector(values);
        return MultivectorNumeric.get(impl);
    }
    public MultivectorNumeric createMultivectorNumeric(double[] nonzeros, int[] rows){
        SparseCGANumericMultivector impl = new SparseCGANumericMultivector(nonzeros, rows);
        return MultivectorNumeric.get(impl);
    }
    public MultivectorNumeric createRandomMultivectorNumeric(){
        return createMultivectorNumeric(createRandomMultivector(CGACayleyTable.CGABasisBladeNames.length));
    }
    
    public FunctionSymbolic createFunctionSymbolic(String name, List<MultivectorSymbolic> parameters,
                                           List<MultivectorSymbolic> returns){
        return FunctionSymbolic.get(new CGASymbolicFunction(), name, parameters, returns);
    }
    
    
    // helper methods
    
    static double[] createRandomMultivector(int basisBladesCount){
        Random random = new Random();
        return random.doubles(-1, 1).
                limit(basisBladesCount).toArray();
    }
}
