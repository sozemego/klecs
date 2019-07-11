package com.soze.klecs.engine;

import java.util.*;

/**
 * Container for all components for a given entity.
 */
public class EntityComponentContainer {

  private final Map<Class<?>, Object> components = new HashMap<>();

  public EntityComponentContainer() {

  }

  /**
   * @return previous component if present, null otherwise
   * @see Map#put
   */
  public Object addComponent(final Object component) {
    Objects.requireNonNull(component);
    return components.put(component.getClass(), component);
  }

  public Object getComponent(final Class<?> clazz) {
    Objects.requireNonNull(clazz);
    return components.get(clazz);
  }

  public <T> T getComponentByParent(final Class<T> clazz) {
    Objects.requireNonNull(clazz);
    for (final Map.Entry<Class<?>, Object> classObjectEntry : components.entrySet()) {
      Class<?> key = classObjectEntry.getKey();
      if(clazz.isAssignableFrom(key)) {
        return (T) classObjectEntry.getValue();
      }
    }
    return null;
  }

  public void removeComponent(final Class<?> clazz) {
    Objects.requireNonNull(clazz);
    components.remove(clazz);
  }

  public boolean hasComponent(final Class<?> clazz) {
    return components.containsKey(clazz);
  }

  public int componentCount() {
    return components.size();
  }

  public boolean isEmpty() {
    return components.isEmpty();
  }

  public Collection<Object> getAllComponents() {
    return components.values();
  }
}
