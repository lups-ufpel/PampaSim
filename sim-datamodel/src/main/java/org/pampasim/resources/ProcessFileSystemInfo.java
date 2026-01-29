package org.pampasim.resources;

import lombok.Getter;
import java.util.List;
import org.pampasim.resources.fileops.FileSystemOperation;

public final class ProcessFileSystemInfo
    extends ProcessModuleInfo {

    @Getter private final List<FileSystemOperation> operations;

    public ProcessFileSystemInfo(List<FileSystemOperation> operations) {
        this.operations = operations;
    }

}
