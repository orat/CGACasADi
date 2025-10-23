package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;

public class SparsityComplexPropagationTest {

    public static void main(String[] args) {
        var fac = CGAExprGraphFactory.instance;
        var a = fac.createMultivectorPurelySymbolic("a", 0);
        var b = fac.createMultivectorPurelySymbolic("b", 0);

        var withoutNum = a.sub(a);  // Evaluiert zu struktureller Null.
        System.out.println(withoutNum);

        SX withScalar = a.add(a.gpWithScalar(-2)).add(a).getSX();
        SX simpleSX = SxStatic.simplify(withScalar); // Evaluiert zu numerischer Null.
        System.out.println(withScalar);
        System.out.println(simpleSX);

        SX onlyPure = a.add(a.gpWithScalar(-1)).getSX(); // Evaluiert zu numerischer Null.
        SX onlyPureSym = SxStatic.simplify(onlyPure);
        System.out.println(onlyPure);
        System.out.println(onlyPureSym);
    }
}
