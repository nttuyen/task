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

import org.exoplatform.task.domain.Status;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class TaskQuery {
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

  public long getTaskId() {
    return taskId;
  }

  public void setTaskId(long taskId) {
    this.taskId = taskId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Long> getProjectIds() {
    return projectIds;
  }

  public void setProjectIds(List<Long> projectIds) {
    this.projectIds = projectIds;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public List<OrderBy> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<OrderBy> orderBy) {
    this.orderBy = orderBy;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Boolean getCalendarIntegrated() {
    return calendarIntegrated;
  }

  public void setCalendarIntegrated(Boolean calendarIntegrated) {
    this.calendarIntegrated = calendarIntegrated;
  }

  public void setMemberships(List<String> permissions) {
    this.memberships =  permissions;
  }

  public List<String> getMemberships() {
    return memberships;
  }  

  public List<String> getOrFields() {
    return orFields;
  }

  public void setOrFields(List<String> orFields) {
    this.orFields = orFields;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Date getDueDateFrom() {
    return dueDateFrom;
  }

  public void setDueDateFrom(Date dueDateFrom) {
    this.dueDateFrom = dueDateFrom;
  }

  public Date getDueDateTo() {
    return dueDateTo;
  }

  public void setDueDateTo(Date dueDateTo) {
    this.dueDateTo = dueDateTo;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Boolean getIsIncoming() {
    return isIncoming;
  }

  public void setIsIncoming(Boolean isIncoming) {
    this.isIncoming = isIncoming;
  }

  public Boolean getIsTodo() {
    return isTodo;
  }

  public void setIsTodo(Boolean isTodo) {
    this.isTodo = isTodo;
  }

  public String getNullField() {
    return nullField;
  }

  public void setNullField(String nullField) {
    this.nullField = nullField;
  }

  public TaskQuery clone() {
    TaskQuery q = new TaskQuery();
    q.setTaskId(taskId);
    q.setTitle(title);
    q.setDescription(description);
    q.setKeyword(keyword);

    q.setIsIncoming(isIncoming);
    q.setIsTodo(isTodo);
    q.setUsername(username);

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
