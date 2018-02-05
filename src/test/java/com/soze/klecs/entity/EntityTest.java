package com.soze.klecs.entity;

import com.soze.klecs.engine.Engine;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntityTest {

  private EntityFactory entityFactory;

  @Before
  public void setup() {
    entityFactory = new Engine().getEntityFactory();
  }

  @Test
  public void testAddComponent() {
    Entity entity = entityFactory.createEntity();
    assertTrue(entity.addComponent("ITS A STRING"));
  }

  @Test
  public void testAddSameComponentClassTwice() {
    Entity entity = entityFactory.createEntity();
    assertTrue(entity.addComponent("A STRING"));
    //we are adding a different string, but it's the same class as previously added component
    assertFalse(entity.addComponent("ANOTHER STRING"));
  }

  @Test
  public void testGetComponent() {
    Entity entity = entityFactory.createEntity();
    assertTrue(entity.addComponent("A STRING"));
    Optional<String> component = entity.getComponent(String.class);
    assertTrue(component.isPresent());
  }

  @Test
  public void testGetNonExistentComponent() {
    Entity entity = entityFactory.createEntity();
    assertTrue(entity.addComponent("A STRING"));
    Optional<Integer> component = entity.getComponent(Integer.class);
    assertFalse(component.isPresent());
  }

}