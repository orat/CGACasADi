package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorCasadiInt;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacalc.spi.iLoopService;
import de.orat.math.gacalc.spi.iMultivectorPurelySymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolicArray;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (var arr : argsArray) {
            assert arr.size() == iterations;
        }

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
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInitial.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (var arr : argsArray) {
            assert arr.size() == iterations;
        }

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
                // CasADi treats a SX as an arbitrary long List of SX.
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
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInitial.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (var arr : argsArray) {
            assert arr.size() == iterations;
        }

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
                // CasADi treats a SX as an arbitrary long List of SX.
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

    public static void mainMapaccum() {
        var xi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("xi");
        var ai = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("ai");
        var bi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("bi");
        var h = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("h");
        var xi1 = xi.add(xi);
        var ai1 = ai.add(bi);
        var c = h;

        var paramsAccum = List.of(xi, ai);
        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsAccum = List.of(xi1, ai1);
        var returnsArray = List.of(c);

        var x0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("x0", 3.0);
        var a0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a0", 5.0);
        var argb1 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b1", 7.0);
        var argb2 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b2", 11.0);
        var arga = new CGAArray(List.of(argb1, argb2));

        var harg = CGAExprGraphFactory.instance.createMultivectorSymbolic("h", 2.7);

        var argsAccumInitial = List.of(x0, a0);
        var argsSimple = List.of(harg);
        var argsArray = List.of(arga);
        int iteration = 2;

        var res = mapaccumImpl(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
        res.returnsAccum().forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("....");
        res.returnsArray().forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void mainFold() {
        var xi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("xi");
        var ai = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("ai");
        var bi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("bi");
        var h = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("h");
        var xi1 = xi.add(xi);
        var ai1 = ai.add(bi);
        var c = h;

        var paramsAccum = List.of(xi, ai);
        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsAccum = List.of(xi1, ai1);
        var returnsArray = List.of(c);

        var x0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("x0", 3.0);
        var a0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a0", 5.0);
        var argb1 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b1", 7.0);
        var argb2 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b2", 11.0);
        var arga = new CGAArray(List.of(argb1, argb2));

        var harg = CGAExprGraphFactory.instance.createMultivectorSymbolic("h", 2.7);

        var argsAccumInitial = List.of(x0, a0);
        var argsSimple = List.of(harg);
        var argsArray = List.of(arga);
        int iteration = 2;

        var res = foldImpl(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
        res.returnsAccum().forEach(System.out::println);
        res.returnsArray().forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void mainMap() {
        var bi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("bi");
        var h = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("h");
        var xi = bi.add(bi);
        var yi = bi.add(h);

        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsArray = List.of(xi, yi);

        var argb1 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b1", 7.0);
        var argb2 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b2", 11.0);
        var argb = new CGAArray(List.of(argb1, argb2));

        var argh = CGAExprGraphFactory.instance.createMultivectorSymbolic("h", 2.7);

        var argsSimple = List.of(argh);
        var argsArray = List.of(argb);
        int iteration = 2;

        var res = mapImpl(paramsSimple, paramsArray, returnsArray, argsSimple, argsArray, iteration);
        res.forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void main(String[] args) {
        mainMap();
        mainFold();
        mainMapaccum();
    }
}
