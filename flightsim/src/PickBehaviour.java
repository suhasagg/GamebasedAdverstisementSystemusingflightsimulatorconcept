import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

import javax.media.j3d.*;

/**
 * A simple picking behavior that detects when a meteor is clicked/picked. Upon detection, it updates the
 * Text2D score.
 *
 * @author Cormac Redmond -- credmond85 /at/ gmail
 */
public class PickBehaviour extends PickMouseBehavior {
    Text2D score;
    ClassLoader cl = this.getClass().getClassLoader();

    /**
     * The only constructor that should be used.
     *
     * @param canvas the caller's {@link Canvas3D}
     * @param root   the {@link BranchGroup} that the behaviour should be added to
     * @param bounds the scheduling {@link Bounds}
     * @param scoreP the {@link Text2D} to hold the score
     */
    public PickBehaviour(Canvas3D canvas, BranchGroup root, Bounds bounds, Text2D scoreP) {
        super(canvas, root, bounds);
        this.score = scoreP;
        this.setSchedulingBounds(bounds);
        root.addChild(this);

        // Pick using the bounds of the 'pickable' nodes.
        // The PickResult returned will contain the SceneGraphPath to the picked Node.
        pickCanvas.setMode(PickTool.BOUNDS);
    }

    /**
     * Called when a user clicks
     *
     * @param xpos the x coordinate
     * @param ypos the y coordinate
     */
    public void updateScene(int xpos, int ypos) {
        PickResult pickResult = null;
        Sphere sphere = null;

        // Defines the location on the canvas where the pick is to be performed
        pickCanvas.setShapeLocation(xpos, ypos);

        // Pick the closest object - generally what we meant to click
        pickResult = pickCanvas.pickClosest();
        if (pickResult != null) {
            // We're only interested in primitive spheres - ignore anything else
            sphere = (Sphere) pickResult.getNode(PickResult.PRIMITIVE);
        }

        if (sphere != null) {
            // See if the meteor has already been shot
            if (sphere.getName() == null) {
                sphere.setName("Shot");
                RunFlightSim.updateScore();
                score.setString("Score: " + RunFlightSim.getScore());
            }

            // Make the meteor go on fire, symbolising that it has been shot
            Texture tex = new TextureLoader(cl.getResource("fire.jpg"), null).getTexture();
            Appearance newApp = new Appearance();
            newApp.setTexture(tex);
            sphere.setAppearance(newApp);
        }
    }
}