package simuladorso.Command.ClassCommanders;

import java.util.ArrayList;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;

import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Command;
import simuladorso.MessageBroker.Message;
import simuladorso.Kernel.Process;
import simuladorso.Kernel.State;
import simuladorso.VirtualMachine.Sbyte;
import simuladorso.VirtualMachine.Processor.Registers;

public class ProcessCommand implements Command {
    private Process process;

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

    public ProcessCommand(Process process) {
        this.process = process;
    }

    public Object execute(Message msg) throws IllegalMethodCall, IllegalClassCall {
        // verifyParams(msg);
        // Switch case to call the correct method from the process
        switch (msg.getAction()) {
            case "getPid":
                return process.getPid();
            case "setPid":
                process.setPid((int) msg.getParameters());
                break;
            case "getParentPid":
                return process.getParentPid();
            case "setParentPid":
                process.setParentPid((int) msg.getParameters());
                break;
            case "getPriority":
                return process.getPriority();
            case "setPriority":
                process.setPriority((int) msg.getParameters());
                break;
            case "getCpuPercentage":
                return process.getCpuPercentage();
            case "setCpuPercentage":
                process.setCpuPercentage((int) msg.getParameters());
                break;
            case "getArrivalTime":
                return process.getArrivalTime();
            case "setArrivalTime":
                process.setArrivalTime((int) msg.getParameters());
                break;
            case "getState":
                return process.getState();
            case "setState":
                process.setState((State) msg.getParameters());
                break;
            case "getRegisters":
                return process.getRegisters();
            case "setRegisters":
                process.setRegisters((Registers) msg.getParameters());
                break;
            case "getStackSize":
                return process.getStackSize();
            case "setStackSize":
                process.setStackSize((int) msg.getParameters());
                break;
            case "getMemory":
                return process.getMemory();
            case "setMemory":
                process.setMemory((ArrayList<Sbyte>) msg.getParameters());
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
