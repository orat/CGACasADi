package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.cgacasadi.impl.gen.CachedSparseCGASymbolicMultivector;
import de.orat.math.gacalc.spi.iMultivectorPurelySymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import util.cga.CGAKVectorSparsity;
import util.cga.CGAMultivectorSparsity;

public class PurelySymbolicCachedSparseCGASymbolicMultivector extends CachedSparseCGASymbolicMultivector implements iMultivectorPurelySymbolic<SparseCGASymbolicMultivector> {

    public PurelySymbolicCachedSparseCGASymbolicMultivector(String name, ColumnVectorSparsity sparsity) {
        super(name, SX.sym(name, CasADiUtil.toCasADiSparsity(sparsity)));
        assert super.getSX().is_valid_input();
    }

    public PurelySymbolicCachedSparseCGASymbolicMultivector(String name) {
        super(name, SX.sym(name, de.dhbw.rahmlab.casadi.impl.casadi.Sparsity.dense(32)));
        assert super.getSX().is_valid_input();
    }

    /**
     * <pre>
     * Creates a k-Vector.
     * </pre>
     *
     * @param name
     * @param grade
     */
    public PurelySymbolicCachedSparseCGASymbolicMultivector(String name, int grade) {
        this(name, CGAKVectorSparsity.instance(grade));
    }

    public PurelySymbolicCachedSparseCGASymbolicMultivector(String name, int[] grades) {
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

        SparseCGASymbolicMultivector mv = new PurelySymbolicCachedSparseCGASymbolicMultivector( String.valueOf(grades[0]), grades[0]);
        for (int i = 1; i < grades.length; ++i) {
            mv = mv.add(new PurelySymbolicCachedSparseCGASymbolicMultivector(String.valueOf(grades[i]), grades[i]));
        }

        return mv.getSparsity();
    }
}
