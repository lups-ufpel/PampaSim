package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.pampasim.resources.viewmodel.ProcessViewModel;
import org.pampasim.resources.Process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ViewListBinder {

    public static void bind(
            Map<Process.State, HBox> nodeMap,
            ObservableList<ProcessViewModel> allProcess,
            Callback<ProcessViewModel, Parent> nodeProvider) {

        Map<ProcessViewModel, Parent> nodeCache = new HashMap<>();

        // Um provider que reusa nós já criados
        Callback<ProcessViewModel, Parent> cachedProvider =
                vm -> nodeCache.computeIfAbsent(vm, nodeProvider::call);

        allProcess.addListener((ListChangeListener<ProcessViewModel>) change -> {
            while (change.next()) {

                /* ========== 1. ITENS ADICIONADOS ========== */
                if (change.wasAdded()) {
                    for (ProcessViewModel vm : change.getAddedSubList()) {

                        // -- 1.a Coloca o nó no HBox correto logo de cara --
                        Parent node = cachedProvider.call(vm);

                        HBox targetBox = nodeMap.get(vm.getState());
                        if (targetBox != null && !targetBox.getChildren().contains(node)) {
                            targetBox.getChildren().add(node);
                        }

                        // -- 1.b Listener para mudanças futuras de estado --
                        vm.stateProperty().addListener((obs, oldState, newState) -> {
                            Parent n = cachedProvider.call(vm);
                            Optional.ofNullable(nodeMap.get(oldState))
                                    .ifPresent(box -> box.getChildren().remove(n));
                            Optional.ofNullable(nodeMap.get(newState))
                                    .ifPresent(box -> box.getChildren().add(n));
                        });
                    }
                }

                /* ========== 2. ITENS REMOVIDOS ========== */
                if (change.wasRemoved()) {
                    for (ProcessViewModel vm : change.getRemoved()) {
                        Parent node = nodeCache.remove(vm);
                        // Remove o nó de qualquer HBox em que ainda esteja
                        nodeMap.values().forEach(box -> box.getChildren().remove(node));
                        // Se for necessário, remova também o listener de estado aqui
                        // (use WeakListener ou guarde a referência para detach explícito)
                    }
                }
            }
        });

        /* ========== 3. TRATA A LISTA INICIAL (caso já não esteja vazia) ========== */
        allProcess.forEach(vm -> {
            Parent node = cachedProvider.call(vm);
            HBox targetBox = nodeMap.get(vm.getState());
            if (targetBox != null && !targetBox.getChildren().contains(node)) {
                targetBox.getChildren().add(node);
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
