package de.orat.math.cgacasadi;

import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorMX;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.SparseStringMatrix;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class UtilsTest {
    
    public UtilsTest() {
    }
    
    @Test
    public void testMatrixExpr(){
        MX x = MX.sym("x", 2);
	MX A = new MX(2, 2);
	A.at(0, 0).assign(x.at(0));
	A.at(1, 1).assign(MX.mtimes(x.at(0), new MX(3)));
                
        // A:(project((zeros(2x2,1nz)[0] = x[0]))[1] = (x[0]+x[1]))
	System.out.println("A:" + A);
        //FIXME
        // A[0][0]=(project((zeros(2x2,1nz)[0] = x[0]))[1] = (3*x[0]))[0]
        // statt den Ausdruck für die Zelle bekomme ich den Inhalt der gesamten Matrix
        System.out.println("A[0][0]="+A.at(0, 0));
    }

    public void testCGAKSparsity(){
        CGACayleyTableGeometricProduct table = CGACayleyTableGeometricProduct.instance();
        String[] basisBladeNames = table.getBasisBladeNames();
        int size = basisBladeNames.length;
        CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(1);
        
        
        String[][] m = new String[size][size];
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                m[i][j] = "*";
            }
        }
        SparseStringMatrix sm = new SparseStringMatrix(sparsity, m);
        System.out.println(sm.toString(true));
        System.out.println(sm.toString(false));
    }
}