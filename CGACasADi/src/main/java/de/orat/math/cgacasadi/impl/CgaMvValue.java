package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.cgacasadi.delegating.annotation.api.GenerateDelegate;
import de.orat.math.cgacasadi.impl.gen.DelegatingCgaMvValue;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;
import de.orat.math.gacalc.spi.IConstants;
import de.orat.math.gacalc.spi.IMultivectorValue;

@GenerateDelegate(to = CgaMvExpr.class)
public class CgaMvValue extends DelegatingCgaMvValue implements IMultivectorValue<CgaMvValue, CgaMvExpr>, IGetSX {

    private final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    private MultivectorValue.Callback callback;

    @Override
    public void init(MultivectorValue.Callback callback) {
        this.callback = callback;
    }

    private final ComposableImmutableBinaryTree<CgaMvValue> inputs;

    /**
     * Only to be used by non-static create Method for DelegatingCgaMvValue.
     */
    @Deprecated
    private CgaMvValue(CgaMvExpr sym, ComposableImmutableBinaryTree<CgaMvValue> inputs) {
        super(sym);
        this.inputs = inputs;
    }

    /**
     * Only to be used from DelegatingCgaMvValue! Otherwise will lead to inconsistencies!
     */
    @Deprecated
    @Override
    protected CgaMvValue create(CgaMvExpr sym) {
        // Call permitted here.
        return new CgaMvValue(sym, this.inputs);
    }

    /**
     * Only to be used from DelegatingCgaMvValue! Otherwise will lead to inconsistencies!
     */
    @Deprecated
    @Override
    protected CgaMvValue create(CgaMvExpr sym, CgaMvValue other) {
        var combinedInputs = this.inputs.append(other.inputs);
        // Call permitted here.
        return new CgaMvValue(sym, combinedInputs);
    }

    /**
     * Creates a leaf. Only to be used by static create Method with DM input.
     */
    private CgaMvValue(DM dm) {
        super(dmToPureSym(dm));
        this.lazyDM = dm;
        // Not "leaking this", because the passed "this" will not be used before fully constructed.
        // Because ComposableImmutableBinaryTree instance just stores the "this" and is itself not visible from outside the constructor.
        this.inputs = new ComposableImmutableBinaryTree<>(this);
    }

    private static int num = 0;

    private static CgaMvVariable dmToPureSym(DM dm) {
        var nameSym = String.format("x%s", String.valueOf(num));
        ++num;
        var pureSym = new CgaMvVariable(nameSym, dm.sparsity());
        return pureSym;
    }

    public static CgaMvValue create(DM dm) {
        return new CgaMvValue(dm);
    }

    public static CgaMvValue createFrom(CgaMvExpr sym) {
        /*
         * https://github.com/casadi/casadi/wiki/L_rf
         * Evaluates the expression numerically.
         * An error is raised when the expression contains symbols.
         */
        var dm = SxStatic.evalf(sym.getSX());
        return create(dm);
    }

    public static CgaMvValue create(SparseDoubleMatrix vec) {
        double[] nonzeros = vec.nonzeros();
        int[] rows = vec.getSparsity().getrow();
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

    public static CgaMvValue create(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        SparseDoubleMatrix sdm = new SparseDoubleMatrix(sparsity, new double[]{scalar});
        return create(sdm);
    }

    /**
     * Nullable!
     */
    private DM lazyDM = null;

    /**
     * Expensive for MVnum, which are not created directly from a numerical constructor, but through method
     * chaining.
     */
    public DM getDM() {
        if (this.lazyDM == null) {
            var allInputs = this.inputs.computeUniqueLeafs().stream().toList();
            var allInputsParams = allInputs.stream().map(CgaMvValue::delegatePurelySym).toList();
            var func = new CgaFunction("getDM", allInputsParams, List.of(this.delegate));
            var evalMV = func.callValue(allInputs).get(0);
            // lazyDM is set for all leafs.
            this.lazyDM = evalMV.lazyDM;
        }
        return lazyDM;
    }

    /**
     * Can be expensive.
     */
    @Override
    public String toString() {
        return this.getDM().toString();
    }

    /**
     * Get a complete multivector as double[], inclusive structural 0 values. Can be expensive.
     *
     * @return double[32] elements corresponding to the underlaying implementation specific coordindate
     * system.
     */
    @Override
    public SparseDoubleMatrix elements() {
        return CasADiUtil.elements(this.getDM());
    }

    /**
     * Can be expensive.
     */
    @Override
    public CgaMvExpr toExpr() {
        var dm = this.getDM();
        var mv = CgaMvExpr.create(dm).simplifySparsify();
        return mv;
    }

    /**
     * Only works on MVnum which were constructed from a DM.
     */
    private CgaMvVariable delegatePurelySym() {
        return (CgaMvVariable) super.delegate;
    }

    @Override
    public IConstants<CgaMvValue> constants() {
        return CgaFactory.instance.constantsValue();
    }

    @Override
    public SX getSX() {
        return super.delegate.getSX();
    }
}
