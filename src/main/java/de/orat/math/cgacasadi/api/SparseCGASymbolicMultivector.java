package de.orat.math.cgacasadi.api;

import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.orat.math.cgacasadi.CGACayleyTableGeometricProduct;
import de.orat.math.cgacasadi.CGAKVectorSparsity;
import de.orat.math.cgacasadi.CasADiUtil;
import static de.orat.math.cgacasadi.CGACayleyTable.CGABasisBladeNames;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseStringMatrix;

public class SparseCGASymbolicMultivector {
   
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    
    // a multivector is represented by a sparse column vector
    final MatrixSparsity sparsity;
    final MX mx;
    
    /**
     * Creates a k-Vector.
     * 
     * @param name
     * @param grade 
     */
    public SparseCGASymbolicMultivector(String name, int grade){
        sparsity = new CGAKVectorSparsity(CGABasisBladeNames, grade);
        mx = MX.sym(name, CasADiUtil.toCasADiSparsity(sparsity));
    }
    SparseCGASymbolicMultivector(MX mx){
        sparsity = CasADiUtil.toMatrixSparsity(mx.sparsity());
        this.mx = mx;
    }
    public String toString(){
        //TODO
        // eventuell sparsity noch mit ausgeben
        SparseStringMatrix stringMatrix = CasADiUtil.toStringMatrix(mx);
        return stringMatrix.toString(true);
    }
    public MatrixSparsity getSparsity(){
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
    
    public SparseCGASymbolicMultivector binop_Mul (SparseCGASymbolicMultivector b){
        MX gpm = CasADiUtil.toMXProductMatrix(this, CGACayleyTableGeometricProduct.instance());
        System.out.println("product matrix:");
        System.out.println(gpm.toString(true));
        MX result = MX.mtimes(gpm.T(), b.mx);
        return new SparseCGASymbolicMultivector(result);
    }
    
}

