package de.orat.math.cgacasadi;

import util.cga.CGACayleyTable;
import util.cga.CGAMultivectorSparsity;
import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.DenseDoubleColumnVector;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import util.cga.CGACayleyTableGeometricProduct;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAImplTest {
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
   
    public CGAImplTest() {
        //ExprGraphFactory exprGraphFactory = ExprGraphFactory.get(new CGAExprGraphFactory());
    }

    @Test
    public void testAdd() {
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1,2,3});
        MultivectorSymbolic mvsa = exprGraphFactory.createMultivectorSymbolic("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1,3,4});
        MultivectorSymbolic mvsb = exprGraphFactory.createMultivectorSymbolic("b", sparsity_b);
        
        MultivectorSymbolic mvsc = mvsa.addition(mvsb);
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorSymbolic> returns = new ArrayList<>();
        returns.add(mvsc);
        
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("c", parameters, returns);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = new double[baseCayleyTable.getBladesCount()];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        System.out.println("a="+arg_a.toString());
        arguments.add(arg_a);
        
        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorNumeric arg_b = exprGraphFactory.createMultivectorNumeric(values_B);
        System.out.println("b="+arg_b.toString());
        arguments.add(arg_b);
        
        double[] test = add(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
       
        try {
            List<MultivectorNumeric> mv = f.callNumeric(arguments);
            System.out.println("c=a+b="+mv.iterator().next().toString());
            System.out.println("test="+testMatrix.toString());
            assertTrue(equals(mv.iterator().next().elements().toArray(), test));
        } catch (Exception e){}
    }
    
    @Test
    public void testSub() {
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1,2,3});
        MultivectorSymbolic mvsa = exprGraphFactory.createMultivectorSymbolic("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1,3,4});
        MultivectorSymbolic mvsb = exprGraphFactory.createMultivectorSymbolic("b", sparsity_b);
        
        MultivectorSymbolic mvsc = mvsa.subtraction(mvsb);
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorSymbolic> returns = new ArrayList<>();
        returns.add(mvsc);
        
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("c", parameters, returns);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = new double[baseCayleyTable.getBladesCount()];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        System.out.println("a="+arg_a.toString());
        arguments.add(arg_a);
        
        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorNumeric arg_b = exprGraphFactory.createMultivectorNumeric(values_B);
        System.out.println("b="+arg_b.toString());
        arguments.add(arg_b);
        
        double[] test = sub(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
       
        try {
            List<MultivectorNumeric> mv = f.callNumeric(arguments);
            System.out.println("c=a-b="+mv.iterator().next().toString());
            System.out.println("test="+testMatrix.toString());
            assertTrue(equals(mv.iterator().next().elements().toArray(), test));
        } catch (Exception e){}
    }
    
    
    @Test
    public void testOP() {
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1,2,3});
        MultivectorSymbolic mvsa = exprGraphFactory.createMultivectorSymbolic("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1,3,4});
        MultivectorSymbolic mvsb = exprGraphFactory.createMultivectorSymbolic("b", sparsity_b);
        
        MultivectorSymbolic mvsc = mvsa.outerProduct(mvsb);
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorSymbolic> returns = new ArrayList<>();
        returns.add(mvsc);
        
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, returns);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = new double[baseCayleyTable.getBladesCount()]; 
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        //values_A = exprGraphFactory.createRandomCGAKVector(1);
        
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        System.out.println("a="+arg_a.toString());
        arguments.add(arg_a);
        
        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        //values_B = exprGraphFactory.createRandomCGAKVector(1);
        
        MultivectorNumeric arg_b = exprGraphFactory.createMultivectorNumeric(values_B);
        System.out.println("b="+arg_b.toString());
        arguments.add(arg_b);
        
        
        double[] test = op(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric mv = result2.iterator().next();
            System.out.println("a^b="+mv.toString());
            //FIXME alle Werte sind 0
            
            // a=[00, 1, 2, 3, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]
            // b=[00, 1, 00, 1, 1, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]
            // c=a^b=[00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]
            // test=={0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, -2.0, 1.0, 0.0, 2.0, 2.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}

            System.out.println("test=="+testMatrix.toString());
            assertTrue(equals(mv.elements().toArray(), test));
        } catch (Exception e){}
    }
    
    @Test
    public void testGradeSelectionRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = CGAMultivectorSparsity.dense();
        MultivectorSymbolic mva = exprGraphFactory.createMultivectorSymbolic("a", sparsity_a);
        //MultivectorSymbolic mva = exprGraphFactory.createMultivectorSymbolic("a", 1);
       
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        arguments.add(arg_a);
        
        //TODO
        // alle grades durchspielen bzw. random auswählen
        int grade = 3; // für 0, 1,2, 4,5 funktioniert es, für 3 funktioniert es nicht
        MultivectorSymbolic res = mva.gradeExtraction(grade);
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mva);
        
        List<MultivectorSymbolic> result = new ArrayList<>();
        result.add(res);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, result);
        
        
        double[] test = gradeSelection(values_A, grade);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric mv = result2.iterator().next();
            System.out.println("gradeSelection()="+mv.toString());
            System.out.println("test="+testMatrix.toString());
            assertTrue(equals(mv.elements().toArray(), test));
        } catch (Exception e){}
    }
    
    @Test
    public void testGPVec1Fix() {
        
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        //CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1,2,3});
        //MultivectorSymbolic mva = CGAExprGraphFactory.createMultivectorSymbolic("a", sparsity_a);
        
        MultivectorSymbolic mva = exprGraphFactory.createMultivectorSymbolic("a", 1);
        //System.out.println("a (sparsity): "+mva.getSparsity().toString());
        //System.out.println("a: "+mva.toString());
        
        MultivectorSymbolic mvb = exprGraphFactory.createMultivectorSymbolic("b", 1); 
        //System.out.println("b (sparsity): "+mvb.getSparsity().toString());
        //System.out.println("b: "+mvb.toString());
        
        MultivectorSymbolic res = mva.geometricProduct(mvb);
        System.out.println("result (sym vec1fix): "+res.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb); // vertauschen von a und be hatte keinen Effekt
        
        List<MultivectorSymbolic> result = new ArrayList<>();
        result.add(res);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, result);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = new double[baseCayleyTable.getBladesCount()];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        //System.out.println("a="+arg_a.toString());
        arguments.add(arg_a);
        
        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorNumeric arg_b = exprGraphFactory.createMultivectorNumeric(values_B);
        //System.out.println("b="+arg_b.toString());
        arguments.add(arg_b);
       
        double[] test = gp(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric mv = result2.iterator().next();
            System.out.println("a b="+mv.toString());
            System.out.println("test="+testMatrix.toString());
            //FIXME
            // Vorzeichenfehler aller Komponentn ausser für den scalar
            // a b=[4, 00, 00, 00, 00, 00, 2, 2, -1, 0, -2, -2, 0, -3, 0, 0, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]
            // test={4.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, -2.0, 1.0, 0.0, 2.0, 2.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
            // ganja: 4-2e12-2e13+e14+2e23+2e24+3e34
            // test function ist damit überprüft, der Vorzeichenfehler muss also an der symbolic-gp method liegen
            assertTrue(equals(mv.elements().toArray(), test));
        } catch (Exception e){}
    }
   
    @Test
    public void testGPRandom() {
       
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mva = exprGraphFactory.createMultivectorSymbolic("a"/*, 1*/);
        MultivectorSymbolic mvb = exprGraphFactory.createMultivectorSymbolic("b"/*, 1*/); 
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb); 
        
        MultivectorSymbolic res = mva.geometricProduct(mvb);
        System.out.println("gprandom: "+res.toString());
        
        List<MultivectorSymbolic> result = new ArrayList<>();
        result.add(res);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, result);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        arguments.add(arg_a);
        
        double[] values_B = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg_b = exprGraphFactory.createMultivectorNumeric(values_B);
        arguments.add(arg_b);
       
        double[] test = gp(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());
        
        try {
            System.out.println("a="+arg_a.toString());
            System.out.println("b="+arg_b.toString());
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric mv = result2.iterator().next();
            System.out.println("random (gp): a b="+mv.toString());
            System.out.println("test="+testMatrix.toString());
            //FIXME
            //a=[0.788533, 0.230104, 0.617871, -0.928288, -0.105082, -0.769252, -0.0912453, -0.338095, -0.603159, 0.426324, 0.353887, 0.830786, -0.266535, 0.933946, 0.974447, 0.381749, -0.52536, -0.275709, -0.163835, -0.387657, 0.0793199, 0.184763, -0.746645, -0.56926, 0.684507, 0.0170876, -0.754337, -0.0343847, 0.791464, -0.614593, 0.591833, 0.227089]
            //b=[0.0436466, -0.832396, 0.283982, -0.337702, 0.527104, 0.277705, 0.693611, 0.12766, 0.866075, -0.093915, 0.399915, 0.718882, 0.615126, 0.293259, 0.270382, -0.299662, -0.13652, 0.681146, -0.0903113, -0.464942, -0.939624, -0.208155, 0.482373, -0.821425, 0.974287, -0.998317, 0.406073, 0.439755, -0.804543, -0.51233, -0.451544, 0.667235]
            //random: a b=[0.355931, -1.78226, -0.541808, -1.09884, 0.994596, -0.73997, 2.64298, 1.71158, 0.512802, -2.05135, 3.60021, 0.5254, -2.22526, 2.67642, -0.582524, 0.625841, 0.900617, 0.896198, -0.0642288, 1.61451, -2.17924, 3.58869, -0.560492, 1.50894, 3.1433, -2.80302, -1.77937, 0.127167, -3.52281, -1.53133, -1.24064, -0.667185]
            //test={1.5129998566656144, 0.18279855321983673, -1.5486842481574314, -1.0307638838007547, 1.7355424748572692, 0.6963847626010777, 1.9340365152238923, -1.0463436077043107, -1.061457856849736, -1.6177672782510588, 0.19108293787230862, 1.0730930365434563, 1.2005647669797161, 0.8094238573650144, -3.3604704289582132, -0.840300135953945, 0.342225499220247, -0.506030354827997, -2.379276486840147, 0.7498230502220242, -1.466120946386584, -2.104513282014597, 0.18580517101020536, 1.7646581748137726, 2.463900023143224, -2.7117845454550986, -2.2909198246697, -1.3522817204400508, 2.1899067681117153, -1.5587260627620478, -0.2283382415197428, 0.5172856161891328}

            double eps = 0.00001;
            assertTrue(equals(mv.elements().toArray(), test, eps));
        } catch (Exception e){}
    }
    
    @Test
    public void testOPRandom() {
       
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mva = exprGraphFactory.createMultivectorSymbolic("a");
        MultivectorSymbolic mvb = exprGraphFactory.createMultivectorSymbolic("b"); 
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb); 
        
        MultivectorSymbolic res = mva.outerProduct(mvb);
        System.out.println("oprandom: "+res.toString());
        
        List<MultivectorSymbolic> result = new ArrayList<>();
        result.add(res);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, result);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        arguments.add(arg_a);
        
        double[] values_B = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg_b = exprGraphFactory.createMultivectorNumeric(values_B);
        arguments.add(arg_b);
       
        double[] test = op(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());
        
        try {
            System.out.println("a="+arg_a.toString());
            System.out.println("b="+arg_b.toString());
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric mv = result2.iterator().next();
            System.out.println("random (op): ab="+mv.toString());
            System.out.println("test="+testMatrix.toString());
            
            double eps = 0.00001;
            assertTrue(equals(mv.elements().toArray(), test, eps));
        } catch (Exception e){}
    }
    
    @Test
    public void testGPRandom1Vec() {
       
        //CGAExprGraphFactory fac = new CGAExprGraphFactory();
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mva = exprGraphFactory.createMultivectorSymbolic("a", 1);
        MultivectorSymbolic mvb = exprGraphFactory.createMultivectorSymbolic("b", 1); 
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb); 
        
        MultivectorSymbolic res = mva.geometricProduct(mvb);
        System.out.println("radmon1vec: "+res.toString());
        
        List<MultivectorSymbolic> result = new ArrayList<>();
        result.add(res);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, result);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] values_A = TestExprGraphFactory.createRandomCGAKVector(1);
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(values_A);
        arguments.add(arg_a);
        
        double[] values_B = TestExprGraphFactory.createRandomCGAKVector(1);
        MultivectorNumeric arg_b = exprGraphFactory.createMultivectorNumeric(values_B);
        arguments.add(arg_b);
       
        double[] test = gp(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());
        
        try {
            System.out.println("a="+arg_a.toString());
            System.out.println("b="+arg_b.toString());
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric mv = result2.iterator().next();
            System.out.println("random 1-vec: a b="+mv.toString());
            System.out.println("test="+testMatrix.toString());
            //FIXME
            //random 1-vec: a b=[0.321973, 00, 00, 00, 00, 00, 0.378457, -0.177341, -0.126853, (r)
            // 0.177544, (f)
            // -0.43196, -0.608462, (r)
            // 0.66326, (f)
            // 0.140333, (r)
            // -0.108153, 0.0631305, (f) 
            // 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]
            //test={0.11216005820801957, 0.0, 0.0, 0.0, 0.0, 0.0, 0.3784567994893423, -0.1773408889254089, -0.1268527623710874, 
            // 0.03246793089164196, 
            // -0.43196015989458275, -0.6084620730108591, 
            // 0.568534557924607, 
            // 0.14033270233199763, 
            // -0.22935133252958934, -0.13836367773049948, 
            // 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}

            double eps = 0.00001;
            assertTrue(equals(mv.elements().toArray(), test, eps));
        } catch (Exception e){}
    }
    
    /**
     * Mul.
     *
     * The geometric product.
     *
     * @param a
     * @param b
     * @return a * b
     */
    private static double[] gp(double[] a, double[] b){
        double[] res = new double[a.length];
        
        // a, b scheinen vertauscht
        // gprandom: T{((((((((((((((((((((((((((((((((a_0*b_0)+(a_1*b_1))+(a_2*b_2))+(a_3*b_3))+(a_4*b_4))-(a_5*b_5))-(a_6*b_6))-(a_7*b_7))-(a_8*b_8))+(a_9*b_9))-(a_10*b_10))-(a_11*b_11))+(a_12*b_12))-(a_13*b_13))+(a_14*b_14))+(a_15*b_15))-(a_16*b_16))-(a_17*b_17))+(a_18*b_18))-(a_19*b_19))+(a_20*b_20))+(a_21*b_21))-(a_22*b_22))+(a_23*b_23))+(a_24*b_24))+(a_25*b_25))+(a_26*b_26))-(a_27*b_27))-(a_28*b_28))-(a_29*b_29))-(a_30*b_30))-(a_31*b_31)), 
        //             ((((((((((((((((((((((((((((((((a_1*b_0)+(a_0*b_1))-(a_6*b_2))-(a_7*b_3))-(a_8*b_4))+(a_9*b_5))+(a_2*b_6))+(a_3*b_7))+(a_4*b_8))-(a_5*b_9))-(a_16*b_10))-(a_17*b_11))+(a_18*b_12))-(a_19*b_13))+(a_20*b_14))+(a_21*b_15))-(a_10*b_16))-(a_11*b_17))+(a_12*b_18))-(a_13*b_19))+(a_14*b_20))+(a_15*b_21))+(a_26*b_22))-(a_27*b_23))-(a_28*b_24))-(a_29*b_25))-(a_22*b_26))+(a_23*b_27))+(a_24*b_28))+(a_25*b_29))-(a_31*b_30))-(a_30*b_31)), 
        //             ((((((((((((((((((((((((((((((((a_2*b_0)+(a_6*b_1))+(a_0*b_2))-(a_10*b_3))-(a_11*b_4))+(a_12*b_5))-(a_1*b_6))+(a_16*b_7))+(a_17*b_8))-(a_18*b_9))+(a_3*b_10))+(a_4*b_11))-(a_5*b_12))-(a_22*b_13))+(a_23*b_14))+(a_24*b_15))+(a_7*b_16))+(a_8*b_17))-(a_9*b_18))-(a_26*b_19))+(a_27*b_20))+(a_28*b_21))-(a_13*b_22))+(a_14*b_23))+(a_15*b_24))-(a_30*b_25))+(a_19*b_26))-(a_20*b_27))-(a_21*b_28))+(a_31*b_29))+(a_25*b_30))+(a_29*b_31)), ((((((((((((((((((((((((((((((((a_3*b_0)+(a_7*b_1))+(a_10*b_2))+(a_0*b_3))-(a_13*b_4))+(a_14*b_5))-(a_16*b_6))-(a_1*b_7))+(a_19*b_8))-(a_20*b_9))-(a_2*b_10))+(a_22*b_11))-(a_23*b_12))+(a_4*b_13))-(a_5*b_14))+(a_25*b_15))-(a_6*b_16))+(a_26*b_17))-(a_27*b_18))+(a_8*b_19))-(a_9*b_20))+(a_29*b_21))+(a_11*b_22))-(a_12*b_23))+(a_30*b_24))+(a_15*b_25))-(a_17*b_26))+(a_18*b_27))-(a_31*b_28))-(a_21*b_29))-(a_24*b_30))-(a_28*b_31)), ((((((((((((((((((((((((((((((((a_4*b_0)+(a_8*b_1))+(a_11*b_2))+(a_13*b_3))+(a_0*b_4))+(a_15*b_5))-(a_17*b_6))-(a_19*b_7))-(a_1*b_8))-(a_21*b_9))-(a_22*b_10))-(a_2*b_11))-(a_24*b_12))-(a_3*b_13))-(a_25*b_14))-(a_5*b_15))-(a_26*b_16))-(a_6*b_17))-(a_28*b_18))-(a_7*b_19))-(a_29*b_20))-(a_9*b_21))-(a_10*b_22))-(a_30*b_23))-(a_12*b_24))-(a_14*b_25))+(a_16*b_26))+(a_31*b_27))+(a_18*b_28))+(a_20*b_29))+(a_23*b_30))+(a_27*b_31)), ((((((((((((((((((((((((((((((((a_5*b_0)+(a_9*b_1))+(a_12*b_2))+(a_14*b_3))+(a_15*b_4))+(a_0*b_5))-(a_18*b_6))-(a_20*b_7))-(a_21*b_8))-(a_1*b_9))-(a_23*b_10))-(a_24*b_11))-(a_2*b_12))-(a_25*b_13))-(a_3*b_14))-(a_4*b_15))-(a_27*b_16))-(a_28*b_17))-(a_6*b_18))-(a_29*b_19))-(a_7*b_20))-(a_8*b_21))-(a_30*b_22))-(a_10*b_23))-(a_11*b_24))-(a_13*b_25))+(a_31*b_26))+(a_16*b_27))+(a_17*b_28))+(a_19*b_29))+(a_22*b_30))+(a_26*b_31)), ((((((((((((((((((((((((((((((((a_6*b_0)+(a_2*b_1))-(a_1*b_2))+(a_16*b_3))+(a_17*b_4))-(a_18*b_5))+(a_0*b_6))-(a_10*b_7))-(a_11*b_8))+(a_12*b_9))+(a_7*b_10))+(a_8*b_11))-(a_9*b_12))-(a_26*b_13))+(a_27*b_14))+(a_28*b_15))+(a_3*b_16))+(a_4*b_17))-(a_5*b_18))-(a_22*b_19))+(a_23*b_20))+(a_24*b_21))+(a_19*b_22))-(a_20*b_23))-(a_21*b_24))+(a_31*b_25))-(a_13*b_26))+(a_14*b_27))+(a_15*b_28))-(a_30*b_29))+(a_29*b_30))+(a_25*b_31)), ((((((((((((((((((((((((((((((((a_7*b_0)+(a_3*b_1))-(a_16*b_2))-(a_1*b_3))+(a_19*b_4))-(a_20*b_5))+(a_10*b_6))+(a_0*b_7))-(a_13*b_8))+(a_14*b_9))-(a_6*b_10))+(a_26*b_11))-(a_27*b_12))+(a_8*b_13))-(a_9*b_14))+(a_29*b_15))-(a_2*b_16))+(a_22*b_17))-(a_23*b_18))+(a_4*b_19))-(a_5*b_20))+(a_25*b_21))-(a_17*b_22))+(a_18*b_23))-(a_31*b_24))-(a_21*b_25))+(a_11*b_26))-(a_12*b_27))+(a_30*b_28))+(a_15*b_29))-(a_28*b_30))-(a_24*b_31)), ((((((((((((((((((((((((((((((((a_8*b_0)+(a_4*b_1))-(a_17*b_2))-(a_19*b_3))-(a_1*b_4))-(a_21*b_5))+(a_11*b_6))+(a_13*b_7))+(a_0*b_8))+(a_15*b_9))-(a_26*b_10))-(a_6*b_11))-(a_28*b_12))-(a_7*b_13))-(a_29*b_14))-(a_9*b_15))-(a_22*b_16))-(a_2*b_17))-(a_24*b_18))-(a_3*b_19))-(a_25*b_20))-(a_5*b_21))+(a_16*b_22))+(a_31*b_23))+(a_18*b_24))+(a_20*b_25))-(a_10*b_26))-(a_30*b_27))-(a_12*b_28))-(a_14*b_29))+(a_27*b_30))+(a_23*b_31)), ((((((((((((((((((((((((((((((((a_9*b_0)+(a_5*b_1))-(a_18*b_2))-(a_20*b_3))-(a_21*b_4))-(a_1*b_5))+(a_12*b_6))+(a_14*b_7))+(a_15*b_8))+(a_0*b_9))-(a_27*b_10))-(a_28*b_11))-(a_6*b_12))-(a_29*b_13))-(a_7*b_14))-(a_8*b_15))-(a_23*b_16))-(a_24*b_17))-(a_2*b_18))-(a_25*b_19))-(a_3*b_20))-(a_4*b_21))+(a_31*b_22))+(a_16*b_23))+(a_17*b_24))+(a_19*b_25))-(a_30*b_26))-(a_10*b_27))-(a_11*b_28))-(a_13*b_29))+(a_26*b_30))+(a_22*b_31)), ((((((((((((((((((((((((((((((((a_10*b_0)+(a_16*b_1))+(a_3*b_2))-(a_2*b_3))+(a_22*b_4))-(a_23*b_5))-(a_7*b_6))+(a_6*b_7))-(a_26*b_8))+(a_27*b_9))+(a_0*b_10))-(a_13*b_11))+(a_14*b_12))+(a_11*b_13))-(a_12*b_14))+(a_30*b_15))+(a_1*b_16))-(a_19*b_17))+(a_20*b_18))+(a_17*b_19))-(a_18*b_20))+(a_31*b_21))+(a_4*b_22))-(a_5*b_23))+(a_25*b_24))-(a_24*b_25))-(a_8*b_26))+(a_9*b_27))-(a_29*b_28))+(a_28*b_29))+(a_15*b_30))+(a_21*b_31)), ((((((((((((((((((((((((((((((((a_11*b_0)+(a_17*b_1))+(a_4*b_2))-(a_22*b_3))-(a_2*b_4))-(a_24*b_5))-(a_8*b_6))+(a_26*b_7))+(a_6*b_8))+(a_28*b_9))+(a_13*b_10))+(a_0*b_11))+(a_15*b_12))-(a_10*b_13))-(a_30*b_14))-(a_12*b_15))+(a_19*b_16))+(a_1*b_17))+(a_21*b_18))-(a_16*b_19))-(a_31*b_20))-(a_18*b_21))-(a_3*b_22))-(a_25*b_23))-(a_5*b_24))+(a_23*b_25))+(a_7*b_26))+(a_29*b_27))+(a_9*b_28))-(a_27*b_29))-(a_14*b_30))-(a_20*b_31)), ((((((((((((((((((((((((((((((((a_12*b_0)+(a_18*b_1))+(a_5*b_2))-(a_23*b_3))-(a_24*b_4))-(a_2*b_5))-(a_9*b_6))+(a_27*b_7))+(a_28*b_8))+(a_6*b_9))+(a_14*b_10))+(a_15*b_11))+(a_0*b_12))-(a_30*b_13))-(a_10*b_14))-(a_11*b_15))+(a_20*b_16))+(a_21*b_17))+(a_1*b_18))-(a_31*b_19))-(a_16*b_20))-(a_17*b_21))-(a_25*b_22))-(a_3*b_23))-(a_4*b_24))+(a_22*b_25))+(a_29*b_26))+(a_7*b_27))+(a_8*b_28))-(a_26*b_29))-(a_13*b_30))-(a_19*b_31)), ((((((((((((((((((((((((((((((((a_13*b_0)+(a_19*b_1))+(a_22*b_2))+(a_4*b_3))-(a_3*b_4))-(a_25*b_5))-(a_26*b_6))-(a_8*b_7))+(a_7*b_8))+(a_29*b_9))-(a_11*b_10))+(a_10*b_11))+(a_30*b_12))+(a_0*b_13))+(a_15*b_14))-(a_14*b_15))-(a_17*b_16))+(a_16*b_17))+(a_31*b_18))+(a_1*b_19))+(a_21*b_20))-(a_20*b_21))+(a_2*b_22))+(a_24*b_23))-(a_23*b_24))-(a_5*b_25))-(a_6*b_26))-(a_28*b_27))+(a_27*b_28))+(a_9*b_29))+(a_12*b_30))+(a_18*b_31)), ((((((((((((((((((((((((((((((((a_14*b_0)+(a_20*b_1))+(a_23*b_2))+(a_5*b_3))-(a_25*b_4))-(a_3*b_5))-(a_27*b_6))-(a_9*b_7))+(a_29*b_8))+(a_7*b_9))-(a_12*b_10))+(a_30*b_11))+(a_10*b_12))+(a_15*b_13))+(a_0*b_14))-(a_13*b_15))-(a_18*b_16))+(a_31*b_17))+(a_16*b_18))+(a_21*b_19))+(a_1*b_20))-(a_19*b_21))+(a_24*b_22))+(a_2*b_23))-(a_22*b_24))-(a_4*b_25))-(a_28*b_26))-(a_6*b_27))+(a_26*b_28))+(a_8*b_29))+(a_11*b_30))+(a_17*b_31)), ((((((((((((((((((((((((((((((((a_15*b_0)+(a_21*b_1))+(a_24*b_2))+(a_25*b_3))+(a_5*b_4))-(a_4*b_5))-(a_28*b_6))-(a_29*b_7))-(a_9*b_8))+(a_8*b_9))-(a_30*b_10))-(a_12*b_11))+(a_11*b_12))-(a_14*b_13))+(a_13*b_14))+(a_0*b_15))-(a_31*b_16))-(a_18*b_17))+(a_17*b_18))-(a_20*b_19))+(a_19*b_20))+(a_1*b_21))-(a_23*b_22))+(a_22*b_23))+(a_2*b_24))+(a_3*b_25))+(a_27*b_26))-(a_26*b_27))-(a_6*b_28))-(a_7*b_29))-(a_10*b_30))-(a_16*b_31)), ((((((((((((((((((((((((((((((((a_16*b_0)+(a_10*b_1))-(a_7*b_2))+(a_6*b_3))-(a_26*b_4))+(a_27*b_5))+(a_3*b_6))-(a_2*b_7))+(a_22*b_8))-(a_23*b_9))+(a_1*b_10))-(a_19*b_11))+(a_20*b_12))+(a_17*b_13))-(a_18*b_14))+(a_31*b_15))+(a_0*b_16))-(a_13*b_17))+(a_14*b_18))+(a_11*b_19))-(a_12*b_20))+(a_30*b_21))-(a_8*b_22))+(a_9*b_23))-(a_29*b_24))+(a_28*b_25))+(a_4*b_26))-(a_5*b_27))+(a_25*b_28))-(a_24*b_29))+(a_21*b_30))+(a_15*b_31)), ((((((((((((((((((((((((((((((((a_17*b_0)+(a_11*b_1))-(a_8*b_2))+(a_26*b_3))+(a_6*b_4))+(a_28*b_5))+(a_4*b_6))-(a_22*b_7))-(a_2*b_8))-(a_24*b_9))+(a_19*b_10))+(a_1*b_11))+(a_21*b_12))-(a_16*b_13))-(a_31*b_14))-(a_18*b_15))+(a_13*b_16))+(a_0*b_17))+(a_15*b_18))-(a_10*b_19))-(a_30*b_20))-(a_12*b_21))+(a_7*b_22))+(a_29*b_23))+(a_9*b_24))-(a_27*b_25))-(a_3*b_26))-(a_25*b_27))-(a_5*b_28))+(a_23*b_29))-(a_20*b_30))-(a_14*b_31)), ((((((((((((((((((((((((((((((((a_18*b_0)+(a_12*b_1))-(a_9*b_2))+(a_27*b_3))+(a_28*b_4))+(a_6*b_5))+(a_5*b_6))-(a_23*b_7))-(a_24*b_8))-(a_2*b_9))+(a_20*b_10))+(a_21*b_11))+(a_1*b_12))-(a_31*b_13))-(a_16*b_14))-(a_17*b_15))+(a_14*b_16))+(a_15*b_17))+(a_0*b_18))-(a_30*b_19))-(a_10*b_20))-(a_11*b_21))+(a_29*b_22))+(a_7*b_23))+(a_8*b_24))-(a_26*b_25))-(a_25*b_26))-(a_3*b_27))-(a_4*b_28))+(a_22*b_29))-(a_19*b_30))-(a_13*b_31)), ((((((((((((((((((((((((((((((((a_19*b_0)+(a_13*b_1))-(a_26*b_2))-(a_8*b_3))+(a_7*b_4))+(a_29*b_5))+(a_22*b_6))+(a_4*b_7))-(a_3*b_8))-(a_25*b_9))-(a_17*b_10))+(a_16*b_11))+(a_31*b_12))+(a_1*b_13))+(a_21*b_14))-(a_20*b_15))-(a_11*b_16))+(a_10*b_17))+(a_30*b_18))+(a_0*b_19))+(a_15*b_20))-(a_14*b_21))-(a_6*b_22))-(a_28*b_23))+(a_27*b_24))+(a_9*b_25))+(a_2*b_26))+(a_24*b_27))-(a_23*b_28))-(a_5*b_29))+(a_18*b_30))+(a_12*b_31)), ((((((((((((((((((((((((((((((((a_20*b_0)+(a_14*b_1))-(a_27*b_2))-(a_9*b_3))+(a_29*b_4))+(a_7*b_5))+(a_23*b_6))+(a_5*b_7))-(a_25*b_8))-(a_3*b_9))-(a_18*b_10))+(a_31*b_11))+(a_16*b_12))+(a_21*b_13))+(a_1*b_14))-(a_19*b_15))-(a_12*b_16))+(a_30*b_17))+(a_10*b_18))+(a_15*b_19))+(a_0*b_20))-(a_13*b_21))-(a_28*b_22))-(a_6*b_23))+(a_26*b_24))+(a_8*b_25))+(a_24*b_26))+(a_2*b_27))-(a_22*b_28))-(a_4*b_29))+(a_17*b_30))+(a_11*b_31)), ((((((((((((((((((((((((((((((((a_21*b_0)+(a_15*b_1))-(a_28*b_2))-(a_29*b_3))-(a_9*b_4))+(a_8*b_5))+(a_24*b_6))+(a_25*b_7))+(a_5*b_8))-(a_4*b_9))-(a_31*b_10))-(a_18*b_11))+(a_17*b_12))-(a_20*b_13))+(a_19*b_14))+(a_1*b_15))-(a_30*b_16))-(a_12*b_17))+(a_11*b_18))-(a_14*b_19))+(a_13*b_20))+(a_0*b_21))+(a_27*b_22))-(a_26*b_23))-(a_6*b_24))-(a_7*b_25))-(a_23*b_26))+(a_22*b_27))+(a_2*b_28))+(a_3*b_29))-(a_16*b_30))-(a_10*b_31)), ((((((((((((((((((((((((((((((((a_22*b_0)+(a_26*b_1))+(a_13*b_2))-(a_11*b_3))+(a_10*b_4))+(a_30*b_5))-(a_19*b_6))+(a_17*b_7))-(a_16*b_8))-(a_31*b_9))+(a_4*b_10))-(a_3*b_11))-(a_25*b_12))+(a_2*b_13))+(a_24*b_14))-(a_23*b_15))+(a_8*b_16))-(a_7*b_17))-(a_29*b_18))+(a_6*b_19))+(a_28*b_20))-(a_27*b_21))+(a_0*b_22))+(a_15*b_23))-(a_14*b_24))+(a_12*b_25))-(a_1*b_26))-(a_21*b_27))+(a_20*b_28))-(a_18*b_29))-(a_5*b_30))-(a_9*b_31)), ((((((((((((((((((((((((((((((((a_23*b_0)+(a_27*b_1))+(a_14*b_2))-(a_12*b_3))+(a_30*b_4))+(a_10*b_5))-(a_20*b_6))+(a_18*b_7))-(a_31*b_8))-(a_16*b_9))+(a_5*b_10))-(a_25*b_11))-(a_3*b_12))+(a_24*b_13))+(a_2*b_14))-(a_22*b_15))+(a_9*b_16))-(a_29*b_17))-(a_7*b_18))+(a_28*b_19))+(a_6*b_20))-(a_26*b_21))+(a_15*b_22))+(a_0*b_23))-(a_13*b_24))+(a_11*b_25))-(a_21*b_26))-(a_1*b_27))+(a_19*b_28))-(a_17*b_29))-(a_4*b_30))-(a_8*b_31)), ((((((((((((((((((((((((((((((((a_24*b_0)+(a_28*b_1))+(a_15*b_2))-(a_30*b_3))-(a_12*b_4))+(a_11*b_5))-(a_21*b_6))+(a_31*b_7))+(a_18*b_8))-(a_17*b_9))+(a_25*b_10))+(a_5*b_11))-(a_4*b_12))-(a_23*b_13))+(a_22*b_14))+(a_2*b_15))+(a_29*b_16))+(a_9*b_17))-(a_8*b_18))-(a_27*b_19))+(a_26*b_20))+(a_6*b_21))-(a_14*b_22))+(a_13*b_23))+(a_0*b_24))-(a_10*b_25))+(a_20*b_26))-(a_19*b_27))-(a_1*b_28))+(a_16*b_29))+(a_3*b_30))+(a_7*b_31)), ((((((((((((((((((((((((((((((((a_25*b_0)+(a_29*b_1))+(a_30*b_2))+(a_15*b_3))-(a_14*b_4))+(a_13*b_5))-(a_31*b_6))-(a_21*b_7))+(a_20*b_8))-(a_19*b_9))-(a_24*b_10))+(a_23*b_11))-(a_22*b_12))+(a_5*b_13))-(a_4*b_14))+(a_3*b_15))-(a_28*b_16))+(a_27*b_17))-(a_26*b_18))+(a_9*b_19))-(a_8*b_20))+(a_7*b_21))+(a_12*b_22))-(a_11*b_23))+(a_10*b_24))+(a_0*b_25))-(a_18*b_26))+(a_17*b_27))-(a_16*b_28))-(a_1*b_29))-(a_2*b_30))-(a_6*b_31)), ((((((((((((((((((((((((((((((((a_26*b_0)+(a_22*b_1))-(a_19*b_2))+(a_17*b_3))-(a_16*b_4))-(a_31*b_5))+(a_13*b_6))-(a_11*b_7))+(a_10*b_8))+(a_30*b_9))+(a_8*b_10))-(a_7*b_11))-(a_29*b_12))+(a_6*b_13))+(a_28*b_14))-(a_27*b_15))+(a_4*b_16))-(a_3*b_17))-(a_25*b_18))+(a_2*b_19))+(a_24*b_20))-(a_23*b_21))-(a_1*b_22))-(a_21*b_23))+(a_20*b_24))-(a_18*b_25))+(a_0*b_26))+(a_15*b_27))-(a_14*b_28))+(a_12*b_29))-(a_9*b_30))-(a_5*b_31)), ((((((((((((((((((((((((((((((((a_27*b_0)+(a_23*b_1))-(a_20*b_2))+(a_18*b_3))-(a_31*b_4))-(a_16*b_5))+(a_14*b_6))-(a_12*b_7))+(a_30*b_8))+(a_10*b_9))+(a_9*b_10))-(a_29*b_11))-(a_7*b_12))+(a_28*b_13))+(a_6*b_14))-(a_26*b_15))+(a_5*b_16))-(a_25*b_17))-(a_3*b_18))+(a_24*b_19))+(a_2*b_20))-(a_22*b_21))-(a_21*b_22))-(a_1*b_23))+(a_19*b_24))-(a_17*b_25))+(a_15*b_26))+(a_0*b_27))-(a_13*b_28))+(a_11*b_29))-(a_8*b_30))-(a_4*b_31)), ((((((((((((((((((((((((((((((((a_28*b_0)+(a_24*b_1))-(a_21*b_2))+(a_31*b_3))+(a_18*b_4))-(a_17*b_5))+(a_15*b_6))-(a_30*b_7))-(a_12*b_8))+(a_11*b_9))+(a_29*b_10))+(a_9*b_11))-(a_8*b_12))-(a_27*b_13))+(a_26*b_14))+(a_6*b_15))+(a_25*b_16))+(a_5*b_17))-(a_4*b_18))-(a_23*b_19))+(a_22*b_20))+(a_2*b_21))+(a_20*b_22))-(a_19*b_23))-(a_1*b_24))+(a_16*b_25))-(a_14*b_26))+(a_13*b_27))+(a_0*b_28))-(a_10*b_29))+(a_7*b_30))+(a_3*b_31)), ((((((((((((((((((((((((((((((((a_29*b_0)+(a_25*b_1))-(a_31*b_2))-(a_21*b_3))+(a_20*b_4))-(a_19*b_5))+(a_30*b_6))+(a_15*b_7))-(a_14*b_8))+(a_13*b_9))-(a_28*b_10))+(a_27*b_11))-(a_26*b_12))+(a_9*b_13))-(a_8*b_14))+(a_7*b_15))-(a_24*b_16))+(a_23*b_17))-(a_22*b_18))+(a_5*b_19))-(a_4*b_20))+(a_3*b_21))-(a_18*b_22))+(a_17*b_23))-(a_16*b_24))-(a_1*b_25))+(a_12*b_26))-(a_11*b_27))+(a_10*b_28))+(a_0*b_29))-(a_6*b_30))-(a_2*b_31)), ((((((((((((((((((((((((((((((((a_30*b_0)+(a_31*b_1))+(a_25*b_2))-(a_24*b_3))+(a_23*b_4))-(a_22*b_5))-(a_29*b_6))+(a_28*b_7))-(a_27*b_8))+(a_26*b_9))+(a_15*b_10))-(a_14*b_11))+(a_13*b_12))+(a_12*b_13))-(a_11*b_14))+(a_10*b_15))+(a_21*b_16))-(a_20*b_17))+(a_19*b_18))+(a_18*b_19))-(a_17*b_20))+(a_16*b_21))+(a_5*b_22))-(a_4*b_23))+(a_3*b_24))-(a_2*b_25))-(a_9*b_26))+(a_8*b_27))-(a_7*b_28))+(a_6*b_29))+(a_0*b_30))+(a_1*b_31)), ((((((((((((((((((((((((((((((((a_31*b_0)+(a_30*b_1))-(a_29*b_2))+(a_28*b_3))-(a_27*b_4))+(a_26*b_5))+(a_25*b_6))-(a_24*b_7))+(a_23*b_8))-(a_22*b_9))+(a_21*b_10))-(a_20*b_11))+(a_19*b_12))+(a_18*b_13))-(a_17*b_14))+(a_16*b_15))+(a_15*b_16))-(a_14*b_17))+(a_13*b_18))+(a_12*b_19))-(a_11*b_20))+(a_10*b_21))-(a_9*b_22))+(a_8*b_23))-(a_7*b_24))+(a_6*b_25))+(a_5*b_26))-(a_4*b_27))+(a_3*b_28))-(a_2*b_29))+(a_1*b_30))+(a_0*b_31))}

        // {((((((((((((((((((((((((((((((((b_0*a_0)+(b_1*a_1))+(b_2*a_2))+(b_3*a_3))+(b_4*a_4))-(b_5*a_5))-(b_6*a_6))-(b_7*a_7))-(b_8*a_8))+(b_9*a_9))-(b_10*a_10))-(b_11*a_11))+(b_12*a_12))-(b_13*a_13))+(b_14*a_14))+(b_15*a_15))-(b_16*a_16))-(b_17*a_17))+(b_18*a_18))-(b_19*a_19))+(b_20*a_20))+(b_21*a_21))-(b_22*a_22))+(b_23*a_23))+(b_24*a_24))+(b_25*a_25))+(b_26*a_26))-(b_27*a_27))-(b_28*a_28))-(b_29*a_29))-(b_30*a_30))-(b_31*a_31)), 
        //  ((((((((((((((((((((((((((((((((b_0*a_1)+(b_1*a_0))-(b_2*a_6))-(b_3*a_7))-(b_4*a_8))+(b_5*a_9))+(b_6*a_2))+(b_7*a_3))+(b_8*a_4))-(b_9*a_5))-(b_10*a_16))-(b_11*a_17))+(b_12*a_18))-(b_13*a_19))+(b_14*a_20))+(b_15*a_21))-(b_16*a_10))-(b_17*a_11))+(b_18*a_12))-(b_19*a_13))+(b_20*a_14))+(b_21*a_15))+(b_22*a_26))-(b_23*a_27))-(b_24*a_28))-(b_25*a_29))-(b_26*a_22))+(b_27*a_23))+(b_28*a_24))+(b_29*a_25))-(b_30*a_31))-(b_31*a_30)), ((((((((((((((((((((((((((((((((b_0*a_2)+(b_1*a_6))+(b_2*a_0))-(b_3*a_10))-(b_4*a_11))+(b_5*a_12))-(b_6*a_1))+(b_7*a_16))+(b_8*a_17))-(b_9*a_18))+(b_10*a_3))+(b_11*a_4))-(b_12*a_5))-(b_13*a_22))+(b_14*a_23))+(b_15*a_24))+(b_16*a_7))+(b_17*a_8))-(b_18*a_9))-(b_19*a_26))+(b_20*a_27))+(b_21*a_28))-(b_22*a_13))+(b_23*a_14))+(b_24*a_15))-(b_25*a_30))+(b_26*a_19))-(b_27*a_20))-(b_28*a_21))+(b_29*a_31))+(b_30*a_25))+(b_31*a_29)), ((((((((((((((((((((((((((((((((b_0*a_3)+(b_1*a_7))+(b_2*a_10))+(b_3*a_0))-(b_4*a_13))+(b_5*a_14))-(b_6*a_16))-(b_7*a_1))+(b_8*a_19))-(b_9*a_20))-(b_10*a_2))+(b_11*a_22))-(b_12*a_23))+(b_13*a_4))-(b_14*a_5))+(b_15*a_25))-(b_16*a_6))+(b_17*a_26))-(b_18*a_27))+(b_19*a_8))-(b_20*a_9))+(b_21*a_29))+(b_22*a_11))-(b_23*a_12))+(b_24*a_30))+(b_25*a_15))-(b_26*a_17))+(b_27*a_18))-(b_28*a_31))-(b_29*a_21))-(b_30*a_24))-(b_31*a_28)), ((((((((((((((((((((((((((((((((b_0*a_4)+(b_1*a_8))+(b_2*a_11))+(b_3*a_13))+(b_4*a_0))+(b_5*a_15))-(b_6*a_17))-(b_7*a_19))-(b_8*a_1))-(b_9*a_21))-(b_10*a_22))-(b_11*a_2))-(b_12*a_24))-(b_13*a_3))-(b_14*a_25))-(b_15*a_5))-(b_16*a_26))-(b_17*a_6))-(b_18*a_28))-(b_19*a_7))-(b_20*a_29))-(b_21*a_9))-(b_22*a_10))-(b_23*a_30))-(b_24*a_12))-(b_25*a_14))+(b_26*a_16))+(b_27*a_31))+(b_28*a_18))+(b_29*a_20))+(b_30*a_23))+(b_31*a_27)), ((((((((((((((((((((((((((((((((b_0*a_5)+(b_1*a_9))+(b_2*a_12))+(b_3*a_14))+(b_4*a_15))+(b_5*a_0))-(b_6*a_18))-(b_7*a_20))-(b_8*a_21))-(b_9*a_1))-(b_10*a_23))-(b_11*a_24))-(b_12*a_2))-(b_13*a_25))-(b_14*a_3))-(b_15*a_4))-(b_16*a_27))-(b_17*a_28))-(b_18*a_6))-(b_19*a_29))-(b_20*a_7))-(b_21*a_8))-(b_22*a_30))-(b_23*a_10))-(b_24*a_11))-(b_25*a_13))+(b_26*a_31))+(b_27*a_16))+(b_28*a_17))+(b_29*a_19))+(b_30*a_22))+(b_31*a_26)), ((((((((((((((((((((((((((((((((b_0*a_6)+(b_1*a_2))-(b_2*a_1))+(b_3*a_16))+(b_4*a_17))-(b_5*a_18))+(b_6*a_0))-(b_7*a_10))-(b_8*a_11))+(b_9*a_12))+(b_10*a_7))+(b_11*a_8))-(b_12*a_9))-(b_13*a_26))+(b_14*a_27))+(b_15*a_28))+(b_16*a_3))+(b_17*a_4))-(b_18*a_5))-(b_19*a_22))+(b_20*a_23))+(b_21*a_24))+(b_22*a_19))-(b_23*a_20))-(b_24*a_21))+(b_25*a_31))-(b_26*a_13))+(b_27*a_14))+(b_28*a_15))-(b_29*a_30))+(b_30*a_29))+(b_31*a_25)), ((((((((((((((((((((((((((((((((b_0*a_7)+(b_1*a_3))-(b_2*a_16))-(b_3*a_1))+(b_4*a_19))-(b_5*a_20))+(b_6*a_10))+(b_7*a_0))-(b_8*a_13))+(b_9*a_14))-(b_10*a_6))+(b_11*a_26))-(b_12*a_27))+(b_13*a_8))-(b_14*a_9))+(b_15*a_29))-(b_16*a_2))+(b_17*a_22))-(b_18*a_23))+(b_19*a_4))-(b_20*a_5))+(b_21*a_25))-(b_22*a_17))+(b_23*a_18))-(b_24*a_31))-(b_25*a_21))+(b_26*a_11))-(b_27*a_12))+(b_28*a_30))+(b_29*a_15))-(b_30*a_28))-(b_31*a_24)), ((((((((((((((((((((((((((((((((b_0*a_8)+(b_1*a_4))-(b_2*a_17))-(b_3*a_19))-(b_4*a_1))-(b_5*a_21))+(b_6*a_11))+(b_7*a_13))+(b_8*a_0))+(b_9*a_15))-(b_10*a_26))-(b_11*a_6))-(b_12*a_28))-(b_13*a_7))-(b_14*a_29))-(b_15*a_9))-(b_16*a_22))-(b_17*a_2))-(b_18*a_24))-(b_19*a_3))-(b_20*a_25))-(b_21*a_5))+(b_22*a_16))+(b_23*a_31))+(b_24*a_18))+(b_25*a_20))-(b_26*a_10))-(b_27*a_30))-(b_28*a_12))-(b_29*a_14))+(b_30*a_27))+(b_31*a_23)), ((((((((((((((((((((((((((((((((b_0*a_9)+(b_1*a_5))-(b_2*a_18))-(b_3*a_20))-(b_4*a_21))-(b_5*a_1))+(b_6*a_12))+(b_7*a_14))+(b_8*a_15))+(b_9*a_0))-(b_10*a_27))-(b_11*a_28))-(b_12*a_6))-(b_13*a_29))-(b_14*a_7))-(b_15*a_8))-(b_16*a_23))-(b_17*a_24))-(b_18*a_2))-(b_19*a_25))-(b_20*a_3))-(b_21*a_4))+(b_22*a_31))+(b_23*a_16))+(b_24*a_17))+(b_25*a_19))-(b_26*a_30))-(b_27*a_10))-(b_28*a_11))-(b_29*a_13))+(b_30*a_26))+(b_31*a_22)), ((((((((((((((((((((((((((((((((b_0*a_10)+(b_1*a_16))+(b_2*a_3))-(b_3*a_2))+(b_4*a_22))-(b_5*a_23))-(b_6*a_7))+(b_7*a_6))-(b_8*a_26))+(b_9*a_27))+(b_10*a_0))-(b_11*a_13))+(b_12*a_14))+(b_13*a_11))-(b_14*a_12))+(b_15*a_30))+(b_16*a_1))-(b_17*a_19))+(b_18*a_20))+(b_19*a_17))-(b_20*a_18))+(b_21*a_31))+(b_22*a_4))-(b_23*a_5))+(b_24*a_25))-(b_25*a_24))-(b_26*a_8))+(b_27*a_9))-(b_28*a_29))+(b_29*a_28))+(b_30*a_15))+(b_31*a_21)), ((((((((((((((((((((((((((((((((b_0*a_11)+(b_1*a_17))+(b_2*a_4))-(b_3*a_22))-(b_4*a_2))-(b_5*a_24))-(b_6*a_8))+(b_7*a_26))+(b_8*a_6))+(b_9*a_28))+(b_10*a_13))+(b_11*a_0))+(b_12*a_15))-(b_13*a_10))-(b_14*a_30))-(b_15*a_12))+(b_16*a_19))+(b_17*a_1))+(b_18*a_21))-(b_19*a_16))-(b_20*a_31))-(b_21*a_18))-(b_22*a_3))-(b_23*a_25))-(b_24*a_5))+(b_25*a_23))+(b_26*a_7))+(b_27*a_29))+(b_28*a_9))-(b_29*a_27))-(b_30*a_14))-(b_31*a_20)), ((((((((((((((((((((((((((((((((b_0*a_12)+(b_1*a_18))+(b_2*a_5))-(b_3*a_23))-(b_4*a_24))-(b_5*a_2))-(b_6*a_9))+(b_7*a_27))+(b_8*a_28))+(b_9*a_6))+(b_10*a_14))+(b_11*a_15))+(b_12*a_0))-(b_13*a_30))-(b_14*a_10))-(b_15*a_11))+(b_16*a_20))+(b_17*a_21))+(b_18*a_1))-(b_19*a_31))-(b_20*a_16))-(b_21*a_17))-(b_22*a_25))-(b_23*a_3))-(b_24*a_4))+(b_25*a_22))+(b_26*a_29))+(b_27*a_7))+(b_28*a_8))-(b_29*a_26))-(b_30*a_13))-(b_31*a_19)), ((((((((((((((((((((((((((((((((b_0*a_13)+(b_1*a_19))+(b_2*a_22))+(b_3*a_4))-(b_4*a_3))-(b_5*a_25))-(b_6*a_26))-(b_7*a_8))+(b_8*a_7))+(b_9*a_29))-(b_10*a_11))+(b_11*a_10))+(b_12*a_30))+(b_13*a_0))+(b_14*a_15))-(b_15*a_14))-(b_16*a_17))+(b_17*a_16))+(b_18*a_31))+(b_19*a_1))+(b_20*a_21))-(b_21*a_20))+(b_22*a_2))+(b_23*a_24))-(b_24*a_23))-(b_25*a_5))-(b_26*a_6))-(b_27*a_28))+(b_28*a_27))+(b_29*a_9))+(b_30*a_12))+(b_31*a_18)), ((((((((((((((((((((((((((((((((b_0*a_14)+(b_1*a_20))+(b_2*a_23))+(b_3*a_5))-(b_4*a_25))-(b_5*a_3))-(b_6*a_27))-(b_7*a_9))+(b_8*a_29))+(b_9*a_7))-(b_10*a_12))+(b_11*a_30))+(b_12*a_10))+(b_13*a_15))+(b_14*a_0))-(b_15*a_13))-(b_16*a_18))+(b_17*a_31))+(b_18*a_16))+(b_19*a_21))+(b_20*a_1))-(b_21*a_19))+(b_22*a_24))+(b_23*a_2))-(b_24*a_22))-(b_25*a_4))-(b_26*a_28))-(b_27*a_6))+(b_28*a_26))+(b_29*a_8))+(b_30*a_11))+(b_31*a_17)), ((((((((((((((((((((((((((((((((b_0*a_15)+(b_1*a_21))+(b_2*a_24))+(b_3*a_25))+(b_4*a_5))-(b_5*a_4))-(b_6*a_28))-(b_7*a_29))-(b_8*a_9))+(b_9*a_8))-(b_10*a_30))-(b_11*a_12))+(b_12*a_11))-(b_13*a_14))+(b_14*a_13))+(b_15*a_0))-(b_16*a_31))-(b_17*a_18))+(b_18*a_17))-(b_19*a_20))+(b_20*a_19))+(b_21*a_1))-(b_22*a_23))+(b_23*a_22))+(b_24*a_2))+(b_25*a_3))+(b_26*a_27))-(b_27*a_26))-(b_28*a_6))-(b_29*a_7))-(b_30*a_10))-(b_31*a_16)), ((((((((((((((((((((((((((((((((b_0*a_16)+(b_1*a_10))-(b_2*a_7))+(b_3*a_6))-(b_4*a_26))+(b_5*a_27))+(b_6*a_3))-(b_7*a_2))+(b_8*a_22))-(b_9*a_23))+(b_10*a_1))-(b_11*a_19))+(b_12*a_20))+(b_13*a_17))-(b_14*a_18))+(b_15*a_31))+(b_16*a_0))-(b_17*a_13))+(b_18*a_14))+(b_19*a_11))-(b_20*a_12))+(b_21*a_30))-(b_22*a_8))+(b_23*a_9))-(b_24*a_29))+(b_25*a_28))+(b_26*a_4))-(b_27*a_5))+(b_28*a_25))-(b_29*a_24))+(b_30*a_21))+(b_31*a_15)), ((((((((((((((((((((((((((((((((b_0*a_17)+(b_1*a_11))-(b_2*a_8))+(b_3*a_26))+(b_4*a_6))+(b_5*a_28))+(b_6*a_4))-(b_7*a_22))-(b_8*a_2))-(b_9*a_24))+(b_10*a_19))+(b_11*a_1))+(b_12*a_21))-(b_13*a_16))-(b_14*a_31))-(b_15*a_18))+(b_16*a_13))+(b_17*a_0))+(b_18*a_15))-(b_19*a_10))-(b_20*a_30))-(b_21*a_12))+(b_22*a_7))+(b_23*a_29))+(b_24*a_9))-(b_25*a_27))-(b_26*a_3))-(b_27*a_25))-(b_28*a_5))+(b_29*a_23))-(b_30*a_20))-(b_31*a_14)), ((((((((((((((((((((((((((((((((b_0*a_18)+(b_1*a_12))-(b_2*a_9))+(b_3*a_27))+(b_4*a_28))+(b_5*a_6))+(b_6*a_5))-(b_7*a_23))-(b_8*a_24))-(b_9*a_2))+(b_10*a_20))+(b_11*a_21))+(b_12*a_1))-(b_13*a_31))-(b_14*a_16))-(b_15*a_17))+(b_16*a_14))+(b_17*a_15))+(b_18*a_0))-(b_19*a_30))-(b_20*a_10))-(b_21*a_11))+(b_22*a_29))+(b_23*a_7))+(b_24*a_8))-(b_25*a_26))-(b_26*a_25))-(b_27*a_3))-(b_28*a_4))+(b_29*a_22))-(b_30*a_19))-(b_31*a_13)), ((((((((((((((((((((((((((((((((b_0*a_19)+(b_1*a_13))-(b_2*a_26))-(b_3*a_8))+(b_4*a_7))+(b_5*a_29))+(b_6*a_22))+(b_7*a_4))-(b_8*a_3))-(b_9*a_25))-(b_10*a_17))+(b_11*a_16))+(b_12*a_31))+(b_13*a_1))+(b_14*a_21))-(b_15*a_20))-(b_16*a_11))+(b_17*a_10))+(b_18*a_30))+(b_19*a_0))+(b_20*a_15))-(b_21*a_14))-(b_22*a_6))-(b_23*a_28))+(b_24*a_27))+(b_25*a_9))+(b_26*a_2))+(b_27*a_24))-(b_28*a_23))-(b_29*a_5))+(b_30*a_18))+(b_31*a_12)), ((((((((((((((((((((((((((((((((b_0*a_20)+(b_1*a_14))-(b_2*a_27))-(b_3*a_9))+(b_4*a_29))+(b_5*a_7))+(b_6*a_23))+(b_7*a_5))-(b_8*a_25))-(b_9*a_3))-(b_10*a_18))+(b_11*a_31))+(b_12*a_16))+(b_13*a_21))+(b_14*a_1))-(b_15*a_19))-(b_16*a_12))+(b_17*a_30))+(b_18*a_10))+(b_19*a_15))+(b_20*a_0))-(b_21*a_13))-(b_22*a_28))-(b_23*a_6))+(b_24*a_26))+(b_25*a_8))+(b_26*a_24))+(b_27*a_2))-(b_28*a_22))-(b_29*a_4))+(b_30*a_17))+(b_31*a_11)), ((((((((((((((((((((((((((((((((b_0*a_21)+(b_1*a_15))-(b_2*a_28))-(b_3*a_29))-(b_4*a_9))+(b_5*a_8))+(b_6*a_24))+(b_7*a_25))+(b_8*a_5))-(b_9*a_4))-(b_10*a_31))-(b_11*a_18))+(b_12*a_17))-(b_13*a_20))+(b_14*a_19))+(b_15*a_1))-(b_16*a_30))-(b_17*a_12))+(b_18*a_11))-(b_19*a_14))+(b_20*a_13))+(b_21*a_0))+(b_22*a_27))-(b_23*a_26))-(b_24*a_6))-(b_25*a_7))-(b_26*a_23))+(b_27*a_22))+(b_28*a_2))+(b_29*a_3))-(b_30*a_16))-(b_31*a_10)), ((((((((((((((((((((((((((((((((b_0*a_22)+(b_1*a_26))+(b_2*a_13))-(b_3*a_11))+(b_4*a_10))+(b_5*a_30))-(b_6*a_19))+(b_7*a_17))-(b_8*a_16))-(b_9*a_31))+(b_10*a_4))-(b_11*a_3))-(b_12*a_25))+(b_13*a_2))+(b_14*a_24))-(b_15*a_23))+(b_16*a_8))-(b_17*a_7))-(b_18*a_29))+(b_19*a_6))+(b_20*a_28))-(b_21*a_27))+(b_22*a_0))+(b_23*a_15))-(b_24*a_14))+(b_25*a_12))-(b_26*a_1))-(b_27*a_21))+(b_28*a_20))-(b_29*a_18))-(b_30*a_5))-(b_31*a_9)), ((((((((((((((((((((((((((((((((b_0*a_23)+(b_1*a_27))+(b_2*a_14))-(b_3*a_12))+(b_4*a_30))+(b_5*a_10))-(b_6*a_20))+(b_7*a_18))-(b_8*a_31))-(b_9*a_16))+(b_10*a_5))-(b_11*a_25))-(b_12*a_3))+(b_13*a_24))+(b_14*a_2))-(b_15*a_22))+(b_16*a_9))-(b_17*a_29))-(b_18*a_7))+(b_19*a_28))+(b_20*a_6))-(b_21*a_26))+(b_22*a_15))+(b_23*a_0))-(b_24*a_13))+(b_25*a_11))-(b_26*a_21))-(b_27*a_1))+(b_28*a_19))-(b_29*a_17))-(b_30*a_4))-(b_31*a_8)), ((((((((((((((((((((((((((((((((b_0*a_24)+(b_1*a_28))+(b_2*a_15))-(b_3*a_30))-(b_4*a_12))+(b_5*a_11))-(b_6*a_21))+(b_7*a_31))+(b_8*a_18))-(b_9*a_17))+(b_10*a_25))+(b_11*a_5))-(b_12*a_4))-(b_13*a_23))+(b_14*a_22))+(b_15*a_2))+(b_16*a_29))+(b_17*a_9))-(b_18*a_8))-(b_19*a_27))+(b_20*a_26))+(b_21*a_6))-(b_22*a_14))+(b_23*a_13))+(b_24*a_0))-(b_25*a_10))+(b_26*a_20))-(b_27*a_19))-(b_28*a_1))+(b_29*a_16))+(b_30*a_3))+(b_31*a_7)), ((((((((((((((((((((((((((((((((b_0*a_25)+(b_1*a_29))+(b_2*a_30))+(b_3*a_15))-(b_4*a_14))+(b_5*a_13))-(b_6*a_31))-(b_7*a_21))+(b_8*a_20))-(b_9*a_19))-(b_10*a_24))+(b_11*a_23))-(b_12*a_22))+(b_13*a_5))-(b_14*a_4))+(b_15*a_3))-(b_16*a_28))+(b_17*a_27))-(b_18*a_26))+(b_19*a_9))-(b_20*a_8))+(b_21*a_7))+(b_22*a_12))-(b_23*a_11))+(b_24*a_10))+(b_25*a_0))-(b_26*a_18))+(b_27*a_17))-(b_28*a_16))-(b_29*a_1))-(b_30*a_2))-(b_31*a_6)), ((((((((((((((((((((((((((((((((b_0*a_26)+(b_1*a_22))-(b_2*a_19))+(b_3*a_17))-(b_4*a_16))-(b_5*a_31))+(b_6*a_13))-(b_7*a_11))+(b_8*a_10))+(b_9*a_30))+(b_10*a_8))-(b_11*a_7))-(b_12*a_29))+(b_13*a_6))+(b_14*a_28))-(b_15*a_27))+(b_16*a_4))-(b_17*a_3))-(b_18*a_25))+(b_19*a_2))+(b_20*a_24))-(b_21*a_23))-(b_22*a_1))-(b_23*a_21))+(b_24*a_20))-(b_25*a_18))+(b_26*a_0))+(b_27*a_15))-(b_28*a_14))+(b_29*a_12))-(b_30*a_9))-(b_31*a_5)), ((((((((((((((((((((((((((((((((b_0*a_27)+(b_1*a_23))-(b_2*a_20))+(b_3*a_18))-(b_4*a_31))-(b_5*a_16))+(b_6*a_14))-(b_7*a_12))+(b_8*a_30))+(b_9*a_10))+(b_10*a_9))-(b_11*a_29))-(b_12*a_7))+(b_13*a_28))+(b_14*a_6))-(b_15*a_26))+(b_16*a_5))-(b_17*a_25))-(b_18*a_3))+(b_19*a_24))+(b_20*a_2))-(b_21*a_22))-(b_22*a_21))-(b_23*a_1))+(b_24*a_19))-(b_25*a_17))+(b_26*a_15))+(b_27*a_0))-(b_28*a_13))+(b_29*a_11))-(b_30*a_8))-(b_31*a_4)), ((((((((((((((((((((((((((((((((b_0*a_28)+(b_1*a_24))-(b_2*a_21))+(b_3*a_31))+(b_4*a_18))-(b_5*a_17))+(b_6*a_15))-(b_7*a_30))-(b_8*a_12))+(b_9*a_11))+(b_10*a_29))+(b_11*a_9))-(b_12*a_8))-(b_13*a_27))+(b_14*a_26))+(b_15*a_6))+(b_16*a_25))+(b_17*a_5))-(b_18*a_4))-(b_19*a_23))+(b_20*a_22))+(b_21*a_2))+(b_22*a_20))-(b_23*a_19))-(b_24*a_1))+(b_25*a_16))-(b_26*a_14))+(b_27*a_13))+(b_28*a_0))-(b_29*a_10))+(b_30*a_7))+(b_31*a_3)), ((((((((((((((((((((((((((((((((b_0*a_29)+(b_1*a_25))-(b_2*a_31))-(b_3*a_21))+(b_4*a_20))-(b_5*a_19))+(b_6*a_30))+(b_7*a_15))-(b_8*a_14))+(b_9*a_13))-(b_10*a_28))+(b_11*a_27))-(b_12*a_26))+(b_13*a_9))-(b_14*a_8))+(b_15*a_7))-(b_16*a_24))+(b_17*a_23))-(b_18*a_22))+(b_19*a_5))-(b_20*a_4))+(b_21*a_3))-(b_22*a_18))+(b_23*a_17))-(b_24*a_16))-(b_25*a_1))+(b_26*a_12))-(b_27*a_11))+(b_28*a_10))+(b_29*a_0))-(b_30*a_6))-(b_31*a_2)), ((((((((((((((((((((((((((((((((b_0*a_30)+(b_1*a_31))+(b_2*a_25))-(b_3*a_24))+(b_4*a_23))-(b_5*a_22))-(b_6*a_29))+(b_7*a_28))-(b_8*a_27))+(b_9*a_26))+(b_10*a_15))-(b_11*a_14))+(b_12*a_13))+(b_13*a_12))-(b_14*a_11))+(b_15*a_10))+(b_16*a_21))-(b_17*a_20))+(b_18*a_19))+(b_19*a_18))-(b_20*a_17))+(b_21*a_16))+(b_22*a_5))-(b_23*a_4))+(b_24*a_3))-(b_25*a_2))-(b_26*a_9))+(b_27*a_8))-(b_28*a_7))+(b_29*a_6))+(b_30*a_0))+(b_31*a_1)), ((((((((((((((((((((((((((((((((b_0*a_31)+(b_1*a_30))-(b_2*a_29))+(b_3*a_28))-(b_4*a_27))+(b_5*a_26))+(b_6*a_25))-(b_7*a_24))+(b_8*a_23))-(b_9*a_22))+(b_10*a_21))-(b_11*a_20))+(b_12*a_19))+(b_13*a_18))-(b_14*a_17))+(b_15*a_16))+(b_16*a_15))-(b_17*a_14))+(b_18*a_13))+(b_19*a_12))-(b_20*a_11))+(b_21*a_10))-(b_22*a_9))+(b_23*a_8))-(b_24*a_7))+(b_25*a_6))+(b_26*a_5))-(b_27*a_4))+(b_28*a_3))-(b_29*a_2))+(b_30*a_1))+(b_31*a_0))}

        
        res[0]=b[0]*a[0]+b[1]*a[1]+b[2]*a[2]+b[3]*a[3]+b[4]*a[4]-b[5]*a[5]-b[6]*a[6]-b[7]*a[7]-b[8]*a[8]+b[9]*a[9]-b[10]*a[10]-b[11]*a[11]+b[12]*a[12]-b[13]*a[13]+b[14]*a[14]+b[15]*a[15]-b[16]*a[16]-b[17]*a[17]+b[18]*a[18]-b[19]*a[19]+b[20]*a[20]+b[21]*a[21]-b[22]*a[22]+b[23]*a[23]+b[24]*a[24]+b[25]*a[25]+b[26]*a[26]-b[27]*a[27]-b[28]*a[28]-b[29]*a[29]-b[30]*a[30]-b[31]*a[31];
	res[1]=b[1]*a[0]+b[0]*a[1]-b[6]*a[2]-b[7]*a[3]-b[8]*a[4]+b[9]*a[5]+b[2]*a[6]+b[3]*a[7]+b[4]*a[8]-b[5]*a[9]-b[16]*a[10]-b[17]*a[11]+b[18]*a[12]-b[19]*a[13]+b[20]*a[14]+b[21]*a[15]-b[10]*a[16]-b[11]*a[17]+b[12]*a[18]-b[13]*a[19]+b[14]*a[20]+b[15]*a[21]+b[26]*a[22]-b[27]*a[23]-b[28]*a[24]-b[29]*a[25]-b[22]*a[26]+b[23]*a[27]+b[24]*a[28]+b[25]*a[29]-b[31]*a[30]-b[30]*a[31];
	res[2]=b[2]*a[0]+b[6]*a[1]+b[0]*a[2]-b[10]*a[3]-b[11]*a[4]+b[12]*a[5]-b[1]*a[6]+b[16]*a[7]+b[17]*a[8]-b[18]*a[9]+b[3]*a[10]+b[4]*a[11]-b[5]*a[12]-b[22]*a[13]+b[23]*a[14]+b[24]*a[15]+b[7]*a[16]+b[8]*a[17]-b[9]*a[18]-b[26]*a[19]+b[27]*a[20]+b[28]*a[21]-b[13]*a[22]+b[14]*a[23]+b[15]*a[24]-b[30]*a[25]+b[19]*a[26]-b[20]*a[27]-b[21]*a[28]+b[31]*a[29]+b[25]*a[30]+b[29]*a[31];
	res[3]=b[3]*a[0]+b[7]*a[1]+b[10]*a[2]+b[0]*a[3]-b[13]*a[4]+b[14]*a[5]-b[16]*a[6]-b[1]*a[7]+b[19]*a[8]-b[20]*a[9]-b[2]*a[10]+b[22]*a[11]-b[23]*a[12]+b[4]*a[13]-b[5]*a[14]+b[25]*a[15]-b[6]*a[16]+b[26]*a[17]-b[27]*a[18]+b[8]*a[19]-b[9]*a[20]+b[29]*a[21]+b[11]*a[22]-b[12]*a[23]+b[30]*a[24]+b[15]*a[25]-b[17]*a[26]+b[18]*a[27]-b[31]*a[28]-b[21]*a[29]-b[24]*a[30]-b[28]*a[31];
	res[4]=b[4]*a[0]+b[8]*a[1]+b[11]*a[2]+b[13]*a[3]+b[0]*a[4]+b[15]*a[5]-b[17]*a[6]-b[19]*a[7]-b[1]*a[8]-b[21]*a[9]-b[22]*a[10]-b[2]*a[11]-b[24]*a[12]-b[3]*a[13]-b[25]*a[14]-b[5]*a[15]-b[26]*a[16]-b[6]*a[17]-b[28]*a[18]-b[7]*a[19]-b[29]*a[20]-b[9]*a[21]-b[10]*a[22]-b[30]*a[23]-b[12]*a[24]-b[14]*a[25]+b[16]*a[26]+b[31]*a[27]+b[18]*a[28]+b[20]*a[29]+b[23]*a[30]+b[27]*a[31];
	res[5]=b[5]*a[0]+b[9]*a[1]+b[12]*a[2]+b[14]*a[3]+b[15]*a[4]+b[0]*a[5]-b[18]*a[6]-b[20]*a[7]-b[21]*a[8]-b[1]*a[9]-b[23]*a[10]-b[24]*a[11]-b[2]*a[12]-b[25]*a[13]-b[3]*a[14]-b[4]*a[15]-b[27]*a[16]-b[28]*a[17]-b[6]*a[18]-b[29]*a[19]-b[7]*a[20]-b[8]*a[21]-b[30]*a[22]-b[10]*a[23]-b[11]*a[24]-b[13]*a[25]+b[31]*a[26]+b[16]*a[27]+b[17]*a[28]+b[19]*a[29]+b[22]*a[30]+b[26]*a[31];
	res[6]=b[6]*a[0]+b[2]*a[1]-b[1]*a[2]+b[16]*a[3]+b[17]*a[4]-b[18]*a[5]+b[0]*a[6]-b[10]*a[7]-b[11]*a[8]+b[12]*a[9]+b[7]*a[10]+b[8]*a[11]-b[9]*a[12]-b[26]*a[13]+b[27]*a[14]+b[28]*a[15]+b[3]*a[16]+b[4]*a[17]-b[5]*a[18]-b[22]*a[19]+b[23]*a[20]+b[24]*a[21]+b[19]*a[22]-b[20]*a[23]-b[21]*a[24]+b[31]*a[25]-b[13]*a[26]+b[14]*a[27]+b[15]*a[28]-b[30]*a[29]+b[29]*a[30]+b[25]*a[31];
	res[7]=b[7]*a[0]+b[3]*a[1]-b[16]*a[2]-b[1]*a[3]+b[19]*a[4]-b[20]*a[5]+b[10]*a[6]+b[0]*a[7]-b[13]*a[8]+b[14]*a[9]-b[6]*a[10]+b[26]*a[11]-b[27]*a[12]+b[8]*a[13]-b[9]*a[14]+b[29]*a[15]-b[2]*a[16]+b[22]*a[17]-b[23]*a[18]+b[4]*a[19]-b[5]*a[20]+b[25]*a[21]-b[17]*a[22]+b[18]*a[23]-b[31]*a[24]-b[21]*a[25]+b[11]*a[26]-b[12]*a[27]+b[30]*a[28]+b[15]*a[29]-b[28]*a[30]-b[24]*a[31];
	res[8]=b[8]*a[0]+b[4]*a[1]-b[17]*a[2]-b[19]*a[3]-b[1]*a[4]-b[21]*a[5]+b[11]*a[6]+b[13]*a[7]+b[0]*a[8]+b[15]*a[9]-b[26]*a[10]-b[6]*a[11]-b[28]*a[12]-b[7]*a[13]-b[29]*a[14]-b[9]*a[15]-b[22]*a[16]-b[2]*a[17]-b[24]*a[18]-b[3]*a[19]-b[25]*a[20]-b[5]*a[21]+b[16]*a[22]+b[31]*a[23]+b[18]*a[24]+b[20]*a[25]-b[10]*a[26]-b[30]*a[27]-b[12]*a[28]-b[14]*a[29]+b[27]*a[30]+b[23]*a[31];
	res[9]=b[9]*a[0]+b[5]*a[1]-b[18]*a[2]-b[20]*a[3]-b[21]*a[4]-b[1]*a[5]+b[12]*a[6]+b[14]*a[7]+b[15]*a[8]+b[0]*a[9]-b[27]*a[10]-b[28]*a[11]-b[6]*a[12]-b[29]*a[13]-b[7]*a[14]-b[8]*a[15]-b[23]*a[16]-b[24]*a[17]-b[2]*a[18]-b[25]*a[19]-b[3]*a[20]-b[4]*a[21]+b[31]*a[22]+b[16]*a[23]+b[17]*a[24]+b[19]*a[25]-b[30]*a[26]-b[10]*a[27]-b[11]*a[28]-b[13]*a[29]+b[26]*a[30]+b[22]*a[31];
	res[10]=b[10]*a[0]+b[16]*a[1]+b[3]*a[2]-b[2]*a[3]+b[22]*a[4]-b[23]*a[5]-b[7]*a[6]+b[6]*a[7]-b[26]*a[8]+b[27]*a[9]+b[0]*a[10]-b[13]*a[11]+b[14]*a[12]+b[11]*a[13]-b[12]*a[14]+b[30]*a[15]+b[1]*a[16]-b[19]*a[17]+b[20]*a[18]+b[17]*a[19]-b[18]*a[20]+b[31]*a[21]+b[4]*a[22]-b[5]*a[23]+b[25]*a[24]-b[24]*a[25]-b[8]*a[26]+b[9]*a[27]-b[29]*a[28]+b[28]*a[29]+b[15]*a[30]+b[21]*a[31];
	res[11]=b[11]*a[0]+b[17]*a[1]+b[4]*a[2]-b[22]*a[3]-b[2]*a[4]-b[24]*a[5]-b[8]*a[6]+b[26]*a[7]+b[6]*a[8]+b[28]*a[9]+b[13]*a[10]+b[0]*a[11]+b[15]*a[12]-b[10]*a[13]-b[30]*a[14]-b[12]*a[15]+b[19]*a[16]+b[1]*a[17]+b[21]*a[18]-b[16]*a[19]-b[31]*a[20]-b[18]*a[21]-b[3]*a[22]-b[25]*a[23]-b[5]*a[24]+b[23]*a[25]+b[7]*a[26]+b[29]*a[27]+b[9]*a[28]-b[27]*a[29]-b[14]*a[30]-b[20]*a[31];
	res[12]=b[12]*a[0]+b[18]*a[1]+b[5]*a[2]-b[23]*a[3]-b[24]*a[4]-b[2]*a[5]-b[9]*a[6]+b[27]*a[7]+b[28]*a[8]+b[6]*a[9]+b[14]*a[10]+b[15]*a[11]+b[0]*a[12]-b[30]*a[13]-b[10]*a[14]-b[11]*a[15]+b[20]*a[16]+b[21]*a[17]+b[1]*a[18]-b[31]*a[19]-b[16]*a[20]-b[17]*a[21]-b[25]*a[22]-b[3]*a[23]-b[4]*a[24]+b[22]*a[25]+b[29]*a[26]+b[7]*a[27]+b[8]*a[28]-b[26]*a[29]-b[13]*a[30]-b[19]*a[31];
	res[13]=b[13]*a[0]+b[19]*a[1]+b[22]*a[2]+b[4]*a[3]-b[3]*a[4]-b[25]*a[5]-b[26]*a[6]-b[8]*a[7]+b[7]*a[8]+b[29]*a[9]-b[11]*a[10]+b[10]*a[11]+b[30]*a[12]+b[0]*a[13]+b[15]*a[14]-b[14]*a[15]-b[17]*a[16]+b[16]*a[17]+b[31]*a[18]+b[1]*a[19]+b[21]*a[20]-b[20]*a[21]+b[2]*a[22]+b[24]*a[23]-b[23]*a[24]-b[5]*a[25]-b[6]*a[26]-b[28]*a[27]+b[27]*a[28]+b[9]*a[29]+b[12]*a[30]+b[18]*a[31];
	res[14]=b[14]*a[0]+b[20]*a[1]+b[23]*a[2]+b[5]*a[3]-b[25]*a[4]-b[3]*a[5]-b[27]*a[6]-b[9]*a[7]+b[29]*a[8]+b[7]*a[9]-b[12]*a[10]+b[30]*a[11]+b[10]*a[12]+b[15]*a[13]+b[0]*a[14]-b[13]*a[15]-b[18]*a[16]+b[31]*a[17]+b[16]*a[18]+b[21]*a[19]+b[1]*a[20]-b[19]*a[21]+b[24]*a[22]+b[2]*a[23]-b[22]*a[24]-b[4]*a[25]-b[28]*a[26]-b[6]*a[27]+b[26]*a[28]+b[8]*a[29]+b[11]*a[30]+b[17]*a[31];
	res[15]=b[15]*a[0]+b[21]*a[1]+b[24]*a[2]+b[25]*a[3]+b[5]*a[4]-b[4]*a[5]-b[28]*a[6]-b[29]*a[7]-b[9]*a[8]+b[8]*a[9]-b[30]*a[10]-b[12]*a[11]+b[11]*a[12]-b[14]*a[13]+b[13]*a[14]+b[0]*a[15]-b[31]*a[16]-b[18]*a[17]+b[17]*a[18]-b[20]*a[19]+b[19]*a[20]+b[1]*a[21]-b[23]*a[22]+b[22]*a[23]+b[2]*a[24]+b[3]*a[25]+b[27]*a[26]-b[26]*a[27]-b[6]*a[28]-b[7]*a[29]-b[10]*a[30]-b[16]*a[31];
	res[16]=b[16]*a[0]+b[10]*a[1]-b[7]*a[2]+b[6]*a[3]-b[26]*a[4]+b[27]*a[5]+b[3]*a[6]-b[2]*a[7]+b[22]*a[8]-b[23]*a[9]+b[1]*a[10]-b[19]*a[11]+b[20]*a[12]+b[17]*a[13]-b[18]*a[14]+b[31]*a[15]+b[0]*a[16]-b[13]*a[17]+b[14]*a[18]+b[11]*a[19]-b[12]*a[20]+b[30]*a[21]-b[8]*a[22]+b[9]*a[23]-b[29]*a[24]+b[28]*a[25]+b[4]*a[26]-b[5]*a[27]+b[25]*a[28]-b[24]*a[29]+b[21]*a[30]+b[15]*a[31];
	res[17]=b[17]*a[0]+b[11]*a[1]-b[8]*a[2]+b[26]*a[3]+b[6]*a[4]+b[28]*a[5]+b[4]*a[6]-b[22]*a[7]-b[2]*a[8]-b[24]*a[9]+b[19]*a[10]+b[1]*a[11]+b[21]*a[12]-b[16]*a[13]-b[31]*a[14]-b[18]*a[15]+b[13]*a[16]+b[0]*a[17]+b[15]*a[18]-b[10]*a[19]-b[30]*a[20]-b[12]*a[21]+b[7]*a[22]+b[29]*a[23]+b[9]*a[24]-b[27]*a[25]-b[3]*a[26]-b[25]*a[27]-b[5]*a[28]+b[23]*a[29]-b[20]*a[30]-b[14]*a[31];
	res[18]=b[18]*a[0]+b[12]*a[1]-b[9]*a[2]+b[27]*a[3]+b[28]*a[4]+b[6]*a[5]+b[5]*a[6]-b[23]*a[7]-b[24]*a[8]-b[2]*a[9]+b[20]*a[10]+b[21]*a[11]+b[1]*a[12]-b[31]*a[13]-b[16]*a[14]-b[17]*a[15]+b[14]*a[16]+b[15]*a[17]+b[0]*a[18]-b[30]*a[19]-b[10]*a[20]-b[11]*a[21]+b[29]*a[22]+b[7]*a[23]+b[8]*a[24]-b[26]*a[25]-b[25]*a[26]-b[3]*a[27]-b[4]*a[28]+b[22]*a[29]-b[19]*a[30]-b[13]*a[31];
	res[19]=b[19]*a[0]+b[13]*a[1]-b[26]*a[2]-b[8]*a[3]+b[7]*a[4]+b[29]*a[5]+b[22]*a[6]+b[4]*a[7]-b[3]*a[8]-b[25]*a[9]-b[17]*a[10]+b[16]*a[11]+b[31]*a[12]+b[1]*a[13]+b[21]*a[14]-b[20]*a[15]-b[11]*a[16]+b[10]*a[17]+b[30]*a[18]+b[0]*a[19]+b[15]*a[20]-b[14]*a[21]-b[6]*a[22]-b[28]*a[23]+b[27]*a[24]+b[9]*a[25]+b[2]*a[26]+b[24]*a[27]-b[23]*a[28]-b[5]*a[29]+b[18]*a[30]+b[12]*a[31];
	res[20]=b[20]*a[0]+b[14]*a[1]-b[27]*a[2]-b[9]*a[3]+b[29]*a[4]+b[7]*a[5]+b[23]*a[6]+b[5]*a[7]-b[25]*a[8]-b[3]*a[9]-b[18]*a[10]+b[31]*a[11]+b[16]*a[12]+b[21]*a[13]+b[1]*a[14]-b[19]*a[15]-b[12]*a[16]+b[30]*a[17]+b[10]*a[18]+b[15]*a[19]+b[0]*a[20]-b[13]*a[21]-b[28]*a[22]-b[6]*a[23]+b[26]*a[24]+b[8]*a[25]+b[24]*a[26]+b[2]*a[27]-b[22]*a[28]-b[4]*a[29]+b[17]*a[30]+b[11]*a[31];
	res[21]=b[21]*a[0]+b[15]*a[1]-b[28]*a[2]-b[29]*a[3]-b[9]*a[4]+b[8]*a[5]+b[24]*a[6]+b[25]*a[7]+b[5]*a[8]-b[4]*a[9]-b[31]*a[10]-b[18]*a[11]+b[17]*a[12]-b[20]*a[13]+b[19]*a[14]+b[1]*a[15]-b[30]*a[16]-b[12]*a[17]+b[11]*a[18]-b[14]*a[19]+b[13]*a[20]+b[0]*a[21]+b[27]*a[22]-b[26]*a[23]-b[6]*a[24]-b[7]*a[25]-b[23]*a[26]+b[22]*a[27]+b[2]*a[28]+b[3]*a[29]-b[16]*a[30]-b[10]*a[31];
	res[22]=b[22]*a[0]+b[26]*a[1]+b[13]*a[2]-b[11]*a[3]+b[10]*a[4]+b[30]*a[5]-b[19]*a[6]+b[17]*a[7]-b[16]*a[8]-b[31]*a[9]+b[4]*a[10]-b[3]*a[11]-b[25]*a[12]+b[2]*a[13]+b[24]*a[14]-b[23]*a[15]+b[8]*a[16]-b[7]*a[17]-b[29]*a[18]+b[6]*a[19]+b[28]*a[20]-b[27]*a[21]+b[0]*a[22]+b[15]*a[23]-b[14]*a[24]+b[12]*a[25]-b[1]*a[26]-b[21]*a[27]+b[20]*a[28]-b[18]*a[29]-b[5]*a[30]-b[9]*a[31];
	res[23]=b[23]*a[0]+b[27]*a[1]+b[14]*a[2]-b[12]*a[3]+b[30]*a[4]+b[10]*a[5]-b[20]*a[6]+b[18]*a[7]-b[31]*a[8]-b[16]*a[9]+b[5]*a[10]-b[25]*a[11]-b[3]*a[12]+b[24]*a[13]+b[2]*a[14]-b[22]*a[15]+b[9]*a[16]-b[29]*a[17]-b[7]*a[18]+b[28]*a[19]+b[6]*a[20]-b[26]*a[21]+b[15]*a[22]+b[0]*a[23]-b[13]*a[24]+b[11]*a[25]-b[21]*a[26]-b[1]*a[27]+b[19]*a[28]-b[17]*a[29]-b[4]*a[30]-b[8]*a[31];
	res[24]=b[24]*a[0]+b[28]*a[1]+b[15]*a[2]-b[30]*a[3]-b[12]*a[4]+b[11]*a[5]-b[21]*a[6]+b[31]*a[7]+b[18]*a[8]-b[17]*a[9]+b[25]*a[10]+b[5]*a[11]-b[4]*a[12]-b[23]*a[13]+b[22]*a[14]+b[2]*a[15]+b[29]*a[16]+b[9]*a[17]-b[8]*a[18]-b[27]*a[19]+b[26]*a[20]+b[6]*a[21]-b[14]*a[22]+b[13]*a[23]+b[0]*a[24]-b[10]*a[25]+b[20]*a[26]-b[19]*a[27]-b[1]*a[28]+b[16]*a[29]+b[3]*a[30]+b[7]*a[31];
	res[25]=b[25]*a[0]+b[29]*a[1]+b[30]*a[2]+b[15]*a[3]-b[14]*a[4]+b[13]*a[5]-b[31]*a[6]-b[21]*a[7]+b[20]*a[8]-b[19]*a[9]-b[24]*a[10]+b[23]*a[11]-b[22]*a[12]+b[5]*a[13]-b[4]*a[14]+b[3]*a[15]-b[28]*a[16]+b[27]*a[17]-b[26]*a[18]+b[9]*a[19]-b[8]*a[20]+b[7]*a[21]+b[12]*a[22]-b[11]*a[23]+b[10]*a[24]+b[0]*a[25]-b[18]*a[26]+b[17]*a[27]-b[16]*a[28]-b[1]*a[29]-b[2]*a[30]-b[6]*a[31];
	res[26]=b[26]*a[0]+b[22]*a[1]-b[19]*a[2]+b[17]*a[3]-b[16]*a[4]-b[31]*a[5]+b[13]*a[6]-b[11]*a[7]+b[10]*a[8]+b[30]*a[9]+b[8]*a[10]-b[7]*a[11]-b[29]*a[12]+b[6]*a[13]+b[28]*a[14]-b[27]*a[15]+b[4]*a[16]-b[3]*a[17]-b[25]*a[18]+b[2]*a[19]+b[24]*a[20]-b[23]*a[21]-b[1]*a[22]-b[21]*a[23]+b[20]*a[24]-b[18]*a[25]+b[0]*a[26]+b[15]*a[27]-b[14]*a[28]+b[12]*a[29]-b[9]*a[30]-b[5]*a[31];
	res[27]=b[27]*a[0]+b[23]*a[1]-b[20]*a[2]+b[18]*a[3]-b[31]*a[4]-b[16]*a[5]+b[14]*a[6]-b[12]*a[7]+b[30]*a[8]+b[10]*a[9]+b[9]*a[10]-b[29]*a[11]-b[7]*a[12]+b[28]*a[13]+b[6]*a[14]-b[26]*a[15]+b[5]*a[16]-b[25]*a[17]-b[3]*a[18]+b[24]*a[19]+b[2]*a[20]-b[22]*a[21]-b[21]*a[22]-b[1]*a[23]+b[19]*a[24]-b[17]*a[25]+b[15]*a[26]+b[0]*a[27]-b[13]*a[28]+b[11]*a[29]-b[8]*a[30]-b[4]*a[31];
	res[28]=b[28]*a[0]+b[24]*a[1]-b[21]*a[2]+b[31]*a[3]+b[18]*a[4]-b[17]*a[5]+b[15]*a[6]-b[30]*a[7]-b[12]*a[8]+b[11]*a[9]+b[29]*a[10]+b[9]*a[11]-b[8]*a[12]-b[27]*a[13]+b[26]*a[14]+b[6]*a[15]+b[25]*a[16]+b[5]*a[17]-b[4]*a[18]-b[23]*a[19]+b[22]*a[20]+b[2]*a[21]+b[20]*a[22]-b[19]*a[23]-b[1]*a[24]+b[16]*a[25]-b[14]*a[26]+b[13]*a[27]+b[0]*a[28]-b[10]*a[29]+b[7]*a[30]+b[3]*a[31];
	res[29]=b[29]*a[0]+b[25]*a[1]-b[31]*a[2]-b[21]*a[3]+b[20]*a[4]-b[19]*a[5]+b[30]*a[6]+b[15]*a[7]-b[14]*a[8]+b[13]*a[9]-b[28]*a[10]+b[27]*a[11]-b[26]*a[12]+b[9]*a[13]-b[8]*a[14]+b[7]*a[15]-b[24]*a[16]+b[23]*a[17]-b[22]*a[18]+b[5]*a[19]-b[4]*a[20]+b[3]*a[21]-b[18]*a[22]+b[17]*a[23]-b[16]*a[24]-b[1]*a[25]+b[12]*a[26]-b[11]*a[27]+b[10]*a[28]+b[0]*a[29]-b[6]*a[30]-b[2]*a[31];
	res[30]=b[30]*a[0]+b[31]*a[1]+b[25]*a[2]-b[24]*a[3]+b[23]*a[4]-b[22]*a[5]-b[29]*a[6]+b[28]*a[7]-b[27]*a[8]+b[26]*a[9]+b[15]*a[10]-b[14]*a[11]+b[13]*a[12]+b[12]*a[13]-b[11]*a[14]+b[10]*a[15]+b[21]*a[16]-b[20]*a[17]+b[19]*a[18]+b[18]*a[19]-b[17]*a[20]+b[16]*a[21]+b[5]*a[22]-b[4]*a[23]+b[3]*a[24]-b[2]*a[25]-b[9]*a[26]+b[8]*a[27]-b[7]*a[28]+b[6]*a[29]+b[0]*a[30]+b[1]*a[31];
	res[31]=b[31]*a[0]+b[30]*a[1]-b[29]*a[2]+b[28]*a[3]-b[27]*a[4]+b[26]*a[5]+b[25]*a[6]-b[24]*a[7]+b[23]*a[8]-b[22]*a[9]+b[21]*a[10]-b[20]*a[11]+b[19]*a[12]+b[18]*a[13]-b[17]*a[14]+b[16]*a[15]+b[15]*a[16]-b[14]*a[17]+b[13]*a[18]+b[12]*a[19]-b[11]*a[20]+b[10]*a[21]-b[9]*a[22]+b[8]*a[23]-b[7]*a[24]+b[6]*a[25]+b[5]*a[26]-b[4]*a[27]+b[3]*a[28]-b[2]*a[29]+b[1]*a[30]+b[0]*a[31];
        return res;
    }
    
    @Test
    public void testInvoluteRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv", 1);
        System.out.println("mv (sparsity): "+mv.getSparsity().toString());
        System.out.println("mv: "+mv.toString());
        MultivectorSymbolic result = mv.gradeInversion();
        System.out.println("result (sym): "+result.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mv);
        
        List<MultivectorSymbolic> res = new ArrayList<>();
        res.add(result);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, res);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] randomValues = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg = exprGraphFactory.createMultivectorNumeric(randomValues);
        arguments.add(arg);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric out = result2.iterator().next();
            //System.out.println("b=reverse(a)="+out.toString());
            double[] values = out.elements().toArray();
            assertTrue(equals(values, involute(randomValues), mv.getSparsity()));
        } catch (Exception e){}
    }
    
    /**
     * involute.
     *
     * Main involution - grade inversion<p>
     *
     * @param a
     * @return 
     */
    private static double[] involute (double[] _mVec){
        double[] res = new double[32];
	res[0]=_mVec[0];
	res[1]=-_mVec[1];
	res[2]=-_mVec[2];
	res[3]=-_mVec[3];
	res[4]=-_mVec[4];
	res[5]=-_mVec[5];
	res[6]=_mVec[6];
	res[7]=_mVec[7];
	res[8]=_mVec[8];
	res[9]=_mVec[9];
	res[10]=_mVec[10];
	res[11]=_mVec[11];
	res[12]=_mVec[12];
	res[13]=_mVec[13];
	res[14]=_mVec[14];
	res[15]=_mVec[15];
	res[16]=-_mVec[16];
	res[17]=-_mVec[17];
	res[18]=-_mVec[18];
	res[19]=-_mVec[19];
	res[20]=-_mVec[20];
	res[21]=-_mVec[21];
	res[22]=-_mVec[22];
	res[23]=-_mVec[23];
	res[24]=-_mVec[24];
	res[25]=-_mVec[25];
	res[26]=_mVec[26];
	res[27]=_mVec[27];
	res[28]=_mVec[28];
	res[29]=_mVec[29];
	res[30]=_mVec[30];
	res[31]=-_mVec[31];
        return res;
    }
    
    @Test
    public void testReverseRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv");
        System.out.println("reverse (sparsity): "+mv.getSparsity().toString());
        //System.out.println("mv: "+mv.toString());
        MultivectorSymbolic result = mv.reverse();
        System.out.println("result (sym): "+result.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mv);
        
        List<MultivectorSymbolic> res = new ArrayList<>();
        res.add(result);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, res);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] randomValues = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg = exprGraphFactory.createMultivectorNumeric(randomValues);
        arguments.add(arg);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric out = result2.iterator().next();
            System.out.println("reverse(a)="+out.toString());
            double[] values = out.elements().toArray();
            assertTrue(equals(values, reverse(randomValues), mv.getSparsity()));
        } catch (Exception e){}
    }
    private static double[] reverse(double[] a){
        double[] res = new double[32];
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
     * Grade projection/extraction.
     * 
     * Retrives the k-grade part of the multivector.
     * 
     * @param grade
     * @return k-grade part of the multivector
     * @throws IllegalArgumentException if grade <0 or grade > 5
     */
    public static double[] gradeSelection(double[] _mVec, int grade){
        if (grade > 5 || grade < 0) 
            throw new IllegalArgumentException ("Grade "+String.valueOf(grade)+" not allowed!");
        
        double[] arr = new double[32];
        switch (grade){
            case 0 -> arr[0] = _mVec[0];
            case 1 -> {
                for (int i=1;i<=5;i++){
                    arr[i] = _mVec[i];
                }
            }
            case 2 -> {
                for (int i=6;i<=15;i++){
                    arr[i] = _mVec[i];
                }
            }
            case 3 -> {
                for (int i=16;i<=25;i++){
                    arr[i] = _mVec[i];
                }
            }
            case 4 -> {
                for (int i=26;i<=30;i++){
                    arr[i] = _mVec[i];
                }
            }
            case 5 -> arr[31] = _mVec[31];
        }
        return arr;
    }
    
    @Test
    public void testDualRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv");
        System.out.println("mv (sparsity für dual): "+mv.getSparsity().toString());
        System.out.println("mv: (dual) "+mv.toString());
        MultivectorSymbolic result = mv.dual();
        System.out.println("result (dual) (sym): "+result.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mv);
        
        List<MultivectorSymbolic> res = new ArrayList<>();
        res.add(result);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, res);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] randomValues = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg = exprGraphFactory.createMultivectorNumeric(randomValues);
        arguments.add(arg);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric out = result2.iterator().next();
            System.out.println("b=dual(a)="+out.toString());
            double[] values = out.elements().toArray();
            
            double[] test = dual(randomValues);
            DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
            System.out.println(testMatrix.toString());
            assertTrue(equals(values, test, mv.getSparsity()));
        } catch (Exception e){}
    }
    
    @Test
    public void testScalarInverseRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv", 0);
        MultivectorSymbolic result = mv.scalarInverse();
        //System.out.println("result (sym scalarInverse): "+result.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mv);
        
        List<MultivectorSymbolic> res = new ArrayList<>();
        res.add(result);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, res);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] randomValues = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg = exprGraphFactory.createMultivectorNumeric(randomValues);
        arguments.add(arg);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric out = result2.iterator().next();
            System.out.println("b=scalarInverse(a)="+out.toString());
            double[] values = out.elements().toArray();
            assertTrue(values[0] == scalarInverse(randomValues));
        } catch (Exception e){}
    }
    
    private double scalarInverse(double[] mv){
        return 1d/mv[0];
    }
    @Test
    public void testConjugateRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv", 1);
        System.out.println("mv (sparsity): "+mv.getSparsity().toString());
        System.out.println("mv: "+mv.toString());
        MultivectorSymbolic result = mv.cliffordConjugate();
        System.out.println("result (sym, conjugate): "+result.toString());
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mv);
        
        List<MultivectorSymbolic> res = new ArrayList<>();
        res.add(result);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, res);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] randomValues = exprGraphFactory.createRandomCGAMultivector();
        MultivectorNumeric arg = exprGraphFactory.createMultivectorNumeric(randomValues);
        arguments.add(arg);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric out = result2.iterator().next();
            //System.out.println("b=reverse(a)="+out.toString());
            double[] values = out.elements().toArray();
            assertTrue(equals(values, conjugate(randomValues), mv.getSparsity()));
        } catch (Exception e){}
    }
    
    /**
     * Conjugate.
     *
     * Clifford Conjugation
     *
     * @param a
     * @return a.Conjugate()
     */
    private double[] conjugate(double[] _mVec){
        double[] res = new double[32];
        
	res[0]=_mVec[0];
        
	res[1]=-_mVec[1];
	res[2]=-_mVec[2];
	res[3]=-_mVec[3];
	res[4]=-_mVec[4];
	res[5]=-_mVec[5];
	res[6]=-_mVec[6];
	res[7]=-_mVec[7];
	res[8]=-_mVec[8];
	res[9]=-_mVec[9];
	res[10]=-_mVec[10];
	res[11]=-_mVec[11];
	res[12]=-_mVec[12];
	res[13]=-_mVec[13];
	res[14]=-_mVec[14];
	res[15]=-_mVec[15];
        
	res[16]=_mVec[16];
	res[17]=_mVec[17];
	res[18]=_mVec[18];
	res[19]=_mVec[19];
	res[20]=_mVec[20];
	res[21]=_mVec[21];
	res[22]=_mVec[22];
	res[23]=_mVec[23];
	res[24]=_mVec[24];
	res[25]=_mVec[25];
	res[26]=_mVec[26];
	res[27]=_mVec[27];
	res[28]=_mVec[28];
	res[29]=_mVec[29];
	res[30]=_mVec[30];
        
	res[31]=-_mVec[31];
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
    
   
    /**
     * Dot.
     *
     * The inner product - left contraction.
     *
     * @param a
     * @param b
     * @return a | b
     */
    public static double[] lc (double[] a, double[] b){
        double[] res = new double[a.length];
        res[0]=b[0]*a[0]+b[1]*a[1]+b[2]*a[2]+b[3]*a[3]+b[4]*a[4]-b[5]*a[5]-b[6]*a[6]-b[7]*a[7]-b[8]*a[8]+b[9]*a[9]-b[10]*a[10]-b[11]*a[11]+b[12]*a[12]-b[13]*a[13]+b[14]*a[14]+b[15]*a[15]-b[16]*a[16]-b[17]*a[17]+b[18]*a[18]-b[19]*a[19]+b[20]*a[20]+b[21]*a[21]-b[22]*a[22]+b[23]*a[23]+b[24]*a[24]+b[25]*a[25]+b[26]*a[26]-b[27]*a[27]-b[28]*a[28]-b[29]*a[29]-b[30]*a[30]-b[31]*a[31];
	res[1]=b[1]*a[0]+b[0]*a[1]-b[6]*a[2]-b[7]*a[3]-b[8]*a[4]+b[9]*a[5]+b[2]*a[6]+b[3]*a[7]+b[4]*a[8]-b[5]*a[9]-b[16]*a[10]-b[17]*a[11]+b[18]*a[12]-b[19]*a[13]+b[20]*a[14]+b[21]*a[15]-b[10]*a[16]-b[11]*a[17]+b[12]*a[18]-b[13]*a[19]+b[14]*a[20]+b[15]*a[21]+b[26]*a[22]-b[27]*a[23]-b[28]*a[24]-b[29]*a[25]-b[22]*a[26]+b[23]*a[27]+b[24]*a[28]+b[25]*a[29]-b[31]*a[30]-b[30]*a[31];
	res[2]=b[2]*a[0]+b[6]*a[1]+b[0]*a[2]-b[10]*a[3]-b[11]*a[4]+b[12]*a[5]-b[1]*a[6]+b[16]*a[7]+b[17]*a[8]-b[18]*a[9]+b[3]*a[10]+b[4]*a[11]-b[5]*a[12]-b[22]*a[13]+b[23]*a[14]+b[24]*a[15]+b[7]*a[16]+b[8]*a[17]-b[9]*a[18]-b[26]*a[19]+b[27]*a[20]+b[28]*a[21]-b[13]*a[22]+b[14]*a[23]+b[15]*a[24]-b[30]*a[25]+b[19]*a[26]-b[20]*a[27]-b[21]*a[28]+b[31]*a[29]+b[25]*a[30]+b[29]*a[31];
	res[3]=b[3]*a[0]+b[7]*a[1]+b[10]*a[2]+b[0]*a[3]-b[13]*a[4]+b[14]*a[5]-b[16]*a[6]-b[1]*a[7]+b[19]*a[8]-b[20]*a[9]-b[2]*a[10]+b[22]*a[11]-b[23]*a[12]+b[4]*a[13]-b[5]*a[14]+b[25]*a[15]-b[6]*a[16]+b[26]*a[17]-b[27]*a[18]+b[8]*a[19]-b[9]*a[20]+b[29]*a[21]+b[11]*a[22]-b[12]*a[23]+b[30]*a[24]+b[15]*a[25]-b[17]*a[26]+b[18]*a[27]-b[31]*a[28]-b[21]*a[29]-b[24]*a[30]-b[28]*a[31];
	res[4]=b[4]*a[0]+b[8]*a[1]+b[11]*a[2]+b[13]*a[3]+b[0]*a[4]+b[15]*a[5]-b[17]*a[6]-b[19]*a[7]-b[1]*a[8]-b[21]*a[9]-b[22]*a[10]-b[2]*a[11]-b[24]*a[12]-b[3]*a[13]-b[25]*a[14]-b[5]*a[15]-b[26]*a[16]-b[6]*a[17]-b[28]*a[18]-b[7]*a[19]-b[29]*a[20]-b[9]*a[21]-b[10]*a[22]-b[30]*a[23]-b[12]*a[24]-b[14]*a[25]+b[16]*a[26]+b[31]*a[27]+b[18]*a[28]+b[20]*a[29]+b[23]*a[30]+b[27]*a[31];
	res[5]=b[5]*a[0]+b[9]*a[1]+b[12]*a[2]+b[14]*a[3]+b[15]*a[4]+b[0]*a[5]-b[18]*a[6]-b[20]*a[7]-b[21]*a[8]-b[1]*a[9]-b[23]*a[10]-b[24]*a[11]-b[2]*a[12]-b[25]*a[13]-b[3]*a[14]-b[4]*a[15]-b[27]*a[16]-b[28]*a[17]-b[6]*a[18]-b[29]*a[19]-b[7]*a[20]-b[8]*a[21]-b[30]*a[22]-b[10]*a[23]-b[11]*a[24]-b[13]*a[25]+b[31]*a[26]+b[16]*a[27]+b[17]*a[28]+b[19]*a[29]+b[22]*a[30]+b[26]*a[31];
	res[6]=b[6]*a[0]+b[16]*a[3]+b[17]*a[4]-b[18]*a[5]+b[0]*a[6]-b[26]*a[13]+b[27]*a[14]+b[28]*a[15]+b[3]*a[16]+b[4]*a[17]-b[5]*a[18]+b[31]*a[25]-b[13]*a[26]+b[14]*a[27]+b[15]*a[28]+b[25]*a[31];
	res[7]=b[7]*a[0]-b[16]*a[2]+b[19]*a[4]-b[20]*a[5]+b[0]*a[7]+b[26]*a[11]-b[27]*a[12]+b[29]*a[15]-b[2]*a[16]+b[4]*a[19]-b[5]*a[20]-b[31]*a[24]+b[11]*a[26]-b[12]*a[27]+b[15]*a[29]-b[24]*a[31];
	res[8]=b[8]*a[0]-b[17]*a[2]-b[19]*a[3]-b[21]*a[5]+b[0]*a[8]-b[26]*a[10]-b[28]*a[12]-b[29]*a[14]-b[2]*a[17]-b[3]*a[19]-b[5]*a[21]+b[31]*a[23]-b[10]*a[26]-b[12]*a[28]-b[14]*a[29]+b[23]*a[31];
	res[9]=b[9]*a[0]-b[18]*a[2]-b[20]*a[3]-b[21]*a[4]+b[0]*a[9]-b[27]*a[10]-b[28]*a[11]-b[29]*a[13]-b[2]*a[18]-b[3]*a[20]-b[4]*a[21]+b[31]*a[22]-b[10]*a[27]-b[11]*a[28]-b[13]*a[29]+b[22]*a[31];
	res[10]=b[10]*a[0]+b[16]*a[1]+b[22]*a[4]-b[23]*a[5]-b[26]*a[8]+b[27]*a[9]+b[0]*a[10]+b[30]*a[15]+b[1]*a[16]+b[31]*a[21]+b[4]*a[22]-b[5]*a[23]-b[8]*a[26]+b[9]*a[27]+b[15]*a[30]+b[21]*a[31];
	res[11]=b[11]*a[0]+b[17]*a[1]-b[22]*a[3]-b[24]*a[5]+b[26]*a[7]+b[28]*a[9]+b[0]*a[11]-b[30]*a[14]+b[1]*a[17]-b[31]*a[20]-b[3]*a[22]-b[5]*a[24]+b[7]*a[26]+b[9]*a[28]-b[14]*a[30]-b[20]*a[31];
	res[12]=b[12]*a[0]+b[18]*a[1]-b[23]*a[3]-b[24]*a[4]+b[27]*a[7]+b[28]*a[8]+b[0]*a[12]-b[30]*a[13]+b[1]*a[18]-b[31]*a[19]-b[3]*a[23]-b[4]*a[24]+b[7]*a[27]+b[8]*a[28]-b[13]*a[30]-b[19]*a[31];
	res[13]=b[13]*a[0]+b[19]*a[1]+b[22]*a[2]-b[25]*a[5]-b[26]*a[6]+b[29]*a[9]+b[30]*a[12]+b[0]*a[13]+b[31]*a[18]+b[1]*a[19]+b[2]*a[22]-b[5]*a[25]-b[6]*a[26]+b[9]*a[29]+b[12]*a[30]+b[18]*a[31];
	res[14]=b[14]*a[0]+b[20]*a[1]+b[23]*a[2]-b[25]*a[4]-b[27]*a[6]+b[29]*a[8]+b[30]*a[11]+b[0]*a[14]+b[31]*a[17]+b[1]*a[20]+b[2]*a[23]-b[4]*a[25]-b[6]*a[27]+b[8]*a[29]+b[11]*a[30]+b[17]*a[31];
	res[15]=b[15]*a[0]+b[21]*a[1]+b[24]*a[2]+b[25]*a[3]-b[28]*a[6]-b[29]*a[7]-b[30]*a[10]+b[0]*a[15]-b[31]*a[16]+b[1]*a[21]+b[2]*a[24]+b[3]*a[25]-b[6]*a[28]-b[7]*a[29]-b[10]*a[30]-b[16]*a[31];
	res[16]=b[16]*a[0]-b[26]*a[4]+b[27]*a[5]+b[31]*a[15]+b[0]*a[16]+b[4]*a[26]-b[5]*a[27]+b[15]*a[31];
	res[17]=b[17]*a[0]+b[26]*a[3]+b[28]*a[5]-b[31]*a[14]+b[0]*a[17]-b[3]*a[26]-b[5]*a[28]-b[14]*a[31];
	res[18]=b[18]*a[0]+b[27]*a[3]+b[28]*a[4]-b[31]*a[13]+b[0]*a[18]-b[3]*a[27]-b[4]*a[28]-b[13]*a[31];
	res[19]=b[19]*a[0]-b[26]*a[2]+b[29]*a[5]+b[31]*a[12]+b[0]*a[19]+b[2]*a[26]-b[5]*a[29]+b[12]*a[31];
	res[20]=b[20]*a[0]-b[27]*a[2]+b[29]*a[4]+b[31]*a[11]+b[0]*a[20]+b[2]*a[27]-b[4]*a[29]+b[11]*a[31];
	res[21]=b[21]*a[0]-b[28]*a[2]-b[29]*a[3]-b[31]*a[10]+b[0]*a[21]+b[2]*a[28]+b[3]*a[29]-b[10]*a[31];
	res[22]=b[22]*a[0]+b[26]*a[1]+b[30]*a[5]-b[31]*a[9]+b[0]*a[22]-b[1]*a[26]-b[5]*a[30]-b[9]*a[31];
	res[23]=b[23]*a[0]+b[27]*a[1]+b[30]*a[4]-b[31]*a[8]+b[0]*a[23]-b[1]*a[27]-b[4]*a[30]-b[8]*a[31];
	res[24]=b[24]*a[0]+b[28]*a[1]-b[30]*a[3]+b[31]*a[7]+b[0]*a[24]-b[1]*a[28]+b[3]*a[30]+b[7]*a[31];
	res[25]=b[25]*a[0]+b[29]*a[1]+b[30]*a[2]-b[31]*a[6]+b[0]*a[25]-b[1]*a[29]-b[2]*a[30]-b[6]*a[31];
	res[26]=b[26]*a[0]-b[31]*a[5]+b[0]*a[26]-b[5]*a[31];
	res[27]=b[27]*a[0]-b[31]*a[4]+b[0]*a[27]-b[4]*a[31];
	res[28]=b[28]*a[0]+b[31]*a[3]+b[0]*a[28]+b[3]*a[31];
	res[29]=b[29]*a[0]-b[31]*a[2]+b[0]*a[29]-b[2]*a[31];
	res[30]=b[30]*a[0]+b[31]*a[1]+b[0]*a[30]+b[1]*a[31];
	res[31]=b[31]*a[0]+b[0]*a[31];
        return res;
    }
    
    /**
     * Wedge.
     *
     * The outer product. (MEET)
     *
     * @param a
     * @param b
     * @return a ^ b
     */
    private static double[] op(double[] a, double[] b){
        double[] res = new double[a.length];
        
        // oprandom: T{((((((((((((((((((((((((((((((((b_0*a_0)+(b_1*a_1))+(b_2*a_2))+(b_3*a_3))+(b_4*a_4))-(b_5*a_5))-(b_6*a_6))-(b_7*a_7))-(b_8*a_8))+(b_9*a_9))-(b_10*a_10))-(b_11*a_11))+(b_12*a_12))-(b_13*a_13))+(b_14*a_14))+(b_15*a_15))-(b_16*a_16))-(b_17*a_17))+(b_18*a_18))-(b_19*a_19))+(b_20*a_20))+(b_21*a_21))-(b_22*a_22))+(b_23*a_23))+(b_24*a_24))+(b_25*a_25))+(b_26*a_26))-(b_27*a_27))-(b_28*a_28))-(b_29*a_29))-(b_30*a_30))-(b_31*a_31)), ((((((((((((((((b_0*a_1)+(b_2*a_6))+(b_3*a_7))+(b_4*a_8))-(b_5*a_9))-(b_10*a_16))-(b_11*a_17))+(b_12*a_18))-(b_13*a_19))+(b_14*a_20))+(b_15*a_21))-(b_22*a_26))+(b_23*a_27))+(b_24*a_28))+(b_25*a_29))-(b_30*a_31)), 
        //             ((((((((((((((((b_0*a_2)-(b_1*a_6))+(b_3*a_10))+(b_4*a_11))-(b_5*a_12))+(b_7*a_16))+(b_8*a_17))-(b_9*a_18))-(b_13*a_22))+(b_14*a_23))+(b_15*a_24))+(b_19*a_26))-(b_20*a_27))-(b_21*a_28))+(b_25*a_30))+(b_29*a_31)), ((((((((((((((((b_0*a_3)-(b_1*a_7))-(b_2*a_10))+(b_4*a_13))-(b_5*a_14))-(b_6*a_16))+(b_8*a_19))-(b_9*a_20))+(b_11*a_22))-(b_12*a_23))+(b_15*a_25))-(b_17*a_26))+(b_18*a_27))-(b_21*a_29))-(b_24*a_30))-(b_28*a_31)), ((((((((((((((((b_0*a_4)-(b_1*a_8))-(b_2*a_11))-(b_3*a_13))-(b_5*a_15))-(b_6*a_17))-(b_7*a_19))-(b_9*a_21))-(b_10*a_22))-(b_12*a_24))-(b_14*a_25))+(b_16*a_26))+(b_18*a_28))+(b_20*a_29))+(b_23*a_30))+(b_27*a_31)), ((((((((((((((((b_0*a_5)-(b_1*a_9))-(b_2*a_12))-(b_3*a_14))-(b_4*a_15))-(b_6*a_18))-(b_7*a_20))-(b_8*a_21))-(b_10*a_23))-(b_11*a_24))-(b_13*a_25))+(b_16*a_27))+(b_17*a_28))+(b_19*a_29))+(b_22*a_30))+(b_26*a_31)), ((((((((b_0*a_6)+(b_3*a_16))+(b_4*a_17))-(b_5*a_18))-(b_13*a_26))+(b_14*a_27))+(b_15*a_28))+(b_25*a_31)), ((((((((b_0*a_7)-(b_2*a_16))+(b_4*a_19))-(b_5*a_20))+(b_11*a_26))-(b_12*a_27))+(b_15*a_29))-(b_24*a_31)), ((((((((b_0*a_8)-(b_2*a_17))-(b_3*a_19))-(b_5*a_21))-(b_10*a_26))-(b_12*a_28))-(b_14*a_29))+(b_23*a_31)), ((((((((b_0*a_9)-(b_2*a_18))-(b_3*a_20))-(b_4*a_21))-(b_10*a_27))-(b_11*a_28))-(b_13*a_29))+(b_22*a_31)), ((((((((b_0*a_10)+(b_1*a_16))+(b_4*a_22))-(b_5*a_23))-(b_8*a_26))+(b_9*a_27))+(b_15*a_30))+(b_21*a_31)), ((((((((b_0*a_11)+(b_1*a_17))-(b_3*a_22))-(b_5*a_24))+(b_7*a_26))+(b_9*a_28))-(b_14*a_30))-(b_20*a_31)), ((((((((b_0*a_12)+(b_1*a_18))-(b_3*a_23))-(b_4*a_24))+(b_7*a_27))+(b_8*a_28))-(b_13*a_30))-(b_19*a_31)), ((((((((b_0*a_13)+(b_1*a_19))+(b_2*a_22))-(b_5*a_25))-(b_6*a_26))+(b_9*a_29))+(b_12*a_30))+(b_18*a_31)), ((((((((b_0*a_14)+(b_1*a_20))+(b_2*a_23))-(b_4*a_25))-(b_6*a_27))+(b_8*a_29))+(b_11*a_30))+(b_17*a_31)), ((((((((b_0*a_15)+(b_1*a_21))+(b_2*a_24))+(b_3*a_25))-(b_6*a_28))-(b_7*a_29))-(b_10*a_30))-(b_16*a_31)), ((((b_0*a_16)+(b_4*a_26))-(b_5*a_27))+(b_15*a_31)), ((((b_0*a_17)-(b_3*a_26))-(b_5*a_28))-(b_14*a_31)), ((((b_0*a_18)-(b_3*a_27))-(b_4*a_28))-(b_13*a_31)), ((((b_0*a_19)+(b_2*a_26))-(b_5*a_29))+(b_12*a_31)), ((((b_0*a_20)+(b_2*a_27))-(b_4*a_29))+(b_11*a_31)), ((((b_0*a_21)+(b_2*a_28))+(b_3*a_29))-(b_10*a_31)), ((((b_0*a_22)-(b_1*a_26))-(b_5*a_30))-(b_9*a_31)), ((((b_0*a_23)-(b_1*a_27))-(b_4*a_30))-(b_8*a_31)), ((((b_0*a_24)-(b_1*a_28))+(b_3*a_30))+(b_7*a_31)), ((((b_0*a_25)-(b_1*a_29))-(b_2*a_30))-(b_6*a_31)), ((b_0*a_26)-(b_5*a_31)), ((b_0*a_27)-(b_4*a_31)), ((b_0*a_28)+(b_3*a_31)), ((b_0*a_29)-(b_2*a_31)), ((b_0*a_30)+(b_1*a_31)), (b_0*a_31)}
        // sparsity der product matrix scheint nicht berücksichtigt zu werden
        // 
        res[0]=b[0]*a[0];
	res[1]=b[1]*a[0]+b[0]*a[1];
	res[2]=b[2]*a[0]+b[0]*a[2];
	res[3]=b[3]*a[0]+b[0]*a[3];
	res[4]=b[4]*a[0]+b[0]*a[4];
	res[5]=b[5]*a[0]+b[0]*a[5];
	res[6]=b[6]*a[0]+b[2]*a[1]-b[1]*a[2]+b[0]*a[6];
	res[7]=b[7]*a[0]+b[3]*a[1]-b[1]*a[3]+b[0]*a[7];
	res[8]=b[8]*a[0]+b[4]*a[1]-b[1]*a[4]+b[0]*a[8];
	res[9]=b[9]*a[0]+b[5]*a[1]-b[1]*a[5]+b[0]*a[9];
	res[10]=b[10]*a[0]+b[3]*a[2]-b[2]*a[3]+b[0]*a[10];
	res[11]=b[11]*a[0]+b[4]*a[2]-b[2]*a[4]+b[0]*a[11];
	res[12]=b[12]*a[0]+b[5]*a[2]-b[2]*a[5]+b[0]*a[12];
	res[13]=b[13]*a[0]+b[4]*a[3]-b[3]*a[4]+b[0]*a[13];
	res[14]=b[14]*a[0]+b[5]*a[3]-b[3]*a[5]+b[0]*a[14];
	res[15]=b[15]*a[0]+b[5]*a[4]-b[4]*a[5]+b[0]*a[15];
	res[16]=b[16]*a[0]+b[10]*a[1]-b[7]*a[2]+b[6]*a[3]+b[3]*a[6]-b[2]*a[7]+b[1]*a[10]+b[0]*a[16];
	res[17]=b[17]*a[0]+b[11]*a[1]-b[8]*a[2]+b[6]*a[4]+b[4]*a[6]-b[2]*a[8]+b[1]*a[11]+b[0]*a[17];
	res[18]=b[18]*a[0]+b[12]*a[1]-b[9]*a[2]+b[6]*a[5]+b[5]*a[6]-b[2]*a[9]+b[1]*a[12]+b[0]*a[18];
	res[19]=b[19]*a[0]+b[13]*a[1]-b[8]*a[3]+b[7]*a[4]+b[4]*a[7]-b[3]*a[8]+b[1]*a[13]+b[0]*a[19];
	res[20]=b[20]*a[0]+b[14]*a[1]-b[9]*a[3]+b[7]*a[5]+b[5]*a[7]-b[3]*a[9]+b[1]*a[14]+b[0]*a[20];
	res[21]=b[21]*a[0]+b[15]*a[1]-b[9]*a[4]+b[8]*a[5]+b[5]*a[8]-b[4]*a[9]+b[1]*a[15]+b[0]*a[21];
	res[22]=b[22]*a[0]+b[13]*a[2]-b[11]*a[3]+b[10]*a[4]+b[4]*a[10]-b[3]*a[11]+b[2]*a[13]+b[0]*a[22];
	res[23]=b[23]*a[0]+b[14]*a[2]-b[12]*a[3]+b[10]*a[5]+b[5]*a[10]-b[3]*a[12]+b[2]*a[14]+b[0]*a[23];
	res[24]=b[24]*a[0]+b[15]*a[2]-b[12]*a[4]+b[11]*a[5]+b[5]*a[11]-b[4]*a[12]+b[2]*a[15]+b[0]*a[24];
	res[25]=b[25]*a[0]+b[15]*a[3]-b[14]*a[4]+b[13]*a[5]+b[5]*a[13]-b[4]*a[14]+b[3]*a[15]+b[0]*a[25];
	res[26]=b[26]*a[0]+b[22]*a[1]-b[19]*a[2]+b[17]*a[3]-b[16]*a[4]+b[13]*a[6]-b[11]*a[7]+b[10]*a[8]+b[8]*a[10]-b[7]*a[11]+b[6]*a[13]+b[4]*a[16]-b[3]*a[17]+b[2]*a[19]-b[1]*a[22]+b[0]*a[26];
	res[27]=b[27]*a[0]+b[23]*a[1]-b[20]*a[2]+b[18]*a[3]-b[16]*a[5]+b[14]*a[6]-b[12]*a[7]+b[10]*a[9]+b[9]*a[10]-b[7]*a[12]+b[6]*a[14]+b[5]*a[16]-b[3]*a[18]+b[2]*a[20]-b[1]*a[23]+b[0]*a[27];
	res[28]=b[28]*a[0]+b[24]*a[1]-b[21]*a[2]+b[18]*a[4]-b[17]*a[5]+b[15]*a[6]-b[12]*a[8]+b[11]*a[9]+b[9]*a[11]-b[8]*a[12]+b[6]*a[15]+b[5]*a[17]-b[4]*a[18]+b[2]*a[21]-b[1]*a[24]+b[0]*a[28];
	res[29]=b[29]*a[0]+b[25]*a[1]-b[21]*a[3]+b[20]*a[4]-b[19]*a[5]+b[15]*a[7]-b[14]*a[8]+b[13]*a[9]+b[9]*a[13]-b[8]*a[14]+b[7]*a[15]+b[5]*a[19]-b[4]*a[20]+b[3]*a[21]-b[1]*a[25]+b[0]*a[29];
	res[30]=b[30]*a[0]+b[25]*a[2]-b[24]*a[3]+b[23]*a[4]-b[22]*a[5]+b[15]*a[10]-b[14]*a[11]+b[13]*a[12]+b[12]*a[13]-b[11]*a[14]+b[10]*a[15]+b[5]*a[22]-b[4]*a[23]+b[3]*a[24]-b[2]*a[25]+b[0]*a[30];
	res[31]=b[31]*a[0]+b[30]*a[1]-b[29]*a[2]+b[28]*a[3]-b[27]*a[4]+b[26]*a[5]+b[25]*a[6]-b[24]*a[7]+b[23]*a[8]-b[22]*a[9]+b[21]*a[10]-b[20]*a[11]+b[19]*a[12]+b[18]*a[13]-b[17]*a[14]+b[16]*a[15]+b[15]*a[16]-b[14]*a[17]+b[13]*a[18]+b[12]*a[19]-b[11]*a[20]+b[10]*a[21]-b[9]*a[22]+b[8]*a[23]-b[7]*a[24]+b[6]*a[25]+b[5]*a[26]-b[4]*a[27]+b[3]*a[28]-b[2]*a[29]+b[1]*a[30]+b[0]*a[31];
        return res;
    }
    /**
     * Add.
     *
     * Multivector addition
     *
     * @param a
     * @param b
     * @return a + b
     */
    private static double[] add (double[] a, double[] b){
        double[] res = new double[a.length];
	res[0] = a[0]+b[0];
	res[1] = a[1]+b[1];
	res[2] = a[2]+b[2];
	res[3] = a[3]+b[3];
	res[4] = a[4]+b[4];
	res[5] = a[5]+b[5];
	res[6] = a[6]+b[6];
	res[7] = a[7]+b[7];
	res[8] = a[8]+b[8];
	res[9] = a[9]+b[9];
	res[10] = a[10]+b[10];
	res[11] = a[11]+b[11];
	res[12] = a[12]+b[12];
	res[13] = a[13]+b[13];
	res[14] = a[14]+b[14];
	res[15] = a[15]+b[15];
	res[16] = a[16]+b[16];
	res[17] = a[17]+b[17];
	res[18] = a[18]+b[18];
	res[19] = a[19]+b[19];
	res[20] = a[20]+b[20];
	res[21] = a[21]+b[21];
	res[22] = a[22]+b[22];
	res[23] = a[23]+b[23];
	res[24] = a[24]+b[24];
	res[25] = a[25]+b[25];
	res[26] = a[26]+b[26];
	res[27] = a[27]+b[27];
	res[28] = a[28]+b[28];
	res[29] = a[29]+b[29];
	res[30] = a[30]+b[30];
	res[31] = a[31]+b[31];
        return res;
    }
    /**
     * Sub.
     *
     * Multivector subtraction
     *
     * @param a
     * @param b
     * @return a - b
     */
    private static double[] sub (double[] a, double[] b){
        double[] res = new double[a.length];
        res[0] = a[0]-b[0];
	res[1] = a[1]-b[1];
	res[2] = a[2]-b[2];
	res[3] = a[3]-b[3];
	res[4] = a[4]-b[4];
	res[5] = a[5]-b[5];
	res[6] = a[6]-b[6];
	res[7] = a[7]-b[7];
	res[8] = a[8]-b[8];
	res[9] = a[9]-b[9];
	res[10] = a[10]-b[10];
	res[11] = a[11]-b[11];
	res[12] = a[12]-b[12];
	res[13] = a[13]-b[13];
	res[14] = a[14]-b[14];
	res[15] = a[15]-b[15];
	res[16] = a[16]-b[16];
	res[17] = a[17]-b[17];
	res[18] = a[18]-b[18];
	res[19] = a[19]-b[19];
	res[20] = a[20]-b[20];
	res[21] = a[21]-b[21];
	res[22] = a[22]-b[22];
	res[23] = a[23]-b[23];
	res[24] = a[24]-b[24];
	res[25] = a[25]-b[25];
	res[26] = a[26]-b[26];
	res[27] = a[27]-b[27];
	res[28] = a[28]-b[28];
	res[29] = a[29]-b[29];
	res[30] = a[30]-b[30];
	res[31] = a[31]-b[31];
        return res;
    }

    private boolean equals(double[] a, double[] b, double eps){
        if (a.length != b.length) throw new IllegalArgumentException("a.length != b.length");
        for (int i=0;i<a.length;i++){
            if (Math.abs(a[i] - b[i]) > eps) return false;
        }
        return true;
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
