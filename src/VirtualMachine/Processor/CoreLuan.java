package VirtualMachine.Processor;
import java.util.HashMap;
import Os.Process;

public class CoreLuan extends Core {
    
    @Override
    public void execute(Process process) {
        instruction = "";
        if (process == null) {
            System.out.println("No process to be executed");
            return;
        }
        System.out.println("Executing process " + process.getPid() + ", state: " + process.getState());
        registers = process.getRegisters();
        pc = registers.getReg("PC");
        memory = process.getMemory();
        registers = process.getRegisters();
        // Get 4 bytes from memory and concatenate them to form the instruction
        for (int i = 0; i < 4; i++) {
            instruction += memory.get(pc.getValue()).getStringValue();
            registers.incrementPC();
        }

        opcode = Opcodes.get(instruction.substring(0, 6));

        if (opcode == "RFORMAT") {
            decodeRFormat();
            return;
        } 
        else if (opcode == "J") {
            imm = Integer.parseInt(instruction.substring(6, 32), 2);
            pc.setValue(pc.getValue() + (imm << 2));
            return;
        } 
        else if (opcode == "JAL") {
            imm = Integer.parseInt(instruction.substring(6, 32), 2);
            registers.getReg("RA").setValue(pc.getValue());
            pc.setValue(pc.getValue() + (imm << 2));
            return;
        } 
        else {
            decodeIFormat();
        }
            
    }

        // returns 0 if R-format, 1 if I-format, 2 if J-format
        // private int getInstructionFormat() {

        // }
    @Override
    public void decodeIFormat() {
        // I-format
        rs = registers.getReg(instruction.substring(6, 11));
        rsv = rs.getValue();
        rt = registers.getReg(instruction.substring(11, 16));
        rtv = rt.getValue();

        System.out.println("rs: " + instruction.substring(6, 11) + " rt: " + instruction.substring(11, 16) + " imm: "
                + instruction.substring(16, 32));
        System.out.println("rs value: " + rs.getValue() + " rt value: " + rtv);

        imm = Integer.parseInt(instruction.substring(16, 32), 2);

        int aux;
        String Saux;
        switch (opcode) {
            case "ADDI":
                System.out.println("ADDI: $rt = $rs + imm => " + rtv + " = " + rsv + " + " + imm);
                rt.setValue(rsv + imm);
                break;
            case "ADDIU":
                // Usa o tipo long para realizar a soma sem sinal e reconverte para int
                rt.setValue((int) (((long) rsv + (long) imm) & 0xFFFFFFFFL));
                break;
            case "ANDI":
                rt.setValue(rsv & imm);
                break;
            case "ORI":
                rt.setValue(rsv | imm);
                break;
            case "XORI":
                rt.setValue(rsv ^ imm);
                break;
            case "SLTI":
                rt.setValue(rsv < imm ? 1 : 0);
                break;
            case "SLTIU":
                rt.setValue(rsv < imm ? 1 : 0); // Implementar com unsigned
                break;
            case "LHI":
                rt.setValue((rtv & 0xFFFF) | (imm << 16));
                break;
            case "LLO":
                rt.setValue((rtv & 0xFFFF0000) | imm);
                break;
            case "BEQ":
                if (rsv == rtv) {
                    pc.setValue(pc.getValue() + (imm << 2));
                }
                break;
            case "BNE":
                if (rsv != rtv) {
                    pc.setValue(pc.getValue() + (imm << 2));
                }
                break;
            case "BGTZ":
                if (rsv > 0) {
                    pc.setValue(pc.getValue() + (imm << 2));
                }
                break;
            case "BLEZ":
                if (rsv <= 0) {
                    pc.setValue(pc.getValue() + (imm << 2));
                }
                break;
            case "LB":
                rt.setValue(memory.get(rsv + imm).getValue());// Implementar com signed
                break;
            case "LBU":
                rt.setValue(memory.get(rsv + imm).getValue()); // Implementar com unsigned
                break;
            case "LH": // Implementar com signed
                Saux = memory.get(rsv + imm).getStringValue();
                Saux += memory.get(rsv + imm + 1).getStringValue();

                aux = Integer.parseInt(Saux, 2) << 16;
                rt.setValue(aux);
                break;
            case "LHU": // Implementar com unsigned
                Saux = memory.get(rsv + imm).getStringValue();
                Saux += memory.get(rsv + imm + 1).getStringValue();
                
                aux = Integer.parseInt(Saux, 2) << 16;
                rt.setValue(aux);
                break;
            case "LW": // Implementar com signed
                Saux = memory.get(rsv + imm).getStringValue();
                Saux += memory.get(rsv + imm + 1).getStringValue();
                Saux += memory.get(rsv + imm + 2).getStringValue();
                Saux += memory.get(rsv + imm + 3).getStringValue();

                aux = Integer.parseInt(Saux, 2);
                rt.setValue(aux);
                break;
            default:
                break;
        }
    }

    @Override
    public void decodeRFormat() {
        String function = Funct.get(instruction.substring(26, 32));
        rsv = registers.getReg(instruction.substring(6, 11)).getValue();
        rtv = registers.getReg(instruction.substring(11, 16)).getValue();
        rd = registers.getReg(instruction.substring(16, 21));
        shamt = Integer.parseInt(instruction.substring(21, 26), 2);

        switch (function) {
            // Arithmetic and Logic (ArithLog) instructions
            case "ADD":
                System.out.println("ADD: $rd = $rs + $rt => " + rtv + " = " + rsv + " + " + imm);
                rd.setValue(rsv + rtv);
                System.out.println(rd.getValue());
                break;
            case "ADDU":
                // Usa o tipo long para realizar a soma sem sinal e reconverte para int
                rd.setValue((int) (((long) rsv + (long) rtv) & 0xFFFFFFFFL));
                break;
            case "AND":
                rd.setValue(rsv & rtv);
                break;
            case "NOR":
                rd.setValue(~(rsv | rtv));
                break;
            case "OR":
                rd.setValue(rsv | rtv);
                break;
            case "SUB":
                rd.setValue(rsv - rtv);
                break;
            case "SUBU":
                // Usa o tipo long para realizar a subtração sem sinal e reconverte para int
                rd.setValue((int) (((long) rsv - (long) rtv) & 0xFFFFFFFFL));
                break;
            case "XOR":
                rd.setValue(rsv ^ rtv);
                break;
            case "SLT":
                rd.setValue(rsv < rtv ? 1 : 0);
                break;
            case "SLTU":
                rd.setValue((rsv & 0xFFFFFFFFL) < (rtv & 0xFFFFFFFFL) ? 1 : 0);
                break;
            // DivMult instructions
            case "DIV":
                registers.getReg("HI").setValue(rsv % rtv);
                registers.getReg("LO").setValue(rsv / rtv);
                break;
            case "DIVU":
                registers.getReg("HI").setValue((int) ((rsv & 0xFFFFFFFFL) % (rtv & 0xFFFFFFFFL)));
                registers.getReg("LO").setValue((int) ((rsv & 0xFFFFFFFFL) / (rtv & 0xFFFFFFFFL)));
                break;
            case "MULT":
                registers.getReg("HI").setValue((rsv * rtv));
                registers.getReg("LO").setValue((int) ((rsv * rtv) & 0xFFFFFFFFL));
                break;
            case "MULTU":
                registers.getReg("HI").setValue((int) ((rsv & 0xFFFFFFFFL) * (rtv & 0xFFFFFFFFL)));
                registers.getReg("LO").setValue((int) (((rsv & 0xFFFFFFFFL) * (rtv & 0xFFFFFFFFL)) & 0xFFFFFFFFL));
                break;
            // Shift instructions
            case "SLL":
                rd.setValue(rtv << shamt);
                break;
            case "SRA":
                rd.setValue(rtv >> shamt);
                break;
            case "SRL":
                rd.setValue(rtv >>> shamt);
                break;
            case "SLLV":
                rd.setValue(rtv << rsv);
                break;
            case "SRAV":
                rd.setValue(rtv >> rsv);
                break;
            case "SRLV":
                rd.setValue(rtv >>> rsv);
                break;
            // JumpR instructions
            case "JALR":
                registers.getReg("RA").setValue(pc.getValue());
                pc.setValue(rsv);
                break;
            case "JR":
                pc.setValue(rsv);
                break;
            // MoveFrom instructions
            case "MFHI":
                rd.setValue(registers.getReg("HI").getValue());
                break;
            case "MFLO":
                rd.setValue(registers.getReg("LO").getValue());
                break;
            // MoveTo instructions
            case "MTHI":
                registers.getReg("HI").setValue(rsv);
                break;
            case "MTLO":
                registers.getReg("LO").setValue(rsv);
                break;
            default:
                break;
        }
    }
    
    public CoreLuan() {
        instruction = "";

        Opcodes = new HashMap<String, String>();
        Funct = new HashMap<String, String>();

        // ArithLogI instructions
        Opcodes.put("000000", "RFORMAT"); // R-format

        Opcodes.put("001000", "ADDI"); // $1 = $2 + imm
        Opcodes.put("001001", "ADDIU"); // $1 = $2 + u
        Opcodes.put("001100", "ANDI"); // $1 = $2 & imm
        Opcodes.put("001101", "ORI"); // $1 = $2 | imm
        Opcodes.put("001110", "XORI"); // $1 = $2 xor imm
        Opcodes.put("001010", "SLTI"); // $1 = $2 < imm
        Opcodes.put("001011", "SLTIU"); // $1 = $2 < u

        // LoadI instructions
        Opcodes.put("011001", "LHI"); // $1 = imm & 0xFFFF0000
        Opcodes.put("011000", "LLO"); // $1 = imm & 0x0000FFFF

        // Branch instructions
        Opcodes.put("000100", "BEQ"); // if $1 == $2 pc = pc + imm
        Opcodes.put("000101", "BNE"); // if $1 != $2 pc = pc + imm

        // BranchZ instructions
        Opcodes.put("000111", "BGTZ"); // if $1 > 0 pc = pc + imm
        Opcodes.put("000110", "BLEZ"); // if $1 <= 0 pc = pc + imm

        // LoadStore instructions -> Load
        Opcodes.put("100000", "LB"); // $1 = memory[$2 + imm] | Load Byte
        Opcodes.put("100100", "LBU"); // $1 = memory[$2 + u] | Load Byte Unsigned
        Opcodes.put("100001", "LH"); // $1 = memory[$2 + imm] | Load Halfword
        Opcodes.put("100101", "LHU"); // $1 = memory[$2 + u] | Load Halfword Unsigned
        Opcodes.put("100011", "LW"); // $1 = memory[$2 + imm] | Load Word
        // LoadStore instructions -> Store
        Opcodes.put("101000", "SB"); // memory[$2 + imm] = $1 | Store Byte
        Opcodes.put("101001", "SH"); // memory[$2 + imm] = $1 | Store Halfword
        Opcodes.put("101011", "SW"); // memory[$2 + imm] = $1 | Store Word

        // Jump instructions
        Opcodes.put("000010", "J"); // pc = imm | Jump
        Opcodes.put("000011", "JAL"); // $ra = pc + 4, pc = imm | Jump and Link

        Opcodes.put("011010", "SYSCALL"); // System Call
        /**
         * syscall codes:
         * $v0 = 1 -> Escreve inteiro na saída padrão -> print($a0)
         * $v0 = 2 -> Escreve float na saída padrão -> print($f12)
         * $v0 = 3 -> Escreve double na saída padrão -> print($f12)
         * $v0 = 4 -> Escreve string na saída padrão -> print(@a0)
         * $v0 = 5 -> Lê inteiro da entrada padrão -> $v0 = read()
         * $v0 = 6 -> Lê float da entrada padrão -> $f0 = read()
         * $v0 = 7 -> Lê double da entrada padrão -> $f0 = read()
         * 
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * || $v0 = 8 -> Lê string da entrada padrão ||
         * || $a0 = writeAddr, $a1 = maxSize ||
         * || Adiciona uma quebra de linha no final e um \0 em seguida ||
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * 
         * $v0 = 9 -> Aloca memória -> $v0 = malloc($a0)
         * $v0 = 10 -> Termina o programa -> exit()
         * $v0 = 11 -> Escreve char na saída padrão -> print($a0) |ASCII|
         * $v0 = 12 -> Lê char da entrada padrão -> $v0 = read() |ASCII|
         * 
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * || $v0 = 13 -> Open file ||
         * || Objetivo: abrir um arquivo e obter o seu fileDescriptor ||
         * || $v0 = open($a0, $a1) -> $v0 = fileDescriptor |stdin| ||
         * || $a0 = fileName, $a1 = flags ||
         * || flags: ||
         * || O_RDONLY -> 00000000 ||
         * || O_WRONLY -> 00000001 ||
         * || O_RDWR -> 00000010 ||
         * || O_CREAT -> 00000100 ||
         * || O_TRUNC -> 00001000 Abre o arquivo e o zera ||
         * || O_APPEND -> 00010000 Abre o arquivo e escreve no final ||
         * || O_EXCL -> 00100000 Se o arquivo já existe, retorna erro ||
         * || ||
         * || flags podem ser combinadas entre si ||
         * || Um fileDescriptor é um inteiro que representa um arquivo aberto ||
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * 
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * || $v0 = 14 -> read from file ||
         * || $v0 = read($a0, $a1, $a2) -> $v0 = bytesRead |quantia| ||
         * || $a0 = fileDescriptor, $a1 = addrBuffer, $a2 = maxSize ||
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * 
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * || $v0 = 15 -> write to file ||
         * || $v0 = write($a0, $a1, $a2) -> $v0 = bytesWritten |quantia| ||
         * || $a0 = fileDescriptor, $a1 = addrBuffer, $a2 = maxSize ||
         * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
         * 
         * $v0 = 16 -> Close file -> close($a0)
         * 
         */

        //////////////////////////////// | Funct |\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        /////////////// Todas instruções do tipo R tem opcode 000000 \\\\\\\\\\\\\\

        // ArithLog instructions
        Funct.put("100000", "ADD"); // $1 = $2 + $3
        Funct.put("100001", "ADDU"); // $1 = $2 + $3 (unsigned)
        Funct.put("100100", "AND"); // $1 = $2 & $3
        Funct.put("100111", "NOR"); // $1 = ~($2 | $3)
        Funct.put("100101", "OR"); // $1 = $2 | $3
        Funct.put("100010", "SUB"); // $1 = $2 - $3
        Funct.put("100011", "SUBU"); // $1 = $2 - $3 (unsigned)
        Funct.put("100110", "XOR"); // $1 = $2 xor $3
        Funct.put("101010", "SLT"); // $1 = $2 < $3
        Funct.put("101001", "SLTU"); // $1 = $2 < $3 (unsigned)

        // DivMult instructions
        Funct.put("011010", "DIV"); // $lo = $2 / $3, $hi = $2 % $3
        Funct.put("011011", "DIVU"); // $lo = $2 / $3, $hi = $2 % $3 (unsigned)
        Funct.put("011000", "MULT"); // $lo = $2 * $3
        Funct.put("011001", "MULTU"); // $lo = $2 * $3 (unsigned)

        // Shift instructions
        Funct.put("000000", "SLL"); // $1 = $2 << imm
        Funct.put("000011", "SRA"); // $1 = $2 >> imm (arithmetic)
        Funct.put("000010", "SRL"); // $1 = $2 >> imm (logical)

        // ShiftV instructions
        Funct.put("000100", "SLLV"); // $1 = $2 << $3
        Funct.put("000111", "SRAV"); // $1 = $2 >> $3 (arithmetic)
        Funct.put("000110", "SRLV"); // $1 = $2 >> $3 (logical)

        // JumpR instructions
        Funct.put("001001", "JALR"); // $ra = pc + 4, pc = $1
        Funct.put("001000", "JR"); // pc = $1

        // MoveFrom instructions
        Funct.put("010000", "MFHI"); // $1 = $hi
        Funct.put("010010", "MFLO"); // $1 = $lo

        // MoveTo instructions
        Funct.put("010001", "MTHI"); // $hi = $1
        Funct.put("010011", "MTLO"); // $lo = $1

    }
}
