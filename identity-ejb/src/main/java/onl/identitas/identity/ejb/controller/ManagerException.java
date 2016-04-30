package onl.identitas.identity.ejb.controller;

/**
 *
 * @author José M. Fernández-Alba @lt;jmfernandezdalba@gmail.com@gt;
 */
@SuppressWarnings("ClassWithoutLogger")
public class ManagerException extends Exception {

private static final long serialVersionUID = 1L;

public ManagerException(Throwable cause) {
    super(cause);
}

public ManagerException(String message, Throwable cause) {
    super(message, cause);
}
}
