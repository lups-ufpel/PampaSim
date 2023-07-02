public class CpuCommand implements Command{
    public Cpu cpu; //deveria ser private
    public String msg;
    public Process processo;
   
    public CpuCommand(Mensagem mensagem){//Cpu cpu, String msg, Processo proc){
        this.cpu = mensagem.cpu;
        this.msg = mensagem.msg;
        this.processo = mensagem.proc;
    }
    
    public void execute(){
        System.out.println("Iniciando a execução do Processo");
        this.cpu.executaProcesso(this.processo);
    }
    
}