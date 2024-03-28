package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGASymbolicFunction implements iFunctionSymbolic<SparseCGASymbolicMultivector, SparseCGANumericMultivector> {

    private final String name;
    private final int arity;
    private final int resultCount;

    // available after plugging the impl into the api object
    private FunctionSymbolic.Callback callback;

    private final Function f_sym_casadi;

    public CGASymbolicFunction(String name, List<SparseCGASymbolicMultivector> parameters,
        List<SparseCGASymbolicMultivector> returns) {
        var f_sym_in = transformImpl(parameters);
        var f_sym_out = transformImpl(returns);
        //String name = callback.getName();
        this.name = name;
        arity = parameters.size();
        resultCount = returns.size();
        this.f_sym_casadi = new Function(name, f_sym_in, f_sym_out);
    }

    // schwierig zu implementieren
    /*public boolean equals(List<iMultivectorSymbolic> parameters, 
                                     List<iMultivectorSymbolic> returns){
        
    }*/
    protected static StdVectorSX transformImpl(List<SparseCGASymbolicMultivector> mvs) {
        List<SX> mxs = mvs.stream().map(mv -> (mv).getSX()).toList();
        return new StdVectorSX(mxs);
    }

    @Override
    public List<SparseCGASymbolicMultivector> callSymbolic(List<SparseCGASymbolicMultivector> arguments) {
        var f_sym_in = transformImpl(arguments);
        var f_sym_out = new StdVectorSX();
        this.f_sym_casadi.call(f_sym_in, f_sym_out);
        return f_sym_out.stream().map(mx -> (new SparseCGASymbolicMultivector(mx))).toList();
    }

    @Override
    public List<SparseCGANumericMultivector> callNumeric(List<SparseCGANumericMultivector> arguments) {
        var f_num_in = new StdVectorDM(arguments.stream().map(
            imvn -> (imvn).dm).toList());
        var f_num_out = new StdVectorDM();
        this.f_sym_casadi.call(f_num_in, f_num_out);
        return f_num_out.stream().map(dm -> (new SparseCGANumericMultivector(dm))).toList();
    }

    @Override
    public String toString() {
        return f_sym_casadi.toString();
    }

    @Override
    public void init(FunctionSymbolic.Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    @Override
    public String getName() {
        return name;
    }
}
