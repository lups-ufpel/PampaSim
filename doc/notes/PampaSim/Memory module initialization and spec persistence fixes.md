I investigated why a spec created without the memory module did not work correctly when reopened with memory enabled. Comparing the saved files showed that the scenario created with memory already enabled contained the module declaration, the MemoryManagement entity, and the process memory data. The other scenario initially lacked some of these elements.

The initial dialog applied the selected modules before loading the spec. Loading then replaced the scenario and discarded that selection. I moved the existing blocks so the file is loaded first and the selected modules are applied afterward.

In SimulatedScenario.setupModules(), I registered the default configuration in the spec when the module is first created. The configuration still comes from memoryModuleTemplate.xml.

Saving previously appended another module configuration each time. I added upsertEntityConfig() to remove the previous configuration of the same entity class before adding the current one. I also registered the association between MemoryProcessCreationData and MemoryModule before interpreting the loaded events, so their memory data is retained.

Process changes were not always reflected in the XML representation. I added synchronizeProcessArrivals() to update the serialized arrival events from the current event schedule before saving. The XML is synchronized before removing the process, and the comparison object now includes its module data.

The memory tab was missing after loading another spec because the interface remained attached to the initial spec's module map. I added a stable MapProperty in PampaSimViewModel and update its underlying map when loading a spec. Existing listeners now observe the current scenario.

Memory settings are registered in the spec before rebuilding the simulation. Module selection uses syncWithSpec(), which clears the displayed process list before reconstruction. Process edits mark the scenario as unsaved. When memory is deselected, its entity configuration is removed so it is not instantiated again from the spec.

In MemoryManagement, I moved subsystem construction from the constructor into bind(), after the configuration has been applied. I corrected the page-size argument because initialize() expects KB, while getPageSize() returns bytes. I also reversed the allocation threshold arguments to match the order expected by initialize().

I added memoryProcessTemplate.xml for processes that were created without memory data. It uses the existing memoryProcessCreationData.xsd schema and defines one page, access to address zero, empty file-backed and modified-page lists, and a repeating access list. These values are separate from the global settings in memoryModuleTemplate.xml.

initializeMissingMemoryData() loads a separate template instance for each process without memory data and preserves processes that are already configured. The migration is checked before rebuilding a scenario with memory enabled. Its return value indicates whether the scenario needs saving. The template filename must match the .xml resource name used by the loader.

The migrated processes still failed even when the saved XML contained their memory data. I traced this to empty JAXB lists in ProcessMemoryInfo. Directly accessing modifyPages.pageId and fileBackedPages.pageId could return null, causing sort() to fail. Newly created processes worked because the creation dialog already initialized these lists through getPageId().

The process template was validated against the existing schema. The saved specs confirmed that module configuration and process memory data were being recorded. The final runtime correction is implemented, but execution checks still need to be recorded for old and new processes together, saving and reopening the migrated scenario, editing and deleting processes, and saving with memory disabled.