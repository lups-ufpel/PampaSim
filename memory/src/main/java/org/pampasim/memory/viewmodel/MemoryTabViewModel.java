package org.pampasim.memory.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.events.Event;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.entity.PhysicalMemory;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MemoryTabViewModel implements ViewModel {
    private static final Logger LOGGER = LogManager.getLogger(MemoryTabViewModel.class);

    private Map<Long, Color> colorMap;
    private MemoryManagement memoryManagement;

    @Getter
    private final SimpleStringProperty virtualAddress = new SimpleStringProperty("");
    @Getter
    private final SimpleStringProperty pageNumber = new SimpleStringProperty("");
    @Getter
    private final SimpleStringProperty offset = new SimpleStringProperty("");
    @Getter
    private final SimpleStringProperty pageTableNumber = new SimpleStringProperty("");
    @Getter
    private final SimpleStringProperty validBit = new SimpleStringProperty("");
    @Getter
    private final SimpleStringProperty frameNumber = new SimpleStringProperty("");
    @Getter
    private final SimpleStringProperty physicalAddress = new SimpleStringProperty("");
    @Getter
    private final SimpleStringProperty infoTitle = new SimpleStringProperty("Test");
    @Getter
    private final SimpleStringProperty infoText = new SimpleStringProperty("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent sit amet lacus gravida, ultricies nunc et, fermentum eros. Nunc eu est facilisis, interdum odio et, suscipit nisl. Aliquam mattis, augue id convallis mollis, metus nibh malesuada ipsum, eu cursus nisi nibh quis erat. Nullam commodo nisi ut suscipit elementum. Morbi dignissim condimentum mi eu fringilla. Nullam eu accumsan felis, nec luctus orci. Sed id nunc diam. Cras quis turpis nec elit finibus interdum ac in orci. In eget eleifend orci. Nulla sollicitudin ac tellus at ornare. Praesent ut augue at est mattis consectetur.");

    @Getter
    private final ObservableList<MemoryFrameViewModel> observableRamFrameList = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<MemoryFrameViewModel> observableSwapFrameList = FXCollections.observableArrayList();

    public MemoryTabViewModel(MemoryManagement memoryManagement, Map<Long, Color> colorMap) {
        this.memoryManagement = memoryManagement;
        this.colorMap = colorMap;
        setupSnoopers();

        List<Process> ramFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getMainMemory().getFrameAllocationList();
        List<Process> swapFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getSwapFile().getFrameAllocationList();

        createFrameList(ramFrameList, observableRamFrameList);
        createFrameList(swapFrameList, observableSwapFrameList);
    }

    private void createFrameList(List<Process> frameList, ObservableList<MemoryFrameViewModel> observableList) {
        IntStream.range(0, frameList.size())
                .forEach(i -> {
                    Process process = frameList.get(i);
                    MemoryFrameViewModel vm = new MemoryFrameViewModel(i);
                    if (process != null) {
                        vm.getColorProperty().set(colorMap.getOrDefault(process.getCreationData().getCreationId(), Color.BLACK));
                        vm.getPid().set(process.getPid());
                    } else {
                        vm.getPid().set(null);
                        vm.getColorProperty().set(null);
                    }
                    observableList.add(vm);
                });
    }

    private void updateFrameList(List<Process> frameList, ObservableList<MemoryFrameViewModel> observableList) {
        for (int i = 0; i < frameList.size(); i++) {
            Process process = frameList.get(i);
            MemoryFrameViewModel vm = observableList.get(i);

            if (process != null) {
                vm.getPid().set(process.getPid());
                vm.getColorProperty().set(colorMap.getOrDefault(process.getCreationData().getCreationId(), null));
            } else {
                vm.getPid().set(null);
                vm.getColorProperty().set(null);
            }
        }
    }

    public void handleProcessEvent(Event uncastEvent) {
        switch (uncastEvent) {
            case org.pampasim.events.Memory.TlbNoTranslation e -> {
                Process process = e.getProcess();
                ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
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
            default -> {

            }
        }

        LOGGER.debug("MemoryTabViewModel observed event {}", uncastEvent);

        List<Process> ramFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getMainMemory().getFrameAllocationList();
        List<Process> swapFrameList = memoryManagement.getEntity(PhysicalMemory.class)
                .getSwapFile().getFrameAllocationList();

        updateFrameList(ramFrameList, observableRamFrameList);
        updateFrameList(swapFrameList, observableSwapFrameList);
    }

    public void setupSnoopers() {
        memoryManagement.getEventManager().addSnooper(org.pampasim.events.ProcessEvent.class,
                this::handleProcessEvent);
    }

    public void setMemoryManagement(MemoryManagement memoryManagement, Map<Long, Color> colorMap) {
        this.memoryManagement = memoryManagement;
        this.colorMap = colorMap;
        setupSnoopers();
    }
}

