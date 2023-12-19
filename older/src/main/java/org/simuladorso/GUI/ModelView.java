package org.simuladorso.GUI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.control.Label;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.MediatorDefault;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.Process;
import org.simuladorso.Os.Scheduler;
import org.simuladorso.Os.SchedulerFCFS;
import org.simuladorso.Utils.Statistics.ExecutionTable;
import org.simuladorso.VirtualMachine.Vm;
import org.simuladorso.VirtualMachine.VmSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelView {

    public Button BtnCreateProcess;
    public Button BtnStartSimulation;
    public Button BtnStopSimulation;
    public Button BtnPlusSign;
    public Button BtnLessSign;
    public MenuItem exitMenuItem;
    public HBox RunningProcesses;
    public HBox ReadyProcesses;
    public HBox CreatedProcesses;

    Timeline executeAnimation;


    /*
    * =====================================================================
    * =====================================================================
    * =====================================================================
    * EU MOVI O QUE TINHA NA CLASSE MAIN PARA ESSA CLASSE
    * =====================================================================
    * =====================================================================
    * =====================================================================
    */
    static final Logger LOGGER = LoggerFactory.getLogger("Main");
    static final Process.Type PROCESS_TYPE = Process.Type.SIMPLE;
    static final int CORES = 1;

    static Mediator mediator = new MediatorDefault();
    static Os kernel;
    static Scheduler scheduler;
    static Vm vm;

    /*
     * =====================================================================
     * =====================================================================
     * =====================================================================
     * =====================================================================
     * =====================================================================
     */

    public ModelView(){
        kernel = new Os(mediator);
        scheduler = new SchedulerFCFS(CORES, mediator);
        vm = new VmSimple(CORES, mediator);
        mediator.registerComponent(Mediator.Component.OS, kernel);
        mediator.registerComponent(Mediator.Component.SCHEDULER, scheduler);
        mediator.registerComponent(Mediator.Component.VM, vm);
        mediator.subscribe(Mediator.Action.STOP_VM,this,this::StopVM);
        mediator.subscribe(Mediator.Action.CORE_EXECUTE,this,this::schedule);
        mediator.subscribe(Mediator.Action.SCHEDULER_ADD_TO_QUEUE,this,this::CreatedToReady);
    }
    @FXML
    private void Stub() {
        //TO-DO
        System.out.println("i'm a stub method");
    }
    @FXML
    private void Stop() {
        executeAnimation.pause();
        System.out.println("Simulation Paused");
    }
    private void StopVM(Mediator.Action action){
        Stop();
    }
    @FXML
    private void CreateProcess() {
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{PROCESS_TYPE, 5, 2, 0});
        Color col = Color.color(Math.random(),Math.random(),Math.random());
        Circle c = new Circle(18, col);
        CreatedProcesses.getChildren().add(c);
    }
    @FXML
    private void StartSimulation(){
        RunSimulation.start();
    }
    private final Service<Void> RunSimulation = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    executeAnimation = new Timeline(new KeyFrame(Duration.millis(500),
                            e -> {
                                vm.run();
                                //timer.oneSecondPassed();
//                              //TimerLabel.setText(timer.toString());
                            }
                    ));

                    executeAnimation.setCycleCount(Animation.INDEFINITE); // loop forever
                    executeAnimation.play();
                    return null;
                };
            };
        }
    };
    private void schedule(Mediator.Action action){
        if(RunningProcesses.getChildren().isEmpty()){
            RunningProcesses.getChildren().add((ReadyProcesses.getChildren().remove(0)));
        }
        else{
            ReadyProcesses.getChildren().add(RunningProcesses.getChildren().remove(0));
            RunningProcesses.getChildren().add(ReadyProcesses.getChildren().remove(0));
        }
    }
    public void CreatedToReady(Mediator.Action action){
        if(CreatedProcesses.getChildren().isEmpty()){
            System.out.println("a lista de processos criada esta vazia e foi tentado acesso");
        }
        ReadyProcesses.getChildren().add(CreatedProcesses.getChildren().remove(0));
    }
}
