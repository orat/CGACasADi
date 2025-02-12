package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
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
    public static List<CGAArray> map(CGASymbolicFunction func, List<CGAArray> args, int iterations) {
        assert args.size() == func.getArity();
        for (var arr : args) {
            assert arr.getMVS().size() == iterations;
        }

        var casadiMapFunc = func.getCasADiFunction().map(iterations);
        var f_sym_in = new StdVectorSX(args.stream().map(CGAArray::horzcat).toList());
        var f_sym_out = new StdVectorSX();
        casadiMapFunc.call(f_sym_in, f_sym_out);
        return f_sym_out.stream()
            .map(CGAArray::horzsplit)
            .map(CGAArray::new)
            .toList();
    }

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

    // Das könnte ich in eine Utility Methode in SymbolicFunction sichern.
    // Nur den Teil, der eine Funktion mit mehreren Argumenten mappt auf eine Funktion mit einem Array an Argumenten.
    public static List<SparseCGASymbolicMultivector> foldMultipleAccum(CGASymbolicFunction func, List<SparseCGASymbolicMultivector> initialAccumValues, int iterations) {
        assert initialAccumValues.size() == func.getArity();
        // Jeder Output wird genau dem Input an der selben Stelle zugewiesen.
        assert func.getArity() == func.getResultCount();
        // Input und Output sparsity muss gleich sein.

        var casadiFunc = func.getCasADiFunction();

        StdVectorSX oldParams = casadiFunc.sx_in();

        SX oldParamsHorzcat = SX.horzcat(oldParams);
        var in2 = new StdVectorSX(List.of(oldParamsHorzcat));

        var oldReturns = new StdVectorSX();
        casadiFunc.call(oldParams, oldReturns);
        SX oldReturnsHorzcat = SX.horzcat(oldReturns);
        var out2 = new StdVectorSX(List.of(oldReturnsHorzcat));

        var newfunc = new Function("foldMultipleAccum", in2, out2);
        var newFoldedFunc = newfunc.fold(iterations);

        var f_sym_in = new StdVectorSX(List.of(CGAArray.horzcat(initialAccumValues)));
        var f_sym_out = new StdVectorSX();
        newFoldedFunc.call(f_sym_in, f_sym_out);
        var newRets = CGAArray.horzsplit(f_sym_out.get(0)).stream().map(SparseCGASymbolicMultivector::create).toList();
        return newRets;
    }

    /**
     * <pre>
     * Hier fehlt Doku.
     * </pre>
     */
    public static List<? extends SparseCGASymbolicMultivector> fold(CGASymbolicFunction func, SparseCGASymbolicMultivector initialAccumValue, List<CGAArray> otherArgs, int iterations) {
        assert otherArgs.size() + 1 == func.getArity();
        for (var arr : otherArgs) {
            assert arr.getMVS().size() == iterations;
        }
        // Input und Output sparsity muss gleich sein.

        var casadiFoldFunc = func.getCasADiFunction().fold(iterations);
        var f_sym_in = new StdVectorSX(Stream.concat(Stream.of(initialAccumValue.getSX()), otherArgs.stream().map(CGAArray::horzcat)).toList());
        var f_sym_out = new StdVectorSX();
        casadiFoldFunc.call(f_sym_in, f_sym_out);
        return f_sym_out.stream()
            .map(SparseCGASymbolicMultivector::create)
            .toList();
    }

    /**
     * <pre>
     * Hier fehlt Doku.
     * </pre>
     */
    public static List<CGAArray> mapaccum(CGASymbolicFunction func, SparseCGASymbolicMultivector initialAccumValue, List<CGAArray> otherArgs, int iterations) {
        assert otherArgs.size() + 1 == func.getArity();
        for (var arr : otherArgs) {
            assert arr.getMVS().size() == iterations;
        }
        // Input und Output sparsity muss gleich sein.

        var casadiMapaccumFunc = func.getCasADiFunction().mapaccum(iterations);
        var f_sym_in = new StdVectorSX(Stream.concat(Stream.of(initialAccumValue.getSX()), otherArgs.stream().map(CGAArray::horzcat)).toList());
        var f_sym_out = new StdVectorSX();
        casadiMapaccumFunc.call(f_sym_in, f_sym_out);
        return f_sym_out.stream()
            .map(CGAArray::horzsplit)
            .map(CGAArray::new)
            .toList();
    }

    public static List<CGAArray> mapaccumWithAccum(CGASymbolicFunction func, List<SparseCGASymbolicMultivector> initialAccumValues, int iterations) {
        assert initialAccumValues.size() == func.getArity();
        // Input und Output sparsity muss gleich sein.

        var accum = new StdVectorCasadiInt(LongStream.range(0, initialAccumValues.size()).boxed().toList());
        var casadiMapaccumFunc = func.getCasADiFunction().mapaccum("aa", iterations, accum, accum);
        var f_sym_in = new StdVectorSX(initialAccumValues.stream().map(SparseCGASymbolicMultivector::getSX).toList());
        var f_sym_out = new StdVectorSX();
        casadiMapaccumFunc.call(f_sym_in, f_sym_out);
        return f_sym_out.stream()
            .map(CGAArray::horzsplit)
            .map(CGAArray::new)
            .toList();
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

    public static void mainMapaccum() {
        var xi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("x");
        var a = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("a");
        var xi1 = xi.add(a);

        var func = CGAExprGraphFactory.instance.createFunctionSymbolic("func", List.of(xi, a), List.of(xi1));
        System.out.println(func);

        var x0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("x0", 1.0);
        var arga1 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a1", 1.0);
        var arga2 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a2", 3.0);
        var arga = new CGAArray(List.of(arga1, arga2));

        var res = mapaccum(func, x0, List.of(arga), 2).get(0).getMVS();;
        res.forEach(System.out::println);
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

    public static void mainFoldMultipleAccum() {
        var xi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("x");
        var ai = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("a");
        var xi1 = xi.add(xi);
        var ai1 = ai.add(ai);

        var func = CGAExprGraphFactory.instance.createFunctionSymbolic("func", List.of(xi, ai), List.of(xi1, ai1));

        var x0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("x0", 1.0);
        var a0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a1", 2.0);

        var res = foldMultipleAccum(func, List.of(x0, a0), 2);
        res.forEach(System.out::println);
        System.out.println("------");
    }

    public static void mainMapaccumWithAccum() {
        var xi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("x");
        var ai = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("a");
        var xi1 = xi.add(xi);
        var ai1 = ai.add(ai);

        var func = CGAExprGraphFactory.instance.createFunctionSymbolic("func", List.of(xi, ai), List.of(xi1, ai1));

        var x0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("x0", 1.0);
        var a0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a1", 2.0);

        var res = mapaccumWithAccum(func, List.of(x0, a0), 2);
        res.stream().map(CGAArray::getMVS).forEach(l -> l.forEach(System.out::println));
        // res.forEach(System.out::println);
        System.out.println("------");
    }

    public static void mainFold() {
        var xi = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("x");
        var a = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("a");
        var xi1 = xi.add(a);

        var func = CGAExprGraphFactory.instance.createFunctionSymbolic("func", List.of(xi, a), List.of(xi1));
        System.out.println(func);

        var x0 = CGAExprGraphFactory.instance.createMultivectorSymbolic("x0", 1.0);
        var arga1 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a1", 1.0);
        var arga2 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a2", 3.0);
        var arga = new CGAArray(List.of(arga1, arga2));

        var res = fold(func, x0, List.of(arga), 2);
        res.forEach(System.out::println);
        System.out.println("------");
    }

    public static void mainMap() {
        var a = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("a");
        var b = CGAExprGraphFactory.instance.createMultivectorPurelySymbolicDense("b");
        var sum = a.add(b);

        var func = CGAExprGraphFactory.instance.createFunctionSymbolic("func", List.of(a, b), List.of(sum));
        System.out.println(func);

        // a1+b1=3
        // a2+b2=7
        var arga1 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a1", 1.0);
        var arga2 = CGAExprGraphFactory.instance.createMultivectorSymbolic("a2", 3.0);
        var arga = new CGAArray(List.of(arga1, arga2));

        var argb1 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b1", 2.0);
        var argb2 = CGAExprGraphFactory.instance.createMultivectorSymbolic("b2", 4.0);
        var argb = new CGAArray(List.of(argb1, argb2));

        var res = map(func, List.of(arga, argb), 2).get(0);

        res.getMVS().forEach(System.out::println);
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
        mainMap();
        mainMapSupreme();
    }
}
