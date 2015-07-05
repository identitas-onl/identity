package onl.identitas.identity.web;

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
