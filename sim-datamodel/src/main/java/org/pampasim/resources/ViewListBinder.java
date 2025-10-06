package org.pampasim.resources;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

    public static void bind(
            Map<Process.State, HBox> nodeMap,
            ProgressBar progressBar,
            Function<ProcessViewModel, ObservableValue<Double>> progressExtractor,
            ObservableList<ProcessViewModel> allProcess,
            Callback<ProcessViewModel, Parent> nodeProvider,
            Process.State runningState) {

        Map<ProcessViewModel, Parent> nodeCache = new HashMap<>();

        Callback<ProcessViewModel, Parent> cachedProvider =
                vm -> nodeCache.computeIfAbsent(vm, nodeProvider::call);

        allProcess.addListener((ListChangeListener<ProcessViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessViewModel vm : change.getAddedSubList()) {

                        // Place node in initial state box
                        Parent node = cachedProvider.call(vm);
                        HBox targetBox = nodeMap.get(vm.getState());
                        if (targetBox != null && !targetBox.getChildren().contains(node)) {
                            targetBox.getChildren().add(node);
                        }

                        // Listen for state transitions
                        vm.stateProperty().addListener((obs, oldState, newState) -> {
                            Parent n = cachedProvider.call(vm);
                            Optional.ofNullable(nodeMap.get(oldState))
                                    .ifPresent(box -> box.getChildren().remove(n));
                            Optional.ofNullable(nodeMap.get(newState))
                                    .ifPresent(box -> box.getChildren().add(n));

                            if (newState == runningState) {
                                progressBar.progressProperty().bind(progressExtractor.apply(vm));
                            } else if (oldState == runningState) {
                                if (progressBar.progressProperty().isBound()) {
                                    progressBar.progressProperty().unbind();
                                }
                                progressBar.setProgress(0);
                            }
                        });

                        // Initial state check
                        if (vm.getState() == runningState) {
                            progressBar.progressProperty().bind(progressExtractor.apply(vm));
                        }
                    }
                }

                if (change.wasRemoved()) {
                    for (ProcessViewModel vm : change.getRemoved()) {
                        Parent node = nodeCache.remove(vm);
                        nodeMap.values().forEach(box -> box.getChildren().remove(node));
                        if (progressBar.progressProperty().isBound()) {
                            progressBar.progressProperty().unbind();
                        }
                        progressBar.setProgress(0);
                    }
                }
            }
        });

        // Handle initial list
        allProcess.forEach(vm -> {
            Parent node = cachedProvider.call(vm);
            HBox targetBox = nodeMap.get(vm.getState());
            if (targetBox != null && !targetBox.getChildren().contains(node)) {
                targetBox.getChildren().add(node);
            }

            if (vm.getState() == runningState) {
                progressBar.progressProperty().bind(progressExtractor.apply(vm));
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
