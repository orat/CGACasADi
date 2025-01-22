package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.dhbw.rahmlab.casadi.implUtil.WrapUtil;
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

    // available after plugging the impl into the api object
    private FunctionSymbolic.Callback callback;

    private final Function f_sym_casadi;

    private CGASymbolicFunction(Function f_sym_casadi) {
        this.name = f_sym_casadi.name();
        this.arity = (int) f_sym_casadi.n_in();
        this.resultCount = (int) f_sym_casadi.n_out();
        this.f_sym_casadi = f_sym_casadi;
    }

    /**
     * @param name A valid CasADi function name starts with a letter followed by letters, numbers or
     * non-consecutive underscores.
     */
    public <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> CGASymbolicFunction(String name, List<MV> parameters, List<? extends SparseCGASymbolicMultivector> returns) {
        try {
            // Only need runtime check, if parameters are not with their type enforced to be purely symbolic.
            // Parameters are only correct, if purely symbolic. The following ensures that.
//            for (var param : parameters) {
//                if (!param.getSX().is_valid_input()) {
//                    throw new IllegalArgumentException("CGASymbolicFunction: got non-purely-symbolic parameter.");
//                }
//            }

            var f_sym_in = transformImpl(parameters);
            var f_sym_out = transformImpl(returns);
            //String name = callback.getName();
            this.name = name;
            arity = parameters.size();
            resultCount = returns.size();
            this.f_sym_casadi = new Function(name, f_sym_in, f_sym_out);
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    protected static StdVectorSX transformImpl(List<? extends ISparseCGASymbolicMultivector> mvs) {
        List<SX> sxs = mvs.stream().map(ISparseCGASymbolicMultivector::getSX).toList();
        return new StdVectorSX(sxs);
    }

    /**
     * Caution: does not check for sparsity compatibility with the formal parameters given in the constructor.
     */
    @Override
    public List<? extends SparseCGASymbolicMultivector> callSymbolic(List<? extends SparseCGASymbolicMultivector> arguments) {
        try {
            var f_sym_in = transformImpl(arguments);
            var f_sym_out = new StdVectorSX();
            this.f_sym_casadi.call(f_sym_in, f_sym_out);
            return f_sym_out.stream().map(SparseCGASymbolicMultivector::create).toList();
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    /**
     * Caution: does not check for sparsity compatibility with the formal parameters given in the constructor.
     */
    @Override
    public List<? extends SparseCGANumericMultivector> callNumeric(List<? extends SparseCGANumericMultivector> arguments) {
        try {
            var f_num_in = new StdVectorDM(arguments.stream().map(SparseCGANumericMultivector::getDM).toList());
            var f_num_out = new StdVectorDM();
            this.f_sym_casadi.call(f_num_in, f_num_out);
            return f_num_out.stream().map(SparseCGANumericMultivector::create).toList();
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    /**
     * <pre>
     * for-loop equivalent.
     * Suppose you are interested in computing a function repeatedly on all columns of a matrix,
     * and aggregating all results in a result matrix.
     * The aggregate function can be obtained with the map construct.
     * n is the number of invocations. With other words: the length of the arguments arrays.
     *
     *    Suppose the function has a signature of:
     *    {@literal
     *    f: (a, p) -> ( s )
     *    }
     *
     *    The the mapped version has the signature:
     *    {@literal
     *    F: (A, P) -> (S )
     *
     *    with
     *       A: horzcat([a0, a1, ..., a_(N-1)])
     *       P: horzcat([p0, p1, ..., p_(N-1)])
     *       S: horzcat([s0, s1, ..., s_(N-1)])
     *    and
     *       s0 <- f(a0, p0)
     *       s1 <- f(a1, p1)
     *       ...
     *       s_(N-1) <- f(a_(N-1), p_(N-1))
     *    }
     * </pre>
     */
    public CGASymbolicFunction map(int n) {
        // unroll | serial | openmp
        var casadiMapFunc = this.f_sym_casadi.map(n);
        return new CGASymbolicFunction(casadiMapFunc);
    }

    public static void mainMap() {
        var a = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("a");
        var b = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("b");
        var sum = a.add(b);

        var sumFunc = CGAExprGraphFactory.instance.createFunctionSymbolic("func", List.of(a, b), List.of(sum));
        System.out.println(sumFunc.f_sym_casadi);

        var mapSumFunc = sumFunc.map(2);
        System.out.println(mapSumFunc.f_sym_casadi);

        // 1a+1b=3
        var arg1a = CGAExprGraphFactory.instance.createMultivectorNumeric(1.0);
        var arg1b = CGAExprGraphFactory.instance.createMultivectorNumeric(2.0);

        // 2a+2b=7
        var arg2a = CGAExprGraphFactory.instance.createMultivectorNumeric(3.0);
        var arg2b = CGAExprGraphFactory.instance.createMultivectorNumeric(4.0);

        var argA = SparseCGANumericMultivector.create(DM.horzcat(new StdVectorDM(new DM[]{arg1a.getDM(), arg2a.getDM()})));
        var argB = SparseCGANumericMultivector.create(DM.horzcat(new StdVectorDM(new DM[]{arg1b.getDM(), arg2b.getDM()})));

        var mapSumFuncOutDM = mapSumFunc.callNumeric(List.of(argA, argB)).get(0).getDM();
        var cols = DM.horzsplit_n(mapSumFuncOutDM, mapSumFuncOutDM.columns());
        System.out.println(mapSumFuncOutDM);
        System.out.println("------");
        System.out.println(cols.get(0));
    }

    public static void main(String[] args) {
        mainMap();
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
