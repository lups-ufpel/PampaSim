package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCBView implements FxmlView<ProcessViewModel> {

    @InjectViewModel
    ProcessViewModel viewModel;

    @FXML
    VBox mainVBox;

    public record ModuleView(Class<? extends FxmlView<ProcessViewModel>> clazz) {};
    public static HashSet<ModuleView> moduleViews = new HashSet<>();

    public void initialize() {
        for (var moduleView : moduleViews) {
            var view = FluentViewLoader
                    .fxmlView(moduleView.clazz)
                    .viewModel(viewModel)
                    .load().getView();
            mainVBox.getChildren().add(view);
        }
    }

    /**
     * @param moduleView The module to add
     * Register a module's inspector view node
     * @apiNote Call before instancing the PCBView, so initialize() is called with the right state.
     * @apiNote idempotent, modules can only be registered once, extra calls are ignored
     * @see this.unregisterModuleView
     */
    public static void registerModuleView(ModuleView moduleView) {
        moduleViews.add(moduleView);
    }

    /**
     * @param moduleView The module to remove
     * Unregister a module's inspector view node
     * @apiNote Won't remove module view from existing PCBView instances
     * @apiNote idempotent, modules can only be registered once, extra calls are ignored
     * @see this.registerModuleView
     */
    public static void unregisterModuleView(ModuleView moduleView) {
        moduleViews.remove(moduleView);
    }
}
