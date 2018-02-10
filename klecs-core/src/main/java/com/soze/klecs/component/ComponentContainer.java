package com.soze.klecs.component;

import com.soze.klecs.node.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A container of components for one engine.
 * The goal is to keep all components for all entities in one place.
 * This allows for efficiently getting components, nodes of components or entities by components.
 */
public class ComponentContainer {

  //TODO can solve this with guava
  private final Map<Long, Map<Class<?>, Object>> components = new HashMap<>();

  //TODO can solve this with guava
  private final Map<Long, Map<Node, Map<Class<?>, Object>>> nodeCache = new HashMap<>();

  public ComponentContainer() {

  }

  public boolean addComponent(final long entityId, final Object component) {
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    return entityComponents.put(component.getClass(), component) == null;
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> getComponent(final long entityId, final Class<T> clazz) {
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    return Optional.ofNullable((T) entityComponents.get(clazz));
  }

  /**
   *
   */
  public Map<Class<?>, Object> getNodeComponents(final long entityId, final Node node) {
    final Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);

    Map<Node, Map<Class<?>, Object>> cacheElement = nodeCache.computeIfAbsent(entityId, (key) -> {
      final Map<Node, Map<Class<?>, Object>> newCacheElement = new HashMap<>();
      Map<Class<?>, Object> components = new HashMap<>(entityComponents.size());

      boolean hasAllNodeComponents = true;
      for (Class<?> clazz : node.getComponentClasses()) {
        final Optional<Object> component = Optional.ofNullable(entityComponents.get(clazz));
        if (!component.isPresent()) {
          hasAllNodeComponents = false;
          break;
        }
        components.put(clazz, component.get());
      }

      if (!hasAllNodeComponents) {
        components = Collections.emptyMap();
      }

      newCacheElement.put(node, Collections.unmodifiableMap(components));
      return newCacheElement;
    });

    return cacheElement.get(node);
  }

  public void removeComponent(final long entityId, final Class<?> clazz) {
    //clear this entities cache
//    nodeCache.put(entityId, new HashMap<>());

//    private final Map<Long, Map<Class<?>, Object>> components = new HashMap<>();
    final Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    entityComponents.remove(clazz);
    nodeCache.remove(entityId);
  }

  /**
   * Returns given entity's components. This collection is created if it was absent before.
   */
  private Map<Class<?>, Object> getEntityComponents(final long entityId) {
    return components.computeIfAbsent(entityId, (key) -> new HashMap<>());
  }

}
