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

    public static CGAExprGraphFactory impl_ = CGAExprGraphFactory.instance;

    public static ExprGraphFactory instance() {
        return get(impl_);
    }

    protected TestExprGraphFactory(iExprGraphFactory impl) {
        super(impl);
    }

    /*public static double[] createRandomKVector(int basisBladesCount) {
        return impl_.createRandomKVector(basisBladesCount);
    }

    public double[] createRandomCGAMultivector() {
        return impl_.createRandomCGAMultivector();
    }
    
    public static double[] createRandomCGAKVector(int basisBladesCount, int grade){
        return impl_.createRandomCGAKVector(basisBladesCount, grade);
    }

    public static double[] createRandomCGAKVector(int grade){
        return impl_.createRandomCGAKVector(grade);
    }*/
}
