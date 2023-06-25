package Processor;
import java.util.ArrayList;

import Processor.Memory.Memory;
public class CPU {


    private Registers registers;
    private Memory memory;
    private ArrayList<Instruction> instructions;
    public CPU(Memory mem){
        
        registers = new Registers();
        memory = mem;
        instructions = new ArrayList<>();
        instructions.add(new ADD("ADD", 3, false, 2));
        instructions.add(new HALT("HALT", 1, true, 0));
        instructions.add(new NOP("NOP", 1, false, 0));
    }

    // Fetches the next instruction from main memory
    private String fetch(){

        int pcVal = registers.getProgramCounter();
        String word = memory.readWord(pcVal++);
        registers.setProgramCounter(pcVal);
        return word;
    }
    private Instruction decode(String mnemonic){
        Instruction instr;
        int regIndex;
        String operand;
        instr = FindInstruction(mnemonic);
        if(instr instanceof ArithmeticInstr){
                ArithmeticInstr amInstr = (ArithmeticInstr) instr;
                operand = fetch();
                operand = operand.replaceAll("\\D+", "");
                regIndex = Integer.parseInt(operand);
                amInstr.setFirstOperand(registers.getGeneralPurposeReg(regIndex));
                operand = fetch();
                operand = operand.replaceAll("\\D+", "");
                regIndex = Integer.parseInt(operand);
                amInstr.setSecondOperand(registers.getGeneralPurposeReg(regIndex));
        }
        else if(instr instanceof HALT){
            HALT halt = (HALT) instr;
            halt.setOperand(registers.getInterruptionRegister());
        }
        return instr;
        
    }
    private Instruction FindInstruction(String mnemonic){
        for(int i=0; i < instructions.size(); i++){
                if(instructions.get(i).getInstructionName() == mnemonic){
                    return instructions.get(i);
                }
            }
        throw new IllegalArgumentException("Incorrect mnemonic: " + mnemonic);
    }
    public void startRegisterFive(int value) throws Exception{
        registers.setGeneralPurposeReg(5, value);
    } 
    public void run(){

        while (true) {
            String mnemonic = fetch();
            Instruction instruction = decode(mnemonic);
            instruction.execute();
            if(registers.getInterruption() == -1){
                break;
            }
            // put a excpetion handling here to deal with halt cpu
        }
    }

}