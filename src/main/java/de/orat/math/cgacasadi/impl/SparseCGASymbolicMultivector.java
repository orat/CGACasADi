package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.orat.math.cgacasadi.CGACayleyTableGeometricProduct;
import de.orat.math.cgacasadi.CGACayleyTableOuterProduct;
import de.orat.math.cgacasadi.CGAKVectorSparsity;
import de.orat.math.cgacasadi.CGAMultivectorSparsity;
import de.orat.math.cgacasadi.CGAOperatorMatrixUtils;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;

public class SparseCGASymbolicMultivector implements iMultivectorSymbolic {
   
    private MultivectorSymbolic.Callback callback;
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    final static CGAOperatorMatrixUtils cgaOperatorMatrixUtils = new CGAOperatorMatrixUtils(baseCayleyTable);
    
    // a multivector is represented by a sparse column vector
    final /*ColumnVectorSparsity*/ CGAMultivectorSparsity /*MatrixSparsity*/ sparsity;
    private final MX mx;
    
    public SparseCGASymbolicMultivector instance(String name, int grade){
        return new SparseCGASymbolicMultivector(name, grade);
    }
    /**
     * Creates a k-Vector.
     * 
     * @param name
     * @param grade 
     */
    protected SparseCGASymbolicMultivector(String name, int grade){
        sparsity = CGAKVectorSparsity.instance(grade);
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
    }
    public SparseCGASymbolicMultivector instance(String name, ColumnVectorSparsity sparsity){
        return new SparseCGASymbolicMultivector(name, sparsity);
    }
    protected SparseCGASymbolicMultivector(String name, ColumnVectorSparsity sparsity){
        this.sparsity = new CGAMultivectorSparsity(sparsity);
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
    }
    public SparseCGASymbolicMultivector instance(String name){
        return new SparseCGASymbolicMultivector(name);
    }
    protected SparseCGASymbolicMultivector(String name){
        sparsity = CGAKVectorSparsity.dense();
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
    }
    SparseCGASymbolicMultivector(MX mx){
        sparsity = CasADiUtil.toCGAMultivectorSparsity(mx.sparsity());
        this.mx = mx;
    }
    public SparseCGASymbolicMultivector(){
        sparsity = null;
        mx = null;
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
        return sparsity;
    }
    public MX getMX(){
        return mx;
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
        if (row == -1) throw new IllegalArgumentException("The given bladeName ="+bladeName+" does not exist in the cayley table!");
        if (sparsity.isNonZero(row,0)) return mx.at(row, 0);
        return null;
    }
    
    public int getBladesCount(){
        return baseCayleyTable.getBladesCount();
    }
    
    
    // operators
    
    public iMultivectorSymbolic gp(iMultivectorSymbolic b){
        MX gpm = CasADiUtil.toMXProductMatrix(this, CGACayleyTableGeometricProduct.instance());
        System.out.println("product matrix:");
        System.out.println(gpm.toString(true));
        MX result = MX.mtimes(gpm.T(), ((SparseCGASymbolicMultivector) b).mx);
        return new SparseCGASymbolicMultivector(result);
    }
    
    //TODO das ließe sich auch ohne Matrizen sehr leicht implementieren, indem
    // einfach nur die Vorzeichen des Multivektors entsprechend geändert werden
    // vermutlich wäre das dann auch effizienter
    public iMultivectorSymbolic reverse(){
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getReversionOperatorMatrix();
        System.out.println("Reverse matrix = "+m.toString(false));
        System.out.println(m.toString(true));
        MX rev  = CasADiUtil.toMX(m);
        MX result = MX.mtimes(rev, this.mx);
        return new SparseCGASymbolicMultivector(result);
    }

    /**
     * Dual.
     *
     * Poincare duality operator.
     *
     * @param a
     * @return !a
     */
    public iMultivectorSymbolic dual(){
        /*res[0]=-a[31];
	res[1]=-a[30];
        
	res[2]=a[29];
        
	res[3]=-a[28];
        
	res[4]=a[27];
	res[5]=a[26];
	res[6]=a[25];
        
	res[7]=-a[24];
        
	res[8]=a[23];
	res[9]=a[22];
	res[10]=a[21];
        
	res[11]=-a[20];
	res[12]=-a[19];
        
	res[13]=a[18];
	res[14]=a[17];
        
	res[15]=-a[16];
        
	res[16]=a[15];
        
	res[17]=-a[14];
	res[18]=-a[13];
        
	res[19]=a[12];
	res[20]=a[11];
        
	res[21]=-a[10];
	res[22]=-a[9];
	res[23]=-a[8];
        
	res[24]=a[7];
        
	res[25]=-a[6];
	res[26]=-a[5];
	res[27]=-a[4];
        
	res[28]=a[3];
        
	res[29]=-a[2];
        
	res[30]=a[1];
	res[31]=a[0];*/
        //TODO
        return null;
    }

    /**
     * Conjugate.
     *
     * Clifford Conjugation
     *
     * @param a
     * @return a.Conjugate()
     */
    public iMultivectorSymbolic conjugate(){
        /*res[0]=this._mVec[0];
        
	res[1]=-this._mVec[1];
	res[2]=-this._mVec[2];
	res[3]=-this._mVec[3];
	res[4]=-this._mVec[4];
	res[5]=-this._mVec[5];
	res[6]=-this._mVec[6];
	res[7]=-this._mVec[7];
	res[8]=-this._mVec[8];
	res[9]=-this._mVec[9];
	res[10]=-this._mVec[10];
	res[11]=-this._mVec[11];
	res[12]=-this._mVec[12];
	res[13]=-this._mVec[13];
	res[14]=-this._mVec[14];
	res[15]=-this._mVec[15];
        
	res[16]=this._mVec[16];
	res[17]=this._mVec[17];
	res[18]=this._mVec[18];
	res[19]=this._mVec[19];
	res[20]=this._mVec[20];
	res[21]=this._mVec[21];
	res[22]=this._mVec[22];
	res[23]=this._mVec[23];
	res[24]=this._mVec[24];
	res[25]=this._mVec[25];
	res[26]=this._mVec[26];
	res[27]=this._mVec[27];
	res[28]=this._mVec[28];
	res[29]=this._mVec[29];
	res[30]=this._mVec[30];
        
	res[31]=-this._mVec[31];*/
        //TODO
        return null;
    }

    /**
     * Involute.
     *
     * Main involution
     *
     * @param a
     * @return a.Involute()
     */
    public iMultivectorSymbolic involute (){
        
	/*res[0]=this._mVec[0];
	res[1]=-this._mVec[1];
	res[2]=-this._mVec[2];
	res[3]=-this._mVec[3];
	res[4]=-this._mVec[4];
	res[5]=-this._mVec[5];
	res[6]=this._mVec[6];
	res[7]=this._mVec[7];
	res[8]=this._mVec[8];
	res[9]=this._mVec[9];
	res[10]=this._mVec[10];
	res[11]=this._mVec[11];
	res[12]=this._mVec[12];
	res[13]=this._mVec[13];
	res[14]=this._mVec[14];
	res[15]=this._mVec[15];
	res[16]=-this._mVec[16];
	res[17]=-this._mVec[17];
	res[18]=-this._mVec[18];
	res[19]=-this._mVec[19];
	res[20]=-this._mVec[20];
	res[21]=-this._mVec[21];
	res[22]=-this._mVec[22];
	res[23]=-this._mVec[23];
	res[24]=-this._mVec[24];
	res[25]=-this._mVec[25];
	res[26]=this._mVec[26];
	res[27]=this._mVec[27];
	res[28]=this._mVec[28];
	res[29]=this._mVec[29];
	res[30]=this._mVec[30];
	res[31]=-this._mVec[31];*/
        //TODO
        return null;
    }
    
    public iMultivectorSymbolic op(iMultivectorSymbolic b){
        MX opm = CasADiUtil.toMXProductMatrix(this, CGACayleyTableOuterProduct.instance());
        System.out.println("product matrix:");
        System.out.println(opm.toString(true));
        MX result = MX.mtimes(opm.T(), ((SparseCGASymbolicMultivector) b).mx);
        return new SparseCGASymbolicMultivector(result);
        
	/*res[0]=b[0]*a[0];
	res[1]=b[1]*a[0]+b[0]*a[1];
	res[2]=b[2]*a[0]+b[0]*a[2];
	res[3]=b[3]*a[0]+b[0]*a[3];
	res[4]=b[4]*a[0]+b[0]*a[4];
	res[5]=b[5]*a[0]+b[0]*a[5];
	res[6]=b[6]*a[0]+b[2]*a[1]-b[1]*a[2]+b[0]*a[6];
	res[7]=b[7]*a[0]+b[3]*a[1]-b[1]*a[3]+b[0]*a[7];
	res[8]=b[8]*a[0]+b[4]*a[1]-b[1]*a[4]+b[0]*a[8];
	res[9]=b[9]*a[0]+b[5]*a[1]-b[1]*a[5]+b[0]*a[9];
	res[10]=b[10]*a[0]+b[3]*a[2]-b[2]*a[3]+b[0]*a[10];
	res[11]=b[11]*a[0]+b[4]*a[2]-b[2]*a[4]+b[0]*a[11];
	res[12]=b[12]*a[0]+b[5]*a[2]-b[2]*a[5]+b[0]*a[12];
	res[13]=b[13]*a[0]+b[4]*a[3]-b[3]*a[4]+b[0]*a[13];
	res[14]=b[14]*a[0]+b[5]*a[3]-b[3]*a[5]+b[0]*a[14];
	res[15]=b[15]*a[0]+b[5]*a[4]-b[4]*a[5]+b[0]*a[15];
	res[16]=b[16]*a[0]+b[10]*a[1]-b[7]*a[2]+b[6]*a[3]+b[3]*a[6]-b[2]*a[7]+b[1]*a[10]+b[0]*a[16];
	res[17]=b[17]*a[0]+b[11]*a[1]-b[8]*a[2]+b[6]*a[4]+b[4]*a[6]-b[2]*a[8]+b[1]*a[11]+b[0]*a[17];
	res[18]=b[18]*a[0]+b[12]*a[1]-b[9]*a[2]+b[6]*a[5]+b[5]*a[6]-b[2]*a[9]+b[1]*a[12]+b[0]*a[18];
	res[19]=b[19]*a[0]+b[13]*a[1]-b[8]*a[3]+b[7]*a[4]+b[4]*a[7]-b[3]*a[8]+b[1]*a[13]+b[0]*a[19];
	res[20]=b[20]*a[0]+b[14]*a[1]-b[9]*a[3]+b[7]*a[5]+b[5]*a[7]-b[3]*a[9]+b[1]*a[14]+b[0]*a[20];
	res[21]=b[21]*a[0]+b[15]*a[1]-b[9]*a[4]+b[8]*a[5]+b[5]*a[8]-b[4]*a[9]+b[1]*a[15]+b[0]*a[21];
	res[22]=b[22]*a[0]+b[13]*a[2]-b[11]*a[3]+b[10]*a[4]+b[4]*a[10]-b[3]*a[11]+b[2]*a[13]+b[0]*a[22];
	res[23]=b[23]*a[0]+b[14]*a[2]-b[12]*a[3]+b[10]*a[5]+b[5]*a[10]-b[3]*a[12]+b[2]*a[14]+b[0]*a[23];
	res[24]=b[24]*a[0]+b[15]*a[2]-b[12]*a[4]+b[11]*a[5]+b[5]*a[11]-b[4]*a[12]+b[2]*a[15]+b[0]*a[24];
	res[25]=b[25]*a[0]+b[15]*a[3]-b[14]*a[4]+b[13]*a[5]+b[5]*a[13]-b[4]*a[14]+b[3]*a[15]+b[0]*a[25];
	res[26]=b[26]*a[0]+b[22]*a[1]-b[19]*a[2]+b[17]*a[3]-b[16]*a[4]+b[13]*a[6]-b[11]*a[7]+b[10]*a[8]+b[8]*a[10]-b[7]*a[11]+b[6]*a[13]+b[4]*a[16]-b[3]*a[17]+b[2]*a[19]-b[1]*a[22]+b[0]*a[26];
	res[27]=b[27]*a[0]+b[23]*a[1]-b[20]*a[2]+b[18]*a[3]-b[16]*a[5]+b[14]*a[6]-b[12]*a[7]+b[10]*a[9]+b[9]*a[10]-b[7]*a[12]+b[6]*a[14]+b[5]*a[16]-b[3]*a[18]+b[2]*a[20]-b[1]*a[23]+b[0]*a[27];
	res[28]=b[28]*a[0]+b[24]*a[1]-b[21]*a[2]+b[18]*a[4]-b[17]*a[5]+b[15]*a[6]-b[12]*a[8]+b[11]*a[9]+b[9]*a[11]-b[8]*a[12]+b[6]*a[15]+b[5]*a[17]-b[4]*a[18]+b[2]*a[21]-b[1]*a[24]+b[0]*a[28];
	res[29]=b[29]*a[0]+b[25]*a[1]-b[21]*a[3]+b[20]*a[4]-b[19]*a[5]+b[15]*a[7]-b[14]*a[8]+b[13]*a[9]+b[9]*a[13]-b[8]*a[14]+b[7]*a[15]+b[5]*a[19]-b[4]*a[20]+b[3]*a[21]-b[1]*a[25]+b[0]*a[29];
	res[30]=b[30]*a[0]+b[25]*a[2]-b[24]*a[3]+b[23]*a[4]-b[22]*a[5]+b[15]*a[10]-b[14]*a[11]+b[13]*a[12]+b[12]*a[13]-b[11]*a[14]+b[10]*a[15]+b[5]*a[22]-b[4]*a[23]+b[3]*a[24]-b[2]*a[25]+b[0]*a[30];
	res[31]=b[31]*a[0]+b[30]*a[1]-b[29]*a[2]+b[28]*a[3]-b[27]*a[4]+b[26]*a[5]+b[25]*a[6]-b[24]*a[7]+b[23]*a[8]-b[22]*a[9]+b[21]*a[10]-b[20]*a[11]+b[19]*a[12]+b[18]*a[13]-b[17]*a[14]+b[16]*a[15]+b[15]*a[16]-b[14]*a[17]+b[13]*a[18]+b[12]*a[19]-b[11]*a[20]+b[10]*a[21]-b[9]*a[22]+b[8]*a[23]-b[7]*a[24]+b[6]*a[25]+b[5]*a[26]-b[4]*a[27]+b[3]*a[28]-b[2]*a[29]+b[1]*a[30]+b[0]*a[31];
        */
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
     * Dot.
     *
     * The inner product.
     *
     * @param a
     * @param b
     * @return a | b
     */
    public iMultivectorSymbolic dot (iMultivectorSymbolic b){
        
	/*res[0]=b[0]*a[0]+b[1]*a[1]+b[2]*a[2]+b[3]*a[3]+b[4]*a[4]-b[5]*a[5]-b[6]*a[6]-b[7]*a[7]-b[8]*a[8]+b[9]*a[9]-b[10]*a[10]-b[11]*a[11]+b[12]*a[12]-b[13]*a[13]+b[14]*a[14]+b[15]*a[15]-b[16]*a[16]-b[17]*a[17]+b[18]*a[18]-b[19]*a[19]+b[20]*a[20]+b[21]*a[21]-b[22]*a[22]+b[23]*a[23]+b[24]*a[24]+b[25]*a[25]+b[26]*a[26]-b[27]*a[27]-b[28]*a[28]-b[29]*a[29]-b[30]*a[30]-b[31]*a[31];
	res[1]=b[1]*a[0]+b[0]*a[1]-b[6]*a[2]-b[7]*a[3]-b[8]*a[4]+b[9]*a[5]+b[2]*a[6]+b[3]*a[7]+b[4]*a[8]-b[5]*a[9]-b[16]*a[10]-b[17]*a[11]+b[18]*a[12]-b[19]*a[13]+b[20]*a[14]+b[21]*a[15]-b[10]*a[16]-b[11]*a[17]+b[12]*a[18]-b[13]*a[19]+b[14]*a[20]+b[15]*a[21]+b[26]*a[22]-b[27]*a[23]-b[28]*a[24]-b[29]*a[25]-b[22]*a[26]+b[23]*a[27]+b[24]*a[28]+b[25]*a[29]-b[31]*a[30]-b[30]*a[31];
	res[2]=b[2]*a[0]+b[6]*a[1]+b[0]*a[2]-b[10]*a[3]-b[11]*a[4]+b[12]*a[5]-b[1]*a[6]+b[16]*a[7]+b[17]*a[8]-b[18]*a[9]+b[3]*a[10]+b[4]*a[11]-b[5]*a[12]-b[22]*a[13]+b[23]*a[14]+b[24]*a[15]+b[7]*a[16]+b[8]*a[17]-b[9]*a[18]-b[26]*a[19]+b[27]*a[20]+b[28]*a[21]-b[13]*a[22]+b[14]*a[23]+b[15]*a[24]-b[30]*a[25]+b[19]*a[26]-b[20]*a[27]-b[21]*a[28]+b[31]*a[29]+b[25]*a[30]+b[29]*a[31];
	res[3]=b[3]*a[0]+b[7]*a[1]+b[10]*a[2]+b[0]*a[3]-b[13]*a[4]+b[14]*a[5]-b[16]*a[6]-b[1]*a[7]+b[19]*a[8]-b[20]*a[9]-b[2]*a[10]+b[22]*a[11]-b[23]*a[12]+b[4]*a[13]-b[5]*a[14]+b[25]*a[15]-b[6]*a[16]+b[26]*a[17]-b[27]*a[18]+b[8]*a[19]-b[9]*a[20]+b[29]*a[21]+b[11]*a[22]-b[12]*a[23]+b[30]*a[24]+b[15]*a[25]-b[17]*a[26]+b[18]*a[27]-b[31]*a[28]-b[21]*a[29]-b[24]*a[30]-b[28]*a[31];
	res[4]=b[4]*a[0]+b[8]*a[1]+b[11]*a[2]+b[13]*a[3]+b[0]*a[4]+b[15]*a[5]-b[17]*a[6]-b[19]*a[7]-b[1]*a[8]-b[21]*a[9]-b[22]*a[10]-b[2]*a[11]-b[24]*a[12]-b[3]*a[13]-b[25]*a[14]-b[5]*a[15]-b[26]*a[16]-b[6]*a[17]-b[28]*a[18]-b[7]*a[19]-b[29]*a[20]-b[9]*a[21]-b[10]*a[22]-b[30]*a[23]-b[12]*a[24]-b[14]*a[25]+b[16]*a[26]+b[31]*a[27]+b[18]*a[28]+b[20]*a[29]+b[23]*a[30]+b[27]*a[31];
	res[5]=b[5]*a[0]+b[9]*a[1]+b[12]*a[2]+b[14]*a[3]+b[15]*a[4]+b[0]*a[5]-b[18]*a[6]-b[20]*a[7]-b[21]*a[8]-b[1]*a[9]-b[23]*a[10]-b[24]*a[11]-b[2]*a[12]-b[25]*a[13]-b[3]*a[14]-b[4]*a[15]-b[27]*a[16]-b[28]*a[17]-b[6]*a[18]-b[29]*a[19]-b[7]*a[20]-b[8]*a[21]-b[30]*a[22]-b[10]*a[23]-b[11]*a[24]-b[13]*a[25]+b[31]*a[26]+b[16]*a[27]+b[17]*a[28]+b[19]*a[29]+b[22]*a[30]+b[26]*a[31];
	res[6]=b[6]*a[0]+b[16]*a[3]+b[17]*a[4]-b[18]*a[5]+b[0]*a[6]-b[26]*a[13]+b[27]*a[14]+b[28]*a[15]+b[3]*a[16]+b[4]*a[17]-b[5]*a[18]+b[31]*a[25]-b[13]*a[26]+b[14]*a[27]+b[15]*a[28]+b[25]*a[31];
	res[7]=b[7]*a[0]-b[16]*a[2]+b[19]*a[4]-b[20]*a[5]+b[0]*a[7]+b[26]*a[11]-b[27]*a[12]+b[29]*a[15]-b[2]*a[16]+b[4]*a[19]-b[5]*a[20]-b[31]*a[24]+b[11]*a[26]-b[12]*a[27]+b[15]*a[29]-b[24]*a[31];
	res[8]=b[8]*a[0]-b[17]*a[2]-b[19]*a[3]-b[21]*a[5]+b[0]*a[8]-b[26]*a[10]-b[28]*a[12]-b[29]*a[14]-b[2]*a[17]-b[3]*a[19]-b[5]*a[21]+b[31]*a[23]-b[10]*a[26]-b[12]*a[28]-b[14]*a[29]+b[23]*a[31];
	res[9]=b[9]*a[0]-b[18]*a[2]-b[20]*a[3]-b[21]*a[4]+b[0]*a[9]-b[27]*a[10]-b[28]*a[11]-b[29]*a[13]-b[2]*a[18]-b[3]*a[20]-b[4]*a[21]+b[31]*a[22]-b[10]*a[27]-b[11]*a[28]-b[13]*a[29]+b[22]*a[31];
	res[10]=b[10]*a[0]+b[16]*a[1]+b[22]*a[4]-b[23]*a[5]-b[26]*a[8]+b[27]*a[9]+b[0]*a[10]+b[30]*a[15]+b[1]*a[16]+b[31]*a[21]+b[4]*a[22]-b[5]*a[23]-b[8]*a[26]+b[9]*a[27]+b[15]*a[30]+b[21]*a[31];
	res[11]=b[11]*a[0]+b[17]*a[1]-b[22]*a[3]-b[24]*a[5]+b[26]*a[7]+b[28]*a[9]+b[0]*a[11]-b[30]*a[14]+b[1]*a[17]-b[31]*a[20]-b[3]*a[22]-b[5]*a[24]+b[7]*a[26]+b[9]*a[28]-b[14]*a[30]-b[20]*a[31];
	res[12]=b[12]*a[0]+b[18]*a[1]-b[23]*a[3]-b[24]*a[4]+b[27]*a[7]+b[28]*a[8]+b[0]*a[12]-b[30]*a[13]+b[1]*a[18]-b[31]*a[19]-b[3]*a[23]-b[4]*a[24]+b[7]*a[27]+b[8]*a[28]-b[13]*a[30]-b[19]*a[31];
	res[13]=b[13]*a[0]+b[19]*a[1]+b[22]*a[2]-b[25]*a[5]-b[26]*a[6]+b[29]*a[9]+b[30]*a[12]+b[0]*a[13]+b[31]*a[18]+b[1]*a[19]+b[2]*a[22]-b[5]*a[25]-b[6]*a[26]+b[9]*a[29]+b[12]*a[30]+b[18]*a[31];
	res[14]=b[14]*a[0]+b[20]*a[1]+b[23]*a[2]-b[25]*a[4]-b[27]*a[6]+b[29]*a[8]+b[30]*a[11]+b[0]*a[14]+b[31]*a[17]+b[1]*a[20]+b[2]*a[23]-b[4]*a[25]-b[6]*a[27]+b[8]*a[29]+b[11]*a[30]+b[17]*a[31];
	res[15]=b[15]*a[0]+b[21]*a[1]+b[24]*a[2]+b[25]*a[3]-b[28]*a[6]-b[29]*a[7]-b[30]*a[10]+b[0]*a[15]-b[31]*a[16]+b[1]*a[21]+b[2]*a[24]+b[3]*a[25]-b[6]*a[28]-b[7]*a[29]-b[10]*a[30]-b[16]*a[31];
	res[16]=b[16]*a[0]-b[26]*a[4]+b[27]*a[5]+b[31]*a[15]+b[0]*a[16]+b[4]*a[26]-b[5]*a[27]+b[15]*a[31];
	res[17]=b[17]*a[0]+b[26]*a[3]+b[28]*a[5]-b[31]*a[14]+b[0]*a[17]-b[3]*a[26]-b[5]*a[28]-b[14]*a[31];
	res[18]=b[18]*a[0]+b[27]*a[3]+b[28]*a[4]-b[31]*a[13]+b[0]*a[18]-b[3]*a[27]-b[4]*a[28]-b[13]*a[31];
	res[19]=b[19]*a[0]-b[26]*a[2]+b[29]*a[5]+b[31]*a[12]+b[0]*a[19]+b[2]*a[26]-b[5]*a[29]+b[12]*a[31];
	res[20]=b[20]*a[0]-b[27]*a[2]+b[29]*a[4]+b[31]*a[11]+b[0]*a[20]+b[2]*a[27]-b[4]*a[29]+b[11]*a[31];
	res[21]=b[21]*a[0]-b[28]*a[2]-b[29]*a[3]-b[31]*a[10]+b[0]*a[21]+b[2]*a[28]+b[3]*a[29]-b[10]*a[31];
	res[22]=b[22]*a[0]+b[26]*a[1]+b[30]*a[5]-b[31]*a[9]+b[0]*a[22]-b[1]*a[26]-b[5]*a[30]-b[9]*a[31];
	res[23]=b[23]*a[0]+b[27]*a[1]+b[30]*a[4]-b[31]*a[8]+b[0]*a[23]-b[1]*a[27]-b[4]*a[30]-b[8]*a[31];
	res[24]=b[24]*a[0]+b[28]*a[1]-b[30]*a[3]+b[31]*a[7]+b[0]*a[24]-b[1]*a[28]+b[3]*a[30]+b[7]*a[31];
	res[25]=b[25]*a[0]+b[29]*a[1]+b[30]*a[2]-b[31]*a[6]+b[0]*a[25]-b[1]*a[29]-b[2]*a[30]-b[6]*a[31];
	res[26]=b[26]*a[0]-b[31]*a[5]+b[0]*a[26]-b[5]*a[31];
	res[27]=b[27]*a[0]-b[31]*a[4]+b[0]*a[27]-b[4]*a[31];
	res[28]=b[28]*a[0]+b[31]*a[3]+b[0]*a[28]+b[3]*a[31];
	res[29]=b[29]*a[0]-b[31]*a[2]+b[0]*a[29]-b[2]*a[31];
	res[30]=b[30]*a[0]+b[31]*a[1]+b[0]*a[30]+b[1]*a[31];
	res[31]=b[31]*a[0]+b[0]*a[31];
        CGA res_ret = new CGA(CGA.Empty);
        res_ret._mVec = res;
        return res_ret;*/
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

    public iMultivectorSymbolic gradeInversion(){
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
}

