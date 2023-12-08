package de.orat.math.cgacasadi;

import static de.orat.math.cgacasadi.CGACayleyTable.CGABasisBladeNames;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAMultivectorSparsity extends ColumnVectorSparsity {
    
    public static CGAMultivectorSparsity dense(){
        return new CGAMultivectorSparsity(createDenseRows(CGABasisBladeNames.length));
    }
    
    /**
     * It is allowed to have nonzeros indizes corresponding to different grades.
     * 
     * @param nonzeros nonzeros indizes of the nonzeros
     */
    public CGAMultivectorSparsity(int[] nonzeros) {
        super(CGABasisBladeNames.length, nonzeros);
    }
    
    public CGAMultivectorSparsity(double[] values) {
        super(values);
    }
    
    CGAMultivectorSparsity(ColumnVectorSparsity sparsity){
        super(CGABasisBladeNames.length, sparsity.getrow());
        if (sparsity.getn_row() != CGACayleyTable.CGABasisBladeNames.length){
            throw new IllegalArgumentException("The column vector sparsity argument has not the needed length to reprsent a CGA multivector!");
        }
    }
     
    public CGAMultivectorSparsity intersect(CGAMultivectorSparsity sparsity){
        return new CGAMultivectorSparsity(super.intersect(sparsity));
    }
    public int[] getGrades(){
        List<Integer> grades = new ArrayList<>();
        int[] rows = getrow();
        for (int i=0;i<rows.length;i++){
            int grade = CGACayleyTable.getGrade(rows[i]);
            if (!grades.contains(grade)) grades.add(grade);
        }
        return grades.stream().mapToInt(d -> d).toArray();
    }
    
    public static CGAKVectorSparsity createSparsity(int[] nonzeros){
        return new CGAKVectorSparsity(nonzeros);
    }
}
