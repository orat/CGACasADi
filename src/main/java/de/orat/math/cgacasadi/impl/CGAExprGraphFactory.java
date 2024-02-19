package de.orat.math.cgacasadi.impl;

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
import util.cga.CGAMultivectorSparsity;
import util.cga.SparseCGAColumnVector;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory implements iExprGraphFactory {

    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    
    // create symbolic multivectors
    
    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name, ColumnVectorSparsity sparsity) {
        return new SparseCGASymbolicMultivector(name, sparsity);
    }

    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name) {
        return new SparseCGASymbolicMultivector(name);
    }
    
    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name, int grade) {
        CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        return new SparseCGASymbolicMultivector(name, sparsity);
    }

    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name, SparseDoubleColumnVector sparseVector) {
        return SparseCGASymbolicMultivector.instance(name, sparseVector);
    }

    
    // helper methods
    
    public double[] createRandomMultivector() {
        return createRandomMultivector(baseCayleyTable.getBladesCount());
    }
    
    public double[] createRandomKVector(int grade){
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


    // create numeric multivectors
    
    /**
     * Create a numeric multivector. Sparsity is created from zero values.
     *
     * @param values
     * @return
     */
    @Override
    public iMultivectorNumeric createMultivectorNumeric(double[] values) {
        return new SparseCGANumericMultivector(values);
    }
    @Override
    public iMultivectorNumeric createMultivectorNumeric(double[] nonzeros, int[] rows) {
        return new SparseCGANumericMultivector(nonzeros, rows);
    }
    @Override
    public iMultivectorNumeric createRandomMultivectorNumeric() {
        return createMultivectorNumeric(createRandomKVector(baseCayleyTable.getBladesCount()));
    }

    @Override
    public iMultivectorNumeric createMultivectorNumeric(SparseDoubleColumnVector vec) {
        return new SparseCGANumericMultivector(vec.nonzeros(), vec.getSparsity().getrow());
    }

    
    // create function objects
    
    public iFunctionSymbolic createFunctionSymbolic(String name, List<iMultivectorSymbolic> parameters,
        List<iMultivectorSymbolic> returns) {
        return new CGASymbolicFunction(name, parameters, returns);
    }

    
    // methods to describe the functionality of the implementation
    
    @Override
    public String getAlgebra() {
        return "cga";
    }
    
    @Override
    public String getName() {
        return "cgacasadimx";
    }

    
    // create constants
    
    @Override
    public SparseDoubleColumnVector createBaseVectorOrigin(double scalar) {
        double[] nonzeros = new double[]{-0.5d*scalar, 0.5d*scalar};
        int[] rows = new int[]{4,5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
        //CGA e4s = new CGA(4, -0.5*scale);
	//CGA e5s = new CGA(5, 0.5*scale);
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorInfinity(double scalar) {
        double[] nonzeros = new double[]{scalar, scalar};
        //CGA e4s = new CGA(4, scale);
	//CGA e5s = new CGA(5, scale);
        int[] rows = new int[]{4,5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorX(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{1};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorY(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{2};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorZ(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }

    @Override
    public SparseCGAColumnVector createScalar(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        return new SparseCGAColumnVector(sparsity, new double[]{scalar});
    }
    
    @Override
    public SparseDoubleColumnVector createEpsilonPlus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4,5});
        return new SparseCGAColumnVector(sparsity, new double[]{0d, 1d});
    }

    @Override
    public SparseDoubleColumnVector createEpsilonMinus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4,5});
        return new SparseCGAColumnVector(sparsity, new double[]{1d,0d});
    }

    @Override
    public SparseDoubleColumnVector createEuclideanPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{16});
        return new SparseCGAColumnVector(sparsity, new double[]{1d});
    }

    @Override
    public SparseDoubleColumnVector createPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{31});
        return new SparseCGAColumnVector(sparsity, new double[]{1d});
    }
    
    @Override
    public SparseDoubleColumnVector createMinkovskyBiVector() {
        // 2e45
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{15});
        return new SparseCGAColumnVector(sparsity, new double[]{2d});
    }
    
    @Override
    public SparseDoubleColumnVector createE(double x, double y, double z) {
        double[] nonzeros = new double[]{x, y, z};
        int[] rows = new int[]{1,2,3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }
    
    @Override
    public SparseDoubleColumnVector createBaseVectorInfinityDorst() {
        double[] nonzeros = new double[]{-1d, 1d};
        int[] rows = new int[]{4,5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorOriginDorst() {
        double[] nonzeros = new double[]{0.5d, 0.5d};
        int[] rows = new int[]{4,5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorInfinityDoran() {
        double[] nonzeros = new double[]{1d, 1d};
        int[] rows = new int[]{4,5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorOriginDoran() {
        double[] nonzeros = new double[]{1d, -1d};
        int[] rows = new int[]{4,5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseCGAColumnVector(sparsity, nonzeros);
    }
}