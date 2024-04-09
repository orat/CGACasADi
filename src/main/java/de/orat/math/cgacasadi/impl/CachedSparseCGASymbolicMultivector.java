package de.orat.math.cgacasadi.impl;

import java.util.List;

public final class CachedSparseCGASymbolicMultivector extends SparseCGASymbolicMultivector {

    private final CGASymbolicFunctionCache cache;

    public CachedSparseCGASymbolicMultivector(CGASymbolicFunctionCache cache, SparseCGASymbolicMultivector mv) {
        super(mv);
        this.cache = cache;
    }

    @Override
    public SparseCGASymbolicMultivector op(SparseCGASymbolicMultivector b) {
        String funName = this.cache.createBipedFuncName("op", super.grades(), b.grades());
        return this.cache.getOrCreateSymbolicFunction(funName, List.of(this, b),
            (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).op_super(params.get(1))
        );
    }

    private SparseCGASymbolicMultivector op_super(SparseCGASymbolicMultivector b) {
        return super.op(b);
    }
}
