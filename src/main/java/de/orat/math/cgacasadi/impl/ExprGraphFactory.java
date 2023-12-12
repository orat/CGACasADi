package de.orat.math.cgacasadi.impl;

import de.orat.math.cgacasadi.CGACayleyTable;
import de.orat.math.cgacasadi.CGAMultivectorSparsity;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class ExprGraphFactory {
    
    public static MultivectorSymbolic createMultivectorSymbolic(String name, ColumnVectorSparsity sparsity){
         return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
    }
     
    public static MultivectorSymbolic createMultivectorSymbolic(String name){
         return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name));
    }
    
    /**
     * Create a numeric multivector. Sparsity is created from zero values. 
     * 
     * @param values
     * @return 
     */
    public static MultivectorNumeric createMultivectorNumeric(double[] values){
        SparseCGANumericMultivector impl = new SparseCGANumericMultivector(values);
        return MultivectorNumeric.get(impl);
    }
    public static MultivectorNumeric createMultivectorNumeric(double[] nonzeros, int[] rows){
        SparseCGANumericMultivector impl = new SparseCGANumericMultivector(nonzeros, rows);
        return MultivectorNumeric.get(impl);
    }
    public static MultivectorNumeric createRandomMultivectorNumeric(){
        return createMultivectorNumeric(createRandomMultivector(CGACayleyTable.CGABasisBladeNames.length));
    }
    
    public static FunctionSymbolic createFunctionSymbolic(String name, List<MultivectorSymbolic> parameters,
                                           List<MultivectorSymbolic> returns){
        FunctionSymbolic f = FunctionSymbolic.get(new CGASymbolicFunction());
        f.setSymbolic(name, parameters, returns);
        return f;
    }
    
    public static FunctionSymbolic createFunctionNumeric(String name, List<MultivectorSymbolic> parameters,
                                           List<MultivectorNumeric> returns){
        FunctionSymbolic f = FunctionSymbolic.get(new CGASymbolicFunction());
        f.setNumeric(name, parameters, returns);
        return f;
    }
    
    
    // helper methods
    
    static double[] createRandomMultivector(int basisBladesCount){
        Random random = new Random();
        return random.doubles(-1, 1).
                limit(basisBladesCount).toArray();
    }
}
