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

package org.exoplatform.task.dao.jpa;

import java.lang.reflect.Array;
import java.util.List;

import javax.lang.model.element.Element;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Project;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.domain.TaskLog;
import org.exoplatform.task.domain.UserSetting;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class JPAQueryListAccess<E> implements ListAccess<E> {
  private final Class<E> clazz;
  private final TypedQuery<Long> countQuery;
  private final TypedQuery<E> selectQuery;

  private long size = -1;

  public JPAQueryListAccess(Class<E> clazz, TypedQuery<Long> countQuery, TypedQuery<E> selectQuery) {
    this.clazz = clazz;
    this.countQuery = countQuery;
    this.selectQuery = selectQuery;
  }

  @Override
  public E[] load(int index, int length) throws Exception, IllegalArgumentException {
    if (length > 0) {
      selectQuery.setFirstResult(index).setMaxResults(length);
    } else {
      // Load all
      selectQuery.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
    }
    List<E> list = selectQuery.getResultList();

    E[] e = (E[])Array.newInstance(clazz, list.size());
    for (int i = 0; i < e.length; i++) {
      E clone = clone(list.get(i));
      e[i] = clone != null ? clone : list.get(i);
    }
    return e;
  }

  @Override
  public int getSize() throws Exception {
    if (size == -1) {
      size = countQuery.getSingleResult();
    }
    return (int)size;
  }

  //TODO: This method is need to improvement
  private <E> E clone(E element) {
    if (element == null) {
      return element;
    } else if (element instanceof Project) {
      return (E)((Project)element).clone(false);
    }
    /*if (element == null) return null;
    if (element instanceof Task) {
      return (E)((Task)element).clone();
    } else if (element instanceof Status) {
      return (E)((Status)element).clone();
    } else if (element instanceof Project) {
      return (E)((Project)element).clone(false);
    } else if (element instanceof Comment) {
      return (E)((Comment)element).clone();
    } else if (element instanceof TaskLog) {
      return (E)((TaskLog)element).clone();
    } else if (element instanceof UserSetting) {
      return (E)((UserSetting)element).clone();
    }*/

    return element;
  }
}
