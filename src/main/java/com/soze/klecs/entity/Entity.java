package com.soze.klecs.entity;

import com.soze.klecs.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Entity, which is a collection of components.
 */
public class Entity {

  /**
   * Unique id of the entity in a given {@link com.soze.klecs.engine.Engine}.
   */
  private final long id;

  /**
   * A collection of components this entity has.
   * Retrieval by class takes O(1) time.
   */
  private final Map<Class<?>, Object> components = new HashMap<>();



  protected Entity(final long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  /**
   * Adds a component to this entity. This can be any object.
   * @return true if there was no component with the same class as this one added already, false otherwise.
   */
  public boolean addComponent(Object component) {
    Class<?> componentClass = component.getClass();
    return components.put(componentClass, component) == null;
  }

  /**
   * Returns a component from this entity.
   * If the entity does not have a component with given class, returns an empty Optional.
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> getComponent(Class<T> clazz) {
    Object component = components.get(clazz);
    return Optional.ofNullable((T) component);
  }

  /**
   * Given a node, returns all components which belong to this node.
   * Nodes are immutable, and so return value of this method can be cached.
   * Reference equality will be used to determine if components are present in cache,
   * so use the same instance of Node for all queries.
   * @param node
   * @return
   */
  public Map<Class<?>, Object> getNodeComponents(Node node) {
    return null;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Entity entity = (Entity) o;
    return id == entity.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
