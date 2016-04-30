/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.

 Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package onl.identitas.identity.ejb.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import onl.identitas.identity.ejb.entities.Project;
import onl.identitas.identity.ejb.entities.Sprint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author José M. Fernández-Alba @lt;jmfernandezdalba@gmail.com@gt;
 */
@ManagedBean(name = "sprintManager")
@SessionScoped
public class SprintManager extends AbstractManager implements Serializable {

private static final long serialVersionUID = 1L;

private static final Logger LOG = LogManager.getLogger();

private Sprint currentSprint;
private transient DataModel<Sprint> sprints;
private List<Sprint> sprintList;
@ManagedProperty("#{projectManager}")
private ProjectManager projectManager;
private Project currentProject;

@PostConstruct
public void construct() {
    Sprint sprint = new Sprint();
    Project pmCurrentProject = getProjectManager().getCurrentProject();
    pmCurrentProject.addSprint(sprint);
    setCurrentSprint(sprint);
    sprintList = new LinkedList<>(pmCurrentProject.getSprints());
}

@PreDestroy
public void destroy() {
    sprints = null;
    if (null != sprintList) {
        sprintList.clear();
        sprintList = null;
    }
    projectManager = null;
    currentProject = null;
}

public String create() {
    Sprint sprint = new Sprint();
    getProjectManager().getCurrentProject().addSprint(sprint);
    setCurrentSprint(sprint);
    return CREATE_RESULT;
}

public String save() {
    if (currentSprint != null) {
        try {
            Sprint merged = doInTransaction((EntityManager em) -> {
                if (currentSprint.isNew()) {
                    em.persist(currentSprint);
                } else if (!em.contains(currentSprint)) {
                    return em.merge(currentSprint);
                }
                return currentSprint;
            });
            if (!currentSprint.equals(merged)) {
                setCurrentSprint(merged);
                int idx = sprintList.indexOf(currentSprint);
                if (idx != -1) {
                    sprintList.set(idx, merged);
                }
            }
            getProjectManager().getCurrentProject().addSprint(merged);
            if (!sprintList.contains(merged)) {
                sprintList.add(merged);
            }
        }
        catch (ManagerException e) {
            LOG.catching(e);
            addMessage("Error on try to save Sprint",
                       FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }
    return SHOW_RESULT;
}

public String edit() {
    setCurrentSprint(sprints.getRowData());
    return EDIT_RESULT;
}

public String remove() {
    final Sprint sprint = sprints.getRowData();
    if (sprint != null) {
        try {
            doInTransaction((EntityManager em) -> {
                if (em.contains(sprint)) {
                    em.remove(sprint);
                } else {
                    em.remove(em.merge(sprint));
                }
            });
            getProjectManager().getCurrentProject().removeSprint(sprint);
            sprintList.remove(sprint);
        }
        catch (ManagerException e) {
            LOG.catching(e);
            addMessage("Error on try to remove Sprint",
                       FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }
    return SHOW_RESULT;
}

/*
 * This method can be pointed to by a validator methodExpression, such as:
 *
 * <h:inputText id="itName" value="#{sprintManager.currentSprint.name}" required="true"
 *   requiredMessage="#{i18n['sprint.form.label.name.required']}" maxLength="30" size="30"
 *   validator="#{sprintManager.checkUniqueSprintName}" />
 */
public void checkUniqueSprintNameFacesValidatorMethod(FacesContext context,
                                                      UIComponent component,
                                                      Object newValue) {

    final String newName = (String) newValue;
    String message = checkUniqueSprintNameApplicationValidatorMethod(newName);
    if (null != message) {
        throw new ValidatorException(getFacesMessageForKey(
                "sprint.form.label.name.unique"));
    }
}


/*
 * This method is called by the JSR-303 SprintNameUniquenessConstraintValidator.
 * If it returns non-null, the result must be interpreted as the localized
 * validation message.
 *
 */
public String checkUniqueSprintNameApplicationValidatorMethod(String newValue) {
    String message = null;

    final String newName = newValue;
    try {
        Long count = doInTransaction((EntityManager em) -> {
            Query query = em.createNamedQuery((currentSprint.isNew())
                                                      ? "sprint.new.countByNameAndProject"
                                              : "sprint.countByNameAndProject");
            query.setParameter("name", newName);
            query.setParameter("project", getProjectManager()
                               .getCurrentProject());
            if (!currentSprint.isNew()) {
                query.setParameter("currentSprint", currentSprint);
            }
            return (Long) query.getSingleResult();
        });
        if (count != null && count > 0) {
            message = getFacesMessageForKey("sprint.form.label.name.unique")
                    .getSummary();
        }
    }
    catch (ManagerException ex) {
        LOG.catching(ex);
    }

    return message;
}

public String cancelEdit() {
    return "show";
}

public String showStories() {
    setCurrentSprint(sprints.getRowData());
    return "showStories";
}

public String showDashboard() {
    setCurrentSprint(sprints.getRowData());
    return "showDashboard";
}

public Sprint getCurrentSprint() {
    return currentSprint;
}

public void setCurrentSprint(Sprint currentSprint) {
    this.currentSprint = currentSprint;
}

public DataModel<Sprint> getSprints() {
    this.sprints = new ListDataModel<>(projectManager.getCurrentProject()
            .getSprints());
    return this.sprints;
}

public void setSprints(DataModel<Sprint> sprints) {
    this.sprints = sprints;
}

public ProjectManager getProjectManager() {
    return projectManager;
}

public void setProjectManager(ProjectManager projectManager) {
    this.projectManager = projectManager;
}

public Project getProject() {
    Project pmCurrentProject = projectManager.getCurrentProject();
    // Verify if the currentProject is out of date
    // If there is a new CurrentProject we need to update sprintList and set currentSprint to null and tell user he/she needs to select a Sprint
    if (pmCurrentProject != currentProject) {
        this.setCurrentSprint(null);
        this.sprintList = pmCurrentProject.getSprints();
        this.sprints = new ListDataModel<>(sprintList);
        this.currentProject = pmCurrentProject;
    }
    return currentProject;
}

public void setProject(Project project) {
    projectManager.setCurrentProject(project);
}
}
