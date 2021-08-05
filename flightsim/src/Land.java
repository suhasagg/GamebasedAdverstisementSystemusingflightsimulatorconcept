import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.net.URL;

/**
 * A subclass of {@link TransformGroup} which creates a large peace of 'collidable' land.
 *
 * @author Cormac Redmond -- credmond85 /at/ gmail
 */
public class Land extends TransformGroup {

    private final float LAND_WIDTH = 300.0f;
    private final float LAND_HEIGHT = 0.0f;
    private final float LAND_LENGTH = -300.0f;

    // Texture location
    private URL texture;
    Appearance app = new Appearance();
    Text2D collisions;

    /**
     * This constructor should be used.
     *
     * @param collisionsP a {@link Text2D} which is will be updated with the score
     * @param textureLocP {@link URL} of desired texture image
     */
    public Land(Text2D collisionsP, URL textureLocP) {
        this.texture = textureLocP;
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
        // QuadArray to represent the land
        QuadArray quadArray = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

        float[] coordArray = {
                -LAND_WIDTH, LAND_HEIGHT, 0,
                LAND_WIDTH, LAND_HEIGHT, 0,
                LAND_WIDTH, LAND_HEIGHT, LAND_LENGTH,
                -LAND_WIDTH, LAND_HEIGHT, LAND_LENGTH
        };

        float[] texArray = {0, 0, 1, 0, 1, 1, 0, 1};
        quadArray.setCoordinates(0, coordArray, 0, 4);
        quadArray.setTextureCoordinates(0, 0, texArray, 0, 4);

        TextureAttributes textureAttrib = new TextureAttributes();
        Transform3D textureTransform = new Transform3D();

        // Make texture tile itself
        textureTransform.setScale(new Vector3d(50.0f, 50.0, 1.0f));

        textureAttrib.setTextureTransform(textureTransform);

        Texture tex = new TextureLoader(texture, null).getTexture();
        app.setTextureAttributes(textureAttrib);
        app.setTexture(tex);

        // Create the land with the Appearance node
        Shape3D sh = new Shape3D(quadArray, app);

        // Add the land to this, the TransformGroup
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
