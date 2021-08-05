import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewerAvatar;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

/**
 * Creates and sets up the Java3D game.
 * <p/>
 * The game is basically flying a plane, shooting asteroids,
 * while trying to avoid collisions.
 *
 * @author Cormac Redmond -- credmond85 /at/ gmail
 */
public class RunFlightSim extends JPanel implements ActionListener {
    private Background back = new Background();
    private static int noOfCollisions = 0;
    private static int score = 0;
    private Text2D collisions2D;
    private Text2D score2D;

    ClassLoader cl = this.getClass().getClassLoader();

    public RunFlightSim() throws IOException {
        setLayout(new BorderLayout());
        init();
    }

    /**
     * Static method called by {@link Behavior}s to update or get collision count
     */
    public static void updateCollisions() {
        noOfCollisions++;
    }

    public static int getCollisions() {
        return noOfCollisions;
    }

    /**
     * Static method called by {@link Behavior}s to update or get score count
     */
    public static void updateScore() {
        score++;
    }

    public static int getScore() {
        return score;
    }

    // Creates a random number between a minus and plus range
    private float getRandomNumber(float basis, float random) {
        return basis + (float) Math.random() * random * 2 - (random);
    }

    /**
     * Creates and returns a {@link BoundingSphere}.
     *
     * @return the {@link BoundingSphere}.
     */
    public BoundingSphere getBoundingSphere() {
        return new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 400.0);
    }

    /**
     * This method sets up the main scene
     *
     * @return BranchGroup the populated scene
     */
    public BranchGroup createSceneGraph() {
        BranchGroup bg = new BranchGroup();

        // Add the grassy land
        bg.addChild(new Land(collisions2D, cl.getResource("data/land.jpg")));

        // Add the runway
        bg.addChild(new Runway(collisions2D, cl.getResource("data/1.png")));

        // Add a number of targets/meteors, in accordance with runway length
        for (int n = (int) Runway.LENGTH; n < 0; n = n + 5) {
            double meteorSize = (Math.random() * 2) + .5;
            Meteor car = new Meteor(collisions2D, cl.getResource("data/1.png"),
                    new Vector3d(getRandomNumber(0.0f, 150.0f),
                            Math.random() * 10 + 3,
                            getRandomNumber(n, 5.0f)),
                    new Vector3d(meteorSize, meteorSize, meteorSize));

            bg.addChild(car);
        }

        // Add a textured background and set the capability the change it at runtime
        back.setCapability(Background.ALLOW_GEOMETRY_WRITE);
        back.setApplicationBounds(getBoundingSphere());
        bg.addChild(back);

        BranchGroup bgGeometry = new BranchGroup();
        Appearance app = new Appearance();

        Texture tex = new TextureLoader(cl.getResource("data/sky.jpg"), this).getTexture();
        app.setTexture(tex);

        Sphere sphere = new Sphere(1.0f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD, app);
        bgGeometry.addChild(sphere);

        // Background is an inverted textured sphere
        back.setGeometry(bgGeometry);

        // Return the created scene
        return bg;
    }

    /**
     * Method to alter the background image
     *
     * @param bg a boolean determining whether to turn background on/true or off/false
     */
    private void setBG(boolean bg) {
        BranchGroup bgGeometry = new BranchGroup();
        Appearance app = new Appearance();

        //If true, use a background
        if (bg) {
            Texture tex = new TextureLoader(cl.getResource("data/sky.jpg"), this).getTexture();
            app.setTexture(tex);
        } else {
            Texture tex = null;
            app.setTexture(tex);
        }

        Sphere sphere = new Sphere(1.0f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD, app);
        bgGeometry.addChild(sphere);
        back.setGeometry(bgGeometry);
    }

    /**
     * Creates an 'avatar' - an avatar is how the user's "virtual self" appears in the virtual world.
     * </p>
     * The avatar consists of the cockpit, a collision count, and a score count
     *
     * @return ViewerAvatar the avatar
     * @throws IOException 
     */
    private ViewerAvatar createAvatar() throws IOException {
        ViewerAvatar va = new ViewerAvatar();
        TransformGroup tg = new TransformGroup();

        // The dashboard/cockit
    /*    TextureLoader loader = new TextureLoader(cl.getResource("1.png"), null);
        ImageComponent2D image = loader.getImage();
  */
        java.awt.image.BufferedImage image1 = null;
        image1 = javax.imageio.ImageIO.read(new java.io.File("data/cockpit.jpg"));
        // Creates the texture
        ImageComponent2D image = new ImageComponent2D (ImageComponent.FORMAT_RGB, image1);
        Texture2D texture = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGB, image1.getWidth(), image1.getHeight());
        texture.setImage(0,image);
        
        // Create a TextureAttributes to control how the texture is displayed
        TextureAttributes textureAttrib = new TextureAttributes();
        Transform3D textureTransform = new Transform3D();
        textureAttrib.setTextureTransform(textureTransform);

        // Create an Appearance and set the texture, attributes and material
        Appearance floorAppearance = new Appearance();

        floorAppearance.setTexture(texture);
        floorAppearance.setTextureAttributes(textureAttrib);
        floorAppearance.setMaterial(new Material());
        Box car = new Box(1.4f, 0.23f, .0015f, Box.GENERATE_TEXTURE_COORDS, floorAppearance);

        Transform3D t3d = new Transform3D();

        // Create the Text2D to display number of collisions
        TransformGroup tg2 = new TransformGroup();
        collisions2D = new Text2D("Collisions: " + noOfCollisions, new Color3f(0.1f, 0.2f, .3f),
                "Helvetica", 18, Font.ITALIC);

        // Create the Text2D to display score
        TransformGroup tg3 = new TransformGroup();
        score2D = new Text2D("Score: " + score, new Color3f(0.1f, 0.2f, .3f),
                "Helvetica", 18, Font.ITALIC);

        // Scales, translates and adds the cockpit
        t3d.setScale(new Vector3d(.30, .35, 1.0));
        t3d.setTranslation(new Vector3d(0.0, -0.297, -1.0));
        tg.setTransform(t3d);

        // Scales, translates and adds the collision counter
        t3d.setScale(new Vector3d(.30, .35, 1.0));
        t3d.setTranslation(new Vector3d(-0.4, .35, -1.1));
        tg2.setTransform(t3d);

        // Scales, translates and adds score counter
        t3d.setScale(new Vector3d(.30, .35, 1.0));
        t3d.setTranslation(new Vector3d(.3, .35, -1.1));
        tg3.setTransform(t3d);

        tg.addChild(car);
        tg2.addChild(collisions2D);
        tg3.addChild(score2D);
        va.addChild(tg);
        va.addChild(tg2);
        va.addChild(tg3);
        return va;
    }

    /**
     * Calls all the relevant methods to setup and display game
     * @throws IOException 
     */
    public void init() throws IOException {
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        SimpleUniverse universe = new SimpleUniverse(canvas);
        TransformGroup tg = universe.getViewer().getViewingPlatform().getViewPlatformTransform();

        setLayout(new BorderLayout());
        add("Center", canvas);

        BoundingSphere boundingSphere = new BoundingSphere(new Point3d(0f, 0f, 0f), 600f);

        Transform3D t3d = new Transform3D();

        // Move viewer up .5
        t3d.set(new Vector3f(0, 0.5f, 0));
        tg.setTransform(t3d);

        // Add the behaviour class PlaneControls which detects key presses
        PlaneControls controls = new PlaneControls(tg);
        controls.setSchedulingBounds(boundingSphere);

        universe.getViewer().setAvatar(createAvatar());

        // Add everything to the scene graph - it will now be displayed.
        BranchGroup scene = createSceneGraph();
        scene.addChild(controls);

        // Create PickBehaviour to detect mouse clicks
        new PickBehaviour(canvas, scene, boundingSphere, score2D);

        universe.addBranchGraph(scene);
    }

    /**
     * Called when the there is an action on the JPanel.
     *
     * @param ae the {@link java.awt.event.ActionEvent}
     */
    public void actionPerformed(ActionEvent ae) {
        java.util.StringTokenizer toker = new java.util.StringTokenizer(ae.getActionCommand(), "|");

        String menu = toker.nextToken();
        String command = toker.nextToken();

        if (menu.equals("File")) {
            if (command.equals("Exit")) {
                System.exit(0);
            }
        } else if (menu.equals("Background")) {
            if (command.equals("On")) {
                setBG(true);
            } else if (command.equals("Off")) {
                setBG(false);
            }
        }
    }

    // Helper method to creates a Swing JMenuItem.
    private JMenuItem createMenuItem(String menuText, String buttonText, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(buttonText);
        menuItem.addActionListener(listener);
        menuItem.setActionCommand(menuText + "|" + buttonText);
        return menuItem;
    }

    /**
     * Registers a window listener to handle ALT+F4 window closing.
     *
     * @param frame the JFrame for which we want to intercept close
     *              messages
     */
    static protected void registerWindowListener(JFrame frame) {
        // disable automatic close support for Swing frame.
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // adds the window listener
        frame.addWindowListener(
                new WindowAdapter() {
                    // handles the system exit window message
                    public void windowClosing(WindowEvent e) {
                        System.exit(1);
                    }
                }
        );
    }

    /**
     * Creates the menu.
     *
     * @return the populated menu
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = null;

        menu = new JMenu("File");
        menu.add(createMenuItem("File", "Exit", this));
        menuBar.add(menu);

        menu = new JMenu("Background");
        menu.add(createMenuItem("Background", "On", this));
        menu.add(createMenuItem("Background", "Off", this));
        menuBar.add(menu);

        return menuBar;
    }

    /**
     * Main entry point for the application. Creates the parent
     * JFrame, the JMenuBar and creates the JPanel which is the
     * application itself.
     *
     * @param args arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setLightWeightPopupEnabled(false);

        JFrame frame = new JFrame();

        RunFlightSim swingTest = new RunFlightSim();
        frame.setJMenuBar(swingTest.createMenuBar());

        frame.getContentPane().add(swingTest);
        frame.setSize(550, 550);
        registerWindowListener(frame);

        frame.setVisible(true);
    }
}

