package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
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

    /**
     * Only to be used by non-static create Method for DelegatingSparseCGANumericMultivector.
     */
    @Deprecated
    private SparseCGANumericMultivector(SparseCGASymbolicMultivector sym) {
        super(sym);
        this.isInputParent = false;
    }

    /**
     * Only to be used from DelegatingSparseCGANumericMultivector! Otherwise will lead to inconsistencies!
     */
    @Deprecated
    @Override
    protected SparseCGANumericMultivector create(SparseCGASymbolicMultivector sym) {
        // Call permitted here.
        return new SparseCGANumericMultivector(sym);
    }

    private final boolean isInputParent;

    /**
     * Only to be used by static create Method with DM input.
     */
    @Deprecated
    private SparseCGANumericMultivector(PurelySymbolicCachedSparseCGASymbolicMultivector pureSym, DM dm) {
        super(pureSym);
        this.lazyDM = dm;
        this.isInputParent = true;
    }

    private static int num = 0;

    public static SparseCGANumericMultivector create(DM dm) {
        var nameSym = String.format("x%s", String.valueOf(num));
        ++num;
        var pureSym = new PurelySymbolicCachedSparseCGASymbolicMultivector(nameSym, dm.sparsity());
        // Call permittet here.
        return new SparseCGANumericMultivector(pureSym, dm);
    }

    public static SparseCGANumericMultivector createFrom(SparseCGASymbolicMultivector sym) {
        /*
         * https://github.com/casadi/casadi/wiki/L_rf
         * Evaluates the expression numerically.
         * An error is raised when the expression contains symbols.
         */
        var dm = SxStatic.evalf(sym.getSX());
        return create(dm);
    }

    public static SparseCGANumericMultivector create(double[] values) {
        if (baseCayleyTable.getBladesCount() != values.length) {
            throw new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "
                + String.valueOf(values.length));
        }
        var dm = CasADiUtil.toDM(values, true);
        return create(dm);
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
        return create(dm);
    }

    public static SparseCGANumericMultivector create(SparseDoubleMatrix vec) {
        return create(vec.nonzeros(), vec.getSparsity().getrow());
    }

    public static SparseCGANumericMultivector create(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        SparseDoubleMatrix sdm = new SparseDoubleMatrix(sparsity, new double[]{scalar});
        return create(sdm);
    }

    private DM lazyDM = null;

    public DM getDM() {
        if (this.lazyDM == null) {
            /*
            StdVectorSX in = new StdVectorSX();
            StdVectorSX out = new StdVectorSX(new SX[]{super.delegate.getSX()});
            var options = new de.dhbw.rahmlab.casadi.impl.std.Dict();
            options.put("allow_free", new de.dhbw.rahmlab.casadi.impl.casadi.GenericType(true));
            var func = new Function("ddd", in, out, options);
            var freeList = func.free_sx();
            System.out.println(freeList.size());
            for (var free : freeList) {
                System.out.println(free.name());
            }
             */
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
