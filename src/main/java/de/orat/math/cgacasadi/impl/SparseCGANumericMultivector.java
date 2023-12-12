package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.orat.math.cgacasadi.CGACayleyTable;
import de.orat.math.cgacasadi.CGACayleyTableGeometricProduct;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.spi.iMultivectorNumeric;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class SparseCGANumericMultivector implements iMultivectorNumeric {
    
    final DM dm;
    
    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();
    
    private MultivectorNumeric.Callback callback;

    public SparseCGANumericMultivector(){
        this.dm = new DM(baseCayleyTable.getBladesCount(),1);
    }
    
    public SparseCGANumericMultivector(double[] values){
        if (CGACayleyTable.CGABasisBladeNames.length != values.length) throw
                new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "+
                        String.valueOf(values.length));
        this.dm = CasADiUtil.toDM(values);
    }
    public SparseCGANumericMultivector(double[] nonzeros, int[] rows){
        if (CGACayleyTable.CGABasisBladeNames.length < nonzeros.length) throw
                new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "+
                        String.valueOf(nonzeros.length));
        if (nonzeros.length != rows.length) throw new IllegalArgumentException("Construction of CGA multivector failed because nonzeros.length != rows.length!");
        this.dm = CasADiUtil.toDM(CGACayleyTable.CGABasisBladeNames.length, nonzeros, rows);
    }
    
    public DM getDM(){
        return dm;
    }
    SparseCGANumericMultivector(DM dm){
        this.dm = dm;
    }
    
    @Override
    public void init(MultivectorNumeric.Callback callback){
        this.callback = callback;
    }
    
    @Override
    public String toString(){
        return dm.toString();
    }
    
    public double[] elements(){
        return CasADiUtil.elements(dm);
    }
}
