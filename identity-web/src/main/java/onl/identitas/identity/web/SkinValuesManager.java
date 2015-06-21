package onl.identitas.identity.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class SkinValuesManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger();

	private final String defaultSkin = "blue";

	private Map<String, String> values;

	@PostConstruct
	public void construct() {
		LOG.entry();

		values = new LinkedHashMap<>(4);
		values.put("yellow", "appYellowSkin.css");
		values.put("orange", "appOrangeSkin.css");
		values.put("red", "appRedSkin.css");
		values.put(defaultSkin, "appBlueSkin.css");

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
		return values.get(defaultSkin);
	}

	public List<String> getNames() {
		return new ArrayList<>(values == null ? null : values.keySet());
	}

	public int getSize() {
		return values.keySet().size();
	}
}
