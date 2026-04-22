package org.pampasim.memory.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
import org.pampasim.resources.viewmodel.ProcessMemoryInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemoryTabViewModel implements ViewModel {
    private static final Logger LOGGER = LogManager.getLogger(MemoryTabViewModel.class);

    //private IdentityHashMap<Process, Color> colorMap;
    private MemoryManagement memoryManagement;

    @Getter private final SimpleStringProperty virtualAddress = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty pageNumber = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty offset = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty pageTableNumber = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty validBit = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty frameAddress = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty physicalAddress = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty infoTitle = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty infoText = new SimpleStringProperty("");
    @Getter private final SimpleObjectProperty<Color> infoColor = new SimpleObjectProperty<>();
    @Getter private final SimpleStringProperty ioOperationInfoSwapInText = new SimpleStringProperty("");
    @Getter private final SimpleStringProperty ioOperationInfoSwapOutText = new SimpleStringProperty("");
    @Getter private final SimpleObjectProperty<Color> ioOperationInfoSwapInColor = new SimpleObjectProperty<>();
    @Getter private final SimpleObjectProperty<Color> ioOperationInfoSwapOutColor = new SimpleObjectProperty<>();

    @Getter private final ObservableList<MemoryFrameViewModel> observableRamFrameList = FXCollections.observableArrayList();
    @Getter private final ObservableList<MemoryFrameViewModel> observableSwapFrameList = FXCollections.observableArrayList();
    private ObservableMap<Process, ProcessViewModel> observablePvmMap;

    private final MemoryStatisticsViewModel memoryStatisticsViewModel;

    private boolean resetInfo;

    public MemoryTabViewModel(
            MemoryManagement memoryManagement,
            ObservableMap<Process, ProcessViewModel> observablePvmMap,
            MemoryStatisticsViewModel memoryStatisticsViewModel) {

        this.memoryManagement = memoryManagement;
        //this.colorMap = colorMap;
        this.observablePvmMap = observablePvmMap;
        this.memoryStatisticsViewModel = memoryStatisticsViewModel;

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
                ProcessViewModel procVm = observablePvmMap.get(process);
                vm.getColorProperty().set(procVm.getColorProperty().get());
                vm.getPid().set(process.getPid());
                vm.getPageNumber().set(entry.getPageNumber());
                vm.getReferenced().set(entry.isReferenced());
                vm.getDirty().set(entry.isDirty());
            } else {
                vm.getPid().set(null);
                vm.getColorProperty().set(null);
                vm.getPageNumber().set(-1);
                vm.getReferenced().set(false);
                vm.getDirty().set(false);
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
                ProcessViewModel procVm = observablePvmMap.get(process);
                vm.getColorProperty().set(procVm.getColorProperty().get());
                vm.getPid().set(process.getPid());
                vm.getPageNumber().set(entry.getPageNumber());
                vm.getReferenced().set(entry.isReferenced());
                vm.getDirty().set(entry.isDirty());
            } else {
                vm.getPid().set(null);
                vm.getColorProperty().set(null);
                vm.getPageNumber().set(-1);
                vm.getReferenced().set(false);
                vm.getDirty().set(false);
            }
        }
    }

    public void handleProcessEvent(Event uncastEvent) {
        org.pampasim.events.ProcessEvent event = (org.pampasim.events.ProcessEvent) uncastEvent;

        // Early return for allocation events
        if (event instanceof org.pampasim.events.Process.Allocate ||
                event instanceof org.pampasim.events.Memory.Allocate) {
            return;
        }

        Process process = event.getProcess();
        ProcessMemoryInfo memoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        if (memoryInfo == null) {
            throw new IllegalStateException("Process MemoryInfo not found");
        }

        // Update MemoryInfoViewModel for all matching processes
        observablePvmMap
                .get(process)
                .getModuleInfoViewModels()
                .forEach(m -> {
                    if (!(m instanceof MemoryInfoViewModel)) {
                        return;
                    }
                    ((MemoryInfoViewModel) m).updateFrom(memoryInfo);
                });

        LOGGER.debug("MemoryTabViewModel observed ProcessEvent {}", event);

        // Switch-case for event type handling
        switch (event) {
            case org.pampasim.events.Memory.TlbNoTranslation tlbEvent -> handleTlbNoTranslation(memoryInfo, process);
            case org.pampasim.events.Memory.DiskOperation diskOp -> handleDiskOperation(process, memoryInfo);
            case org.pampasim.events.Memory.DiskOperationFinished diskOpFinished -> handleDiskOperationFinished(process, memoryInfo);
            case org.pampasim.events.Memory.PageHit pageHit -> handlePageHit(process, memoryInfo);
            case org.pampasim.events.Memory.PageFault pageFault -> handlePageFault(process, memoryInfo);
            default -> {} // Ignore other events
        }

        updateFrameLists();
        // I sure hope the .values().stream().toList() chain is not expensive
        memoryStatisticsViewModel.updateStatistics(memoryManagement, observablePvmMap.values().stream().toList());
    }

    private void handleTlbNoTranslation(ProcessMemoryInfo memoryInfo, Process process) {
        int access = memoryInfo.getCurrentAccess();
        virtualAddress.set(Integer.toString(access));
        pageNumber.set(Integer.toString(MemoryConfig.extractPageNumber(access)));
        offset.set(Integer.toString(MemoryConfig.extractOffsetNumber(access)));
        pageTableNumber.set(Integer.toString(MemoryConfig.extractPageNumber(access)));

        PageTableEntry pageTableEntry = memoryInfo.getPageTable().getEntry(MemoryConfig.extractPageNumber(access));
        validBit.set(pageTableEntry.isValid() ? "1" : "0");
        Integer frameAddress = pageTableEntry.getFrameAddress();
        Integer frameNumber = pageTableEntry.getFrameNumber();

        if (frameAddress != null) {
            this.frameAddress.set(Integer.toString(frameAddress));
            physicalAddress.set(Integer.toString(
                    MemoryConfig.combineToPhysicalAddress(
                            frameNumber,
                            MemoryConfig.extractOffsetNumber(access),
                            pageTableEntry.isValid()
                    )));
        } else {
            this.frameAddress.set("Indefinido");
            physicalAddress.set("Indefinido");
        }
    }

    private void handleDiskOperation(Process process, ProcessMemoryInfo memoryInfo) {
        boolean cpuIdle = observablePvmMap.values().stream()
                .noneMatch(vm -> vm.getState() == Process.State.RUNNING);

        if (resetInfo && cpuIdle) {
            clearAddressInfo();
        } else {
            resetInfo = true;
        }

        // Update I/O waiting times for queued processes
        updateIoWaitingTimes();
    }

    private void handleDiskOperationFinished(Process process, ProcessMemoryInfo memoryInfo) {
        switch (memoryInfo.getCurrentIoOperation()) {
            case PAGE_FAULT -> handleDiskOperationFinishedPageFault(process, memoryInfo);
            case DISK_ACCESS -> handleDiskOperationFinishedDiskAccess(process, memoryInfo);
        }
    }

    private void handleDiskOperationFinishedPageFault(Process process, ProcessMemoryInfo memoryInfo) {
        PhysicalMemory physicalMemory = memoryManagement.getEntity(PhysicalMemory.class);
        List<PageTableEntry> swappedInPages = physicalMemory.getLastSwappedInPages();
        PageTableEntry swappedOutPage = physicalMemory.getLastSwappedOutPage();

        // Build swap-in message
        StringBuilder swapInMessage = new StringBuilder();
        if (!swappedInPages.isEmpty()) {
            ProcessViewModel procVm = observablePvmMap.get(process);
            Color processColor = procVm.getColorProperty().get();
            swapInMessage.append(" Processo ")
                    .append(process.getPid())
                    .append(swappedInPages.size() == 1 ? " carregou a página " : " carregou as páginas ")
                    .append(swappedInPages.stream()
                            .map(PageTableEntry::getPageNumber)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")))
                    .append(" para a memória RAM");

            ioOperationInfoSwapInColor.set(processColor);
        }

        if (swappedOutPage != null) {
            Process swappedOutProcess = swappedOutPage.getProcess();
            ProcessViewModel swappedOutProcVm = observablePvmMap.get(swappedOutProcess);
            swapInMessage.append(", substituindo a página ").append(swappedOutPage.getPageNumber()).append(" do ");
            Color swappedOutColor = swappedOutProcVm.getColorProperty().get();
            String swapOutMessage = " Processo " +
                    swappedOutProcess.getPid() +
                    (swappedOutPage.isFileBacked() ?
                            " (salva no sistema de arquivos)" :
                            " (salva no endereço " + swappedOutPage.getFrameAddress() + " da swapfile)");
            updateSwapOutPageTable();

            ioOperationInfoSwapOutText.set(swapOutMessage);
            ioOperationInfoSwapOutColor.set(swappedOutColor);
        } else {
            ioOperationInfoSwapOutText.set("");
            ioOperationInfoSwapOutColor.set(null);
        }

        ioOperationInfoSwapInText.set(swapInMessage.toString());
    }

    private void handleDiskOperationFinishedDiskAccess(Process process, ProcessMemoryInfo memoryInfo) {
        ioOperationInfoSwapInText.set(" Processo " + process.getPid() + " Finalizou acesso a disco");
        ProcessViewModel procVm = observablePvmMap.get(process);
        Color processColor = procVm.getColorProperty().get();
        ioOperationInfoSwapInColor.set(processColor);
    }

    private void updateIoWaitingTimes() {
        List<Process> processIoQueue = memoryManagement.getEntity(PhysicalMemory.class)
                .getIoEventQueue()
                .stream()
                .map(ioEvent -> ((org.pampasim.events.ProcessEvent) ioEvent).getProcess())
                .toList();

        for (Process queuedProc : processIoQueue) {
            Optional.ofNullable(observablePvmMap.get(queuedProc))
                    .ifPresent(pvm -> pvm.getModuleInfoViewModel(MemoryInfoViewModel.class)
                            .getIoWaitingTime()
                            .set(queuedProc.getModuleInfo(ProcessMemoryInfo.class).getIoWaitingTime()));
        }
    }

    private void handlePageHit(Process process, ProcessMemoryInfo memoryInfo) {
        int access = memoryInfo.getCurrentAccess();
        ProcessViewModel procVm = observablePvmMap.get(process);
        infoTitle.set("Page Hit");
        infoText.set(" Processo " + process.getPid() + " acessou o endereço virtual " + access + " que está presente na memória RAM");
        infoColor.set(procVm.getColorProperty().get());
        resetInfo = false;
    }

    private void handlePageFault(Process process, ProcessMemoryInfo memoryInfo) {
        int access = memoryInfo.getCurrentAccess();
        ProcessViewModel procVm = observablePvmMap.get(process);
        infoTitle.set("Page Fault");
        infoText.set(" Processo " + process.getPid() + " acessou o endereço virtual " + access + " que não está presente na memória RAM, e deve ser carregado da memória secundária");
        infoColor.set(procVm.getColorProperty().get());
        resetInfo = false;
    }

    private void clearAddressInfo() {
        virtualAddress.set("");
        pageNumber.set("");
        offset.set("");
        pageTableNumber.set("");
        validBit.set("");
        frameAddress.set("");
        physicalAddress.set("");
        infoTitle.set("");
        infoText.set("");
        infoColor.set(null);
    }

    private void updateFrameLists() {
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

    public void setMemoryManagement(MemoryManagement memoryManagement, ObservableMap<Process, ProcessViewModel> observablePvmMap) {
        this.memoryManagement = memoryManagement;
        this.observablePvmMap = observablePvmMap;
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

    public void updateSwapOutPageTable() {
        PageTableEntry lastSwappedOutEntry = memoryManagement.getEntity(PhysicalMemory.class).getLastSwappedOutPage();
        var lastSwappedOutEntryProcess = lastSwappedOutEntry.getProcess();
        Optional.ofNullable(observablePvmMap.get(lastSwappedOutEntryProcess))
                .ifPresent(pvm ->
                        pvm.getModuleInfoViewModel(MemoryInfoViewModel.class)
                                .updateFrom(lastSwappedOutEntry.getProcess().getModuleInfo(ProcessMemoryInfo.class)));
    }
}