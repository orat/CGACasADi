package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.api.SXColVec;
import de.dhbw.rahmlab.casadi.api.SXScalar;
import de.dhbw.rahmlab.casadi.api.Trigometry;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.SXElem;
import de.dhbw.rahmlab.casadi.impl.casadi.SxSubIndex;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorVectorDouble;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;
import util.cga.CGAOperatorMatrixUtils;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.cgacasadi.caching.annotation.api.GenerateCached;
import de.orat.math.cgacasadi.caching.annotation.api.Uncached;
import de.orat.math.cgacasadi.impl.gen.CachedSparseCGASymbolicMultivector;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;
import java.util.Objects;
import util.CayleyTable;
import util.cga.CGACayleyTable;
//import util.cga.CGACayleyTableOuterProduct;
import util.cga.CGAOperations;

/**
 * <pre>
 * abstract to prevent inadvertent instantiation.
 * Doc for @GenerateCached:
 *    https://github.com/orat/CGACasADi/blob/master/CGACasADi_SymbolicMultivectorCachingProcessor/README.md
 * </pre>
 */
@GenerateCached(warnFailedToCache = false, warnUncached = false)
public abstract class SparseCGASymbolicMultivector implements iMultivectorSymbolic<SparseCGASymbolicMultivector>, ISparseCGASymbolicMultivector {

    private MultivectorSymbolic.Callback callback;
    private final String name;

    private final static CGACayleyTableGeometricProduct baseCayleyTable
        = CGACayleyTableGeometricProduct.instance();
    private final static CGAOperatorMatrixUtils cgaOperatorMatrixUtils
        = new CGAOperatorMatrixUtils(baseCayleyTable);

    private final static CGAExprGraphFactory fac = CGAExprGraphFactory.instance;

    // a multivector is represented by a sparse column vector
    private final SX sx;

    //======================================================
    // Constructors and static creators.
    // -> Constructors must only used within subclasses.
    // -> creators return cached instances.
    //======================================================
    /**
     * Constructors must only used within subclasses.
     */
    @Deprecated
    protected SparseCGASymbolicMultivector(SparseCGASymbolicMultivector other) {
        this.name = other.getName();
        this.sx = other.getSX();
    }

    /**
     * Constructors must only used within subclasses.
     */
    @Deprecated
    protected SparseCGASymbolicMultivector(String name, SX sx) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(sx);
        this.name = name;
        this.sx = sx;
    }

    public static SparseCGASymbolicMultivector create(SparseCGASymbolicMultivector other) {
        return new CachedSparseCGASymbolicMultivector(other);
    }

    public static SparseCGASymbolicMultivector create(String name, SparseDoubleMatrix vector) {
        StdVectorDouble vecDouble = new StdVectorDouble(vector.nonzeros());
        SX sx = new SX(CasADiUtil.toCasADiSparsity(vector.getSparsity()),
            new SX(new StdVectorVectorDouble(new StdVectorDouble[]{vecDouble})));
        return new CachedSparseCGASymbolicMultivector(name, sx);
    }

    public static PurelySymbolicCachedSparseCGASymbolicMultivector create(String name, int[] grades) {
        return new PurelySymbolicCachedSparseCGASymbolicMultivector(name, grades);
    }

    /**
     * <pre>
     * Creates a k-Vector.
     * </pre>
     *
     * @param name
     * @param grade
     */
    public static PurelySymbolicCachedSparseCGASymbolicMultivector create(String name, int grade) {
        return new PurelySymbolicCachedSparseCGASymbolicMultivector(name, grade);
    }

    public static PurelySymbolicCachedSparseCGASymbolicMultivector create(String name, ColumnVectorSparsity sparsity) {
        return new PurelySymbolicCachedSparseCGASymbolicMultivector(name, sparsity);
    }

    public static PurelySymbolicCachedSparseCGASymbolicMultivector create(String name) {
        return new PurelySymbolicCachedSparseCGASymbolicMultivector(name);
    }

    public static SparseCGASymbolicMultivector create(DM dm) {
        var nonZeros = new StdVectorVectorDouble(1, dm.nonzeros());
        var sx = new SX(dm.sparsity(), new SX(nonZeros));
        return new CachedSparseCGASymbolicMultivector("", sx);
    }

    public static SparseCGASymbolicMultivector create(SX sx) {
        return new CachedSparseCGASymbolicMultivector("", sx);
    }

    public static SparseCGASymbolicMultivector create(String name, SX sx) {
        return new CachedSparseCGASymbolicMultivector(name, sx);
    }

    //======================================================
    // Other methods
    //======================================================
    @Override
    public void init(MultivectorSymbolic.Callback callback) {
        this.callback = callback;
    }

    @Override
    public CayleyTable getCayleyTable() {
        return baseCayleyTable;
    }

    @Override
    public int grade() {
        return this.getSparsity().getGrade();
    }

    @Override
    public int[] grades() {
        return this.getSparsity().getGrades();
    }

    @Override
    public String toString() {
        SparseStringMatrix stringMatrix = CasADiUtil.toStringMatrix(sx);
        return stringMatrix.toString(true);
    }

    @Override
    public CGAMultivectorSparsity/*ColumnVectorSparsity*/ getSparsity() {
        return CasADiUtil.toCGAMultivectorSparsity(sx.sparsity());
    }

    public SX getSX() {
        return this.sx;
    }

    /**
     * Get SX representation of a blade.
     *
     * @param bladeName pseudoscalar_name of the blade
     * @return null, if blade is structurel null else the SX representing the blade
     * @throws IllegalArgumentException if the given blade pseudoscalar_name does not exist in the
     * cayley-table
     */
    SX getSX(String bladeName) {
        int row = baseCayleyTable.getBasisBladeIndex(bladeName);
        if (row == -1) {
            throw new IllegalArgumentException("The given bladeName ="
                + bladeName + " does not exist in the cayley table!");
        }
        //if (sparsity.isNonZero(row,0)) return sx1.at(row, 0);
        if (sx.sparsity().has_nz(row, 0)) {
            return sx.at(row, 0);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getBladesCount() {
        return baseCayleyTable.getBladesCount();
    }

    public boolean isGeneralEven() {
        return getSparsity().isGeneralEven();
    }
    public boolean isEven(){
        return getSparsity().isEven();
    }

    public boolean isBivector(){
        return (grade() == 2);
    }
    
    /**
     * Is structural zero.
     * 
     * @return true if the multivector is structurel zero.
     */
    public boolean isZero() {
        //TODO unklar ob das so korrekt ist, hier muss auf struktural zero getestet werden
        // nicht erst auf zero zur Laufzeit
        return sx.is_zero();
    }

    private static final CGAConstantsSymbolic CONSTANTS = CGAConstantsSymbolic.instance;

    @Override
    public CGAConstantsSymbolic constants() {
        return CONSTANTS;
    }

    public CGAExprGraphFactory fac() {
        return CGAExprGraphFactory.instance;
    }

    //======================================================
    // Operators
    //======================================================
    @Override
    public SparseCGASymbolicMultivector gradeSelection(int grade) {
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createGradeSelectionOperatorMatrix(baseCayleyTable, grade);
        SX gradeSelectionMatrix = CasADiUtil.toSX(m); // bestimmt sparsity
        return create(SX.mtimes(gradeSelectionMatrix, sx));
    }

    /**
     * Generic GA reverse function implementation based on matrix calculations.
     *
     * @return reverse of a multivector
     */
    @Override
    public SparseCGASymbolicMultivector reverse() {
        SparseDoubleMatrix revm = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        SX result = SX.mtimes(CasADiUtil.toSX(revm), sx);
        return create(result);
    }

    @Override
    public SparseCGASymbolicMultivector gp(SparseCGASymbolicMultivector b) {
        //System.out.println("---gp---");
        SX opm = CasADiUtil.toSXProductMatrix(b, CGACayleyTableGeometricProduct.instance());
        //System.out.println("--- end of gp matrix creation ---");
        SX result = SX.mtimes(opm.T(), this.getSX());
        return create(result);
    }

    /**
     * <pre>
     * Anmerkung von Fabian:
     * - Cache ich erst mal nicht.
     * - Würde ich sogar eventuell entfernen.
     * - Sparsities werden beim Caching beachtet. Falls CasADi die Sparsities beachtet, sollte es zur Laufzeit
     *      keinen Unterschied geben zwischen gp mit einem Scalar Multivector und gpWithScalar.
     *      Man könnte dann einfach gp verwenden.
     * - Falls es doch einen Unterschied gibt, Vorschlag:
     *      - In gp (ohne "withScalar") ein if-else machen, welches prüft, ob b ein Scalar ist.
     *      - Dann den Code aus gpWithScalar rein kopieren mit folgender Anpassung:
     *      - getScalarMultiplicationOperatorMatrix anstatt dem double eine 1x1 symbolic SX Variable übergeben.
     *          Entsprechend darf auch keine SparseDoubleMatrix zurück geliefert werden.
     * - Grund:
     *      - Wird momentan nur intern an wenigen Stellen verwendet.
     *      - Funktioniert nur mit Scalar rechts.
     *      - Numerische Double Parameter zu cachen führt schnell zu einer Explosion an gecached'ten Functions.
     *      - Bei den Doubles muss man eine Genauigkeit festlegen, wann sie als gleich gelten sollen.
     *      - Ich vermute, dass es keinen großen Geschwindigkeitsvorteil gibt gegenüber der Variante mit einem
     *          symbolischen Scalar.
     * </pre>
     */
    @Override
    @Uncached
    public SparseCGASymbolicMultivector gpWithScalar(double s) {
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getScalarMultiplicationOperatorMatrix(s);
        SX result = SX.mtimes(CasADiUtil.toSX(m), sx);
        return create(result);
    }

    /**
     * Involute.
     *
     * Main involution - grade inversion<p>
     *
     * swapping the parity of each grade<p>
     *
     * @return involution/grade inversion
     */
    @Override
    public SparseCGASymbolicMultivector gradeInversion() {
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createInvolutionOperatorMatrix(baseCayleyTable);
        return create(SX.mtimes(CasADiUtil.toSX(m), sx));
    }

    /**
     * Dual.
     *
     * Poincare duality operator based on matrix based implementation of left contraction.
     *
     * @param a
     * @return !a
     */
    /*
    public SparseCGASymbolicMultivector dual() {
        SX lcm = CasADiUtil.toSXProductMatrix(this,
            CGACayleyTableLeftContractionProduct.instance());
        return create(SX.mtimes(lcm,
            (inversePseudoscalar()).getSX()));
    }
     */
    //TODO
    // undual zu implementieren muss ich erst gp() implementieren damit ich createGPFuncition() zur Verfügung habe
    /**
     *
     * @return undual of a multivector
     */
    /*private static CGASymbolicFunction createUndualFunction(){
        SX sxarg = SX.sym("mv",baseCayleyTable.getBladesCount());
        SparseDoubleMatrix revm = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        SX sxres = SX.mtimes(CasADiUtil.toSX(revm), sxarg);
        return new CGASymbolicFunction("undual",
                Collections.singletonList( create(sxarg)),
                Collections.singletonList( create(sxres)));
    }*/
    /**
     * Undual cga specific implementation based on dual and fix sign changed.
     *
     * @return undual function
     */
    @Override
    public SparseCGASymbolicMultivector undual() {
        //return gp(exprGraphFac.createPseudoscalar()).gpWithScalar(-1d); // -1 wird gebraucht
        return dual().gpWithScalar(-1d);
    }

    /**
     * Conjugate.
     *
     * Clifford Conjugation
     *
     * @param a
     * @return a.Conjugate()
     */
    @Override
    public SparseCGASymbolicMultivector conjugate() {
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getConjugationOperatorMatrix();
        SX result = SX.mtimes(CasADiUtil.toSX(m), sx);
        return create(result);
    }

    /*@Override
    public SparseCGASymbolicMultivector op(SparseCGASymbolicMultivector b){
        //TODO mit transponieren und vertauschen von a und b wirds völlig falsch
        // so wie es jetzt ist stimmen nur die ersten 5 Elemente
        System.out.println("---op---");
        SX gpm = CasADiUtil.toSXProductMatrix( this, CGACayleyTableOuterProduct.instance());
        System.out.println("--- end of op matrix creation ---");
        SX result = SX.mtimes(gpm, ( b).getSX());
        return create(result);
    }*/
    /**
     * Scalar product.
     *
     * TODO alternativ via gradeSelection() implementieren?
     *
     * @param rhs
     * @return scalar product of this with a 'rhs'
     */
    @Override
    public SparseCGASymbolicMultivector scp(SparseCGASymbolicMultivector rhs) {
        // eventuell sollte ich ip() explizit implementieren und hier verwenden,
        // damit scp() unabhängig von der Reihenfolge der Argumente wird
        // return ip(x, LEFT_CONTRACTION).scalarPart();
        SX sxres = (lc(rhs)).getSX().at(0);
        CGAMultivectorSparsity scalarSparsity = new CGAMultivectorSparsity(new int[]{0});
        SX result = new SX(CasADiUtil.toCasADiSparsity(scalarSparsity));
        result.assign(sxres);
        return create(sxres);
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
    @Override
    public SparseCGASymbolicMultivector add(SparseCGASymbolicMultivector b) {
        //System.out.println("sparsity(a)="+sx.sparsity().toString(true));
        //System.out.println("sparsity(b)="+( b).getSX().sparsity().toString(true));
        SX result = SX.plus(sx, b.getSX());
        //System.out.println("sparsity(add)="+result.sparsity().toString(true));
        return create(result);
    }

    /**
     * Multivector subtraction.
     *
     * @param a
     * @param b
     * @return a - b
     */
    @Override
    public SparseCGASymbolicMultivector sub(SparseCGASymbolicMultivector b) {
        SX result = SX.minus(sx, b.getSX());
        return create(result);
    }

    /**
     * Negates the signs of the vector and 4-vector parts of an multivector.
     *
     * @return multivector with changed signs for vector and 4-vector parts
     */
    @Override
    public SparseCGASymbolicMultivector negate14() {
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createNegate14MultiplicationMatrix(baseCayleyTable);
        return create(SX.mtimes(CasADiUtil.toSX(m), sx));
    }

    @Override
    public SparseCGASymbolicMultivector scalarAbs() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.abs(sx));
    }

    @Override
    public SparseCGASymbolicMultivector scalarAtan2(SparseCGASymbolicMultivector y) {
        if (!isScalar()) {
            throw new IllegalArgumentException("The argument x of atan2(x,y) is no scalar!");
        }
        if (!y.isScalar()) {
            throw new IllegalArgumentException("The argument y of atan2(x,y) is no scalar!");
        }
        //throw new UnsupportedOperationException("Not supported yet.");
        // not yet tested
        SX result = SX.atan2(y.getSX(), sx);
        return create(result);
    }

    @Override
    public SparseCGASymbolicMultivector scalarSqrt() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.sqrt(sx));
    }

    
    // new scalar functions
    
    public SparseCGASymbolicMultivector scalarSign() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.sign(sx));
    }
    public SparseCGASymbolicMultivector scalarSin() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.sin(sx));
    }
    public SparseCGASymbolicMultivector scalarCos() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.cos(sx));
    }
    public SparseCGASymbolicMultivector scalarTan() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.tan(sx));
    }
    public SparseCGASymbolicMultivector scalarAtan() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.atan(sx));
    }
    public SparseCGASymbolicMultivector scalarAsin() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.asin(sx));
    }
    public SparseCGASymbolicMultivector scalarAcos() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return create(SX.acos(sx));
    }
    
    // non linear operators/functions
    // [8] M Roelfs and S De Keninck. 2021.
    // Graded Symmetry Groups: Plane and Simple. arXiv:2107.03771 [math-ph]
    // https://arxiv.org/pdf/2107.03771
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // exponential of a bivector only for CGA (R41)
    @Override
    public SparseCGASymbolicMultivector exp() {
        if (!isBivector()){
            throw new IllegalArgumentException("exp() defined for bivectors only!");
        }
        
        SXColVec B = new SXColVec(sx, CGACayleyTable.getBivectorIndizes());

        // var S = -B[0]*B[0]-B[1]*B[1]-B[2]*B[2]+B[3]*B[3]-B[4]*B[4]-B[5]*B[5]+B[6]*B[6]-B[7]*B[7]+B[8]*B[8]+B[9]*B[9];
        SXScalar S = SXScalar.sumSq(B, new int[]{3,6,8,9}).sub(SXScalar.sumSq(B, new int[]{0,1,2,4,5,7}));

        // 2*(B[4]*B[9]-B[5]*B[8]+B[6]*B[7]), //e2345
        SXScalar T1 = SXScalar.sumProd(B, new int[]{4,6}, new int[]{9,7}).
            sub(B.get(5).mul(B.get(8))).muls(2d);
        // 2*(B[1]*B[9]-B[2]*B[8]+B[3]*B[7]), //e1345
        //SXScalar T2 = SXScalar.sumProd(B, new int[]{1,3}, new int[]{9,7}).
        SXScalar T2 = B.get(1).mul(B.get(9)).add(B.get(3).mul(B.get(7))).
            sub(B.get(2).mul(B.get(8))).muls(2d);
        // 2*(B[0]*B[9]-B[2]*B[6]+B[3]*B[5]), //e1245
        SXScalar T3 = SXScalar.sumProd(B, new int[]{0,3}, new int[]{9,5}).
            sub(B.get(2).mul(B.get(6))).muls(2d);
        // 2*(B[0]*B[8]-B[1]*B[6]+B[3]*B[4]), //e1235
        SXScalar T4 = SXScalar.sumProd(B, new int[]{0,3}, new int[]{8,4}).
            sub(B.get(1).mul(B.get(6))).muls(2d);
        // 2*(B[0]*B[7]-B[1]*B[5]+B[2]*B[4])  //e1234
        SXScalar T5 = SXScalar.sumProd(B, new int[]{0,2}, new int[]{7,4}). 
            sub(B.get(1).mul(B.get(5))).muls(2d);
        
        // Calculate the norms of the invariants
        // var Tsq = -T1*T1-T2*T2-T3*T3-T4*T4+T5*T5;
        SXScalar Tsq = T1.sq().negate().sub(T2.sq()).sub(T3.sq()).sub(T4.sq()).add(T5.sq());
        // var norm = sqrt(S*S - Tsq), sc = -0.5/norm, lambdap = 0.5*S+0.5*norm;
        SXScalar norm = S.sq().sub(Tsq).sqrt();
        SXScalar sc = (new SXScalar(-0.5)).div(norm);
        SXScalar lambdap = (new SXScalar(0.5)).mul(S).add(new SXScalar(0.5).mul(norm));
        // var [lp, lm] = [sqrt(abs(lambdap)), sqrt(-0.5*S+0.5*norm)]
        SXScalar lp = lambdap.abs().sqrt();
        SXScalar lm = (new SXScalar(-0.5)).mul(S).add((new SXScalar(0.5).mul(norm))).sqrt();
        // The associated trig (depending on sign lambdap)
        // var [cp, sp] = lambdap>0?[cosh(lp), sinh(lp)/lp]:lambdap<0?[cos(lp), sin(lp)/lp]:[1,1]
        SXScalar[] temp = lambdap.lt(0d,
            new SXScalar[]{lp.cos(), lp.sin().div(lp)}, 
            new SXScalar[]{new SXScalar(1d), new SXScalar(1d)});
        SXScalar[] cp_sp = lambdap.gt(0d, new SXScalar[]{lp.cosh(), lp.sinh().div(lp)}, temp);
        SXScalar cp = cp_sp[0];
        SXScalar sp = cp_sp[1];
        // var [cm, sm] = [cos(lm), lm==0?1:sin(lm)/lm]
        SXScalar cm = lm.cos();
        SXScalar sm = lm.eq(0d, new SXScalar(1d), lm.sin().div(lm));
        // Calculate the mixing factors alpha and beta_i.
        //var [cmsp, cpsm, spsm] = [cm*sp,cp*sm,sp*sm/2], D = cmsp-cpsm, E = sc*D;
        SXScalar cmsp = cm.mul(sp);
        SXScalar cpsm = cp.mul(sm);
        SXScalar spsm = sp.mul(sm).div(2d);
        SXScalar D = cmsp.sub(cpsm);
        SXScalar E = sc.mul(D);
            
        // var [alpha,beta1,beta2,beta3,beta4,beta5] = [ D*(0.5-sc*S) + cpsm, E*T1, -E*T2, E*T3, -E*T4, -E*T5 ]
        SXScalar alpha = D.mul(new SXScalar(0.5).sub(sc.mul(S))).add(cpsm);
        SXScalar beta1 = E.mul(T1);
        SXScalar beta2 = E.mul(T2).negate();
        SXScalar beta3 = E.mul(T3);
        SXScalar beta4 = E.mul(T4).negate();
        SXScalar beta5 = E.mul(T5).negate();
        
        // create SX with sparsity corresponding to a rotor (even element)
        SXElem[] generalRotorValues = new SXElem[]{
            //cp*cm,
            cp.mul(cm).sx.scalar(),
            //new SXScalar(1d).sx.scalar(),
            //cm.sx.scalar(),
            //Tsq.sx.scalar(),
            
            //(B[0]*alpha+B[7]*beta5-B[8]*beta4+B[9]*beta3),
            B.get(0).mul(alpha).add(B.get(7).mul(beta5)).sub(B.get(8).mul(beta4)).
                add(B.get(9).mul(beta3)).sx.scalar(),
            //(B[1]*alpha-B[5]*beta5+B[6]*beta4-B[9]*beta2),
            B.get(1).mul(alpha).sub(B.get(5).mul(beta5)).add(B.get(6).mul(beta4)).
                sub(B.get(9).mul(beta2)).sx.scalar(),
            //(B[2]*alpha+B[4]*beta5-B[6]*beta3+B[8]*beta2),
            B.get(2).mul(alpha).add(B.get(4).mul(beta5)).sub(B.get(6).mul(beta3)).
                add(B.get(8).mul(beta2)).sx.scalar(),
            //(B[3]*alpha+B[4]*beta4-B[5]*beta3+B[7]*beta2),
            B.get(3).mul(alpha).add(B.get(4).mul(beta4)).sub(B.get(5).mul(beta3)).
                add(B.get(7).mul(beta2)).sx.scalar(),
            //(B[4]*alpha+B[2]*beta5-B[3]*beta4+B[9]*beta1),
            B.get(4).mul(alpha).add(B.get(2).mul(beta5)).sub(B.get(3).mul(beta4)).
                add(B.get(9).mul(beta1)).sx.scalar(),
            //(B[5]*alpha-B[1]*beta5+B[3]*beta3-B[8]*beta1),
            B.get(5).mul(alpha).sub(B.get(1).mul(beta5)).add(B.get(3).mul(beta3)). 
                sub(B.get(8).mul(beta1)).sx.scalar(),
            //(B[6]*alpha-B[1]*beta4+B[2]*beta3-B[7]*beta1),
            B.get(6).mul(alpha).sub(B.get(1).mul(beta4)).add(B.get(2).mul(beta3)). 
                sub(B.get(7).mul(beta1)).sx.scalar(),
            //(B[7]*alpha+B[0]*beta5-B[3]*beta2+B[6]*beta1),
            B.get(7).mul(alpha).add(B.get(0).mul(beta5)).sub(B.get(3).mul(beta2)). 
                add(B.get(6).mul(beta1)).sx.scalar(),
            //(B[8]*alpha+B[0]*beta4-B[2]*beta2+B[5]*beta1),
            B.get(8).mul(alpha).add(B.get(0).mul(beta4)).sub(B.get(2).mul(beta2)).  
                add(B.get(5).mul(beta1)).sx.scalar(),
            //(B[9]*alpha-B[0]*beta3+B[1]*beta2-B[4]*beta1),
            B.get(9).mul(alpha).sub(B.get(0).mul(beta3)).add(B.get(1).mul(beta2)).  
                sub(B.get(4).mul(beta1)).sx.scalar(),
            //spsm*T5, spsm*T4, spsm*T3, spsm*T2, spsm*T1
            spsm.mul(T5).sx.scalar(), spsm.mul(T4).sx.scalar(), spsm.mul(T3).sx.scalar(), 
            spsm.mul(T2).sx.scalar(), spsm.mul(T1).sx.scalar()
        };
        return create(new SXColVec(getCayleyTable().getBladesCount(), 
            generalRotorValues, CGACayleyTable.getEvenIndizes()).sx);
    }

   
    /**
     * CGA R4,1. e1*e1 = e2*e2 = e3*e3 = e4*4 = 1, e5*e5 = -1<p>
     * 
     * Normalize an even element (a general rotor R with 16 coefficients)
     * X = [1,e12,e13,e14,e15,e23,e24,e25,e34,e35,e45,e1234,e1235,e1245,e1345,e2345]<p>
     *
     * Normalization, Square Roots, and the Exponential and Logarithmic Maps in<br>
     * Geometric Algebras of Less than 6D<br>
     * S. de. Keninck, M. Roelfs, 2022
     */
    public SparseCGASymbolicMultivector normalizeRotor() {
        if (!isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }
        
        int[] evenIndizes = CGACayleyTable.getEvenIndizes();
        SXColVec R = new SXColVec(sx, evenIndizes);

        // var S = R[0]*R[0]-R[10]*R[10]+R[11]*R[11]-R[12]*R[12]-R[13]*R[13]-R[14]*R[14]-R[15]*R[15]+R[1]*R[1]
        // +R[2]*R[2]+R[3]*R[3]-R[4]*R[4]+R[5]*R[5]+R[6]*R[6]-R[7]*R[7]+R[8]*R[8]-R[9]*R[9];
        SXScalar S = SXScalar.sumSq(R, new int[]{0,11,1,2,3,5,6,8}).
            sub(SXScalar.sumSq(R, new int[]{10,12,13,14,15,4,7,9}));

        // var T1 = 2*(R[0]*R[11]-R[10]*R[12]+R[13]*R[9]-R[14]*R[7]+R[15]*R[4]-R[1]*R[8]+R[2]*R[6]-R[3]*R[5]);
        SXScalar T1 = SXScalar.sumProd(R, new int[]{0,13,15,2}, new int[]{11,9,4,6}).
            sub(SXScalar.sumProd(R, new int[]{10,14,1,3}, new int[]{12,7,8,5})).muls(2d);

        // var T2 = 2*(R[0]*R[12]-R[10]*R[11]+R[13]*R[8]-R[14]*R[6]+R[15]*R[3]-R[1]*R[9]+R[2]*R[7]-R[4]*R[5]);
        SXScalar T2 = SXScalar.sumProd(R, new int[]{0,13,15,2}, new int[]{12,8,3,7}).
            sub(SXScalar.sumProd(R, new int[]{10,14,1,4}, new int[]{11,6,9,5})).muls(2d);

        //var T3 = 2*(R[0]*R[13]-R[10]*R[1]+R[11]*R[9]-R[12]*R[8]+R[14]*R[5]-R[15]*R[2]+R[3]*R[7]-R[4]*R[6]);
        SXScalar T3 = SXScalar.sumProd(R, new int[]{0,11,14,3}, new int[]{13,9,5,7}).
            sub(SXScalar.sumProd(R, new int[]{10,12,15,4}, new int[]{1,8,2,6})).muls(2d);

        //var T4 = 2*(R[0]*R[14]-R[10]*R[2]-R[11]*R[7]+R[12]*R[6]-R[13]*R[5]+R[15]*R[1]+R[3]*R[9]-R[4]*R[8]);
        SXScalar T4 = SXScalar.sumProd(R, new int[]{0,12,15,3}, new int[]{14,6,1,9}).
            sub(SXScalar.sumProd(R, new int[]{10,11,13,4}, new int[]{2,7,5,8})).muls(2d);

        //var T5 = 2*(R[0]*R[15]-R[10]*R[5]+R[11]*R[4]-R[12]*R[3]+R[13]*R[2]-R[14]*R[1]+R[6]*R[9]-R[7]*R[8]);
        SXScalar T5 = SXScalar.sumProd(R, new int[]{0,11,13,6}, new int[]{15,4,2,9}).
            sub(SXScalar.sumProd(R, new int[]{10,12,14,7}, new int[]{5,3,1,8})).muls(2d);

        //var TT = -T1*T1+T2*T2+T3*T3+T4*T4+T5*T5;
        SXScalar TT = T1.sq().negate().add(T2.sq()).add(T3.sq()).add(T4.sq()).add(T5.sq());

        //var N = ((S*S+TT)**0.5+S)**0.5, N2 = N*N;
        SXScalar N = S.sq().add(TT).pow(0.5).add(S).pow(0.5);
        SXScalar N2 = N.sq();

        //var M = 2**0.5*N/(N2*N2+TT);
        SXScalar M = new SXScalar(Math.pow(2d, 0.5)).mul(N).div(N2.sq().add(TT));
        //var A = N2*M, [B1,B2,B3,B4,B5] = [-T1*M,-T2*M,-T3*M,-T4*M,-T5*M];
        SXScalar A = N2.mul(M);
        //TODO
        // neue Methode mit function als argument um negate().mul(M) übergeben zu können
        // damit die nachfolgenden Zeilen in eine zusammengezogen werden können
        SXScalar B1 = T1.negate().mul(M);
        SXScalar B2 = T2.negate().mul(M);
        SXScalar B3 = T3.negate().mul(M);
        SXScalar B4 = T4.negate().mul(M);
        SXScalar B5 = T5.negate().mul(M);
         
        
        SXElem[] values = new SXElem[]{
            // A*R[0] + B1*R[11] - B2*R[12] - B3*R[13] - B4*R[14] - B5*R[15],
            A.mul(R.get(0)).add(B1.mul(R.get(11))).sub(B2.mul(R.get(12))).
              sub(B3.mul(R.get(13))).sub(B4.mul(R.get(14))).sub(B5.mul(R.get(15))).sx.scalar(),
            // A*R[1] - B1*R[8] + B2*R[9] + B3*R[10] - B4*R[15] + B5*R[14],
            A.mul(R.get(1)).sub(B1.mul(R.get(8))).add(B2.mul(R.get(9))).
                add(B3.mul(R.get(10))).sub(B4.mul(R.get(15))).add(B5.mul(R.get(14))).sx.scalar(),
            // A*R[2] + B1*R[6] - B2*R[7] + B3*R[15] + B4*R[10] - B5*R[13],
            A.mul(R.get(2)).add(B1.mul(R.get(6))).sub(B2.mul(R.get(7))).
                add(B3.mul(R.get(15))).add(B4.mul(R.get(10))).sub(B5.mul(R.get(13))).sx.scalar(),
            //A*R[3] - B1*R[5] - B2*R[15] - B3*R[7] - B4*R[9] + B5*R[12],
            A.mul(R.get(3)).sub(B1.mul(R.get(5))).sub(B2.mul(R.get(15))).
                sub(B3.mul(R.get(7))).sub(B4.mul(R.get(9))).add(B5.mul(R.get(12))).sx.scalar(),
            //A*R[4] - B1*R[15] - B2*R[5] - B3*R[6] - B4*R[8] + B5*R[11],
SXScalar.sumProd(new SXScalar[]{A,B5}, R, new int[]{4,11}).
    sub(SXScalar.sumProd(new SXScalar[]{B1, B2, B3, B4}, R, new int[]{15,5,6,8})).sx.scalar(),
            //A*R[5] - B1*R[3] + B2*R[4] - B3*R[14] + B4*R[13] + B5*R[10],
SXScalar.sumProd(new SXScalar[]{A,B2,B4,B5}, R, new int[]{5,4,13,10}).
    sub(SXScalar.sumProd(new SXScalar[]{B1,B3}, R, new int[]{3,14})).sx.scalar(),
            //A*R[6] + B1*R[2] + B2*R[14] + B3*R[4] - B4*R[12] - B5*R[9],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B3}, R, new int[]{6,2,14,4}).
    sub(SXScalar.sumProd(new SXScalar[]{B4,B5}, R, new int[]{12,9})).sx.scalar(),
            //A*R[7] + B1*R[14] + B2*R[2] + B3*R[3] - B4*R[11] - B5*R[8],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B3}, R, new int[]{7,14,2,3}).
    sub(SXScalar.sumProd(new SXScalar[]{B4,B5}, R, new int[]{11,8})).sx.scalar(),
            //A*R[8] - B1*R[1] - B2*R[13] + B3*R[12] + B4*R[4] + B5*R[7],
SXScalar.sumProd(new SXScalar[]{A,B3,B4,B5}, R, new int[]{8,12,4,7}).
    sub(SXScalar.sumProd(new SXScalar[]{B1,B2}, R, new int[]{1,13})).sx.scalar(),
            //A*R[9] - B1*R[13] - B2*R[1] + B3*R[11] + B4*R[3] + B5*R[6],
SXScalar.sumProd(new SXScalar[]{A,B3,B4,B5}, R, new int[]{9,11,3,6}).
    sub(SXScalar.sumProd(new SXScalar[]{B1,B2}, R, new int[]{13,1})).sx.scalar(),
            //A*R[10] + B1*R[12] - B2*R[11] - B3*R[1] - B4*R[2] - B5*R[5],
SXScalar.sumProd(new SXScalar[]{A,B1}, R, new int[]{10,12}).
    sub(SXScalar.sumProd(new SXScalar[]{B2,B3,B4,B5}, R, new int[]{11,1,2,5})).sx.scalar(),
            //A*R[11] + B1*R[0] + B2*R[10] - B3*R[9] + B4*R[7] - B5*R[4],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B4}, R, new int[]{11,0,10,7}).
    sub(SXScalar.sumProd(new SXScalar[]{B3,B5}, R, new int[]{9,4})).sx.scalar(),
            //A*R[12] + B1*R[10] + B2*R[0] - B3*R[8] + B4*R[6] - B5*R[3],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B4}, R, new int[]{12,10,0,6}).
    sub(SXScalar.sumProd(new SXScalar[]{B3,B5}, R, new int[]{8,3})).sx.scalar(),
            //A*R[13] - B1*R[9] + B2*R[8] + B3*R[0] - B4*R[5] + B5*R[2],
SXScalar.sumProd(new SXScalar[]{A,B2,B3,B5}, R, new int[]{13,8,0,2}).
    sub(SXScalar.sumProd(new SXScalar[]{B1,B4}, R, new int[]{9,5})).sx.scalar(),
            //A*R[14] + B1*R[7] - B2*R[6] + B3*R[5] + B4*R[0] - B5*R[1],
SXScalar.sumProd(new SXScalar[]{A,B1,B3,B4}, R, new int[]{14,7,5,0}).
    sub(SXScalar.sumProd(new SXScalar[]{B2,B5}, R, new int[]{6,1})).sx.scalar(),
            //A*R[15] - B1*R[4] + B2*R[3] - B3*R[2] + B4*R[1] + B5*R[0]
SXScalar.sumProd(new SXScalar[]{A,B2,B4,B5}, R, new int[]{15,3,1,0}).
    sub(SXScalar.sumProd(new SXScalar[]{B1,B3}, R, new int[]{4,2})).sx.scalar()
        };
        
        // create SX with sparsity corresponding to a rotor (even element)
        return create(new SXColVec(getCayleyTable().getBladesCount(), values, evenIndizes).sx);
    }

    
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    @Override
    public SparseCGASymbolicMultivector sqrt() {
        if (isEven()) {
            if (this.isScalar()){
                return scalarSqrt();
            } else {
                return add(CONSTANTS.one()).normalizeRotor();
            }
        }
        throw new RuntimeException("sqrt() not yet implemented for non even elements. Should be implemented in the default method of the interface with a generic version.");
    }

    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // log of a normalized rotor
    @Override
    public SparseCGASymbolicMultivector log() {
      
        if (!isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }
        
        //int[] indizes = CGACayleyTable.getEvenIndizes();
        
        SXColVec R = new SXColVec(sx, CGACayleyTable.getEvenIndizes());
        
        SXScalar S = R.get(0).sq().add(R.get(11).sq()).sub(R.get(12).sq()). 
                     sub(R.get(13).sq()).sub(R.get(14).sq()).sub(R.get(15).sq()).sub(new SXScalar(1d));
  
        SXScalar T1 = (new SXScalar(2d)).mul(R.get(0)).mul(R.get(15));   //e2345
        SXScalar T2 = R.get(0).mul(R.get(14)).muls(2d);   //e1345
        SXScalar T3 = R.get(0).mul(R.get(13)).muls(2d);   //e1245
        SXScalar T4 = R.get(0).mul(R.get(12)).muls(2d);   //e1235
        SXScalar T5 = R.get(0).mul(R.get(11)).muls(2d);    //e1234
  
        //-T1*T1-T2*T2-T3*T3-T4*T4+T5*T5;
        SXScalar Tsq      = T1.sq().negate().sub(T2.sq()).sub(T3.sq()).sub(T4.sq()).add(T5.sq()); 
        SXScalar norm     = S.sq().sub(Tsq).sqrt(); //Math.sqrt(S*S - Tsq);
        //if (norm==0 && S==0)   // at most a single translation
        //    return bivector(new double[]{R[1], R[2], R[3], R[4], R[5], R[6], R[7], R[8], R[9], R[10]});
        SXScalar[] Bif = new SXScalar[]{R.get(1), R.get(2), R.get(3), R.get(4), 
            R.get(5), R.get(6), R.get(7), R.get(8), R.get(9), R.get(10)};
       
        SXScalar lambdap  = new SXScalar(0.5d).mul(S).add(new SXScalar(0.5).mul(norm));
        // lm is always a rotation, lp can be boost, translation, rotation
        //double lp = Math.sqrt(Math.abs(lambdap));
        SXScalar lp = lambdap.abs().sqrt();
        
        //double lm = Math.sqrt(-0.5*S+0.5*norm);
        SXScalar lm = (new SXScalar(-0.5d)).mul(S).add((new SXScalar(0.5)).mul(norm)).sqrt();
        
        //double theta2   = lm==0?0:atan2(lm, R[0]); 
        //double theta2 = 0d;
        //if (lm != 0d) theta2 = Math.atan2(lm, R[0]);
        //SXScalar theta2 = lm.eq(0d, new SXScalar(0d),SXScalar.atan2(lm, R.get(0)));
        SXScalar theta2 = lm.ne(0d, SXScalar.atan2(lm, R.get(0)));
        
        // var theta1   = lambdap<0?asin(lp/cos(theta2)):lambdap>0?atanh(lp/R[0]):lp/R[0];
        //double theta1;
        //if (lambdap < 0){
        //    theta1 = Math.asin(lp/Math.cos(theta2));
        //} else if (lambdap > 0){
        //    theta1 = Trigometry.atanh(lp/R[0]);
        //} else {
        //    theta1 = lp/R[0];
        //}
        SXScalar temp = lambdap.gt(0d, lp.div(R.get(0)).atanh(), lp.div(R.get(0)));
        SXScalar theta1 = lambdap.lt(0d, lp.div(theta2.cos()).asin(), temp);
        // var [l1, l2] = [lp==0?0:theta1/lp, lm==0?0:theta2/lm]
        //double l1=0d;
        //if (lp != 0){
        //    l1 = theta1/lp;
        //}
        //double l2=0d;
        //if (lm != 0){
        //    l2 = theta2/lm;
        //}
        //SXScalar l1 = lp.eq(0d, new SXScalar(0d), theta1.div(lp));
        SXScalar l1 = lp.ne(0d, theta1.div(lp));
        //SXScalar l2 = lm.eq(0d, new SXScalar(0d), theta2.div(lm));
        SXScalar l2 = lm.ne(0d, theta2.div(lm));
        
        //var [A, B1, B2, B3, B4, B5]   = [
        //  (l1-l2)*0.5*(1+S/norm) + l2,  -0.5*T1*(l1-l2)/norm, -0.5*T2*(l1-l2)/norm, 
        //  -0.5*T3*(l1-l2)/norm,         -0.5*T4*(l1-l2)/norm, -0.5*T5*(l1-l2)/norm, 
        //];

        // (l1-l2)*0.5*(1+S/norm) + l2;
        SXScalar A = l1.sub(l2).muls(0.5d).mul((new SXScalar(1d)).add(S.div(norm))).add(l2);
        // -0.5*T1*(l1-l2)/norm;
        SXScalar B1 = new SXScalar(-0.5).mul(T1).mul(l1.sub(l2)).div(norm);
        // -0.5*T2*(l1-l2)/norm;
        SXScalar B2 = new SXScalar(-0.5).mul(T2).mul(l1.sub(l2)).div(norm);
        // -0.5*T3*(l1-l2)/norm;
        SXScalar B3 = new SXScalar(-0.5).mul(T3).mul(l1.sub(l2)).div(norm);
        // -0.5*T4*(l1-l2)/norm;
        SXScalar B4 = new SXScalar(-0.5).mul(T4).mul(l1.sub(l2)).div(norm);
        // -0.5*T5*(l1-l2)/norm;
        SXScalar B5 = new SXScalar(-0.5).mul(T5).mul(l1.sub(l2)).div(norm);
        
        SXScalar[] Belse = new SXScalar[]{
            //(A*R[1]+B3*R[10]+B4*R[9]-B5*R[8]),
            A.mul(R.get(1)).add(B3.mul(R.get(10))).add(B4.mul(R.get(9))).sub(B5.mul(R.get(8))),
            
            //Tsq, 
            //(A*R[2]+B2*R[10]-B4*R[7]+B5*R[6]),
            A.mul(R.get(2)).add(B2.mul(R.get(10))).sub(B4.mul(R.get(7))).add(B5.mul(R.get(6))),
            //(A*R[3]-B2*R[9]-B3*R[7]-B5*R[5]),
            A.mul(R.get(3)).sub(B2.mul(R.get(9))).sub(B3.mul(R.get(7))).sub(B5.mul(R.get(5))),
            

            //FIXME besonders schlecht
            //(A*R[4]-B2*R[8]-B3*R[6]-B4*R[5]),
            A.mul(R.get(4)).sub(B2.mul(R.get(8))).sub(B3.mul(R.get(6))).sub(B4.mul(R.get(5))),
            //(A*R[5]+B1*R[10]+B4*R[4]-B5*R[3]),
            A.mul(R.get(5)).add(B1.mul(R.get(10))).add(B4.mul(R.get(4))).sub(B5.mul(R.get(3))),
            //(A*R[6]-B1*R[9]+B3*R[4]+B5*R[2]),
            A.mul(R.get(6)).sub(B1.mul(R.get(9))).add(B3.mul(R.get(4))).add(B5.mul(R.get(2))),
            //(A*R[7]-B1*R[8]+B3*R[3]+B4*R[2]),
            A.mul(R.get(7)).sub(B1.mul(R.get(8))).add(B3.mul(R.get(3))).add(B4.mul(R.get(2))),
            //(A*R[8]+B1*R[7]+B2*R[4]-B5*R[1]),
            A.mul(R.get(8)).add(B1.mul(R.get(7))).add(B2.mul(R.get(4))).sub(B5.mul(R.get(1))),
            //(A*R[9]+B1*R[6]+B2*R[3]-B4*R[1]),
            A.mul(R.get(9)).add(B1.mul(R.get(6))).add(B2.mul(R.get(3))).sub(B4.mul(R.get(1))),
            //(A*R[10]-B1*R[5]-B2*R[2]-B3*R[1])
            A.mul(R.get(10)).sub(B1.mul(R.get(5))).sub(B2.mul(R.get(2))).sub(B3.mul(R.get(1)))
        };
        SXScalar[] B = SXScalar.eq(norm, new SXScalar(0d), S, new SXScalar(0d), Bif, Belse);
         
        //return bivector
        SXElem[] values = conv(B);
        return create(new SXColVec(getCayleyTable().getBladesCount(), 
            values, CGACayleyTable.getBivectorIndizes()).sx);
    }
    private SXElem[] conv(SXScalar[] values){
        SXElem[] result = new SXElem[values.length];
        for (int i=0;i<values.length;i++){
            result[i] = values[i].sx.scalar();
        }
        return result;
    }
    
    public SparseCGASymbolicMultivector meet(SparseCGASymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SparseCGASymbolicMultivector join(SparseCGASymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Calculate the Euclidean norm. (strict positive, from squared norm).
     *
     * The Euclidean norm is just the regular 2-norm over the 2n dimensional linear space of blades.<p>
     *
     * It must be computed using an Euclidean metric.<p>
     *
     * We also use the squared Euclidean norm, which is just:<br>
     * again with the geometric product evaluated using a Euclidean metric.<p>
     *
     * TODO<br>
     * ist das mit conjugate so richtig? Muss das nicht reverse() sein?<br>
     * Bei ganja ist das conjugate im impl paper normalization/sqrt/pow ist das reverse
     *
     * https://math.stackexchange.com/questions/1128844/about-the-definition-of-norm-in-clifford-algebra?rq=1
     */
    @Override
    public SparseCGASymbolicMultivector norm() {
        SX sx1 = (gp(conjugate()).gradeSelection(0)).getSX();
        return create(SX.sqrt(SX.abs(sx1)));
    }

    /**
     * Calculate the Ideal norm. (signed)
     *
     * standard euclidean vector space norm
     */
    @Override
    public SparseCGASymbolicMultivector inorm() {
        return dual().norm();
        //return unop_Dual(this).norm();
    }

    /**
     * Normalize.
     *
     * TODO encapsulation into cached function<p>
     *
     * overwrites the default implementation in the interface.<p>
     *
     * @throws ArithmeticException if the used norm() is 0.
     * @throws IllegalArgumentException if the arguments norm is no structural scalar
     * @return a normalized (Euclidean) element.
     */
    @Override
    public SparseCGASymbolicMultivector normalizeBySquaredNorm() {
        //return binop_muls(this, 1d / norm());
        return divs(norm());
    }

   
    /**
     * General inverse implemented in an efficient cga specific way.
     *
     * Typically a versor inverse can be implemented more efficient than a general inverse operation.<p>
     *
     * TODO Kann die derzeitige cga spezifische Implementierung auch versors gut invertieren? Wenn ja, dann
     * sollte ich hier die Methode versorInverse() so implementieren, dass die gleiche generalInverse()
     * Methode aufgerufen wird.
     *
     * TODO Eine Implementierung basierend auf der Invertierung der Cayley-Table des geometrischen Produkts
     * ist auch möglich. Das solle ich auch ausprobieren.
     *
     * https://pure.uva.nl/ws/files/4375498/52687_fontijne.pdf
     *
     * @return
     */
    @Override
    public SparseCGASymbolicMultivector generalInverse() {
        return CGAOperations.generalInverse(this);
    }

    /**
     * Scalar inverse.
     *
     * @throws IllegalArgumentException if the multivector is null.
     * @return scalar inverse
     */
    @Override
    public SparseCGASymbolicMultivector scalarInverse() {
        if (!isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        //FIXME unklar ob isZero überhaupt korrekt implementiert ist, das muss nach
        // structural zero testen... also nicht erst zur Laufzeit
        if (isZero()) {
            throw new IllegalArgumentException("zero is not allowed!");
        }
        //System.out.println("scalar inverse: sparsity="+CasADiUtil.toMatrixSparsity(sx.sparsity()).toString());
        //System.out.println(CasADiUtil.toStringMatrix(sx).toString(true));
        //TODO
        // das sollte aber einfacher gehen
        SX resultSX = new SX(sx.sparsity());
        resultSX.at(0).assign(SX.inv(sx.at(0)));
        SparseCGASymbolicMultivector result = create(resultSX);
        //System.out.println(CasADiUtil.toStringMatrix(sx).toString(true));
        return result;
    }

    //======================================================
    // Not in the interface
    //======================================================
    // wer braucht das überhaupt?
    //FIXME
    private SparseCGASymbolicMultivector mul(SparseCGASymbolicMultivector b) {
        SX result = SX.mtimes(sx, (b).getSX());
        return create(result);
    }

    //TODO
    // sollte reverseNorm() nicht default norm() sein? Aber hier gibts ja ein 
    // Vorzeichen, d.h. ist das dann nicht ideal-norm?
    // When a non-positive-definite metric is used, the reverse norm is not a norm in
    // the strict mathematical sense as defined above, since kXkR may have a negative
    // value. However, in practice the reverse norm is useful, especially due to its possible
    // negative sign. E.g., in the conformal model the sign of the reverse norm squared of
    // a sphere indicates whether the sphere is real or imaginary. Hence we will (ab-)use
    // the term “norm” for it throughout this thesis.
    public SparseCGASymbolicMultivector reverseNorm() {
        SparseCGASymbolicMultivector squaredReverseNorm = gp(reverse()).gradeSelection(0);
        SX scalar = (squaredReverseNorm).sx;
        SX sign = SX.sign(scalar);
        SX sqrt = SX.sqrt(SX.abs(scalar));
        return create(SX.mtimes(sign, sqrt));
    }

    /**
     * Elementwise multiplication with a scalar.
     *
     * @param s scalar
     * @return
     */
    private SparseCGASymbolicMultivector muls(SparseCGASymbolicMultivector s) {
        if (s.getSX().is_scalar_()) {
            throw new IllegalArgumentException("The argument of muls() must be a scalar!");
        }
        return create(SX.times(sx, s.getSX()));
    }

    /**
     * Elementwise division with a scalar.
     *
     * @param s scalar
     * @throws IllegalArgumentException if the argument is no structural scalar
     * @return a multivector for which each component of the given multivector is divided by the given scalar
     */
    private SparseCGASymbolicMultivector divs(SparseCGASymbolicMultivector s) {
        // test allowed because it is a test against structural beeing a scalar
        // test against structural 0 not useful
        // runtime can fail if scalar == 0
        if (!s.isScalar()) {
            throw new IllegalArgumentException("The argument of divs() must be a scalar!");
        }
        SX svec = SX.repmat(s.getSX().at(0), sx.sparsity().rows(), 1);
        return create(SX.rdivide(sx, svec));
    }

    // strict positive?
    private static SparseCGASymbolicMultivector norm_e(SparseCGASymbolicMultivector a) {
        SX norme = SX.sqrt(norm_e2(a).getSX().at(0));
        //return Math.sqrt(norm_e2(b));
        CGAMultivectorSparsity scalarSparsity = new CGAMultivectorSparsity(new int[]{0});
        SX result = new SX(CasADiUtil.toCasADiSparsity(scalarSparsity));
        result.assign(norme);
        return create(norme);
    }

    private static SparseCGASymbolicMultivector norm_e2(SparseCGASymbolicMultivector a) {
        SparseCGASymbolicMultivector s = a.scp(a.reverse());
        CGAMultivectorSparsity scalarSparsity = new CGAMultivectorSparsity(new int[]{0});
        SX result = new SX(CasADiUtil.toCasADiSparsity(scalarSparsity));
        SX norme2 = SX.times(SX.gt((s).getSX(), new SX(0d)),
            (s).getSX());
        //double s = scp(reverse());
        //if (s < 0.0) return 0.0; // avoid FP round off causing negative 's'
        result.assign(norme2);
        return create(result);
    }

    
    //-------- voraussichtlich deprecated
    
    private SparseCGASymbolicMultivector expSeries(SparseCGASymbolicMultivector mv, int order) {

        long scale = 1;
        //TODO
        // first scale by power of 2 so that its norm is ~ 1
        /*long scale=1; {
            SparseCGASymbolicMultivector max = norm_e(mv); // das war vorher double
            if (max > 1.0) scale <<= 1;
            while (max > 1.0) {
                max = max / 2;
                scale <<= 1;
            }
        }*/

        SparseCGASymbolicMultivector scaled = mv.gpWithScalar(1.0 / scale);

        //TODO
        return null;
    }

    /**
     * evaluates exp(this) using special cases if possible, using series otherwise.
     *
     * @param M
     * @param order
     * @return
     */
    /*protected Multivector exp(Object M, int order) {
        // check out this^2 for special cases
        Multivector A2 = this.gp(this, M).compress();
        if (A2.isNull(1e-8)) {
            // special case A^2 = 0
            return this.add(1);
        } else if (A2.isScalar()) {
            double a2 = A2.scalarPart();
            // special case A^2 = +-alpha^2
            if (a2 < 0) {
                double alpha = Math.sqrt(-a2);
                return gp(Math.sin(alpha) / alpha).add(Math.cos(alpha));
            }
            //hey: todo what if a2 == 0?
            else {
                double alpha = Math.sqrt(a2);
                return gp(MathU.sinh(alpha) / alpha).add(MathU.cosh(alpha));
            }
        } else return expSeries(M, order);
    }*/
    /**
     * Evaluates exp using series ...(== SLOW & INPRECISE!)
     *
     * @param M metric
     * @param order typicall 12
     * @return
     */
    /*protected Multivector expSeries(Object M, int order) {
        // first scale by power of 2 so that its norm is ~ 1
        long scale=1; {
            double max = this.norm_e();
            if (max > 1.0) scale <<= 1;
            while (max > 1.0) {
                max = max / 2;
                scale <<= 1;
            }
        }

        Multivector scaled = this.gp(1.0 / scale);

        // taylor approximation
        Multivector result = new Multivector(1.0); {
            Multivector tmp = new Multivector(1.0);
            for (int i = 1; i < order; i++) {
                tmp = tmp.gp(scaled.gp(1.0 / i), M);
                result = result.add(tmp);
            }
        }

        // undo scaling
        while (scale > 1) {
            result = result.gp(result, M);
            scale >>>= 1;
        }
        return result;
    }*/
    //----------------------
}
