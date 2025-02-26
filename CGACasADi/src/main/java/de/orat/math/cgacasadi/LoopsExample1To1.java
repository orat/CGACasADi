package de.orat.math.cgacasadi;

import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.List;

public class LoopsExample1To1 {

    public static void mapaccum1To1() {
        var fac = GAExprGraphFactoryService.getExprGraphFactoryThrowing("cga", "cgacasadisx");

        // Lokale Variablen vor dem Loop.
        var aSim = fac.createScalarLiteral("aSim", 5);
        var aArr0 = fac.createScalarLiteral("aArr0", 7);
        var aArr1 = fac.createScalarLiteral("aArr1", 11);
        var aArr = new MultivectorSymbolicArray(List.of(aArr0, aArr1));
        var arAcc = new MultivectorSymbolicArray();
        var arAcc0 = fac.createScalarLiteral("arAcc0", 3);
        arAcc.ensureSize(1, fac.constantsSymbolic().getSparseEmptyInstance());
        arAcc.set(0, arAcc0);
        var rArr = new MultivectorSymbolicArray();

        // Loop: Abbildung der Variablen auf rein symbolische Parameter.
        // Nebenbedingung: Array Elemente müssen die gleiche Sparsity haben.
        var sym_arAcc = fac.createMultivectorPurelySymbolicFrom("sym_arAcc", arAcc0);
        var sym_aSim = fac.createMultivectorPurelySymbolicFrom("sym_aSim", aSim);
        var sym_aArr = fac.createMultivectorPurelySymbolicFrom("sym_aArr", aArr0);

        // Loop: Definition der "inneren Funktion".
        var arAcc_i1 = sym_arAcc.addition(sym_aArr);
        var rArr_i = sym_arAcc.addition(sym_aSim).addition(fac.createScalarLiteral("2", 2));

        // Loop: Erzeugung der Argumente für den Aufruf der Loop API.
        var paramsAccum = List.of(sym_arAcc);
        var paramsSimple = List.of(sym_aSim);
        var paramsArray = List.of(sym_aArr);
        var returnsAccum = List.of(arAcc_i1);
        var returnsArray = List.of(rArr_i);
        var argsAccumInitial = List.of(arAcc0);
        var argsSimple = List.of(aSim);
        var argsArray = List.of(aArr);
        var iterations = 2;

        // Loop: Aufruf der Loop API.
        var res = fac.getLoopService().mapaccum(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iterations);

        // Loop: Zuweisung der Rückgabe.
        arAcc.addAll(res.returnsAccum().get(0));
        rArr.addAll(res.returnsArray().get(0));

        // Print
        arAcc.forEach(System.out::println);
        System.out.println("---");
        rArr.forEach(System.out::println);
        System.out.println("------");
    }

    public static void main(String[] args) {
        mapaccum1To1();
    }
}
