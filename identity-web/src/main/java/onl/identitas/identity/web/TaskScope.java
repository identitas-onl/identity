package onl.identitas.identity.web;

import java.util.concurrent.ConcurrentHashMap;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.PostConstructCustomScopeEvent;
import javax.faces.event.PreDestroyCustomScopeEvent;
import javax.faces.event.ScopeContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Actually, custom scope is a Map where the instances of managed bean are
 * store.
 */
public class TaskScope extends ConcurrentHashMap<String, Object> {

private static final long serialVersionUID = 1L;
private static final Logger LOG = LogManager.getLogger();

private final Application application;

public TaskScope(Application application) {
	this.application = application;
}

/**
 * Will call the postConstruct method.
 *
 * @param scopeName
 * @param facesContext
 */
public void notifyCreate(String scopeName, FacesContext facesContext) {
	LOG.entry(scopeName, facesContext);
	ScopeContext scopeContext = new ScopeContext(scopeName, this);
	application.publishEvent(facesContext, PostConstructCustomScopeEvent.class,
							 scopeContext);
	LOG.exit();
}

/**
 * Will call the preDestroy method.
 *
 * @param scopeName
 * @param facesContext
 */
public void notifyDestroy(String scopeName, FacesContext facesContext) {
	LOG.entry(scopeName, facesContext);
	ScopeContext scopeContext = new ScopeContext(scopeName, this);
	application.publishEvent(facesContext, PreDestroyCustomScopeEvent.class,
							 scopeContext);
	LOG.exit();
}
}
