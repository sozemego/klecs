package com.soze.klecs.entity;

import com.soze.klecs.engine.Engine;
import com.soze.klecs.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
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

  @Test
  public void testGetNodeFromEntity() {
    Entity entity = entityFactory.createEntity();

    String component1 = "Cool";
    Integer component2 = 5;
    Object component3 = new Object();
    entity.addComponent(component1);
    entity.addComponent(component2);
    entity.addComponent(component3);

    Node node = Node.of(Arrays.asList(component1.getClass(), component2.getClass(), component3.getClass()));

    Map<Class<?>, Object> components = entity.getNodeComponents(node);
    assertTrue(components.containsKey(component1.getClass()));
    assertTrue(components.containsKey(component2.getClass()));
    assertTrue(components.containsKey(component3.getClass()));
    assertTrue(components.get(component1.getClass()) == component1);
    assertTrue(components.get(component2.getClass()) == component2);
    assertTrue(components.get(component3.getClass()) == component3);
  }

  @Test
  public void testGetNodeFromEntityDifferentNode() {
    Entity entity = entityFactory.createEntity();

    String component1 = "Cool";
    Integer component2 = 5;

    Node node = Node.of(Arrays.asList(component1.getClass(), component2.getClass()));
    Node sameComponentsDifferentInstance = Node.of(Arrays.asList(component1.getClass(), component2.getClass()));
    assertFalse(entity.getNodeComponents(node) == entity.getNodeComponents(sameComponentsDifferentInstance));

  }


}