package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.caching.iCachedSymbolicMultivector;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import de.orat.math.sparsematrix.MatrixSparsity;
import java.util.List;
import java.util.function.Supplier;
import util.CayleyTable;

public final class CachedSparseCGASymbolicMultivector implements iCachedSymbolicMultivector<SparseCGASymbolicMultivector, CachedSparseCGASymbolicMultivector> {

    private final CGASymbolicFunctionCache cache;
    private final SparseCGASymbolicMultivector delegate;

    public CachedSparseCGASymbolicMultivector(CGASymbolicFunctionCache cache, SparseCGASymbolicMultivector delegate) {
        this.cache = cache;
        this.delegate = delegate;
    }

    @Override
    public SparseCGASymbolicMultivector getDelegate() {
        return this.delegate;
    }

    @Override
    public CachedSparseCGASymbolicMultivector op(CachedSparseCGASymbolicMultivector b) {
        // createBipedFuncName gibt es nicht in jeder Methode.
        String funName = this.cache.createBipedFuncName("op", this.delegate.grades(), b.delegate.grades());
        return this.cache.getOrCreateSymbolicFunction(funName, List.of(this.delegate, b.delegate), (List<SparseCGASymbolicMultivector> params) -> params.get(0).op(params.get(1)));
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
    public CachedSparseCGASymbolicMultivector _asCachedSymbolicFunction(String name, List<CachedSparseCGASymbolicMultivector> args, CachedSparseCGASymbolicMultivector res) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector _asCachedSymbolicFunction(String name, List<CachedSparseCGASymbolicMultivector> params, List<CachedSparseCGASymbolicMultivector> args, Supplier<CachedSparseCGASymbolicMultivector> res) {
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
    public CachedSparseCGASymbolicMultivector pseudoscalar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector inversePseudoscalar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getUndualFunction() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector scalarInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iFunctionSymbolic getConjugateFunction() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector toSymbolic() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector add(CachedSparseCGASymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector negate14() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector scalarAbs() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector scalarAtan2(CachedSparseCGASymbolicMultivector y) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector scalarSqrt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector denseEmptyInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector sparseEmptyInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector exp() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector sqrt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector log() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector meet(CachedSparseCGASymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector join(CachedSparseCGASymbolicMultivector b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector inorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector normalizeBySquaredNorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector normalizeEvenElement() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CachedSparseCGASymbolicMultivector generalInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
