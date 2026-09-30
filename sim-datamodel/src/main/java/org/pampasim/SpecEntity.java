package org.pampasim;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.resources.EntityConfig;

public interface SpecEntity extends SimEntity {
    /// get the XML element
    EntityConfig getSpecData();
    void applySpecData(EntityConfig entityConfig) throws SpecEntityDataMismatch;
}
