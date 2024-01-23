package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iEuclideanTypeConverter;
import util.cga.CGAKVectorSparsity;
import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;
import java.util.Random;
import org.jogamp.vecmath.Tuple3d;
import util.cga.CGACayleyTableGeometricProduct;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory implements iExprGraphFactory {

    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    
    // create symbolic multivectors
    
    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name, ColumnVectorSparsity sparsity) {
        //return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
        return new SparseCGASymbolicMultivector(name, sparsity);
    }

    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name) {
        //return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name));
        return new SparseCGASymbolicMultivector(name);
    }
    
    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name, int grade) {
        CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        //return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name, sparsity));
        return new SparseCGASymbolicMultivector(name, sparsity);
    }

    @Override
    public iMultivectorSymbolic createMultivectorSymbolic(String name, SparseDoubleColumnVector sparseVector) {
        //return MultivectorSymbolic.get(SparseCGASymbolicMultivector.instance(name, sparseVector));
        return SparseCGASymbolicMultivector.instance(name, sparseVector);
    }

    
    // helper methods
    
    public static double[] createRandomMultivector(int basisBladesCount) {
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
    @Override
    public iMultivectorNumeric createMultivectorNumeric(double[] values) {
        //return MultivectorNumeric.get(new SparseCGANumericMultivector(values));
        return new SparseCGANumericMultivector(values);
    }
    @Override
    public iMultivectorNumeric createMultivectorNumeric(double[] nonzeros, int[] rows) {
        return new SparseCGANumericMultivector(nonzeros, rows);
    }
    @Override
    public iMultivectorNumeric createRandomMultivectorNumeric() {
        return createMultivectorNumeric(createRandomMultivector(baseCayleyTable.getBladesCount()));
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
        return "casadimx";
    }

    
    // create constants
    
    @Override
    public SparseDoubleColumnVector createBaseVectorOrigin(double scalor) {
        double[] nonzeros = new double[]{-0.5d*scalor, 0.5d*scalor};
        int[] rows = new int[]{4,5};
        iMultivectorNumeric mvn = createMultivectorNumeric(nonzeros, rows);
        //CGA e4s = new CGA(4, -0.5*scale);
	//CGA e5s = new CGA(5, 0.5*scale);
        //TODO
        // wie komme ich aus iMultivectorNumeric ein iMultivectorSymbolic?
        //return createMultivectorSymbolic(String name, SparseDoubleColumnVector sparseVector)
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorInfinity(double scalor) {
        //getEuclideanTypeConverter().
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorX(double scalar) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorY(double scalar) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createBaseVectorZ(double scalar) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createEpsilonPlus() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createEpsilonMinus() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createMinkovskyBiVector() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createEuclideanPseudoscalar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleColumnVector createPseudoscalar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private static iEuclideanTypeConverter euclideanTypeConverter;
    
    public iEuclideanTypeConverter getEuclideanTypeConverter(){
        if (euclideanTypeConverter == null){
            euclideanTypeConverter = new CGAEuclideanTypeConverter(this);
        }
        return euclideanTypeConverter;
    }

    @Override
    public SparseDoubleColumnVector createE(Tuple3d tuple3d) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}