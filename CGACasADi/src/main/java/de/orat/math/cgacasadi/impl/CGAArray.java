package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import static de.orat.math.cgacasadi.impl.CGASymbolicFunction.transformImpl;
import java.util.List;

public class CGAArray {

    private final List<? extends SparseCGASymbolicMultivector> mvs;

    public CGAArray(List<? extends SparseCGASymbolicMultivector> mvs) {
        this.mvs = mvs;
    }

    public List<? extends SparseCGASymbolicMultivector> getMVS() {
        return this.mvs;
    }

    protected SX horzcat() {
        return horzcat(this.mvs);
    }

    protected static SX horzcat(List<? extends SparseCGASymbolicMultivector> mvs) {
        StdVectorSX stdVec = transformImpl(mvs);
        SX sxHorzcat = SX.horzcat(stdVec);
        return sxHorzcat;
    }

    protected static List<? extends SparseCGASymbolicMultivector> horzsplit(SX sxHorzcat) {
        StdVectorSX stdVec = SX.horzsplit_n(sxHorzcat, sxHorzcat.columns());
        var mvs = stdVec.stream().map(SparseCGASymbolicMultivector::create).toList();
        return mvs;
    }
}
