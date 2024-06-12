package org.pampasim.Mediator.Handlers.Kernel;


import org.pampasim.Mediator.Message;
import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.Os;
import org.pampasim.Os.Process;
import org.pampasim.Utils.Command;

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
