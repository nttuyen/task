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
package org.exoplatform.task.dao.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.task.dao.OrderBy;
import org.exoplatform.task.dao.TaskHandler;
import org.exoplatform.task.dao.TaskQuery;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.util.TaskUtil;

/**
 * Created by The eXo Platform SAS
 * Author : Thibault Clement
 * tclement@exoplatform.com
 * 4/8/15
 */
public class TaskDAOImpl extends GenericDAOJPAImpl<Task, Long> implements TaskHandler {

  private EntityManagerService entityService;

  public TaskDAOImpl(EntityManagerService entityService) {
    this.entityService = entityService;
  }

  @Override
  public EntityManager getEntityManager() {
    return entityService.getEntityManager();
  }



  @Override
  public List<Task> findByUser(String user) {

    List<String> memberships = new ArrayList<String>();
    memberships.add(user);

    return  findAllByMembership(user, memberships);
  }

  public List<Task> findAllByMembership(String user, List<String> memberships) {

    Query query = getEntityManager().createNamedQuery("Task.findByMemberships", Task.class);
    query.setParameter("userName", user);
    query.setParameter("memberships", memberships);

    return query.getResultList();
  }

  @Override
  public ListAccess<Task> findTasks(TaskQuery query) {
    EntityManager em = getEntityManager();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery q = cb.createQuery();

    Root<Task> task = q.from(Task.class);

    List<Predicate> predicates = this.buildPredicate(query, task, cb);

    if (predicates == null) {
      return EMPTY;
    }

    if(predicates.size() > 0) {
      Iterator<Predicate> it = predicates.iterator();
      Predicate p = it.next();
      while(it.hasNext()) {
        p = cb.and(p, it.next());
      }
      q.where(p);
    }

    //
    q.select(cb.count(task));
    final TypedQuery<Long> countQuery = em.createQuery(q);

    //
    q.select(task);

    if(query.getOrderBy() != null && !query.getOrderBy().isEmpty()) {
      List<OrderBy> orderBies = query.getOrderBy();
      Order[] orders = new Order[orderBies.size()];
      for(int i = 0; i < orders.length; i++) {
        OrderBy orderBy = orderBies.get(i);
        Path p = task.get(orderBy.getFieldName());
        orders[i] = orderBy.isAscending() ? cb.asc(p) : cb.desc(p);
      }
      q.orderBy(orders);
    }

    final TypedQuery<Task> selectQuery = em.createQuery(q);

    return new JPAQueryListAccess<Task>(Task.class, countQuery, selectQuery);
  }

  @Override
  public <T> List<T> selectTaskField(TaskQuery query, String fieldName) {
    EntityManager em = getEntityManager();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery q = cb.createQuery();

    Root<Task> task = q.from(Task.class);

    List<Predicate> predicates = this.buildPredicate(query, task, cb);

    if (predicates == null) {
      return Collections.emptyList();
    }

    if(predicates.size() > 0) {
      Iterator<Predicate> it = predicates.iterator();
      Predicate p = it.next();
      while(it.hasNext()) {
        p = cb.and(p, it.next());
      }
      q.where(p);
    }

    //
    Path path = null;
    if (fieldName.indexOf('.') != -1) {
      String[] strs = fieldName.split("\\.");
      for (String s : strs) {
        if (s.isEmpty()) {
          break;
        }
        if (path == null) {
          path = task.get(s);
        } else {
          path = path.get(s);
        }
      }
    } else {
      path = task.get(fieldName);
    }
    q.select(path).distinct(true);

    if(query.getOrderBy() != null && !query.getOrderBy().isEmpty()) {
      List<OrderBy> orderBies = query.getOrderBy();
      List<Order> orders = new ArrayList<Order>();
      for(OrderBy orderBy : orderBies) {
        if (!orderBy.getFieldName().equals(fieldName)) {
          continue;
        }
        Path p = task.get(orderBy.getFieldName());
        orders.add(orderBy.isAscending() ? cb.asc(p) : cb.desc(p));
      }
      if (!orders.isEmpty()) {
        q.orderBy(orders);
      }
    }

    final TypedQuery<T> selectQuery = em.createQuery(q);
    return selectQuery.getResultList();
  }

  private List<Predicate> buildPredicate(TaskQuery query, Root<Task> task, CriteriaBuilder cb) {
    List<Predicate> predicates = new ArrayList<Predicate>();

    // Incoming?
    final String username = query.getUsername();
    if (query.getIsIncoming() != null && query.getIsIncoming()) {
      // Case query for IN-COMING tasks
      Join<Task, String> join = task.join("coworker", JoinType.LEFT);

      Predicate nullStatus = cb.isNull(task.get("status"));
      Predicate isAssingee = cb.equal(task.get("assignee"), username);
      Predicate isCreator = cb.equal(task.get("createdBy"), username);
      Predicate isCoworker = cb.equal(join, username);

      predicates.add(cb.and(nullStatus, cb.or(isAssingee, isCreator, isCoworker)));


    } else if (query.getIsTodo() != null && query.getIsTodo()) {
      // Case TO-DO tasks
      Predicate isAssignee = cb.equal(task.get("assignee"), username);
      predicates.add(isAssignee);
    }

    //.
    if(query.getTaskId() > 0) {
      predicates.add(cb.equal(task.get("id"), query.getTaskId()));
    }

    //
    if (query.getTitle() != null && !query.getTitle().isEmpty()) {
      predicates.add(cb.like(task.<String>get("title"), "%" + query.getTitle() + "%"));
    }

    //
    if (query.getDescription() != null && !query.getDescription().isEmpty()) {
      predicates.add(cb.like(task.<String>get("description"), '%' + query.getDescription() + '%'));
    }

    //
    if(query.getKeyword() != null && !query.getKeyword().isEmpty()) {
      List<Predicate> keyConditions = new LinkedList<Predicate>();
      for (String k : query.getKeyword().split(" ")) {
        if (!(k = k.trim()).isEmpty()) {
          k = "%" + k.toLowerCase() + "%";
          keyConditions.add(cb.or(
                  cb.like(cb.lower(task.<String>get("title")), k),
                  cb.like(cb.lower(task.<String>get("description")), k),
                  cb.like(cb.lower(task.<String>get("assignee")), k)
          ));
        }
      }
      predicates.add(cb.or(keyConditions.toArray(new Predicate[keyConditions.size()])));
    }

    //
    Predicate assignPred = null;
    if (query.getAssignee() != null && !query.getAssignee().isEmpty()) {
      assignPred = cb.like(task.<String>get("assignee"), '%' + query.getAssignee() + '%');
    }

    //
    Predicate msPred = null;
    if (query.getMemberships() != null) {
      msPred = cb.or(task.join("status").join("project").join("manager", JoinType.LEFT).in(query.getMemberships()),
              task.join("status").join("project").join("participator", JoinType.LEFT).in(query.getMemberships()));
    }

    //
    Predicate projectPred = null;
    if (query.getProjectIds() != null) {
      if (query.getProjectIds().isEmpty()) {
        //TODO: How is this case?
        return null;
      } else if (query.getProjectIds().size() == 1 && query.getProjectIds().get(0) == 0) {
        projectPred = cb.isNotNull(task.get("status"));
      } else {
        projectPred = task.get("status").get("project").get("id").in(query.getProjectIds());
      }
    }

    //
    List<Predicate> tmp = new LinkedList<Predicate>();
    for (String or : query.getOrFields()) {
      if (or.equals(TaskUtil.ASSIGNEE)) {
        tmp.add(assignPred);
      }
      if (or.equals(TaskUtil.MEMBERSHIP)) {
        tmp.add(msPred);
      }
      if (or.equals(TaskUtil.PROJECT)) {
        tmp.add(projectPred);
      }
    }

    if (!tmp.isEmpty()) {
      predicates.add(cb.or(tmp.toArray(new Predicate[tmp.size()])));
    }

    if (!query.getOrFields().contains(TaskUtil.ASSIGNEE) && assignPred != null) {
      predicates.add(assignPred);
    }
    if (!query.getOrFields().contains(TaskUtil.MEMBERSHIP) && msPred != null) {
      predicates.add(msPred);
    }
    if (!query.getOrFields().contains(TaskUtil.PROJECT) && projectPred != null) {
      predicates.add(projectPred);
    }

    //
    if (query.getCompleted() != null) {
      if (query.getCompleted()) {
        predicates.add(cb.equal(task.get("completed"), query.getCompleted()));
      } else {
        predicates.add(cb.notEqual(task.get("completed"), !query.getCompleted()));
      }
    }

    //
    if (query.getCalendarIntegrated() != null) {
      if (query.getCalendarIntegrated()) {
        predicates.add(cb.equal(task.get("calendarIntegrated"), query.getCalendarIntegrated()));
      } else {
        predicates.add(cb.notEqual(task.get("calendarIntegrated"), !query.getCalendarIntegrated()));
      }
    }

    //
    if (query.getStartDate() != null) {
      predicates.add(cb.greaterThanOrEqualTo(task.<Date>get("endDate"), query.getStartDate()));
    }
    if (query.getEndDate() != null) {
      predicates.add(cb.lessThanOrEqualTo(task.<Date>get("startDate"), query.getEndDate()));
    }

    //
    if (query.getDueDateFrom() != null || query.getDueDateTo() != null) {
      predicates.add(cb.isNotNull(task.get("dueDate")));
    }
    if (query.getDueDateFrom() != null) {
      predicates.add(cb.greaterThanOrEqualTo(task.<Date>get("dueDate"), query.getDueDateFrom()));
    }
    if (query.getDueDateTo() != null) {
      predicates.add(cb.lessThanOrEqualTo(task.<Date>get("dueDate"), query.getDueDateTo()));
    }

    //
    if (query.getStatus() != null) {
      predicates.add(cb.equal(task.get("status").<Long>get("id"), query.getStatus().getId()));
    }

    //
    if (query.getNullField() != null) {
      predicates.add(cb.isNull(task.get(query.getNullField())));
    }

    return predicates;
  }

  @Override
  public Task findTaskByActivityId(String activityId) {
    if (activityId == null || activityId.isEmpty()) {
      return null;
    }
    EntityManager em = getEntityManager();
    Query query = em.createNamedQuery("Task.findTaskByActivityId", Task.class);
    query.setParameter("activityId", activityId);
    try {
      return (Task) query.getSingleResult();
    } catch (PersistenceException e) {
      return null;
    }
  }

  @Override
  public void updateTaskOrder(long currentTaskId, Status newStatus, long[] orders) {
      int currentTaskIndex = -1;
      for (int i = 0; i < orders.length; i++) {
          if (orders[i] == currentTaskId) {
              currentTaskIndex = i;
              break;
          }
      }
      if (currentTaskIndex == -1) {
          return;
      }

      Task currentTask = find(currentTaskId);
      Task prevTask = null;
      Task nextTask = null;
      if (currentTaskIndex < orders.length - 1) {
          prevTask = find(orders[currentTaskIndex + 1]);
      }
      if (currentTaskIndex > 0) {
          nextTask = find(orders[currentTaskIndex - 1]);
      }

      int oldRank = currentTask.getRank();
      int prevRank = prevTask != null ? prevTask.getRank() : 0;
      int nextRank = nextTask != null ? nextTask.getRank() : 0;
      int newRank = prevRank + 1;
      if (newStatus != null && currentTask.getStatus().getId() != newStatus.getId()) {
          oldRank = 0;
          currentTask.setStatus(newStatus);
      }

      EntityManager em = getEntityManager();
      StringBuilder sql = null;

      if (newRank == 1 || oldRank == 0) {
          int increment = 1;
          StringBuilder exclude = new StringBuilder();
          if (nextRank == 0) {
              for (int i = currentTaskIndex - 1; i >= 0; i--) {
                  Task task = find(orders[i]);
                  if (task.getRank() > 0) {
                    break;
                  }
                  task.setRank(newRank + currentTaskIndex - i);
                  update(task);
                  if (exclude.length() > 0) {
                      exclude.append(',');
                  }
                  exclude.append(task.getId());
                  increment++;
              }
          }
          //Update rank of tasks have rank >= newRank with rank := rank + increment
          sql = new StringBuilder("UPDATE Task as ta SET ta.rank = ta.rank + ").append(increment)
                                .append(" WHERE ta.rank >= ").append(newRank);
          if (exclude.length() > 0) {
              sql.append(" AND ta.id NOT IN (").append(exclude.toString()).append(")");
          }

      } else if (oldRank < newRank) {
          //Update all task where oldRank < rank < newRank: rank = rank - 1
          sql = new StringBuilder("UPDATE Task as ta SET ta.rank = ta.rank - 1")
                                .append(" WHERE ta.rank > ").append(oldRank)
                                .append(" AND ta.rank < ").append(newRank);
          newRank --;
      } else if (oldRank > newRank) {
          //Update all task where newRank <= rank < oldRank: rank = rank + 1
          sql = new StringBuilder("UPDATE Task as ta SET ta.rank = ta.rank + 1")
                  .append(" WHERE ta.rank >= ").append(newRank)
                  .append(" AND ta.rank < ").append(oldRank);
          newRank ++;
      }

      if (sql != null && sql.length() > 0) {
          // Add common condition
          sql.append(" AND ta.completed = FALSE AND ta.status.id = ").append(currentTask.getStatus().getId());

          //TODO: This block code is temporary workaround because the update is require transaction
          EntityTransaction trans = em.getTransaction();
          boolean active = false;
          if (!trans.isActive()) {
            trans.begin();
            active = true;
          }

          em.createQuery(sql.toString()).executeUpdate();

          if (active) {
            trans.commit();
          }
      }
      currentTask.setRank(newRank);
      update(currentTask);
  }

  private static final ListAccess<Task> EMPTY = new ListAccess<Task>() {
    @Override
    public Task[] load(int index, int length) throws Exception, IllegalArgumentException {
      return new Task[0];
    }

    @Override
    public int getSize() throws Exception {
      return 0;
    }
  };
}

