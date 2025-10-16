package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorCasadiInt;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.gacalc.spi.iLoopService;
import de.orat.math.gacalc.spi.iMultivectorPurelySymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolicArray;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * <pre>
 * https://web.casadi.org/docs/#for-loop-equivalents
 * https://web.casadi.org/api/html/da/da4/classcasadi_1_1Function.html
 * </pre>
 */
public class CGASymbolicFunctionService implements iLoopService<SparseCGASymbolicMultivector, PurelySymbolicCachedSparseCGASymbolicMultivector, CGAArray> {

    private CGASymbolicFunctionService() {

    }

    public static final CGASymbolicFunctionService instance = new CGASymbolicFunctionService();

    @Override
    public iMultivectorSymbolicArray<SparseCGASymbolicMultivector> toSymbolicArray(List<SparseCGASymbolicMultivector> from) {
        return new CGAArray(from);
    }

    @Override
    public List<CGAArray> map(
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsSimple,
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsArray,
        List<SparseCGASymbolicMultivector> returnsArray,
        List<SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        return mapImpl(paramsSimple, paramsArray, returnsArray, argsSimple, argsArray, iterations);
    }

    @Override
    public AccumArrayListReturn<SparseCGASymbolicMultivector, CGAArray> fold(
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsAccum,
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsSimple,
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsArray,
        List<SparseCGASymbolicMultivector> returnsAccum,
        List<SparseCGASymbolicMultivector> returnsArray,
        List<SparseCGASymbolicMultivector> argsAccumInitial,
        List<SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        return foldImpl(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iterations);
    }

    @Override
    public AccumArrayListReturn<CGAArray, CGAArray> mapaccum(
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsAccum,
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsSimple,
        List<PurelySymbolicCachedSparseCGASymbolicMultivector> paramsArray,
        List<SparseCGASymbolicMultivector> returnsAccum,
        List<SparseCGASymbolicMultivector> returnsArray,
        List<SparseCGASymbolicMultivector> argsAccumInitial,
        List<SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        return mapaccumImpl(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iterations);
    }

    /**
     * <pre>
     * for-loop equivalent.
     *
     * The map operation exhibits constant graph size and initialization time.
     * </pre>
     *
     * @param paramsSimple Plain variables.
     * @param paramsArray Array variables.
     * @param argsArray Hint: Index of element used in the computation is equal to the current iteration.
     * @return One array element for each iteration for the variables of the returnsArray parameter.
     */
    public static <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> List<CGAArray> mapImpl(
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends SparseCGASymbolicMultivector> returnsArray,
        List<? extends SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        assert iterations >= 1;
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (int i = 0; i < paramsArray.size(); ++i) {
            var param = paramsArray.get(i);
            var argsArr = argsArray.get(i);
            assert argsArr.size() == iterations;
            assert argsArr.areSparsitiesSubsetsOf(param.getSX().sparsity());
        }
        assert CasADiUtil.areMVSparsitiesSupersetsOfSubsets(paramsSimple, argsSimple);

        var def_sym_in = new StdVectorSX(
            Stream.concat(
                paramsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                paramsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(returnsArray.stream().map(ISparseCGASymbolicMultivector::getSX).toList());
        var f_sym_casadi = new Function("MapBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            Stream.concat(
                argsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                argsArray.stream().map(CGAArray::horzcat)
            ).toList()
        );
        var call_sym_out = new StdVectorSX();
        // Works as long as they are first in def_sym_in and call_sym_in.
        var nonRepeated = new StdVectorCasadiInt(LongStream.range(0, paramsSimple.size()).boxed().toList());
        // parallelization = unroll|serial|openmp
        f_sym_casadi.map("MapMap", "serial", iterations, nonRepeated, new StdVectorCasadiInt()).call(call_sym_in, call_sym_out);

        var call_out = call_sym_out.stream().map(CGAArray::horzsplit).map(CGAArray::new).toList();
        return call_out;
    }

    private static <T> Stream<T> StreamConcat(Stream<? extends T> a, Stream<? extends T> b, Stream<? extends T> c) {
        return Stream.concat(a, Stream.concat(b, c));
    }

    /**
     * <pre>
     * for-loop equivalent.
     *
     * The fold operation exhibits a graph size and initialization time that scales logarithmically with n.
     * </pre>
     *
     * @param paramsAccum Variables (fold) or array elements (mapaccum) which depend on the previous
     * iteration.
     * @param paramsSimple Plain variables.
     * @param paramsArray Array variables.
     * @param argsArray Hint: Index of element used in the computation is equal to the current iteration.
     * @return Only end results of accum Variables. One array element for each iteration for the variables of
     * the returnsArray parameter.
     */
    public static <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> AccumArrayListReturn<SparseCGASymbolicMultivector, CGAArray> foldImpl(
        List<MV> paramsAccum,
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends SparseCGASymbolicMultivector> returnsAccum,
        List<? extends SparseCGASymbolicMultivector> returnsArray,
        List<? extends SparseCGASymbolicMultivector> argsAccumInitial,
        List<? extends SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        assert iterations >= 1;
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInitial.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (int i = 0; i < paramsArray.size(); ++i) {
            var param = paramsArray.get(i);
            var argsArr = argsArray.get(i);
            assert argsArr.size() == iterations;
            assert argsArr.areSparsitiesSubsetsOf(param.getSX().sparsity());
        }
        assert CasADiUtil.areMVSparsitiesSupersetsOfSubsets(paramsAccum, returnsAccum);
        assert CasADiUtil.areMVSparsitiesSupersetsOfSubsets(paramsAccum, argsAccumInitial);
        assert CasADiUtil.areMVSparsitiesSupersetsOfSubsets(paramsSimple, argsSimple);

        var def_sym_in = new StdVectorSX(
            StreamConcat(
                Stream.of(CGAArray.horzcat(paramsAccum)),
                paramsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                paramsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(
            Stream.concat(
                Stream.of(CGAArray.horzcat(returnsAccum)),
                returnsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var f_sym_casadi = new Function("foldBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            StreamConcat(
                Stream.of(CGAArray.horzcat(argsAccumInitial)),
                // CasADi treats a SX as an arbitrary long List of SxStatic.
                // No need to use repmat.
                argsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                argsArray.stream().map(CGAArray::horzcat)
            ).toList()
        );
        var call_sym_out = new StdVectorSX();
        f_sym_casadi.fold(iterations).call(call_sym_in, call_sym_out);

        var call_out_all = call_sym_out.stream().toList();
        var call_out_accum = CGAArray.horzsplit(call_out_all.get(0)).stream().map(SparseCGASymbolicMultivector::create).toList();
        var call_out_array = call_out_all.subList(1, call_out_all.size()).stream().map(CGAArray::horzsplit).map(CGAArray::new).toList();
        var call_out = new AccumArrayListReturn(call_out_accum, call_out_array);
        return call_out;
    }

    /**
     * <pre>
     * for-loop equivalent.
     *
     * The mapaccum operation exhibits a graph size and initialization time that scales logarithmically with n.
     * </pre>
     *
     * @param paramsAccum Variables (fold) or array elements (mapaccum) which depend on the previous
     * iteration.
     * @param paramsSimple Plain variables.
     * @param paramsArray Array variables.
     * @param argsArray Hint: Index of element used in the computation is equal to the current iteration.
     * @return Results of all iterations of accum Variables. One array element for each iteration for the
     * variables of the returnsArray parameter.
     */
    public static <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> AccumArrayListReturn<CGAArray, CGAArray> mapaccumImpl(
        List<MV> paramsAccum,
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends SparseCGASymbolicMultivector> returnsAccum,
        List<? extends SparseCGASymbolicMultivector> returnsArray,
        List<? extends SparseCGASymbolicMultivector> argsAccumInitial,
        List<? extends SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        assert iterations >= 1;
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInitial.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (int i = 0; i < paramsArray.size(); ++i) {
            var param = paramsArray.get(i);
            var argsArr = argsArray.get(i);
            assert argsArr.size() == iterations;
            assert argsArr.areSparsitiesSubsetsOf(param.getSX().sparsity());
        }
        assert CasADiUtil.areMVSparsitiesSupersetsOfSubsets(paramsAccum, returnsAccum);
        assert CasADiUtil.areMVSparsitiesSupersetsOfSubsets(paramsAccum, argsAccumInitial);
        assert CasADiUtil.areMVSparsitiesSupersetsOfSubsets(paramsSimple, argsSimple);

        var def_sym_in = new StdVectorSX(
            StreamConcat(
                paramsAccum.stream().map(ISparseCGASymbolicMultivector::getSX),
                paramsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                paramsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(
            Stream.concat(
                returnsAccum.stream().map(ISparseCGASymbolicMultivector::getSX),
                returnsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var f_sym_casadi = new Function("MapaccumBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            StreamConcat(
                argsAccumInitial.stream().map(ISparseCGASymbolicMultivector::getSX),
                // CasADi treats a SX as an arbitrary long List of SxStatic.
                // No need to use repmat.
                argsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                argsArray.stream().map(CGAArray::horzcat)
            ).toList()
        );
        var call_sym_out = new StdVectorSX();
        // Works as long as they are first in def_sym_in and call_sym_in.
        var accumVars = new StdVectorCasadiInt(LongStream.range(0, paramsAccum.size()).boxed().toList());
        f_sym_casadi.mapaccum("MapaccumMapaccum", iterations, accumVars, accumVars).call(call_sym_in, call_sym_out);

        var call_out_all = call_sym_out.stream().toList();
        var call_out_accum = call_out_all.subList(0, returnsAccum.size()).stream().map(CGAArray::horzsplit).map(CGAArray::new).toList();
        var call_out_array = call_out_all.subList(returnsAccum.size(), call_out_all.size()).stream().map(CGAArray::horzsplit).map(CGAArray::new).toList();
        var call_out = new AccumArrayListReturn(call_out_accum, call_out_array);
        return call_out;
    }
}
