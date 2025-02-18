package de.orat.math.cgacasadi;

import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.List;

public class LoopsExample {

    public static void mapaccum() {
        var fac = GAExprGraphFactoryService.getExprGraphFactoryThrowing();

        var xi = fac.createMultivectorPurelySymbolicDense("xi");
        var ai = fac.createMultivectorPurelySymbolicDense("ai");
        var bi = fac.createMultivectorPurelySymbolicDense("bi");
        var h = fac.createMultivectorPurelySymbolicDense("h");
        var xi1 = xi.addition(xi);
        var ai1 = ai.addition(bi);
        var c = h;

        var paramsAccum = List.of(xi, ai);
        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsAccum = List.of(xi1, ai1);
        var returnsArray = List.<MultivectorSymbolic>of(c);

        var x0 = fac.createScalarLiteral("x0", 3.0);
        var a0 = fac.createScalarLiteral("a0", 5.0);
        var argb1 = fac.createScalarLiteral("b1", 7.0);
        var argb2 = fac.createScalarLiteral("b2", 11.0);
        var arga = new MultivectorSymbolicArray(List.of(argb1, argb2));

        var harg = fac.createScalarLiteral("h", 2.7);

        var argsAccumInitial = List.of(x0, a0);
        var argsSimple = List.of(harg);
        var argsArray = List.of(arga);
        int iteration = 2;

        var res = fac.getLoopService().mapaccum(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
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

    public static void fold() {
        var fac = GAExprGraphFactoryService.getExprGraphFactoryThrowing();

        var xi = fac.createMultivectorPurelySymbolicDense("xi");
        var ai = fac.createMultivectorPurelySymbolicDense("ai");
        var bi = fac.createMultivectorPurelySymbolicDense("bi");
        var h = fac.createMultivectorPurelySymbolicDense("h");
        var xi1 = xi.addition(xi);
        var ai1 = ai.addition(bi);
        var c = h;

        var paramsAccum = List.of(xi, ai);
        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsAccum = List.of(xi1, ai1);
        var returnsArray = List.<MultivectorSymbolic>of(c);

        var x0 = fac.createScalarLiteral("x0", 3.0);
        var a0 = fac.createScalarLiteral("a0", 5.0);
        var argb1 = fac.createScalarLiteral("b1", 7.0);
        var argb2 = fac.createScalarLiteral("b2", 11.0);
        var arga = new MultivectorSymbolicArray(List.of(argb1, argb2));

        var harg = fac.createScalarLiteral("h", 2.7);

        var argsAccumInitial = List.of(x0, a0);
        var argsSimple = List.of(harg);
        var argsArray = List.of(arga);
        int iteration = 2;

        var res = fac.getLoopService().fold(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
        res.returnsAccum().forEach(System.out::println);
        res.returnsArray().forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void map() {
        var fac = GAExprGraphFactoryService.getExprGraphFactoryThrowing();

        var bi = fac.createMultivectorPurelySymbolicDense("bi");
        var h = fac.createMultivectorPurelySymbolicDense("h");
        var xi = bi.addition(bi);
        var yi = bi.addition(h);

        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsArray = List.of(xi, yi);

        var argb1 = fac.createScalarLiteral("b1", 7.0);
        var argb2 = fac.createScalarLiteral("b2", 11.0);
        var argb = new MultivectorSymbolicArray(List.of(argb1, argb2));

        var argh = fac.createScalarLiteral("h", 2.7);

        var argsSimple = List.of(argh);
        var argsArray = List.of(argb);
        int iteration = 2;

        var res = fac.getLoopService().map(paramsSimple, paramsArray, returnsArray, argsSimple, argsArray, iteration);
        res.forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void main(String[] args) {
        map();
        fold();
        mapaccum();
    }
}
