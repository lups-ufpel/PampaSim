package org.pampasim.resources.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import org.pampasim.PampaSimModule;

public abstract class ModuleInfoViewModel implements ViewModel {
    public abstract Class<? extends PampaSimModule> getPampaSimModuleClass();
}
