package de.orat.math.cgacasadi.impl;

//import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.SXElem;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorVectorDouble;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAKVectorSparsity;
import util.cga.CGAMultivectorSparsity;
import util.cga.CGAOperatorMatrixUtils;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;
import util.CayleyTable;
import util.cga.CGAOperations;

public class SparseCGASymbolicMultivector implements iMultivectorSymbolic {
   
    private MultivectorSymbolic.Callback callback;
    private String name;
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    final static CGAOperatorMatrixUtils cgaOperatorMatrixUtils = new CGAOperatorMatrixUtils(baseCayleyTable);
    
    final static CGAExprGraphFactory fac = new CGAExprGraphFactory();
    
    // a multivector is represented by a sparse column vector
    private final SX sx;
    
    // zum Erzeugen von symbolischen Konstanten
    public static SparseCGASymbolicMultivector instance(String name, SparseDoubleColumnVector vector){
        return new SparseCGASymbolicMultivector(name, vector);
    }
    private SparseCGASymbolicMultivector(String name, SparseDoubleColumnVector vector){
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

    private static iMultivectorSymbolic pseudoscalar;
    public iMultivectorSymbolic pseudoscalar(){
        if (pseudoscalar == null){
            SparseDoubleColumnVector pseudo = fac.createPseudoscalar();
            String pseudoscalar_name = baseCayleyTable.getPseudoscalarName();
            pseudoscalar = fac.createMultivectorSymbolic(pseudoscalar_name, pseudo);
            //int grade = baseCayleyTable.getGrade(baseCayleyTable.getBasisBladeNames().length-1);
            //pseudoscalar = instance(pseudoscalar_name, grade);
        }
        return pseudoscalar;
    }
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
    
   
    // operators
    
    /**
     * Generic GA reverse implementation based on matrix calculations.
     * 
     * @return 
     */
    @Override
    public iMultivectorSymbolic reverse(){
        SparseDoubleMatrix revm = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        System.out.println("Reverse matrix = "+revm.toString(false));
        //System.out.println(revm.toString(true));
        SX result = SX.mtimes(CasADiUtil.toSX(revm), sx);
        return new SparseCGASymbolicMultivector(result);
    }
    
    /**
     * Generic GA reverse implementation based on grade selection.
     * 
     * [Dorst] p. 604
     * 
     * noch unvollständig
     * 
     * @return 
     */
    /*public iMultivectorSymbolic reverse_(){
        int[] grades = grades();
        iMultivectorSymbolic result = null; //denseEmptyInstance(); 
        for (int i=0;i<grades.length;i++){
            System.out.println("reverse:grade()="+String.valueOf(grades[i]));
            SX multiplier = SX.pow(new SX(-1d), new SX((new SX(grades[i])),));
            iMultivectorSymbolic norme2 = gradeSelection(grades[i]).
                    gp(new SparseCGASymbolicMultivector(multiplier));
            System.out.println("reverse:norme2 grade()="+String.valueOf(grades[i])+" ="+norme2.toString());
            //TODO
            // eleganter wäre es die for-Schleifen bei 1 starten zu lassen
            // und den ersten Wert vor dem Vorschleifen in die Variable zu streichen
            // dann könnte ich das if vermeiden.
            if (result == null) {
                result = norme2;
            } else {
                result = result.add(norme2);
            }
            System.out.println("reverse:norme2 sparsity="+result.getSparsity().toString());
        }
        return result;
    }*/
    
    // tested
    @Override
    public iMultivectorSymbolic gradeSelection(int grade){
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createGradeSelectionOperatorMatrix(baseCayleyTable, grade);
        SX gradeSelectionMatrix  = CasADiUtil.toSX(m); // bestimmt sparsity
        return new SparseCGASymbolicMultivector(SX.mtimes(gradeSelectionMatrix, sx));
    }

    /**
     * Dual.
     *
     * Poincare duality operator based on matrix based implementation of left contraction
     * 
     * TODO
     * CGACayleyTableLeftContractionProduct muss noch implementiert werden
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

    /**
     * Conjugate.
     *
     * Clifford Conjugation
     *
     * @param a
     * @return a.Conjugate()
     */
    @Override
    public iMultivectorSymbolic conjugate(){
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getConjugationOperatorMatrix();
        //System.out.println("conjugate result:"+revm.toString(true));
        //FIXME
        // muss da nicht times verwendet werden wie bei op und gp?
        SX result = SX.mtimes(CasADiUtil.toSX(m), sx);
        //System.out.println(CasADiUtil.toStringMatrix(result).toString(true));
        return new SparseCGASymbolicMultivector(result);
    }

    /**
     * Involute.
     *
     * Main involution - grade inversion
     * 
     * swapping the parity of each grade
     * 
     * Das kann leicht für beliebige GAs formuliert werden
     * 
     * @return involution/grade inversion
     */
    @Override
    public iMultivectorSymbolic gradeInversion (){
        // involute (Ak) = (-1) hoch k * Ak
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createInvolutionOperatorMatrix(baseCayleyTable);
        return new SparseCGASymbolicMultivector(SX.mtimes(CasADiUtil.toSX(m), sx));
    }
    
    @Override
    public iMultivectorSymbolic gp(iMultivectorSymbolic b){
        //TODO
        // scheint nicht zu funktionieren wenn die Summe der Grade der Argumente größer 5 ist
        System.out.println("---gp---");
        SX opm = CasADiUtil.toSXProductMatrix((SparseCGASymbolicMultivector) b, CGACayleyTableGeometricProduct.instance());
        System.out.println("--- end of gp matrix creation ---");
        SX result = SX.mtimes(opm.T(), ((SparseCGASymbolicMultivector) this).getSX());
        return new SparseCGASymbolicMultivector(result);
    }
    
    // die default-impl im Interface sollte eigentlich auch funktionieren
    // tut sie aber nicht, sie liefert nur 00-Werte
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
     * Calculate the Euclidean norm. (strict positive).
     * 
     * The Euclidean norm is just the regular 2-norm over the 2n dimensional linear
     * space of blades. 
     * It must be computed using a Euclidean metric.
     * 
     * We also use the squared Euclidean norm, which is just:
     * again with the geometric product evaluated using a Euclidean metric.
     * 
     * TODO
     * ist das mit conjugate so richtig? Muss das nicht reverse() sein?
     */
    @Override
    public SparseCGASymbolicMultivector norm() {
        SX sx1 = ((SparseCGASymbolicMultivector) gp(conjugate()).gradeSelection(0)).getSX();
        return new SparseCGASymbolicMultivector(SX.sqrt(SX.abs(sx1)));
        //return Math.sqrt(Math.abs(binop_Mul(this, this.Conjugate())._mVec[0]));
    }
    

    /**
     * Calculate the Ideal norm. (signed)
     */
    @Override
    public iMultivectorSymbolic inorm() {
        return dual().norm();
        //return unop_Dual(this).norm();
    }

    // When a non-positive-definite metric is used, the reverse norm is not a norm in
    // the strict mathematical sense as defined above, since kXkR may have a negative
    // value. However, in practice the reverse norm is useful, especially due to its possible
    // negative sign. E.g., in the conformal model the sign of the reverse norm squared of
    // a sphere indicates whether the sphere is real or imaginary. Hence we will (ab-)use
    // the term “norm” for it throughout this thesis.
    public SparseCGASymbolicMultivector reverseNorm(){
        iMultivectorSymbolic squaredReverseNorm = this.gp(reverse()).gradeSelection(0);
        SX scalar = ((SparseCGASymbolicMultivector) squaredReverseNorm).sx;
        SX sign = SX.sign(scalar);
        SX sqrt = SX.sqrt(scalar);
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
     * @return 
     */
    private SparseCGASymbolicMultivector divs(SparseCGASymbolicMultivector s){
        if (s.getSX().is_scalar_()) throw new IllegalArgumentException("The argument of divs() must be a scalar!");
        return new SparseCGASymbolicMultivector(SX.rdivide(sx, s.getSX()));
    }
    
    /**
     * normalized.
     *
     * Returns a normalized (Euclidean) element.
     */
    @Override
    public SparseCGASymbolicMultivector normalized() {
        //return binop_muls(this, 1d / norm());
        return divs(norm());
    }

    /**
     * General inverse implemented in an efficient cga specific way.
     * 
     * Typically a versor inverse can be implemented more efficient than a general
     * inverse operation. 
     * 
     * TODO
     * Kann die derzeitige cga spezifische Implementierung auch versors gut
     * invertieren? Wenn ja, dann sollte ich hier die Methode versorInverse()
     * so implementieren, dass die gleiche generalInverse() Methode aufgerufen wird.
     * 
     * @return 
     */
    @Override
    public iMultivectorSymbolic generalInverse(){
        return CGAOperations.generalInverse(this);
    }
    
    @Override
    public iMultivectorSymbolic scalarInverse(){
       //System.out.println("scalar inverse: sparsity="+CasADiUtil.toMatrixSparsity(sx.sparsity()).toString());
       //System.out.println(CasADiUtil.toStringMatrix(sx).toString(true));
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
    
    
    //------------
    
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

    //TODO brauche ich hier nicht statt double ein symbolic scalar?
    @Override
    public iMultivectorSymbolic gp(double s) {
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getScalarMultiplicationOperatorMatrix(s);
        SX result = SX.mtimes(CasADiUtil.toSX(m), sx);
        return new SparseCGASymbolicMultivector(result);
    }
    
    /**
     * Undual cga specific implementation based on dual and fix sign changed.
     * 
     * @return 
     */
    @Override
    public iMultivectorSymbolic undual() {
         //return gp(exprGraphFac.createPseudoscalar()).gp(-1); // -1 wird gebraucht
         return dual().gp(-1);
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
    public ColumnVectorSparsity getSparsity(){
        return CasADiUtil.toColumnVectorSparsity(sx.sparsity());
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

    @Override
    public iMultivectorSymbolic atan2(iMultivectorSymbolic y) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iMultivectorSymbolic exp() {
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
    // umbenennen in euclideanNorm()
    private static SparseCGASymbolicMultivector norm_e(SparseCGASymbolicMultivector a) {
        SX norme = SX.sqrt(norm_e2(a).getSX().at(0));
        //return Math.sqrt(norm_e2(b));
        CGAMultivectorSparsity scalarSparsity = new CGAMultivectorSparsity(new int[]{0});
        SX result = new SX(CasADiUtil.toCasADiSparsity(scalarSparsity));
        result.assign(norme);
        return new SparseCGASymbolicMultivector(norme);
    }
    // umbenennen in euclideanNormSquare()
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
    
    public iMultivectorSymbolic meet(iMultivectorSymbolic b){
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
 
    }
    public iMultivectorSymbolic join(iMultivectorSymbolic b){
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iMultivectorSymbolic abs() {
        return new SparseCGASymbolicMultivector(SX.abs(sx));
    }
}