package org.simuladorso.Utils.Statistics;

import com.github.freva.asciitable.AsciiTable;
import org.simuladorso.Mediator.MediatorDefault;

public class ExecutionTable extends TableFormat{


    static String[] headers;
    static String[][] data;


    public static void showSimulationResults(){

        headers = new String[]{"", "Name", "Diameter", "Mass", "Atmosphere"};
        data = new String[][]{
                {"1", "Mercury", "0.382", "0.06", "minimal"},
                {"2", "Venus", "0.949", "0.82", "Carbon dioxide, Nitrogen"},
                {"3", "Earth", "1.000", "1.00", "Nitrogen, Oxygen, Argon"},
                {"4", "Mars", "0.532", "0.11", "Carbon dioxide, Nitrogen, Argon"}};
        LOGGER.info("\n\n{}",AsciiTable.getTable(headers,data));
    }
    public static void showSystemComponents(MediatorDefault mediator){
        headers = new String[]{"Simulator Component"};
        LOGGER.info("\n\n{}",AsciiTable.getTable(headers,mediator.getComponentsNames()
        ));
    }


}
