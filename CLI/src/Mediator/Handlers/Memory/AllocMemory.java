package Mediator.Handlers.Memory;

import Mediator.Mediator;
import Mediator.Message;
import Os.MemoryManager;
import Utils.Command;

public class AllocMemory implements Command{
   
    @Override
    public Object execute(Message message) {
        int size = (int) message.getParameters()[0];
        MemoryManager mem_manager = (MemoryManager) message.getComponents().get(Mediator.Component.MEM_MANAGER);
        return mem_manager.allocMemory(size);
    }
    
}
