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

package org.exoplatform.task.management.test.service;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.task.domain.Priority;
import org.exoplatform.task.management.model.TaskFilterData;
import org.exoplatform.task.management.model.ViewType;
import org.exoplatform.task.management.service.TaskFilterDataStorage;
import org.exoplatform.task.util.TaskUtil;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class TestTaskFilterDataStorage {

  private TaskFilterDataStorage filterDataStorage;

  @Before
  public void setUp() throws Exception {
    filterDataStorage = new TaskFilterDataStorage();
    filterDataStorage.setSettingService(new MockSettingService());
  }

  @Test
  public void testGetDefaultFilter() {
    TaskFilterData.Filter filter = filterDataStorage.getFilter("root", TaskFilterData.FilterKey.withProject(1L, null));
    assertNotNull(filter);
    assertFalse(filter.isEnabled());
    assertFalse(filter.isShowCompleted());
    assertEquals("", filter.getKeyword());
  }

  @Test
  public void testSaveAndGetFilter() {
    TaskFilterData.Filter filter = new TaskFilterData.Filter();
    filter.setEnabled(true);
    filter.setShowCompleted(true);
    filter.setViewType(ViewType.BOARD);
    filter.setAssignee(Arrays.asList("root"));
    filter.setPriority(Priority.HIGH);
    filter.setDue(TaskUtil.DUE.TODAY);
    filter.setStatus(1L);
    filter.setLabel(Arrays.asList(1L, 2L));
    filter.setKeyword("keyword");
    filter.setTag(Arrays.asList("tag1", "tag2"));

    TaskFilterData.FilterKey key = TaskFilterData.FilterKey.withProject(1L, null);

    filterDataStorage.saveFilter("root", key, filter);

    filter = filterDataStorage.getFilter("root", key);

    assertNotNull(filter);
    assertTrue(filter.isEnabled());
    assertTrue(filter.isShowCompleted());
    assertEquals(ViewType.BOARD, filter.getViewType());
    assertEquals(1, filter.getAssignee().size());
    assertEquals(Priority.HIGH, filter.getPriority());
    assertEquals(TaskUtil.DUE.TODAY, filter.getDue());
    assertEquals(1L, filter.getStatus().longValue());
    assertEquals(2, filter.getLabel().size());
    assertEquals(2L, filter.getLabel().get(1).longValue());
    assertEquals("keyword", filter.getKeyword());
    assertEquals(2, filter.getTag().size());
    assertEquals("tag2", filter.getTag().get(1));
  }

  private static class MockSettingService implements SettingService {
    private final Map<String, SettingValue<?>> values = new HashMap<>();
    @Override
    public void set(Context context, Scope scope, String key, SettingValue<?> value) {
      values.put(key, value);
    }

    @Override
    public void remove(Context context, Scope scope, String key) {
      values.remove(key);
    }

    @Override
    public void remove(Context context, Scope scope) {
      values.clear();
    }

    @Override
    public void remove(Context context) {
      values.clear();
    }

    @Override
    public SettingValue<?> get(Context context, Scope scope, String key) {
      return values.get(key);
    }
  }
}
