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

  Task updateTaskInfo(long id, String param, String[] values, TimeZone timezone)
      throws TaskNotFoundException, ParameterEntityException, StatusNotFoundException;

  void updateTaskOrder(long currentTaskId, Status newStatus, long[] orders);

  //TODO: switch updateTaskInfo()
  @Deprecated
  Task updateTaskCompleted(long id, Boolean completed)
      throws TaskNotFoundException, ParameterEntityException, StatusNotFoundException;

  void deleteTask(Task task);

  void deleteTaskById(long id) throws TaskNotFoundException;

  Task cloneTaskById(long id) throws TaskNotFoundException;

  Task getTaskById(long id) throws TaskNotFoundException;

  /**
   * use {@link #getComments(long)}
   * @param task
   * @return
   */
  @Deprecated
  Long getNbOfCommentsByTask(Task task);

  /**
   * use {@link #getComments(long)}
   * @param id
   * @param start
   * @param limit
   * @return
   * @throws TaskNotFoundException
   */
  @Deprecated
  List<Comment> getCommentsByTaskId(long id, int start, int limit) throws TaskNotFoundException;

  /**
   * use {@link #getComments(long)}
   * @param task
   * @param start
   * @param limit
   * @return
   */
  @Deprecated
  List<Comment> getCommentsByTask(Task task, int start, int limit);

  ListAccess<Comment> getComments(long taskId);

  Comment addCommentToTaskId(long id, String username, String comment) throws TaskNotFoundException;

  void deleteCommentById(long commentId) throws CommentNotFoundException;

  /**
   * use {@link #getIncomingTasks(String, org.exoplatform.task.dao.OrderBy)}
   * @param username
   * @param orderBy
   * @return
   */
  @Deprecated
  List<Task> getIncomingTasksByUser(String username, OrderBy orderBy);

  ListAccess<Task> getIncomingTasks(String username, OrderBy orderBy);

  /**
   * use {@link #getTodoTasks(String, java.util.List, org.exoplatform.task.dao.OrderBy, java.util.Date, java.util.Date)}
   * @param username
   * @param projectIds
   * @param orderBy
   * @param fromDueDate
   * @param toDueDate
   * @return
   */
  @Deprecated
  List<Task> getToDoTasksByUser(String username, List<Long> projectIds, OrderBy orderBy, Date fromDueDate, Date toDueDate);

  ListAccess<Task> getTodoTasks(String username, List<Long> projectIds, OrderBy orderBy, Date fromDueDate, Date toDueDate);

  /**
   * use {@link #findTasks(org.exoplatform.task.dao.TaskQuery)}
   * @param query
   * @return
   */
  @Deprecated
  List<Task> findTaskByQuery(TaskQuery query);

  ListAccess<Task> findTasks(TaskQuery query);

  /**
   * use {@link #findTasks(org.exoplatform.task.dao.TaskQuery)}
   * @param username
   * @param projectIds
   * @return
   */
  @Deprecated
  long getTaskNum(String username, List<Long> projectIds);

  TaskLog addTaskLog(long taskId, String username, String msg, String target) throws TaskNotFoundException;
}
