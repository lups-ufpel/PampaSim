package simuladorso;

public class CpuCommand implements Command{
    private Cpu cpu; //deveria ser private
    private String msg;
    private Process process;
   
    public CpuCommand(Message message){//Cpu cpu, String msg, Processo proc){
        this.cpu = message.cpu;
        this.msg = message.msg;
        this.process = message.proc;
    }
    
    public void execute() {
        System.out.println("Iniciando a execução do Processo");
        this.cpu.executeProcess(this.process);
    }
    
}