import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.net.URL;

/**
 * A subclass of {@link TransformGroup} which represents a spinning, 'pickable', 'collidable' random sized meteor.
 *
 * @author Cormac Redmond -- credmond85 /at/ gmail
 */
public class Meteor extends TransformGroup {
    public static final float WIDTH = 0.5f;
    public static final float HEIGHT = 3.0f;
    public static final float LENGTH = 0.9f;

    private URL textureLoc;
    Appearance app = new Appearance();
    private Vector3d position = new Vector3d(0, 0, 0);
    private Vector3d scale = new Vector3d(1, 1, 1);
    Text2D collisions;

    /**
     * This constructor should be used.
     *
     * @param scoreP      passed into collision behavior to keep track of score
     * @param textureLocP {@link URL} of texture image
     * @param positionP   position to place meteor
     * @param scaleP      scale to random sizes
     */
    public Meteor(Text2D scoreP, URL textureLocP, Vector3d positionP, Vector3d scaleP) {
        this.textureLoc = textureLocP;
        position = positionP;
        scale = scaleP;
        collisions = scoreP;
        createObject();
    }

    /**
     * Creates the object and adds to itself.
     */
    public void createObject() {
        Transform3D t3d = new Transform3D();

        // Scale and position the meteor
        t3d.setScale(scale);
        t3d.setTranslation(position);
        this.setTransform(t3d);

        // Setup texture
        Texture tex = new TextureLoader(textureLoc, null).getTexture();
        app.setTexture(tex);

        // Create a new TransformGroup
        TransformGroup rotatingTG = new TransformGroup();

        // Add a sphere/meteor that is pickable, and can have its appearances changed
        rotatingTG.addChild(new Sphere(WIDTH, Sphere.GENERATE_TEXTURE_COORDS | Sphere.ENABLE_PICK_REPORTING | Sphere.ENABLE_APPEARANCE_MODIFY, app));

        // Set rotatingTG to allow transform writes - neccessary for rotation
        rotatingTG.setCapability(ALLOW_TRANSFORM_WRITE);

        // Everything in this TransformGroup (one TransformGroup holding a sphere) is 'collidable'
        this.setCollidable(true);
        this.setCollisionBounds(getGeometryBounds());
        CollideBehaviour collision = new CollideBehaviour(collisions, this);
        collision.setSchedulingBounds(getGeometryBounds());

        // Create a RotationInterpolator to spin the meteors
        Transform3D yAxis = new Transform3D();
        Alpha rotationAlpha = new Alpha(-1, 4000);

        RotationInterpolator rotator = new RotationInterpolator(rotationAlpha,
                rotatingTG, yAxis, 0.0f,
                (float) Math.PI * 2.0f);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        rotator.setSchedulingBounds(bounds);

        rotatingTG.addChild(rotator);

        //Add rotation and collision behaviours
        this.addChild(rotatingTG);
        this.addChild(collision);
    }

    public Bounds getGeometryBounds() {
        return new BoundingSphere(new Point3d(0, 0, 0), 1);
    }
}