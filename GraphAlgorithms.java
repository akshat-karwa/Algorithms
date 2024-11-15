import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * My implementation of various different graph algorithms.
 *
 * @author AKSHAT KARWA
 */
public class GraphAlgorithms {

    /**
     * Performs a breadth first search (bfs) on the input graph, starting at
     * the parameterized starting vertex.
     *
     * When exploring a vertex, we explore in the order of neighbors returned by
     * the adjacency list.
     *
     * We do not modify the structure of the graph. The graph remains unmodified
     * after this method terminates.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the bfs on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     */
    public static <T> List<Vertex<T>> bfs(Vertex<T> start, Graph<T> graph) {
        if (start == null || graph == null || !graph.getVertices().contains(start)) {
            throw new IllegalArgumentException("Input is null or start does not exist in the graph!");
        }
        List<Vertex<T>> list = new LinkedList<>();
        Set<Vertex<T>> visited = new HashSet<>();
        Queue<Vertex<T>> queue = new LinkedList<>();
        visited.add(start);
        list.add(start);
        queue.add(start);
        while (queue.peek() != null) {
            Vertex<T> vertex = queue.remove();
            for (VertexDistance<T> adjacentVertexDistance : graph.getAdjList().get(vertex)) {
                if (!visited.contains(adjacentVertexDistance.getVertex())) {
                    visited.add(adjacentVertexDistance.getVertex());
                    list.add(adjacentVertexDistance.getVertex());
                    queue.add(adjacentVertexDistance.getVertex());
                }
            }
        }
        return list;
    }

    /**
     * Performs a depth first search (dfs) on the input graph, starting at
     * the parameterized starting vertex.
     *
     * When exploring a vertex, we explore in the order of neighbors returned by
     * the adjacency list.
     *
     * We implement this method recursively.
     * We do not modify the structure of the graph. The graph remains unmodified
     * after this method terminates.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the dfs on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     */
    public static <T> List<Vertex<T>> dfs(Vertex<T> start, Graph<T> graph) {
        if (start == null || graph == null || !graph.getVertices().contains(start)) {
            throw new IllegalArgumentException("Input is null or start does not exist in the graph!");
        }
        Set<Vertex<T>> visited = new HashSet<>();
        List<Vertex<T>> list = new LinkedList<>();
        dfsRecursive(start, graph, visited, list);
        return list;
    }

    /**
     * Private Helper Recursive Depth First Search Method
     *
     * @param <T>   the generic typing of the data
     * @param start the innerVertex to begin the dfsRecursive method on
     * @param graph the graph to search through
     * @param visited is the HashSet of currently visited vertices in the
     *                recursive dfs traversal
     * @param list is the list of the vertices traversed
     */
    private static <T> void dfsRecursive(Vertex<T> start, Graph<T> graph, Set<Vertex<T>> visited,
                                         List<Vertex<T>> list) {
        visited.add(start);
        list.add(start);
        for (VertexDistance<T> adjacentVertexDistance : graph.getAdjList().get(start)) {
            if (!visited.contains(adjacentVertexDistance.getVertex())) {
                dfsRecursive(adjacentVertexDistance.getVertex(), graph, visited, list);
            }
        }
    }

    /**
     * Finds the single-source shortest distance between the start vertex and
     * all vertices given a weighted graph with non-negative edge weights.
     *
     * Returns a map of the shortest distances such that the key of each entry
     * is a node in the graph and the value for the key is the shortest distance
     * to that node from start, or Integer.MAX_VALUE (representing
     * infinity) if no path exists.
     * 
     * We implement Dijkstra's algorithm using two
     * termination conditions in conjunction.
     *
     * 1) We check if all of the vertices have been visited.
     * 2) We check if the PriorityQueue is empty yet.
     *
     * We do not modify the structure of the graph. The graph remains unmodified
     * after this method terminates.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the Dijkstra's on (source)
     * @param graph the graph we are applying Dijkstra's to
     * @return a map of the shortest distances from start to every
     * other node in the graph
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph.
     */
    public static <T> Map<Vertex<T>, Integer> dijkstras(Vertex<T> start,
                                                        Graph<T> graph) {
        if (start == null || graph == null || !graph.getVertices().contains(start)) {
            throw new IllegalArgumentException("Input is null or start does not exist in the graph!");
        }
        Map<Vertex<T>, Integer> distanceMap = new HashMap<>();
        Set<Vertex<T>> visited = new HashSet<>();
        Queue<VertexDistance<T>> pQueue = new PriorityQueue<>();
        for (Vertex<T> vertex : graph.getVertices()) {
            distanceMap.put(vertex, Integer.MAX_VALUE);
        }
        VertexDistance<T> v = new VertexDistance<>(start, 0);
        pQueue.add(v);
        while (pQueue.peek() != null && visited.size() != graph.getVertices().size()) {
            VertexDistance<T> node = pQueue.remove();
            if (!visited.contains(node.getVertex())) {
                visited.add(node.getVertex());
                distanceMap.put(node.getVertex(), node.getDistance());
                for (VertexDistance<T> adjacentVertexDistance : graph.getAdjList().get(node.getVertex())) {
                    if (!visited.contains(adjacentVertexDistance.getVertex())) {
                        VertexDistance<T> currNode = new VertexDistance<>(adjacentVertexDistance.getVertex(),
                                node.getDistance() + adjacentVertexDistance.getDistance());
                        pQueue.add(currNode);
                    }
                }
            }
        }
        return distanceMap;
    }

    /**
     * Runs Kruskal's algorithm on the given graph and returns the Minimal
     * Spanning Tree (MST) in the form of a set of Edges. If the graph is
     * disconnected and therefore no valid MST exists, we return null.
     *
     * We assume that the passed in graph is undirected. In this framework,
     * this means that if (u, v, 3) is in the graph, then the opposite edge
     * (v, u, 3) will also be in the graph, though as a separate Edge object.
     *
     * The returned set of edges form an undirected graph. This means
     * that every time we add an edge to our return set, we add the
     * reverse edge to the set as well.
     * We also assume that there will only be one valid MST that can be formed.
     *
     * Kruskal's algorithm also requires us to use a Disjoint Set. 
     * A Disjoint Set will keep track of which vertices are
     * connected given the edges in our current MST, allowing us to easily
     * figure out whether adding an edge will create a cycle. Refer
     * to the DisjointSet and DisjointSetNode classes for more information.
     *
     * We do not allow self-loops or parallel edges into the MST.
     *
     * By using the Disjoint Set provided, we avoid adding self-loops and
     * parallel edges into the MST.
     *
     * We do not modify the structure of the graph. The graph remains unmodified
     * after this method terminates.
     *
     * @param <T>   the generic typing of the data
     * @param graph the graph we are applying Kruskals to
     * @return the MST of the graph or null if there is no valid MST
     * @throws IllegalArgumentException if any input is null
     */
    public static <T> Set<Edge<T>> kruskals(Graph<T> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph input cannot be null!!");
        }
        Set<Edge<T>> mstEdgeSet = new HashSet<>();
        DisjointSet<T> disjointSet = new DisjointSet<>();
        for (Vertex<T> vertex : graph.getVertices()) {
            disjointSet.find(vertex);
        }
        Queue<Edge<T>> pQueue = new PriorityQueue<>();
        for (Edge<T> edge : graph.getEdges()) {
            pQueue.add(edge);
        }
        while (pQueue.peek() != null && mstEdgeSet.size() < (2 * (graph.getVertices().size() - 1))) {
            Edge<T> edge = pQueue.remove();
            Edge<T> oppEdge = new Edge<>(edge.getV(), edge.getU(), edge.getWeight());
            if (!(disjointSet.find(edge.getU())).equals(disjointSet.find(edge.getV()))) {
                mstEdgeSet.add(edge);
                mstEdgeSet.add(oppEdge);
                disjointSet.union(edge.getU().getData(), edge.getV().getData());
            }
        }
        if (mstEdgeSet.size() < 2 * (graph.getVertices().size() - 1)) {
            return null;
        }
        return mstEdgeSet;
    }
}