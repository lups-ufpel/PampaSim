package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.util.HashMap;
import java.util.Map;

public final class ViewListBinder {

    private ViewListBinder() {}

    public static <VM> void bind(
            HBox container,
           ObservableList<VM> items,
           Callback<VM, Parent> nodeProvider) {

        Map<VM, Parent> nodeCache = new HashMap<>();
        Callback<VM, Parent> cachedProvider = vm -> nodeCache.computeIfAbsent(vm, nodeProvider::call);

        container.getChildren().setAll(items.stream().map(cachedProvider::call).toList());

        items.addListener((ListChangeListener<VM>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    for (VM vm : change.getRemoved()) {
                        Parent node = nodeCache.get(vm);
                        container.getChildren().remove(node);
                    }
                }
                if (change.wasAdded()) {
                    for (VM vm : change.getAddedSubList()) {
                        Parent node = cachedProvider.call(vm);
                        container.getChildren().add(node);
                    }
                }
            }
        });
    }

    // Factory para MVVMFX FXML
    public static <VM extends ViewModel> Callback<VM, Parent> mvvmfxFxmlFactory(
            Class<? extends FxmlView<VM>> viewClass) {
        return vm -> FluentViewLoader
                .fxmlView(viewClass)
                .viewModel(vm)
                .load()
                .getView();
    }
}
