package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorMX;
import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;
import de.orat.math.cgacasadi.impl.ExprGraphFactory;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class APICGAImplTest {
    
    public APICGAImplTest() {
    }

    @Test
    public void testAdd() {
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
            System.out.println("c=a+b="+result.iterator().next().toString());
        } catch (Exception e){}
    }
    
    @Test
    public void testOP() {
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1,2,3});
        MultivectorSymbolic mvsa = ExprGraphFactory.createMultivectorSymbolic("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1,3,4});
        MultivectorSymbolic mvsb = ExprGraphFactory.createMultivectorSymbolic("b", sparsity_b);
        
        MultivectorSymbolic mvsc = mvsa.op(mvsb);
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorSymbolic> returns = new ArrayList<>();
        returns.add(mvsc);
        
        FunctionSymbolic f = ExprGraphFactory.createFunctionSymbolic("c", parameters, returns);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = new double[CGACayleyTable.CGABasisBladeNames.length];
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
            System.out.println("c=a+b="+result.iterator().next().toString());
        } catch (Exception e){}
    }
    
    
    @Test
    public void testGP() {
        MultivectorSymbolic mva = CGAExprGraphFactory.createMultivectorSymbolic("a", 1);
        System.out.println("a (sparsity): "+mva.getSparsity().toString());
        System.out.println("a: "+mva.toString());
        MultivectorSymbolic mvb = CGAExprGraphFactory.createMultivectorSymbolic("b", 1); 
        System.out.println("b (sparsity): "+mvb.getSparsity().toString());
        System.out.println("b: "+mvb.toString());
        
        MultivectorSymbolic res = mva.gp(mvb);
        System.out.println("result (sym): "+res.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);
        
        List<MultivectorSymbolic> result = new ArrayList<>();
        result.add(res);
        FunctionSymbolic f = ExprGraphFactory.createFunctionSymbolic("f", parameters, result);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = new double[CGACayleyTable.CGABasisBladeNames.length];
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
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            System.out.println("c=a b="+result2.iterator().next().toString());
        } catch (Exception e){}
    }
    
    @Test
    public void testReverse(){
        MultivectorSymbolic mv = CGAExprGraphFactory.createMultivectorSymbolic("mv", 1);
        System.out.println("mv (sparsity): "+mv.getSparsity().toString());
        System.out.println("mv: "+mv.toString());
        MultivectorSymbolic result = mv.reverse();
        System.out.println("result (sym): "+result.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mv);
        
        List<MultivectorSymbolic> res = new ArrayList<>();
        res.add(result);
        FunctionSymbolic f = ExprGraphFactory.createFunctionSymbolic("f", parameters, res);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] randomValues = CGAExprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg = CGAExprGraphFactory.createMultivectorNumeric(randomValues);
        arguments.add(arg);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric out = result2.iterator().next();
            System.out.println("b=reverse(a)="+out.toString());
            double[] values = out.elements();
            assertTrue(equals(values, reverse(randomValues), mv.getSparsity()));
        } catch (Exception e){}
    }
    private double[] reverse(double[] a){
        double[] res = new double[32];
        // scheint zu stimmen
        // data=[1.0,1.0,1.0,1.0,1.0,1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,1.0,1.0,1.0,1.0,1.0,1.0]
        res[0]=a[0];
	res[1]=a[1];
	res[2]=a[2];
	res[3]=a[3];
	res[4]=a[4];
	res[5]=a[5];
	res[6]=-a[6];
	res[7]=-a[7];
	res[8]=-a[8];
	res[9]=-a[9];
	res[10]=-a[10];
	res[11]=-a[11];
	res[12]=-a[12];
	res[13]=-a[13];
	res[14]=-a[14];
	res[15]=-a[15];
	res[16]=-a[16];
	res[17]=-a[17];
	res[18]=-a[18];
	res[19]=-a[19];
	res[20]=-a[20];
	res[21]=-a[21];
	res[22]=-a[22];
	res[23]=-a[23];
	res[24]=-a[24];
	res[25]=-a[25];
	res[26]=a[26];
	res[27]=a[27];
	res[28]=a[28];
	res[29]=a[29];
	res[30]=a[30];
	res[31]=a[31];
        return res;
    }
    
     /**
     * Dual.
     *
     * Poincare duality operator.
     *
     * @param a
     * @return !a
     */
    private static double[] dual (double[] values){
        double[] res = new double[values.length];
        res[0]=-values[31];
	res[1]=-values[30];
        
	res[2]=values[29];
        
	res[3]=-values[28];
        
	res[4]=values[27];
	res[5]=values[26];
	res[6]=values[25];
        
	res[7]=-values[24];
        
	res[8]=values[23];
	res[9]=values[22];
	res[10]=values[21];
        
	res[11]=-values[20];
	res[12]=-values[19];
        
	res[13]=values[18];
	res[14]=values[17];
        
	res[15]=-values[16];
        
	res[16]=values[15];
        
	res[17]=-values[14];
	res[18]=-values[13];
        
	res[19]=values[12];
	res[20]=values[11];
        
	res[21]=-values[10];
	res[22]=-values[9];
	res[23]=-values[8];
        
	res[24]=values[7];
        
	res[25]=-values[6];
	res[26]=-values[5];
	res[27]=-values[4];
        
	res[28]=values[3];
        
	res[29]=-values[2];
        
	res[30]=values[1];
	res[31]=values[0];
        return res;
    }
    
    private boolean equals(double[] a, double[] b){
        if (a.length != b.length) throw new IllegalArgumentException("a.length != b.length");
        for (int i=0;i<a.length;i++){
            if (a[i] != b[i]) return false;
        }
        return true;
    }
    
    private boolean equals(double[] a, double[] b, ColumnVectorSparsity sparsity){
        if (a.length != b.length) throw new IllegalArgumentException("a.length != b.length");
        int[] rows = sparsity.getrow();
        for (int i=0;i<rows.length;i++){
            if (a[rows[i]] != b[rows[i]]) return false;
        }
        return true;
    }
    
}
