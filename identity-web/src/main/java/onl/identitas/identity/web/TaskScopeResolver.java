package onl.identitas.identity.web;

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

	private static final String SCOPE_NAME = "taskScope";
	private static final Logger LOG = LogManager.getLogger();

	public static void destroyScope() {
		LOG.entry();
		FacesContext ctx = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = ctx.getExternalContext()
				.getSessionMap();
		TaskScope taskScope = (TaskScope) sessionMap.remove(SCOPE_NAME);
		if (taskScope != null) {
			taskScope.notifyDestroy(SCOPE_NAME, ctx);
		}
		LOG.exit();
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		if (base != null) {
			return null;
		}
		return String.class;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
															 Object base) {
		return Collections.<FeatureDescriptor>emptyList().iterator();
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		return Object.class;
	}

	@Override
	public Object getValue(ELContext context, Object scope, Object property) {
		if (property == null) {
			throw new PropertyNotFoundException();
		}
		if (scope == null && SCOPE_NAME.equals(property.toString())) {
			TaskScope scopeManager = getScope(context);
			context.setPropertyResolved(true);
			return scopeManager;
		} else if (scope != null && scope instanceof TaskScope) {
			//looking for bean in scope already created.
			return lookupBean(context, (TaskScope) scope, property.toString());
		} else if (scope == null) {
			return lookupBean(context, getScope(context), property.toString());
		}
		return null;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property,
						 Object value) {
	}

	private TaskScope getScope(ELContext context) {
		//looking for custom scope in the session
		//if doesn't exists create and put it in the session
		FacesContext facesContext = (FacesContext) context.getContext(
				FacesContext.class);
		Map<String, Object> sessionMap = facesContext.getExternalContext()
				.getSessionMap();

		TaskScope scopeManager = (TaskScope) sessionMap.get(SCOPE_NAME);
		if (scopeManager == null) {
			scopeManager = new TaskScope(facesContext.getApplication());
			sessionMap.put(SCOPE_NAME, scopeManager);
			scopeManager.notifyCreate(SCOPE_NAME, facesContext);
		}
		return scopeManager;
	}

	private Object lookupBean(ELContext context, TaskScope scope, String key) {
		//looking for mbean in taskScope
		Object value = scope.get(key);
		context.setPropertyResolved(value != null);
		return value;
	}
}
