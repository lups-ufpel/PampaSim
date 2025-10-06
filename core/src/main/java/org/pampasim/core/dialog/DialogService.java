package org.pampasim.core.dialog;


import java.util.Optional;

public interface DialogService <T> {

    Optional<T> showDialog(Object ... args);
}
