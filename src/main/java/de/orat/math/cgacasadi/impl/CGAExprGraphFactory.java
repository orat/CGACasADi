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

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAExprGraphFactory implements iExprGraphFactory {

	final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

	// create symbolic multivectors
	public iMultivectorSymbolic createMultivectorSymbolic(String name, ColumnVectorSparsity sparsity) {
		return new SparseCGASymbolicMultivector(name, sparsity);
	}

	public iMultivectorSymbolic createMultivectorSymbolic(String name) {
		return new SparseCGASymbolicMultivector(name);
	}

	public iMultivectorSymbolic createMultivectorSymbolic(String name, int grade) {
		CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
		return new SparseCGASymbolicMultivector(name, sparsity);
	}

	@Override
	public iMultivectorSymbolic createMultivectorSymbolic(String name, SparseDoubleColumnVector sparseVector) {
		return SparseCGASymbolicMultivector.instance(name, sparseVector);
	}

	// create numeric multivectors
	/**
	 * Create a numeric multivector. Sparsity is created from zero values.
	 *
	 * @param values
	 * @return
	 */
	public iMultivectorNumeric createMultivectorNumeric(double[] values) {
		return new SparseCGANumericMultivector(values);
	}

	public iMultivectorNumeric createMultivectorNumeric(double[] nonzeros, int[] rows) {
		return new SparseCGANumericMultivector(nonzeros, rows);
	}

	public iMultivectorNumeric createRandomMultivectorNumeric() {
		return createMultivectorNumeric(createRandomMultivector(baseCayleyTable.getBladesCount()));
	}

	@Override
	public iMultivectorNumeric createMultivectorNumeric(double[] nonzeros, SparseDoubleColumnVector sparsity) {
		throw new UnsupportedOperationException("Not supported yet.");
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

	// helper methods
	static double[] createRandomMultivector(int basisBladesCount) {
		Random random = new Random();
		return random.doubles(-1, 1).
			limit(basisBladesCount).toArray();
	}

	public double[] createRandomCGAMultivector() {
		return createRandomMultivector(baseCayleyTable.getBladesCount());
	}
}
