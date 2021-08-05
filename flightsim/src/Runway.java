import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.net.URL;

/**
 * A subclass of {@link TransformGroup} that creates a 'collidable' runway.
 *
 * @author Cormac Redmond -- credmond85 /at/ gmail
 */
public class Runway extends TransformGroup {
    public static final float WIDTH = 3.0f;
    public static final float HEIGHT = 0.01f;
    public static final float LENGTH = -300.0f;

    private Appearance app = new Appearance();

    // Texture location
    private URL textureLoc;
    Text2D collisions;

    /**
     * This constructor should always be used.
     *
     * @param collisionsP passed into to keep track of score
     * @param textureLocP {@link URL} of texture image
     */
    public Runway(Text2D collisionsP, URL textureLocP) {
        this.textureLoc = textureLocP;
        this.collisions = collisionsP;
        createObject();
    }

    private Bounds getGeometryBounds() {
        return new BoundingSphere(new Point3d(0, 0, 0), 300);
    }

    /**
     * Creates the object and adds to itself
     */
    public void createObject() {
        QuadArray quadArray = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

        float[] coordArray = {-WIDTH, HEIGHT, 0,
                WIDTH, HEIGHT, 0,
                WIDTH, HEIGHT, LENGTH,
                -WIDTH, HEIGHT, LENGTH
        };

        float[] texArray = {0, 0, 1, 0, 1, 1, 0, 1};

        quadArray.setCoordinates(0, coordArray, 0, 4);

        quadArray.setTextureCoordinates(0, 0, texArray, 0, 4);
        Texture tex = new TextureLoader(textureLoc, null).getTexture();

        TextureAttributes textureAttrib = new TextureAttributes();
        Transform3D textureTransform = new Transform3D();

        // Make texture tile itself along the road
        textureTransform.setScale(new Vector3d(1.0f, 15.0, 1.0f));

        textureAttrib.setTextureTransform(textureTransform);

        // Create the land with the Appearance node
        app.setTextureAttributes(textureAttrib);
        app.setTexture(tex);

        Shape3D sh = new Shape3D(quadArray, app);

        this.addChild(sh);

        // Set ENABLE_COLLISION_REPORTING so this transform is checked for collision
        this.setCapability(ENABLE_COLLISION_REPORTING);
        this.setCollidable(true);

        // Set the bounds - e.g. How 'far' to check for collisions
        this.setCollisionBounds(getGeometryBounds());
        CollideBehaviour collision = new CollideBehaviour(collisions, this);
        collision.setSchedulingBounds(getGeometryBounds());

        // Add the collision behaviour
        this.addChild(collision);
	}	
}
