public class Mensagem {
    //criar um padrão para msg
    public Cpu cpu;
    public String msg;
    public PCB proc;
    public Mensagem(Cpu cpu, String msg, PCB proc){
        this.cpu = cpu;
        this.msg = msg;
        this.proc = proc;
    }
}