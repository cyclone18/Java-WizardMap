public class Node {
    private final int x;
    private final int y;
    private int actualType;    // the real type of the node
    private int displayedType; // the type currently used by pathfinding
    private boolean revealed;

    public Node(int x, int y, int actualType) {
        this.x = x;
        this.y = y;
        this.actualType = actualType;

        if (actualType == 0 || actualType == 1) {
            this.displayedType = actualType;
            this.revealed = true;
        } else {
            // actualType≥2: hidden obstacle, initially appear as 0 (free)
            this.displayedType = 0;
            this.revealed = false;
        }
    }
    public int getX() { return x; }

    public int getY() { return y; }

    public int getActualType() { return actualType; }

    public int getDisplayedType() { return displayedType; }

    public boolean isRevealed() {
        return revealed;
    }


    // explicitly set a revealed state
    //needed for reverting states in WizardHelper
    public void setRevealed(boolean newRevealed) {
        this.revealed = newRevealed;
        //  we do not automatically change displayedType here,
        // because reverting state is handled manually by WizardHelper.
    }


    // reveals the node. If actual type >=2, update the displayedType to actualType
    public void reveal() {
        if (!revealed) {
            revealed = true;
            if (actualType >= 2) {
                this.displayedType = actualType;
            }
        }
    }

    public void setType(int newType) {
        this.actualType = newType;
        if (revealed) {
            // if already revealed, displayedType matches actualType
            this.displayedType = newType;
        } else {
            // not revealed
            if (newType == 0 || newType == 1) {
                this.revealed = true;
                this.displayedType = newType;
            } else {
                // newType≥2, not revealed: appear as free (0)
                this.displayedType = 0;
            }
        }
    }


    // force displayedType to a specific value
    // needed to revert states in WizardHelper
    public void forceDisplayedType(int newDisplayedType) {
        this.displayedType = newDisplayedType;
    }
}
