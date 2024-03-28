package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import de.orat.math.sparsematrix.MatrixSparsity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import util.CayleyTable;

public final class CachedSymbolicMultivector implements iMultivectorSymbolic<CachedSymbolicMultivector> {

    // Besser wäre: Eigene Klasse für den Cache. Instanz wird in CGAExprGraphFactory gehalten. Und den CachedSymbolicMultivector übergeben. "CacheFactory". Nur die CacheFactory darf den CachedSymbolicMultivector bauen. .
    private static final Map<String, CGASymbolicFunction> functionCache = new HashMap<>();

    // Evtl. Erzeugung und Aufruf trennen.
    // Alternative für res wäre Function<List<iMultivectorSymbolic>, iMultivectorSymbolic>
    //   Dann könnte ich dem res die params übergeben.
    //   Und ich könnte die Params direkt übergeben und in der Funktion selbst toSymbolic() aufrufen.
    //   Dann spare ich mit das Lazy.
    private CachedSymbolicMultivector getOrCreateSymbolicFunction(String name, List<SparseCGASymbolicMultivector> args, Function<List<SparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> res) {
        CGASymbolicFunction fun = functionCache.get(name);
        if (fun == null) {
            final int size = args.size();
            List<SparseCGASymbolicMultivector> params = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                SparseCGASymbolicMultivector arg = args.get(i);
                // Convert to purely symbolic multivector.
                SparseCGASymbolicMultivector param = fac.createMultivectorSymbolic(Integer.toString(i), arg.getSparsity());
                params.add(param);
            }
            fun = fac.createFunctionSymbolic(name, params, List.of(res.apply(params)));
            functionCache.put(name, fun);
        }
        return new CachedSymbolicMultivector(fun.callSymbolic(args).get(0));
    }

    private final static CGAExprGraphFactory fac = new CGAExprGraphFactory();

    private final SparseCGASymbolicMultivector delegate;

    public CachedSymbolicMultivector(SparseCGASymbolicMultivector delegate) {
        this.delegate = delegate;
    }

    @Override
    public CachedSymbolicMultivector op(CachedSymbolicMultivector b) {
        // _createBipedFuncName gibt es nicht in jeder Methode.
        String funName = _createBipedFuncName("op", grades(), b.grades());
        return getOrCreateSymbolicFunction(funName, List.of(this.delegate, b.delegate), (List<SparseCGASymbolicMultivector> params) -> params.get(0).op(params.get(1)));
    }

    @Override
    public void init(MultivectorSymbolic.Callback callback) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MatrixSparsity getSparsity() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isZero() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CayleyTable getCayleyTable() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector _asCachedSymbolicFunction(String name, List<CachedSymbolicMultivector> args, CachedSymbolicMultivector res) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector _asCachedSymbolicFunction(String name, List<CachedSymbolicMultivector> params, List<CachedSymbolicMultivector> args, Supplier<CachedSymbolicMultivector> res) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int grade() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int[] grades() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getGradeSelectionFunction(int grade) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getReverseFunction() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getGPFunction() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getGPWithScalarFunction(double s) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector pseudoscalar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector inversePseudoscalar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getUndualFunction() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector scalarInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getConjugateFunction() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector toSymbolic() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector add(CachedSymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector negate14() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector scalarAbs() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector scalarAtan2(CachedSymbolicMultivector y) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector scalarSqrt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector denseEmptyInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector sparseEmptyInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector exp() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector sqrt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector log() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector meet(CachedSymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector join(CachedSymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector inorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector normalizeBySquaredNorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector normalizeEvenElement() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSymbolicMultivector generalInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
