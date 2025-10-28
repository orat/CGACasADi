package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;
import de.orat.math.cgacasadi.impl.gen.CachedSparseCGASymbolicMultivector;
import util.cga.SparseCGAColumnVector;

public class SparsityComplexPropagationTest {

    public static void main(String[] args) {
        var fac = CGAExprGraphFactory.instance;
        var a = fac.createMultivectorPurelySymbolic("a", 0);
        var b = fac.createMultivectorPurelySymbolic("b", 0);

        SX withScalar = a.add(a.gpWithScalar(-2)).add(a).getSX();
        System.out.println(withScalar); // Strukturelle Null.

        var yy = fac.createMultivectorNumeric(3);
        var zz = fac.createMultivectorNumeric(3);

        var yyOut = yy.add(yy.gpWithScalar(-2)).add(yy);
        System.out.println(yyOut); // Strukturelle Null

        var yyzzOut = yy.add(zz.gpWithScalar(-2)).add(yy);
        System.out.println(yyzzOut); // Numerische Null

        ////////////

        var zero = new SX(1, 1);
        var value = SxStatic.sym("value", 1, 1);
        var minusFunc = SxStatic.minus(zero, value);
        System.out.println(minusFunc);

        var xNum = new SX(12);
        var xSym = SxStatic.sym("xSym", 1, 1);
        System.out.println(xNum);
        System.out.println(xSym);
        var resNum = SxStatic.minus(xNum, xNum);
        var resSym = SxStatic.minus(xSym, xSym);
        System.out.println(resNum); // Numerische Null.
        System.out.println(resSym); // Numerische Null.

        ////////////

        var withoutNum = a.sub(a);
        System.out.println(withoutNum.toString()); // SymbolicMV printet strukturelle Null
        System.out.println(withoutNum.getSX()); // SX printet strukturelle Null.

        ////////////

        /*
        SparseCGAColumnVector pVec = SparseCGAColumnVector.createEuclid(new double[]{0.5, 0.5, 0.5});
        var p = fac.createMultivectorNumeric(pVec);
        var translator = fac.createMultivectorNumeric(-0.5).gp(p).gp(fac.constantsNumeric().getBaseVectorInfinity());
        //translator.getSX().at(10).assign(new SX(0.1));
        //var translator = fac.createMultivectorPurelySymbolic("bivec", 2);
        var exp = translator.exp();
        System.out.println(p);
        System.out.println(translator);
        System.out.println(exp);
        */

        ////////////

        String caching = CachedSparseCGASymbolicMultivector.getCache().cachedFunctionUsageToString();
        System.out.println(caching);
    }
}
