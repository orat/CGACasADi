package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iConstantsFactory;
import de.orat.math.gacalc.spi.iMultivector;

public interface iCGAConstants<IMV extends iMultivector<IMV>> extends iConstantsFactory<IMV> {

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    @Override
    default IMV getInversePseudoscalar() {
        return cached(() -> newConstant("E˜", this.getPseudoscalar().reverse()));
    }
}
