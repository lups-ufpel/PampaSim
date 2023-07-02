package simuladorso;
public class Message {
    //criar um padrão para msg
    public Cpu cpu;
    public String msg;
    public Process proc;
    public Message(Cpu cpu, String msg, Process proc){
        this.cpu = cpu;
        this.msg = msg;
        this.proc = proc;
    }
}