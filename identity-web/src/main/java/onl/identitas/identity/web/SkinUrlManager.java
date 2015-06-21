package onl.identitas.identity.web;

import java.io.Serializable;
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
public class SkinUrlManager extends AbstractManager implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger();

	private String skin;

	@ManagedProperty(value = "#{skinManager}")
	private SkinManager skinManager;
	@ManagedProperty(value = "#{skinValuesManager}")
	private SkinValuesManager skinValuesManager;

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public void update() {
		LOG.entry();
		if (skin == null || skin.isEmpty()) {
			LOG.trace("skin is null or empty");
			LOG.exit();
			return;
		}
		String skinCss = skinValuesManager.getSkinCss(skin.toLowerCase());
		skinManager.setSelectedSkin(skinCss);
		LOG.exit();
	}

	public SkinManager getSkinManager() {
		return skinManager;
	}

	public void setSkinManager(SkinManager skinManager) {
		this.skinManager = skinManager;
	}

	public SkinValuesManager getSkinValuesManager() {
		return skinValuesManager;
	}

	public void setSkinValuesManager(SkinValuesManager skinValuesManager) {
		this.skinValuesManager = skinValuesManager;
	}
}
