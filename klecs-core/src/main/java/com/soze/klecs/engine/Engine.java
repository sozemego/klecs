package com.soze.klecs.engine;

import com.soze.klecs.entity.Entity;
import com.soze.klecs.entity.EntityFactory;
import com.soze.klecs.node.Node;
import com.soze.klecs.system.EntitySystem;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents an ECS engine, which handles updates to systems and stores entities.
 * <p>
 * This class is not thread-safe.
 */
public class Engine {

  private final AtomicLong defaultId = new AtomicLong(1L);
  private final ComponentContainer componentContainer = new ComponentContainer();
  private final EntityFactory entityFactory;
  private final List<EntitySystem> systems = new ArrayList<>();

  /**
   * Entities already added to the engine.
   */
  private final Map<Object, Entity> entities = new HashMap<>();

  /**
   * Entities waiting to be added to the engine.
   */
  private final Map<Object, Entity> addEntityQueue = new HashMap<>();

  /**
   * Entities waiting to be removed from the engine.
   */
  private final Set<Object> removeEntityQueue = new HashSet<>();

  private final List<Consumer<EntityEvent>> entityEventListeners = new ArrayList<>();

  private boolean updating = false;

  private boolean metrics = false;

  public Engine() {
    this.entityFactory = new EntityFactory(this, componentContainer, () -> defaultId.getAndAdd(1));
  }

  public Engine(final Supplier<Object> idSupplier) {
    this.entityFactory = new EntityFactory(this, componentContainer, idSupplier);
  }

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
    for (EntitySystem system : systems) {
      if (system.getClass().equals(systemClass)) {
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
   *
   * @throws IllegalStateException if entity with given id is already added to this system
   *                               (already added or waiting to be added)
   */
  public void addEntity(final Entity entity) {
    if (entities.containsKey(entity.getId()) || addEntityQueue.containsKey(entity.getId())) {
      throw new IllegalStateException("Entity with id: " + entity.getId() + " already added.");
    }

    if (!updating) {
      entities.put(entity.getId(), entity);
      final AddedEntityEvent event = new AddedEntityEvent(entity);
      entityEventListeners.forEach(listener -> listener.accept(event));
    } else {
      addEntityQueue.put(entity.getId(), entity);
    }
    entity.setRemoved(false);
  }

  public Optional<Entity> getEntityById(final Object id) {
    return Optional.ofNullable(entities.get(id));
  }

  /**
   * Removes an entity with given id from this engine.
   * The entity is removed immediately if the engine is not updating, otherwise after the update method has finished.
   *
   * @param id
   * @throws IllegalStateException if entity with given id is not in the engine
   */
  public void removeEntity(final Object id) {
    if (!entities.containsKey(id)) {
      throw new IllegalStateException("Entity with id: " + id + " not added to this engine.");
    }

    if (!updating) {
      final Entity entity = getEntityById(id).get();
      final RemovedEntityEvent entityEvent = new RemovedEntityEvent(entity);
      entityEventListeners.forEach(listener -> listener.accept(entityEvent));
      entities.remove(id);
      componentContainer.removeEntityComponents(id);
      entity.setRemoved(true);
    } else {
      final Entity entity = getEntityById(id).get();
      entity.setRemoved(true);
      removeEntityQueue.add(id);
    }
  }

  /**
   * Returns all entities added to the engine.
   * This does not include entities waiting to be added.
   * The returned List can be modified, because it is a copy.
   */
  public List<Entity> getAllEntities() {
    return new ArrayList<>(entities.values());
  }

  /**
   * Returns all entities added to the engine.
   * This does not include entities waiting to be added.
   */
  public Collection<Entity> getAllEntitiesCollection() {
    return Collections.unmodifiableCollection(entities.values());
  }

  public List<Entity> getEntitiesByNode(final Node node) {
    final List<Object> ids = (List<Object>) componentContainer.getEntitiesByNode(node);

    return ids
             .stream()
             .map(entities::get)
             //this filter is a temporary workaround for entities which got components added but are not yet added to the engine
             .filter(Objects::nonNull)
             .collect(Collectors.toList());
  }

  public void addEntityEventListener(final Consumer<EntityEvent> listener) {
    Objects.requireNonNull(listener);
    if (updating) {
      throw new IllegalStateException("Don't add a listener when updating the engine");
    }
    this.entityEventListeners.add(listener);
  }

  public void removeEntityEventListener(final Consumer<EntityEvent> listener) {
    Objects.requireNonNull(listener);
    if (updating) {
      throw new IllegalStateException("Don't remove a listener when updating the engine");
    }
    this.entityEventListeners.remove(listener);
  }

  /**
   * Calls shouldUpdate on all systems and for those which return true,
   * calls the update method.
   *
   * @param delta time in seconds since the last update
   */
  public void update(float delta) {
    if (updating) {
      throw new IllegalStateException("Engine is already updating");
    }

    updating = true;
    final long t0 = System.nanoTime();

    try {
      //1. update all non-rendering systems
      for (EntitySystem system : systems) {
        if (!system.isRenderer() && system.shouldUpdate(delta)) {
          final long systemStartTime = System.nanoTime();
          system.update(delta);
          if (metrics) {
            System.out.println("Took " + ((System.nanoTime() - systemStartTime) / 1e9) + " s to update " + system.getClass());
          }
        }
      }

      if (metrics) {
        System.out.println("Took " + ((System.nanoTime() - t0) / 1e9) + " s to update " + systems.size() + " non-rendering systems.");
      }

    } catch (Exception e) {
      e.printStackTrace(); // don't know what to do here?
      throw e;
    } finally {
      updating = false;
    }

    //2. add all entities in the queue
    final List<Entity> entitiesToAdd = new ArrayList<>(addEntityQueue.values());
    addEntityQueue.clear();
    for (final Entity entity : entitiesToAdd) {
      addEntity(entity);
    }

    //3. remove all entities in the queue
    final List<Object> entityIdsToRemove = new ArrayList<>(removeEntityQueue);
    removeEntityQueue.clear();
    for (final Object entityId : entityIdsToRemove) {
      removeEntity(entityId);
    }
  }

  public void render(final float delta) {
    if (updating) {
      throw new IllegalStateException("Engine is already updating");
    }

    updating = true;
    final long t0 = System.nanoTime();

    try {
      //update all rendering systems
      for (EntitySystem system : systems) {
        if (system.isRenderer() && system.shouldUpdate(delta)) {
          final long systemStartTime = System.nanoTime();
          system.update(delta);
          if (metrics) {
            System.out.println("Took " + ((System.nanoTime() - systemStartTime) / 1e9) + " s to update " + system.getClass());
          }
        }
      }

      if (metrics) {
        System.out.println("Took " + ((System.nanoTime() - t0) / 1e9) + " s to update " + systems.size() + " rendering systems.");
      }
    } catch (final Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      updating = false;
    }
  }

}
