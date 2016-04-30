package onl.identitas.identity.web.controller;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import onl.identitas.identity.ejb.controller.AbstractManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO: escribir esta documentación y más cosas. Prueba de jenkins webhook.
 *
 * @author Chema
 */
@ManagedBean(name = "skinManager")
@SessionScoped
public class SkinManager extends AbstractManager implements Serializable {

private static final long serialVersionUID = 1L;
private static final Logger LOG = LogManager.getLogger();

@ManagedProperty(value = "#{skinValuesManager}")
private SkinValuesManager skinValuesManager;

private String selectedSkin;

@PostConstruct
public void construct() {
    LOG.entry();
    selectedSkin = skinValuesManager.getDefaultSkinCss();
    LOG.exit();
}

public SkinValuesManager getSkinValuesManager() {
    return LOG.exit(skinValuesManager);
}

public void setSkinValuesManager(SkinValuesManager skinValuesManager) {
    LOG.entry(skinValuesManager);
    this.skinValuesManager = skinValuesManager;
}

public String getSelectedSkin() {
    return LOG.exit(selectedSkin);
}

public void setSelectedSkin(String selectedSkin) {
    LOG.entry(selectedSkin);
    this.selectedSkin = selectedSkin;
}
}
