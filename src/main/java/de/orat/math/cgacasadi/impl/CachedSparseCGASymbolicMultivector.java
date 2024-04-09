package de.orat.math.cgacasadi.impl;

import java.util.List;

public final class CachedSparseCGASymbolicMultivector extends SparseCGASymbolicMultivector {

    private final CGASymbolicFunctionCache cache;

    public CachedSparseCGASymbolicMultivector(CGASymbolicFunctionCache cache, SparseCGASymbolicMultivector mv) {
        super(mv);
        this.cache = cache;
    }

    //======================================================
    // Template Fälle
    //======================================================
    // Fall: Binärer Operator
    @Override
    public SparseCGASymbolicMultivector op(SparseCGASymbolicMultivector b) {
        String funName = this.cache.createFuncName("op", this, b);
        return this.cache.getOrCreateSymbolicFunction(funName, List.of(this, b),
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
        String funName = this.cache.createFuncName("gradeSelection", this, grade);
        return this.cache.getOrCreateSymbolicFunction(funName, List.of(this),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).gradeSelection_super(grade)
        );
    }

    private SparseCGASymbolicMultivector gradeSelection_super(int grade) {
        return super.gradeSelection(grade);
    }

    // Fall: Unärer Operator
    @Override
    public SparseCGASymbolicMultivector reverse() {
        String funName = this.cache.createFuncName("reverse", this);
        return this.cache.getOrCreateSymbolicFunction(funName, List.of(this),
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
     * - Würde ich sogar gerne entfernen.
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
    public SparseCGASymbolicMultivector gpWithScalar(double s) {
        return super.gpWithScalar(s);
    }

    //======================================================
    // Straight-forward
    //======================================================
    @Override
    public SparseCGASymbolicMultivector gp(SparseCGASymbolicMultivector b) {
        String funName = this.cache.createFuncName("gp", this, b);
        return this.cache.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).gp_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector gp_super(SparseCGASymbolicMultivector b) {
        return super.gp(b);
    }
}
