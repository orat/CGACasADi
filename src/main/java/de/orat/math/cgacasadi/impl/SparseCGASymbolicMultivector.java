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

public class SparseCGASymbolicMultivector implements iMultivectorSymbolic{
   
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
        mx = new MX(CasADiUtil.toCasADiSparsity(vector.getSparsity()), new MX(vecDouble));
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
        mx = null;
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
        //sparsity = CasADiUtil.toCGAMultivectorSparsity(mx.sparsity());
        this.mx = mx;
        this.name = null;
    }
    /*public SparseCGASymbolicMultivector(){
        //sparsity = null;
        mx = null;
        this.name = null;
    }*/
    
    @Override
    public String toString(){
        //TODO
        // eventuell sparsity noch mit ausgeben
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

    public String getName(){
        if (name != null) return name;
        return mx.name();
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
        //if (sparsity.isNonZero(row,0)) return mx.at(row, 0);
        if (mx.sparsity().has_nz(row, 0)) return mx.at(row, 0);
        return null;
    }
    
    public int getBladesCount(){
        return baseCayleyTable.getBladesCount();
    }
    
    
    // operators
    
    public iMultivectorSymbolic reverse(){
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        //System.out.println("Reverse matrix = "+m.toString(false));
        //System.out.println(m.toString(true));
        MX result = MX.mtimes(CasADiUtil.toMX(m), mx);
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
        MX result = MX.mtimes(CasADiUtil.toMX(m), mx);
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
    
    // implementation based on cayley table for the outer product
    // test failed
    /*public iMultivectorSymbolic op_(iMultivectorSymbolic b){
        MX opm = CasADiUtil.toMXProductMatrix(this, CGACayleyTableOuterProduct.instance());
        //System.out.println("product matrix:");
        //System.out.println(opm.toString(true));
        return new SparseCGASymbolicMultivector(MX.mtimes(opm, ((SparseCGASymbolicMultivector) b).getMX()));
    }*/

    public iMultivectorSymbolic gp(iMultivectorSymbolic b){
        MX gpm = CasADiUtil.toMXProductMatrix(this, CGACayleyTableGeometricProduct.instance());
        // Die Darstellung ist praktisch nicht leserlich
        //System.out.println("product matrix gp:");
        //System.out.println(gpm.toString(true));
        MX result = MX.mtimes(gpm/*.T()*/, ((SparseCGASymbolicMultivector) b).getMX());
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
        MX result = MX.minus(mx, ((SparseCGASymbolicMultivector) b).getMX());
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
        MX mx = ((SparseCGASymbolicMultivector) gp(conjugate()).gradeSelection(0)).getMX();
        return new SparseCGASymbolicMultivector(MX.sqrt(MX.abs(mx)));
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
        iMultivectorSymbolic conjugate = conjugate();
        iMultivectorSymbolic gradeInversion = gradeInversion();
        iMultivectorSymbolic reversion = reverse();
        iMultivectorSymbolic part1 = conjugate.gp(gradeInversion).gp(reversion); 
        iMultivectorSymbolic part2 = gp(part1); 
        //iMultivectorSymbolic part3 = negate14(part2);
        //double scalar = part2.gp(part3).scalarPart(); 
        //return part1.gp(part3).gp(1d/scalar);
        //TODO
        return null;
    }
    
    /**
     * Negates only the signs of the vector and 4-vector parts of an multivector. 
     * 
     * @return multivector with changed signs for vector and 4-vector parts
     */
    /*private iMultivectorSymbolic negate14(iMultivectorSymbolic m){
        
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
    public void init(MultivectorSymbolic.Callback callback) {
        this.callback = callback;
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
    public iMultivectorSymbolic zeroInstance() {
        return new SparseCGASymbolicMultivector();
    }

    @Override
    public CayleyTable getCayleyTable() {
       return baseCayleyTable;
    }

    @Override
    public iMultivectorSymbolic gp(double s) {
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getScalarMultiplicationOperatorMatrix(s);
        MX result = MX.mtimes(CasADiUtil.toMX(m), mx);
        return new SparseCGASymbolicMultivector(result);
    }
}