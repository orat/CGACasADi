package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import java.util.List;

public final class CachedSparseCGASymbolicMultivector extends SparseCGASymbolicMultivector {

    private static final CGASymbolicFunctionCache CACHE = CGASymbolicFunctionCache.instance();

    public CachedSparseCGASymbolicMultivector(SparseCGASymbolicMultivector mv) {
        super(mv.getName(), mv.getSX());
    }

    public CachedSparseCGASymbolicMultivector(String name, SX sx) {
        super(name, sx);
    }

    //======================================================
    // Template Fälle
    //======================================================
    // Fall: Binärer Operator
    @Override
    public SparseCGASymbolicMultivector op(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("op", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).op_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector op_super(SparseCGASymbolicMultivector b) {
        return super.op(b);
    }

    // Spezialfall Unärer Operator mit weiterem nicht-MV Parameter, der aber stark beschränkt ist:
    //    grade direct capturen, hat keine Sparsity
    // Wichtig: grade ist Parameter, landet im Namen.
    @Override
    public SparseCGASymbolicMultivector gradeSelection(int grade) {
        String funName = CACHE.createFuncName("gradeSelection", this, grade);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).gradeSelection_super(grade)
        );
    }

    private SparseCGASymbolicMultivector gradeSelection_super(int grade) {
        return super.gradeSelection(grade);
    }

    // Fall: Unärer Operator
    @Override
    public SparseCGASymbolicMultivector reverse() {
        String funName = CACHE.createFuncName("reverse", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).reverse_super()
        );
    }

    private SparseCGASymbolicMultivector reverse_super() {
        return super.reverse();
    }

    // Fall: Uncached
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
     *      - getScalarMultiplicationOperatorMatrix anstatt dem double eine 1x1 symbolic SX Variable    
     *        übergeben.
     *        Entsprechend darf auch keine SparseDoubleMatrix zurück geliefert werden.
     * - Grund:
     *      - Wird momentan nur intern an wenigen Stellen verwendet.
     *      - Funktioniert nur mit Scalar rechts.
     *      - Numerische Double Parameter zu cachen führt schnell zu einer Explosion an gecached-ten  
     *        Functions.
     *      - Bei den Doubles muss man eine Genauigkeit festlegen, wann sie als gleich gelten sollen.
     *      - Ich vermute, dass es keinen großen Geschwindigkeitsvorteil gibt gegenüber der Variante mit einem
     *          symbolischen Scalar.
     * </pre>
     */
    @Override
    public SparseCGASymbolicMultivector gpWithScalar(double s) {
        return super.gpWithScalar(s);
    }

    //======================================================
    // Straight-forward
    //======================================================
    // BinOp
    @Override
    public SparseCGASymbolicMultivector gp(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("gp", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).gp_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector gp_super(SparseCGASymbolicMultivector b) {
        return super.gp(b);
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector gradeInversion() {
        String funName = CACHE.createFuncName("gradeInversion", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).gradeInversion_super()
        );
    }

    private SparseCGASymbolicMultivector gradeInversion_super() {
        return super.gradeInversion();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector dual() {
        String funName = CACHE.createFuncName("dual", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).dual_super()
        );
    }

    private SparseCGASymbolicMultivector dual_super() {
        return super.dual();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector undual() {
        String funName = CACHE.createFuncName("undual", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).undual_super()
        );
    }

    private SparseCGASymbolicMultivector undual_super() {
        return super.dual();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector conjugate() {
        String funName = CACHE.createFuncName("conjugate", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).conjugate_super()
        );
    }

    private SparseCGASymbolicMultivector conjugate_super() {
        return super.conjugate();
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector lc(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("lc", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) -> 
                params.get(0).lc_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector lc_super(SparseCGASymbolicMultivector b) {
        return super.lc(b);
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector rc(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("rc", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).rc_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector rc_super(SparseCGASymbolicMultivector b) {
        return super.rc(b);
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector scp(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("scp", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).scp_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector scp_super(SparseCGASymbolicMultivector b) {
        return super.scp(b);
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector dot(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("dot", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).dot_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector dot_super(SparseCGASymbolicMultivector b) {
        return super.dot(b);
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector ip(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("ip", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).ip_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector ip_super(SparseCGASymbolicMultivector b) {
        return super.ip(b);
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector vee(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("vee", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).vee_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector vee_super(SparseCGASymbolicMultivector b) {
        return super.vee(b);
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector add(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("add", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).add_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector add_super(SparseCGASymbolicMultivector b) {
        return super.add(b);
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector sub(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("sub", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) -> 
                params.get(0).sub_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector sub_super(SparseCGASymbolicMultivector b) {
        return super.sub(b);
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector negate14() {
        String funName = CACHE.createFuncName("negate14", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).negate14_super()
        );
    }

    private SparseCGASymbolicMultivector negate14_super() {
        return super.negate14();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector scalarAbs() {
        String funName = CACHE.createFuncName("scalarAbs", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scalarAbs_super()
        );
    }

    private SparseCGASymbolicMultivector scalarAbs_super() {
        return super.scalarAbs();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector scalarSqrt() {
        String funName = CACHE.createFuncName("scalarSqrt", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scalarSqrt_super()
        );
    }

    private SparseCGASymbolicMultivector scalarSqrt_super() {
        return super.scalarSqrt();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector sqrt() {
        String funName = CACHE.createFuncName("sqrt", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).sqrt_super()
        );
    }

    private SparseCGASymbolicMultivector sqrt_super() {
        return super.sqrt();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector norm() {
        String funName = CACHE.createFuncName("norm", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).norm_super()
        );
    }

    private SparseCGASymbolicMultivector norm_super() {
        return super.norm();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector inorm() {
        String funName = CACHE.createFuncName("inorm", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).inorm_super()
        );
    }

    private SparseCGASymbolicMultivector inorm_super() {
        return super.inorm();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector normalizeBySquaredNorm() {
        String funName = CACHE.createFuncName("normalizeBySquaredNorm", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).normalizeBySquaredNorm_super()
        );
    }

    private SparseCGASymbolicMultivector normalizeBySquaredNorm_super() {
        return super.normalizeBySquaredNorm();
    }

    // BinOp
    @Override
    public SparseCGASymbolicMultivector div(SparseCGASymbolicMultivector b) {
        String funName = CACHE.createFuncName("div", this, b);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) 
                -> params.get(0).div_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector div_super(SparseCGASymbolicMultivector b) {
        return super.div(b);
    }

    //////////////////////////7
    // UnOp
    @Override
    public SparseCGASymbolicMultivector generalInverse() {
        String funName = CACHE.createFuncName("generalInverse", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).generalInverse_super()
        );
    }

    private SparseCGASymbolicMultivector generalInverse_super() {
        return super.generalInverse();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector scalarInverse() {
        String funName = CACHE.createFuncName("scalarInverse", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scalarInverse_super()
        );
    }

    private SparseCGASymbolicMultivector scalarInverse_super() {
        return super.scalarInverse();
    }

    // UnOp
    @Override
    public SparseCGASymbolicMultivector versorInverse() {
        String funName = CACHE.createFuncName("versorInverse", this);
        return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).versorInverse_super()
        );
    }

    private SparseCGASymbolicMultivector versorInverse_super() {
        return super.versorInverse();
    }
}
