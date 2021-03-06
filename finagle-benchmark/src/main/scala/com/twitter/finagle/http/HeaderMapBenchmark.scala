package com.twitter.finagle.http

import com.twitter.finagle.benchmark.StdBenchAnnotations
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import scala.util.Random

@State(Scope.Benchmark)
abstract class HeaderMapBenchmark extends StdBenchAnnotations {

  protected def newMap(): HeaderMap

  // We supply 18 random strings of the length of 3 and build a 9-element
  // header map of them. The 10th element is foo -> bar so we can reliably
  // query it in the benchmark.
  private val map = Iterator.fill(9 * 2)(Random.alphanumeric.take(3).mkString)
    .grouped(2)
    .foldLeft(newMap())((map, h) => map.add(h.head, h.last))
    .add("foo", "bar")

  @Benchmark
  def create(): HeaderMap = newMap()

  @Benchmark
  def get(): Option[String] = map.get("foo")

  @Benchmark
  def createAndAdd(): HeaderMap = newMap().add("foo", "bar")

  @Benchmark
  def iterate(b: Blackhole): Unit = map.foreach(h => b.consume(h))
}

class DefaultHeaderMapBenchmark extends HeaderMapBenchmark {
  protected def newMap(): HeaderMap = HeaderMap()
}
