package de.orat.math.cgacasadi;

import de.orat.math.sparsematrix.MatrixSparsity;

/**
  * @author Oliver Rettig (Oliver.Rettig@orat.de)
  * 
  * TODO
  * vielleicht läßt sich eine generische class
  * SparsityKVector implementieren von ich hier dann erben kann
  * vermutlich kann die ganze Klasse genenerisch für GAs mit beliebiger Anzahl
  * an Basisvektoren formuliert werden
  * --> SparsityKVector
  */
public class CGAKVectorSparsity extends MatrixSparsity {
    
    /**
     * Creates a sparse definition for a colum k-vector.
     * 
     * @param basisBladeNames 
     * @param grade grade of the k-vector
     */
    public CGAKVectorSparsity(String[] basisBladeNames, int grade){
        // int n_row, int n_col, int[] colind, int[] row
        super(basisBladeNames.length, 1, new int[]{0,colind(grade)}, rows(basisBladeNames, 
              grade, colind(grade)));
    }
    
    /**
     * Count of blades for the given grade - corresponding to the second value of 
     * the accumulated column indizes.
     * 
     * Das ist CGA spezifisch - müsste es aber nicht sein. Aus der Zahl der 
     * basisBladeNames könnte die Zahl der basisvektoren bestimmt werden und daraus
     * die Zahl der Baisvektoren eines bestimmten Grades.
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