package Processor;

public final class Register {

    private static final int W_SIZE = 16;
    private int value;
    
    public Register(){

        value = 0; 
    }
    public int getValue(){
        return value;
    }
    public String getValueAsString(){
        
        return Integer.toString(value);
    }
    public void setValue(int value) throws Exception {
        if(value <= Math.pow(2, W_SIZE)){
            this.value = value;
        }
        else{
            throw new Exception(
                        "Error: Register value overflow, current size is 65536 and value is" 
                        + Integer.toString(value));
        }
    }
}
