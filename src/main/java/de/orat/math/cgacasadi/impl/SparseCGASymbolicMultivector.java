package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
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
import util.cga.CGACayleyTableOuterProduct;
import util.cga.CGAOperations;

public class SparseCGASymbolicMultivector implements iMultivectorSymbolic {
   
    private static CGAExprGraphFactory exprGraphFac = new CGAExprGraphFactory();
    
    private MultivectorSymbolic.Callback callback;
    private String name;
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    final static CGAOperatorMatrixUtils cgaOperatorMatrixUtils = new CGAOperatorMatrixUtils(baseCayleyTable);
    
    // a multivector is represented by a sparse column vector
    private final MX mx;
    
    public static SparseCGASymbolicMultivector instance(String name, SparseDoubleColumnVector vector){
        return new SparseCGASymbolicMultivector(name, vector);
    }
    private SparseCGASymbolicMultivector(String name, SparseDoubleColumnVector vector){
        StdVectorDouble vecDouble = new StdVectorDouble(vector.nonzeros());
        //FIXME
        // gibt das wirklich einen Spaltenvektor?
        mx = new MX(CasADiUtil.toCasADiSparsity(vector.getSparsity()), new MX(vecDouble));
        //FIXME
        // warum kann ich den Namen nicht im MX speichern?
        this.name = name;
    }
   
    public static SparseCGASymbolicMultivector instance(String name, int grade){
        return new SparseCGASymbolicMultivector(name, grade);
    }
    
    public SparseCGASymbolicMultivector(){
        mx = new MX(CasADiUtil.toCasADiSparsity(
                ColumnVectorSparsity.empty(baseCayleyTable.getRows())), new MX(new StdVectorDouble()));
    }
    /*public SparseCGASymbolicMultivector(){
        mx1 = null;
        this.name = null;
    }*/
    
    /**
     * Creates a k-Vector.
     * 
     * @param name
     * @param grade 
     */
    protected SparseCGASymbolicMultivector(String name, int grade){
        CGAMultivectorSparsity sparsity = CGAKVectorSparsity.instance(grade);
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
        this.name = null;
    }
    
    @Override
    public void init(MultivectorSymbolic.Callback callback) {
        this.callback = callback;
    }

    private static SparseCGASymbolicMultivector pseudoscalar;
    public /*static*/ SparseCGASymbolicMultivector pseudoscalar(){
        if (pseudoscalar == null){
            String pseudoscalar_name = baseCayleyTable.getPseudoscalarName();
            int grade = baseCayleyTable.getGrade(baseCayleyTable.getBasisBladeNames().length-1);
            pseudoscalar = instance(pseudoscalar_name, grade);
        }
        return pseudoscalar;
    }
    private static SparseCGASymbolicMultivector inversePseudoscalar;
    public /*static*/ SparseCGASymbolicMultivector inversePseudoscalar(){
        if (inversePseudoscalar == null){
            inversePseudoscalar = (SparseCGASymbolicMultivector) pseudoscalar().reverse();
        }
        return inversePseudoscalar;
    }
    
    public static SparseCGASymbolicMultivector instance(String name, ColumnVectorSparsity sparsity){
        return new SparseCGASymbolicMultivector(name, sparsity);
    }
    protected SparseCGASymbolicMultivector(String name, ColumnVectorSparsity sparsity){
        //this.sparsity = new CGAMultivectorSparsity(sparsity);
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
        this.name = null;
    }
    public static SparseCGASymbolicMultivector instance(String name){
        return new SparseCGASymbolicMultivector(name);
    }
    protected SparseCGASymbolicMultivector(String name){
        CGAMultivectorSparsity sparsity = CGAKVectorSparsity.dense();
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
        //TODO gleich in casadi eine dense sparsity erzeugen
        this.name = name;
    }
    SparseCGASymbolicMultivector(MX mx){
        //sparsity = CasADiUtil.toCGAMultivectorSparsity(mx1.sparsity());
        this.mx = mx;
        this.name = null;
    }
    
   
    // operators
    
    public iMultivectorSymbolic reverse(){
        SparseDoubleMatrix revm = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        System.out.println("Reverse matrix = "+revm.toString(false));
        //System.out.println(revm.toString(true));
        MX result = MX.mtimes(CasADiUtil.toMX(revm), mx);
        return new SparseCGASymbolicMultivector(result);
    }
    
    //test für grade==3 failed
    public iMultivectorSymbolic gradeSelection(int grade){
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createGradeSelectionOperatorMatrix(baseCayleyTable, grade);
        MX gradeSelection  = CasADiUtil.toMX(m);
        return new SparseCGASymbolicMultivector(MX.mtimes(gradeSelection, mx));
    }

    /**
     * Dual.
     *
     * Poincare duality operator.
     *
     * @param a
     * @return !a
     */
    /*public iMultivectorSymbolic dual(){
        MX lcm = CasADiUtil.toMXProductMatrix(this,
                CGACayleyTableLeftContractionProduct.instance());
        return new SparseCGASymbolicMultivector(MX.mtimes(lcm, 
                ((SparseCGASymbolicMultivector) inversePseudoscalar()).getMX()));
    }*/

    /**
     * Conjugate.
     *
     * Clifford Conjugation
     *
     * @param a
     * @return a.Conjugate()
     */
    public iMultivectorSymbolic conjugate(){
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getConjugationOperatorMatrix();
        //System.out.println("conjugate result:"+revm.toString(true));
        //FIXME
        // muss da nicht times verwendet werden wie bei op und gp?
        MX result = MX.mtimes(CasADiUtil.toMX(m), mx);
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
     * @param a
     * @return a.Involute()
     */
    public iMultivectorSymbolic gradeInversion (){
        // involute (Ak) = (-1) hoch k * Ak
        SparseDoubleMatrix m = CGAOperatorMatrixUtils.createInvolutionOperatorMatrix(baseCayleyTable);
        return new SparseCGASymbolicMultivector(MX.mtimes(CasADiUtil.toMX(m), mx));
    }
    
    public iMultivectorSymbolic gp(iMultivectorSymbolic b){
        System.out.println("---gp---");
        MX opm = CasADiUtil.toMXProductMatrix((SparseCGASymbolicMultivector) this, CGACayleyTableGeometricProduct.instance());
        System.out.println("--- end of gp matrix creation ---");
        MX result = MX.times(opm, ((SparseCGASymbolicMultivector) b).getMX());
        return new SparseCGASymbolicMultivector(result);
    }
    
    // die default-impl im Interface sollte eigentlich auch funktionieren
    public iMultivectorSymbolic op(iMultivectorSymbolic b){
        System.out.println("---op---");
        MX gpm = CasADiUtil.toMXProductMatrix((SparseCGASymbolicMultivector) this, CGACayleyTableOuterProduct.instance());
        System.out.println("--- end of op matrix creation ---");
        MX result = MX.times(gpm, ((SparseCGASymbolicMultivector) b).getMX());
        return new SparseCGASymbolicMultivector(result);
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
    public iMultivectorSymbolic add(iMultivectorSymbolic b){
        System.out.println("sparsity(a)="+mx.sparsity().toString(true));
        System.out.println("sparsity(b)="+((SparseCGASymbolicMultivector) b).getMX().sparsity().toString(true));
        MX result = MX.plus(mx, ((SparseCGASymbolicMultivector) b).getMX());
        System.out.println("sparsity(add)="+result.sparsity().toString(true));
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
        MX result = MX.minus(mx1, ((SparseCGASymbolicMultivector) b).getMX());
        return new SparseCGASymbolicMultivector(result);
    }*/

    // wer braucht das überhaupt?
    //FIXME
    public iMultivectorSymbolic mul(iMultivectorSymbolic b){
        MX result = MX.mtimes(mx, ((SparseCGASymbolicMultivector) b).getMX());
        return new SparseCGASymbolicMultivector(result);
    }
    
    /**
     * Calculate the Euclidean norm. (strict positive).
     */
    public iMultivectorSymbolic norm() {
        MX mx1 = ((SparseCGASymbolicMultivector) gp(conjugate()).gradeSelection(0)).getMX();
        return new SparseCGASymbolicMultivector(MX.sqrt(MX.abs(mx1)));
        //return Math.sqrt(Math.abs(binop_Mul(this, this.Conjugate())._mVec[0]));
    }
    

    /**
     * Calculate the Ideal norm. (signed)
     */
    public iMultivectorSymbolic inorm() {
        return dual().norm();
        //return unop_Dual(this).norm();
    }

    
    /**
     * normalized.
     *
     * Returns a normalized (Euclidean) element.
     */
    public iMultivectorSymbolic normalized() {
        //return binop_muls(this, 1d / norm());
        //TODO
        return null;
    }

    public iMultivectorSymbolic generalInverse(){
        return CGAOperations.generalInverse(this);
    }
    
    public iMultivectorSymbolic scalorInverse(){
       return new SparseCGASymbolicMultivector(MX.inv(mx));
    }
    
    /**
     * Negates only the signs of the vector and 4-vector parts of an multivector. 
     * 
     * @return multivector with changed signs for vector and 4-vector parts
     */
    /*private iMultivectorSymbolic negate14(iMultivectorSymbolic revm){
        
        DM result = new DM(CasADiUtil.toCasADiSparsity(sparsity),dm);
        //StdVectorDouble result = dm.get_nonzeros();
        
        int[] grade1Indizes = sparsity.getIndizes(1);
        for (int i=0;i<grade1Indizes.length;i++){
           result.at(grade1Indizes[i]).assign(new DM(-dm.at(grade1Indizes[i]).scalar()));
        }
        int[] grade4Indizes = sparsity.getIndizes(4);
        for (int i=0;i<grade4Indizes.length;i++){
           result.at(grade4Indizes[i]).assign(new DM(-dm.at(grade4Indizes[i]).scalar()));
        }
        
        return new CGA5Multivector(result, sparsity);
    }*/
    
    
    //------------
    
    @Override
    public iMultivectorSymbolic zeroInstance() {
        return new SparseCGASymbolicMultivector();
    }

    @Override
    public iMultivectorSymbolic gp(double s) {
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getScalarMultiplicationOperatorMatrix(s);
        MX result = MX.mtimes(CasADiUtil.toMX(m), mx);
        return new SparseCGASymbolicMultivector(result);
    }

    /*@Override
    public iMultivectorSymbolic dual() {
        
    }*/
    
    @Override
    public iMultivectorSymbolic undual() {
         //return gp(exprGraphFac.createPseudoscalar()).gp(-1); // -1 wird gebraucht
         return dual().gp(-1);
    }

    @Override
    public iMultivectorSymbolic negate14() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    //---- non symbolic functions
    
    @Override
    public CayleyTable getCayleyTable() {
       return baseCayleyTable;
    }
    
    @Override
    public int grade() {
        CGAMultivectorSparsity sparsity = CasADiUtil.toCGAMultivectorSparsity(mx.sparsity());
        return sparsity.getGrade();
    }

    @Override
    public int[] grades() {
        return CasADiUtil.toCGAMultivectorSparsity(mx.sparsity()).getGrades();
    }
    
    @Override
    public String toString(){
        SparseStringMatrix stringMatrix = CasADiUtil.toStringMatrix(mx);
        return stringMatrix.toString(true);
    }
    
    @Override
    public ColumnVectorSparsity getSparsity(){
        return CasADiUtil.toColumnVectorSparsity(mx.sparsity());
        //return sparsity;
    }
    public MX getMX(){
        return mx;
    }

    /**
     * Get MX representation of a blade.
     * 
     * @param bladeName pseudoscalar_name of the blade
     * @return null, if blade is structurel null else the MX representing the blade
     * @throws IllegalArgumentException if the given blade pseudoscalar_name does not exist in the cayley-table
     */
    MX getMX(String bladeName){
        int row = baseCayleyTable.getBasisBladeRow(bladeName);
        if (row == -1) throw new IllegalArgumentException("The given bladeName ="+
                bladeName+" does not exist in the cayley table!");
        //if (sparsity.isNonZero(row,0)) return mx1.at(row, 0);
        if (mx.sparsity().has_nz(row, 0)) return mx.at(row, 0);
        return null;
    }
    
    public String getName(){
        if (name != null) return name;
        return mx.name();
    }
    
    public int getBladesCount(){
        return baseCayleyTable.getBladesCount();
    }
}