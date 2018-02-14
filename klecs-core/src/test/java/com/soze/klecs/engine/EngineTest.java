package com.soze.klecs.engine;

import com.soze.klecs.entity.Entity;
import com.soze.klecs.entity.EntityFactory;
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

    EntitySystem system = new EntitySystem() {
      @Override
      public void update(float delta) {
        asserts.add("SOMETHING");
      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };

    engine.addSystem(system);
    engine.update(0);
    assertEquals(1, asserts.size());
  }

  @Test
  public void testAddingTwoSystems() {
    List<String> asserts = new ArrayList<>();
    EntitySystem system1 = new EntitySystem() {
      @Override
      public void update(float delta) {
        asserts.add("SOMETHING");
      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    EntitySystem system2 = new EntitySystem() {
      @Override
      public void update(float delta) {
        asserts.add("SOMETHING");
      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    engine.addSystem(system1);
    engine.addSystem(system2);
    engine.update(0);
    assertEquals(2, asserts.size());
  }

  @Test
  public void testGetSystem() {
    EntitySystem system1 = new EntitySystem() {
      @Override
      public void update(float delta) {

      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    engine.addSystem(system1);
    assertEquals(system1, engine.getSystem(system1.getClass()).get());
  }

  @Test
  public void testGetTwoSystems() {
    EntitySystem system1 = new EntitySystem() {
      @Override
      public void update(float delta) {

      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    EntitySystem system2 = new EntitySystem() {
      @Override
      public void update(float delta) {

      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    engine.addSystem(system1);
    engine.addSystem(system2);
    assertEquals(system1, engine.getSystem(system1.getClass()).get());
    assertEquals(system2, engine.getSystem(system2.getClass()).get());
  }

  @Test
  public void testRemoveSystem() {
    EntitySystem system1 = new EntitySystem() {
      @Override
      public void update(float delta) {

      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    engine.addSystem(system1);
    assertEquals(system1, engine.getSystem(system1.getClass()).get());
    engine.removeSystem(system1.getClass());
    assertFalse(engine.getSystem(system1.getClass()).isPresent());
  }

  @Test
  public void testRemoveTwoSystems() {
    EntitySystem system1 = new EntitySystem() {
      @Override
      public void update(float delta) {

      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    EntitySystem system2 = new EntitySystem() {
      @Override
      public void update(float delta) {

      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
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

  @Test
  public void testAddEntity() {
    EntityFactory entityFactory = engine.getEntityFactory();
    Entity entity = entityFactory.createEntity();
    engine.addEntity(entity);
    assertEquals(engine.getAllEntities().size(), 1);
  }

  @Test
  public void testAddFiveEntities() {
    EntityFactory entityFactory = engine.getEntityFactory();
    entityFactory.createEntityAndAddToEngine();
    entityFactory.createEntityAndAddToEngine();
    entityFactory.createEntityAndAddToEngine();
    entityFactory.createEntityAndAddToEngine();
    entityFactory.createEntityAndAddToEngine();
    assertEquals(engine.getAllEntities().size(), 5);
  }

  @Test
  public void testRemoveEntity() {
    EntityFactory entityFactory = engine.getEntityFactory();
    Entity entity = entityFactory.createEntity();
    engine.addEntity(entity);
    assertEquals(engine.getAllEntities().size(), 1);
    engine.removeEntity(entity.getId());
    assertEquals(engine.getAllEntities().size(), 0);
  }

  @Test(expected = IllegalStateException.class)
  public void testAlreadyAddedEntity() {
    EntityFactory entityFactory = engine.getEntityFactory();
    Entity entity = entityFactory.createEntity();
    engine.addEntity(entity);
    engine.addEntity(entity);
  }

  @Test(expected = IllegalStateException.class)
  public void testUpdateAlreadyUpdatingEngine() {
    EntitySystem system = new EntitySystem() {
      @Override
      public void update(float delta) {
        engine.update(delta);
      }

      @Override
      public Engine getEngine() {
        return engine;
      }
    };
    engine.addSystem(system);
    engine.update(0);
  }

  @Test(expected = IllegalStateException.class)
  public void testRemoveAlreadyRemovedEntity() {
    engine.removeEntity(25);
  }

  @Test
  public void testAddEntityWhileUpdating() {
    EntitySystem system = new EntitySystem() {
      @Override
      public void update(float delta) {
        engine.addEntity(engine.getEntityFactory().createEntity());
        assertEquals(0, engine.getAllEntities().size());
      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    engine.addSystem(system);
    engine.update(0);
    assertEquals(1, engine.getAllEntities().size());
  }


}