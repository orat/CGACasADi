package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.api.SXColVec;
import de.dhbw.rahmlab.casadi.api.SXScalar;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorVectorDouble;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAKVectorSparsity;
import util.cga.CGAMultivectorSparsity;
import util.cga.CGAOperatorMatrixUtils;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.CayleyTable;
//import util.cga.CGACayleyTableOuterProduct;
import util.cga.CGAOperations;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SparseCGASymbolicMultivector implements iMultivectorSymbolic {
   
    private MultivectorSymbolic.Callback callback;
    private String name;
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    final static CGAOperatorMatrixUtils cgaOperatorMatrixUtils = new CGAOperatorMatrixUtils(baseCayleyTable);
    
    final static CGAExprGraphFactory fac = new CGAExprGraphFactory();
    
    // a multivector is represented by a sparse column vector
    private final SX sx;
    
    // zum Erzeugen von symbolischen Konstanten
    public static SparseCGASymbolicMultivector instance(String name, SparseDoubleMatrix vector){
        return new SparseCGASymbolicMultivector(name, vector);
    }
    protected SparseCGASymbolicMultivector(String name, SparseDoubleMatrix vector){
        StdVectorDouble vecDouble = new StdVectorDouble(vector.nonzeros());
        sx = new SX(CasADiUtil.toCasADiSparsity(vector.getSparsity()), 
                new SX(new StdVectorVectorDouble(new StdVectorDouble[]{vecDouble})));
        //FIXME
        // warum kann ich den Namen nicht im SX speichern?
        this.name = name;
    }
   
    public static SparseCGASymbolicMultivector instance(String name, int grade){
        return new SparseCGASymbolicMultivector(name, grade);
    }
    /**
     * Creates a k-Vector.
     * 
     * @param name
     * @param grade 
     */
    protected SparseCGASymbolicMultivector(String name, int grade){
        CGAMultivectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        sx = SX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
        this.name = null;
    }
    
    @Override
    public void init(MultivectorSymbolic.Callback callback) {
        this.callback = callback;
    }

    public iMultivectorSymbolic scalar(double value){
            SparseDoubleMatrix sca = fac.createScalar(value);
            String scalar_name = baseCayleyTable.getBasisBladeName(0);
            return fac.createMultivectorSymbolic(scalar_name, sca);
    }
    private static iMultivectorSymbolic pseudoscalar;
    public iMultivectorSymbolic pseudoscalar(){
        if (pseudoscalar == null){
            SparseDoubleMatrix pseudo = fac.createPseudoscalar();
            String pseudoscalar_name = baseCayleyTable.getPseudoscalarName();
            pseudoscalar = fac.createMultivectorSymbolic(pseudoscalar_name, pseudo);
            //int grade = baseCayleyTable.getGrade(baseCayleyTable.getBasisBladeNames().length-1);
            //pseudoscalar = instance(pseudoscalar_name, grade);
        }
        return pseudoscalar;
    }
    
    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vieleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    private static SparseCGASymbolicMultivector inversePseudoscalar;
    public SparseCGASymbolicMultivector inversePseudoscalar(){
        if (inversePseudoscalar == null){
            inversePseudoscalar = (SparseCGASymbolicMultivector) pseudoscalar().reverse();
        }
        return inversePseudoscalar;
    }
    
    public static SparseCGASymbolicMultivector instance(String name, ColumnVectorSparsity sparsity){
        return new SparseCGASymbolicMultivector(name, sparsity);
    }
    protected SparseCGASymbolicMultivector(String name, ColumnVectorSparsity sparsity){
        sx = SX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
        this.name = null;
    }
    public static SparseCGASymbolicMultivector instance(String name){
        return new SparseCGASymbolicMultivector(name);
    }
    protected SparseCGASymbolicMultivector(String name){
        //de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sparsity = CasADiUtil.toCasADiSparsity(CGAKVectorSparsity.dense());
        de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sparsity2 = de.dhbw.rahmlab.casadi.impl.casadi.Sparsity.dense(32);
        sx = SX.sym(name, sparsity2);
        this.name = name;
    }
    SparseCGASymbolicMultivector(SX sx){
        this.sx = sx;
        this.name = null;
    }
    
    protected final static class Lazy implements Supplier<CGASymbolicFunction> {
        private Supplier<CGASymbolicFunction> supplier;
        private CGASymbolicFunction value;

        private Lazy(Supplier<CGASymbolicFunction> supplier) {
            this.supplier = supplier;
        }
        @Override
        public CGASymbolicFunction get() {
            if (supplier == null) {
                return value;
            } else {
                value = supplier.get();
                supplier = null;
                return value;
            }
        }
    }
    
    private static final Supplier<CGASymbolicFunction> reverseFunction = 
            new Lazy(() -> createReverseFunction());
   
    
    // operators
    
    // reverse
    
    /**
     * Generic GA reverse function implementation based on matrix calculations.
     * 
     * @return reverse of a multivector
     */
    private static CGASymbolicFunction createReverseFunction(){
        SX sxarg = SX.sym("mv",baseCayleyTable.getBladesCount());
        SparseDoubleMatrix revm = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        SX sxres = SX.mtimes(CasADiUtil.toSX(revm), sxarg);
        return new CGASymbolicFunction("reverse", 
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxarg)),
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxres)));    
    }
    @Override
    public CGASymbolicFunction getReverseFunction(){
        return reverseFunction.get();
    }
    //TODO
    // um die Wiederverwertung der Functions zu aktivieren die Methode einfach auskommentieren,
    // dann greift die default impl im Interface die obige getGradeSelectionFunction() Methode verwendet
    /*@Override
    public iMultivectorSymbolic reverse(){
        SparseDoubleMatrix revm = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        SX result = SX.mtimes(CasADiUtil.toSX(revm), sx);
        return new SparseCGASymbolicMultivector(result);
    }*/
    
    
    // grade selection
    
    private static final List<Supplier<CGASymbolicFunction>> gradeSelectionFunctions  = 
            createGradeSelectionList();
    private static List<Supplier<CGASymbolicFunction>> createGradeSelectionList(){
        List<Supplier<CGASymbolicFunction>> result = new ArrayList<>();
        int length = baseCayleyTable.getPseudoscalarGrade()+1;
        for (int i=0;i<length;i++){
            final int grade = i;
            result.add(new Lazy(() -> createGradeSelectionFunction(grade)));
        }
        return result;
    } 
    private static CGASymbolicFunction createGradeSelectionFunction(int grade){
        SX sxarg = SX.sym("mv",baseCayleyTable.getBladesCount());
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createGradeSelectionOperatorMatrix(baseCayleyTable, grade);
        SX gradeSelectionMatrix  = CasADiUtil.toSX(m); // bestimmt sparsity
        SX sxres = SX.mtimes(gradeSelectionMatrix, sxarg);
        return new CGASymbolicFunction("grade"+String.valueOf(grade)+"Selection", 
           Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxarg)),
           Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxres))); 
    }
    @Override
    public iFunctionSymbolic getGradeSelectionFunction(int grade){
        if (grade >= gradeSelectionFunctions.size()) throw new IllegalArgumentException("grade "+String.valueOf(grade)+
                " does not exist! Max grade == "+String.valueOf(gradeSelectionFunctions.size()-1));
        return gradeSelectionFunctions.get(grade).get();
    }
    //TODO
    // um die Wiederverwertung der Functions zu aktivieren die Methode einfach auskommentieren,
    // dann greift die default impl im Interface die obige getGradeSelectionFunction() Methode verwendet
    /*@Override
    public iMultivectorSymbolic gradeSelection(int grade){
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createGradeSelectionOperatorMatrix(baseCayleyTable, grade);
        SX gradeSelectionMatrix  = CasADiUtil.toSX(m); // bestimmt sparsity
        return new SparseCGASymbolicMultivector(SX.mtimes(gradeSelectionMatrix, sx));
    }*/

    
    // dual
    
    /**
     * Dual.
     *
     * Poincare duality operator based on matrix based implementation of left 
     * contraction.
     *
     * @param a
     * @return !a
     */
    /*public iMultivectorSymbolic dual(){
        SX lcm = CasADiUtil.toSXProductMatrix(this,
                CGACayleyTableLeftContractionProduct.instance());
        return new SparseCGASymbolicMultivector(SX.mtimes(lcm, 
                ((SparseCGASymbolicMultivector) inversePseudoscalar()).getSX()));
    }*/

    
    // undual
    
    //TODO
    // undual zu implmentieren muss ich erst gp() implementieren damit ich createGPFuncition() zur Verfügung habe
    //private static final Supplier<CGASymbolicFunction> undualFunction = 
    //        new Lazy(() -> createUndualFunction());
    /**
     * 
     * @return undual of a multivector
     */
    /*private static CGASymbolicFunction createUndualFunction(){
        SX sxarg = SX.sym("mv",baseCayleyTable.getBladesCount());
        SparseDoubleMatrix revm = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        SX sxres = SX.mtimes(CasADiUtil.toSX(revm), sxarg);
        return new CGASymbolicFunction("undual", 
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxarg)),
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxres)));    
    }*/
    /*@Override
    public CGASymbolicFunction getUndualFunction(){
        
    }*/
    
    private static final Supplier<CGASymbolicFunction> undualFunction = 
            new Lazy(() -> createUndualFunction());
    @Override
    public CGASymbolicFunction getUndualFunction(){
        return undualFunction.get();
    }
    /**
     * Undual cga specific implementation based on dual and fix sign changed.
     * 
     * @return undual function
     */
    private static CGASymbolicFunction createUndualFunction(){
        SX sxarg = SX.sym("mv",baseCayleyTable.getBladesCount());
        iMultivectorSymbolic mv = new SparseCGASymbolicMultivector(sxarg);
        iMultivectorSymbolic res = mv.dual().gp(-1d);
         return new CGASymbolicFunction("undual", 
                Collections.singletonList(mv),
                Collections.singletonList(res));    
    }
    /*@Override
    public iMultivectorSymbolic undual() {
        //return gp(exprGraphFac.createPseudoscalar()).gp(-1); // -1 wird gebraucht
        return dual().gp(-1);
    }*/

    
    // conjugate
    
    private static final Supplier<CGASymbolicFunction> conjugateFunction = 
            new Lazy(() -> createConjugateFunction());
    private static CGASymbolicFunction createConjugateFunction(){
        SX sxarg = SX.sym("mv",baseCayleyTable.getBladesCount());
        SparseDoubleMatrix conjm = cgaOperatorMatrixUtils.getConjugationOperatorMatrix();
        SX sxres = SX.mtimes(CasADiUtil.toSX(conjm), sxarg);
        return new CGASymbolicFunction("conjugate", 
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxarg)),
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxres)));    
    }
    /**
     * Clifford Conjugation.
     */
    @Override
    public CGASymbolicFunction getConjugateFunction(){
        return conjugateFunction.get();
    }
    /**
     * Conjugate.
     *
     * Clifford Conjugation
     *
     * @param a
     * @return a.Conjugate()
     */
    /*@Override
    public iMultivectorSymbolic conjugate(){
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getConjugationOperatorMatrix();
        SX result = SX.mtimes(CasADiUtil.toSX(m), sx);
        return new SparseCGASymbolicMultivector(result);
    }*/

    
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
    public iMultivectorSymbolic gradeInversion (){
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createInvolutionOperatorMatrix(baseCayleyTable);
        return new SparseCGASymbolicMultivector(SX.mtimes(CasADiUtil.toSX(m), sx));
    }
    
    private static final Map<String, CGASymbolicFunction> functions = new HashMap<>();
    /**
     * 
     * @param name
     * @param args
     * @param res
     * @return 
     * 
     * TODO
     * wenn die Methode für verschiedene Funktionen aber mit gleichem name aufgerufen
     * wird, dann wird immer die zuerst erzeugte Funktion zurückgeliefert. Dieser
     * Fehlerfall ist nicht abgefangen. Theoretisch könnte res und args mit den
     * korrespondierenden Parametern der zurückgegebenen Function verglichen werden
     * und bei nicht-Übereinstimmung eine ex gefeuert werden. Das ist aber schwierig
     * zu implementieren
     */
    @Override
    public iMultivectorSymbolic _asCachedSymbolicFunction(String name, 
        List<iMultivectorSymbolic> args, iMultivectorSymbolic res) {
        CGASymbolicFunction fun = functions.get(name);
        if (fun == null){
            fun = new CGASymbolicFunction(name, args,
                Collections.singletonList(res));  
            functions.put(name, fun);
        }
        return fun.callSymbolic(args).iterator().next();
    }
    
    // geometric product
    
    private static final Supplier<CGASymbolicFunction> gpFunction = 
            new Lazy(() -> createGPFunction());
    /**
      * @return gp between two multivectors
     */
    private static CGASymbolicFunction createGPFunction(){
        SX sxarga = SX.sym("a",baseCayleyTable.getBladesCount());
        SX sxargb = SX.sym("b",baseCayleyTable.getBladesCount());
        
        SX opm = CasADiUtil.toSXProductMatrix(new SparseCGASymbolicMultivector(sxargb), CGACayleyTableGeometricProduct.instance());
        SX sxres = SX.mtimes(opm.T(), sxarga);
        
        List<iMultivectorSymbolic> args = Arrays.asList(new SparseCGASymbolicMultivector(sxarga),
                new SparseCGASymbolicMultivector(sxargb));
        return new CGASymbolicFunction("gp", args,
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxres)));    
    }
    @Override
    public CGASymbolicFunction getGPFunction(){
        return gpFunction.get();
    }
    // uncomment to deactivate function cache
    /*@Override
    public iMultivectorSymbolic gp(iMultivectorSymbolic b){
        //System.out.println("---gp---");
        SX opm = CasADiUtil.toSXProductMatrix((SparseCGASymbolicMultivector) b, CGACayleyTableGeometricProduct.instance());
        //System.out.println("--- end of gp matrix creation ---");
        SX result = SX.mtimes(opm.T(), ((SparseCGASymbolicMultivector) this).getSX());
        return new SparseCGASymbolicMultivector(result);
    }*/
    
    // geometric product with scalar
    
    private static final Map<Double, CGASymbolicFunction> gpWithScalarFunctions  = 
            new HashMap<>();
    private static CGASymbolicFunction createGPWithScalarFunction(double s){
        SX sxarg = SX.sym("mv",baseCayleyTable.getBladesCount());
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getScalarMultiplicationOperatorMatrix(s);
        SX sxres = SX.mtimes(CasADiUtil.toSX(m), sxarg);
        return new CGASymbolicFunction("gp", Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxarg)),
                Collections.singletonList((iMultivectorSymbolic) new SparseCGASymbolicMultivector(sxres)));    
    }
    @Override
    public CGASymbolicFunction getGPWithScalarFunction(double s){
        CGASymbolicFunction result =  gpWithScalarFunctions.get(s);
        if (result == null){
            result = createGPWithScalarFunction(s);
            gpWithScalarFunctions.put(s, result);
        }
        return result;
    }
    /*@Override
    public iMultivectorSymbolic gp(double s) {
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getScalarMultiplicationOperatorMatrix(s);
        SX result = SX.mtimes(CasADiUtil.toSX(m), sx);
        return new SparseCGASymbolicMultivector(result);
    }*/
    
    /*@Override
    public iMultivectorSymbolic op(iMultivectorSymbolic b){
        //TODO mit transponieren und vertauschen von a und b wirds völlig falsch
        // so wie es jetzt ist stimmen nur die ersten 5 Elemente
        System.out.println("---op---");
        SX gpm = CasADiUtil.toSXProductMatrix((SparseCGASymbolicMultivector) this, CGACayleyTableOuterProduct.instance());
        System.out.println("--- end of op matrix creation ---");
        SX result = SX.mtimes(gpm, ((SparseCGASymbolicMultivector) b).getSX());
        return new SparseCGASymbolicMultivector(result);
    }*/

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
    public iMultivectorSymbolic add(iMultivectorSymbolic b){
        //System.out.println("sparsity(a)="+sx.sparsity().toString(true));
        //System.out.println("sparsity(b)="+((SparseCGASymbolicMultivector) b).getSX().sparsity().toString(true));
        SX result = SX.plus(sx, ((SparseCGASymbolicMultivector) b).getSX());
        //System.out.println("sparsity(add)="+result.sparsity().toString(true));
        return new SparseCGASymbolicMultivector(result);
    }

    /**
     * Multivector subtraction.
     *
     * @param a
     * @param b
     * @return a - b
     */
    /*public iMultivectorSymbolic sub (iMultivectorSymbolic b){
        SX result = SX.minus(sx1, ((SparseCGASymbolicMultivector) b).getSX());
        return new SparseCGASymbolicMultivector(result);
    }*/

    // wer braucht das überhaupt?
    //FIXME
    private iMultivectorSymbolic mul(iMultivectorSymbolic b){
        SX result = SX.mtimes(sx, ((SparseCGASymbolicMultivector) b).getSX());
        return new SparseCGASymbolicMultivector(result);
    }
    
    /**
     * Calculate the Euclidean norm. (strict positive, from squared norm).
     * 
     * The Euclidean norm is just the regular 2-norm over the 2n dimensional linear
     * space of blades.<p>
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
        SX sx1 = ((SparseCGASymbolicMultivector) gp(conjugate()).gradeSelection(0)).getSX();
        return new SparseCGASymbolicMultivector(SX.sqrt(SX.abs(sx1)));
    }
    
    /**
     * Calculate the Ideal norm. (signed)
     * 
     * standard euclidean vector space norm 
     */
    @Override
    public iMultivectorSymbolic inorm() {
        return dual().norm();
        //return unop_Dual(this).norm();
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
    public SparseCGASymbolicMultivector reverseNorm(){
        iMultivectorSymbolic squaredReverseNorm = gp(reverse()).gradeSelection(0);
        SX scalar = ((SparseCGASymbolicMultivector) squaredReverseNorm).sx;
        SX sign = SX.sign(scalar);
        SX sqrt = SX.sqrt(SX.abs(scalar));
        return new SparseCGASymbolicMultivector(SX.mtimes(sign, sqrt));
    }
    
    
    
    /**
     * Elementwise multiplication with a scalar.
     * 
     * @param s scalar
     * @return 
     */
    private SparseCGASymbolicMultivector muls(SparseCGASymbolicMultivector s){
        if (s.getSX().is_scalar_()) throw new IllegalArgumentException("The argument of muls() must be a scalar!");
        return new SparseCGASymbolicMultivector(SX.times(sx, s.getSX()));
    }
    /**
     * Elementwise division with a scalar.
     * 
     * @param s scalar
     * @throws IllegalArgumentException if the argument is no structural scalar
     * @return a multivector for which each component of the given multivector is divided by the given scalar
     */
    private SparseCGASymbolicMultivector divs(SparseCGASymbolicMultivector s){
        // test allowed because it is a test against structural beeing a scalar
        // test against structural 0 not useful
        // runtime can fail if scalar == 0
        if (!s.isScalar()) throw new IllegalArgumentException("The argument of divs() must be a scalar!");
        SX svec = SX.repmat(s.getSX().at(0),sx.sparsity().rows(),1);
        return new SparseCGASymbolicMultivector(SX.rdivide(sx, svec));
    }
    
    /**
     * Normalize.
     *
     * TODO
     * encapsulation into cached function<p>
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
    
    // CGA R4,1. e1*e1 = e2*e2 = e3*e3 = e4*4 = 1, e5*e5 = -1
    // Normalize an even element X = [1,e12,e13,e14,e15,e23,e24,e25,e34,e35,e45,
    //                                e1234,e1235,e1245,e1345,e2345]
    // Normalization, Square Roots, and the Exponential and Logarithmic Maps in 
    // Geometric Algebras of Less than 6D
    // S de. Keninck, M. Roelfs, 2022
    public SparseCGASymbolicMultivector normalizeEvenElement(){
        if (!isEven()) throw new IllegalArgumentException("Element must be an even element!");
        // var S = X[0]*X[0]-X[10]*X[10]+X[11]*X[11]-X[12]*X[12]-X[13]*X[13]-X[14]*X[14]-X[15]*X[15]+X[1]*X[1]
        // +X[2]*X[2]+X[3]*X[3]-X[4]*X[4]+X[5]*X[5]+X[6]*X[6]-X[7]*X[7]+X[8]*X[8]-X[9]*X[9];
        /*SX S = CasADiUtil.createScalar();
        S = SX.plus(S, SX.sq(sx.at(0)));
        S = SX.minus(S, SX.sq(sx.at(10)));
        S = SX.plus(S, SX.sq(sx.at(11)));
        S = SX.minus(S, SX.sq(sx.at(12)));
        S = SX.minus(S, SX.sq(sx.at(13)));
        S = SX.minus(S, SX.sq(sx.at(14)));
        S = SX.minus(S, SX.sq(sx.at(15)));
        S = SX.plus(S, SX.sq(sx.at(1)));
        S = SX.plus(S, SX.sq(sx.at(2)));
        S = SX.plus(S, SX.sq(sx.at(3)));
        S = SX.minus(S, SX.sq(sx.at(4)));
        S = SX.plus(S, SX.sq(sx.at(5)));
        S = SX.plus(S, SX.sq(sx.at(6)));
        S = SX.minus(S, SX.sq(sx.at(7)));
        S = SX.plus(S, SX.sq(sx.at(8)));
        S = SX.minus(S, SX.sq(sx.at(9)));*/
        SXColVec X = new SXColVec(sx);
        SXScalar S = SXScalar.sumSq(X, new int[]{0,11,1,2,3,5,6,8});
        S = S.sub(SXScalar.sumSq(X, new int[]{10,12,13,14,15,4,7,9}));
        
        // var T1 = 2*(X[0]*X[11]-X[10]*X[12]+X[13]*X[9]-X[14]*X[7]+X[15]*X[4]-X[1]*X[8]+X[2]*X[6]
        // -X[3]*X[5]);
        SXScalar T1 = SXScalar.sumProd(X, new int[]{0,13,15,2}, new int[]{11,9,4,6});
        T1 = T1.sub(SXScalar.sumProd(X, new int[]{10,14,1,3}, new int[]{12,7,8,5}));
        
        // var T2 = 2*(X[0]*X[12]-X[10]*X[11]+X[13]*X[8]-X[14]*X[6]+X[15]*X[3]-X[1]*X[9]+X[2]*X[7]-X[4]*X[5]);
        SXScalar T2 = SXScalar.sumProd(X, new int[]{0,13,15,2}, new int[]{12,8,3,7});
        T2 = T2.sub(SXScalar.sumProd(X, new int[]{10,14,1,4}, new int[]{11,6,9,5}));
        
        //var T3 = 2*(X[0]*X[13]-X[10]*X[1]+X[11]*X[9]-X[12]*X[8]+X[14]*X[5]-X[15]*X[2]+X[3]*X[7]-X[4]*X[6]);
        SXScalar T3 = SXScalar.sumProd(X, new int[]{0,11,14,3}, new int[]{13,9,5,7});
        T3 = T3.sub(SXScalar.sumProd(X, new int[]{10,12,15,4}, new int[]{1,8,2,6})).muls(2);
        
        //var T4 = 2*(X[0]*X[14]-X[10]*X[2]-X[11]*X[7]+X[12]*X[6]-X[13]*X[5]+X[15]*X[1]+X[3]*X[9]-X[4]*X[8]);
        SXScalar T4 = SXScalar.sumProd(X, new int[]{0,12,15,3}, new int[]{14,6,1,9});
        T4 = T4.sub(SXScalar.sumProd(X, new int[]{10,11,13,4}, new int[]{2,7,5,8})).muls(2d);
        
        //var T5 = 2*(X[0]*X[15]-X[10]*X[5]+X[11]*X[4]-X[12]*X[3]+X[13]*X[2]-X[14]*X[1]+X[6]*X[9]-X[7]*X[8]);
        SXScalar T5 = SXScalar.sumProd(X, new int[]{0,11,13,6}, new int[]{15,4,2,9});
        T5 = T5.sub(SXScalar.sumProd(X, new int[]{10,12,14,7}, new int[]{5,3,1,8})).muls(2d);
        
        //var TT = -T1*T1+T2*T2+T3*T3+T4*T4+T5*T5;
        SXScalar TT = T1.sq().add(T2.sq()).add(T3.sq()).add(T4.sq()).add(T5.sq()).negate();
        
        //var N = ((S*S+TT)**0.5+S)**0.5, N2 = N*N;
        SXScalar N = S.sq().add(TT).pow(0.5).add(S);
        SXScalar N2 = N.sq();
        
        //var M = 2**0.5*N/(N2*N2+TT);
        SXScalar M = SXScalar.pow(2d, N.muls(0.5d).div(N2.sq().add(TT)));
        
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
        
        /*return rotor(A*X[0] + B1*X[11] - B2*X[12] - B3*X[13] - B4*X[14] - B5*X[15],
        A*X[1] - B1*X[8] + B2*X[9] + B3*X[10] - B4*X[15] + B5*X[14],
        A*X[2] + B1*X[6] - B2*X[7] + B3*X[15] + B4*X[10] - B5*X[13],
        A*X[3] - B1*X[5] - B2*X[15] - B3*X[7] - B4*X[9] + B5*X[12],
        A*X[4] - B1*X[15] - B2*X[5] - B3*X[6] - B4*X[8] + B5*X[11],
        A*X[5] - B1*X[3] + B2*X[4] - B3*X[14] + B4*X[13] + B5*X[10],
        A*X[6] + B1*X[2] + B2*X[14] + B3*X[4] - B4*X[12] - B5*X[9],
        A*X[7] + B1*X[14] + B2*X[2] + B3*X[3] - B4*X[11] - B5*X[8],
        A*X[8] - B1*X[1] - B2*X[13] + B3*X[12] + B4*X[4] + B5*X[7],
        A*X[9] - B1*X[13] - B2*X[1] + B3*X[11] + B4*X[3] + B5*X[6],
        A*X[10] + B1*X[12] - B2*X[11] - B3*X[1] - B4*X[2] - B5*X[5],
        A*X[11] + B1*X[0] + B2*X[10] - B3*X[9] + B4*X[7] - B5*X[4],
        A*X[12] + B1*X[10] + B2*X[0] - B3*X[8] + B4*X[6] - B5*X[3],
        A*X[13] - B1*X[9] + B2*X[8] + B3*X[0] - B4*X[5] + B5*X[2],
        A*X[14] + B1*X[7] - B2*X[6] + B3*X[5] + B4*X[0] - B5*X[1],
        A*X[15] - B1*X[4] + B2*X[3] - B3*X[2] + B4*X[1] + B5*X[0]);*/
        
        //TODO
        throw new RuntimeException("not yet implemented!");
    }
    /*
    function Normalize(X) {
        var S = X[0]*X[0]-X[10]*X[10]+X[11]*X[11]-X[12]*X[12]-X[13]*X[13]-X[14]*X[14]-X[15]*X[15]+X[1]*X[1]
        +X[2]*X[2]+X[3]*X[3]-X[4]*X[4]+X[5]*X[5]+X[6]*X[6]-X[7]*X[7]+X[8]*X[8]-X[9]*X[9];
        var T1 = 2*(X[0]*X[11]-X[10]*X[12]+X[13]*X[9]-X[14]*X[7]+X[15]*X[4]-X[1]*X[8]+X[2]*X[6]-X[3]*X[5]);
        var T2 = 2*(X[0]*X[12]-X[10]*X[11]+X[13]*X[8]-X[14]*X[6]+X[15]*X[3]-X[1]*X[9]+X[2]*X[7]-X[4]*X[5]);
        var T3 = 2*(X[0]*X[13]-X[10]*X[1]+X[11]*X[9]-X[12]*X[8]+X[14]*X[5]-X[15]*X[2]+X[3]*X[7]-X[4]*X[6]);
        var T4 = 2*(X[0]*X[14]-X[10]*X[2]-X[11]*X[7]+X[12]*X[6]-X[13]*X[5]+X[15]*X[1]+X[3]*X[9]-X[4]*X[8]);
        var T5 = 2*(X[0]*X[15]-X[10]*X[5]+X[11]*X[4]-X[12]*X[3]+X[13]*X[2]-X[14]*X[1]+X[6]*X[9]-X[7]*X[8]);
        var TT = -T1*T1+T2*T2+T3*T3+T4*T4+T5*T5;
        var N = ((S*S+TT)**0.5+S)**0.5, N2 = N*N;
        var M = 2**0.5*N/(N2*N2+TT);
        var A = N2*M, [B1,B2,B3,B4,B5] = [-T1*M,-T2*M,-T3*M,-T4*M,-T5*M];
        return rotor(A*X[0] + B1*X[11] - B2*X[12] - B3*X[13] - B4*X[14] - B5*X[15],
        A*X[1] - B1*X[8] + B2*X[9] + B3*X[10] - B4*X[15] + B5*X[14],
        A*X[2] + B1*X[6] - B2*X[7] + B3*X[15] + B4*X[10] - B5*X[13],
        A*X[3] - B1*X[5] - B2*X[15] - B3*X[7] - B4*X[9] + B5*X[12],
        A*X[4] - B1*X[15] - B2*X[5] - B3*X[6] - B4*X[8] + B5*X[11],
        A*X[5] - B1*X[3] + B2*X[4] - B3*X[14] + B4*X[13] + B5*X[10],
        A*X[6] + B1*X[2] + B2*X[14] + B3*X[4] - B4*X[12] - B5*X[9],
        A*X[7] + B1*X[14] + B2*X[2] + B3*X[3] - B4*X[11] - B5*X[8],
        A*X[8] - B1*X[1] - B2*X[13] + B3*X[12] + B4*X[4] + B5*X[7],
        A*X[9] - B1*X[13] - B2*X[1] + B3*X[11] + B4*X[3] + B5*X[6],
        A*X[10] + B1*X[12] - B2*X[11] - B3*X[1] - B4*X[2] - B5*X[5],
        A*X[11] + B1*X[0] + B2*X[10] - B3*X[9] + B4*X[7] - B5*X[4],
        A*X[12] + B1*X[10] + B2*X[0] - B3*X[8] + B4*X[6] - B5*X[3],
        A*X[13] - B1*X[9] + B2*X[8] + B3*X[0] - B4*X[5] + B5*X[2],
        A*X[14] + B1*X[7] - B2*X[6] + B3*X[5] + B4*X[0] - B5*X[1],
        A*X[15] - B1*X[4] + B2*X[3] - B3*X[2] + B4*X[1] + B5*X[0]);
    }
    */
    /**
     * General inverse implemented in an efficient cga specific way.
     * 
     * Typically a versor inverse can be implemented more efficient than a general
     * inverse operation.<p>
     * 
     * TODO
     * Kann die derzeitige cga spezifische Implementierung auch versors gut
     * invertieren? Wenn ja, dann sollte ich hier die Methode versorInverse()
     * so implementieren, dass die gleiche generalInverse() Methode aufgerufen wird.
     * 
     * TODO
     * Eine Implementierung basierend auf der Invertierung der Cayley-Table des
     * geometrischen Produkts ist auch möglich. Das solle ich auch ausprobieren.
     * 
     * https://pure.uva.nl/ws/files/4375498/52687_fontijne.pdf
     * 
     * @return 
     */
    @Override
    public iMultivectorSymbolic generalInverse(){
        return CGAOperations.generalInverse(this);
    }
    
    /**
     * Scalar inverse.
     * 
     * @throws IllegalArgumentException if the multivector is null.
     * @return scalar inverse
     */
    @Override
    public iMultivectorSymbolic scalarInverse(){
       //FIXME unklar ob isZero überhaupt korrekt implementiert ist, das muss nach
       // structural zero testen... also nicht erst zur Laufzeit
       if (isZero()) throw new IllegalArgumentException("zero is not allowed!");
       //System.out.println("scalar inverse: sparsity="+CasADiUtil.toMatrixSparsity(sx.sparsity()).toString());
       //System.out.println(CasADiUtil.toStringMatrix(sx).toString(true));
       //TODO
       // das sollte aber einfacher gehen
       SX resultSX = new SX(sx.sparsity());
       resultSX.at(0).assign(SX.inv(sx.at(0)));
       SparseCGASymbolicMultivector result =  new SparseCGASymbolicMultivector(resultSX);
       //System.out.println(CasADiUtil.toStringMatrix(sx).toString(true));
       return result;
    }
    
    /**
     * Negates the signs of the vector and 4-vector parts of an multivector. 
     * 
     * @return multivector with changed signs for vector and 4-vector parts
     */
    @Override
    public iMultivectorSymbolic negate14() {
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createNegate14MultiplicationMatrix(baseCayleyTable);
        return new SparseCGASymbolicMultivector(SX.mtimes(CasADiUtil.toSX(m), sx));
    }
    
    @Override
    public iMultivectorSymbolic scalarAtan2(iMultivectorSymbolic y) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // exponential of a bivector only for CGA (R41)
    // kann aus CGAImplTest.exp() abgeleitet werden
    @Override
    public iMultivectorSymbolic exp() {
        //TODO
        // exception werfen, wenn kein Bivector
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // exponential of a bivector only for CGA (R41)
    // kann aus CGAImplTest.exp() abgeleitet werden
    @Override
    public iMultivectorSymbolic sqrt(){
        if (isEven()){
            return add(scalar(1d)).normalizeEvenElement();
        }
        throw new RuntimeException("sqrt() not yet implemented for non even elements.");
    }
    @Override
    public iMultivectorSymbolic log() {
        //TODO
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Scalar product.
     * 
     * TODO
     * alternativ via gradeSelection() implementieren?
     * 
     * @param rhs
     * @return scalar product of this with a 'rhs'
     */
    private SparseCGASymbolicMultivector scp(SparseCGASymbolicMultivector rhs) {
        // eventuell sollte ich ip() explizit implementieren und hier verwenden,
        // damit scp() unabhängig von der Reihenfolge der Argumente wird
        // return ip(x, LEFT_CONTRACTION).scalarPart();
        SX sxres = ((SparseCGASymbolicMultivector) lc(rhs)).getSX().at(0);
        CGAMultivectorSparsity scalarSparsity = new CGAMultivectorSparsity(new int[]{0});
        SX result = new SX(CasADiUtil.toCasADiSparsity(scalarSparsity));
        result.assign(sxres);
	return new SparseCGASymbolicMultivector(sxres);
    }
    // strict positive?
    private static SparseCGASymbolicMultivector norm_e(SparseCGASymbolicMultivector a) {
        SX norme = SX.sqrt(norm_e2(a).getSX().at(0));
        //return Math.sqrt(norm_e2(b));
        CGAMultivectorSparsity scalarSparsity = new CGAMultivectorSparsity(new int[]{0});
        SX result = new SX(CasADiUtil.toCasADiSparsity(scalarSparsity));
        result.assign(norme);
        return new SparseCGASymbolicMultivector(norme);
    }
    private static SparseCGASymbolicMultivector norm_e2(SparseCGASymbolicMultivector a) {
        iMultivectorSymbolic s = a.scp((SparseCGASymbolicMultivector) a.reverse());
        CGAMultivectorSparsity scalarSparsity = new CGAMultivectorSparsity(new int[]{0});
        SX result = new SX(CasADiUtil.toCasADiSparsity(scalarSparsity));
        SX norme2 = SX.times(SX.gt(((SparseCGASymbolicMultivector) s).getSX(), new SX(0d)), 
                ((SparseCGASymbolicMultivector) s).getSX());
        //double s = scp(reverse());
        //if (s < 0.0) return 0.0; // avoid FP round off causing negative 's'
        result.assign(norme2);
        return new SparseCGASymbolicMultivector(result);
    }
    
    
    //-------- voraussichtlich deprecated
    
    private SparseCGASymbolicMultivector expSeries(SparseCGASymbolicMultivector mv, int order){
        
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

        iMultivectorSymbolic scaled = mv.gp(1.0 / scale);
        
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
    
    
    public iMultivectorSymbolic meet(iMultivectorSymbolic b){
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
 
    }
    public iMultivectorSymbolic join(iMultivectorSymbolic b){
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iMultivectorSymbolic scalarAbs() {
        return new SparseCGASymbolicMultivector(SX.abs(sx));
    }
    @Override
    public iMultivectorSymbolic scalarSqrt(){
        return new SparseCGASymbolicMultivector(SX.sqrt(sx));
    }
    
    
    //---- non symbolic functions
    
    @Override
    public CayleyTable getCayleyTable() {
       return baseCayleyTable;
    }
    
    @Override
    public int grade() {
        CGAMultivectorSparsity sparsity = CasADiUtil.toCGAMultivectorSparsity(sx.sparsity());
        return sparsity.getGrade();
    }

    @Override
    public int[] grades() {
        return CasADiUtil.toCGAMultivectorSparsity(sx.sparsity()).getGrades();
    }
    
    @Override
    public String toString(){
        SparseStringMatrix stringMatrix = CasADiUtil.toStringMatrix(sx);
        return stringMatrix.toString(true);
    }
    
    @Override
    public CGAMultivectorSparsity/*ColumnVectorSparsity*/ getSparsity(){
        return CasADiUtil.toCGAMultivectorSparsity(sx.sparsity());
    }
    
    public SX getSX(){
        return sx;
    }

    /**
     * Get SX representation of a blade.
     * 
     * @param bladeName pseudoscalar_name of the blade
     * @return null, if blade is structurel null else the SX representing the blade
     * @throws IllegalArgumentException if the given blade pseudoscalar_name does not exist in the cayley-table
     */
    SX getSX(String bladeName){
        int row = baseCayleyTable.getBasisBladeRow(bladeName);
        if (row == -1) throw new IllegalArgumentException("The given bladeName ="+
                bladeName+" does not exist in the cayley table!");
        //if (sparsity.isNonZero(row,0)) return sx1.at(row, 0);
        if (sx.sparsity().has_nz(row, 0)) return sx.at(row, 0);
        return null;
    }
    
    public String getName(){
        if (name != null) return name;
        return sx.name();
    }
    
    public int getBladesCount(){
        return baseCayleyTable.getBladesCount();
    }
    
    public boolean isEven(){
        return getSparsity().isEven();
    }
    
    public boolean isZero(){
        //TODO unklar ob das so korrekt ist, hier muss auf struktural zero getestet werden
        // nicht erst auf zero zur Laufzeit
        return sx.is_zero();
    }
    
    @Override
    public iMultivectorSymbolic sparseEmptyInstance() {
        // empty vector implementation
        SX mysx = new SX(CasADiUtil.toCasADiSparsity(
                ColumnVectorSparsity.empty(baseCayleyTable.getRows())), 
                new SX(new StdVectorVectorDouble(new StdVectorDouble[]{new StdVectorDouble()})));
        return new SparseCGASymbolicMultivector(mysx);
    }
    
    @Override
    public SparseCGASymbolicMultivector denseEmptyInstance(){
        SX mysx = new SX(CasADiUtil.toCasADiSparsity(
                ColumnVectorSparsity.dense(baseCayleyTable.getRows())));
        return new SparseCGASymbolicMultivector(mysx);
    }
}