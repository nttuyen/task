/*
 * Copyright (C) 2015 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.task.dao;

import org.exoplatform.task.dao.query.AggregateCondition;
import org.exoplatform.task.dao.query.Condition;
import org.exoplatform.task.dao.query.Query;
import org.exoplatform.task.domain.Status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.exoplatform.task.dao.query.Query.*;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class TaskQuery implements Cloneable {

  private AggregateCondition aggCondition = null;

  private List<Long> projectIds;
  private String assignee = null;
  
  private List<OrderBy> orderBy = new ArrayList<OrderBy>();

  public TaskQuery() {

  }

  public static TaskQuery or(TaskQuery... queries) {
    List<Condition> cond = new ArrayList<Condition>();
    for(TaskQuery q : queries) {
      if (q.getCondition() != null) {
        cond.add(q.getCondition());
      }
    }
    Condition c = Query.or(cond.toArray(new Condition[cond.size()]));
    TaskQuery q = new TaskQuery();
    q.add(c);
    return q;
  }

  public TaskQuery add(TaskQuery taskQuery) {
    this.add(taskQuery.getCondition());
    return this;
  }

  TaskQuery add(Condition condition) {
    if (condition == null) return this;

    if (aggCondition == null) {
      aggCondition = and(condition);
    } else {
      aggCondition.add(condition);
    }
    return this;
  }

  public Condition getCondition() {
    return this.aggCondition;
  }

  public void setTitle(String title) {
    this.add(like(TASK_TITLE, '%' + title + '%'));
  }

  public void setDescription(String description) {
    this.add(like(TASK_DES, '%' + description + '%'));
  }

  @Deprecated
  public List<Long> getProjectIds() {
    return projectIds;
  }

  public void setProjectIds(List<Long> projectIds) {
    this.projectIds = projectIds;
    this.add(in(TASK_PROJECT, projectIds));
  }

  @Deprecated
  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
    this.add(eq(TASK_ASSIGNEE, assignee));
  }

  public void setKeyword(String keyword) {
    List<Condition> conditions = new ArrayList<Condition>();
    for(String k : keyword.split(" ")) {
      if (!k.trim().isEmpty()) {
        k = "%" + k.trim().toLowerCase() + "%";
        conditions.add(like(TASK_TITLE, k));
        conditions.add(like(TASK_DES, k));
        conditions.add(like(TASK_ASSIGNEE, k));
      }
    }
    add(Query.or(conditions.toArray(new Condition[conditions.size()])));
  }

  public List<OrderBy> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<OrderBy> orderBy) {
    this.orderBy = orderBy;
  }

  public void setCompleted(Boolean completed) {
    if (completed) {
      add(isTrue(TASK_COMPLETED));
    } else {
      add(isFalse(TASK_COMPLETED));
    }
  }

  public void setStartDate(Date startDate) {
    add(gte(TASK_END_DATE, startDate));
  }

  public void setEndDate(Date endDate) {
    add(lte(TASK_START_DATE, endDate));
  }

  public void setCalendarIntegrated(Boolean calendarIntegrated) {
    if (calendarIntegrated) {
      add(isTrue(TASK_CALENDAR_INTEGRATED));
    } else {
      add(isFalse(TASK_CALENDAR_INTEGRATED));
    }
  }

  public void setMemberships(List<String> permissions) {
    add(Query.or(in(TASK_PARTICIPATOR, permissions), in(TASK_MANAGER, permissions)));
  }

  public void setAssigneeOrMembership(String username, List<String> memberships) {
    this.assignee = username;
    this.add(Query.or(eq(TASK_ASSIGNEE, username), in(TASK_MANAGER, memberships), in(TASK_PARTICIPATOR, memberships)));
  }

  public void setAssigneeOrInProject(String username, List<Long> projectIds) {
    this.assignee = username;
    this.projectIds = projectIds;
    this.add(Query.or(eq(TASK_ASSIGNEE, username), in(TASK_PROJECT, projectIds)));
  }

  public void setStatus(Status status) {
    add(eq(TASK_STATUS, status));
  }

  public void setDueDateFrom(Date dueDateFrom) {
    add(gte(TASK_DUEDATE, dueDateFrom));
  }

  public void setDueDateTo(Date dueDateTo) {
    add(lte(TASK_DUEDATE, dueDateTo));
  }

  public void setIsIncomingOf(String username) {
    add(and(Query.or(eq(TASK_ASSIGNEE, username), eq(TASK_COWORKER, username), eq(TASK_CREATOR, username)), isNull(TASK_STATUS)));
  }

  public void setIsTodoOf(String username) {
    setAssignee(username);
    //add(eq(TASK_ASSIGNEE, username));
  }

  public void setNullField(String nullField) {
    add(isNull(nullField));
  }

  public TaskQuery clone() {
    try {
      return (TaskQuery) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("clone is not supported in TaskQuery", e);
    }
  }
}
