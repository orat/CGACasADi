package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.function.Supplier;
import de.orat.math.gacalc.spi.iConstantsProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CGAConstantsProvider implements iConstantsProvider<SparseCGASymbolicMultivector> {

    public static final CGAConstantsProvider instance = new CGAConstantsProvider();

    private CGAConstantsProvider() {

    }

    @Override
    public SparseCGASymbolicMultivector newConstant(String name, SparseDoubleMatrix definition) {
        return SparseCGASymbolicMultivector.create(name, definition);
    }

    @Override
    public iExprGraphFactory<SparseCGASymbolicMultivector, PurelySymbolicCachedSparseCGASymbolicMultivector> fac() {
        return CGAExprGraphFactory.instance;
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final Map<Supplier<SparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public synchronized SparseCGASymbolicMultivector cached(Supplier<SparseCGASymbolicMultivector> creator) {
        return this.cache.computeIfAbsent(creator, Supplier::get);
    }

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    @Override
    public SparseCGASymbolicMultivector getInversePseudoscalar() {
        return cached(() -> SparseCGASymbolicMultivector.create("E˜", this.getPseudoscalar().reverse().getSX()));
    }

}
