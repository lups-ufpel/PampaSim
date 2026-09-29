package org.pampasim;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.resources.ModuleSimulation;
import org.pampasim.resources.Process;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class SimulatedScenario {
    private static final Logger LOGGER = LogManager.getLogger(SimulatedScenario.class);
    @Setter
    private Path specPath;
    private ObjectProperty<Simulation> simulation;
    @Setter
    private Function<SimulatedScenario, Simulation> simulationFactory;

    @Setter
    private Consumer<PampaSimModule> modulePreInitHook;
    @Setter
    private Consumer<PampaSimModule> modulePostInitHook;

    @Setter
    private Spec spec;
    @Setter
    private boolean saved = true; // an empty scenario is "saved" since it doesn't need saving

    // feels not very java-y but by love I love passing functions around
    public SimulatedScenario(
            Spec template,
            Function<SimulatedScenario, Simulation> simulationFactory,
            Consumer<PampaSimModule> modulePreInitHook,
            Consumer<PampaSimModule> modulePostInitHook
    ) {
        this.spec = template;
        this.simulationFactory = simulationFactory;
        this.modulePreInitHook = modulePreInitHook;
        this.modulePostInitHook = modulePostInitHook;
        this.simulation = new SimpleObjectProperty<>(simulationFactory.apply(this));
    }

    public void resetToSpec() {
        var specModuleNames = getSpec().getInnerSpec().getExtraModules().getModule();
        specModuleNames.sort(null);
        var currentModuleNames = getSpec().getLoadedModules()
                .values().stream()
                .map(module -> module.getClass().getCanonicalName()).sorted().toList();
        if (!currentModuleNames.equals(specModuleNames)) {
            LOGGER.info("spec module list doesn't match currently loaded modules list, reloading all modules!");
            getSpec().getLoadedModules().clear();
            for (var moduleFQCN : specModuleNames) {
                loadModule(moduleFQCN, modulePreInitHook, modulePostInitHook);
            }
        }
        this.simulation.set(simulationFactory.apply(this));
    }

    public void saveSpec(Path path) {
        this.spec.saveSpec(path);
        this.specPath = path;
        this.saved = true;
    }

    public void setupModules(Simulation sim)
            throws ModuleSimulation.ConfigError
    {
        for (var moduleClassName : spec.getInnerSpec().getExtraModules().getModule()) {
            var freshlyLoaded = loadModule(moduleClassName, modulePreInitHook, modulePostInitHook);
            var module = getLoadedModuleByName(moduleClassName);
            var moduleClass = module.getClass();

            if (!freshlyLoaded) {
                LOGGER.debug("kept module {}", moduleClass);
                if (module.getModuleState() != PampaSimModule.ModuleState.UNBOUND) {
                    module.invalidateBinding();
                }
            }

            var payloadToModuleMap = Spec.getPayloadToModuleMap();
            var moduleEventPayloadClasses = module.getEventPayloadClasses();
            if (moduleEventPayloadClasses != null) {
                for (var payloadClass : module.getEventPayloadClasses()) {
                    payloadToModuleMap.put(payloadClass, moduleClass);
                    LOGGER.trace("payload {} associated with module {}", payloadClass, moduleClass);
                }
            }
            module.bind(sim);

            var moduleConfigClass = module.getSimulation().configClass();
            if (moduleConfigClass != null) {
                LOGGER.debug("configuring module {}", moduleClass);
                var configElemOpt = spec.getInnerSpec()
                        .getEntities()
                        .getEntity()
                        .stream()
                        .filter(entityConfig ->
                                entityConfig.getFullyQualifiedClassName().equals(moduleConfigClass.getCanonicalName())
                        ).findAny();
                if (configElemOpt.isPresent()) {
                    module.getSimulation().applyConfig(configElemOpt.get().getAny());
                } else {
                    //throw new ModuleSimulation.ConfigError("missing element! " + moduleConfigClass.getCanonicalName());
                    LOGGER.debug("module {} missing explicit config, using default", moduleClassName);
                }
            }
        }
    }

    /**
     * @param moduleClassName fully qualified class path for the module to load
     * @return whether the class was loaded (true) or the operation no-oped (false)
     */
    public boolean loadModule(
            String moduleClassName,
            Consumer<PampaSimModule> preInitCb,
            Consumer<PampaSimModule> posInitCb) {
        var alreadyLoaded = getLoadedModuleByName(moduleClassName);
        if (alreadyLoaded != null) {
            return false;
        }

        Class<? extends PampaSimModule> moduleClass = null;
        PampaSimModule module = null;
        try {
            moduleClass = (Class<? extends PampaSimModule>) Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(moduleClassName);
            var cons = moduleClass.getConstructor();
            module = cons.newInstance();
            preInitCb.accept(module);
            module.initialize();
            posInitCb.accept(module);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't load module " + moduleClassName, e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Class " + moduleClassName + " is not a module", e);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        getLoadedModules().put(moduleClass, module);

        return true;
    }

    /**
     * @param moduleClassName fully qualified class name of the target module
     * @return the module instance if loaded, null otherwise
     */
    public PampaSimModule getLoadedModuleByName(String moduleClassName) {
        var opt = getLoadedModules().entrySet().stream()
                .filter(entry ->
                        entry.getKey().getCanonicalName().equals(moduleClassName)
                ).findAny();
        return opt.map(Map.Entry::getValue).orElse(null);
    }

    public ObservableMap<Class<? extends PampaSimModule>, PampaSimModule> getLoadedModules() {
        return getSpec().getLoadedModules();
    }
}
