package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;
import java.util.Map;
import java.util.Random;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory implements iExprGraphFactory<SparseCGASymbolicMultivector> {

    private final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    // create symbolic multivectors
    @Override
    public SparseCGASymbolicMultivector createMultivectorSymbolic(String name, MatrixSparsity sparsity) {
        return SparseCGASymbolicMultivector.create(name, ColumnVectorSparsity.instance(sparsity));
    }

    @Override
    public SparseCGASymbolicMultivector createMultivectorSymbolic(String name) {
        return SparseCGASymbolicMultivector.create(name);
    }

    @Override
    public SparseCGASymbolicMultivector createMultivectorSymbolic(String name, int grade) {
        return SparseCGASymbolicMultivector.create(name, grade);
    }

    @Override
    public SparseCGASymbolicMultivector createMultivectorSymbolic(String name, SparseDoubleMatrix sparseVector) {
        return SparseCGASymbolicMultivector.create(name, sparseVector);
    }

    // helper methods
    @Override
    public double[] createRandomMultivector() {
        return createRandomMultivector(baseCayleyTable.getBladesCount());
    }

    public double[] createRandomKVector(int grade) {
        double[] result = new double[baseCayleyTable.getRows()];
        Random random = new Random();
        int[] indizes = CGACayleyTableGeometricProduct.getIndizes(grade);
        double[] values = random.doubles(-1, 1).
            limit(indizes.length).toArray();
        for (int i = 0; i < indizes.length; i++) {
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
    public iMultivectorNumeric createMultivectorNumeric(SparseDoubleMatrix vec) {
        return new SparseCGANumericMultivector(vec.nonzeros(), vec.getSparsity().getrow());
    }

    // create function objects
    @Override
    public CGASymbolicFunction createFunctionSymbolic(String name, List<SparseCGASymbolicMultivector> parameters, List<SparseCGASymbolicMultivector> returns) {
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
    public SparseDoubleMatrix createBaseVectorOrigin(double scalar) {
        double[] nonzeros = new double[]{-0.5d * scalar, 0.5d * scalar};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
        //CGA e4s = new CGA(4, -0.5*scale);
        //CGA e5s = new CGA(5, 0.5*scale);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorInfinity(double scalar) {
        double[] nonzeros = new double[]{scalar, scalar};
        //CGA e4s = new CGA(4, scale);
        //CGA e5s = new CGA(5, scale);
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorX(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{1};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorY(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{2};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorZ(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createScalar(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    @Override
    public SparseDoubleMatrix createEpsilonPlus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4, 5});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{0d, 1d});
    }

    @Override
    public SparseDoubleMatrix createEpsilonMinus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4, 5});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{1d, 0d});
    }

    @Override
    public SparseDoubleMatrix createEuclideanPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{16});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{1d});
    }

    @Override
    public SparseDoubleMatrix createPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{31});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{1d});
    }

    @Override
    public SparseDoubleMatrix createMinkovskyBiVector() {
        // 2e45
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{15});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{2d});
    }

    @Override
    public SparseDoubleMatrix createE(double x, double y, double z) {
        double[] nonzeros = new double[]{x, y, z};
        int[] rows = new int[]{1, 2, 3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorInfinityDorst() {
        double[] nonzeros = new double[]{-1d, 1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorOriginDorst() {
        double[] nonzeros = new double[]{0.5d, 0.5d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorInfinityDoran() {
        double[] nonzeros = new double[]{1d, 1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorOriginDoran() {
        double[] nonzeros = new double[]{1d, -1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }
}
