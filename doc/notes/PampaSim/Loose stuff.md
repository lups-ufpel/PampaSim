manually culled TODO list:
```
sim-datamodel/src/main/java/org/pampasim/resources/Process.java:    public static class CreationData { // TODO: some module info needs to be part of the creation data later when a module is included
sim-datamodel/src/main/java/org/pampasim/resources/ProcessorCore.java:    // TODO: there is an issue where the core status will not update back to FREE when a process is interrupted by memory
sim-datamodel/src/main/java/org/pampasim/resources/view/CreateProcessDialogView.java:        //TODO: handle more invalid inputs
sim-datamodel/src/main/java/org/pampasim/resources/viewmodel/ProcessMemoryInfoViewModel.java:            // TODO: log invalid address input
sim-datamodel/src/main/java/org/pampasim/resources/viewmodel/ProcessViewModel.java:    /// callback hell magic, TODO FIXME
sim-datamodel/src/main/java/org/pampasim/resources/viewmodel/ProcessViewModel.java:    /// callback hell magic, TODO FIXME
sim/src/main/java/org/pampasim/dialog/EditProcessDialogService.java:        viewTuple.getCodeBehind().setProcessData(start, duration, priority, color); //TODO: NOT THE BEST OPTION
sim/src/main/java/org/pampasim/dsl/spec/Spec.java:                        bodge = true; // TODO / FIXME: no clue how to translate these properly without restructuring the modules
sim/src/main/java/org/pampasim/entity/Processor.java:        // TODO: handle IO operation schedule
sim/src/main/java/org/pampasim/entity/schedulers/Scheduler.java:// TODO: write a suite of tests that assert the invariants as described below to validate foreign schedulers
sim/src/main/java/org/pampasim/viewModel/PampaSimViewModel.java:                        //TODO: make adding module info part of the creation data
sim/src/main/java/org/pampasim/viewModel/PampaSimViewModel.java:        // FIXME / TODO: this can be made more thorough by analysing if there are any unhandled events
```
FIXME list:
```
core/src/main/java/org/pampasim/core/SimulationBase.java:286:        // WARN / FIXME: will name conflict if there are multiple entities of the same type in a simulation!
core/src/main/java/org/pampasim/core/entity/AbstractSimEntity.java:178:        // WARN / FIXME: will name conflict if there are multiple entities of the same type in a simulation!
memory/src/main/java/org/pampasim/memory/entity/MMU.java:75:        // FIXME: setting up the process memory info here for testing purposes
schemas/event.xsd:9:        <!-- FIXME: figure out a way to give each event kind a schema -->
schemas/event.xsd:14:    <!-- FIXME: timing characteristic complex type -->
schemas/events/processCreationData.xsd:9:                <!-- FIXME: regex restriction for the color -->
sim-datamodel/pom.xml:14:  <!-- FIXME change it to the project's website -->
sim-datamodel/src/main/java/org/pampasim/resources/view/CreateProcessDialogView.java:91:                        //FIXME: not working properly
sim-datamodel/src/main/java/org/pampasim/resources/viewmodel/ProcessViewModel.java:43:    /// callback hell magic, TODO FIXME
sim-datamodel/src/main/java/org/pampasim/resources/viewmodel/ProcessViewModel.java:49:    /// callback hell magic, TODO FIXME
sim/src/main/java/org/pampasim/dsl/spec/Spec.java:121:                        bodge = true; // TODO / FIXME: no clue how to translate these properly without restructuring the modules
sim/src/main/java/org/pampasim/entity/schedulers/RoundRobin.java:11:// Doesn't respect priorities! FIXME
sim/src/main/java/org/pampasim/view/PampaSimView.java:184:        pampaSimViewModel.setTabPane(moduleTabPane); // FIXME: tight coupling
sim/src/main/java/org/pampasim/view/PampaSimView.java:406:                    // FIXME: this might be too slow, consider exposing a hashmap for these queries
sim/src/main/java/org/pampasim/view/PampaSimView.java:448:                                                    setTooltip(new Tooltip(item.toString())); // FIXME: localization?
sim/src/main/java/org/pampasim/viewModel/PampaSimViewModel.java:161:            // FIXME: There likely is a more elegant solution than this
sim/src/main/java/org/pampasim/viewModel/PampaSimViewModel.java:194:        LOGGER.info("saved to {}", path); // might've failed, report back if so FIXME
sim/src/main/java/org/pampasim/viewModel/PampaSimViewModel.java:236:        if (userSelection.modules().getFirst().equals("memory")) { // FIXME: multiple modules
sim/src/main/java/org/pampasim/viewModel/PampaSimViewModel.java:407:            // FIXME: allProcesses should be a hashMap over (creationId, pvm) at this point
sim/src/main/java/org/pampasim/viewModel/PampaSimViewModel.java:497:        // FIXME / TODO: this can be made more thorough by analysing if there are any unhandled events
tools/src/main/java/org/pampasim/tools/EntityCodeGenTool.java:120:            // FIXME: use the Path API
tools/src/main/java/org/pampasim/tools/EventCodeGenTool.java:92:                // FIXME: use resource streams like https://stackoverflow.com/a/17705322
```

- [x] TODO Remember to update the memory information once the process view model gets a process binding ✅ 2026-05-29