package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.cgacasadi.impl.gen.CachedCgaMvExpr;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAKVectorSparsity;
import util.cga.CGAMultivectorSparsity;
import de.orat.math.gacalc.spi.IMultivectorVariable;

public class CgaMvVariable extends CachedCgaMvExpr implements IMultivectorVariable<CgaMvExpr> {

    private static final ColumnVectorSparsity SPARSE = ColumnVectorSparsity.empty(CGACayleyTableGeometricProduct.instance().getBladesCount());

    public static CgaMvVariable createSparse(String name) {
        return new CgaMvVariable(name, SPARSE);
    }

    private static final ColumnVectorSparsity DENSE = ColumnVectorSparsity.dense(CGACayleyTableGeometricProduct.instance().getBladesCount());

    public static CgaMvVariable createDense(String name) {
        return new CgaMvVariable(name, DENSE);
    }

    public CgaMvVariable(String name, ColumnVectorSparsity sparsity) {
        super(name, SxStatic.sym(name, CasADiUtil.toCasADiSparsity(sparsity)));
        assert super.getSX().is_valid_input();
    }

    public CgaMvVariable(String name, Sparsity sparsity) {
        super(name, SxStatic.sym(name, sparsity));
        assert super.getSX().is_valid_input();
    }

    public CgaMvVariable(String name, CgaMvExpr from) {
        this(name, from.getSX().sparsity());
    }

    /**
     * <pre>
     * Creates a k-Vector.
     * </pre>
     *
     * @param name
     * @param grade
     */
    public CgaMvVariable(String name, int grade) {
        this(name, CGAKVectorSparsity.instance(grade));
    }

    public CgaMvVariable(String name, int[] grades) {
        this(name, computeSparsityFromGradesArray(grades));
    }

    /**
     * <pre>
     * Does not check for the same grade given more then once!
     * ToDo: create fast implementation of this.
     * </pre>
     */
    private static CGAMultivectorSparsity computeSparsityFromGradesArray(int[] grades) {
        if (grades.length < 1) {
            throw new RuntimeException("At least one grade must be given.");
        }

        CgaMvExpr mv = new CgaMvVariable(String.valueOf(grades[0]), grades[0]);
        for (int i = 1; i < grades.length; ++i) {
            mv = mv.add(new CgaMvVariable(String.valueOf(grades[i]), grades[i]));
        }

        return mv.getSparsity();
    }
}
