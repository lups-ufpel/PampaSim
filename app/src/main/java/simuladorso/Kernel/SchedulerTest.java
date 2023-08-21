package simuladorso.Kernel;

import simuladorso.Command.MainCommand.Invoker;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;

public class SchedulerTest {
    private final Logger logger = new Logger();
    private final Invoker invoker = new Invoker(logger);

    public void test() throws IllegalMethodCall, IllegalClassCall, OutOfMemoryException {

        Process[] runningList;

        for (int i = 0; i < 6; i++) {
            // processes.newProcess();
            invoker.invoke("Kernel", new Message("newProcess"));
        }

        for (int i = 0; i < 3; i++) {
            System.out.println("------------------------------------");
            // scheduler.schedule();
            // scheduler.printLists();

            runningList = (Process[]) invoker.invoke("Scheduler", new Message("schedule"));
            invoker.invoke("Scheduler", new Message("printLists"));

        }

        for (int i = 0; i < 6; i++) {
            invoker.invoke("Kernel", new Message("newProcess"));
        }
        System.out.println("====================================");

        // runningList = scheduler.schedule();
        // scheduler.printLists();
        runningList = (Process[]) invoker.invoke("Scheduler", new Message("schedule"));
        invoker.invoke("Scheduler", new Message("printLists"));

        System.out.println("####################################");

        // runningList[0].setState(State.WAITING);
        // System.out.println("Process " + runningList[0].getPid() + " is now waiting");
        // scheduler.printLists();

        invoker.invoke("Process", new Message("setState", State.WAITING, runningList[0]));
        System.out.println("Process " + runningList[0].getPid() + " is now waiting");
        invoker.invoke("Scheduler", new Message("printLists"));

        System.out.println("====================================");

        for (int i = 0; i < 4; i++) {
            System.out.println("------------------------------------");

            // scheduler.schedule();
            // scheduler.printLists();

            runningList = (Process[]) invoker.invoke("Scheduler", new Message("schedule"));
            invoker.invoke("Scheduler", new Message("printLists"));
        }

        Process pcb_aux;
        // processes.getPCB(2).setState(State.READY);
        pcb_aux = (Process) invoker.invoke("Kernel", new Message("getPCB", 2));
        invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        // processes.getPCB(4).setState(State.READY);
        pcb_aux = (Process) invoker.invoke("Kernel", new Message("getPCB", 4));
        invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        // processes.getPCB(1).setState(State.READY);
        pcb_aux = (Process) invoker.invoke("Kernel", new Message("getPCB", 1));
        invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        System.out.println("\n\nProcessess 2, 4 and 1 are now ready\n\n");

        for (int i = 0; i < 4; i++) {
            System.out.println("------------------------------------");

            // runningList = scheduler.schedule();
            // scheduler.printLists();

            runningList = (Process[]) invoker.invoke("Scheduler", new Message("schedule"));
            invoker.invoke("Scheduler", new Message("printLists"));
        }

        for (int i = 0; i < runningList.length; i++) {
            invoker.invoke("Process", new Message("setState", State.TERMINATED, runningList[i]));
        }

        System.out.println("Every running process has been terminated\n\n");
        System.out.println("Process 5 is now ready\n\n");

        // processes.getPCB(5).setState(State.READY);
        pcb_aux = (Process) invoker.invoke("Kernel", new Message("getPCB", 5));
        invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        for (int i = 0; i < 3; i++) {
            System.out.println("------------------------------------");
            runningList = (Process[]) invoker.invoke("Scheduler", new Message("schedule"));
            invoker.invoke("Scheduler", new Message("printLists"));
        }
    }
}
