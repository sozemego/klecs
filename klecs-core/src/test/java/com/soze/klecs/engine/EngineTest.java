package com.soze.klecs.engine;

import com.soze.klecs.system.EntitySystem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EngineTest {

  private Engine engine;

  @Before
  public void setup() {
    engine = new Engine();
  }

  @Test
  public void testAddingSystem() {
    List<String> asserts = new ArrayList<>();
    EntitySystem system = delta -> asserts.add("Anything");
    engine.addSystem(system);
    engine.update(0);
    assertEquals(1, asserts.size());
  }

  @Test
  public void testAddingTwoSystems() {
    List<String> asserts = new ArrayList<>();
    EntitySystem system1 = delta -> asserts.add("Anything");
    EntitySystem system2 = delta -> asserts.add("Anything");
    engine.addSystem(system1);
    engine.addSystem(system2);
    engine.update(0);
    assertEquals(2, asserts.size());
  }

  @Test
  public void testGetSystem() {
    EntitySystem system1 = delta -> {};
    engine.addSystem(system1);
    assertEquals(system1, engine.getSystem(system1.getClass()).get());
  }

  @Test
  public void testGetTwoSystems() {
    EntitySystem system1 = delta -> {};
    EntitySystem system2 = delta -> {};
    engine.addSystem(system1);
    engine.addSystem(system2);
    assertEquals(system1, engine.getSystem(system1.getClass()).get());
    assertEquals(system2, engine.getSystem(system2.getClass()).get());
  }

  @Test
  public void testRemoveSystem() {
    EntitySystem system1 = delta -> {};
    engine.addSystem(system1);
    assertEquals(system1, engine.getSystem(system1.getClass()).get());
    engine.removeSystem(system1.getClass());
    assertFalse(engine.getSystem(system1.getClass()).isPresent());
  }

  @Test
  public void testRemoveTwoSystems() {
    EntitySystem system1 = delta -> {};
    EntitySystem system2 = delta -> {};
    engine.addSystem(system1);
    engine.addSystem(system2);
    assertEquals(system1, engine.getSystem(system1.getClass()).get());
    assertEquals(system2, engine.getSystem(system2.getClass()).get());
    engine.removeSystem(system1.getClass());
    assertFalse(engine.getSystem(system1.getClass()).isPresent());
    assertEquals(system2, engine.getSystem(system2.getClass()).get());
    engine.removeSystem(system2.getClass());
    assertFalse(engine.getSystem(system2.getClass()).isPresent());
  }

}