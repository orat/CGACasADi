package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorVectorDouble;
import util.cga.CGACayleyTableGeometricProduct;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iConstantsSymbolic;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import util.CayleyTable;
//import de.orat.math.sparsematrix.SparseDoubleColumnVector;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 *
 * Achtung: Es können Objekte mit und ohne sparsity erzeugt werden.
 */
public class SparseCGANumericMultivector implements iMultivectorNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector> {

    final DM dm;

    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    private MultivectorNumeric.Callback callback;

    public SparseCGANumericMultivector() {
        //this.dm = new DM(baseCayleyTable.getBladesCount(),1);
        dm = null;
    }

    public SparseCGANumericMultivector instance(double[] values) {
        return new SparseCGANumericMultivector(values);
    }

    protected SparseCGANumericMultivector(double[] values) {
        if (baseCayleyTable.getBladesCount() != values.length) {
            throw new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "
                + String.valueOf(values.length));
        }
        this.dm = CasADiUtil.toDM(values);
    }

    public SparseCGANumericMultivector instance(double[] nonzeros, int[] rows) {
        return new SparseCGANumericMultivector(nonzeros, rows);
    }

    protected SparseCGANumericMultivector(double[] nonzeros, int[] rows) {
        if (baseCayleyTable.getBladesCount() < nonzeros.length) {
            throw new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "
                + String.valueOf(nonzeros.length));
        }
        if (nonzeros.length != rows.length) {
            throw new IllegalArgumentException("Construction of CGA multivector failed because nonzeros.length != rows.length!");
        }
        this.dm = CasADiUtil.toDM(baseCayleyTable.getBladesCount(), nonzeros, rows);
    }

    public DM getDM() {
        return dm;
    }

    SparseCGANumericMultivector(DM dm) {
        this.dm = dm;
    }

    @Override
    public void init(MultivectorNumeric.Callback callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return dm.toString();
    }

    /**
     * Get a complete multivector as double[], inclusive structural 0 values.
     *
     * @return double[32] elements corresponding to the underlaying implementation specific coordindate
     * system.
     */
    @Override
    public SparseDoubleMatrix elements() {
        return CasADiUtil.elements(dm);
    }

    /*@Override
    public SparseCGANumericMultivector op(SparseCGANumericMultivector mv) {
        return new SparseCGANumericMultivector(op(elements(), mv.elements()));
    }
    
    @Override
    public SparseCGANumericMultivector add(SparseCGANumericMultivector mv) {
        return new SparseCGANumericMultivector(add(elements(), mv.elements()));
    }*/
    /**
     * Wedge.
     *
     * The outer product. (MEET)
     *
     * @param a
     * @param b
     * @return a ^ b
     */
    private static double[] op(double[] a, double[] b) {
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
    private static double[] add(double[] a, double[] b) {
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

    private static final CGAExprGraphFactory fac = CGAExprGraphFactory.instance;

    @Override
    public SparseCGASymbolicMultivector toSymbolic() {
        var nonZeros = new StdVectorVectorDouble(1, this.dm.nonzeros());
        var sx = new SX(this.dm.sparsity(), new SX(nonZeros));
        return SparseCGASymbolicMultivector.create(sx);
        // Potentially slower alternative:
        // return fac.createMultivectorSymbolic("", this.elements());
    }

    /**
     * <pre>
     * https://github.com/casadi/casadi/wiki/L_rf
     * Evaluates the expression numerically.
     * An error is raised when the expression contains symbols.
     * </pre>
     */
    public static SparseCGANumericMultivector fromSymbolic(SparseCGASymbolicMultivector sym) {
        var dm = SX.evalf(sym.getSX());
        return new SparseCGANumericMultivector(dm);
    }

    @Override
    public SparseCGANumericMultivector add(SparseCGANumericMultivector mv) {
        var thisSym = this.toSymbolic();
        var mvSym = mv.toSymbolic();
        var resSym = thisSym.add(mvSym);
        var resNum = fromSymbolic(resSym);
        return resNum;
    }

    // For optimal performance, override all default methods.

    public static void main(String[] args) {
        var first = fac.createMultivectorNumeric(fac.createScalar(46));
        System.out.println(first);

        var second = fac.createMultivectorNumeric(fac.createScalar(46));
        System.out.println(second);

        var res = first.gp(second);
        System.out.println(res);
    }

    @Override
    public SparseCGANumericMultivector gp(SparseCGANumericMultivector rhs) {
        var thisSym = this.toSymbolic();
        var mvSym = rhs.toSymbolic();
        var resSym = thisSym.gp(mvSym);
        var resNum = fromSymbolic(resSym);
        return resNum;
    }

    @Override
    public void init(MultivectorSymbolic.Callback callback) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MatrixSparsity getSparsity() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isZero() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CayleyTable getCayleyTable() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int grade() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int[] grades() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iConstantsSymbolic<SparseCGANumericMultivector> constants() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector gradeSelection(int grade) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector reverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector gpWithScalar(double s) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector undual() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector conjugate() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector negate14() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector scalarAbs() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector scalarAtan2(SparseCGANumericMultivector y) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector scalarSqrt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector exp() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector sqrt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector log() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector meet(SparseCGANumericMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector join(SparseCGANumericMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector inorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector normalizeBySquaredNorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector normalizeEvenElement() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector generalInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseCGANumericMultivector scalarInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
