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
import java.time.LocalTime;
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
@Table(name = "sprints", uniqueConstraints = @UniqueConstraint(columnNames = {
    "name", "project_id"}))
@NamedQueries({
    @NamedQuery(name = "sprint.countByNameAndProject",
                query
                        = "select count(s) from Sprint as s where s.name = :name and s.project = :project and not(s = :currentSprint)"),
    @NamedQuery(name = "sprint.new.countByNameAndProject",
                query
                        = "select count(s) from Sprint as s where s.name = :name and s.project = :project")})
public class Sprint extends AbstractEntity implements Serializable {

private static final long serialVersionUID = 1L;

private static final Logger LOG = LogManager.getLogger();

@Column(nullable = false)
private String name;
private String goals;
@Temporal(TemporalType.DATE)
@Column(name = "start_date", nullable = false)
private LocalDate startDate;
@Temporal(TemporalType.DATE)
@Column(name = "end_date")
private LocalDate endDate;
@Column(name = "iteration_scope")
private int iterationScope;
@Column(name = "gained_story_points")
private int gainedStoryPoints;
@Temporal(TemporalType.TIME)
@Column(name = "daily_meeting_time")
private LocalTime dailyMeetingTime;
@OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL)
private List<Story> stories;
@ManyToOne
@JoinColumn(name = "project_id")
private Project project;

public Sprint() {
    this.startDate = LocalDate.now();
}

public Sprint(String name) {
    this();
    this.name = name;
}

public Sprint(String name, Project project) {
    this(name);
    this.project = project;
}

@SprintNameUniquenessConstraint
public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getGoals() {
    return goals;
}

public void setGoals(String goals) {
    this.goals = goals;
}

public LocalDate getStartDate() {
    return startDate;
}

public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
}

public LocalDate getEndDate() {
    return endDate;
}

public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
}

public int getIterationScope() {
    return iterationScope;
}

public void setIterationScope(int iterationScope) {
    this.iterationScope = iterationScope;
}

public int getGainedStoryPoints() {
    return gainedStoryPoints;
}

public void setGainedStoryPoints(int gainedStoryPoints) {
    this.gainedStoryPoints = gainedStoryPoints;
}

public LocalTime getDailyMeetingTime() {
    return dailyMeetingTime;
}

public void setDailyMeetingTime(LocalTime dailyMeetingTime) {
    this.dailyMeetingTime = dailyMeetingTime;
}

public List<Story> getStories() {
    return (stories != null) ? unmodifiableList(stories) : emptyList();
}

public boolean addStory(Story story) {
    LOG.entry(story);
    if (stories == null) {
        stories = new LinkedList<>();
    }
    if (!stories.contains(story)) {
        story.setSprint(this);
        stories.add(story);
        return LOG.exit(true);
    } else {
        return LOG.exit(false);
    }
}

public boolean removeStory(Story story) {
    LOG.entry(story);
    if (stories != null && stories.remove(story)) {
        story.setSprint(null);
        return LOG.exit(true);
    } else {
        return LOG.exit(false);
    }
}

public Project getProject() {
    return project;
}

void setProject(Project project) {
    this.project = project;
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
