package org.pampasim.resources.memory;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.Process;

@Getter
public class PageTableEntry {
    // individual entry for the page table of each process
    Process process; // process which the entry is associated with
    private Integer frameNumber; // can be for either main memory or swapfile depending on the valid bit
    private Integer frameAddress;
    @Setter
    private int pageNumber;
    @Setter
    private boolean valid;
    @Setter
    private boolean dirty;
    @Setter
    private boolean referenced; // used for page replacement algorithm and working set
    @Setter
    private boolean fileBacked; // if true, the page is backed in an executable or another file in disk.
                                // if not, it needs to be stored in the swapfile

    PageTableEntry(Process process, int pageNumber, boolean fileBacked) {
        this.process = process;
        this.frameNumber = null; // unset means it's neither in the main memory nor swapfile (new process)
        this.frameAddress = null;
        this.pageNumber = pageNumber;
        this.valid = false;
        this.dirty = false;
        this.referenced = false;
        this.fileBacked = fileBacked;
    }

    public void setFrameNumber(Integer frameNumber, int frameSize) {
        this.frameNumber = frameNumber;
        if (frameNumber == null) {
            this.frameAddress = null;
        } else {
            frameAddress = frameNumber * frameSize;
        }

    }
}
