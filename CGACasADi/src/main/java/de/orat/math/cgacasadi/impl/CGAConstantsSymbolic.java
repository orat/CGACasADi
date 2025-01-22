package de.orat.math.cgacasadi.impl;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.function.Supplier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import de.orat.math.gacalc.spi.iConstantsFactorySymbolic;

public class CGAConstantsSymbolic implements iCGAConstants<SparseCGASymbolicMultivector>, iConstantsFactorySymbolic<SparseCGASymbolicMultivector> {

    public static final CGAConstantsSymbolic instance = new CGAConstantsSymbolic();

    private CGAConstantsSymbolic() {

    }

    @Override
    public SparseCGASymbolicMultivector newConstant(String name, SparseDoubleMatrix definition) {
        return SparseCGASymbolicMultivector.create(name, definition);
    }

    @Override
    public SparseCGASymbolicMultivector newConstant(String name, SparseCGASymbolicMultivector definition) {
        return SparseCGASymbolicMultivector.create(name, definition.getSX());
    }

    @Override
    public CGAExprGraphFactory fac() {
        return CGAExprGraphFactory.instance;
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final Map<Supplier<SparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public synchronized SparseCGASymbolicMultivector cached(Supplier<SparseCGASymbolicMultivector> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(creator);
        if (value == null) {
            value = creator.get();
            this.cache.put(creator, value);
        }
        return value;
    }

    @Override
    public SparseCGASymbolicMultivector getSparseEmptyInstance() {
        return cached(PurelySymbolicCachedSparseCGASymbolicMultivector::createSparse);
    }

    @Override
    public SparseCGASymbolicMultivector getDenseEmptyInstance() {
        return cached(PurelySymbolicCachedSparseCGASymbolicMultivector::createDense);
    }
}
