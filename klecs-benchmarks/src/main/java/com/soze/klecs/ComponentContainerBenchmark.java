package com.soze.klecs;

import com.soze.klecs.engine.ComponentContainer;
import com.soze.klecs.node.Node;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Fork(value = 1, warmups = 1)
@Measurement(iterations = 5, time = 5)
@Warmup(iterations = 5, time = 1)
public class ComponentContainerBenchmark {

  private ComponentContainer componentContainer;
  private Node node;
  private Node smallerNode;


  @Setup(Level.Trial)
  public void setup() {
    componentContainer = new ComponentContainer();
    componentContainer.addComponent(1, "String");
    componentContainer.addComponent(1, 5);
    componentContainer.addComponent(1, new Object());
    componentContainer.addComponent(1, new ArrayList<>());
    componentContainer.addComponent(1, new HashSet<>());
    componentContainer.addComponent(1, Byte.valueOf("5"));
    componentContainer.addComponent(1, Long.valueOf("5"));
    componentContainer.addComponent(1, new LinkedList<>());
    componentContainer.addComponent(1, new HashMap<>());
    componentContainer.addComponent(1, Short.valueOf("5"));

    node = Node.of(
      String.class, Integer.class, Object.class, ArrayList.class,
      HashSet.class, Byte.class, Long.class, LinkedList.class,
      HashMap.class, Short.class
    );

    smallerNode = Node.of(String.class, LinkedList.class, Long.class);
  }

  @Benchmark
  public void testGettingOneNode(Blackhole bh) {
    bh.consume(componentContainer.getNodeComponents(1, node));
  }

  @Benchmark
  public void testGettingSmallerOneNode(Blackhole bh) {
    bh.consume(componentContainer.getNodeComponents(1, smallerNode));
  }

  @Benchmark
  public void testGettingComponentsOneByOne(Blackhole bh) {
    node.getComponentClasses().forEach(clazz -> bh.consume(componentContainer.getComponent(1, clazz)));
  }

  @Benchmark
  public void testRemoveSometimes(Blackhole bh) {
      boolean mess = Math.random() < 2;
      if (mess) {
        componentContainer.removeComponent(1, String.class);
      }
    bh.consume(componentContainer.getNodeComponents(1, node));
      if (mess) {
        componentContainer.addComponent(1, "5");
      }
  }



}
