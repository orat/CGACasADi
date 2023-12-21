package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import util.cga.CGACayleyTable;
import util.cga.CGACayleyTableGeometricProduct;
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
        //this.dm = new DM(baseCayleyTable.getBladesCount(),1);
        dm = null;
    }
    public SparseCGANumericMultivector instance(double[] values){
        return new SparseCGANumericMultivector(values);
    }
    protected SparseCGANumericMultivector(double[] values){
        if (baseCayleyTable.getBladesCount() != values.length) throw
                new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "+
                        String.valueOf(values.length));
        this.dm = CasADiUtil.toDM(values);
    }
    public SparseCGANumericMultivector instance(double[] nonzeros, int[] rows){
        return new SparseCGANumericMultivector(nonzeros, rows);
    }
    protected SparseCGANumericMultivector(double[] nonzeros, int[] rows){
        if (baseCayleyTable.getBladesCount() < nonzeros.length) throw
                new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "+
                        String.valueOf(nonzeros.length));
        if (nonzeros.length != rows.length) throw new IllegalArgumentException("Construction of CGA multivector failed because nonzeros.length != rows.length!");
        this.dm = CasADiUtil.toDM(baseCayleyTable.getBladesCount(), nonzeros, rows);
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
