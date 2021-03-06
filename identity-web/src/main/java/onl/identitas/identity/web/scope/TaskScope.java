package onl.identitas.identity.web.scope;

import java.io.Serializable;
import java.util.HashMap;
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
public class TaskScope implements Serializable {

private static final long serialVersionUID = 1L;

private static final int MAP_INITIAL_SIZE = 16;

private static final Logger LOG = LogManager.getLogger();

private final HashMap<String, Object> map = new HashMap<>(MAP_INITIAL_SIZE);

public Object getValue(String key) {
    LOG.entry(key);
    return LOG.exit(map.get(key));
}

/**
 * Will call the postConstruct method.
 *
 * @param scopeName name of the scope.
 * @param facesContext the context of the application.
 */
public void notifyCreate(String scopeName, FacesContext facesContext) {
    LOG.entry(scopeName, facesContext);
    ScopeContext scopeContext = new ScopeContext(scopeName, map);
    Application application = facesContext.getApplication();
    application.publishEvent(facesContext, PostConstructCustomScopeEvent.class,
                             scopeContext);
    LOG.exit();
}

/**
 * Will call the preDestroy method.
 *
 * @param scopeName name of the scope.
 * @param facesContext the context of the application.
 */
public void notifyDestroy(String scopeName, FacesContext facesContext) {
    LOG.entry(scopeName, facesContext);
    ScopeContext scopeContext = new ScopeContext(scopeName, map);
    Application application = facesContext.getApplication();
    application.publishEvent(facesContext, PreDestroyCustomScopeEvent.class,
                             scopeContext);
    LOG.exit();
}
}
