import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

/**
 * A subclass of {@link Behavior} that detects key presses.
 * <p/>
 * Upon detection, it updates and translates accordingly.
 * <p/>
 * It handles:
 * <ul>
 * <li> arrowkey - turn left/right or move forward/back</li>
 * <li>	ALT + arrowkey - straff left/left, or look up/down</li>
 * <li>	CTRL + arrowkey - rolls around, or shifts up and down</li>
 * </ul>
 *
 * @author Cormac Redmond -- credmond85 /at/ gmail
 */
public class PlaneControls extends Behavior {
    private TransformGroup tg;
    private Transform3D t3d;
    private WakeupCondition keyCriterion;

    private double rotateYAmount = .03;
    private double rotateZAmount = .03;

    private double moveRate = 0.3;
    private double speed = 2.0;

    private int leftKey = KeyEvent.VK_LEFT;
    private int rightKey = KeyEvent.VK_RIGHT;
    private int forwardKey = KeyEvent.VK_UP;
    private int backKey = KeyEvent.VK_DOWN;

    /**
     * The constructor that should be used.
     *
     * @param tg the {@link TransformGroup} to alter upon key presses.
     */
    public PlaneControls(TransformGroup tg) {
        this.tg = tg;
        t3d = new Transform3D();
    }

    /**
     * Initialises wake-up conditions and defines wake up criteria (key presses/releases).
     */
    public void initialize() {
        WakeupCriterion[] keyEvents = new WakeupCriterion[2];
        keyEvents[0] = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        keyEvents[1] = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
        keyCriterion = new WakeupOr(keyEvents);
        wakeupOn(keyCriterion);
    }

    /**
     * This method is called when one or more keypresses are detected
     *
     * @param criteria an {@link Enumeration} of {@link WakeupCriterion}
     */
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] event;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();

            // Are we interested?
            if (!(wakeup instanceof WakeupOnAWTEvent))
                continue;

            event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
            for (AWTEvent anEvent : event) {
                // If a key was pressed call keyPressed()
                if (anEvent.getID() == KeyEvent.KEY_PRESSED) {
                    keyPressed((KeyEvent) anEvent);
                }
            }
        }
        wakeupOn(keyCriterion);
    }

    private void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        // If ALT is down while user pressing key
        if (event.isAltDown())
            ALTMove(keyCode);
            // If CTRL is down while user pressing key
        else if (event.isControlDown())
            CTRLMove(keyCode);
            // Else user is just pressing a key
        else
            simpleMove(keyCode);
    }

    /**
     * Moves forward/backwards and turns left/right
     *
     * @param keyCode - The key that was pressed
     */
    private void simpleMove(int keyCode) {
        if (keyCode == forwardKey)
            doMove(new Vector3d(0.0, 0.0, -getMovementRate()));
        else if (keyCode == backKey)
            doMove(new Vector3d(0.0, 0.0, getMovementRate()));
        else if (keyCode == leftKey)
            doRotateY(getRotateLeftAmount());
        else if (keyCode == rightKey)
            doRotateY(getRotateRightAmount());
    }

    /**
     * Straffs left/right and looks up/down
     *
     * @param keycode - The key that was pressed
     */
    protected void ALTMove(int keycode) {
        if (keycode == forwardKey)
            rotateX(getRotateUpAmount());
        else if (keycode == backKey)
            rotateX(getRotateDownAmount());
        else if (keycode == leftKey)
            doMove(new Vector3d(-getMovementRate(), 0.0, 0.0));
        else if (keycode == rightKey)
            doMove(new Vector3d(getMovementRate(), 0.0, 0.0));
    }

    /**
     * Steps up/down and spins clockwise/anti-clockwise
     *
     * @param keyCode - The key that was pressed
     */
    protected void CTRLMove(int keyCode) {
        if (keyCode == forwardKey)
            doMove(new Vector3d(0.0, getMovementRate(), 0.0));
        else if (keyCode == backKey)
            doMove(new Vector3d(0.0, -getMovementRate(), 0.0));
        else if (keyCode == leftKey)
            rotateZ(getRollLeftAmount());
        else if (keyCode == rightKey)
            rotateZ(getRollRightAmount());
    }

    // The following methods perform the transforms on the TransformGroup
    protected void doRotateY(double radians) {
        tg.getTransform(t3d);
        Transform3D toMove = new Transform3D();
        toMove.rotY(radians);
        t3d.mul(toMove);
        tg.setTransform(t3d);
    }

    protected void rotateX(double radians) {
        tg.getTransform(t3d);
        Transform3D toMove = new Transform3D();
        toMove.rotX(radians);
        t3d.mul(toMove);
        tg.setTransform(t3d);
    }

    protected void rotateZ(double radians) {
        tg.getTransform(t3d);
        Transform3D toMove = new Transform3D();
        toMove.rotZ(radians);
        t3d.mul(toMove);
        tg.setTransform(t3d);
    }

    protected void doMove(Vector3d theMove) {
        tg.getTransform(t3d);
        Transform3D toMove = new Transform3D();
        toMove.setTranslation(theMove);
        t3d.mul(toMove);
        tg.setTransform(t3d);
    }

    // The following determine how much and which direction to move

    protected double getMovementRate() {
        return moveRate * speed;
    }

    protected double getRollLeftAmount() {
        return rotateZAmount * speed;
    }

    protected double getRollRightAmount() {
        return -rotateZAmount * speed;
    }

    protected double getRotateUpAmount() {
        return rotateYAmount * speed;
    }

    protected double getRotateDownAmount() {
        return -rotateYAmount * speed;
    }

    protected double getRotateLeftAmount() {
        return rotateYAmount * speed;
    }

    protected double getRotateRightAmount() {
        return -rotateYAmount * speed;
    }
}