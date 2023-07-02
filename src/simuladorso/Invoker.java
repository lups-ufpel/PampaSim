package simuladorso;

import java.util.HashMap;

public class Invoker {
    private static Invoker instance;
    private HashMap<String, Command> commands;

    private Invoker() {
        commands = new HashMap<String, Command>();
        commands.put("CPU", new NothingCommand());
    }

    public static Invoker getInstance() {
        if (instance == null)
            instance = new Invoker();
        return instance;
    }
   /* public static Mensagem mensagem;
    static{
        comandos.put("Cpu", new CpuCommand(mensagem));
        comandos.put("Escalonador", new EscalonadorCommand());
    }
    */

    public void setCommand(String commandString, Command command) {
        commands.put(commandString, command);
    }

    public void execute(String commandString){
        //System.out.println(msg.msg);
        
        commands.get(commandString).execute();

        /*
        if (command.equals("Cpu")) {//descobre origem
            CpuCommand cmd;
            cmd = new CpuCommand(msg);
            cmd.execute();
            //atualiza GUI
            //cria LOG
        }
            
            //comandos.get(command).execute();
        */
    }
}