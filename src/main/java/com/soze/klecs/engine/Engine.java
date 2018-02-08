package com.soze.klecs.engine;

import com.soze.klecs.entity.EntityFactory;
import com.soze.klecs.system.EntitySystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an ECS engine, which handles updates to systems and stores entities.
 */
public class Engine {

  private final EntityFactory entityFactory = new EntityFactory(this);

  private final List<EntitySystem> systems = new ArrayList<>();
  //METHODS TO IMPLEMENT
  //add entity

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
   * Calls shouldUpdate on all systems and for those which return true,
   * calls the update method.
   * @param delta time in seconds since the last update
   */
  public void update(float delta) {
    for(EntitySystem system: systems) {
      if(system.shouldUpdate(delta)) {
        system.update(delta);
      }
    }
  }


}
