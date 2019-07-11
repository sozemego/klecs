package com.soze.klecs.entity;

import com.soze.klecs.engine.ComponentContainer;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.node.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Entity, which is a collection of components.
 */
public class Entity {

  /**
   * Unique id of the entity in a given {@link com.soze.klecs.engine.Engine}.
   */
  private final Object id;

  /**
   * Component container for all entities in a given engine.
   */
  private final ComponentContainer componentContainer;

  private boolean removed = false;

  protected Entity(final Object id, final ComponentContainer componentContainer) {
    this.id = Objects.requireNonNull(id);
    this.componentContainer = Objects.requireNonNull(componentContainer);
  }

  public Object getId() {
    return id;
  }

  /**
   * Adds a component to this entity. This can be any object.
   *
   * @return true if there was no component with the same class as this one added already, false otherwise.
   * @see ComponentContainer#addComponent(Object, Object)
   */
  public boolean addComponent(final Object component) {
    Objects.requireNonNull(component);
    return componentContainer.addComponent(id, component);
  }

  /**
   * Returns a component from this entity.
   * If the entity does not have a component with given class, returns null.
   * The reason this method returns nulls when there is no component, instead of Optional
   * is to make the garbage collector have a little less work.
   * Another reason is that Engine's method {@link Engine#getEntitiesByNode} returns entities,
   * which contain all components specified by a node, so a chance of returning a null is very low.
   *
   * @see ComponentContainer#getComponent(Object, Class)
   */
  public <T> T getComponent(final Class<T> clazz) {
    Objects.requireNonNull(clazz);
    return componentContainer.getComponent(id, clazz);
  }

  /**
   * Searches component by clazz, but this search will also include parents of the clazz.
   * If no component is found, returns null.
   */
  public <T> T getComponentByParent(final Class<T> clazz) {
    Objects.requireNonNull(clazz);
    return componentContainer.getComponentByParent(id, clazz);
  }

  /**
   * Returns a list of all components belonging to this entity.
   * This method is parametrized so that each component can be cast to this type.
   * This is a convenience if you have a base class for all your components.
   */
  public <T> List<T> getAllComponents(Class<T> clazz) {
    Objects.requireNonNull(clazz);

    return componentContainer.getEntityComponents(this.id)
             .getAllComponents()
             .stream()
             .map(clazz::cast)
             .collect(Collectors.toList());
  }

  /**
   * Given a node, returns all components which belong to this node.
   * Nodes are immutable, and so return value of this method can be cached.
   * Reference equality will be used to determine if components are present in cache,
   * so use the same instance of Node for all queries.
   * If this entity does not contain at least one of the components specified
   * in the Node, this will return an empty collection.
   * Modifications of returned collections will not affect the underlying
   * data structures, they are copies.
   *
   * @see ComponentContainer#getNodeComponents(Object, Node)
   */
  public <T> Collection<T> getNodeComponents(final Node node, final Class<T> clazz) {
    Objects.requireNonNull(node);
    return componentContainer
             .getNodeComponents(id, node)
             .getAllComponents()
             .stream()
             .map(clazz::cast)
             .collect(Collectors.toCollection(ArrayList::new));
  }

  public void removeComponent(final Class<?> clazz) {
    Objects.requireNonNull(clazz);
    componentContainer.removeComponent(id, clazz);
  }

  public boolean isRemoved() {
    return removed;
  }

  public void setRemoved(final boolean removed) {
    this.removed = removed;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Entity entity = (Entity) o;
    return id.equals(entity.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
