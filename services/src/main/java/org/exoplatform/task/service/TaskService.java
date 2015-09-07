package org.exoplatform.task.service;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.task.dao.OrderBy;
import org.exoplatform.task.dao.TaskQuery;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.domain.TaskLog;
import org.exoplatform.task.exception.CommentNotFoundException;
import org.exoplatform.task.exception.ParameterEntityException;
import org.exoplatform.task.exception.StatusNotFoundException;
import org.exoplatform.task.exception.TaskNotFoundException;

/**
 * Created by TClement on 6/3/15.
 */
public interface TaskService {

  Task createTask(Task task);

  Task saveTaskField(long taskId, String fieldName, String[] values, TimeZone timezone)
      throws TaskNotFoundException, ParameterEntityException, StatusNotFoundException;

  void updateTaskOrder(long currentTaskId, Status newStatus, long[] orders);

  void deleteTask(long taskId) throws TaskNotFoundException;

  Task cloneTask(long taskId) throws TaskNotFoundException;

  Task getTask(long taskId) throws TaskNotFoundException;

  ListAccess<Comment> getComments(long taskId);

  Comment createComment(long taskId, String username, String commentText) throws TaskNotFoundException;

  void deleteComment(long commentId) throws CommentNotFoundException;

  ListAccess<Task> findTasks(TaskQuery query);

  public <T> List<T> selectTaskField(TaskQuery query, String fieldName);

  TaskLog addTaskLog(long taskId, String username, String msg, String target) throws TaskNotFoundException;

  //TODO: should use via #findTasks(TaskQuery)?
  Task findTaskByActivityId(String activityId);
}
