package Processor.Memory;
import java.util.Arrays;
public class Memory {
    
    private final int capacity = 65536;
    
    private String[] memory;

    public Memory(){
        
        memory = new String[capacity];
    }

    public String readWord(int address){

        if(address > capacity || address < 0){
            throw new IllegalArgumentException("Invalid memory address");
        }
        return memory[address];
    }
    public void writeWord(int address, String content){

        if(address > capacity || address < 0){
            throw new IllegalArgumentException("Invalid memory address");
        }
        memory[address] = content;
    }
    public void writeFrame(int frameNumber, int frameSize, String[] page){
        for(int phys_addr= frameNumber*frameSize, log_addr=0; phys_addr<frameSize*frameNumber+frameSize; phys_addr++, log_addr++){
            memory[phys_addr] = page[log_addr]; 
        }

    }

    public String[] ReadFrame(int frameNumber, int frameSize){

        String[] frame = new String[frameSize];
        frame = Arrays.copyOfRange(memory, frameNumber*frameSize, frameSize*frameNumber+frameSize);
        return frame;
    }

}