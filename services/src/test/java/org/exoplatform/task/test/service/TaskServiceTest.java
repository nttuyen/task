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
package org.exoplatform.task.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.task.dao.CommentHandler;
import org.exoplatform.task.dao.StatusHandler;
import org.exoplatform.task.dao.TaskHandler;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.exception.CommentNotFoundException;
import org.exoplatform.task.exception.ParameterEntityException;
import org.exoplatform.task.exception.StatusNotFoundException;
import org.exoplatform.task.exception.TaskNotFoundException;
import org.exoplatform.task.service.DAOHandler;
import org.exoplatform.task.service.TaskListener;
import org.exoplatform.task.service.TaskService;
import org.exoplatform.task.service.impl.TaskEvent;
import org.exoplatform.task.service.impl.TaskEvent.Type;
import org.exoplatform.task.service.impl.TaskServiceImpl;
import org.exoplatform.task.test.TestUtils;

/**
 * Created by The eXo Platform SAS
 * Author : Thibault Clement
 * tclement@exoplatform.com
 * 6/8/15
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

  TaskService taskService;

  @Mock
  TaskHandler taskHandler;
  @Mock
  CommentHandler commentHandler;
  @Mock
  StatusHandler statusHandler;
  @Mock
  DAOHandler daoHandler;
  @Mock
  TaskListener taskListener;
  
  //ArgumentCaptors are how you can retrieve objects that were passed into a method call
  @Captor
  ArgumentCaptor<Task> taskCaptor;
  @Captor
  ArgumentCaptor<Comment> commentCaptor;
  @Captor
  ArgumentCaptor<TaskEvent> eventCaptor;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    taskService = TaskServiceImpl.createInstance(daoHandler, Arrays.asList(taskListener));

    //Mock DAO handler to return Mocked DAO

    when(daoHandler.getTaskHandler()).thenReturn(taskHandler);
    when(daoHandler.getCommentHandler()).thenReturn(commentHandler);
    when(daoHandler.getStatusHandler()).thenReturn(statusHandler);

    //Mock some DAO methods

    when(taskHandler.create(any(Task.class))).thenReturn(TestUtils.getDefaultTask());
    when(taskHandler.update(any(Task.class))).thenReturn(TestUtils.getDefaultTask());
    //Mock taskHandler.find(id) to return default task for id = TestUtils.EXISTING_TASK_ID (find(id) return null otherwise)
    when(taskHandler.find(TestUtils.EXISTING_TASK_ID)).thenReturn(TestUtils.getDefaultTask());
    when(statusHandler.find(TestUtils.EXISTING_STATUS_ID)).thenReturn(TestUtils.getDefaultStatus());
    when(commentHandler.find(TestUtils.EXISTING_COMMENT_ID)).thenReturn(TestUtils.getDefaultComment());

    Identity root = new Identity("root");
    ConversationState.setCurrent(new ConversationState(root));
  }

  @After
  public void tearDown() {
    taskService = null;
    ConversationState.setCurrent(null);
  }
  
  @Test
  public void testTaskCreatedEvent() {
    taskService.createTask(TestUtils.getDefaultTask());
    verify(taskListener, times(1)).event(eventCaptor.capture());
    
    TaskEvent event = eventCaptor.getValue();
    assertEquals(Type.CREATED, event.getType());
  }

  @Test
  public void testUpdateTaskTitle() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {

    String newTitle = "newTitle";

    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "title", new String[]{newTitle});
    //capture the object that was passed into the TaskHandler.updateTask(task) method
    //times(1) verify that the method update has been invoked only one time
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    assertEquals(newTitle, taskCaptor.getValue().getTitle());

  }

  @Test
  public void testUpdateTaskDescription() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {

    String newDescription = "This is a new description";

    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "description", new String[]{newDescription});
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    assertEquals(newDescription, taskCaptor.getValue().getDescription());

  }

  @Test
  public void testUpdateTaskCompleted() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {

    Boolean newCompleted = true;

    taskService.updateTaskCompleted(TestUtils.EXISTING_TASK_ID, newCompleted);
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    assertEquals(newCompleted, taskCaptor.getValue().isCompleted());

  }

  @Test
  public void testUpdateTaskAssignee() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {

    String newAssignee = "Tib";

    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "assignee", new String[]{newAssignee});
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    assertEquals(newAssignee, taskCaptor.getValue().getAssignee());

  }

  @Test
  public void testUpdateTaskCoworker() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {

    String[] newCoworkers =  {"Tib","Trong","Phuong","Tuyen"};

    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "coworker", newCoworkers);
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    Set<String> coworker = new HashSet<String>();
    for(String v : newCoworkers) {
      coworker.add(v);
    }
    assertEquals(coworker, taskCaptor.getValue().getCoworker());

  }

  @Test
  public void testUpdateTaskTag() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {

    String[] newTags = {"Flip","Flop"};

    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "tags", newTags);
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    Set<String> tags = new HashSet<String>();
    for(String v : newTags) {
      tags.add(v);
    }
    assertEquals(tags, taskCaptor.getValue().getTags());

  }

  @Test
  public void testUpdateTaskStatus() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {

    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "status", new String[]{String.valueOf(TestUtils.EXISTING_STATUS_ID)});
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    assertEquals(TestUtils.getDefaultStatus(), taskCaptor.getValue().getStatus());

  }

  @Test
  public void testUpdateTaskDueDate() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException, ParseException {

    String dueDate = "1989-01-19";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = sdf.parse(dueDate);

    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "dueDate", new String[]{dueDate});
    verify(taskHandler, times(1)).update(taskCaptor.capture());

    assertEquals(date, taskCaptor.getValue().getDueDate());

  }

  @Test
  public void testDeleteTaskById() throws TaskNotFoundException {
    taskService.deleteTaskById(TestUtils.EXISTING_TASK_ID);
    verify(taskHandler, times(1)).delete(taskCaptor.capture());

    assertEquals(TestUtils.EXISTING_TASK_ID, taskCaptor.getValue().getId());
  }

  @Test
  public void testCloneTaskById() throws TaskNotFoundException {

    Task defaultTask = TestUtils.getDefaultTask();

    taskService.cloneTaskById(TestUtils.EXISTING_TASK_ID);
    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskHandler, times(1)).create(taskCaptor.capture());

    assertEquals("Copy of "+defaultTask.getTitle(), taskCaptor.getValue().getTitle());
    assertEquals(defaultTask.getDescription(), taskCaptor.getValue().getDescription());
    assertEquals(defaultTask.getPriority(), taskCaptor.getValue().getPriority());
    assertEquals(defaultTask.getContext(), taskCaptor.getValue().getContext());
    assertEquals(defaultTask.getAssignee(), taskCaptor.getValue().getAssignee());
    assertEquals(defaultTask.getCoworker(), taskCaptor.getValue().getCoworker());
    assertEquals(defaultTask.getStatus(), taskCaptor.getValue().getStatus());
    assertEquals(defaultTask.getTags(), taskCaptor.getValue().getTags());
    assertEquals(defaultTask.getCreatedBy(), taskCaptor.getValue().getCreatedBy());
    //Only the createdTime must be different for the cloned task
    assertFalse(defaultTask.getCreatedTime() == taskCaptor.getValue().getCreatedTime());
    assertEquals(defaultTask.getEndDate(), taskCaptor.getValue().getEndDate());
    assertEquals(defaultTask.getStartDate(), taskCaptor.getValue().getStartDate());
    assertEquals(defaultTask.getDueDate(), taskCaptor.getValue().getDueDate());
  }

  @Test
  public void testAddCommentsByTaskId() throws TaskNotFoundException {
    String username = "Tib";
    String comment = "Bla bla bla bla bla";
    taskService.addCommentToTaskId(TestUtils.EXISTING_TASK_ID, username, comment);
    verify(commentHandler, times(1)).create(commentCaptor.capture());

    assertEquals(TestUtils.EXISTING_TASK_ID, commentCaptor.getValue().getTask().getId());
    assertEquals(username, commentCaptor.getValue().getAuthor());
    assertEquals(comment, commentCaptor.getValue().getComment());
  }

  @Test
  public void testDeleteCommentById() throws CommentNotFoundException {
    taskService.deleteCommentById(TestUtils.EXISTING_COMMENT_ID);
    verify(commentHandler, times(1)).delete(commentCaptor.capture());

    assertEquals(TestUtils.EXISTING_COMMENT_ID, commentCaptor.getValue().getId());
  }

  @Test(expected = TaskNotFoundException.class)
  public void testTaskNotFoundException() throws TaskNotFoundException {
    taskService.getTaskById(TestUtils.UNEXISTING_TASK_ID);
  }

  @Test(expected = StatusNotFoundException.class)
  public void testStatusNotFoundException() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {
    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "status", new String[]{String.valueOf(TestUtils.UNEXISTING_STATUS_ID)});
  }

  @Test(expected = CommentNotFoundException.class)
  public void testCommentNotFoundException() throws CommentNotFoundException {
    taskService.deleteCommentById(TestUtils.UNEXISTING_COMMENT_ID);
  }

  @Test(expected = ParameterEntityException.class)
  public void testWrongDateFormatException() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {
    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "dueDate", new String[]{"this-is-not-a-date"});
  }

  @Test(expected = ParameterEntityException.class)
  public void testWrongStatusException() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {
    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "status", new String[]{"this-is-not-a-long-id"});
  }

  @Test(expected = ParameterEntityException.class)
  public void testUnknownParameterException() throws ParameterEntityException, StatusNotFoundException, TaskNotFoundException {
    taskService.updateTaskInfo(TestUtils.EXISTING_TASK_ID, "status", new String[]{"this-is-not-a-know-parameter"});
  }

}