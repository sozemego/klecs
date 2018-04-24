package com.soze.klecs.engine;

import com.soze.klecs.entity.Entity;
import com.soze.klecs.entity.EntityFactory;
import com.soze.klecs.node.Node;
import com.soze.klecs.system.EntitySystem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

  @Test(expected = IllegalStateException.class)
  public void testAddEntityTwiceWhileUpdating() {
    EntitySystem system = new EntitySystem() {
      @Override
      public void update(float delta) {
        Entity entity = engine.getEntityFactory().createEntity();
        engine.addEntity(entity);
        engine.addEntity(entity);
      }

      @Override
      public Engine getEngine() {
        return null;
      }
    };
    engine.addSystem(system);
    engine.update(0);
  }

  @Test
  public void testCreateEntityAddLater() {
    final Entity entity = engine.getEntityFactory().createEntity();
    assertEquals(engine.getAllEntities().size(), 0);
    engine.addEntity(entity);
    assertEquals(engine.getAllEntities().size(), 1);
  }

  @Test
  public void testCreateEntityAddLaterShouldNotBeUpdatedBefore() {
    final Entity entity = engine.getEntityFactory().createEntity();

    final List<String> component = new ArrayList<>();

    entity.addComponent(component);

    final EntitySystem tempSystem = new EntitySystem() {
      @Override
      public void update(float delta) {
        final List<Entity> entities = getEngine().getAllEntities();
        for(final Entity entity: entities) {
          final List<String> component = (ArrayList)entity.getComponent(ArrayList.class);
          component.add("MORE");
        }
      }

      @Override
      public Engine getEngine() {
        return engine;
      }
    };
    engine.addSystem(tempSystem);
    assertEquals(engine.getAllEntities().size(), 0);
    engine.update(0);
    assertTrue(component.isEmpty());

    engine.addEntity(entity);
    assertEquals(engine.getAllEntities().size(), 1);
    engine.update(0);
    assertFalse(component.isEmpty());
  }

  @Test
  public void testCreateEntityAddLaterShouldNotBeUpdatedBeforeByNode() {
    final Entity entity = engine.getEntityFactory().createEntity();

    final List<String> component = new ArrayList<>();

    entity.addComponent(component);

    final Node node = Node.of(ArrayList.class);

    final EntitySystem tempSystem = new EntitySystem() {
      @Override
      public void update(float delta) {
        final List<Entity> entities = getEngine().getEntitiesByNode(node);
        for(final Entity entity: entities) {
          final List<String> component = (ArrayList) entity.getComponent(ArrayList.class);
          component.add("MORE");
        }
      }

      @Override
      public Engine getEngine() {
        return engine;
      }
    };
    engine.addSystem(tempSystem);
    assertEquals(engine.getAllEntities().size(), 0);
    engine.update(0);
    assertTrue(component.isEmpty());

    engine.addEntity(entity);
    assertEquals(engine.getAllEntities().size(), 1);
    engine.update(0);
    assertFalse(component.isEmpty());
  }

  @Test
  public void testAddEntityListener() throws Exception {
    final List<EntityEvent> entityEvents = new ArrayList<>();
    final Entity entity = engine.getEntityFactory().createEntity();
    engine.addEntityEventListener(e -> entityEvents.add((EntityEvent) e));
    engine.addEntity(entity);
    engine.update(0);
    assertEquals(1, entityEvents.size());
    assertEquals(entity.getId(), entityEvents.get(0).getEntity().getId());
  }

  @Test(expected = IllegalStateException.class)
  public void testAddEntityListenerWhileUpdating() throws Exception {
    final EntitySystem tempSystem = new EntitySystem() {
      @Override
      public void update(float delta) {
        engine.addEntityEventListener(e -> System.out.println(e));
      }

      @Override
      public Engine getEngine() {
        return engine;
      }
    };
    engine.addSystem(tempSystem);
    engine.update(0);
  }

  @Test(expected = IllegalStateException.class)
  public void testRemoveEntityListenerWhileUpdating() throws Exception {
    final EntitySystem tempSystem = new EntitySystem() {
      @Override
      public void update(float delta) {
        engine.removeEntityEventListener(e -> System.out.println(e));
      }

      @Override
      public Engine getEngine() {
        return engine;
      }
    };
    engine.addSystem(tempSystem);
    engine.update(0);
  }

  @Test
  public void addRemoveEntityEventListener() throws Exception {
    final List<EntityEvent> entityEvents = new ArrayList<>();
    final Entity entity = engine.getEntityFactory().createEntity();
    engine.addEntityEventListener(e -> entityEvents.add((EntityEvent) e));
    engine.addEntity(entity);
    engine.update(0);
    assertEquals(1, entityEvents.size());
    assertTrue(entityEvents.get(0).getClass() == AddedEntityEvent.class);
    assertEquals(entity.getId(), entityEvents.get(0).getEntity().getId());
    engine.removeEntity(entity.getId());
    engine.update(0);
    assertEquals(2, entityEvents.size());
    assertTrue(entityEvents.get(1).getClass() == RemovedEntityEvent.class);
    assertEquals(entity.getId(), entityEvents.get(0).getEntity().getId());
  }

}