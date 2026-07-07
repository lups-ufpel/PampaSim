package org.pampasim.resources;

import org.pampasim.core.SimulationBase;
import org.pampasim.resources.view.PCBView;

/**
 * Behavior expected from simulation modules.
 */
public abstract class ModuleBase extends SimulationBase {
    public PCBView.ModulePCBView getPCBViewExtensions() {
        return null;
    }
}