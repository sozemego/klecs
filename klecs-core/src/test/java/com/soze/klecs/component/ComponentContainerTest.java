package com.soze.klecs.component;

import com.soze.klecs.engine.ComponentContainer;
import com.soze.klecs.engine.EntityComponentContainer;
import com.soze.klecs.node.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

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
    assertNotNull(componentContainer.getComponent(1L, String.class));
  }

  @Test
  public void testGetNonExistentComponent() {
    assertTrue(componentContainer.addComponent(1L, "A STRING"));
    assertNull(componentContainer.getComponent(1L, Integer.class));
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

    EntityComponentContainer components = componentContainer.getNodeComponents(1L, node);
    assertTrue(components.hasComponent(component1.getClass()));
    assertTrue(components.hasComponent(component2.getClass()));
    assertTrue(components.hasComponent(component3.getClass()));
    assertTrue(components.getComponent(component1.getClass()) == component1);
    assertTrue(components.getComponent(component2.getClass()) == component2);
    assertTrue(components.getComponent(component3.getClass()) == component3);
  }

  @Test
  public void testRemoveComponent() {
    String component1 = "Cool";
    Integer component2 = 5;

    componentContainer.addComponent(1, component1);
    componentContainer.addComponent(1, component2);
    assertTrue(componentContainer.getComponent(1, component1.getClass()) != null);
    assertTrue(componentContainer.getComponent(1, component2.getClass()) != null);

    componentContainer.removeComponent(1, component2.getClass());
    assertTrue(componentContainer.getComponent(1, component1.getClass()) != null);
    assertFalse(componentContainer.getComponent(1, component2.getClass()) != null);
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

    EntityComponentContainer components = componentContainer.getNodeComponents(1, node);
    assertEquals(3, components.componentCount());

    componentContainer.removeComponent(1, String.class);
    components = componentContainer.getNodeComponents(1, node);
    assertEquals(0, components.componentCount());
  }

  @Test
  public void testGetEntitiesByNodeNoComponents() {
    Node node = Node.of(String.class, Integer.class);
    List<Long> ids = (List<Long>) componentContainer.getEntitiesByNode(node);
    assertEquals(0, ids.size());
  }

  @Test
  public void testGetEntitiesByNodeOneEntity() {
    Node node = Node.of(String.class, Integer.class);
    componentContainer.addComponent(1, "A");
    componentContainer.addComponent(1, 5);
    List<Long> ids = (List<Long>) componentContainer.getEntitiesByNode(node);
    assertEquals(1, ids.size());
  }

  @Test
  public void testGetEntitiesByNodeManyEntities() {
    Node node = Node.of(String.class, Integer.class);
    int entities = 25;
    for (int i = 0; i < entities; i++) {
      componentContainer.addComponent(i, "A");
      componentContainer.addComponent(i, 5);
    }

    List<Long> ids = (List<Long>) componentContainer.getEntitiesByNode(node);
    assertEquals(entities, ids.size());
  }

  @Test
  public void testGetComponentByParentClass() {
    String strComponent = "A";
    componentContainer.addComponent(1, strComponent);

    Object component = componentContainer.getComponent(1, Object.class);
    Assert.assertNull(component);
  }

  @Test
  public void testGetComponentParentByParentClass() {
    String strComponent = "A";
    componentContainer.addComponent(1, strComponent);

    Object component = componentContainer.getComponentByParent(1, Object.class);
    Assert.assertEquals("A", component);
  }

  @Test
  public void testGetComponentByParentManyChildren() {
    String strComponent = "A";
    componentContainer.addComponent(1, strComponent);
    List<Object> listComponent = new ArrayList<>();
    componentContainer.addComponent(1, listComponent);

    Object component = componentContainer.getComponentByParent(1, Object.class);
    /**
     * Order of retrieved component by parent is undefined
     */
    Assert.assertTrue("A" == component || listComponent == component);
  }

}