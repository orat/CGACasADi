package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorCasadiInt;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacalc.spi.iMultivectorPurelySymbolic;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class CGASymbolicFunctionService {

    /**
     * <pre>
     * for-loop equivalent.
     * Computes the function for each array element.
     *
     * The map operation exhibits constant graph size and initialization time.
     *
     * The size of args list needs to be equal to the arity of the function.
     * The size of the arrays needs to be equal to the number of iterations.
     * </pre>
     */
    public static <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> List<CGAArray> mapSupreme(
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends SparseCGASymbolicMultivector> returnsArray,
        List<? extends SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (var arr : argsArray) {
            assert arr.getMVS().size() == iterations;
        }

        var def_sym_in = new StdVectorSX(
            Stream.concat(
                paramsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                paramsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(returnsArray.stream().map(ISparseCGASymbolicMultivector::getSX).toList());
        var f_sym_casadi = new Function("MapSupremeBase", def_sym_in, def_sym_out);

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
        f_sym_casadi.map("MapSupremeMap", "serial", iterations, nonRepeated, new StdVectorCasadiInt()).call(call_sym_in, call_sym_out);

        var call_out = call_sym_out.stream().map(CGAArray::horzsplit).map(CGAArray::new).toList();
        return call_out;
    }

    private static <T> Stream<T> StreamConcat(Stream<? extends T> a, Stream<? extends T> b, Stream<? extends T> c) {
        return Stream.concat(a, Stream.concat(b, c));
    }

    public static record FoldSupremeReturn(List<SparseCGASymbolicMultivector> returnsAccum, List<CGAArray> returnsArray) {

    }

    public static <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> FoldSupremeReturn foldSupreme(
        List<MV> paramsAccum,
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends SparseCGASymbolicMultivector> returnsAccum,
        List<? extends SparseCGASymbolicMultivector> returnsArray,
        List<? extends SparseCGASymbolicMultivector> argsAccumInital,
        List<? extends SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInital.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (var arr : argsArray) {
            assert arr.getMVS().size() == iterations;
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
        var f_sym_casadi = new Function("foldSupremeBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            StreamConcat(
                Stream.of(CGAArray.horzcat(argsAccumInital)),
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
        var call_out = new FoldSupremeReturn(call_out_accum, call_out_array);
        return call_out;
    }

    public static record MapaccumSupremeReturn(List<CGAArray> returnsAccum, List<CGAArray> returnsArray) {

    }

    public static <MV extends ISparseCGASymbolicMultivector & iMultivectorPurelySymbolic> MapaccumSupremeReturn mapaccumSupreme(
        List<MV> paramsAccum,
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends SparseCGASymbolicMultivector> returnsAccum,
        List<? extends SparseCGASymbolicMultivector> returnsArray,
        List<? extends SparseCGASymbolicMultivector> argsAccumInital,
        List<? extends SparseCGASymbolicMultivector> argsSimple,
        List<CGAArray> argsArray,
        int iterations) {
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInital.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (var arr : argsArray) {
            assert arr.getMVS().size() == iterations;
        }

        var def_sym_in = new StdVectorSX(
            StreamConcat(
                paramsAccum.stream().map(ISparseCGASymbolicMultivector::getSX),
                paramsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                paramsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(
            StreamConcat(
                returnsAccum.stream().map(ISparseCGASymbolicMultivector::getSX),
                Collections.<ISparseCGASymbolicMultivector>emptyList().stream().map(ISparseCGASymbolicMultivector::getSX),
                returnsArray.stream().map(ISparseCGASymbolicMultivector::getSX)
            ).toList()
        );
        var f_sym_casadi = new Function("MapaccumSupremeBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            StreamConcat(
                argsAccumInital.stream().map(ISparseCGASymbolicMultivector::getSX),
                // CasADi treats a SX as an arbitrary long List of SX.
                // No need to use repmat.
                argsSimple.stream().map(ISparseCGASymbolicMultivector::getSX),
                argsArray.stream().map(CGAArray::horzcat)
            ).toList()
        );
        var call_sym_out = new StdVectorSX();
        // Works as long as they are first in def_sym_in and call_sym_in.
        var accumVars = new StdVectorCasadiInt(LongStream.range(0, paramsAccum.size()).boxed().toList());
        f_sym_casadi.mapaccum("MapaccumSupremeMapaccum", iterations, accumVars, accumVars).call(call_sym_in, call_sym_out);

        var call_out_all = call_sym_out.stream().toList();
        var call_out_accum = call_out_all.subList(0, returnsAccum.size()).stream().map(CGAArray::horzsplit).map(CGAArray::new).toList();
        var call_out_array = call_out_all.subList(returnsAccum.size(), call_out_all.size()).stream().map(CGAArray::horzsplit).map(CGAArray::new).toList();
        var call_out = new MapaccumSupremeReturn(call_out_accum, call_out_array);
        return call_out;
    }

    public static void mainMapaccumSupreme() {
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

        var res = mapaccumSupreme(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
        res.returnsAccum.forEach(o -> {
            System.out.println("..");
            o.getMVS().forEach(System.out::println);
        });
        System.out.println("....");
        res.returnsArray.forEach(o -> {
            System.out.println("..");
            o.getMVS().forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void mainFoldSupreme() {
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

        var res = foldSupreme(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
        res.returnsAccum.forEach(System.out::println);
        res.returnsArray.forEach(o -> {
            System.out.println("..");
            o.getMVS().forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void mainMapSupreme() {
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

        var res = mapSupreme(paramsSimple, paramsArray, returnsArray, argsSimple, argsArray, iteration);
        res.forEach(o -> {
            System.out.println("..");
            o.getMVS().forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void main(String[] args) {
        mainMapSupreme();
        mainFoldSupreme();
        mainMapaccumSupreme();
    }
}
