package com.soze.klecs.engine;

import com.soze.klecs.component.ComponentContainer;
import com.soze.klecs.entity.Entity;
import com.soze.klecs.entity.EntityFactory;
import com.soze.klecs.system.EntitySystem;

import java.util.*;

/**
 * Represents an ECS engine, which handles updates to systems and stores entities.
 */
public class Engine {

  private final ComponentContainer componentContainer = new ComponentContainer();
  private final EntityFactory entityFactory = new EntityFactory(this, componentContainer);
  private final List<EntitySystem> systems = new ArrayList<>();

  /**
   * Entities already added to the engine.
   */
  private final Map<Long, Entity> entities = new HashMap<>();

  /**
   * Entities waiting to be added to the engine.
   */
  private final Map<Long, Entity> addEntityQueue = new HashMap<>();

  /**
   * Entities waiting to be removed from the engine.
   */
  private final Map<Long, Entity> removeEntityQueue = new HashMap<>();


  private boolean updating = false;

  /**
   * Returns an EntityFactory for this engine. This method always returns the same instance
   * of the factory.
   */
  public EntityFactory getEntityFactory() {
    return entityFactory;
  }

  public void addSystem(final EntitySystem system) {
    Objects.requireNonNull(system);
    systems.add(system);
  }

  /**
   * Attempts to find a system with given class.
   * If you added two systems with the same class, it will only return the first one.
   */
  public Optional<EntitySystem> getSystem(final Class<? extends EntitySystem> systemClass) {
    for(EntitySystem system: systems) {
      if(system.getClass().equals(systemClass)) {
        return Optional.of(system);
      }
    }
    return Optional.empty();
  }

  /**
   * Removes all systems with this class.
   */
  public void removeSystem(final Class<? extends EntitySystem> systemClass) {
    Objects.requireNonNull(systemClass);
    systems.removeIf(system -> system.getClass().equals(systemClass));
  }

  /**
   * Adds an entity to this system. If the engine is updating, the entity is added after updating has finished.
   * Otherwise it will added immediately.
   * @throws IllegalStateException if entity with given id is already added to this system
   *                                  (already added or waiting to be added)
   */
  public void addEntity(final Entity entity) {
    boolean alreadyAdded;
    if(!updating) {
      alreadyAdded = entities.putIfAbsent(entity.getId(), entity) != null;
    } else {
      alreadyAdded = addEntityQueue.putIfAbsent(entity.getId(), entity) != null || entities.containsKey(entity.getId());
    }
    if(alreadyAdded) {
      throw new IllegalStateException("Entity with id: " + entity.getId() + " already added.");
    }
  }

  /**
   * Removes an entity with given id from this engine.
   * The entity is removed immediately if the engine is not updating, otherwise after the update method has finished.
   * @param id
   * @throws IllegalStateException if entity with given id is not in the engine
   */
  public void removeEntity(final long id) {
    boolean notAdded;
    if(!updating) {
      notAdded = entities.remove(id) == null;
    } else {
      notAdded = addEntityQueue.remove(id) == null || !entities.containsKey(id);
    }
    if(notAdded) {
      throw new IllegalStateException("Entity with id: " + id + " not added to this engine.");
    }
  }

  /**
   * Returns all entities added to the engine.
   * This does not include entities waiting to be added.
   * @return
   */
  public List<Entity> getAllEntities() {
    return Collections.unmodifiableList(new ArrayList<>(entities.values()));
  }

  /**
   * Calls shouldUpdate on all systems and for those which return true,
   * calls the update method.
   * @param delta time in seconds since the last update
   */
  public void update(float delta) {
    if(updating) {
      throw new IllegalStateException("Engine is already updating");
    }

    updating = true;

    for(EntitySystem system: systems) {
      if(system.shouldUpdate(delta)) {
        system.update(delta);
      }
    }

    updating = false;
  }


}
