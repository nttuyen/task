package org.exoplatform.task.service;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.task.dao.TaskQuery;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.domain.TaskLog;
import org.exoplatform.task.exception.EntityNotFoundException;
import org.exoplatform.task.exception.ParameterEntityException;

/**
 * Created by TClement on 6/3/15.
 */
public interface TaskService {

  String TASK_CREATION = "exo.task.taskCreation";

  String TASK_UPDATE = "exo.task.taskUpdate";

  Task createTask(Task task);

  Task saveTaskField(long taskId, String fieldName, String[] values, TimeZone timezone)
      throws EntityNotFoundException, ParameterEntityException;

  void updateTaskOrder(long currentTaskId, Status newStatus, long[] orders);

  void deleteTask(long taskId) throws EntityNotFoundException;

  Task cloneTask(long taskId) throws EntityNotFoundException;

  Task getTask(long taskId) throws EntityNotFoundException;

  ListAccess<Comment> getComments(long taskId);

  Comment createComment(long taskId, String username, String commentText) throws EntityNotFoundException;

  void deleteComment(long commentId) throws EntityNotFoundException;

  ListAccess<Task> findTasks(TaskQuery query);

  public <T> List<T> selectTaskField(TaskQuery query, String fieldName);

  /**
   * Createa a log associated with a task with given <code>taskId</code>.
   * 
   * @param taskId
   * @param username
   * @param msg
   * @param target
   * @return
   * @throws EntityNotFoundException
   */
  TaskLog addTaskLog(long taskId, String username, String msg, String target) throws EntityNotFoundException;

  //TODO: should use via #findTasks(TaskQuery)?
  Task findTaskByActivityId(String activityId);
}
