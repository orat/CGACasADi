package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAOperatorMatrixUtils;

public class SparsityComplexPropagationTest {

    public static void main(String[] args) {
        var fac = CGAExprGraphFactory.instance;
        var a = fac.createMultivectorPurelySymbolic("a", 0);
        var b = fac.createMultivectorPurelySymbolic("b", 0);

        /*
        SX withScalar = a.add(a.gpWithScalar(-2)).add(a).getSX();
        SX simpleSX = SxStatic.simplify(withScalar); // Evaluiert zu numerischer Null.
        System.out.println(withScalar);
        System.out.println(simpleSX);

        SX onlyPure = a.add(a.gpWithScalar(-1)).getSX(); // Evaluiert zu numerischer Null.
        SX onlyPureSym = SxStatic.simplify(onlyPure);
        System.out.println(onlyPure);
        System.out.println(onlyPureSym);

        CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
        CGAOperatorMatrixUtils cgaOperatorMatrixUtils = new CGAOperatorMatrixUtils(baseCayleyTable);
        SparseDoubleMatrix m = cgaOperatorMatrixUtils.getConjugationOperatorMatrix();
        System.out.println(CasADiUtil.toSX(m));

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
        System.out.println(resNum);
        System.out.println(resSym); // Numerische Null. Unerwartet.
         */
        var withoutNum = a.sub(a);
        System.out.println(withoutNum.toString()); // SymbolicMV printet strukturelle Null
        System.out.println(withoutNum.getSX()); // SX printet numerische Null. Widersprüchlich!
        System.out.println(withoutNum.getSX().at(0, 0));
        System.out.println(SX.sparsify(withoutNum.getSX())); // Strukturelle Null.
    }
}
