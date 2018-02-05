package com.soze.klecs.entity;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class EntityTest {

  @Test
  public void testAddComponent() {
    Entity entity = new Entity(1);
    assertTrue(entity.addComponent("ITS A STRING"));
  }

  @Test
  public void testAddSameComponentClassTwice() {
    Entity entity = new Entity(1);
    assertTrue(entity.addComponent("A STRING"));
    //we are adding a different string, but it's the same class as previously added component
    assertFalse(entity.addComponent("ANOTHER STRING"));
  }

  @Test
  public void testGetComponent() {
    Entity entity = new Entity(1);
    assertTrue(entity.addComponent("A STRING"));
    Optional<String> component = entity.getComponent(String.class);
    assertTrue(component.isPresent());
  }

  @Test
  public void testGetNonExistentComponent() {
    Entity entity = new Entity(1);
    assertTrue(entity.addComponent("A STRING"));
    Optional<Integer> component = entity.getComponent(Integer.class);
    assertFalse(component.isPresent());
  }

}