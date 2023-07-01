import java.util.HashMap;
import java.util.Map;
public class Invoker {
    public static Map<String, Command> comandos = new HashMap<String, Command>();
   /* public static Mensagem mensagem;
    static{
        comandos.put("Cpu", new CpuCommand(mensagem));
        comandos.put("Escalonador", new EscalonadorCommand());
    }
*/
    public static void invoke (String command, Mensagem msg){
        System.out.println(msg.msg);
        if (command.equals("Cpu")){//descobre origem
            CpuCommand cmd;
            cmd = new CpuCommand(msg);
            cmd.execute();
            //atualiza GUI
            //cria LOG
        }
            
            //comandos.get(command).execute();
        
    }
}