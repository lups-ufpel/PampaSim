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
    public String getValueAString(){
        
        return Integer.toString(value);
    }
    public void setValue(int value){
        if(value <= Math.pow(2, W_SIZE)){
            this.value = value;
        }
        else{
            throw new IllegalArgumentException(
                "Error: Register value overflow, current size is 65536 and value is" 
                        + Integer.toString(value)
            );       
        }
    }
    // throws NumberFormatExcpetion
    // throws IllegalArgumentException
    public void setValue(String val_as_string){
        int value = Integer.parseInt(val_as_string);
        
        if(value <= Math.pow(2, W_SIZE)){
            this.value = value;
        }
        else{
            throw new IllegalArgumentException(
                "Error: Register value overflow, current size is 65536 and value is " 
                        + Integer.toString(value)
            );       
        }
    }
}
