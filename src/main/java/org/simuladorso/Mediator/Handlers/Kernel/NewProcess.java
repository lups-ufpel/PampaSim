package org.simuladorso.Mediator.Handlers.Kernel;


import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.Process;
import org.simuladorso.Utils.Command;

import java.util.Arrays;


public class NewProcess implements Command {

    final String COMMAND_NAME = this.getClass().getSimpleName();
    @Override
    public Object execute(Message message){
        Os os = (Os) message.getComponents().get(Mediator.Component.KERNEL);
        Object[] attributes = message.getParameters();


        LOGGER.info("Executing {} Command ...", COMMAND_NAME);

        if( os != null){

            if(attributes != null && attributes.length > 0){
                LOGGER.debug("Command Parameters {}", Arrays.toString(attributes));
                os.createProcess((Process.Type) attributes[0], (Integer) attributes[1], (Integer) attributes[2], (Integer) attributes[3]);
            }
            else{
                LOGGER.error("On Command {} parameter list is null or void",COMMAND_NAME);
            }
        }


        return null;
    }
}
