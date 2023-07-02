package simuladorso;

public class CpuCommand implements Command{
    private Cpu cpu; //deveria ser private
    private String msg;
    private PCB processo;
   
    public CpuCommand(Message mensagem){//Cpu cpu, String msg, Processo proc){
        this.cpu = mensagem.cpu;
        this.msg = mensagem.msg;
        this.processo = mensagem.proc;
    }
    
    public void execute() {
        System.out.println("Iniciando a execução do Processo");
        this.cpu.executeProcess(this.processo);
    }
    
}