package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class ExprGraphFactory {
    
    public static MultivectorSymbolic createMultivectorSymbolic(String name){
         return MultivectorSymbolic.get(new SparseCGASymbolicMultivector(name));
    }
     
    public static MultivectorNumeric createMultivectorNumeric(double[] values){
        SparseCGANumericMultivector impl = new SparseCGANumericMultivector(values);
        return MultivectorNumeric.get(impl);
    }
    
    public static FunctionSymbolic createFunctionSymbolic(String name, List<MultivectorSymbolic> parameters,
                                           List<MultivectorSymbolic> returns){
        FunctionSymbolic f = FunctionSymbolic.get(new CGASymbolicFunction());
        f.setSymbolic(name, parameters, returns);
        return f;
    }
    
    public static FunctionSymbolic createFunctionNumeric(String name, List<MultivectorSymbolic> parameters,
                                           List<MultivectorNumeric> returns){
        FunctionSymbolic f = FunctionSymbolic.get(new CGASymbolicFunction());
        f.setNumeric(name, parameters, returns);
        return f;
    }
}
