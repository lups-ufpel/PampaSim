package org.pampasim.Mediator.Handlers.Memory;

import org.pampasim.Mediator.Mediator;
import org.pampasim.Mediator.Message;
import org.pampasim.Os.MemoryManager;
import org.pampasim.Utils.Command;

public class AllocMemory implements Command{
   
    @Override
    public Object execute(Message message) {
        int size = (int) message.getParameters()[0];
        MemoryManager mem_manager = (MemoryManager) message.getComponents().get(Mediator.Component.MEM_MANAGER);
        return mem_manager.allocMemory(size);
    }
    
}
