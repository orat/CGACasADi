package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;

public class ShowStructuralZeroPropagation {

    public static void main(String[] args) {
        // -----------------
        System.out.println("// -----------------");
        var diagSparsity = Sparsity.diag(2, 2);
        SX a = SX.sym("a", diagSparsity);
        SX b = SX.sym("b", diagSparsity);
        System.out.println(a);
        System.out.println(b);
        System.out.println("// -----------------");
        // -----------------
        System.out.println("Addition of submatrices preserves structural zero:");
        var structuralZeroSum = SX.plus(a.at(0, 1), b.at(0, 1));
        System.out.println(structuralZeroSum);
        System.out.println("// -----------------");
        // -----------------
        var c = SX.sym("c", 2, 2);
        System.out.println(c);
        System.out.println(CasADiUtil.toMatrixSparsity(c.sparsity()));
        System.out.println("// -----------------");
        // -----------------
        c.at(0, 0).assign(structuralZeroSum);
        System.out.println("Assignment of structural zero to submatrix works:");
        System.out.println(c);
        System.out.println("// -----------------");
        // -----------------
        System.out.println("Assigned structural zero changes sparsity:");
        System.out.println(CasADiUtil.toMatrixSparsity(c.sparsity()));
        System.out.println("// -----------------");
        // -----------------
    }
}
