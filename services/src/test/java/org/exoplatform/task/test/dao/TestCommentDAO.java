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

package org.exoplatform.task.test.dao;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.task.dao.CommentHandler;
import org.exoplatform.task.dao.TaskHandler;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.service.DAOHandler;
import org.exoplatform.task.test.AbstractTest;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class TestCommentDAO extends AbstractTest {

  private CommentHandler commentDAO;
  private TaskHandler taskDAO;

  private final String username = "root";
  private DAOHandler taskService;

  @Before
  public void initDAOs() {
    PortalContainer container = PortalContainer.getInstance();
    
    taskService = (DAOHandler) container.getComponentInstanceOfType(DAOHandler.class);
    taskDAO = taskService.getTaskHandler();
    commentDAO = taskService.getCommentHandler();
  }

  @After
  public void cleanData() {
    commentDAO.deleteAll();
    taskDAO.deleteAll();
  }

  @Test
  public void testCreateComment() {
    Task task = newDefaultSimpleTask();
    taskDAO.create(task);

    List<Task> tasks = taskDAO.findAll();
    task = tasks.get(0);

    Comment comment = newDefaultSimpleComment(task);
    commentDAO.create(comment);

    List<Comment> comments = commentDAO.findCommentsOfTask(task, 0, 0);
    Assert.assertEquals(1, comments.size());
    comment = comments.get(0);
    Assert.assertEquals(username, comment.getAuthor());
    Assert.assertEquals(task.getId(), comment.getTask().getId());
  }

  @Test
  public void testUpdateComment() {
    // Create Task
    Task task = newDefaultSimpleTask();
    taskDAO.create(task);

    List<Task> tasks = taskDAO.findAll();
    task = tasks.get(0);

    // Create Comment
    Comment comment = newDefaultSimpleComment(task);
    commentDAO.create(comment);

    // Load comment of task
    List<Comment> comments = commentDAO.findCommentsOfTask(task, 0, 0);
    Assert.assertEquals(1, comments.size());
    comment = comments.get(0);

    // Update comment
    long id = comment.getId();
    comment.setComment("New comment content");
    commentDAO.update(comment);

    comment = commentDAO.find(id);
    Assert.assertEquals("New comment content", comment.getComment());
  }

  @Test
  public void testDeleteComment() {
    // Create Task
    Task task = newDefaultSimpleTask();
    taskDAO.create(task);

    List<Task> tasks = taskDAO.findAll();
    task = tasks.get(0);

    // Create Comment
    Comment comment = newDefaultSimpleComment(task);
    commentDAO.create(comment);

    // Load comment of task
    List<Comment> comments = commentDAO.findCommentsOfTask(task, 0, 0);
    Assert.assertEquals(1, comments.size());
    comment = comments.get(0);

    // Delete comment
    long id = comment.getId();
    commentDAO.delete(comment);

    comment = commentDAO.find(id);
    Assert.assertNull(comment);
  }

  @Test
  public void testCountComments() {
    // Create Task
    Task task = newDefaultSimpleTask();
    taskDAO.create(task);

    List<Task> tasks = taskDAO.findAll();
    task = tasks.get(0);

    // Create Comment
    final int number = 10;
    for(int i = 0; i < number; i++) {
      Comment comment = newDefaultSimpleComment(task);
      comment.setComment("Comment number " + i);
      commentDAO.create(comment);
    }

    // Assure have 10 comment in task
    long count = commentDAO.count(task);

    Assert.assertEquals(number, count);
  }

  @Test
  public void testLoadCommentWithLimit() {
    // Create Task
    Task task = newDefaultSimpleTask();
    taskDAO.create(task);

    List<Task> tasks = taskDAO.findAll();
    task = tasks.get(0);

    // Create Comment
    final int number = 10;
    for(int i = 0; i < number; i++) {
      Comment comment = newDefaultSimpleComment(task);
      comment.setComment("Comment number " + i);
      commentDAO.create(comment);
    }

    List<Comment> comments = commentDAO.findCommentsOfTask(task, 0, 5);
    Assert.assertEquals(5, comments.size());

    comments = commentDAO.findCommentsOfTask(task, 2, 5);
    Assert.assertEquals(5, comments.size());

    //. If does not pass limit number, start will be ignored
    comments = commentDAO.findCommentsOfTask(task, 2, 0);
    Assert.assertEquals(10, comments.size());

    comments = commentDAO.findCommentsOfTask(task, 8, 5);
    Assert.assertEquals(2, comments.size());
  }

  private Task newDefaultSimpleTask() {
    Task task = new Task();
    task.setTitle("Default task");
    task.setAssignee("root");
    task.setCreatedBy("root");
    task.setCreatedTime(new Date());
    return task;
  }

  private Comment newDefaultSimpleComment(Task task) {
    Comment comment = new Comment();
    comment.setAuthor(username);
    comment.setComment("Default comment");
    comment.setCreatedTime(new Date());
    comment.setTask(task);
    return comment;
  }
}
