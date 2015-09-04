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

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.task.domain.Task;

import javax.persistence.TypedQuery;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class JPAQueryListAccess<E> implements ListAccess<E> {
  private final TypedQuery<Long> countQuery;
  private final TypedQuery<E> selectQuery;

  private long size = -1;

  public JPAQueryListAccess(TypedQuery<Long> countQuery, TypedQuery<E> selectQuery) {
    this.countQuery = countQuery;
    this.selectQuery = selectQuery;
  }

  @Override
  public E[] load(int index, int length) throws Exception, IllegalArgumentException {
    return (E[])selectQuery
            .setFirstResult(index)
            .setMaxResults(length)
            .getResultList().toArray();
  }

  @Override
  public int getSize() throws Exception {
    if (size == -1) {
      size = countQuery.getSingleResult();
    }
    return (int)size;
  }
}
