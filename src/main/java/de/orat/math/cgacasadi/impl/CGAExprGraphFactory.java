package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import util.cga.CGAKVectorSparsity;
import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;
import java.util.Random;
import util.cga.CGACayleyTableGeometricProduct;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory implements iExprGraphFactory {

    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    
    // create symbolic multivectors
    
    @Override
    public MultivectorSymbolic createMultivectorSymbolic(String name, ColumnVectorSparsity sparsity) {
        return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
        //return new SparseCGASymbolicMultivector(name, sparsity);
    }

    @Override
    public MultivectorSymbolic createMultivectorSymbolic(String name) {
        return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name));
        // return new SparseCGASymbolicMultivector(name);
    }
    
    @Override
    public MultivectorSymbolic createMultivectorSymbolic(String name, int grade) {
        CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
		
        //CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        //return new SparseCGASymbolicMultivector(name, sparsity);
    }

    @Override
    public MultivectorSymbolic createMultivectorSymbolic(String name, SparseDoubleColumnVector sparseVector) {
        return MultivectorSymbolic.get(SparseCGASymbolicMultivector.instance(name, sparseVector));
        //return SparseCGASymbolicMultivector.instance(name, sparseVector);
    }

    
    // helper methods
    
    static double[] createRandomMultivector(int basisBladesCount) {
        Random random = new Random();
        return random.doubles(-1, 1).
                limit(basisBladesCount).toArray();
    }

    public double[] createRandomCGAMultivector() {
        return createRandomMultivector(baseCayleyTable.getBladesCount());
    }
    
    public double[] createRandomCGAKVector(int basisBladesCount, int grade){
        double[] result = new double[baseCayleyTable.getRows()];
        Random random = new Random();
        int[] indizes = baseCayleyTable.getIndizes(grade);
        double[] values = random.doubles(-1, 1).
                limit(indizes.length).toArray();
        for (int i=0;i<indizes.length;i++){
            result[indizes[i]] = values[i];
        }
        return result;
    }

    /*public double[] createRandomCGAKVector(){
        Random random = new Random();
        return createRandomCGAKVector(random.nextInt(0, baseCayleyTable.getPseudoscalarGrade()+1));
    }*/
    public double[] createRandomCGAKVector(int grade){
        return createRandomCGAKVector(baseCayleyTable.getBladesCount(), grade);
    }

	

    // create numeric multivectors
    
    /**
     * Create a numeric multivector. Sparsity is created from zero values.
     *
     * @param values
     * @return
     */
    public MultivectorNumeric createMultivectorNumeric(double[] values) {
        return MultivectorNumeric.get(new SparseCGANumericMultivector(values));
        //return new SparseCGANumericMultivector(values);
    }

    public MultivectorNumeric createMultivectorNumeric(double[] nonzeros, int[] rows) {
        return MultivectorNumeric.get(new SparseCGANumericMultivector(nonzeros, rows));
    }

    public MultivectorNumeric createRandomMultivectorNumeric() {
        return createMultivectorNumeric(createRandomMultivector(baseCayleyTable.getBladesCount()));
    }

    @Override
    public MultivectorNumeric createMultivectorNumeric(double[] nonzeros, SparseDoubleColumnVector sparsity) {
            throw new UnsupportedOperationException("Not supported yet.");
    }

    // create function objects
    public FunctionSymbolic createFunctionSymbolic(String name, List<MultivectorSymbolic> parameters,
            List<MultivectorSymbolic> returns) {
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
}