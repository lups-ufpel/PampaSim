package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

import org.pampasim.filesystem.viewmodel.FileSystemTabViewModel;

public class FileSystemTabView implements FxmlView<FileSystemTabViewModel> {

    @InjectViewModel
    private FileSystemTabViewModel viewModel;

}
