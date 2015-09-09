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
import org.exoplatform.task.domain.Status;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import static org.exoplatform.task.dao.query.Query.*;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class TaskQuery {

  private AggregateCondition aggCondition = null;

  private long taskId = 0;
  private String title = null;
  private String description = null;
  private String keyword = null;

  //
  private Boolean isIncoming = null;
  private Boolean isTodo = null;
  private String username = null;

  //
  private List<Long> projectIds;
  private Status status = null;

  private String assignee = null;
  private List<String> memberships;

  private Boolean calendarIntegrated;

  private Boolean completed;

  private Date startDate;
  private Date endDate;

  //
  private Date dueDateFrom = null;
  private Date dueDateTo = null;

  //
  private String nullField = null;
  
  private List<OrderBy> orderBy = new ArrayList<OrderBy>();

  private List<String> orFields = new LinkedList<String>();

  public TaskQuery() {

  }

  public TaskQuery add(Condition condition) {
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

  @Deprecated
  public long getTaskId() {
    return taskId;
  }

  public void setTaskId(long taskId) {
    this.taskId = taskId;
    this.add(eq(TASK_ID, taskId));
  }

  @Deprecated
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
    this.add(like(TASK_TITLE, '%' + title + '%'));
  }

  @Deprecated
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  @Deprecated
  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
    List<Condition> conditions = new ArrayList<Condition>();
    for(String k : keyword.split(" ")) {
      if (!k.trim().isEmpty()) {
        k = "%" + k.trim().toLowerCase() + "%";
        conditions.add(like(TASK_TITLE, k));
        conditions.add(like(TASK_DES, k));
        conditions.add(like(TASK_ASSIGNEE, k));
      }
    }
    add(or(conditions.toArray(new Condition[conditions.size()])));
  }

  public List<OrderBy> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<OrderBy> orderBy) {
    this.orderBy = orderBy;
  }

  @Deprecated
  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
    if (completed) {
      add(isTrue(TASK_COMPLETED));
    } else {
      add(isFalse(TASK_COMPLETED));
    }
  }

  @Deprecated
  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
    add(gte(TASK_END_DATE, startDate));
  }

  @Deprecated
  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
    add(lte(TASK_START_DATE, endDate));
  }

  @Deprecated
  public Boolean getCalendarIntegrated() {
    return calendarIntegrated;
  }

  public void setCalendarIntegrated(Boolean calendarIntegrated) {
    this.calendarIntegrated = calendarIntegrated;
    if (calendarIntegrated) {
      add(isTrue(TASK_CALENDAR_INTEGRATED));
    } else {
      add(isFalse(TASK_CALENDAR_INTEGRATED));
    }
  }

  public void setMemberships(List<String> permissions) {
    this.memberships =  permissions;
    add(or(in(TASK_PARTICIPATOR, permissions), in(TASK_MANAGER, permissions)));
  }

  @Deprecated
  public List<String> getMemberships() {
    return memberships;
  }  

  public List<String> getOrFields() {
    return orFields;
  }

  public void setOrFields(List<String> orFields) {
    this.orFields = orFields;
  }

  @Deprecated
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
    add(eq(TASK_STATUS, status));
  }

  @Deprecated
  public Date getDueDateFrom() {
    return dueDateFrom;
  }

  public void setDueDateFrom(Date dueDateFrom) {
    this.dueDateFrom = dueDateFrom;
    add(gte(TASK_DUEDATE, dueDateFrom));
  }

  @Deprecated
  public Date getDueDateTo() {
    return dueDateTo;
  }

  public void setDueDateTo(Date dueDateTo) {
    this.dueDateTo = dueDateTo;
    add(lte(TASK_DUEDATE, dueDateTo));
  }

  @Deprecated
  public String getUsername() {
    return username;
  }

  @Deprecated
  public void setUsername(String username) {
    this.username = username;
  }

  @Deprecated
  public Boolean getIsIncoming() {
    return isIncoming;
  }

  @Deprecated
  public void setIsIncoming(Boolean isIncoming) {
    this.isIncoming = isIncoming;
  }

  public void setIsIncomingOf(String username) {
    setIsIncoming(Boolean.TRUE);
    setUsername(username);
    add(and(or(eq(TASK_ASSIGNEE, username), eq(TASK_COWORKER, username), eq(TASK_CREATOR, username)), isNull(TASK_STATUS)));
  }

  @Deprecated
  public Boolean getIsTodo() {
    return isTodo;
  }

  @Deprecated
  public void setIsTodo(Boolean isTodo) {
    this.isTodo = isTodo;
  }

  public void setIsTodoOf(String username) {
    setIsTodo(Boolean.TRUE);
    setUsername(username);
    add(eq(TASK_ASSIGNEE, username));
  }

  @Deprecated
  public String getNullField() {
    return nullField;
  }

  public void setNullField(String nullField) {
    this.nullField = nullField;
    add(isNull(nullField));
  }

  public TaskQuery clone() {
    TaskQuery q = new TaskQuery();
    q.setTaskId(taskId);
    q.setTitle(title);
    q.setDescription(description);
    q.setKeyword(keyword);

    if (isIncoming) {
      q.setIsIncomingOf(username);
    } else if (isTodo) {
      q.setIsTodoOf(username);
    }

    q.setProjectIds(projectIds);
    q.setStatus(status);
    q.setAssignee(assignee);
    q.setMemberships(memberships);

    q.setCalendarIntegrated(calendarIntegrated);

    q.setCompleted(completed);

    q.setStartDate(startDate);
    q.setEndDate(endDate);

    q.setDueDateFrom(dueDateFrom);
    q.setDueDateTo(dueDateTo);

    q.setOrderBy(orderBy);
    q.setOrFields(orFields);

    return q;
  }
}
