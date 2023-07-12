package Kernel;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;

public class SchedulerTest {

    public void test() {

        Process[] runningList;

        for (int i = 0; i < 6; i++) {
            // processes.newProcess();
            Invoker.invoke("Kernel", new Message("newProcess"));
        }

        for (int i = 0; i < 3; i++) {
            System.out.println("------------------------------------");
            // scheduler.schedule();
            // scheduler.printLists();

            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));
            Invoker.invoke("Scheduler", new Message("printLists"));

        }

        for (int i = 0; i < 6; i++) {
            Invoker.invoke("Kernel", new Message("newProcess"));
        }
        System.out.println("====================================");

        // runningList = scheduler.schedule();
        // scheduler.printLists();
        runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));
        Invoker.invoke("Scheduler", new Message("printLists"));

        System.out.println("####################################");

        // runningList[0].setState(State.WAITING);
        // System.out.println("Process " + runningList[0].getPid() + " is now waiting");
        // scheduler.printLists();

        Invoker.invoke("Process", new Message("setState", State.WAITING, runningList[0]));
        System.out.println("Process " + runningList[0].getPid() + " is now waiting");
        Invoker.invoke("Scheduler", new Message("printLists"));

        System.out.println("====================================");

        for (int i = 0; i < 4; i++) {
            System.out.println("------------------------------------");

            // scheduler.schedule();
            // scheduler.printLists();

            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));
            Invoker.invoke("Scheduler", new Message("printLists"));
        }

        Process pcb_aux;
        // processes.getPCB(2).setState(State.READY);
        pcb_aux = (Process) Invoker.invoke("Kernel", new Message("getPCB", 2));
        Invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        // processes.getPCB(4).setState(State.READY);
        pcb_aux = (Process) Invoker.invoke("Kernel", new Message("getPCB", 4));
        Invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        // processes.getPCB(1).setState(State.READY);
        pcb_aux = (Process) Invoker.invoke("Kernel", new Message("getPCB", 1));
        Invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        System.out.println("\n\nProcessess 2, 4 and 1 are now ready\n\n");

        for (int i = 0; i < 4; i++) {
            System.out.println("------------------------------------");

            // runningList = scheduler.schedule();
            // scheduler.printLists();

            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));
            Invoker.invoke("Scheduler", new Message("printLists"));

        }

        for (int i = 0; i < runningList.length; i++) {
            Invoker.invoke("Process", new Message("setState", State.TERMINATED, runningList[i]));
        }

        System.out.println("Every running process has been terminated\n\n");
        System.out.println("Process 5 is now ready\n\n");

        // processes.getPCB(5).setState(State.READY);
        pcb_aux = (Process) Invoker.invoke("Kernel", new Message("getPCB", 5));
        Invoker.invoke("Process", new Message("setState", State.READY, pcb_aux));

        for (int i = 0; i < 3; i++) {
            System.out.println("------------------------------------");
            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));
            Invoker.invoke("Scheduler", new Message("printLists"));
        }
    }
}
