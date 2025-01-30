public class Pathfinder {


    // finds the shortest path from start to end on the grid
    public MyList<Node> findShortestPath(Grid grid, int sx, int sy, int ex, int ey) {
        if (!grid.isWithinBounds(sx, sy) || !grid.isWithinBounds(ex, ey)) {
            return null; // out of bounds
        }

        Node startNode = grid.getNode(sx, sy);
        Node endNode = grid.getNode(ex, ey);

        // if start or end node is not passable , no path
        if (!isNodePassable(startNode) || !isNodePassable(endNode)) {
            return null;
        }

        int width = grid.getWidth();
        int height = grid.getHeight();

        // distance array to store shortest dist from start
        double[][] dist = new double[width][height];
        // to reconstruct the path
        Node[][] prev = new Node[width][height];

        // initialize distances
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                dist[i][j] = Double.MAX_VALUE;
            }
        }
        dist[sx][sy] = 0.0;

        // Priority Queue for Dijkstra
        MyPriorityQueue<State> pq = new MyPriorityQueue<>();
        pq.add(new State(sx, sy, 0.0));

        // directions for adjacency (4-directional)
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!pq.isEmpty()) {  // main loop for Dijkstra
            State current = pq.poll();

            // if this is not the shortest distance to this node, skip
            if (current.dist > dist[current.x][current.y]) continue;

            // if we reached the end
            if (current.x == ex && current.y == ey) {
                return buildPath(prev, startNode, endNode);
            }

            // explore neighbors
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (!grid.isWithinBounds(nx, ny)) continue;

                Node neighbor = grid.getNode(nx, ny);

                // check if neighbor is passable
                if (!isNodePassable(neighbor)) continue;

                double travelTime = grid.getTravelTimeBetween(grid.getNode(current.x, current.y), neighbor);
                if (travelTime == Double.MAX_VALUE) {
                    // no valid edge or impassable edge
                    continue;
                }

                double newDist = current.dist + travelTime;
                if (newDist < dist[nx][ny]) {
                    dist[nx][ny] = newDist;
                    prev[nx][ny] = grid.getNode(current.x, current.y);
                    pq.add(new State(nx, ny, newDist));
                }
            }
        }

        // if we exit the loop without returning, no path was found
        return null;
    }


    // check if a node is passable based on rules
    private boolean isNodePassable(Node node) {
        if (node == null) return false;
        int dt = node.getDisplayedType();
        // dt=0 means passable
        // dt=1 or >= 2 means obstacle
        return (dt == 0);
    }




    // reconstructs the path from end to start using prev array
    private MyList<Node> buildPath(Node[][] prev, Node start, Node end) {
        // first gather nodes in reverse (end to start)
        MyList<Node> reversedPath = new MyList<>();
        Node current = end;
        while (current != null) {
            reversedPath.add(current);
            if (current == start) break;
            current = prev[current.getX()][current.getY()];
        }

        // if we never reached the start return null
        if (reversedPath.size() == 0 || reversedPath.get(reversedPath.size() - 1) != start) {
            return null;
        }

        // reverse reversedPath to get start-to-end order
        MyList<Node> path = new MyList<>();
        for (int i = reversedPath.size() - 1; i >= 0; i--) {
            path.add(reversedPath.get(i));
        }

        return path;
    }


    // helper class to store the state used in the priority queue
    private static class State implements Comparable<State> {
        int x, y;
        double dist;

        State(int x, int y, double dist) {
            this.x = x;
            this.y = y;
            this.dist = dist;
        }

        @Override
        public int compareTo(State other) {
            return Double.compare(this.dist, other.dist);
        }
    }
}
