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
package onl.identitas.identity.ejb.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Entity
@Table(name = "stories", uniqueConstraints = @UniqueConstraint(columnNames = {
    "name", "sprint_id"}))
@NamedQueries({
    @NamedQuery(name = "story.countByNameAndSprint",
                query
                        = "select count(s) from Story as s where s.name = :name and s.sprint = :sprint and not(s = :currentStory)"),
    @NamedQuery(name = "story.new.countByNameAndSprint",
                query
                        = "select count(s) from Story as s where s.name = :name and s.sprint = :sprint")})
public class Story extends AbstractEntity implements Serializable {

private static final long serialVersionUID = 1L;

private static final Logger LOG = LogManager.getLogger();

@Column(nullable = false)
private String name;
private int priority;
@Temporal(TemporalType.DATE)
@Column(name = "start_date", nullable = false)
private LocalDate startDate;
@Temporal(TemporalType.DATE)
@Column(name = "end_date")
private LocalDate endDate;
private String acceptance;
private int estimation;
@ManyToOne
@JoinColumn(name = "sprint_id")
private Sprint sprint;
@OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
private List<Task> tasks;

public Story() {
    this(null);
}

public Story(String name) {
    this.name = name;
    this.startDate = LocalDate.now();
}

public String getAcceptance() {
    return acceptance;
}

public void setAcceptance(String acceptance) {
    this.acceptance = acceptance;
}

public LocalDate getEndDate() {
    return endDate;
}

public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
}

public int getEstimation() {
    return estimation;
}

public void setEstimation(int estimation) {
    this.estimation = estimation;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public int getPriority() {
    return priority;
}

public void setPriority(int priority) {
    this.priority = priority;
}

public Sprint getSprint() {
    return sprint;
}

void setSprint(Sprint sprint) {
    LOG.entry(sprint);
    this.sprint = sprint;
}

public LocalDate getStartDate() {
    return startDate;
}

public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
}

public List<Task> getTasks() {
    return (tasks != null) ? unmodifiableList(tasks) : emptyList();
}

public List<Task> getDoneTasks() {
    return unmodifiableList(getTasks(TaskStatus.DONE));
}

public List<Task> getWorkingTasks() {
    return unmodifiableList(getTasks(TaskStatus.WORKING));
}

public List<Task> getTodoTasks() {
    return unmodifiableList(getTasks(TaskStatus.TODO));
}

private List<Task> getTasks(TaskStatus status) {
    List<Task> result = new LinkedList<>();
    if (tasks != null) {
        tasks.stream()
                .filter((task) -> (task != null && status.equals(task
                                   .getStatus())))
                .forEach((task) -> {
                    result.add(task);
                });
    }
    return result;
}

public boolean addTask(Task task) {
    LOG.entry(task);
    if (tasks == null) {
        tasks = new LinkedList<>();
    }
    if (!tasks.contains(task)) {
        task.setStory(this);
        tasks.add(task);
        return LOG.exit(true);
    } else {
        return LOG.exit(false);
    }
}

public boolean removeTask(Task task) {
    LOG.entry(task);
    if (tasks != null && tasks.remove(task)) {
        task.setStory(null);
        return LOG.exit(true);
    } else {
        return LOG.exit(false);
    }
}

@Override
@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
}

@Override
public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
}

@Override
public String toString() {
    return ToStringBuilder.reflectionToString(this);
}
}
