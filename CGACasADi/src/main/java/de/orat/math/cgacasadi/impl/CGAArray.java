package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import static de.orat.math.cgacasadi.impl.CGASymbolicFunction.transformImpl;
import de.orat.math.gacalc.spi.iMultivectorSymbolicArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CGAArray extends ArrayList<SparseCGASymbolicMultivector> implements iMultivectorSymbolicArray<SparseCGASymbolicMultivector> {

    public CGAArray() {
        super();
    }

    /**
     *
     * @param mvs all elements must have equal sparsity.
     */
    public CGAArray(Collection<? extends SparseCGASymbolicMultivector> mvs) {
        super(mvs);
    }

    public final boolean areSparsitiesSubsetsOf(Sparsity sparsity) {
        for (var e : this) {
            if (!e.getSX().sparsity().is_subset(sparsity)) {
                return false;
            }
        }
        return true;
    }

    public static SX horzcat(List<? extends ISparseCGASymbolicMultivector> mvs) {
        StdVectorSX stdVec = transformImpl(mvs);
        SX sxHorzcat = SX.horzcat(stdVec);
        return sxHorzcat;
    }

    public static List<? extends SparseCGASymbolicMultivector> horzsplit(SX sxHorzcat) {
        StdVectorSX stdVec = SX.horzsplit_n(sxHorzcat, sxHorzcat.columns());
        var mvs = stdVec.stream().map(SparseCGASymbolicMultivector::create).toList();
        return mvs;
    }
}
