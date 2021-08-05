import com.sun.j3d.utils.geometry.Text2D;

import javax.media.j3d.*;
import java.util.Enumeration;

/**
 * A simple subclass of {@link Behavior} that detects collisions. Upon detection, it updates the collision count,
 * and updates a {@link Text2D} counter.
 *
 * @author Cormac Redmond -- credmond85 /at/ gmail
 */
public class CollideBehaviour extends Behavior {

    private WakeupCondition wakeupCondition = null;
    private Text2D score;

    public CollideBehaviour(Text2D scoreP, Node node) {
        this.score = scoreP;

        WakeupOnCollisionEntry wakeupOne = new WakeupOnCollisionEntry(node, WakeupOnCollisionEntry.USE_GEOMETRY);
        WakeupOnCollisionExit wakeupTwo = new WakeupOnCollisionExit(node, WakeupOnCollisionExit.USE_GEOMETRY);

        WakeupCriterion[] wakeupArray = new WakeupCriterion[2];
        wakeupArray[0] = wakeupOne;
        wakeupArray[1] = wakeupTwo;
        wakeupCondition = new WakeupOr(wakeupArray);
    }

    /**
     * Override {@link Behavior}'s initialize method to setup wakeup criteria.
     */
    public void initialize() {
        // Establish initial wakeup criteria
        wakeupOn(wakeupCondition);
    }

    /**
     * Override {@link Behavior}'s stimulus method to handle the event.
     */
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion genericEvt;
        while (criteria.hasMoreElements()) {
            genericEvt = (WakeupCriterion) criteria.nextElement();

            // Only count collisions on entry
            if (genericEvt instanceof WakeupOnCollisionEntry) {
                System.out.println("Collision - Entry");
                RunFlightSim.updateCollisions();
                score.setString("Collisions: " + RunFlightSim.getCollisions());
            }
            // Detect exit collisions for clarity
            else if (genericEvt instanceof WakeupOnCollisionExit) {
                System.out.println("Collision - Exit");
            }
        }
        // Set wakeup criteria for next time
        wakeupOn(wakeupCondition);
	}	
}
