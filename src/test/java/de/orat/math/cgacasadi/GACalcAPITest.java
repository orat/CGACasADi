package de.orat.math.cgacasadi;

import de.orat.math.cgacasadi.impl.ExprGraphFactory;
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
    public void add() {
        //TODO
        // vermutlich muss ich hier die sparsity mit angeben, damit der result auch
        // die korrekte sparsity hat. 
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1,2,3});
        MultivectorSymbolic mvsa = ExprGraphFactory.createMultivectorSymbolic("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1,3,4});
        MultivectorSymbolic mvsb = ExprGraphFactory.createMultivectorSymbolic("b", sparsity_b);
        
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
        System.out.println("a="+arg_a.toString());
        arguments.add(arg_a);
        
        double[] values_B = new double[CGACayleyTable.CGABasisBladeNames.length];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorNumeric arg_b = ExprGraphFactory.createMultivectorNumeric(values_B);
        System.out.println("b="+arg_b.toString());
        arguments.add(arg_b);
        
        try {
            List<MultivectorNumeric> result = f.callNumeric(arguments);
            //TODO
            // sparsity stimmt nicht mehr oder wird nur nicht richtig angezeigt
            // toString() ruft intern dm.toString() auf
            System.out.println("c=a+b="+result.iterator().next().toString());
        } catch (Exception e){
        
        }
    }
}
