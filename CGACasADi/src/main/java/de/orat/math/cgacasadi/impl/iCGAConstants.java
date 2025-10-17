package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iConstantsFactory;
import de.orat.math.gacalc.spi.iMultivector;
import java.util.function.Supplier;

public interface iCGAConstants<IMV extends iMultivector<IMV>> extends iConstantsFactory<IMV> {

    IMV cached2(String name, Supplier<IMV> creator);

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    // [17.10.2025, Fabian] Wäre besser, wenn die Factory das schon anbieten würde.
    @Override
    default IMV getInversePseudoscalar() {
        return cached2("E˜", () -> this.getPseudoscalar().reverse());
    }
}
