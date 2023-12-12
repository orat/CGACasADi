package de.orat.math.cgacasadi;

import de.orat.math.ga.matrix.utils.CayleyTable;
import java.util.ArrayList;
import java.util.List;

/**
 * https://discourse.bivector.net/t/matrix-representation/232/4
 * 
 * Cayley-Table for CGA generated with ganja.js codegenerator.<p>
 * 
 * make GEN_LANG="java" cga<p>
 * 
 * The brackets are substituted with the replace-functionality of the editor.
 *
 * https://gitlab.com/jordibc/geometric-algebra/-/tree/main?ref_type=heads
 * 
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public abstract class CGACayleyTable extends CayleyTable {
 
    public final static String[] CGABasisBladeNames = new String[]{
        "1",     "e1",    "e2",    "e3",
        "e4",    "e5",    "e12",   "e13",
        "e14",   "e15",   "e23",   "e24",
        "e25",   "e34",   "e35",   "e45",
        "e123",  "e124",  "e125",  "e134",
        "e135",  "e145",  "e234",  "e235",
        "e245",  "e345",  "e1234", "e1235",
        "e1245", "e1345", "e2345", "e12345" };

    public static int getCGAGrade(int index){
        return CGABasisBladeNames[index].length()-1;
    }
     
    CGACayleyTable(String[][] cgaTable){
        super(cgaTable, CGABasisBladeNames);
    }
    
    
    public static int[] getIndizes(int grade){
        List<Integer> result = new ArrayList<>();
        for (int i=0;i<CGABasisBladeNames.length;i++){
            if (CGABasisBladeNames[i].length()-1 == grade){
                result.add(i);
            }
        }
        return result.stream().mapToInt(d -> d).toArray();
    }
    
    public static String getBasisBladeName(int index){
        return CGABasisBladeNames[index];
    }
}
