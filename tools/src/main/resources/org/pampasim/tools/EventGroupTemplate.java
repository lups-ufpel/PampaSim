package DESTINATION_PACKAGE;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class CLASS_NAME extends AbstractEvent {
    @Getter
    @Setter
    private PAYLOAD_CLASS DATA_MEMBER_NAME;

    public CLASS_NAME(SimEntity source, PAYLOAD_CLASS data) {
        super(source);
        DATA_MEMBER_NAME = data;
    }

    @Override
    public org.pampasim.core.events.Event cloneAs(Class<? extends org.pampasim.core.events.Event> asClass)
            throws IncompatibleEventDataException {
        try {
            Constructor<? extends org.pampasim.core.events.Event> cons =
                    asClass.getConstructor(SimEntity.class, PAYLOAD_CLASS.class);
            return cons.newInstance(getSource(), getData());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException _e) {
            throw new IncompatibleEventDataException();
        }
    }

    public Object getData() {
        return DATA_MEMBER_GETTER ();
    }
    public void setData(Object obj) { DATA_MEMBER_SETTER ((PAYLOAD_CLASS) obj); }
}