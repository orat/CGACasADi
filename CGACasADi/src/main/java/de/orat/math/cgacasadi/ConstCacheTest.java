package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;

public class ConstCacheTest {

    public static void main(String[] args) {
        var sx = new SX(new Sparsity(1, 1));
        System.out.println(sx);

        var con = CGAExprGraphFactory.instance.constantsNumeric();
        con.testCache();
        var a = con.getPi();
        con.testCache();
        var b = con.getPi();
        con.testCache();
        System.out.println(a.add(b));
    }
}
