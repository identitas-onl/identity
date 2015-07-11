package onl.identitas.identity.web.controller;

import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessageFactory;

/**
 *
 * @author Dr. Spock (spock at dev.java.net)
 */
public abstract class AbstractManager {

public static final String DO_IN_TRANSACTION_ERROR_MSG
								   = "Exception during transaction: ";

private static final Logger LOG = LogManager.getLogger();
private static final FormattedMessageFactory MSG_FACTORY
													 = new FormattedMessageFactory();

@PersistenceUnit
private EntityManagerFactory emf;
@Resource
private UserTransaction userTransaction;

/**
 * Executes the provided 'action' within a user transaction and returns the
 * result.
 *
 * @param <T> the type returned by the 'action'.
 * @param action the action to be executed.
 *
 * @return the value returned by the 'action'.
 *
 * @throws ManagerException if there was any problem during transaction.
 */
protected <T> T doInTransaction(Function<EntityManager, T> action) throws
		ManagerException {
	LOG.entry(action);

	EntityManager em = emf.createEntityManager();
	try {
		LOG.debug(MSG_FACTORY.newMessage("Executing action: {}", action));

		userTransaction.begin();

		T result = action.apply(em);

		userTransaction.commit();

		LOG.debug("Action executed successfully");

		return LOG.exit(result);
	}
	catch (NotSupportedException | SystemException | RollbackException |
		   HeuristicMixedException | HeuristicRollbackException e) {
		LOG.catching(e);

		try {
			userTransaction.rollback();
		}
		catch (SystemException ex) {
			LOG.catching(ex);
		}

		throw LOG.throwing(new ManagerException(DO_IN_TRANSACTION_ERROR_MSG + e
				.getLocalizedMessage(), e));
	}
	finally {
		em.close();
	}
}

/**
 * Executes the provided 'action' within a user transaction.
 *
 * @param action the action to be executed.
 *
 * @throws ManagerException if there was any problem during transaction.
 */
protected void doInTransaction(Consumer<EntityManager> action) throws
		ManagerException {
	LOG.entry(action);

	EntityManager em = emf.createEntityManager();
	try {
		LOG.debug(MSG_FACTORY.newMessage("Executing action: {}", action));

		userTransaction.begin();

		action.accept(em);

		userTransaction.commit();

		LOG.debug("Action executed successfully");

		LOG.exit();
	}
	catch (HeuristicMixedException | HeuristicRollbackException |
		   NotSupportedException | RollbackException | SystemException e) {
		LOG.catching(e);

		try {
			userTransaction.rollback();
		}
		catch (SystemException ex) {
			LOG.catching(ex);
		}

		throw LOG.throwing(new ManagerException(DO_IN_TRANSACTION_ERROR_MSG + e
				.getLocalizedMessage(), e));
	}
	finally {
		em.close();
	}
}

protected void addMessage(String message) {
	addMessage(null, message, FacesMessage.SEVERITY_INFO);
}

protected void addMessage(String componentId, String message) {
	addMessage(componentId, message, FacesMessage.SEVERITY_INFO);
}

/**
 * Calls {@link #addMessage(String, String, Severity)} with a null componentId.
 *
 * @param message localized summary message text.
 * @param severity the severity of the message.
 */
protected void addMessage(String message, Severity severity) {
	addMessage(null, message, severity);
}

/**
 * Convenience method to invoke
 * {@link FacesContext#addMessage(String, FacesMessage)} on
 * {@link FacesContext#getCurrentInstance()}.
 *
 * @param componentId the client identifier with which this message is
 * associated (if any).
 * @param message localized summary message text.
 * @param severity the severity of the message.
 */
protected void addMessage(String componentId, String message, Severity severity) {
	LOG.entry(componentId, message, severity);
	FacesContext.getCurrentInstance().addMessage(componentId,
												 new FacesMessage(severity,
																  message,
																  message));
	LOG.exit();
}

protected String getMessageForKey(String key) {
	LOG.entry(key);
	FacesContext ctx = FacesContext.getCurrentInstance();
	ResourceBundle rb = ctx.getApplication().getResourceBundle(ctx, "i18n");
	return LOG.exit(rb.getString(key));
}

protected FacesMessage getFacesMessageForKey(String key) {
	LOG.entry(key);
	return LOG.exit(new FacesMessage(getMessageForKey(key)));
}

protected void publishEvent(Class<? extends SystemEvent> eventClass,
							Object source) {
	LOG.entry(eventClass, source);
	if (source != null) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getApplication().publishEvent(ctx, eventClass, source);
	}
	LOG.exit();
}

protected void subscribeToEvent(Class<? extends SystemEvent> eventClass,
								SystemEventListener listener) {
	LOG.entry(eventClass, listener);
	FacesContext.getCurrentInstance().getApplication().subscribeToEvent(
			eventClass, listener);
	LOG.exit();
}

protected void unsubscribeFromEvent(Class<? extends SystemEvent> eventClass,
									SystemEventListener listener) {
	LOG.entry(eventClass, listener);
	FacesContext.getCurrentInstance().getApplication().unsubscribeFromEvent(
			eventClass, listener);
	LOG.exit();
}
}
