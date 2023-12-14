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
public class NewEmptyJUnitTest {
    
    public NewEmptyJUnitTest() {
    }
    
    @Test
    public void testMatrixExpr(){
        MX x = MX.sym("x", 2);
	MX A = new MX(2, 2);
	A.at(0, 0).assign(x.at(0));
	A.at(1, 1).assign(MX.mtimes(x.at(0), new MX(3)));
                
                // A:(project((zeros(2x2,1nz)[0] = x[0]))[1] = (x[0]+x[1]))
	System.out.println("A:" + A);
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
    
    @Test
    public void test() {
        SparseCGASymbolicMultivector fac = new SparseCGASymbolicMultivector();
        SparseCGASymbolicMultivector mva = fac.instance("a", 1);
        System.out.println("a (sparsity): "+mva.getSparsity().toString());
        System.out.println("a: "+mva.toString());
        SparseCGASymbolicMultivector mvb = fac.instance("b", 1);
        iMultivectorSymbolic result = mva.gp(mvb);
        System.out.println("result (sym): "+result.toString());
        StdVectorMX args = new StdVectorMX(new MX[]{mva.getMX(), mvb.getMX()});
        StdVectorMX res = new StdVectorMX(new MX[]{mvb.getMX()});
        Function gp = new Function("gp",args, res);
        StdVectorDM resu = new StdVectorDM();
        // hier zwei Multivektoren als 32x1 vec erzeugen, von denen dann als
        // test das gp bestimmt wird
        // um die Eingabedaten leichter zu erzeugen die Möglichkeit bauen Expressions
        // wie "e1+3e12" in einen 32x1 array zu parsen?
        // das GP dann alternativ mit einer ganja Implementierung berechnen und
        // vergleichen
        //TODO
        StdVectorDouble a_val = new StdVectorDouble(new double[]{10.0, 5.0});
        System.out.println(a_val.toString());
        StdVectorDouble b_val = new StdVectorDouble(new double[]{10.0, 5.0});
        //StdVectorDM argu = new StdVectorDM(new DM[]{new DM(a_val), new DM(b_val)});
        //gp.call(argu, resu);
    }
}