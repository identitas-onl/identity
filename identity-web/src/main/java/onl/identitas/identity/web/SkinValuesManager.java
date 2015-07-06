package onl.identitas.identity.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author edermag
 */
@ManagedBean(name = "skinValuesManager", eager = true)
@ApplicationScoped
public class SkinValuesManager {

private static final String DEFAULT_SKIN = "blue";
private static final Logger LOG = LogManager.getLogger();

private HashMap<String, String> values;

@PostConstruct
public void construct() {
	LOG.entry();

	values = new HashMap<>(4);
	values.put("yellow", "appYellowSkin.css");
	values.put("orange", "appOrangeSkin.css");
	values.put("red", "appRedSkin.css");
	values.put(DEFAULT_SKIN, "appBlueSkin.css");

	LOG.exit();
}

@PreDestroy
public void destroy() {
	LOG.entry();
	if (null != values) {
		LOG.trace("values is null");
		values.clear();
		values = null;
	}
	LOG.exit();
}

protected String getSkinCss(String skin) {
	if (!values.containsKey(skin)) {
		return getDefaultSkinCss();
	}
	return values.get(skin);
}

protected String getDefaultSkinCss() {
	return values.get(DEFAULT_SKIN);
}

public List<String> getNames() {
	return new ArrayList<>(values == null ? null : values.keySet());
}

public int getSize() {
	return values.keySet().size();
}
}
