package org.simuladorso.Utils.Statistics;

import com.github.freva.asciitable.AsciiTable;
import org.simuladorso.Mediator.MediatorDefault;

public class ExecutionTable extends TableFormat{


    static String[] headers;
    static String[][] data;
    public static void showSystemComponents(MediatorDefault mediator){
        headers = new String[]{"Simulator Component"};
        LOGGER.info("\n\n{}",AsciiTable.getTable(headers,mediator.getComponentsNames()
        ));
    }
}
