package de.orat.math.cgacasadi;

import de.orat.math.cgacasadi.impl.CGASymbolicFunction;
import de.orat.math.cgacasadi.impl.ExprGraphFactory;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GACalcAPITest {
    
    public GACalcAPITest() {
    }

    @Test
    public void test() {
        MultivectorSymbolic mvsa = ExprGraphFactory.createMultivectorSymbolic("a");
        MultivectorSymbolic mvsb = ExprGraphFactory.createMultivectorSymbolic("b");
        
        MultivectorSymbolic mvsc = mvsa.add(mvsb);
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorSymbolic> returns = new ArrayList<>();
        returns.add(mvsc);
        
        FunctionSymbolic f = ExprGraphFactory.createFunctionSymbolic("c", parameters, returns);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] values_A = new double[CGACayleyTable.CGABasisBladeNames.length];
        //Werte setzen
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorNumeric arg_a = ExprGraphFactory.createMultivectorNumeric(values_A);
        arguments.add(arg_a);
        
        double[] values_B = new double[CGACayleyTable.CGABasisBladeNames.length];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorNumeric arg_b = ExprGraphFactory.createMultivectorNumeric(values_B);
        arguments.add(arg_b);
        
        try {
            List<MultivectorNumeric> result = f.callNumeric(arguments);
            System.out.println(result.iterator().next().toString());
        } catch (Exception e){
        
        }
    }
}
