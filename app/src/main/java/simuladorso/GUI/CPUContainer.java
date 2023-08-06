package simuladorso.GUI;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class CPUContainer extends Pane {
    private int cpuId;

    private Label cpuIdLbl;

    public CPUContainer(int cpuId) {
        super();
        this.cpuId = cpuId;

    }
}