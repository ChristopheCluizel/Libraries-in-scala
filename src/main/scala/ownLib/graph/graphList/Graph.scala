package ownLib.graph.graphList

import java.io.{BufferedReader, BufferedWriter, FileReader, FileWriter}

import scala.collection.mutable.ArrayBuffer

/**
 * Graph is a class which represents a graph structure. Its implementation is based on an adjacency list which is less heavy than an adjacency matrix. One can use whatever kind of node with Graph.
 *
 * @author christophe
 *
 * @param name The name of the graph.
 * @tparam T Enable any kind of node.
 */

class Graph[T](val name: String = "graph") {

  var numberOfEdges = 0

  /**
   * Implementation of the adjacency list. The key corresponds to the key of a father node and the value to an array containing the key of the successor nodes of this father node.
   */
  var adjacency: Map[Int, ArrayBuffer[Int]] = Map()

  /**
   * Nodes is a list of all the nodes of the graph. It associates a key to any kind of node T.
   */
  var nodes: Map[Int, T] = Map()

  /**
   * Add a node to the graph.
   *
   * @param key The key of the node inserted.
   * @param node The node inserted in the graph.
   */
  def addNode(key: Int, node: T) = {
    if (!nodes.contains(key)) {
      nodes += (key -> node)
      adjacency += (key -> new ArrayBuffer())
    }
  }

  /**
   * Add an edge to the graph between two nodes.
   *
   * @param key1 The key of the first node.
   * @param key2 The key of the second node.
   * @param value Not used yet.
   */
  def addEdge(key1: Int, key2: Int, value: Int) = {
    if (!adjacency(key1).contains(key2)) {
      adjacency(key1) += key2
      numberOfEdges += 1
    }
  }

  /**
   * Remove an edge of the graph between two nodes.
   *
   * @param key1 The key of the first node.
   * @param key2 The key of the second node.
   */
  def removeEdge(key1: Int, key2: Int) = {
    if (adjacency(key1).contains(key2)) adjacency(key1) -= key2
    numberOfEdges -= 1
  }

  /**
   * Indicate if the graph is empty.
   *
   * @return Return whether the graph is empty or not.
   */
  def isEmpty: Boolean = adjacency.isEmpty

  /**
   * Indicate if a node is present in the graph.
   *
   * @param key The key of the node considered.
   * @return Return whether the node is present or not.
   */
  def nodePresent(key: Int): Boolean = adjacency.contains(key)

  /**
   * Indicate if an edge is present between two nodes in the graph.
   *
   * @param key1 The key of the first node.
   * @param key2 The key of the second node.
   * @return Return whether the edge is present or not.
   */
  def edgePresent(key1: Int, key2: Int): Boolean = adjacency(key1).contains(key2)

  /**
   * Indicate the number of nodes in the graph.
   *
   * @return The number of nodes.
   */
  def numberOfNodes: Int = {
    nodes.size
  }

  /**
   * Save the graph in a text file.
   *
   * @param filePath The file path where will be stored the graph.
   *
   *                 The name of the file will be the name of the graph with the extension ".dot".
   */
  def save(filePath: String = "") = {
    val writer = new BufferedWriter(new FileWriter(filePath + name + ".dot"))
    writer.write(toString)
    writer.close()
  }

  /**
   * Redefine the toString method to describe a graph.
   */
  override def toString: String = {
    var string = numberOfEdges.toString + "\n" +
      "graph " + name + " {\n"
    adjacency.keys.foreach { i =>
      for (j <- adjacency(i).indices) {
        string += i.toString + " -> " + adjacency(i)(j).toString + "\n"
      }
    }
    string += "}"
    string
  }

  /**
   * Scan the graph by breadth first search.
   *
   * @param key The key of the start node.
   * @return A string of the nodes crossed, sorted by cross order.
   */
  def breadthFirstSearch(key: Int): String = {
    var queue = new scala.collection.mutable.Queue[Int]
    var markedNode: ArrayBuffer[Int] = ArrayBuffer()
    var actualNodeKey = 0
    var listNodesVisited = ""

    queue += key
    while (queue.nonEmpty) {
      actualNodeKey = queue.dequeue()
      markedNode += actualNodeKey
      listNodesVisited += actualNodeKey.toString + ", "
      // treat actual node here
      for (i <- getSuccessors(actualNodeKey)) if (!markedNode.contains(i) && !queue.contains(i)) queue += i
    }
    listNodesVisited = listNodesVisited.dropRight(2)
    listNodesVisited
  }

  /**
   * Get the keys of the nodes which are the successors of another one.
   *
   * @param key The key of the node whose one wants the successors.
   * @return The keys of the successor nodes.
   */
  def getSuccessors(key: Int): ArrayBuffer[Int] = adjacency(key)

  /**
   * Calculate the eccentricity of a node. The eccentricity is the longest distance between a node and all the other ones.
   *
   * @param key The key of the node whose one wants to calculate the eccentricity.
   * @return The value of the eccentricity of the node.
   */
  def calculateEccentricityOf(key: Int): Int = {
    var queue = new scala.collection.mutable.Queue[Int]
    var actualNodeKey = 0
    var eccentricity = 0
    var distances: scala.collection.mutable.Map[Int, Int] = scala.collection.mutable.Map()

    adjacency.keys.foreach(i => distances += (i -> -1))

    distances.update(key, 0)
    queue += key
    while (queue.nonEmpty) {
      actualNodeKey = queue.dequeue()
      for (i <- getSuccessors(actualNodeKey)) if (distances(i) == -1) {
        queue += i
        distances.update(i, distances(actualNodeKey) + 1)
        eccentricity = distances(i)
      }
    }
    eccentricity
  }

  /**
   * Delete all the leaves of the graph. A leaf is a node which doesn't have any successors. This method has sense only for an oriented-graph.
   */
  def shedTheLeaves() = for (i <- adjacency.keys) if (getSuccessors(i).isEmpty) removeNode(i)

  /**
   * Remove a node of the graph.
   *
   * @param key The key of the node to remove.
   */
  def removeNode(key: Int) {
    nodes -= key
    adjacency -= key
    for (i <- adjacency.keys) if (adjacency(i).contains(key)) adjacency(i) -= key
  }

  /**
   * Display all the graph. Each node is displayed with its key, predecessors and successors.
   */
  // $COVERAGE-OFF$
  def display() {
    adjacency.keys.foreach { i =>
      println("key : " + i + ", Node : " + adjacency(i).toString +
        ", Successors : " + getSuccessors(i).mkString(", ") +
        ", Predecessors : " + getPredecessors(i).mkString(", "))
    }
  }

  /**
   * Get the keys of the nodes which are the predecessors of another one.
   *
   * @param key The key of the node whose one wants the predecessors.
   * @return The keys of the predecessor nodes.
   */
  def getPredecessors(key: Int): ArrayBuffer[Int] = {
    var predecessors: ArrayBuffer[Int] = ArrayBuffer()
    for (i <- adjacency.keys) {
      if (adjacency(i).contains(key)) {
        predecessors += i
      }
    }
    predecessors
  }

  // $COVERAGE-ON$
}

object Graph {

  /**
   * Load a graph from a text file.
   *
   * @param fileName The name of the text file where a graph is saved.
   * @return The graph loaded.
   */
  def load(fileName: String): Graph[Int] = {
    val reader = new BufferedReader(new FileReader(fileName))
    val nbEdges = reader.readLine.toInt
    val firstLine = reader.readLine
    val result = firstLine.split(" ")
    val graphName = result(1)

    val graph = new Graph[Int](graphName)
    for (i <- 0 until nbEdges) {
      val Array(key1, key2) = for (i <- reader.readLine split " -> ") yield i.toInt
      if (!graph.nodePresent(key1)) graph.addNode(key1, key1)
      if (!graph.nodePresent(key2)) graph.addNode(key2, key2)
      graph.addEdge(key1, key2, 1)
    }
    reader.close()
    graph
  }
}
