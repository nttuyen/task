/* 
* Copyright (C) 2003-2015 eXo Platform SAS.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see http://www.gnu.org/licenses/ .
*/
package org.exoplatform.task.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.task.dao.DAOHandler;
import org.exoplatform.task.dao.TaskQuery;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Priority;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.domain.TaskLog;
import org.exoplatform.task.exception.EntityNotFoundException;
import org.exoplatform.task.exception.ParameterEntityException;
import org.exoplatform.task.service.TaskService;
import org.exoplatform.task.service.impl.TaskEvent.EventBuilder;
import org.exoplatform.task.service.impl.TaskEvent.Type;

/**
 * Created by The eXo Platform SAS
 * Author : Thibault Clement
 * tclement@exoplatform.com
 * 6/3/15
 */
@Singleton
public class TaskServiceImpl implements TaskService {

  private static final Log LOG = ExoLogger.getExoLogger(TaskServiceImpl.class);

  @Inject
  private DAOHandler daoHandler;

  private ListenerService listenerService;
  
  public TaskServiceImpl(DAOHandler daoHandler, ListenerService listenerService) {
    this.daoHandler = daoHandler;
    this.listenerService = listenerService;
  }

  // Just for test purpose
  static public TaskServiceImpl createInstance(DAOHandler hl, ListenerService listenerService) {
    TaskServiceImpl sv = new TaskServiceImpl(hl, listenerService);
    return sv;
  }

  @Override
  @ExoTransactional
  public Task createTask(Task task) {
    Task result = daoHandler.getTaskHandler().create(task);
    //
    EventBuilder builder = new TaskEvent.EventBuilder(this);
    builder.withTask(result).withType(TaskEvent.Type.CREATED);
    try {
      listenerService.broadcast(TASK_CREATION, this, builder.build());
    } catch (Exception e) {
      LOG.error("Error while broadcasting task creation event", e);
    }

    return result;
  }

  @Override
  @ExoTransactional
  public Task saveTaskField(long id, String param, String[] values, TimeZone timezone)
      throws EntityNotFoundException, ParameterEntityException {

    Task task = getTask(id);

    if(task == null) {
      LOG.info("Can not find task with ID: " + id);
      throw new EntityNotFoundException(id, Task.class);
    }

    if (timezone == null) {
      timezone = TimeZone.getDefault();
    }

    //
    EventBuilder builder = new TaskEvent.EventBuilder(this);
    builder.withTask(task);
    //
    if ("workPlan".equalsIgnoreCase(param)) {
      long oldStartTime = -1;
      if (task.getStartDate() != null) {
        oldStartTime = task.getStartDate().getTime();
      }
      long oldEndTime = -1;
      if (task.getEndDate() != null) {
        oldEndTime = task.getEndDate().getTime();
      }
      builder.withType(Type.EDIT_WORKPLAN).withOldVal(oldStartTime + "/" + oldEndTime);
      //
      if (values == null) {
        task.setStartDate(null);
        task.setEndDate(null);
      } else {
        if (values.length != 2) {
          LOG.error("workPlan updating lack of params");
        }

        try {
          SimpleDateFormat wpf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
          wpf.setTimeZone(timezone);
          Date startDate = wpf.parse(values[0]);
          Date endDate = wpf.parse(values[1]);
          
          task.setStartDate(startDate);
          task.setEndDate(endDate);
          builder.withNewVal(startDate.getTime() + "/" + endDate.getTime());
        } catch (ParseException ex) {
          LOG.info("Can parse date time value: "+values[0]+" or "+values[1]+" for Task with ID: "+id);
          throw new ParameterEntityException(id, Task.class, param, values[0]+" or "+values[1],
              "cannot be parse to date", ex);
        }
      }
    } else {
      String value = values != null && values.length > 0 ? values[0] : null;

      if("title".equalsIgnoreCase(param)) {
        builder.withType(Type.EDIT_TITLE).withOldVal(task.getTitle());
        //
        task.setTitle(value);
        builder.withNewVal(task.getTitle());
      } else if("dueDate".equalsIgnoreCase(param)) {
        builder.withType(Type.EDIT_DUEDATE).withOldVal(task.getDueDate());
        //
        if(value == null || value.trim().isEmpty()) {
          task.setDueDate(null);
        } else {
          DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
          df.setTimeZone(timezone);
          try {
            Date date = df.parse(value);
            task.setDueDate(date);
            builder.withNewVal(task.getDueDate());
          } catch (ParseException ex) {
            LOG.info("Can parse date time value: "+value+" for Task with ID: "+id);
            throw new ParameterEntityException(id, Task.class, param, value, "cannot be parse to date", ex);
          }
        }
      } else if("status".equalsIgnoreCase(param)) {
        builder.withType(Type.EDIT_STATUS).withOldVal(task.getStatus());
        //
        try {
          Long statusId = Long.parseLong(value);
          Status status = daoHandler.getStatusHandler().find(statusId);
          if(status == null) {
            LOG.info("Status does not exist with ID: " + value);
            throw new EntityNotFoundException(id, Status.class);
          }
          task.setStatus(status);
          builder.withNewVal(task.getStatus());
        } catch (NumberFormatException ex) {
          LOG.info("Status is unacceptable: "+value+" for Task with ID: "+id);
          throw new ParameterEntityException(id, Task.class, param, value, "is unacceptable", ex);
        }
      } else if("description".equalsIgnoreCase(param)) {
        builder.withType(Type.EDIT_DESCRIPTION).withOldVal(task.getDescription());
        task.setDescription(value);
        builder.withNewVal(task.getDescription());
      } else if("completed".equalsIgnoreCase(param)) {
        builder.withType(Type.MARK_DONE).withOldVal(task.isCompleted());
        task.setCompleted(Boolean.parseBoolean(value));
        builder.withNewVal(task.isCompleted());
      } else if("assignee".equalsIgnoreCase(param)) {
        builder.withType(Type.EDIT_ASSIGNEE).withOldVal(task.getAssignee());
        task.setAssignee(value);
        builder.withNewVal(task.getAssignee());
      } else if("coworker".equalsIgnoreCase(param)) {
        Set<String> coworker = new HashSet<String>();
        if (values != null) {
          for (String v : values) {
            if (v != null && !v.isEmpty()) {
              coworker.add(v);
            }
          }
        }
        task.setCoworker(coworker);
      } else if("tags".equalsIgnoreCase(param)) {
        Set<String> old = task.getTag();
        Set<String> tags = new HashSet<String>();
        for(String t : values) {
          tags.add(t);
        }
        task.setTag(tags);

        Set<String> newTags = new HashSet<String>(task.getTag());
        builder.withType(Type.ADD_LABEL).withNewVal(newTags.removeAll(old));
      } else if ("priority".equalsIgnoreCase(param)) {
        Priority priority = Priority.valueOf(value);
        task.setPriority(priority);
      } else if ("project".equalsIgnoreCase(param)) {
        builder.withType(Type.EDIT_PROJECT).withOldVal(task.getStatus() != null ? task.getStatus().getProject() : null);
        try {
          Long projectId = Long.parseLong(value);
          if (projectId > 0) {
            Status st = daoHandler.getStatusHandler().findLowestRankStatusByProject(projectId);
            if (st == null) {
              throw new ParameterEntityException(id, Task.class, param, value, "Status for project is not found", null);
            }
            task.setStatus(st);
            builder.withNewVal(task.getStatus().getProject());
          } else {
            task.setStatus(null);
          }
        } catch (NumberFormatException ex) {
          throw new ParameterEntityException(id, Task.class, param, value, "ProjectID must be long", ex);
        }
      } else if ("calendarIntegrated".equalsIgnoreCase(param)) {
        task.setCalendarIntegrated(Boolean.parseBoolean(value));
      } else {
        LOG.info("Field name: " + param + " is not supported for entity Task");
        throw new ParameterEntityException(id, Task.class, param, value, "is not supported for the entity Task", null);
      }
    }

    Task result = updateTask(task);
    TaskEvent event = builder.build();
    if (event.getType() != null) {
      try {
        listenerService.broadcast("exo.task.updateTask", this, event);
      } catch (Exception e) {
        LOG.error("Error while broadcasting the task update event", e);
      }
    }

    //TODO: save order of task here?

    //.
    if ("status".equalsIgnoreCase(param) && values.length > 2) {
      //TODO: need save order of task (update rank)
      long[] taskIds = new long[values.length - 1];
      int currentTaskIndex = -1;
      for (int i = 1; i < values.length; i++) {
        taskIds[i - 1] = Long.parseLong(values[i]);
        if (taskIds[i - 1] == id) {
          currentTaskIndex = i - 1;
        }
      }
      if (currentTaskIndex > -1) {
        //. Update here

      }
    }

    return result;
  }

  @Override
  @ExoTransactional
  public void updateTaskOrder(long currentTaskId, Status newStatus, long[] orders) {
      daoHandler.getTaskHandler().updateTaskOrder(currentTaskId, newStatus, orders);
  }

  @Override
  @ExoTransactional
  public void deleteTask(long id) throws EntityNotFoundException {

    Task task = getTask(id);// Can throw TaskNotFoundException

    daoHandler.getTaskHandler().delete(task);
  }

  @Override
  @ExoTransactional
  public Task cloneTask(long id) throws EntityNotFoundException {

    Task task = getTask(id);// Can throw TaskNotFoundException

    Task newTask = task.clone();

    return createTask(newTask);
  }

  @Override
  public Task getTask(long id) throws EntityNotFoundException {
    Task task = daoHandler.getTaskHandler().find(id);
    if (task == null) {
      LOG.info("Can not find task with ID: " + id);
      throw new EntityNotFoundException(id, Task.class);
    }
    return task;
  }

  @Override
  public ListAccess<Comment> getComments(long taskId) {
    return daoHandler.getCommentHandler().findComments(taskId);
  }

  @Override
  @ExoTransactional
  public Comment createComment(long id, String username, String comment) throws EntityNotFoundException {

    Task task = getTask(id); //Can throws TaskNotFoundException

    Comment newComment = new Comment();
    newComment.setTask(task);
    newComment.setAuthor(username);
    newComment.setComment(comment);
    newComment.setCreatedTime(new Date());
    Comment obj = daoHandler.getCommentHandler().create(newComment);
    return obj;
  }
  
  @Override
  public TaskLog addTaskLog(long id, String username, String msg, String target) throws EntityNotFoundException {
    Task task = getTask(id); //Can throws TaskNotFoundException

    TaskLog log = new TaskLog();
    log.setAuthor(username);
    log.setMsg(msg);
    log.setTarget(target);
    task.getTaskLogs().add(log);
    daoHandler.getTaskHandler().update(task);
    return log;
  }

  @Override
  @ExoTransactional
  public void deleteComment(long commentId) throws EntityNotFoundException {

    Comment comment = daoHandler.getCommentHandler().find(commentId);

    if(comment == null) {
      LOG.info("Can not find comment with ID: " + commentId);
      throw new EntityNotFoundException(commentId, Comment.class);
    }

    daoHandler.getCommentHandler().delete(comment);
  }

  @Override
  public ListAccess<Task> findTasks(TaskQuery query) {
    return daoHandler.getTaskHandler().findTasks(query);
  }

  @Override
  public <T> List<T> selectTaskField(TaskQuery query, String fieldName) {
    return daoHandler.getTaskHandler().selectTaskField(query, fieldName);
  }
  
  private Task updateTask(Task task) {
    return daoHandler.getTaskHandler().update(task);
  }

  @Override
  public Task findTaskByActivityId(String id) {
    return daoHandler.getTaskHandler().findTaskByActivityId(id);
  }

}