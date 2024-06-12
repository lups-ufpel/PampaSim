package org.pampasim.Utils.Statistics;

import org.pampasim.Mediator.MediatorDefault;

public class ExecutionTable extends TableFormat{


    static String[] headers;
    static String[][] data;
    public static void showSystemComponents(MediatorDefault mediator){
        headers = new String[]{"Simulator Component"};
    }
}
