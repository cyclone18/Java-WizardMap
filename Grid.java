public class Grid {
    private final int width;
    private final int height;
    private final Node[][] nodes;
    private final MyHash<String, Double> travelTimes; // store travel times in MyHash

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.nodes = new Node[width][height];
        this.travelTimes = new MyHash<>(); // initialize MyHash
    }

    public void setNodeType(int x, int y, int type) {
        nodes[x][y] = new Node(x, y, type);
    }



    // store the travel time between two adjacent nodes in both directions
    public void setTravelTime(int x1, int y1, int x2, int y2, double time) {
        String key1 = createEdgeKey(x1, y1, x2, y2);
        String key2 = createEdgeKey(x2, y2, x1, y1);
        travelTimes.put(key1, time);
        travelTimes.put(key2, time);
    }


    // retrieve the node at the specified coordinates if within bounds
    public Node getNode(int x, int y) {
        if (isWithinBounds(x, y)) {
            return nodes[x][y];
        }
        return null;
    }


    // check if coordinates (x, y) fall inside the grid's width and height
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }



    // get the travel time between two nodes if they are adjacent; otherwise return a large value
    public double getTravelTimeBetween(Node a, Node b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        if (!isAdjacent(a.getX(), a.getY(), b.getX(), b.getY())) return Double.MAX_VALUE;

        String key = createEdgeKey(a.getX(), a.getY(), b.getX(), b.getY());
        Double val = travelTimes.get(key);
        return (val == null) ? Double.MAX_VALUE : val;
    }


    // determine if two sets of coordinates are adjacent (differ by 1 in either x or y)
    private boolean isAdjacent(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx + dy == 1);
    }

    // create a string key for identifying the edge between two coordinates (x1,y1) and (x2,y2)
    private String createEdgeKey(int x1, int y1, int x2, int y2) {
        return x1 + "-" + y1 + "," + x2 + "-" + y2;
    }


    // change all nodes of a certain type to another type
    public void changeAllOfTypeTo(int oldType, int newType) {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                Node n = getNode(i,j);
                if (n.getActualType() == oldType) {
                    n.setType(newType);
                    // setType handles revealed/ displayedType logic internally
                }
            }
        }
    }


    // reveal all nodes within a radius
    public void revealNodesWithinRadius(int centerX, int centerY, int radius) {
        int minX = Math.max(centerX - radius, 0);
        int maxX = Math.min(centerX + radius, getWidth() - 1);
        int minY = Math.max(centerY - radius, 0);
        int maxY = Math.min(centerY + radius, getHeight() - 1);

        double rSquared = radius * radius;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                double dx = x - centerX;
                double dy = y - centerY;
                double distSquared = dx*dx + dy*dy;
                if (distSquared <= rSquared) {
                    Node n = getNode(x,y);
                    if (n != null) {
                        n.reveal();
                    }
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
