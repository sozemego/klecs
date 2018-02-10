package com.soze.klecs.entity;

import com.soze.klecs.component.ComponentContainer;
import com.soze.klecs.node.Node;

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
   * Component container for all entities in a given engine.
   */
  private final ComponentContainer componentContainer;

  protected Entity(final long id, final ComponentContainer componentContainer) {
    this.id = id;
    this.componentContainer = Objects.requireNonNull(componentContainer);
  }

  public long getId() {
    return id;
  }

  /**
   * Adds a component to this entity. This can be any object.
   * @return true if there was no component with the same class as this one added already, false otherwise.
   * @see com.soze.klecs.component.ComponentContainer#addComponent(long, Object)
   */
  public boolean addComponent(final Object component) {
    return componentContainer.addComponent(id, component);
  }

  /**
   * Returns a component from this entity.
   * If the entity does not have a component with given class, returns an empty Optional.
   * @see com.soze.klecs.component.ComponentContainer#getComponent(Class)
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> getComponent(final Class<T> clazz) {
    return componentContainer.getComponent(id, clazz);
  }

  /**
   * Given a node, returns all components which belong to this node.
   * Nodes are immutable, and so return value of this method can be cached.
   * Reference equality will be used to determine if components are present in cache,
   * so use the same instance of Node for all queries.
   * If this entity does not contain at least one of the components specified
   * in the Node, this will return an empty collection.
   * Collections returned by this method are not modifiable.
   * @see com.soze.klecs.component.ComponentContainer#getNodeComponents(Node)
   */
  public Map<Class<?>, Object> getNodeComponents(final Node node) {
    return componentContainer.getNodeComponents(id, node);
  }

  public void removeComponent(final Class<?> clazz) {
    componentContainer.removeComponent(id, clazz);
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
