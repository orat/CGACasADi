package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.cgacasadi.impl.CgaFactory;
import de.orat.math.cgacasadi.impl.CgaFunction;
import de.orat.math.cgacasadi.impl.CgaMvExpr;
import de.orat.math.cgacasadi.impl.CgaMvValue;
import de.orat.math.cgacasadi.impl.CgaMvVariable;
import de.orat.math.cgacasadi.impl.IGetSX;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.Arrays;
import java.util.List;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;
import util.cga.SparseCGAColumnVector;

public class ExpNanTest {

    /**
     * Works only if expr are constructed from getDelegate() of inputs.
     */
    public static CgaMvValue evalPoint(List<CgaMvValue> inputs, CgaMvExpr expr) {
        List<CgaMvVariable> variables = inputs.stream().map(v -> (CgaMvVariable) v.getDelegate()).toList();
        return (new CgaFunction("evalPoint", variables, List.of(expr))).callValue(inputs).get(0);
    }

    private static StdVectorSX transformImpl(List<? extends IGetSX> mvs) {
        List<SX> sxs = mvs.stream().map(IGetSX::getSX).toList();
        return new StdVectorSX(sxs);
    }

    public static void main(String[] args) {
        var fac = CgaFactory.instance;

        /*
        // Numerisch Expr tut immer noch:
        var pVec = SparseCGAColumnVector.createEuclid(new double[]{0d, 0.0996, 0d});
        var p = fac.createValue(pVec).toExpr();
        var translator = fac.createExpr(-0.5).gp(p).gp(fac.constantsExpr().getBaseVectorInfinity());
        var exp = translator.exp();
        System.out.println(p);
        System.out.println(translator);
        System.out.println(exp);

        // Symbolisch bei MVValue tut jetzt (davor nicht):
        var pVec2 = SparseCGAColumnVector.createEuclid(new double[]{0d, 0.0996, 0d});
        var p2 = fac.createValue(pVec2);
        var translator2 = fac.createValue(-0.5).gp(p2).gp(fac.constantsValue().getBaseVectorInfinity());
        var exp2 = translator2.exp();
        System.out.println(p2);
        System.out.println(translator2);
        System.out.println(exp2);

        // Test tut jetzt (davor nicht)
        var indizes = CGACayleyTableGeometricProduct.getIndizes(2);
        var sparsity = new CGAMultivectorSparsity(indizes);
        double[] values = {00, 00, -0.5, -0.5, 00, -1.5, -1.5, -2.5, -2.5, 00};
        var sdm = new SparseDoubleColumnVector(sparsity, values);
        var rand = fac.createValue(sdm); // Mit toEpr tut es. So aber nicht.
        var exp3 = rand.exp().toString();
        System.out.println(exp3);
         */
        //  Noch näher an der DSL.
        CgaMvValue ae = fac.createValue(SparseCGAColumnVector.createEuclid(new double[]{0d, 1d, 0d}));
        CgaMvExpr d6 = fac.createExpr(0.0996);
        CgaMvExpr vec = d6.gp(ae.getDelegate());
        CgaMvExpr expInput = fac.createExpr(-0.5).gp(vec).gp(fac.constantsExpr().getBaseVectorInfinity());
        CgaMvExpr exp = expInput.exp();
        CgaMvValue expNum = evalPoint(List.of(ae), exp);
        System.out.println(exp);
        System.out.println(expNum); // Richtig

        StdVectorSX def_sym_in = transformImpl(List.of(ae.getDelegate()));
        StdVectorSX def_sym_out = transformImpl(List.of(exp));
        var func = new Function("casadiExpNanTest", def_sym_in, def_sym_out);
        StdVectorDM call_num_in = new StdVectorDM(List.of(ae.getDM()));
        StdVectorDM call_num_out = new StdVectorDM();
        func.call(call_num_in, call_num_out);
        DM result = call_num_out.get(0);
        System.out.println(result); // Mit NaN

        // Wie verhält sich CasADi# hinsichtlich 0/0?
        CgaMvValue t1 = fac.createValue(0);
        CgaMvValue t2 = fac.createValue(0);
        SX div = SxStatic.rdivide(t1.getDelegate().getSX(), t2.getDelegate().getSX());
        CgaMvExpr expr = CgaMvExpr.create(div).simplifySparsify();
        CgaMvValue nanEval = evalPoint(List.of(t1, t2), expr);
        System.out.println(nanEval); // Das gibt NaN. Also liegt es wo anders.
    }
}
