import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Type: java Main <land_file> <edges_file> <mission_file> <output_file>");
            System.exit(1);
        }

        // extract filenames from command-line arguments
        String landFile = args[0];
        String edgesFile = args[1];
        String missionFile = args[2];
        String outputFile = args[3];

        try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
            // parse the land file to determine grid dimensions and node types
            Scanner landScanner = new Scanner(new File(landFile));
            int width = landScanner.nextInt();
            int height = landScanner.nextInt();
            Grid grid = new Grid(width, height);
            // populate the grid with node types
            while (landScanner.hasNext()) {
                int x = landScanner.nextInt();
                int y = landScanner.nextInt();
                int type = landScanner.nextInt();
                grid.setNodeType(x, y, type);
            }
            landScanner.close();


            // parse the edges file to set travel times between nodes
            Scanner edgeScanner = new Scanner(new File(edgesFile));

            while (edgeScanner.hasNextLine()) {
                String line = edgeScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                // Extract coordinate pairs and travel time
                String[] parts = line.split(" ");
                String coords = parts[0];
                double time = Double.parseDouble(parts[1]);

                // coordinates are in format: x1-y1,x2-y2
                String[] c = coords.split(",");
                String[] c1 = c[0].split("-");
                String[] c2 = c[1].split("-");
                int x1 = Integer.parseInt(c1[0]);
                int y1 = Integer.parseInt(c1[1]);
                int x2 = Integer.parseInt(c2[0]);
                int y2 = Integer.parseInt(c2[1]);
                // set travel time in both directions
                grid.setTravelTime(x1, y1, x2, y2, time);
            }
            edgeScanner.close();

            // parse the mission file to determine starting point, reveal radius, and objectives
            Scanner missionScanner = new Scanner(new File(missionFile));
            int revealRadius = missionScanner.nextInt();
            int startX = missionScanner.nextInt();
            int startY = missionScanner.nextInt();

            Mission mission = new Mission(revealRadius, startX, startY);
            // missions can have type 1 or type 2 objectives
            // type 1: just coordinates
            // type 2: coordinates + a list of wizard options

            while (missionScanner.hasNextLine()) {

                String line = missionScanner.nextLine().trim();

                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");

                if (parts.length == 2) {
                    // type 1 objective
                    int ox = Integer.parseInt(parts[0]);
                    int oy = Integer.parseInt(parts[1]);
                    mission.addObjective(ox, oy);

                } else {
                    // type 2 objective
                    int ox = Integer.parseInt(parts[0]);
                    int oy = Integer.parseInt(parts[1]);
                    MyList<Integer> opts = new MyList<>();
                    for (int i = 2; i < parts.length; i++) {
                        opts.add(Integer.parseInt(parts[i]));
                    }
                    mission.addObjective(ox, oy, opts);
                }
            }
            missionScanner.close();

            // create a Pathfinder instance to find shortest paths

            Pathfinder pathfinder = new Pathfinder();

            // run the mission using the parsed data
            runMission(grid, mission, pathfinder, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // execute the mission: move from start to objectives, revealing nodes along the way
    private static void runMission(Grid grid, Mission mission, Pathfinder pathfinder, PrintWriter out) {
        int currentX = mission.getStartX();
        int currentY = mission.getStartY();
        int radius = mission.getRevealRadius();
        MyList<Mission.Objective> objectives = mission.getObjectives();

        // process each objective in order
        for (int i = 0; i < objectives.size(); i++) {
            Mission.Objective obj = objectives.get(i);
            int targetX = obj.getTargetX();
            int targetY = obj.getTargetY();

            // attempt to find a viable path to the current objective
            MyList<Node> path = findPossiblePath(grid, pathfinder, currentX, currentY, targetX, targetY, radius, out);
            if (path == null) {
                continue;  // if no path found, skip this objective
            }

            // update current position to the last node in the returned path
            Node lastNode = path.get(path.size() - 1);
            currentX = lastNode.getX();
            currentY = lastNode.getY();

            out.println("Objective " + (i + 1) + " reached!");

            // if the current objective has wizard options and there is a next objective,
            // determine the best option and apply it by changing node types
            if (obj.hasWizardOptions() && i < objectives.size() - 1) {
                // next objective known
                Mission.Objective nextObj = objectives.get(i + 1);
                int nextX = nextObj.getTargetX();
                int nextY = nextObj.getTargetY();

                // choose best wizard option

                int bestOption = WizardHelper.determineBestWizardOption(
                        grid, pathfinder, currentX, currentY, nextX, nextY, radius, obj.getWizardOptions());
                out.println("Number " + bestOption + " is chosen!");
                grid.changeAllOfTypeTo(bestOption, 0);
            }
        }
    }


    // main logic here: searches for a possible path from a given node
    // if obstacles are encountered, recalculate the path from where you are currently
    private static MyList<Node> findPossiblePath(Grid grid, Pathfinder pathfinder,
                                                 int sx, int sy, int ex, int ey, int radius, PrintWriter out) {
        boolean obstacleEncountered = true;

        boolean madeAnyMove = false; // track if we have made at least one move


        // keep trying until no more obstacles are encountered or no path is found
        while (obstacleEncountered) {
            obstacleEncountered = false;

            MyList<Node> currentPath = pathfinder.findShortestPath(grid, sx, sy, ex, ey);
            if (currentPath == null) {
                return null;
            }

            if (currentPath.size() == 1) {
                return currentPath; // already at target
            }

            Node currentPos = grid.getNode(sx, sy);

            // traverse along the found path
            for (int i = 1; i < currentPath.size(); i++) {
                Node nextNode = currentPath.get(i);


                // reveal nodes within the specified radius from the current position
                grid.revealNodesWithinRadius(currentPos.getX(), currentPos.getY(), radius);

                boolean futureBlocked = false;
                // check if the future nodes are still passable (type 0)

                for (int j = i; j < currentPath.size(); j++) {
                    Node futureNode = currentPath.get(j);

                    if (!isStepPossible(futureNode)) {
                        // only print  if we have made at least one move
                        if (madeAnyMove) {
                            out.println("Path is impassable!");
                        }

                        // we recalculate from current position again
                        sx = currentPos.getX();
                        sy = currentPos.getY();
                        obstacleEncountered = true;
                        futureBlocked = true;
                        break;
                    }
                }

                if (futureBlocked) {
                    // break out and try finding a new path next loop iteration
                    break;
                }

                // if we reach here, nextNode and all future nodes are possible right now
                out.println("Moving to " + nextNode.getX() + "-" + nextNode.getY());
                madeAnyMove = true; // we have made a move now
                currentPos = nextNode;

                if (i == currentPath.size() - 1) {
                    return currentPath; // reached objective
                }
            }
        }

        return null;
    }

    // check if a node is step-possible (i.e., revealed and type == 0)
    private static boolean isStepPossible(Node node) {
        return node != null && node.getDisplayedType() == 0;
    }

}
