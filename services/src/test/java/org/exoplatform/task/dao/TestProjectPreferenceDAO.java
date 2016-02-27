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

package org.exoplatform.task.dao;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.task.AbstractTest;
import org.exoplatform.task.domain.ProjectPreference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class TestProjectPreferenceDAO extends AbstractTest {
  private ProjectPreferenceHandler pDAO;
  private DAOHandler daoHandler;

  @Before
  public void setup() {
    PortalContainer container = PortalContainer.getInstance();

    daoHandler = (DAOHandler) container.getComponentInstanceOfType(DAOHandler.class);
    pDAO = daoHandler.getProjectPreferenceHandler();
  }

  @After
  public void tearDown() {
    pDAO.deleteAll();
  }

  @Test
  public void testCreateProjectPreference() {
    ProjectPreference pref = new ProjectPreference();
    pref.setUsername("root");
    pref.setType(ProjectPreference.Type.PROJECT);
    pref.setRefId(1L);
    pref.getAttributes().put("key", "value");

    pDAO.create(pref);

    List<ProjectPreference> prefs = pDAO.findAll();
    assertEquals(1, prefs.size());
    assertEquals("root", prefs.get(0).getUsername());
    assertEquals(ProjectPreference.Type.PROJECT, prefs.get(0).getType());
    assertEquals(1l, prefs.get(0).getRefId());
    assertEquals("value", prefs.get(0).getAttributes().get("key"));
  }

  @Test
  public void testUpdateProjectPreference() {
    ProjectPreference pref = new ProjectPreference();
    pref.setUsername("root");
    pref.setType(ProjectPreference.Type.PROJECT);
    pref.setRefId(1L);
    pref.getAttributes().put("key", "value");
    pref.getAttributes().put("key1", "value 1");

    pDAO.create(pref);

    ProjectPreference p = pDAO.find(pref.getId());
    assertEquals(2, p.getAttributes().size());
    assertEquals("value", p.getAttributes().get("key"));
    assertEquals("value 1", p.getAttributes().get("key1"));

    p.getAttributes().remove("key");
    p.getAttributes().put("key1", "value 1 updated");
    p.getAttributes().put("key2", "value 2");

    pDAO.update(p);

    p = pDAO.find(pref.getId());
    assertEquals(2, p.getAttributes().size());
    assertEquals("value 1 updated", p.getAttributes().get("key1"));
    assertEquals("value 2", p.getAttributes().get("key2"));
    assertNull(p.getAttributes().get("key"));
  }

  @Test
  public void testFindProjectPreference() {
    ProjectPreference pref = new ProjectPreference();
    pref.setUsername("root");
    pref.setType(ProjectPreference.Type.PROJECT);
    pref.setRefId(1L);
    pref.getAttributes().put("key", "value");
    pref.getAttributes().put("key1", "value 1");

    pDAO.create(pref);

    ProjectPreference p = pDAO.find("root", ProjectPreference.Type.PROJECT, 1L);
    assertEquals(2, p.getAttributes().size());
    assertEquals("value", p.getAttributes().get("key"));
    assertEquals("value 1", p.getAttributes().get("key1"));
  }
}
