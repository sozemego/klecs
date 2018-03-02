package com.soze.klecs.engine;

import com.soze.klecs.entity.Entity;
import com.soze.klecs.entity.EntityFactory;
import com.soze.klecs.node.Node;
import com.soze.klecs.system.EntitySystem;

import java.util.*;
import java.util.stream.Collectors;

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
  private final Set<Long> removeEntityQueue = new HashSet<>();


  private boolean updating = false;

  private boolean metrics = false;

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

  public void setMetrics(boolean metrics) {
    this.metrics = metrics;
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

  public List<EntitySystem> getSystems() {
    return new ArrayList<>(systems);
  }

  /**
   * Adds an entity to this system. If the engine is updating, the entity is added after updating has finished.
   * Otherwise it will added immediately.
   * @throws IllegalStateException if entity with given id is already added to this system
   *                                  (already added or waiting to be added)
   */
  public void addEntity(final Entity entity) {
    if(entities.containsKey(entity.getId()) || addEntityQueue.containsKey(entity.getId())) {
      throw new IllegalStateException("Entity with id: " + entity.getId() + " already added.");
    }

    if(!updating) {
      entities.put(entity.getId(), entity);
    } else {
      addEntityQueue.put(entity.getId(), entity);
    }
  }

  /**
   * Removes an entity with given id from this engine.
   * The entity is removed immediately if the engine is not updating, otherwise after the update method has finished.
   * @param id
   * @throws IllegalStateException if entity with given id is not in the engine
   */
  public void removeEntity(final long id) {
    if(!entities.containsKey(id)) {
      throw new IllegalStateException("Entity with id: " + id + " not added to this engine.");
    }

    if(!updating) {
      entities.remove(id);
      componentContainer.removeEntityComponents(id);
    } else {
      removeEntityQueue.add(id);
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

  public List<Entity> getEntitiesByNode(final Node node) {
    List<Long> ids = componentContainer.getEntitiesByNode(node);

    return ids
      .stream()
      .map(id -> entities.get(id))
      //this filter is a temporary workaround for entities which got components added but are not yet added to the engine
      .filter(entity -> entity != null)
      .collect(Collectors.toList());
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

    long t0 = System.nanoTime();

    //1. update all systems
    for(EntitySystem system: systems) {
      if(system.shouldUpdate(delta)) {
        long systemStartTime = System.nanoTime();
        system.update(delta);
        if(metrics) {
          System.out.println("Took " + ((System.nanoTime() - systemStartTime) / 1e9) + " s to update " + system.getClass());
        }
      }
    }

    if(metrics) {
      System.out.println("Took " + ((System.nanoTime() - t0) / 1e9) + " s to update " + systems.size() + " systems.");
    }

    updating = false;

    //2. add all entities in the queue
    List<Entity> entitiesToAdd = new ArrayList<>(addEntityQueue.values());
    addEntityQueue.clear();
    for(Entity entity: entitiesToAdd) {
      addEntity(entity);
    }

    //3. remove all entities in the queue
    List<Long> entityIdsToRemove = new ArrayList<>(removeEntityQueue);
    removeEntityQueue.clear();
    for(Long entityId: entityIdsToRemove) {
      removeEntity(entityId);
    }
  }


}
