package org.pampasim.resources.memory;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.Process;

@Getter
@Setter
public class PageTableEntry {
    // individual entry for the page table of each process
    Process process; // process which the entry is associated with
    private Integer frameAddress; // can be for either main memory or swapfile depending on the valid bit
    private int pageNumber;
    private boolean valid;
    private boolean dirty;
    private boolean referenced; // used for page replacement algorithm and working set

    PageTableEntry(Process process, int pageNumber) {
        this.process = process;
        this.frameAddress = null; // unset means it's neither in the main memory nor swapfile (new process)
        this.pageNumber = pageNumber;
        this.valid = false;
        this.dirty = false;
        this.referenced = false;
    }
}
