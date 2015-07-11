package onl.identitas.identity.web.scope;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.faces.context.FacesContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Resolver to #{taskScope} expression.
 *
 * @author eder
 */
public class TaskScopeResolver extends ELResolver {

public static final String SCOPE_NAME = "taskScope";
public static final String PROPERTY_NULL_MSG = "The property is null";

private static final Logger LOG = LogManager.getLogger();

public static void destroyScope() {
	LOG.entry();
	FacesContext ctx = FacesContext.getCurrentInstance();
	Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();
	TaskScope taskScope = (TaskScope) sessionMap.remove(SCOPE_NAME);
	if (taskScope != null) {
		taskScope.notifyDestroy(SCOPE_NAME, ctx);
	}
	LOG.exit();
}

@Override
public Class<?> getCommonPropertyType(ELContext context, Object base) {
	LOG.entry(context, base);
	if (base != null) {
		return LOG.exit(null);
	}
	return LOG.exit(String.class);
}

@Override
public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
														 Object base) {
	LOG.entry(context, base);
	return LOG.exit(Collections.<FeatureDescriptor>emptyListIterator());
}

@Override
public Class<?> getType(ELContext context, Object base, Object property) {
	LOG.entry(context, base, property);
	return LOG.exit(Object.class);
}

@Override
public Object getValue(ELContext context, Object scope, Object property) {
	LOG.entry(context, scope, property);

	if (property == null) {
		throw LOG.throwing(new PropertyNotFoundException(PROPERTY_NULL_MSG));
	}

	if (scope == null) {
		if (SCOPE_NAME.equals(property.toString())) {
			TaskScope scopeManager = getScope(context);
			context.setPropertyResolved(true);
			return LOG.exit(scopeManager);
		} else {
			return LOG.exit(lookupBean(context, getScope(context), property
									   .toString()));
		}
	} else if (scope instanceof TaskScope) {
		//looking for bean in scope already created.
		return LOG.exit(lookupBean(context, (TaskScope) scope, property
								   .toString()));
	}

	return LOG.exit(null);
}

@Override
public boolean isReadOnly(ELContext context, Object base, Object property) {
	LOG.entry(context, base, property);
	return LOG.exit(true);
}

@Override
public void setValue(ELContext context, Object base, Object property,
					 Object value) {
	LOG.entry(context, base, property, value);
}

private TaskScope getScope(ELContext context) {
	LOG.entry(context);
	//looking for custom scope in the session
	//if doesn't exists create and put it in the session
	FacesContext facesContext = (FacesContext) context.getContext(
			FacesContext.class);
	Map<String, Object> sessionMap = facesContext.getExternalContext()
			.getSessionMap();

	TaskScope scopeManager = (TaskScope) sessionMap.get(SCOPE_NAME);
	if (scopeManager == null) {
		scopeManager = new TaskScope();
		sessionMap.put(SCOPE_NAME, scopeManager);
		scopeManager.notifyCreate(SCOPE_NAME, facesContext);
	}

	return LOG.exit(scopeManager);
}

private Object lookupBean(ELContext context, TaskScope scope, String key) {
	LOG.entry(context, scope, key);
	//looking for mbean in taskScope
	Object value = scope.getValue(key);
	context.setPropertyResolved(value != null);
	return LOG.exit(value);
}
}
