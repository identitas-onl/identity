package onl.identitas.identity.web.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author eder
 */
@ManagedBean(name = "skinUrlManager")
@RequestScoped
public class SkinUrlManager extends AbstractManager {

private static final Logger LOG = LogManager.getLogger();

@ManagedProperty(value = "#{skinManager}")
private SkinManager skinManager;
@ManagedProperty(value = "#{skinValuesManager}")
private SkinValuesManager skinValuesManager;

private String skin;

public void update() {
	LOG.entry();
	if (skin == null || skin.isEmpty()) {
		LOG.exit("skin is null or empty");
		return;
	}
	String skinCss = skinValuesManager.getSkinCss(skin.toLowerCase());
	skinManager.setSelectedSkin(skinCss);
	LOG.exit();
}

public SkinManager getSkinManager() {
	return LOG.exit(skinManager);
}

public void setSkinManager(SkinManager skinManager) {
	LOG.entry(skinManager);
	this.skinManager = skinManager;
}

public SkinValuesManager getSkinValuesManager() {
	return LOG.exit(skinValuesManager);
}

public void setSkinValuesManager(SkinValuesManager skinValuesManager) {
	LOG.entry(skinValuesManager);
	this.skinValuesManager = skinValuesManager;
}

public String getSkin() {
	return LOG.exit(skin);
}

public void setSkin(String skin) {
	LOG.entry(skin);
	this.skin = skin;
}
}
