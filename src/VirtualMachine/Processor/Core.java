package VirtualMachine.Processor;

import java.util.ArrayList;
import java.util.HashMap;

import Kernel.Process;
import VirtualMachine.Sbyte;

public class Core {
    ArrayList<Sbyte> memory;

    // HashMap containing the binary instructions and their respective opcode
    private static HashMap<String, String> instructions;
    Registers registers;
    String instruction;

    public void execute(Process process) {

        if (process == null) {
        System.out.println("No process to be executed");
        return;
        }
        System.out.println("Executing process " + process.getPid() + ", state: " +
        process.getState());

        // int pc = process.getPc();
        // memory = process.getMemory();
        // registers = process.getRegisters();

        // // Get 4 bytes from memory and concatenate them to form the instruction
        // for (int i = 0; i < 4; i++) {
        //     instruction += memory.get(pc).getValue();
        //     process.incrementPc();
        // }

        // String opcode = instruction.substring(0, 6);

        // switch (instructions.get(opcode)) {
        //     case "NOP": // nop
        //         break;
        //     case "HALT": // halt
        //         break;
        //     case "ADD": // add $1, $2, $3 ($1 = $2 + $3)

        //         break;
        //     case "SUB": // sub $1, $2, $3 ($1 = $2 - $3)

        //         break;
        //     case "ADDI": // addi $1, $2, 10 ($1 = $2 + 10) (10 is the immediate value)

        //         break;
        //     case "ADDU": // addu $1, $2, $3 ($1 = $2 + $3) (unsigned)

        //         break;
        //     case "SUBU": // subu $1, $2, $3 ($1 = $2 - $3) (unsigned)

        //         break;
        //     case "ADDIU": // addiu $1, $2, 10 ($1 = $2 + 10) (unsigned) (10 is the immediate value)

        //         break;
        //     case "MUL": // mul $1, $2, $3 ($1 = $2 * $3) result is 32 bits, no overflow

        //         break;
        //     case "MULT": // mult $2, $3 ($hi,$low = $1 * $2) result is 64 bits (32 -> hi, 32 -> low),
        //                  // overflow

        //         break;
        //     case "DIV": // div $2, $3 ($hi = $1 % $2, $low = $1 / $2) result is 64 bits (32 -> hi, 32 ->
        //         // low)

        //         break;
        //     case "AND": // and $1, $2, $3 ($1 = $2 & $3)

        //         break;
        //     case "OR": // or $1, $2, $3 ($1 = $2 | $3)

        //         break;
        //     case "ANDI": // andi $1, $2, 10 ($1 = $2 & 10) (10 is the immediate value)

        //         break;
        //     case "ORI": // ori $1, $2, 10 ($1 = $2 | 10) (10 is the immediate value)

        //         break;
        //     case "SLL": // sll $1, $2, 10 ($1 = $2 << 10) (10 is the immediate value)

        //         break;
        //     case "SRL": // srl $1, $2, 10 ($1 = $2 >> 10) (10 is the immediate value)

        //         break;
        //     case "LW": // lw $1, 10($2) ($1 = memory[$2 + 10])

        //         break;
        //     case "SW": // sw $1, 10($2) (memory[$2 + 10] = $1)

        //         break;
        //     case "BEQ": // beq $1, $2, 10 (if $1 == $2) pc = pc + 10 (10 is the immediate value)

        //         break;
        //     case "BNE": // bne $1, $2, 10 (if $1 != $2) pc = pc + 10 (10 is the immediate value)

        //         break;
        //     case "J": // j 10 (pc = 10) (10 is the immediate value)

        //         break;
        //     case "JR": // jr $1 (pc = $1)

        //         break;
        //     case "JAL": // jal 10 ($31 = pc + 4, pc = 10) (10 is the immediate value)

        //         break;
        //     case "JALR": // jalr $1 ($31 = pc + 4, pc = $1)

        //         break;
        //     case "SLT": // slt $1, $2, $3 ($1 = $2 < $3) (signed)

        //         break;
        //     case "SLTI": // slti $1, $2, 10 ($1 = $2 < 10) (signed) (10 is the immediate value)

        //         break;
        //     case "SLTU": // sltu $1, $2, $3 ($1 = $2 < $3) (unsigned)

        //         break;
        //     case "SLTIU": // sltiu $1, $2, 10 ($1 = $2 < 10) (unsigned) (10 is the immediate value)

        //         break;
        //     case "LUI": // lui $1, 10 ($1 = 10 << 16) (10 is the immediate value)

        //         break;
        //     case "MFHI": // mfhi $1 ($1 = $hi)

        //         break;
        //     case "MFLO": // mflo $1 ($1 = $low)

        //         break;
        //     case "SYSCALL": // syscall

        //         break;
        //     case "BLTZ": // bltz $1, 10 (if $1 < 0) pc = pc + 10 (10 is the immediate value)

        //         break;
        //     case "BGTZ": // bgtz $1, 10 (if $1 > 0) pc = pc + 10 (10 is the immediate value)

        //         break;
        //     case "BLEZ": // blez $1, 10 (if $1 <= 0) pc = pc + 10 (10 is the immediate value)

        //         break;
        //     case "BGEZ": // bgez $1, 10 (if $1 >= 0) pc = pc + 10 (10 is the immediate value)

        //         break;
        //     default:
        //         break;
        // }

        
    }

    // returns 0 if R-format, 1 if I-format, 2 if J-format
    // private int getInstructionFormat() {

    // }

    public Core() {
        instruction = "";

        // Initialize the instructions map
        // R-format will have the first bit set to 0
        // I-format will have the first bit set to 1
        // J-format will have the first tree bits set to 1
        instructions = new HashMap<String, String>();
        instructions.put("000000", "NOP");
        instructions.put("111111", "HALT");
        instructions.put("000001", "ADD"); // R-format
        instructions.put("000010", "SUB"); // R-format
        instructions.put("100011", "ADDI"); // I-format
        instructions.put("000100", "ADDU"); // R-format
        instructions.put("000101", "SUBU"); // R-format
        instructions.put("100001", "ADDIU"); // I-format
        instructions.put("000110", "MUL"); // R-format
        instructions.put("000111", "MULT"); // R-format
        instructions.put("001000", "DIV"); // R-format
        instructions.put("001001", "AND"); // R-format
        instructions.put("001010", "OR"); // R-format
        instructions.put("101011", "ANDI"); // I-format
        instructions.put("101001", "ORI"); // I-format
        instructions.put("001011", "SLL"); // R-format
        instructions.put("001100", "SRL"); // R-format
        instructions.put("100000", "LW"); // I-format
        instructions.put("101000", "SW"); // I-format
        instructions.put("101101", "BEQ"); // I-format
        instructions.put("101100", "BNE"); // I-format
        instructions.put("111000", "J"); // J-format
        instructions.put("001101", "JR"); // R-format
        instructions.put("111001", "JAL"); // J-format
        instructions.put("001110", "JALR"); // R-format
        instructions.put("001111", "SLT"); // R-format
        instructions.put("100010", "SLTI"); // I-format
        instructions.put("001101", "SLTU"); // R-format
        instructions.put("100011", "SLTIU"); // I-format
        instructions.put("001000", "LUI"); // I-format
        
    }
}
