package de.orat.math.cgacasadi;

import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;
import java.util.Objects;

public class SparsityComplexPropagationTest {
    public static void main(String[] args) {
        var fac = CGAExprGraphFactory.instance;
        var a = fac.createMultivectorPurelySymbolic("a", 0);
        var b = fac.createMultivectorPurelySymbolic("b", 0);
        var sub1 = a.sub(b);
        var sub2 = a.square().sub(a.square());
        System.out.println(a);
        System.out.println(sub1);
        System.out.println(sub2);
        System.out.println(Objects.toIdentityString(a.square()));
        System.out.println(Objects.toIdentityString(a.square()));
        var c = fac.createMultivectorNumeric(5.5);
        var subNum = c.square().sub(c.square());
        var nonZeroExpr = c.square();
        System.out.println(c);
        System.out.println(subNum);
        System.out.println(nonZeroExpr);
    }
}
