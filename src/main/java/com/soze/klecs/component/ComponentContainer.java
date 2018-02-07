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

  public boolean addComponent(long entityId, Object component) {
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    return entityComponents.put(component.getClass(), component) == null;
  }

  @SuppressWarnings("unchecked")
  public <T>  Optional<T> getComponent(long entityId, Class<T> clazz) {
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    return Optional.ofNullable((T) entityComponents.get(clazz));
  }

  /**
   *
   */
  public Map<Class<?>, Object> getNodeComponents(long entityId, Node node) {
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);

    Map<Node, Map<Class<?>, Object>> cacheElement = nodeCache.computeIfAbsent(entityId, (key) -> {
      Map<Node, Map<Class<?>, Object>> newCacheElement = new HashMap<>();
      Map<Class<?>, Object> components = new HashMap<>(entityComponents.size());

      for(Class<?> clazz: node.getComponentClasses()) {
        Optional<Object> component = Optional.ofNullable(entityComponents.get(clazz));
        if(!component.isPresent()) {
          newCacheElement.put(node, Collections.emptyMap());
          break;
        }
        components.put(clazz, component.get());
      }

      newCacheElement.put(node, Collections.unmodifiableMap(components));
      return newCacheElement;
    });

    return cacheElement.get(node);
  }

  public void removeComponent(long entityId, Class<?> clazz) {
    //clear this entities cache
//    nodeCache.put(entityId, new HashMap<>());

//    private final Map<Long, Map<Class<?>, Object>> components = new HashMap<>();
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    entityComponents.remove(clazz);
  }

  /**
   * Returns given entity's components. This collection is created if it was absent before.
   */
  private Map<Class<?>, Object> getEntityComponents(long entityId) {
    return components.computeIfAbsent(entityId, (key) -> new HashMap<>());
  }

}
