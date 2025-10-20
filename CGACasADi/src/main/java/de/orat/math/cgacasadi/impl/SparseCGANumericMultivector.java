package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import util.cga.CGACayleyTableGeometricProduct;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.cgacasadi.delegating.annotation.api.GenerateDelegate;
import de.orat.math.cgacasadi.impl.gen.DelegatingSparseCGANumericMultivector;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.spi.iConstantsFactory;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import util.cga.CGAMultivectorSparsity;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 *
 * Achtung: Es können Objekte mit und ohne sparsity erzeugt werden.
 */
@GenerateDelegate(to = SparseCGASymbolicMultivector.class)
public class SparseCGANumericMultivector extends DelegatingSparseCGANumericMultivector implements iMultivectorNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector>, ISparseCGASymbolicMultivector {

    private final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    private MultivectorNumeric.Callback callback;

    @Override
    public void init(MultivectorNumeric.Callback callback) {
        this.callback = callback;
    }

    //======================================================
    // Constructors and static creators.
    // -> Constructors must only used within subclasses.
    //======================================================
    /**
     * Constructors must only used within subclasses. Can create invalid (not numeric)
     * SparseCGANumericMultivector if used wrong.
     */
    @Deprecated
    protected SparseCGANumericMultivector(SparseCGASymbolicMultivector sym) {
        super(sym);
    }

    /**
     * Constructors must only used within subclasses.
     */
    private SparseCGANumericMultivector(DM dm) {
        super(SparseCGASymbolicMultivector.create(dm));
        this.lazyDM = dm;
    }

    /**
     * Can create invalid (not numeric) SparseCGANumericMultivector if used wrong.
     */
    @Deprecated
    @Override
    protected SparseCGANumericMultivector create(SparseCGASymbolicMultivector sym) {
        return new SparseCGANumericMultivector(sym);
    }

    public static SparseCGANumericMultivector create(DM dm) {
        return new SparseCGANumericMultivector(dm);
    }

    public static SparseCGANumericMultivector create(double[] values) {
        if (baseCayleyTable.getBladesCount() != values.length) {
            throw new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "
                + String.valueOf(values.length));
        }
        var dm = CasADiUtil.toDM(values);
        return new SparseCGANumericMultivector(dm);
    }

    public static SparseCGANumericMultivector create(double[] nonzeros, int[] rows) {
        if (baseCayleyTable.getBladesCount() < nonzeros.length) {
            throw new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "
                + String.valueOf(nonzeros.length));
        }
        if (nonzeros.length != rows.length) {
            throw new IllegalArgumentException("Construction of CGA multivector failed because nonzeros.length != rows.length!");
        }
        var dm = CasADiUtil.toDM(baseCayleyTable.getBladesCount(), nonzeros, rows);
        return new SparseCGANumericMultivector(dm);
    }

    public static SparseCGANumericMultivector create(SparseDoubleMatrix vec) {
        var sym = SparseCGASymbolicMultivector.create("", vec);
        return new SparseCGANumericMultivector(sym);
    }

    public static SparseCGANumericMultivector create(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        SparseDoubleMatrix sdm = new SparseDoubleMatrix(sparsity, new double[]{scalar});
        var sym = SparseCGASymbolicMultivector.create("", sdm);
        return new SparseCGANumericMultivector(sym);
    }

    private DM lazyDM = null;

    public DM getDM() {
        if (this.lazyDM == null) {
            /*
            * https://github.com/casadi/casadi/wiki/L_rf
            * Evaluates the expression numerically.
            * An error is raised when the expression contains symbols.
             */
            this.lazyDM = SxStatic.evalf(super.delegate.getSX());
        }
        return lazyDM;
    }

    @Override
    public String toString() {
        return this.getDM().toString();
    }

    /**
     * Get a complete multivector as double[], inclusive structural 0 values.
     *
     * @return double[32] elements corresponding to the underlaying implementation specific coordindate
     * system.
     */
    @Override
    public SparseDoubleMatrix elements() {
        return CasADiUtil.elements(this.getDM());
    }

    @Override
    public SparseCGASymbolicMultivector toSymbolic() {
        return super.delegate;
    }

    @Override
    public iConstantsFactory<SparseCGANumericMultivector> constants() {
        return CGAExprGraphFactory.instance.constantsNumeric();
    }

    @Override
    public SX getSX() {
        return this.toSymbolic().getSX();
    }
}
