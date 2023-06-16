package Processor;

public final class Register {

    private int value;
    
    public Register(){

        this.value = 0; 
    }
    public int getValue(){
        return this.value;
    }
    public String getValueAsString(){
        
        return Integer.toString(this.value);
    }
    public void setValue(int value) {
        this.value = value;
    }
}
