package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;

public class LittleTest {

    public static void main(String[] args) {
        CGAExprGraphFactory fac = CGAExprGraphFactory.instance;
        var a = fac.createMultivectorPurelySymbolic("a", 0);
        // var b = a.scalarInverse();
        // var b = a.scp(a);
        // var b = a.scalarCos();
        // var b = a.scalarAtan2(a);
        // System.out.println(a);
        // System.out.println(b);

        var x = fac.createMultivectorNumeric(17);
        System.out.println(x);
    }
}
