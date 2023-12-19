package org.simuladorso.Mediator.Handlers.Memory;

import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.Message;
import org.simuladorso.Os.MemoryManager;
import org.simuladorso.Utils.Command;

public class AllocMemory implements Command{
   
    @Override
    public Object execute(Message message) {
        int size = (int) message.getParameters()[0];
        MemoryManager mem_manager = (MemoryManager) message.getComponents().get(Mediator.Component.MEM_MANAGER);
        return mem_manager.allocMemory(size);
    }
    
}
