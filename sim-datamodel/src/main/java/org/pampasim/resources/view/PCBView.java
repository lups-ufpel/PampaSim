package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PCBView implements FxmlView<ProcessViewModel> {

    @InjectViewModel
    ProcessViewModel viewModel;

    @FXML private TabPane PCBViewTabPane;

    public interface ModulePCBView extends FxmlView<ProcessViewModel> {
        Set<Tab> getTabs();
    }
    public record ModuleView(Class<? extends ModulePCBView> clazz) {};
    public static HashSet<ModuleView> moduleViews = new HashSet<>();

    public void initialize() {
        for (var moduleView : moduleViews) {
            var viewTuple = FluentViewLoader
                    .fxmlView(moduleView.clazz)
                    .viewModel(viewModel)
                    .load();
            var controller = viewTuple.getCodeBehind();
            for (Tab t : controller.getTabs()) {
                var existingTabOpt = PCBViewTabPane.getTabs().stream().filter(
                        et -> Objects.equals(et.getId(), t.getId())
                    ).findAny();
                existingTabOpt.ifPresentOrElse(
                        existingTab -> {
                            var existingContent = existingTab.getContent();
                            var additionalContent = t.getContent();
                            var targetPane = (Pane) existingTab.getContent();
                            if (additionalContent instanceof Pane) {
                                var childOList = ((Pane) additionalContent).getChildren();
                                while(!childOList.isEmpty()) {
                                    // remove child from original node tree, cause node unicity is a thing apparently
                                    var child = childOList.removeFirst();
                                    targetPane.getChildren().add(child);
                                }
                            } else {
                                // remove child from original node tree, cause node unicity is a thing apparently
                                t.setContent(null);
                                targetPane.getChildren().add(additionalContent);
                            }
                        },
                        () -> {
                            PCBViewTabPane.getTabs().add(t);
                            if (!(t.getContent() instanceof Pane)) {
                                // add VBox pane wrapper, so other modules
                                // can piggyback off of the same tab instance
                                var preservedContent = t.getContent();
                                var vboxPane = new VBox();
                                t.setContent(vboxPane);
                                vboxPane.getChildren().add(preservedContent);
                            }
                        }
                );
            }
        }
        PCBViewTabPane.requestLayout();
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
