package onl.identitas.identity.web;

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

/**
 *
 * @author Dr. Spock (spock at dev.java.net)
 */
public abstract class AbstractManager {

	private static final Logger LOG = LogManager.getLogger();

	@PersistenceUnit
	private EntityManagerFactory emf;
	@Resource
	private UserTransaction userTransaction;

	protected <T> T doInTransaction(Function<EntityManager, T> action) throws
			ManagerException {
		LOG.entry(action);

		EntityManager em = emf.createEntityManager();
		try {
			userTransaction.begin();

			T result = action.apply(em);

			userTransaction.commit();

			return LOG.exit(result);
		}
		catch (NotSupportedException | SystemException | RollbackException |
			   HeuristicMixedException | HeuristicRollbackException |
			   SecurityException |
			   IllegalStateException e) {
			LOG.catching(e);

			try {
				userTransaction.rollback();
			}
			catch (IllegalStateException | SecurityException | SystemException ex) {
				LOG.catching(ex);
			}

			throw LOG.throwing(new ManagerException(e));
		}
		finally {
			em.close();
		}
	}

	protected void doInTransaction(Consumer<EntityManager> action) throws
			ManagerException {
		LOG.entry(action);

		EntityManager em = emf.createEntityManager();
		try {
			userTransaction.begin();

			action.accept(em);

			userTransaction.commit();

			LOG.exit();
		}
		catch (IllegalStateException | SecurityException |
			   HeuristicMixedException |
			   HeuristicRollbackException | NotSupportedException |
			   RollbackException |
			   SystemException e) {
			LOG.catching(e);

			try {
				userTransaction.rollback();
			}
			catch (IllegalStateException | SecurityException | SystemException ex) {
				LOG.catching(ex);
			}

			throw LOG.throwing(new ManagerException(e));
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

	protected void addMessage(String message, Severity severity) {
		addMessage(null, message, severity);
	}

	protected void addMessage(String componentId, String message,
							  Severity severity) {
		FacesContext.getCurrentInstance().addMessage(componentId,
													 new FacesMessage(severity,
																	  message,
																	  message));
	}

	protected String getMessageForKey(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ResourceBundle rb = ctx.getApplication().getResourceBundle(ctx, "i18n");
		return rb.getString(key);
	}

	protected FacesMessage getFacesMessageForKey(String key) {
		return new FacesMessage(getMessageForKey(key));
	}

	protected void publishEvent(Class<? extends SystemEvent> eventClass,
								Object source) {
		if (source != null) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().publishEvent(ctx, eventClass, source);
		}
	}

	protected void subscribeToEvent(Class<? extends SystemEvent> eventClass,
									SystemEventListener listener) {
		FacesContext.getCurrentInstance().getApplication().subscribeToEvent(
				eventClass, listener);
	}

	protected void unsubscribeFromEvent(Class<? extends SystemEvent> eventClass,
										SystemEventListener listener) {
		FacesContext.getCurrentInstance().getApplication().
				unsubscribeFromEvent(eventClass, listener);
	}
}
