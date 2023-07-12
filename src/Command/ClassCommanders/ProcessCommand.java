package Command.ClassCommanders;

import Command.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import Kernel.Process;
import Kernel.State;
import Processor.Registers;

public class ProcessCommand implements Command {

    private Process pcb;
    // private Map<String, Supplier<Object>> methodMap;

    public ProcessCommand(Process pcb) {
        this.pcb = pcb;
    }

    public Object execute(Message msg) {

        // Switch case to call the correct method from the PCB
        switch (msg.getMethodName()) {
            case "getPid":
                return pcb.getPid();
            case "setPid":
                pcb.setPid((int) msg.getParam());
                break;
            case "getParentPid":
                return pcb.getParentPid();
            case "setParentPid":
                pcb.setParentPid((int) msg.getParam());
                break;
            case "getPriority":
                return pcb.getPriority();
            case "setPriority":
                pcb.setPriority((int) msg.getParam());
                break;
            case "getCpuPercentage":
                return pcb.getCpuPercentage();
            case "setCpuPercentage":
                pcb.setCpuPercentage((int) msg.getParam());
                break;
            case "getArrivalTime":
                return pcb.getArrivalTime();
            case "setArrivalTime":
                pcb.setArrivalTime((int) msg.getParam());
                break;
            case "getState":
                return pcb.getState();
            case "setState":
                pcb.setState((State) msg.getParam());
                break;
            case "getRegisters":
                return pcb.getRegisters();
            case "setRegisters":
                pcb.setRegisters((Registers) msg.getParam());
                break;
            case "getStackPointer":
                return pcb.getStackPointer();
            case "setStackPointer":
                pcb.setStackPointer((int) msg.getParam());
                break;
            case "getStackSize":
                return pcb.getStackSize();
            case "setStackSize":
                pcb.setStackSize((int) msg.getParam());
                break;
            case "getDiskAddress":
                return pcb.getDiskAddress();
            case "setDiskAddress":
                pcb.setDiskAddress((int) msg.getParam());
                break;
            case "getInterruption":
                return pcb.getInterruption();
            case "setInterruption":
                pcb.setInterruption((Kernel.Interruption) msg.getParam());
                break;
            case "hasInterruption":
                return pcb.hasInterruption();
            default:
                throw new IllegalMethodCall("Process", msg.getMethodName(), msg);
        }
        return null;

        // initializeMethodMap(msg);

        // Supplier<Object> method = methodMap.get(msg.getCall());
        // if (method != null) {
        // return method.get();
        // } else {
        // handleUnknownMethod(msg.getCall(), msg);
        // return null;
        // }
    }

    // private void initializeMethodMap(Message msg) {
    // methodMap = new HashMap<>();
    // methodMap.put("getPid", pcb::getPid);
    // methodMap.put("setPid", () -> {
    // pcb.setPid((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getParentPid", pcb::getParentPid);
    // methodMap.put("setParentPid", () -> {
    // pcb.setParentPid((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getPriority", pcb::getPriority);
    // methodMap.put("setPriority", () -> {
    // pcb.setPriority((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getCpuPercentage", pcb::getCpuPercentage);
    // methodMap.put("setCpuPercentage", () -> {
    // pcb.setCpuPercentage((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getArrivalTime", pcb::getArrivalTime);
    // methodMap.put("setArrivalTime", () -> {
    // pcb.setArrivalTime((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getState", pcb::getState);
    // methodMap.put("setState", () -> {
    // pcb.setState((State) msg.getParam());
    // return null;
    // });
    // methodMap.put("getRegisters", pcb::getRegisters);
    // methodMap.put("setRegisters", () -> {
    // pcb.setRegisters((Registers) msg.getParam());
    // return null;
    // });
    // methodMap.put("getStackPointer", pcb::getStackPointer);
    // methodMap.put("setStackPointer", () -> {
    // pcb.setStackPointer((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getStackSize", pcb::getStackSize);
    // methodMap.put("setStackSize", () -> {
    // pcb.setStackSize((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getDiskAddress", pcb::getDiskAddress);
    // methodMap.put("setDiskAddress", () -> {
    // pcb.setDiskAddress((int) msg.getParam());
    // return null;
    // });
    // methodMap.put("getInterruption", pcb::getInterruption);
    // methodMap.put("setInterruption", () -> {
    // pcb.setInterruption((ProcessManagement.Interruption) msg.getParam());
    // return null;
    // });
    // methodMap.put("hasInterruption", pcb::hasInterruption);

    // }

}
