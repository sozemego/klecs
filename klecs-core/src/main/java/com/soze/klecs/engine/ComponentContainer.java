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

  private final Map<Object, EntityComponentContainer> components = new HashMap<>();
  private final Map<Object, Map<Node, EntityComponentContainer>> nodeCache = new HashMap<>();

  public ComponentContainer() {

  }

  public boolean addComponent(final Object entityId, final Object component) {
    final EntityComponentContainer entityComponents = getEntityComponents(entityId);
    return entityComponents.addComponent(component) == null;
  }

  public <T> T getComponent(final Object entityId, final Class<T> clazz) {
    final EntityComponentContainer entityComponents = getEntityComponents(entityId);
    return (T) entityComponents.getComponent(clazz);
  }

  public EntityComponentContainer getNodeComponents(final Object entityId, final Node node) {
    final EntityComponentContainer entityComponents = getEntityComponents(entityId);

    final Map<Node, EntityComponentContainer> cacheElement = nodeCache.computeIfAbsent(entityId, (key) -> new HashMap<>());

    final EntityComponentContainer components = cacheElement.computeIfAbsent(node, (key) -> {
      EntityComponentContainer newComponents = new EntityComponentContainer();

      boolean hasAllNodeComponents = true;
      for (Class<?> clazz : node.getComponentClasses()) {
        final Optional<Object> component = Optional.ofNullable(entityComponents.getComponent(clazz));
        if (!component.isPresent()) {
          hasAllNodeComponents = false;
          break;
        }
        newComponents.addComponent(component.get());
      }

      if (!hasAllNodeComponents) {
        newComponents = new EntityComponentContainer();
      }

      return newComponents;
    });

    return components;
  }

  /**
   * Removes a component from an entity.
   * TODO this method will only queue a component removal, to be applied on engine update.
   */
  public void removeComponent(final Object entityId, final Class<?> clazz) {
    final EntityComponentContainer entityComponents = getEntityComponents(entityId);
    entityComponents.removeComponent(clazz);
    nodeCache.remove(entityId);
  }

  /**
   * Returns given entity's components. The container is created if it was absent.
   */
  public EntityComponentContainer getEntityComponents(final Object entityId) {
    return components.computeIfAbsent(entityId, (key) -> new EntityComponentContainer());
  }

  /**
   * Removes all traces of this entity.
   *
   * @param entityId
   */
  protected void removeEntityComponents(final Object entityId) {
    components.remove(entityId);
    nodeCache.remove(entityId);
  }

  /**
   * Returns a list of entity ids which contains all components included in this node.
   */
  public List<?> getEntitiesByNode(final Node node) {
    Objects.requireNonNull(node);

    return components
             .keySet()
             .stream()
             .filter(id -> !getNodeComponents(id, node).isEmpty())
             .collect(Collectors.toList());
  }

}
