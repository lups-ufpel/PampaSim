package org.pampasim.resources.memory;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.ProcessModuleInfo;

@Getter
public class ProcessMemoryInfo extends ProcessModuleInfo {
    private final Integer size; // total number of pages the process occupies
    @Setter
    private Integer virtualAddressStart; // where the start of the virtual address range is


    public ProcessMemoryInfo(Integer size) {
        // The next fields are relevant to the memory module
        this.size = size; //TODO: Make the user able to define how many pages the process occupies
        this.virtualAddressStart = null;
    }
}
