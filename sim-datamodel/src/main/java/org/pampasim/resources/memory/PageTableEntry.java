package org.pampasim.resources.memory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageTableEntry {
    // individual entry for the page table of each process
    private Integer frameAddress; // can be for either main memory or swapfile depending on the valid bit
    private boolean valid;
    private boolean dirty;
    private boolean referenced; // used for page replacement algorithms

    PageTableEntry() {
        this.frameAddress = null; // unset means it's neither in the main memory nor swapfile (new process)
        this.valid = false;
        this.dirty = false;
        this.referenced = false;
    }
}
