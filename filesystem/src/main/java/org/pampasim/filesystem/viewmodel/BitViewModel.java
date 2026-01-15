package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;

@Getter
public class BitViewModel implements ViewModel {
    private final int number;
    @Getter private final SimpleStringProperty circleLabel = new SimpleStringProperty("");
    private final BooleanProperty bitProperty = new SimpleBooleanProperty(false);

    // labels are for development purposes
    public BitViewModel(int number, boolean bit) {
        this.number = number;
        this.bitProperty.setValue(bit);
    }

    public Boolean getBit(){
      return bitProperty.get();
    }

    public void setBit(Boolean bit){
      this.bitProperty.set(bit);
    }

    public BooleanProperty bitProperty(){
      return bitProperty;
    }

}
