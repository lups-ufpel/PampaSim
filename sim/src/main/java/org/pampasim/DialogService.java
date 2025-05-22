package org.pampasim;


import java.util.Optional;

public interface DialogService <T> {

    Optional<T> showDialog(Object ... args);
}
