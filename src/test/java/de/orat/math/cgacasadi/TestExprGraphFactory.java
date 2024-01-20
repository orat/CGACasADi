package de.orat.math.cgacasadi;

import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.spi.iExprGraphFactory;
import java.util.Random;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class TestExprGraphFactory extends ExprGraphFactory {
    public static CGAExprGraphFactory impl = new CGAExprGraphFactory();
    
    public static ExprGraphFactory instance(){
        return get(impl);
    }
    
    protected TestExprGraphFactory(iExprGraphFactory impl) {
        super(impl);
    }
    
    public static double[] createRandomMultivector(int basisBladesCount) {
        return impl.createRandomMultivector(basisBladesCount);
    }

    public double[] createRandomCGAMultivector() {
        return impl.createRandomCGAMultivector();
    }
    
    public static double[] createRandomCGAKVector(int basisBladesCount, int grade){
        return impl.createRandomCGAKVector(basisBladesCount, grade);
    }

    public static double[] createRandomCGAKVector(int grade){
        return impl.createRandomCGAKVector(grade);
    }
}
