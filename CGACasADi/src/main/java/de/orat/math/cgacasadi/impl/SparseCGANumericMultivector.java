package de.orat.math.cgacasadi.impl;

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

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 *
 * Achtung: Es können Objekte mit und ohne sparsity erzeugt werden.
 */
@GenerateDelegate(to = SparseCGASymbolicMultivector.class)
public class SparseCGANumericMultivector extends DelegatingSparseCGANumericMultivector implements iMultivectorNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector> {

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
     * Constructors must only used within subclasses.
     */
    @Deprecated
    protected SparseCGANumericMultivector(SparseCGASymbolicMultivector sym) {
        super(sym);
    }

    /**
     * Constructors must only used within subclasses.
     */
    @Deprecated
    private SparseCGANumericMultivector(DM dm) {
        super(SparseCGASymbolicMultivector.create(dm));
    }

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

    private DM lazyDM = null;

    public DM getDM() {
        if (this.lazyDM == null) {
            /*
            * https://github.com/casadi/casadi/wiki/L_rf
            * Evaluates the expression numerically.
            * An error is raised when the expression contains symbols.
             */
            this.lazyDM = SX.evalf(super.delegate.getSX());
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

    /**
     * Wedge.
     *
     * The outer product. (MEET)
     *
     * @param a
     * @param b
     * @return a ^ b
     */
    private static double[] staticOp(double[] a, double[] b) {
        double[] res = new double[a.length];
        res[0] = b[0] * a[0];
        res[1] = b[1] * a[0] + b[0] * a[1];
        res[2] = b[2] * a[0] + b[0] * a[2];
        res[3] = b[3] * a[0] + b[0] * a[3];
        res[4] = b[4] * a[0] + b[0] * a[4];
        res[5] = b[5] * a[0] + b[0] * a[5];
        res[6] = b[6] * a[0] + b[2] * a[1] - b[1] * a[2] + b[0] * a[6];
        res[7] = b[7] * a[0] + b[3] * a[1] - b[1] * a[3] + b[0] * a[7];
        res[8] = b[8] * a[0] + b[4] * a[1] - b[1] * a[4] + b[0] * a[8];
        res[9] = b[9] * a[0] + b[5] * a[1] - b[1] * a[5] + b[0] * a[9];
        res[10] = b[10] * a[0] + b[3] * a[2] - b[2] * a[3] + b[0] * a[10];
        res[11] = b[11] * a[0] + b[4] * a[2] - b[2] * a[4] + b[0] * a[11];
        res[12] = b[12] * a[0] + b[5] * a[2] - b[2] * a[5] + b[0] * a[12];
        res[13] = b[13] * a[0] + b[4] * a[3] - b[3] * a[4] + b[0] * a[13];
        res[14] = b[14] * a[0] + b[5] * a[3] - b[3] * a[5] + b[0] * a[14];
        res[15] = b[15] * a[0] + b[5] * a[4] - b[4] * a[5] + b[0] * a[15];
        res[16] = b[16] * a[0] + b[10] * a[1] - b[7] * a[2] + b[6] * a[3] + b[3] * a[6] - b[2] * a[7] + b[1] * a[10] + b[0] * a[16];
        res[17] = b[17] * a[0] + b[11] * a[1] - b[8] * a[2] + b[6] * a[4] + b[4] * a[6] - b[2] * a[8] + b[1] * a[11] + b[0] * a[17];
        res[18] = b[18] * a[0] + b[12] * a[1] - b[9] * a[2] + b[6] * a[5] + b[5] * a[6] - b[2] * a[9] + b[1] * a[12] + b[0] * a[18];
        res[19] = b[19] * a[0] + b[13] * a[1] - b[8] * a[3] + b[7] * a[4] + b[4] * a[7] - b[3] * a[8] + b[1] * a[13] + b[0] * a[19];
        res[20] = b[20] * a[0] + b[14] * a[1] - b[9] * a[3] + b[7] * a[5] + b[5] * a[7] - b[3] * a[9] + b[1] * a[14] + b[0] * a[20];
        res[21] = b[21] * a[0] + b[15] * a[1] - b[9] * a[4] + b[8] * a[5] + b[5] * a[8] - b[4] * a[9] + b[1] * a[15] + b[0] * a[21];
        res[22] = b[22] * a[0] + b[13] * a[2] - b[11] * a[3] + b[10] * a[4] + b[4] * a[10] - b[3] * a[11] + b[2] * a[13] + b[0] * a[22];
        res[23] = b[23] * a[0] + b[14] * a[2] - b[12] * a[3] + b[10] * a[5] + b[5] * a[10] - b[3] * a[12] + b[2] * a[14] + b[0] * a[23];
        res[24] = b[24] * a[0] + b[15] * a[2] - b[12] * a[4] + b[11] * a[5] + b[5] * a[11] - b[4] * a[12] + b[2] * a[15] + b[0] * a[24];
        res[25] = b[25] * a[0] + b[15] * a[3] - b[14] * a[4] + b[13] * a[5] + b[5] * a[13] - b[4] * a[14] + b[3] * a[15] + b[0] * a[25];
        res[26] = b[26] * a[0] + b[22] * a[1] - b[19] * a[2] + b[17] * a[3] - b[16] * a[4] + b[13] * a[6] - b[11] * a[7] + b[10] * a[8] + b[8] * a[10] - b[7] * a[11] + b[6] * a[13] + b[4] * a[16] - b[3] * a[17] + b[2] * a[19] - b[1] * a[22] + b[0] * a[26];
        res[27] = b[27] * a[0] + b[23] * a[1] - b[20] * a[2] + b[18] * a[3] - b[16] * a[5] + b[14] * a[6] - b[12] * a[7] + b[10] * a[9] + b[9] * a[10] - b[7] * a[12] + b[6] * a[14] + b[5] * a[16] - b[3] * a[18] + b[2] * a[20] - b[1] * a[23] + b[0] * a[27];
        res[28] = b[28] * a[0] + b[24] * a[1] - b[21] * a[2] + b[18] * a[4] - b[17] * a[5] + b[15] * a[6] - b[12] * a[8] + b[11] * a[9] + b[9] * a[11] - b[8] * a[12] + b[6] * a[15] + b[5] * a[17] - b[4] * a[18] + b[2] * a[21] - b[1] * a[24] + b[0] * a[28];
        res[29] = b[29] * a[0] + b[25] * a[1] - b[21] * a[3] + b[20] * a[4] - b[19] * a[5] + b[15] * a[7] - b[14] * a[8] + b[13] * a[9] + b[9] * a[13] - b[8] * a[14] + b[7] * a[15] + b[5] * a[19] - b[4] * a[20] + b[3] * a[21] - b[1] * a[25] + b[0] * a[29];
        res[30] = b[30] * a[0] + b[25] * a[2] - b[24] * a[3] + b[23] * a[4] - b[22] * a[5] + b[15] * a[10] - b[14] * a[11] + b[13] * a[12] + b[12] * a[13] - b[11] * a[14] + b[10] * a[15] + b[5] * a[22] - b[4] * a[23] + b[3] * a[24] - b[2] * a[25] + b[0] * a[30];
        res[31] = b[31] * a[0] + b[30] * a[1] - b[29] * a[2] + b[28] * a[3] - b[27] * a[4] + b[26] * a[5] + b[25] * a[6] - b[24] * a[7] + b[23] * a[8] - b[22] * a[9] + b[21] * a[10] - b[20] * a[11] + b[19] * a[12] + b[18] * a[13] - b[17] * a[14] + b[16] * a[15] + b[15] * a[16] - b[14] * a[17] + b[13] * a[18] + b[12] * a[19] - b[11] * a[20] + b[10] * a[21] - b[9] * a[22] + b[8] * a[23] - b[7] * a[24] + b[6] * a[25] + b[5] * a[26] - b[4] * a[27] + b[3] * a[28] - b[2] * a[29] + b[1] * a[30] + b[0] * a[31];
        return res;
    }

    /**
     * Add.
     *
     * Multivector addition
     *
     * @param a
     * @param b
     * @return a + b
     */
    private static double[] staticAdd(double[] a, double[] b) {
        double[] res = new double[a.length];
        res[0] = a[0] + b[0];
        res[1] = a[1] + b[1];
        res[2] = a[2] + b[2];
        res[3] = a[3] + b[3];
        res[4] = a[4] + b[4];
        res[5] = a[5] + b[5];
        res[6] = a[6] + b[6];
        res[7] = a[7] + b[7];
        res[8] = a[8] + b[8];
        res[9] = a[9] + b[9];
        res[10] = a[10] + b[10];
        res[11] = a[11] + b[11];
        res[12] = a[12] + b[12];
        res[13] = a[13] + b[13];
        res[14] = a[14] + b[14];
        res[15] = a[15] + b[15];
        res[16] = a[16] + b[16];
        res[17] = a[17] + b[17];
        res[18] = a[18] + b[18];
        res[19] = a[19] + b[19];
        res[20] = a[20] + b[20];
        res[21] = a[21] + b[21];
        res[22] = a[22] + b[22];
        res[23] = a[23] + b[23];
        res[24] = a[24] + b[24];
        res[25] = a[25] + b[25];
        res[26] = a[26] + b[26];
        res[27] = a[27] + b[27];
        res[28] = a[28] + b[28];
        res[29] = a[29] + b[29];
        res[30] = a[30] + b[30];
        res[31] = a[31] + b[31];
        return res;
    }

    @Override
    public SparseCGASymbolicMultivector toSymbolic() {
        return super.delegate;
    }

    @Override
    public iConstantsFactory<SparseCGANumericMultivector> constants() {
        return CGAExprGraphFactory.instance.constantsNumeric();
    }
}
