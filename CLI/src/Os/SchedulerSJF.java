// package Os;

// import java.util.List;

// import Mediator.Mediator;

// public class SchedulerSJF extends Scheduler{

//     public SchedulerSJF(List<Process> newList, List<Process> readyList, List<Process> waitingList,
//             List<Process> terminatedList, int numCores, Mediator mediator) {
//         super(newList, readyList, waitingList, terminatedList, numCores, mediator);
//     }

//     @Override
//     protected void readyToRunning(int coreID){
//         if (readyList.isEmpty()) {

//             // if the running process is not terminated, it will continue to run
//             if (runningList[coreID].getState() == Process.State.TERMINATED) {
//                 terminatedList.add(runningList[coreID]);
//                 runningList[coreID] = null;
//             }

//         } else {
//             if (runningList[coreID] != null) {
//                 runningList[coreID].setState(Process.State.READY);
//                 //Invoker.invoke("Process", new Message("setState", Process.State.READY, runningList[coreID]));
//                 readyList.add(runningList[coreID]);
//             }

//             //ESCOLHE O PROCESSO COM MENOR NUMERO DE INSTRUCOES
//             //lógica para escolher o processo com menor numero de instruções
//             runningList[coreID] = readyList.get(0);
//             for(Process process : readyList){
//                 if(process.getburstTime() < runningList[coreID].getburstTime()){
//                     runningList[coreID] = process;
//                 }
//             }
//             readyList.remove(runningList[coreID]);
//             runningList[coreID].setState(Process.State.RUNNING);
//         }
//         clockCycles[coreID] = 0;
//     }
//     @Override
//     public Process[] schedule() {
//         moveNewProcessesToReadyList();
//         moveWaitingProcessesToReadyList();
//         //modify the following code to implement the priority scheduler
//         for (int coreId = 0; coreId < runningList.burstTime; coreId++) {
//             Process currentProcess = runningList[coreId];
        
//             if (currentProcess != null) {
//                 switch (currentProcess.getState()) {
//                     case TERMINATED:
//                         terminatedList.add(currentProcess);
//                         runningList[coreId] = null;
//                         break;
//                     case WAITING:
//                         waitingList.add(currentProcess);
//                         runningList[coreId] = null;
//                         break;
//                     case READY:
//                         readyList.add(currentProcess);
//                         runningList[coreId] = null;
//                         break;
//                     case RUNNING:
//                         if(!readyList.isEmpty()) readyToRunning(coreId);
//                         break;
//                     default:
//                         throw new RuntimeException(currentProcess.getState() + ": Process in an invalid state to schedule");
//                 }
//             }
//             else{
//                 if(!readyList.isEmpty()) readyToRunning(coreId);
//             }
//         }
//         return runningList;
//     }
// }
