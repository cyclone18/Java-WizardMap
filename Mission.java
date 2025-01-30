public class Mission {

    private final int revealRadius;
    private final int startX;
    private final int startY;
    private final MyList<Objective> objectives; //  MyList to store objectives




    //constructor for Mission
    public Mission(int revealRadius, int startX, int startY) {
        this.revealRadius = revealRadius;
        this.startX = startX;
        this.startY = startY;
        this.objectives = new MyList<>();
    }

    //add a type 1 wizard objective with no help
    public void addObjective(int x, int y) {
        objectives.add(new Objective(x, y));
    }

    // add a type 2 objective with wizard help options
    public void addObjective(int x, int y, MyList<Integer> options) {
        objectives.add(new Objective(x, y, options));
    }

    public int getRevealRadius() {
        return revealRadius;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    // returns a list of all objectives
    public MyList<Objective> getObjectives() {
        return objectives;
    }

    // inner class to represent an objective
    public static class Objective {
        private final int targetX;
        private final int targetY;
        private final MyList<Integer> wizardOptions; // null or empty if no wizard help

        public Objective(int targetX, int targetY) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.wizardOptions = null;
        }

        public Objective(int targetX, int targetY, MyList<Integer> wizardOptions) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.wizardOptions = wizardOptions;
        }

        public int getTargetX() {
            return targetX;
        }

        public int getTargetY() {
            return targetY;
        }

        public boolean hasWizardOptions() {
            return wizardOptions != null && wizardOptions.size() > 0;
        }

        public MyList<Integer> getWizardOptions() {
            return wizardOptions;
        }
    }
}
