package onl.identitas.identity.web.controller;

import java.io.Serializable;
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
public class SkinValuesManager implements Serializable {

private static final long serialVersionUID = 1L;

public static final String YELLOW_SKIN = "yellow";
public static final String ORANGE_SKIN = "orange";
public static final String RED_SKIN = "red";
public static final String BLUE_SKIN = "blue";
public static final String DEFAULT_SKIN = ORANGE_SKIN;
private static final int NUM_SKINS = 4;

private static final Logger LOG = LogManager.getLogger();

private HashMap<String, String> values;

@PostConstruct
public void construct() {
    LOG.entry();
    values = new HashMap<>(NUM_SKINS);
    values.put(YELLOW_SKIN, "appYellowSkin.css");
    values.put(ORANGE_SKIN, "appOrangeSkin.css");
    values.put(RED_SKIN, "appRedSkin.css");
    values.put(BLUE_SKIN, "appBlueSkin.css");
    LOG.exit();
}

@PreDestroy
public void destroy() {
    LOG.entry();
    values = null;
    LOG.exit();
}

protected String getSkinCss(String skin) {
    LOG.entry(skin);
    return LOG.exit(values.getOrDefault(skin, getDefaultSkinCss()));
}

protected String getDefaultSkinCss() {
    return LOG.exit(values.get(DEFAULT_SKIN));
}

public List<String> getNames() {
    return LOG.exit(new ArrayList<>(values.keySet()));
}

public int getSize() {
    return LOG.exit(values.size());
}
}
