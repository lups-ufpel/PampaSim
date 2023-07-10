package Command.MainCommand;

public class Message{
    private Object sender;
    private String call;
    private Object param;
    private Object receiver;

    
    public Message(String call){
        this.call = call;
        this.sender = null;
        this.param = null;
        this.receiver = null;
    }
    public Message(String call, Object param){
        this.call = call;
        this.param = param;
        this.sender = null;
        this.receiver = null;
    }
    public Message(String call, Object param, Object receiver){
        this.call = call;
        this.param = param;
        this.receiver = receiver;
        this.sender = null;
    }
    public Message(Object sender, String call, Object param, Object receiver) {
        this.sender = sender;
        this.call = call;
        this.param = param;
        this.receiver = receiver;
    }
    public String getCall() {
        return call;
    }

    public Object getSender() {
        return sender;
    }

    public Object getParam() {
        return param;
    }

    public Object getReceiver() {
        return receiver;
    }
}
