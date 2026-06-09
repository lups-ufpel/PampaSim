package org.pampasim.entity.schedulers;

import jakarta.xml.bind.JAXBElement;
import org.pampasim.ObjectFactory;
import org.pampasim.SpecEntity;

import javax.management.RuntimeErrorException;
import java.math.BigInteger;
import java.util.Optional;

public interface RespectsQuantum extends SpecEntity {
    /// should it error out instead if no info is found?
    default int getQuantum() {
        var anyOpt = Optional.ofNullable(this.getSpecData().getAny());
        var quantumOpt = anyOpt.flatMap(anyElem -> {
            if (anyElem instanceof JAXBElement<?> elem) {
                if (elem.getName().getLocalPart().equals("quantum")) {
                    return Optional.of(((BigInteger) elem.getValue()).intValue());
                }
            }
            return Optional.empty();
        });
        return quantumOpt.orElse(4);
    }
    default void setQuantum(int quantum) {
        var anyOpt = Optional.ofNullable(this.getSpecData().getAny());
        anyOpt.ifPresentOrElse(anyElem -> {
            if (anyElem instanceof JAXBElement<?> elem) {
                if (elem.getName().getLocalPart().equals("quantum")) {
                    ((JAXBElement<BigInteger>) elem).setValue(BigInteger.valueOf(quantum));
                }
            } else {
                throw new RuntimeErrorException(
                        new Error("can't set quantum, existing data object is not quantum"));
            }
        }, () -> {
            var objFact = new ObjectFactory();
            this.getSpecData().setAny(objFact.createQuantum(BigInteger.valueOf(quantum)));
        });
    }
}
