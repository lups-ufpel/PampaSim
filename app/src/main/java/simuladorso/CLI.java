package simuladorso;

import simuladorso.Utils.Observer;

public class CLI implements Observer {
    @Override
    public void update(Object object) {
        System.out.println((String) object);
    }
}
