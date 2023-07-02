package simuladorso;

public class Cpu {
    private int clock;

    public Cpu() {
        this.clock = 1500; //MHz
    }

    public Cpu(int clock) {
        this.clock = clock;
    }

    public void cycle(Process process){
        if(process.programCounter < process.getNumberOfInstructions()){
            process.programCounter++;
            System.out.println("Executando a instrução: "+process.programCounter);
            try {
                // Pausa a execução por 3 segundos
                Thread.sleep((5000)*100/this.clock);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void executeProcess(Process proc){
        int instrucoes = proc.getNumberOfInstructions();

        for(int i=1; i<=instrucoes; i++){
            System.out.println("Executando a instrução: "+i);
            try {
                // Pausa a execução por 3 segundos
                Thread.sleep((5000)*100/this.clock);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Processo Concluído");
    }

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }
}