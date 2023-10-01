package Command.ClassCommanders;

import java.util.ArrayList;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;

import Command.Errors.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import Kernel.ProcessLuan;
import Kernel.State;
import VirtualMachine.Sbyte;
import VirtualMachine.Processor.Registers;

public class ProcessCommand implements Command {
    private ProcessLuan process;

    // static map that maps the param type to the method that should be called
    // private static Map<String, Object> methodMap = new HashMap<String, Object>()
    // {{
    // put("setPid", Integer.class);
    // put("setParentPid", Integer.class);
    // put("setPriority", Integer.class);
    // put("setCpuPercentage", Integer.class);
    // put("setArrivalTime", Integer.class);
    // put("setState", State.class);
    // put("setRegisters", Registers.class);
    // put("setStackPointer", Integer.class);
    // put("setStackSize", Integer.class);
    // put("setMem", ArrayList.class);
    // put("setDiskAddress", Integer.class);

    // }};

    // private void verifyParams(Message msg){
    // // get the method name
    // String methodName = msg.getMethodName();

    // // get the param type
    // Class<?> paramType = (Class<?>) methodMap.get(methodName);

    // if (paramType == null) {
    // return;
    // }

    // // get the param
    // Object param = msg.getParam();

    // // if the param is not of the correct type, throw an exception
    // if (!paramType.isInstance(param)) {
    // throw new IllegalMethodCall("Process", msg);
    // }
    // }

    public ProcessCommand(ProcessLuan process) {
        this.process = process;
    }

    public Object execute(Message msg) {
        // verifyParams(msg);
        // Switch case to call the correct method from the process
        switch (msg.getMethodName()) {
            case "getPid":
                return process.getPid();
            case "setPid":
                process.setPid((int) msg.getParam());
                break;
            case "getParentPid":
                return process.getParentPid();
            case "setParentPid":
                process.setParentPid((int) msg.getParam());
                break;
            case "getPriority":
                return process.getPriority();
            case "setPriority":
                process.setPriority((int) msg.getParam());
                break;
            case "getCpuPercentage":
                return process.getCpuPercentage();
            case "setCpuPercentage":
                process.setCpuPercentage((int) msg.getParam());
                break;
            case "getArrivalTime":
                return process.getArrivalTime();
            case "setArrivalTime":
                process.setArrivalTime((int) msg.getParam());
                break;
            case "getState":
                return process.getState();
            case "setState":
                process.setState((State) msg.getParam());
                break;
            case "getRegisters":
                return process.getRegisters();
            case "setRegisters":
                process.setRegisters((Registers) msg.getParam());
                break;
            case "getStackSize":
                return process.getStackSize();
            case "setStackSize":
                process.setStackSize((int) msg.getParam());
                break;
            case "getMemory":
                return process.getMemory();
            case "setMemory":
                process.setMemory((ArrayList<Sbyte>) msg.getParam());
                break;
            case "getInterruption":
                return process.getInterruption();
            case "hasInterruption":
                return process.hasInterruption();
            default:
                throw new IllegalMethodCall("Process", msg);
        }
        return null;
    }
}
