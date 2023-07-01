public class Cpu {
    public int clock;
    public Cpu (){
        this.clock = 1500; //MHz
    }

    public void cycle(PCB process){
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
    public void executaProcesso(PCB proc){
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
}