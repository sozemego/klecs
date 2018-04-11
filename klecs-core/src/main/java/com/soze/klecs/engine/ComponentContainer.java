package com.soze.klecs.engine;

import com.soze.klecs.node.Node;

import java.util.*;
import java.util.stream.Collectors;

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

  /**
   * Stores a set of entities which belong to a node.
   * The question is, what to do if someone adds or removes a component from an entity.
   * Store ComponentClass - Node map?
   */
  private final Map<Node, Set<Long>> nodeEntityCache = new HashMap<>();

  public ComponentContainer() {

  }

  public boolean addComponent(final long entityId, final Object component) {
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    return entityComponents.put(component.getClass(), component) == null;
  }

  public <T> T getComponent(final long entityId, final Class<T> clazz) {
    Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    return (T) entityComponents.get(clazz);
  }

  /**
   *
   */
  public Map<Class<?>, Object> getNodeComponents(final long entityId, final Node node) {
    final Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);

    Map<Node, Map<Class<?>, Object>> cacheElement = nodeCache.computeIfAbsent(entityId, (key) -> new HashMap<>());

    Map<Class<?>, Object> components = cacheElement.computeIfAbsent(node, (key) -> {
      Map<Class<?>, Object> newComponents = new HashMap<>(entityComponents.size());

      boolean hasAllNodeComponents = true;
      for (Class<?> clazz : node.getComponentClasses()) {
        final Optional<Object> component = Optional.ofNullable(entityComponents.get(clazz));
        if (!component.isPresent()) {
          hasAllNodeComponents = false;
          break;
        }
        newComponents.put(clazz, component.get());
      }

      if (!hasAllNodeComponents) {
        newComponents = Collections.emptyMap();
      }

      return newComponents;
    });

    return components;
  }

  /**
   * Removes a component from an entity.
   * TODO this method will only queue a component removal, to be applied on engine update.
   * @param entityId
   * @param clazz
   */
  public void removeComponent(final long entityId, final Class<?> clazz) {
    final Map<Class<?>, Object> entityComponents = getEntityComponents(entityId);
    entityComponents.remove(clazz);
    nodeCache.remove(entityId);
  }

  /**
   * Returns given entity's components. This collection is created if it was absent before.
   */
  public Map<Class<?>, Object> getEntityComponents(final long entityId) {
    return components.computeIfAbsent(entityId, (key) -> new HashMap<>());
  }

  /**
   * Removes all traces of this entity.
   * @param entityId
   */
  protected void removeEntityComponents(final long entityId) {
    components.remove(entityId);
    nodeCache.remove(entityId);
  }

  /**
   * Returns a list of entity ids which contains all components included in this node.
   * @param node
   * @return
   */
  public List<Long> getEntitiesByNode(final Node node) {
    Objects.requireNonNull(node);

    return components
      .keySet()
      .stream()
      .filter(id -> !getNodeComponents(id, node).isEmpty())
      .collect(Collectors.toList());
  }

}
