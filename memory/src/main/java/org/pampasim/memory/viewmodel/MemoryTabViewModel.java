package org.pampasim.memory.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.events.Event;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.entity.PhysicalMemory;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MemoryTabViewModel implements ViewModel {
    private static final Logger LOGGER = LogManager.getLogger(MemoryTabViewModel.class);

    private Map<Long, Color> colorMap;
    private MemoryManagement memoryManagement;

    @Getter private final SimpleStringProperty virtualAddress = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty pageNumber = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty offset = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty pageTableNumber = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty validBit = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty frameNumber = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty physicalAddress = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty infoTitle = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty infoText = new SimpleStringProperty("");
    @Getter private final SimpleObjectProperty<Color> infoColor = new SimpleObjectProperty<>();

    @Getter private final ObservableList<MemoryFrameViewModel> observableRamFrameList = FXCollections.observableArrayList();
    @Getter private final ObservableList<MemoryFrameViewModel> observableSwapFrameList = FXCollections.observableArrayList();
    @Getter private final ObservableList<ProcessViewModel> observableProcessList;

    private boolean resetInfo;

    public MemoryTabViewModel(
            MemoryManagement memoryManagement,
            Map<Long, Color> colorMap,
            ObservableList<ProcessViewModel> observableProcessList) {

        this.memoryManagement = memoryManagement;
        this.colorMap = colorMap;
        this.observableProcessList = observableProcessList;

        setupSnoopers();

        List<PageTableEntry> ramFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getMainMemory().getRawFrameAllocationList();

        List<PageTableEntry> swapFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getSwapFile().getRawFrameAllocationList();


        createFrameList(ramFrameList, observableRamFrameList);
        createFrameList(swapFrameList, observableSwapFrameList);
    }

    private void createFrameList(List<PageTableEntry> frameList, ObservableList<MemoryFrameViewModel> observableList) {
        IntStream.range(0, frameList.size()).forEach(i -> {
            PageTableEntry entry = frameList.get(i);
            MemoryFrameViewModel vm = new MemoryFrameViewModel(i);

            if (entry != null) {
                Process process = entry.getProcess();
                vm.getColorProperty().set(colorMap.getOrDefault(process.getCreationData().getCreationId(), Color.BLACK));
                vm.getPid().set(process.getPid());
                vm.getPageNumber().set(entry.getPageNumber());
            } else {
                vm.getPid().set(null);
                vm.getColorProperty().set(null);
                vm.getPageNumber().set(-1);
            }

            observableList.add(vm);
        });
    }


    private void updateFrameList(List<PageTableEntry> frameList, ObservableList<MemoryFrameViewModel> observableList) {
        for (int i = 0; i < frameList.size(); i++) {
            PageTableEntry entry = frameList.get(i);
            MemoryFrameViewModel vm = observableList.get(i);

            if (entry != null) {
                Process process = entry.getProcess();
                vm.getPid().set(process.getPid());
                vm.getColorProperty().set(colorMap.getOrDefault(process.getCreationData().getCreationId(), null));
                vm.getPageNumber().set(entry.getPageNumber());
            } else {
                vm.getPid().set(null);
                vm.getColorProperty().set(null);
                vm.getPageNumber().set(-1);
            }
        }
    }


    private PageTableEntry findPageTableEntryForFrame(Process process, int frameIndex) {
        ProcessMemoryInfo memoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        if (memoryInfo == null) return null;

        return memoryInfo.getPageTable().getAllEntries().stream()
                .filter(PageTableEntry::isValid)
                .filter(entry -> entry.getFrameAddress() != null && entry.getFrameAddress() == frameIndex)
                .findFirst()
                .orElse(null);
    }

    public void handleProcessEvent(Event uncastEvent) {
        if (uncastEvent instanceof org.pampasim.events.ProcessEvent e) {
            Process process = e.getProcess();
            ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);

            // Caso TLB não tenha tradução
            if (e instanceof org.pampasim.events.Memory.TlbNoTranslation tlbEvent) {
                int access = processMemoryInfo.getCurrentAccessList().getFirst();
                virtualAddress.set(Integer.toString(access));
                pageNumber.set(Integer.toString(MemoryConfig.extractPageNumber(access)));
                offset.set(Integer.toString(MemoryConfig.extractOffsetNumber(access)));
                pageTableNumber.set(Integer.toString(MemoryConfig.extractPageNumber(access)));

                PageTableEntry pageTableEntry = processMemoryInfo.getPageTable().getEntry(MemoryConfig.extractPageNumber(access));

                validBit.set(pageTableEntry.isValid() ? "1" : "0");
                Integer frameAddress = pageTableEntry.getFrameAddress();

                if (frameAddress != null) {
                    frameNumber.set(frameAddress.toString());
                    physicalAddress.set(Integer.toString(
                            MemoryConfig.combineToAddress(
                                    frameAddress,
                                    MemoryConfig.extractOffsetNumber(access)
                            )));
                } else {
                    frameNumber.set("Indefinido");
                    physicalAddress.set("Indefinido");
                }
            }

            if (e instanceof org.pampasim.events.Memory.DiskOperation) {
                boolean cpuIdle = observableProcessList.stream()
                        .noneMatch(vm -> vm.getState() == Process.State.RUNNING);

                if (resetInfo && cpuIdle) {
                    virtualAddress.set("");
                    pageNumber.set("");
                    offset.set("");
                    pageTableNumber.set("");
                    validBit.set("");
                    frameNumber.set("");
                    physicalAddress.set("");
                    infoTitle.set("");
                    infoText.set("");
                    infoColor.set(null);
                } else {
                    resetInfo = true;
                }

            }

            if (e instanceof org.pampasim.events.Memory.PageHit) {
                int access = processMemoryInfo.getCurrentAccessList().getFirst();
                infoTitle.set("Page Hit");
                infoText.set(" Processo " + process.getPid() + " acessou o endereço virtual " + access + " que está presente na memória RAM");
                infoColor.set(colorMap.getOrDefault(process.getCreationData().getCreationId(), null));
                resetInfo = false;
            } else if (e instanceof org.pampasim.events.Memory.PageFault) {
                int access = processMemoryInfo.getCurrentAccessList().getFirst();
                infoTitle.set("Page Fault");
                infoText.set(" Processo " + process.getPid() + " acessou o endereço virtual " + access + " que não está presente na memória RAM, e deve ser carregado da memória secundária");
                infoColor.set(colorMap.getOrDefault(process.getCreationData().getCreationId(), null));
                resetInfo = false;
            }

            ProcessMemoryInfo memoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
            if (memoryInfo != null) {
                observableProcessList.stream()
                        .filter(vm -> vm.getPid() != null && vm.getPid().get() != null)
                        .filter(vm -> vm.getPid().get().equals(process.getPid()))
                        .flatMap(vm -> vm.getModuleInfoViewModels().stream())
                        .filter(m -> m instanceof MemoryInfoViewModel)
                        .map(m -> (MemoryInfoViewModel) m)
                        .forEach(vm -> vm.updateFrom(memoryInfo));
            }

            LOGGER.debug("MemoryTabViewModel observed ProcessEvent {}", e);
        }

        List<PageTableEntry> ramFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getMainMemory().getRawFrameAllocationList();

        List<PageTableEntry> swapFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getSwapFile().getRawFrameAllocationList();

        updateFrameList(ramFrameList, observableRamFrameList);
        updateFrameList(swapFrameList, observableSwapFrameList);
    }

    public void setupSnoopers() {
        memoryManagement.getEventManager().addSnooper(org.pampasim.events.ProcessEvent.class, this::handleProcessEvent);
    }

    public void setMemoryManagement(MemoryManagement memoryManagement, Map<Long, Color> colorMap) {
        this.memoryManagement = memoryManagement;
        this.colorMap = colorMap;
        setupSnoopers();
    }

    public void refreshFrameList() {
        List<PageTableEntry> ramFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getMainMemory().getRawFrameAllocationList();

        List<PageTableEntry> swapFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getSwapFile().getRawFrameAllocationList();

        observableRamFrameList.clear();
        observableSwapFrameList.clear();

        createFrameList(ramFrameList, observableRamFrameList);
        createFrameList(swapFrameList, observableSwapFrameList);
    }
}
