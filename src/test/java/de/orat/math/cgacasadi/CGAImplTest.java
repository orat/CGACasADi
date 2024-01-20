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
            assertTrue(equals(mv.iterator().next().elements(), test));
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
            assertTrue(equals(mv.iterator().next().elements(), test));
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
            assertTrue(equals(mv.elements(), test));
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
            assertTrue(equals(mv.elements(), test));
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
        //System.out.println("result (sym): "+res.toString());
        
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
            assertTrue(equals(mv.elements(), test));
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
            System.out.println("random: a b="+mv.toString());
            System.out.println("test="+testMatrix.toString());
            //FIXME
            //a=[0.788533, 0.230104, 0.617871, -0.928288, -0.105082, -0.769252, -0.0912453, -0.338095, -0.603159, 0.426324, 0.353887, 0.830786, -0.266535, 0.933946, 0.974447, 0.381749, -0.52536, -0.275709, -0.163835, -0.387657, 0.0793199, 0.184763, -0.746645, -0.56926, 0.684507, 0.0170876, -0.754337, -0.0343847, 0.791464, -0.614593, 0.591833, 0.227089]
            //b=[0.0436466, -0.832396, 0.283982, -0.337702, 0.527104, 0.277705, 0.693611, 0.12766, 0.866075, -0.093915, 0.399915, 0.718882, 0.615126, 0.293259, 0.270382, -0.299662, -0.13652, 0.681146, -0.0903113, -0.464942, -0.939624, -0.208155, 0.482373, -0.821425, 0.974287, -0.998317, 0.406073, 0.439755, -0.804543, -0.51233, -0.451544, 0.667235]
            //random: a b=[0.355931, -1.78226, -0.541808, -1.09884, 0.994596, -0.73997, 2.64298, 1.71158, 0.512802, -2.05135, 3.60021, 0.5254, -2.22526, 2.67642, -0.582524, 0.625841, 0.900617, 0.896198, -0.0642288, 1.61451, -2.17924, 3.58869, -0.560492, 1.50894, 3.1433, -2.80302, -1.77937, 0.127167, -3.52281, -1.53133, -1.24064, -0.667185]
            //test={1.5129998566656144, 0.18279855321983673, -1.5486842481574314, -1.0307638838007547, 1.7355424748572692, 0.6963847626010777, 1.9340365152238923, -1.0463436077043107, -1.061457856849736, -1.6177672782510588, 0.19108293787230862, 1.0730930365434563, 1.2005647669797161, 0.8094238573650144, -3.3604704289582132, -0.840300135953945, 0.342225499220247, -0.506030354827997, -2.379276486840147, 0.7498230502220242, -1.466120946386584, -2.104513282014597, 0.18580517101020536, 1.7646581748137726, 2.463900023143224, -2.7117845454550986, -2.2909198246697, -1.3522817204400508, 2.1899067681117153, -1.5587260627620478, -0.2283382415197428, 0.5172856161891328}

            double eps = 0.00001;
            assertTrue(equals(mv.elements(), test, eps));
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
            assertTrue(equals(mv.elements(), test, eps));
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
            double[] values = out.elements();
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
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv", 1);
        System.out.println("mv (sparsity): "+mv.getSparsity().toString());
        System.out.println("mv: "+mv.toString());
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
            //System.out.println("b=reverse(a)="+out.toString());
            double[] values = out.elements();
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
    public void testConjugateRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv", 1);
        System.out.println("mv (sparsity): "+mv.getSparsity().toString());
        System.out.println("mv: "+mv.toString());
        MultivectorSymbolic result = mv.cliffordConjugate();
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
            double[] values = out.elements();
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
