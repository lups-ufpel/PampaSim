package simuladorso;
public class Message {
    //criar um padrão para msg
    public Cpu cpu;
    public String msg;
    public PCB proc;

    public Message(Cpu cpu, String msg, PCB proc) {
        this.cpu = cpu;
        this.msg = msg;
        this.proc = proc;
    }
}