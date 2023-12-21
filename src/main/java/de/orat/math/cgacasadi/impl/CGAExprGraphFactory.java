package de.orat.math.cgacasadi.impl;

import util.cga.CGACayleyTable;
import util.cga.CGAKVectorSparsity;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;
import java.util.Random;
import util.Algebra;
import util.cga.CGACayleyTableGeometricProduct;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory implements iExprGraphFactory {
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    
    // create symbolic multivectors
    
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
    
    @Override
    public MultivectorSymbolic createMultivectorSymbolic(String name, SparseDoubleColumnVector sparseVector) {
        return MultivectorSymbolic.get(SparseCGASymbolicMultivector.instance(name, sparseVector));
    }

    
    
    
    // create numeric multivectors
    
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
        return createMultivectorNumeric(createRandomMultivector(baseCayleyTable.getBladesCount()));
    }
    
    @Override
    public MultivectorNumeric createMultivectorNumeric(double[] nonzeros, SparseDoubleColumnVector sparsity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    // create function objects
    
    public FunctionSymbolic createFunctionSymbolic(String name, List<MultivectorSymbolic> parameters,
                                           List<MultivectorSymbolic> returns){
        return FunctionSymbolic.get(new CGASymbolicFunction(), name, parameters, returns);
    }
    
    
    // methods to describe the functionality of the implementation
    
    @Override
    public String getAlgebra() {
        return "cga";
    }

    @Override
    public String getName() {
        return "casadimx";
    }
    
    
    // helper methods
    
    static double[] createRandomMultivector(int basisBladesCount){
        Random random = new Random();
        return random.doubles(-1, 1).
                limit(basisBladesCount).toArray();
    }

    public double[] createRandomCGAMultivector(){
        return createRandomMultivector(baseCayleyTable.getBladesCount());
    }
}
