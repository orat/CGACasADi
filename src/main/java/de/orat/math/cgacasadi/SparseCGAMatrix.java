package de.orat.math.cgacasadi;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.MatrixSparsity;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class SparseCGAMatrix extends SparseDoubleMatrix {
    
    public SparseCGAMatrix(SparseCGAKVector sparseCGAKVector){
        super(createMatrixSparsity(sparseCGAKVector.getSparsity()), createValues());
    }
    
    private static MatrixSparsity createMatrixSparsity(MatrixSparsity sparsity){
        //TODO
        return null;
    }
    
    private static double[][] createValues(){
        return null;
        //TODO
    }
    
    public static int[][] multMatrix(int a[][], int b[][]){//a[m][n], b[n][p]
        if(a.length == 0) return new int[0][0];
        if(a[0].length != b.length) return null; //invalid dims

        int n = a[0].length;
        int m = a.length;
        int p = b[0].length;
        int ans[][] = new int[m][p];

        for(int i = 0;i < m;i++){
           for(int j = 0;j < p;j++){
              for(int k = 0;k < n;k++){
                 ans[i][j] += a[i][k] * b[k][j];
              }
           }
        }
        return ans;
   }
}
