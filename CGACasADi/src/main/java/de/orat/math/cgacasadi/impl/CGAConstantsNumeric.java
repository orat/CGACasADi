package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.DmStatic;
import de.orat.math.gacalc.spi.iConstantsFactoryNumeric;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CGAConstantsNumeric implements iConstantsFactoryNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector> {

    public static final CGAConstantsNumeric instance = new CGAConstantsNumeric();

    private CGAConstantsNumeric() {

    }

    @Override
    public CGAExprGraphFactory fac() {
        return CGAExprGraphFactory.instance;
    }

    private static SparseCGANumericMultivector createSparseEmptyInstance() {
        var sparseSym = CGAConstantsSymbolic.instance.getSparseEmptyInstance();
        var sparseNum = SparseCGANumericMultivector.createFrom(sparseSym);
        return sparseNum;
    }

    @Override
    public SparseCGANumericMultivector getSparseEmptyInstance() {
        final String name = "SparseEmptyInstance";
        return cached2(name, CGAConstantsNumeric::createSparseEmptyInstance);
    }

    private static SparseCGANumericMultivector createDenseEmptyInstance() {
        var denseSym = CGAConstantsSymbolic.instance.getDenseEmptyInstance();
        var dm = DmStatic.zeros(denseSym.getSX().sparsity());
        var denseNum = SparseCGANumericMultivector.create(dm);
        return denseNum;
    }

    @Override
    public SparseCGANumericMultivector getDenseEmptyInstance() {
        final String name = "DenseEmptyInstance";
        return cached2(name, CGAConstantsNumeric::createDenseEmptyInstance);
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, SparseCGANumericMultivector> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public SparseCGANumericMultivector cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = SparseCGANumericMultivector.create(creator.get());
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    /**
     * Only to be used locally. creator must use cache of CGAConstants**Symbolic**!
     */
    private SparseCGANumericMultivector cached2(String name, Supplier<SparseCGANumericMultivector> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = creator.get();
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    public void testCache() {
        cache.values().forEach(mv -> System.out.println(mv));
        System.out.println("------------------------");
    }
}
