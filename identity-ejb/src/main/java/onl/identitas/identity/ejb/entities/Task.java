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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Entity
@Table(name = "tasks", uniqueConstraints = @UniqueConstraint(columnNames = {
    "name", "story_id"}))
@NamedQueries({
    @NamedQuery(name = "task.countByNameAndStory",
                query
                        = "select count(t) from Task as t where t.name = :name and t.story = :story and not(t = :currentTask)"),
    @NamedQuery(name = "task.new.countByNameAndStory",
                query
                        = "select count(t) from Task as t where t.name = :name and t.story = :story")})
public class Task extends AbstractEntity implements Serializable {

private static final long serialVersionUID = 1L;

public static final String STATUS_KEY_I18N = "task.show.table.header.status.";

private static final Logger LOG = LogManager.getLogger();

@Column(nullable = false)
private String name;

@Temporal(TemporalType.DATE)
@Column(name = "start_date")
private LocalDate startDate;

@Temporal(TemporalType.DATE)
@Column(name = "end_date")
private LocalDate endDate;

@Enumerated(EnumType.ORDINAL)
private TaskStatus status;

@ManyToOne
@JoinColumn(name = "story_id")
private Story story;

public Task() {
    this(null);
}

public Task(String name) {
    this.name = name;
    this.status = TaskStatus.TODO;
}

public LocalDate getEndDate() {
    return endDate;
}

public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
    changeTaskStatus(this.startDate, endDate);
}

protected void changeTaskStatus(LocalDate startDate, LocalDate endDate) {
    LOG.entry(startDate, endDate);
    if (endDate != null) {
        this.setStatus(TaskStatus.DONE);
    }
    if (endDate == null && this.startDate != null) {
        this.setStatus(TaskStatus.WORKING);
    }
    if (endDate == null && this.startDate == null) {
        this.setStatus(TaskStatus.TODO);
    }
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public LocalDate getStartDate() {
    return startDate;
}

public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
    changeTaskStatus(startDate, this.endDate);
}

public TaskStatus getStatus() {
    return status;
}

public void setStatus(TaskStatus status) {
    this.status = status;
}

public Story getStory() {
    return story;
}

void setStory(Story story) {
    LOG.entry(story);
    this.story = story;
}

public String getStatusKeyI18n() {
    return STATUS_KEY_I18N + status;
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
