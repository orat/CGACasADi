package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import java.util.List;
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

    public static List<? extends SparseCGASymbolicMultivector> foldMultipleAccum(CGASymbolicFunction func, List<SparseCGASymbolicMultivector> initialAccumValues, int iterations) {
        assert initialAccumValues.size() == func.getArity();
        // Jeder Output wird genau dem Input an der selben Stelle zugewiesen.
        assert func.getArity() == func.getResultCount();
        // Input und Output sparsity muss gleich sein.

        // Mal angenommen, ich habe eine Funktion, bei denen jeder Wert von einem vorherigen Wert abhängt.
        // Ich brauche die Parameter der alten Funktion. In der Funktion werden sie horzsplittiert. Und bei Übergeben horzcatted.
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
        System.out.println(newFoldedFunc);

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

        var res = mapaccum(func, x0, List.of(arga), 2).get(0).getMVS();
        res.forEach(System.out::println);
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

    public static void main(String[] args) {
        mainFoldMultipleAccum();
    }
}
