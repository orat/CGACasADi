package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.dhbw.rahmlab.casadi.implUtil.WrapUtil;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import de.orat.math.gacalc.spi.iMultivectorPurelySymbolic;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGASymbolicFunction implements iFunctionSymbolic<SparseCGASymbolicMultivector, SparseCGANumericMultivector> {

    private final String name;
    private final int arity;
    private final int resultCount;
    private final List<Sparsity> paramsSparsities;

    // available after plugging the impl into the api object
    private FunctionSymbolic.Callback callback;

    private final Function f_sym_casadi;

    protected Function getCasADiFunction() {
        return this.f_sym_casadi;
    }

    /**
     * @param name A valid CasADi function name starts with a letter followed by letters, numbers or
     * non-consecutive underscores.
     */
    public <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> CGASymbolicFunction(String name, List<MV> parameters, List<? extends SparseCGASymbolicMultivector> returns) {
        try {
            this.paramsSparsities = parameters.stream().map(ISparseCGASymbolicMultivector::getSX).map(SX::sparsity).toList();
            var def_sym_in = transformImpl(parameters);
            var def_sym_out = transformImpl(returns);
            this.name = name;
            arity = parameters.size();
            resultCount = returns.size();
            this.f_sym_casadi = new Function(name, def_sym_in, def_sym_out);
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    protected static StdVectorSX transformImpl(List<? extends ISparseCGASymbolicMultivector> mvs) {
        List<SX> sxs = mvs.stream().map(ISparseCGASymbolicMultivector::getSX).toList();
        return new StdVectorSX(sxs);
    }

    @Override
    public List<? extends SparseCGASymbolicMultivector> callSymbolic(List<? extends SparseCGASymbolicMultivector> arguments) {
        try {
            if (arguments.size() != this.arity) {
                throw new IllegalArgumentException(String.format("Expected %s arguments, but got %s.",
                    this.arity, arguments.size()));
            }
            assert CasADiUtil.areSparsitiesSupersetsOfSubsets(this.paramsSparsities, CasADiUtil.toSparsities(arguments));

            var call_sym_in = transformImpl(arguments);
            var call_sym_out = new StdVectorSX();
            this.f_sym_casadi.call(call_sym_in, call_sym_out);
            return call_sym_out.stream().map(SparseCGASymbolicMultivector::create).toList();
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    @Override
    public List<? extends SparseCGANumericMultivector> callNumeric(List<? extends SparseCGANumericMultivector> arguments) {
        try {
            if (arguments.size() != this.arity) {
                throw new IllegalArgumentException(String.format("Expected %s arguments, but got %s.",
                    this.arity, arguments.size()));
            }
            assert CasADiUtil.areSparsitiesSupersetsOfSubsets(this.paramsSparsities, CasADiUtil.toSparsities(arguments));

            var call_num_in = new StdVectorDM(arguments.stream().map(SparseCGANumericMultivector::getDM).toList());
            var call_num_out = new StdVectorDM();
            this.f_sym_casadi.call(call_num_in, call_num_out);
            return call_num_out.stream().map(SparseCGANumericMultivector::create).toList();
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
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
