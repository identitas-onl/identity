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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import onl.identitas.identity.ejb.entities.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author José M. Fernández-Alba @lt;jmfernandezdalba@gmail.com@gt;
 */
@ManagedBean(name = "projectManager")
@SessionScoped
public class ProjectManager extends AbstractManager implements Serializable {

private static final long serialVersionUID = 1L;

private static final Logger LOG = LogManager.getLogger();

private Project currentProject;
private transient DataModel<Project> projects;
private LinkedList<SelectItem> projectItems;
private LinkedList<Project> projectList;

@PostConstruct
public void construct() {
    setCurrentProject(new Project());
    init();
}

@PreDestroy
public void destroy() {
    projects = null;
    if (projectItems != null) {
        projectItems.clear();
        projectItems = null;
    }
    if (projectList != null) {
        projectList.clear();
        projectList = null;
    }
    currentProject = null;
}

@SuppressWarnings("unchecked")
public void init() {
    projectList = new LinkedList<>();
    try {
        setProjectList(doInTransaction((em) -> {
            Query query = em.createNamedQuery("project.getAll");
            return (List<Project>) query.getResultList();
        }));
    }
    catch (ManagerException ex) {
        LOG.catching(ex);
    }
    projectItems = new LinkedList<>();
    projectItems.add(new SelectItem(new Project(), "-- Select one project --"));
    if (getProjectList() != null) {
        getProjectList().stream()
                .forEach((p) -> {
                    projectItems.add(new SelectItem(p, p.getName()));
                });
    }
}

public String create() {
    Project project = new Project();
    setCurrentProject(project);
    return "create";
}

public String save() {
    if (getCurrentProject() != null) {
        try {
            Project merged = doInTransaction((EntityManager em) -> {
                if (getCurrentProject().isNew()) {
                    em.persist(getCurrentProject());
                } else if (!em.contains(currentProject)) {
                    return em.merge(getCurrentProject());
                }
                return getCurrentProject();
            });
            if (!currentProject.equals(merged)) {
                setCurrentProject(merged);
                int idx = projectList.indexOf(getCurrentProject());
                if (idx != -1) {
                    projectList.set(idx, merged);
                }
            }
            if (!projectList.contains(merged)) {
                projectList.add(merged);
            }
        }
        catch (ManagerException e) {
            LOG.catching(e);
            addMessage("Error on try to save Project",
                       FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }
    init();
    return SHOW_RESULT;
}

public String edit() {
    setCurrentProject(getProjects().getRowData());
    // Using implicity navigation, this request come from /projects/show.xhtml and directs to /project/edit.xhtml
    return EDIT_RESULT;
}

public String remove() {
    final Project project = getProjects().getRowData();
    if (project != null) {
        try {
            doInTransaction((EntityManager em) -> {
                if (em.contains(project)) {
                    em.remove(project);
                } else {
                    em.remove(em.merge(project));
                }
            });
            getProjectList().remove(project);
        }
        catch (ManagerException e) {
            LOG.catching(e);
            addMessage("Error on try to remove Project",
                       FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }
    init();
    // Using implicity navigation, this request come from /projects/show.xhtml and directs to /project/show.xhtml
    // could be null instead
    return SHOW_RESULT;
}

public void checkUniqueProjectName(FacesContext context, UIComponent component,
                                   Object newValue) {
    final String newName = (String) newValue;
    try {
        Long count = doInTransaction((EntityManager em) -> {
            Query query = em.createNamedQuery((getCurrentProject().isNew())
                                                      ? "project.new.countByName"
                                              : "project.countByName");
            query.setParameter("name", newName);
            if (!currentProject.isNew()) {
                query.setParameter("currentProject", getCurrentProject());
            }
            return (Long) query.getSingleResult();
        });
        if (count != null && count > 0) {
            throw new ValidatorException(getFacesMessageForKey(
                    "project.form.label.name.unique"));
        }
    }
    catch (ManagerException ex) {
        LOG.catching(ex);
    }
}

public String cancelEdit() {
    // Implicity navigation, this request come from /projects/edit.xhtml and directs to /project/show.xhtml
    return SHOW_RESULT;
}

public String showSprints() {
    setCurrentProject(getProjects().getRowData());
    // Implicity navigation, this request come from /projects/show.xhtml and directs to /project/showSprints.xhtml
    return SHOW_SPRINTS_RESULT;
}

public Project getCurrentProject() {
    return currentProject;
}

public void setCurrentProject(Project currentProject) {
    this.currentProject = currentProject;
}

public DataModel<Project> getProjects() {
    if (projects == null) {
        setProjects(new ListDataModel<>(getProjectList()));
    }
    return projects;
}

public void setProjects(DataModel<Project> projects) {
    this.projects = projects;
}

public List<SelectItem> getProjectItems() {
    return Collections.unmodifiableList(projectItems);
}

public void setProjectItems(List<SelectItem> projectItems) {
    this.projectItems.clear();
    this.projectItems.addAll(projectItems);
}

/**
 * @return the projectList
 */
public List<Project> getProjectList() {
    return Collections.unmodifiableList(projectList);
}

/**
 * @param projectList the projectList to set
 */
public void setProjectList(List<Project> projectList) {
    this.projectList.clear();
    this.projectList.addAll(projectList);
}

public String viewSprints() {
    return VIEW_SPRINTS_RESULT;
}
}
