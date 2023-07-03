package simuladorso;

public abstract class Comando implements Mensagem{
    protected Scheduler receiver;
    public Comando(Scheduler receiver){
        this.receiver = receiver;
    }
}
