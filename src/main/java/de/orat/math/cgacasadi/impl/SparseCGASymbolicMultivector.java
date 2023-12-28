package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGACayleyTableOuterProduct;
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

public class SparseCGASymbolicMultivector implements iMultivectorSymbolic{
   
    private MultivectorSymbolic.Callback callback;
    private String name;
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    final static CGAOperatorMatrixUtils cgaOperatorMatrixUtils = new CGAOperatorMatrixUtils(baseCayleyTable);
    
    // a multivector is represented by a sparse column vector
    //final CGAMultivectorSparsity sparsity;
    private final MX mx;
    
    public static SparseCGASymbolicMultivector instance(String name, SparseDoubleColumnVector vector){
        return new SparseCGASymbolicMultivector(name, vector);
    }
    private SparseCGASymbolicMultivector(String name, SparseDoubleColumnVector vector){
        //sparsity = new CGAMultivectorSparsity(vector.getSparsity());
        mx = new MX(CasADiUtil.toCasADiSparsity(vector.getSparsity()), new MX(vector.nonzeros()[0]));
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
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
        this.name = null;
    }
    
    
    private static SparseCGASymbolicMultivector pseudoscalar;
    public /*static*/ SparseCGASymbolicMultivector pseudoscalar(){
        if (pseudoscalar == null){
            String name = baseCayleyTable.getPseudoscalarName();
            int grade = baseCayleyTable.getGrade(baseCayleyTable.getBasisBladeNames().length-1);
            pseudoscalar = instance(name, grade);
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
    public SparseCGASymbolicMultivector(){
        //sparsity = null;
        mx = null;
        this.name = null;
    }
    
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
     * @param bladeName name of the blade
     * @return null, if blade is structurel null else the MX representing the blade
     * @throws IllegalArgumentException if the given blade name does not exist in the cayley-table
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
        System.out.println("product matrix:");
        System.out.println(gpm.toString(true));
        MX result = MX.mtimes(gpm/*.T()*/, ((SparseCGASymbolicMultivector) b).getMX());
        return new SparseCGASymbolicMultivector(result);
    }
    
    
    
    /**
     * Vee.
     *
     * The regressive product. (JOIN)
     *
     * @param a
     * @param b
     * @return a & b
     */
    public iMultivectorSymbolic vee (iMultivectorSymbolic b){
       /*
	res[31]=1*(a[31]*b[31]);
	res[30]=1*(a[30]*b[31]+a[31]*b[30]);
	res[29]=-1*(a[29]*-1*b[31]+a[31]*b[29]*-1);
	res[28]=1*(a[28]*b[31]+a[31]*b[28]);
	res[27]=-1*(a[27]*-1*b[31]+a[31]*b[27]*-1);
	res[26]=1*(a[26]*b[31]+a[31]*b[26]);
	res[25]=1*(a[25]*b[31]+a[29]*-1*b[30]-a[30]*b[29]*-1+a[31]*b[25]);
	res[24]=-1*(a[24]*-1*b[31]+a[28]*b[30]-a[30]*b[28]+a[31]*b[24]*-1);
	res[23]=1*(a[23]*b[31]+a[27]*-1*b[30]-a[30]*b[27]*-1+a[31]*b[23]);
	res[22]=-1*(a[22]*-1*b[31]+a[26]*b[30]-a[30]*b[26]+a[31]*b[22]*-1);
	res[21]=1*(a[21]*b[31]+a[28]*b[29]*-1-a[29]*-1*b[28]+a[31]*b[21]);
	res[20]=-1*(a[20]*-1*b[31]+a[27]*-1*b[29]*-1-a[29]*-1*b[27]*-1+a[31]*b[20]*-1);
	res[19]=1*(a[19]*b[31]+a[26]*b[29]*-1-a[29]*-1*b[26]+a[31]*b[19]);
	res[18]=1*(a[18]*b[31]+a[27]*-1*b[28]-a[28]*b[27]*-1+a[31]*b[18]);
	res[17]=-1*(a[17]*-1*b[31]+a[26]*b[28]-a[28]*b[26]+a[31]*b[17]*-1);
	res[16]=1*(a[16]*b[31]+a[26]*b[27]*-1-a[27]*-1*b[26]+a[31]*b[16]);
	res[15]=1*(a[15]*b[31]+a[21]*b[30]-a[24]*-1*b[29]*-1+a[25]*b[28]+a[28]*b[25]-a[29]*-1*b[24]*-1+a[30]*b[21]+a[31]*b[15]);
	res[14]=-1*(a[14]*-1*b[31]+a[20]*-1*b[30]-a[23]*b[29]*-1+a[25]*b[27]*-1+a[27]*-1*b[25]-a[29]*-1*b[23]+a[30]*b[20]*-1+a[31]*b[14]*-1);
	res[13]=1*(a[13]*b[31]+a[19]*b[30]-a[22]*-1*b[29]*-1+a[25]*b[26]+a[26]*b[25]-a[29]*-1*b[22]*-1+a[30]*b[19]+a[31]*b[13]);
	res[12]=1*(a[12]*b[31]+a[18]*b[30]-a[23]*b[28]+a[24]*-1*b[27]*-1+a[27]*-1*b[24]*-1-a[28]*b[23]+a[30]*b[18]+a[31]*b[12]);
	res[11]=-1*(a[11]*-1*b[31]+a[17]*-1*b[30]-a[22]*-1*b[28]+a[24]*-1*b[26]+a[26]*b[24]*-1-a[28]*b[22]*-1+a[30]*b[17]*-1+a[31]*b[11]*-1);
	res[10]=1*(a[10]*b[31]+a[16]*b[30]-a[22]*-1*b[27]*-1+a[23]*b[26]+a[26]*b[23]-a[27]*-1*b[22]*-1+a[30]*b[16]+a[31]*b[10]);
	res[9]=-1*(a[9]*-1*b[31]+a[18]*b[29]*-1-a[20]*-1*b[28]+a[21]*b[27]*-1+a[27]*-1*b[21]-a[28]*b[20]*-1+a[29]*-1*b[18]+a[31]*b[9]*-1);
	res[8]=1*(a[8]*b[31]+a[17]*-1*b[29]*-1-a[19]*b[28]+a[21]*b[26]+a[26]*b[21]-a[28]*b[19]+a[29]*-1*b[17]*-1+a[31]*b[8]);
	res[7]=-1*(a[7]*-1*b[31]+a[16]*b[29]*-1-a[19]*b[27]*-1+a[20]*-1*b[26]+a[26]*b[20]*-1-a[27]*-1*b[19]+a[29]*-1*b[16]+a[31]*b[7]*-1);
	res[6]=1*(a[6]*b[31]+a[16]*b[28]-a[17]*-1*b[27]*-1+a[18]*b[26]+a[26]*b[18]-a[27]*-1*b[17]*-1+a[28]*b[16]+a[31]*b[6]);
	res[5]=1*(a[5]*b[31]+a[9]*-1*b[30]-a[12]*b[29]*-1+a[14]*-1*b[28]-a[15]*b[27]*-1+a[18]*b[25]-a[20]*-1*b[24]*-1+a[21]*b[23]+a[23]*b[21]-a[24]*-1*b[20]*-1+a[25]*b[18]+a[27]*-1*b[15]-a[28]*b[14]*-1+a[29]*-1*b[12]-a[30]*b[9]*-1+a[31]*b[5]);
	res[4]=-1*(a[4]*-1*b[31]+a[8]*b[30]-a[11]*-1*b[29]*-1+a[13]*b[28]-a[15]*b[26]+a[17]*-1*b[25]-a[19]*b[24]*-1+a[21]*b[22]*-1+a[22]*-1*b[21]-a[24]*-1*b[19]+a[25]*b[17]*-1+a[26]*b[15]-a[28]*b[13]+a[29]*-1*b[11]*-1-a[30]*b[8]+a[31]*b[4]*-1);
	res[3]=1*(a[3]*b[31]+a[7]*-1*b[30]-a[10]*b[29]*-1+a[13]*b[27]*-1-a[14]*-1*b[26]+a[16]*b[25]-a[19]*b[23]+a[20]*-1*b[22]*-1+a[22]*-1*b[20]*-1-a[23]*b[19]+a[25]*b[16]+a[26]*b[14]*-1-a[27]*-1*b[13]+a[29]*-1*b[10]-a[30]*b[7]*-1+a[31]*b[3]);
	res[2]=-1*(a[2]*-1*b[31]+a[6]*b[30]-a[10]*b[28]+a[11]*-1*b[27]*-1-a[12]*b[26]+a[16]*b[24]*-1-a[17]*-1*b[23]+a[18]*b[22]*-1+a[22]*-1*b[18]-a[23]*b[17]*-1+a[24]*-1*b[16]+a[26]*b[12]-a[27]*-1*b[11]*-1+a[28]*b[10]-a[30]*b[6]+a[31]*b[2]*-1);
	res[1]=1*(a[1]*b[31]+a[6]*b[29]*-1-a[7]*-1*b[28]+a[8]*b[27]*-1-a[9]*-1*b[26]+a[16]*b[21]-a[17]*-1*b[20]*-1+a[18]*b[19]+a[19]*b[18]-a[20]*-1*b[17]*-1+a[21]*b[16]+a[26]*b[9]*-1-a[27]*-1*b[8]+a[28]*b[7]*-1-a[29]*-1*b[6]+a[31]*b[1]);
	res[0]=1*(a[0]*b[31]+a[1]*b[30]-a[2]*-1*b[29]*-1+a[3]*b[28]-a[4]*-1*b[27]*-1+a[5]*b[26]+a[6]*b[25]-a[7]*-1*b[24]*-1+a[8]*b[23]-a[9]*-1*b[22]*-1+a[10]*b[21]-a[11]*-1*b[20]*-1+a[12]*b[19]+a[13]*b[18]-a[14]*-1*b[17]*-1+a[15]*b[16]+a[16]*b[15]-a[17]*-1*b[14]*-1+a[18]*b[13]+a[19]*b[12]-a[20]*-1*b[11]*-1+a[21]*b[10]-a[22]*-1*b[9]*-1+a[23]*b[8]-a[24]*-1*b[7]*-1+a[25]*b[6]+a[26]*b[5]-a[27]*-1*b[4]*-1+a[28]*b[3]-a[29]*-1*b[2]*-1+a[30]*b[1]+a[31]*b[0]);
       */
       //TODO
       return null;
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
        System.out.println("sparsity(a)="+this.mx.sparsity().toString(true));
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
    public iMultivectorSymbolic sub (iMultivectorSymbolic b){
        MX result = MX.minus(mx, ((SparseCGASymbolicMultivector) b).getMX());
        return new SparseCGASymbolicMultivector(result);
    }

    // macht vermutlich nur Sinn für scalars
    public iMultivectorSymbolic mul(iMultivectorSymbolic b){
        MX result = MX.mtimes(mx, ((SparseCGASymbolicMultivector) b).getMX());
        return new SparseCGASymbolicMultivector(result);
    }
    
    /**
     * norm.
     *
     * Calculate the Euclidean norm. (strict positive).
     */
    public iMultivectorSymbolic norm() {
        //return Math.sqrt(Math.abs(binop_Mul(this, this.Conjugate())._mVec[0]));
        //TODO
        return null;
    }

    /**
     * inorm.
     *
     * Calculate the Ideal norm. (signed)
     */
    public iMultivectorSymbolic inorm() {
        //return unop_Dual(this).norm();
        //TODO
        return null;
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
}