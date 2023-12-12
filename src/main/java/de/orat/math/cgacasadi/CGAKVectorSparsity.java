package de.orat.math.cgacasadi;

import static de.orat.math.cgacasadi.CGACayleyTable.CGABasisBladeNames;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
  * @author Oliver Rettig (Oliver.Rettig@orat.de)
  */
public class CGAKVectorSparsity extends CGAMultivectorSparsity {
    
    public static Map<Integer, CGAKVectorSparsity> map = new HashMap<>();
    private int grade = -1;
    
    public static CGAKVectorSparsity instance(int grade){
        CGAKVectorSparsity result = map.get(grade);
        if (result == null){
            result = new CGAKVectorSparsity(grade);
            map.put(grade, result);
        }
        return result;
    }
    
    public int getGrade(){
        return grade;
    }
    /**
     * Creates a sparse definition for a basis blade
     * 
      * @param index column index of the base blade
     */
    public static CGAKVectorSparsity createBasisBladeSparsity(int index){
        // so hats mal funktioniert mit extends MatrixSparsity
        // int n_row, int n_col, int[] colind, int[] row
        //super(basisBladeNames.length, 1, new int[]{0,1}, new int[]{index});
        
        //super(basisBladeNames.length,  new int[]{index});
        
        return new CGAKVectorSparsity(new int[]{index});
    }
    
    /**
     * Get the grade corresponding to the given indizes.
     * 
     * @param indizes
     * @return -1 if the given indizes do not correspond to a single grade, elese the grade
     */
    private static int getGrade(int[] indizes){
        int result = CGACayleyTable.getCGAGrade(indizes[0]);
        for (int i=1;i<indizes.length;i++){
            if (CGACayleyTable.getCGAGrade(indizes[i]) != result){
                return -1;
            }
        }
        return result;
    }
    
    /**
     * 
     * @param n_row
     * @param row 
     * @throws IllegalArgumentException if the given row-indizes does not correspond all to the same grade.
     */
    CGAKVectorSparsity(int[] row){
        super(row);
        grade = getGrade(row);
        if (grade <0) throw new IllegalArgumentException("The given indizes correspond to different grades!");
    }
    
    /**
     * Creates a sparse definition for a colum k-vector.
     * 
     * @param basisBladeNames 
     * @param grade grade of the k-vector
     */
    CGAKVectorSparsity(int grade){
        // so hats mal funktioniert:
        // int n_row, int n_col, int[] colind, int[] row
        //super(CGABasisBladeNames.length, 1, new int[]{0,colind(grade)}, rows(CGABasisBladeNames, 
        //      grade, colind(grade)));
        
        super(rows(CGABasisBladeNames, 
              grade, colind(grade)));
        this.grade = grade;
    }
    
    /**
     * Count of blades for the given grade - corresponding to the second value of 
     * the accumulated column indizes.
     * 
     * Das ist CGA spezifisch - müsste es aber nicht sein. Aus der Zahl der 
     * basisBladeNames könnte die Zahl der basisvektoren bestimmt werden und daraus
     * die Zahl der Basisvektoren eines bestimmten Grades.
     * TODO
     * 
     * @param grade
     * @return count of blades with the given grade
     * @throws IllegalArgumentException if the grade is not available for CGA
     */
    private static int colind(int grade){
        int result;
        switch (grade){
            case 0:
            case 5:
                result = 1;
                break;
            case 1:
                result = 5;
                break;
            case 2:
            case 3:
                result = 10;
                break;
            case 4:
                result = 5;
                break;
            default:
                throw new IllegalArgumentException("In CGA only 0<=grade<=5 is possible!");
        }
        return result;
    }
   
    // scheint jetzt zu stimmen zumindest für grade == 1 getestet
    private static int[] rows(String[] basisBladeNames, int grade, int colind){
        int[] rows = new int[colind];
        int charcount = grade;
        if (charcount >0) charcount++;
        int j=0;
        for (int i=0;i<basisBladeNames.length;i++){
            if (basisBladeNames[i].length() == charcount) rows[j++] =i;
        }
        return rows;
    }
}