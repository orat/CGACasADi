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
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GACalcAPITest {
    
    public GACalcAPITest() {
    }

    @Test
    public void test() {
        //MultivectorSymbolic mvsa = MultivectorSymbolic.get(new SparseCGASymbolicMultivector("a"));
        //MultivectorSymbolic mvsb = MultivectorSymbolic.get(new SparseCGASymbolicMultivector("b"));
        
        MultivectorSymbolic mvsa = ExprGraphFactory.createMultivectorSymbolic("a");
        MultivectorSymbolic mvsb = ExprGraphFactory.createMultivectorSymbolic("b");
        
        MultivectorSymbolic mvsc = mvsa.add(mvsb);
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorSymbolic> returns = new ArrayList<>();
        returns.add(mvsc);
        
        FunctionSymbolic f =  ExprGraphFactory.createFunctionSymbolic("c", parameters, returns);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        MultivectorNumeric argument = ExprGraphFactory.createMultivectorNumeric();
        //TODO
        // Werte setzen
        
        arguments.add(argument);
        try {
            List<MultivectorNumeric> result = f.callNumeric(arguments);

            //TODO
            // ergebnis rausholen
        
        } catch (Exception e){
        
        }
        //FunctionSymbolic f = FunctionSymbolic.get(new CGASymbolicFunction());
        //f.set("c=a+b", parameters, returns);
        //MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name));
    }
}
