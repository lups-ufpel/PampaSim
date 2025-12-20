package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

public class fileSystemTabView implements FxmlView<fileSystemTabViewModel>, Initializable {

    @InjectViewModel
    private fileSystemTabViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}
}
