package org.pampasim;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface DialogService <T> {

    Optional<T> showDialog(Object ... args);
}
