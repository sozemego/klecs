package com.soze.klecs.component;

import com.soze.klecs.engine.ComponentContainer;
import com.soze.klecs.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ComponentContainerTest {

  private ComponentContainer componentContainer;

  @Before
  public void setup() {
    componentContainer = new ComponentContainer();
  }

  @Test
  public void testAddComponent() {
    assertTrue(componentContainer.addComponent(1L, "ITS A STRING"));
  }

  @Test
  public void testAddSameComponentClassTwice() {
    assertTrue(componentContainer.addComponent(1L, "A STRING"));
    //we are adding a different string, but it's the same class as previously added component
    assertFalse(componentContainer.addComponent(1L, "ANOTHER STRING"));
  }

  @Test
  public void testGetComponent() {
    assertTrue(componentContainer.addComponent(1L, "A STRING"));
    Optional<String> component = componentContainer.getComponent(1L, String.class);
    assertTrue(component.isPresent());
  }

  @Test
  public void testGetNonExistentComponent() {
    assertTrue(componentContainer.addComponent(1L, "A STRING"));
    Optional<Integer> component = componentContainer.getComponent(1L, Integer.class);
    assertFalse(component.isPresent());
  }

  @Test
  public void testGetNodeFromEntity() {
    String component1 = "Cool";
    Integer component2 = 5;
    Object component3 = new Object();
    componentContainer.addComponent(1L, component1);
    componentContainer.addComponent(1L, component2);
    componentContainer.addComponent(1L, component3);

    Node node = Node.of(Arrays.asList(component1.getClass(), component2.getClass(), component3.getClass()));

    Map<Class<?>, Object> components = componentContainer.getNodeComponents(1L, node);
    assertTrue(components.containsKey(component1.getClass()));
    assertTrue(components.containsKey(component2.getClass()));
    assertTrue(components.containsKey(component3.getClass()));
    assertTrue(components.get(component1.getClass()) == component1);
    assertTrue(components.get(component2.getClass()) == component2);
    assertTrue(components.get(component3.getClass()) == component3);
  }

  @Test
  public void testGetNodeFromEntityDifferentNode() {
    String component1 = "Cool";
    Integer component2 = 5;

    Node node = Node.of(Arrays.asList(component1.getClass(), component2.getClass()));
    Node sameComponentsDifferentInstance = Node.of(Arrays.asList(component1.getClass(), component2.getClass()));
    assertFalse(componentContainer.getNodeComponents(1L, node) == componentContainer.getNodeComponents(1L, sameComponentsDifferentInstance));
  }

  @Test
  public void testRemoveComponent() {
    String component1 = "Cool";
    Integer component2 = 5;

    componentContainer.addComponent(1, component1);
    componentContainer.addComponent(1, component2);
    assertTrue(componentContainer.getComponent(1, component1.getClass()).isPresent());
    assertTrue(componentContainer.getComponent(1, component2.getClass()).isPresent());

    componentContainer.removeComponent(1, component2.getClass());
    assertTrue(componentContainer.getComponent(1, component1.getClass()).isPresent());
    assertFalse(componentContainer.getComponent(1, component2.getClass()).isPresent());
  }

  @Test
  public void testRemovingComponentFromNodeEntityShouldStopReturningComponentsForThisNode() {
    String component1 = "Cool";
    Integer component2 = 5;
    List<?> component3 = new ArrayList<>();

    componentContainer.addComponent(1, component1);
    componentContainer.addComponent(1, component2);
    componentContainer.addComponent(1, component3);

    Node node = Node.of(component1.getClass(), component2.getClass(), component3.getClass());

    Map<Class<?>, Object> components = componentContainer.getNodeComponents(1, node);
    assertEquals(3, components.size());

    componentContainer.removeComponent(1, String.class);
    components = componentContainer.getNodeComponents(1, node);
    assertEquals(0, components.size());
  }

  @Test
  public void testGetEntitiesByNodeNoComponents() {
    Node node = Node.of(String.class, Integer.class);
    List<Long> ids = componentContainer.getEntitiesByNode(node);
    assertEquals(0, ids.size());
  }

  @Test
  public void testGetEntitiesByNodeOneEntity() {
    Node node = Node.of(String.class, Integer.class);
    componentContainer.addComponent(1, "A");
    componentContainer.addComponent(1, 5);
    List<Long> ids = componentContainer.getEntitiesByNode(node);
    assertEquals(1, ids.size());
  }

  @Test
  public void testGetEntitiesByNodeManyEntities() {
    Node node = Node.of(String.class, Integer.class);
    int entities = 25;
    for(int i = 0; i < entities; i++) {
      componentContainer.addComponent(i, "A");
      componentContainer.addComponent(i, 5);
    }

    List<Long> ids = componentContainer.getEntitiesByNode(node);
    assertEquals(entities, ids.size());
  }


}