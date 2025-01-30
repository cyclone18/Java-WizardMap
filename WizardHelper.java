public class WizardHelper {

    /**
     * determine the best wizard option by:
     *  temporarily applying each option (turning all nodes of 'optionType' into 0),
     *  finding the shortest path and measuring its cost,
     *  reverting changes,
     *  choosing the best  option.
     */
    public static int determineBestWizardOption(Grid grid,
                                                Pathfinder pathfinder,
                                                int sx, int sy,
                                                int ex, int ey,
                                                int radius,
                                                MyList<Integer> options) {
        double bestCost = Double.MAX_VALUE;
        int bestOption = -1;

        // iterate through each offered option
        for (int i = 0; i < options.size(); i++) {
            int optionType = options.get(i);

            // store original states of affected nodes
            java.util.ArrayList<NodeBackup> backups = backupNodesOfType(grid, optionType);

            // apply option: turn all nodes of type=optionType to 0
            grid.changeAllOfTypeTo(optionType, 0);

            // now find shortest path from (sx, sy) to (ex, ey)
            MyList<Node> path = pathfinder.findShortestPath(grid, sx, sy, ex, ey);
            double cost = computePathCost(grid, path);

            // revert nodes to original state
            revertNodes(grid, backups);

            // check if this is the best option so far
            if (path != null && cost < bestCost) {
                bestCost = cost;
                bestOption = optionType;
            }
        }
        // after determining the best option, apply it
        if (bestOption != -1) {
            grid.changeAllOfTypeTo(bestOption, 0);
        }
        return bestOption;
    }


    //back up nodes of a given oldType before we apply wizard changes
    private static java.util.ArrayList<NodeBackup> backupNodesOfType(Grid grid, int oldType) {
        java.util.ArrayList<NodeBackup> backups = new java.util.ArrayList<>();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node n = grid.getNode(x,y);
                if (n.getActualType() == oldType) {
                    backups.add(new NodeBackup(x, y, n.getActualType(), n.isRevealed(), n.getDisplayedType()));
                }
            }
        }
        return backups;
    }


    //revert the nodes to their original states from backups
    private static void revertNodes(Grid grid, java.util.ArrayList<NodeBackup> backups) {
        for (NodeBackup backup : backups) {
            Node n = grid.getNode(backup.x, backup.y);
            n.setType(backup.originalActualType);

            //revert revealed and displayedType as per backup if needed

            n.setRevealed(backup.originalRevealed);
            if (!backup.originalRevealed && n.getActualType()>=2) {
                // was hidden obstacle
                n.forceDisplayedType(0);
            } else {
                // was revealed or type0 or 1
                n.forceDisplayedType(backup.originalDisplayedType);
            }
        }
    }

    //compute path cost by summing travel times along the path
    private static double computePathCost(Grid grid, MyList<Node> path) {
        if (path == null) return Double.MAX_VALUE;
        double total = 0.0;
        for (int i = 0; i < path.size()-1; i++) {
            Node a = path.get(i);
            Node b = path.get(i+1);
            double time = grid.getTravelTimeBetween(a,b);
            if (time == Double.MAX_VALUE) return Double.MAX_VALUE;
            total += time;
        }
        return total;
    }



    // a backup record to store the original state of a node before wizard help test
    private static class NodeBackup {
        int x, y;
        int originalActualType;
        boolean originalRevealed;
        int originalDisplayedType;

        NodeBackup(int x, int y, int originalActualType, boolean originalRevealed, int originalDisplayedType) {
            this.x = x;
            this.y = y;
            this.originalActualType = originalActualType;
            this.originalRevealed = originalRevealed;
            this.originalDisplayedType = originalDisplayedType;
        }
    }
}
