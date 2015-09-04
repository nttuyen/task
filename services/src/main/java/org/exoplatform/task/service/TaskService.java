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

  void deleteTaskById(long id) throws TaskNotFoundException;

  Task cloneTaskById(long id) throws TaskNotFoundException;

  Task getTaskById(long id) throws TaskNotFoundException;

  ListAccess<Comment> getComments(long taskId);

  Comment addCommentToTaskId(long id, String username, String comment) throws TaskNotFoundException;

  void deleteCommentById(long commentId) throws CommentNotFoundException;

  ListAccess<Task> getIncomingTasks(String username, OrderBy orderBy);

  ListAccess<Task> getTodoTasks(String username, List<Long> projectIds, OrderBy orderBy, Date fromDueDate, Date toDueDate);

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

  Task findTaskByActivityId(String id);
}
