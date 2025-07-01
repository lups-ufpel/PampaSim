package org.pampasim.dialog;


import java.util.Optional;

public interface DialogService <T> {

    Optional<T> showDialog(Object ... args);
}
