package org.pampasim;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.resources.EntityConfig;

public interface SpecEntity extends SimEntity {
    Class<?> getSpecConfigClass();
    /// get the XML element
    EntityConfig generateSpecEntityConfig();
    void applySpecEntityConfig(EntityConfig entityConfig) throws SpecEntityDataMismatch;
}
