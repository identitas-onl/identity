package onl.identitas.identity.web;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Chema
 */
@ManagedBean(name = "skinManager")
@SessionScoped
public class SkinManager extends AbstractManager {

private static final Logger LOG = LogManager.getLogger();

private String selectedSkin;
@ManagedProperty(value = "#{skinValuesManager}")
private SkinValuesManager skinValuesManager;

@PostConstruct
public void construct() {
	LOG.entry();
	selectedSkin = skinValuesManager.getDefaultSkinCss();
	LOG.exit();
}

public String getSelectedSkin() {
	return selectedSkin;
}

public void setSelectedSkin(String selectedSkin) {
	this.selectedSkin = selectedSkin;
}

public SkinValuesManager getSkinValuesManager() {
	return skinValuesManager;
}

public void setSkinValuesManager(SkinValuesManager skinValuesManager) {
	this.skinValuesManager = skinValuesManager;
}
}
