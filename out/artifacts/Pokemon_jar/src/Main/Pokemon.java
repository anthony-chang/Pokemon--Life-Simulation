package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Pokemon implements ActionListener{
    private static JFrame mainFrame; //window
    public static LifeSimulation environment; //simulation
    private static boolean[][] lakePostition;
    public static WorkingInventory inventoryWindow; //inventory window
    private static PokemonMenuGUI menu = new PokemonMenuGUI(); //menu and pause window
    public static Player player; //user
    private static Draw back; //background
    private static Draw inventoryBtn; //image of the inventory button
    private static JButton actualInventoryBtn; //actual JButton for inventory
    private static Draw pause; //pause button icon
    private static JButton pauseBtn; //actual JButton for pausing
    private JComponent component; //JComponent of player
    private Timer timer; //for animations
    private Map<String, Point> pressedKeys = new HashMap<>(); //key bindings

    //to map key bindings later
    private final static String PRESSED = "pressed ";
    private final static String RELEASED = "released ";

    public Pokemon() {
        //import the images for the walking animations
        BufferedImage[] forFrames = {getImg('f', 1),getImg('f', 2),getImg('f', 3),getImg('f', 4)};
        BufferedImage[] leftFrames = {getImg('l', 1),getImg('l', 2),getImg('l', 3),getImg('l', 4)};
        BufferedImage[] backFrames = {getImg('b', 1),getImg('b', 2),getImg('b', 3),getImg('b', 4)};
        BufferedImage[] rightFrames = {getImg('r', 1),getImg('r', 2),getImg('r', 3),getImg('r', 4)};

        player = new Player(forFrames, leftFrames, backFrames, rightFrames); //initialize the user

        player.setSize(32, 32);
        player.setLocation(0, 0);
        //player.setBounds(610, 330, 32, 32);

        Pokemon game = new Pokemon(player, 50);
        game.addAction("LEFT", -7,  0); //-7 for dx of left arrow
        game.addAction("RIGHT", 7,  0); //7 for dx of right arrow
        game.addAction("UP",    0, -7); //7 for dy of up arrow
        game.addAction("DOWN",  0,  7); //-7 for dy of down array
        //same thing for left handed uers
        game.addAction("W", 0, -7);
        game.addAction("A", -7, 0);
        game.addAction("S", 0, 7);
        game.addAction("D", 7, 0);
    }
    public Pokemon(JComponent component, int delay) {

        this.component = component; //player
        //timer stuff for animations
        timer = new Timer(delay, this);
        timer.setInitialDelay(0);

        mainFrame = new JFrame("Pokemon");
        mainFrame.setLayout(new BorderLayout());
        //menu.setVisible(false);
        inventoryWindow = new WorkingInventory();
        inventoryWindow.setVisible(false);

        //import images
        BufferedImage background = null;
        BufferedImage inventoryImg = null;
        BufferedImage pauseImg = null;
        try {
            background = ImageIO.read(new File("Graphics\\grassBackground.png"));
            inventoryImg = ImageIO.read(new File("Graphics\\button.png"));
            pauseImg = ImageIO.read(new File("Graphics\\pause.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //initialize all the Draw components, set the locations and size
        back = new Draw(background, 0, 0);
        back.setLocation(0, 0);
        inventoryBtn = new Draw(inventoryImg, 0, 0);
        inventoryBtn.setSize(300, 95);
        inventoryBtn.setLocation(760,570);
        actualInventoryBtn = new JButton("Inventory");
        actualInventoryBtn.addActionListener(this);
        actualInventoryBtn.setSize(300, 95);
        actualInventoryBtn.setLocation(760, 570);
        pause = new Draw(pauseImg, 0 ,0);
        pause.setSize(70, 70);
        pause.setLocation(1000, 10);
        pauseBtn = new JButton("Pause");
        pauseBtn.addActionListener(this);
        pauseBtn.setSize(70, 70);
        pauseBtn.setLocation(1000, 10);

        environment = new LifeSimulation(); //initialize the environment
        lakePostition = environment.getLake();
        //needs JLayeredPane for some reason, otherwise background is in front of the sprite
        JLayeredPane content = mainFrame.getLayeredPane();
        //add all the components to the JLayeredPane
        /*
        Layer 0 for background
        Layer 1 for the environment and Pokemon
        Layer 2 for JButtons, and the player sprite
        Layer 3 for the interface things
        */
        content.add(player, 2, 0);
        content.add(environment, 1, 0);
        content.add(inventoryBtn, 3, 0);
        content.add(actualInventoryBtn, 2, 0);
        content.add(back, 0, 0);
        content.add(pause, 3, 0);
        content.add(pauseBtn, 2, 0);

        //window stuff
        mainFrame.setSize(1080, 720);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        mainFrame.setFocusable(true);
        mainFrame.setVisible(false);
    }
    public void toggleVis() { //toggle the visibility of the main window
        mainFrame.setVisible(!mainFrame.isVisible());
    }
    public void setVis() { //make the main window visible
        mainFrame.setVisible(true);
    }
    private static BufferedImage getImg(char c, int x)  { //import sprite images
        try {
            return ImageIO.read(new File("PlayerSprite\\"+c+x+".png"));
        } catch (IOException e) {
            e.printStackTrace() ;
            return null;
        }
    }
    public void addAction(String keyStroke, int dx, int dy) { //set up action map and input map of keys
        int offset = keyStroke.lastIndexOf(" ");
        String key = offset == -1 ? keyStroke : keyStroke.substring(offset + 1);
        String modifiers = keyStroke.replace(key, "");

        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();
        //button pressed
        Action pressedAction = new AnimationAction(key, new Point(dx, dy));
        String pressedKey = modifiers + PRESSED + key;
        KeyStroke pressedKeyStroke = KeyStroke.getKeyStroke(pressedKey);
        inputMap.put(pressedKeyStroke, pressedKey);
        actionMap.put(pressedKey, pressedAction);
        //button released
        Action releasedAction = new AnimationAction(key, null);
        String releasedKey = modifiers + RELEASED + key;
        KeyStroke releasedKeyStroke = KeyStroke.getKeyStroke(releasedKey);
        inputMap.put(releasedKeyStroke, releasedKey);
        actionMap.put(releasedKey, releasedAction);

    }
    private void handleKeyEvent(String key, Point moveDelta) {
        //helper method to fill the map of keys using the AbstractAction Interface
        if (moveDelta == null)
            pressedKeys.remove(key);
        else
            pressedKeys.put(key, moveDelta);

        //start and pause the timer for walking animations
        if (pressedKeys.size() == 1)
            timer.start();
        if (pressedKeys.size() == 0)
            timer.stop();

    }
    public void setInventory(boolean b) { //open the inventory window
        inventoryWindow.setVisible(b);
    }
    public void actionPerformed(ActionEvent e) { //moves the player sprite
        moveComponent(); //movement player sprite (actual user input handled elsewhere)
        try {
            if (e.getActionCommand().equals("Inventory")) { //open inventory
                setInventory(true);
            }
            else if (e.getActionCommand().equals("Pause")) { //open pause menu
                //mainFrame.setVisible(false);
                timer.stop();
                environment.stopTimer();
                menu.setVisible(true);
                menu.setAlwaysOnTop(true);
            }
        } catch (NullPointerException ignored){} //e.getActionCommand for a keyboard button
        // throws NullPointerException
    }
    public void startSimulation() {
        environment.startTimer();
    }
    private void moveComponent() {
        int componentWidth = component.getSize().width;
        int componentHeight = component.getSize().height;

        //if 1080x720, sprite sometimes moves off screen
        int parentWidth  = 1065;
        int parentHeight = 665;

        int deltaX = 0;
        int deltaY = 0;
        //calculate change in pos
        for (Point delta : pressedKeys.values()) {
            deltaX += delta.x;
            deltaY += delta.y;
        }
        try {
            if (deltaX >= 0 && lakePostition[(component.getLocation().y + deltaY) / 4][(component.getLocation().x + deltaX + componentWidth)/4])
                deltaX = 0;
            else if (deltaX < 0 && lakePostition[(component.getLocation().y + deltaY) / 4][(component.getLocation().x + deltaX)/4])
                deltaX = 0;

            if (deltaY >= 0 && lakePostition[(component.getLocation().y + deltaY + componentHeight) / 4][(component.getLocation().x + deltaX)/4])
                deltaY = 0;
            else if (deltaY < 0 && lakePostition[(component.getLocation().y + deltaY + componentHeight - 10) / 4][(component.getLocation().x + deltaX)/4])
                deltaY = 0;
        }catch (ArrayIndexOutOfBoundsException ignored){}

        //keeps sprite within screen
        int nextX = Math.max(component.getLocation().x + deltaX, 0);
        if ( nextX + componentWidth > parentWidth)
            nextX = parentWidth - componentWidth;

        int nextY = Math.max(component.getLocation().y + deltaY, 0);
        if ( nextY + componentHeight > parentHeight)
            nextY = parentHeight - componentHeight;


        //move the sprite on the screen
        component.setLocation(nextX, nextY);
        player.setLocation(nextX, nextY);
        //walking animation
        player.buttonPressed(deltaX, deltaY);

    }
    private class AnimationAction extends AbstractAction implements ActionListener {
        private Point moveDelta;
        //the object inside the ActionMap (copied off internet)
        public AnimationAction(String key, Point moveDelta) {
            super(key);
            this.moveDelta = moveDelta;
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println((String)getValue(NAME));
            handleKeyEvent((String)getValue(NAME), moveDelta);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                menu.setVisible (true);
            }
        });
    }
}

class LifeSimulation extends JPanel {
    //declarations and other stuff
    public static Environment colony = new Environment ();
    private static Timer t;
    private static Movement moveColony;


    public LifeSimulation() {
        moveColony = new Movement(colony); //moves the pokemon around
        t = new Timer(210, moveColony); // set up timer
        t.setDelay(210);
        t.start();
        setSize(1080, 720);
    }
    public void stopTimer() {
        t.stop();
    }
    public void startTimer() {
        t.start();
    }
    public boolean[][] getLake() {
        return colony.lakePos();
    }
    public void paintComponent(Graphics g) {
        colony.show(g);
    }

    class Movement implements ActionListener {
        //private Environment colony;

        public Movement (Environment col) {
            colony = col;
        }
        public void actionPerformed (ActionEvent event) {
            //colony.pokeUpdate();
            paintImmediately(0, 0, 1080, 720);
            //repaint();
        }
    }
}
class Draw extends JPanel { //handles all of the graphics stuff (except for the environment)
    private BufferedImage draw;
    private int x, y;
    public Draw (BufferedImage img, int x, int y) {
        this.setPreferredSize(new Dimension(1080, 720));
        draw = img;
        this.x = x;
        this.y = y;
        //repaint();
    }
    public void change(BufferedImage img) { //change the image
        draw = img;
        //redraw(); //cannot redraw(): keeps on flashing
    }
    public void redraw() { //repaint
        paintImmediately(x, y, draw.getWidth(), draw.getHeight());
        //repaint();
    }
    public void paintComponent(Graphics g) { //magic
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(draw, x, y, this);
    }
}
//@SuppressWarnings("Duplicates")
class Player extends Draw{
    private BufferedImage sprite; //current frame/image
    private int ind; //index of the current frame/image
    private int x, y;
    //stores the forward walking animation frames, left walking, and so on
    private BufferedImage[] forFrames, leftFrames, backFrames, rightFrames;

    public Player (BufferedImage[] forFrames, BufferedImage[] leftFrames, BufferedImage[] backFrames, BufferedImage[] rightFrames) {
        super(forFrames[0], 0, 0); //starting frame
        this.forFrames = forFrames;
        this.leftFrames = leftFrames;
        this.backFrames = backFrames;
        this.rightFrames = rightFrames;
        sprite = forFrames[0];
        ind = 0;
        x = 0;
        y = 0;
    }

    public BufferedImage getSprite() { //get the current frame
        return sprite;
    }
    public void buttonPressed(int dx, int dy) { //change the frames to animate
        //ind mod 4 since there are only 4 frames
        if (dx < 0)
            sprite = leftFrames[ind++%4];
        if (dx > 0)
            sprite = rightFrames[ind++%4];
        if (dy < 0)
            sprite = backFrames[ind++%4];
        if (dy > 0)
            sprite = forFrames[ind++%4];
        if (ind > 3) //so that we don't get integer overflow error
            ind = 0;
        super.change(sprite); //update the sprite (basically the only reason why this is a child of Draw)
    }
}

class Environment{
    public Colour[][] grid; //life
    private long counter = 0;
    //images
    private BufferedImage water;
    private BufferedImage tree;
    private BufferedImage beedrill;
    private BufferedImage squirtle;
    private BufferedImage growlithe;
    private BufferedImage gastly;
    private BufferedImage backgroundImg;
    //stores all the relevant properties of each pokemon
    private ArrayList<Structure2> treeLoc; //(y,x)
    public ArrayList <Structure6> beeLoc; //(y, x, destination y, destination x, find new tree?, health)
    public ArrayList <Structure5> squirtleLoc; //(y, x, destination y, destination x, health)
    public ArrayList <Structure7> growLoc; //(y, x, speed y, speed x, general direction, wait time, health)
    public ArrayList <Structure5> gasLoc; //(y, x, destination y, destination x, health)

    private boolean[][][] pos = new boolean[271][181][4]; //col (x), rows (y),
    // pokemon (0=beedrill, 1=squirtle, 2=growlithe, 3=gastly)
    private final int XMAX = 181, YMAX = 271;

    private ArrayList<Integer> removee = new ArrayList <Integer>();
    int p = (int) (Math.random() * 30+40);
    double avgx=0, avgy=0;
    int rand[] = {-1, 0, 1};
    int gasMove[][] = {{20, 20}, {150, 240}, {150, 20}, {20, 240}, {135, 90}};

    public Environment() { //default randomly generated colony
        //initializes ArrayLists
        treeLoc = new ArrayList<Structure2>();
        beeLoc = new ArrayList<Structure6>();
        squirtleLoc = new ArrayList<Structure5>();
        growLoc = new ArrayList<Structure7>();
        gasLoc = new ArrayList<Structure5>();

        //generates beedrills
        for (int i=0; i<15; i++) {
            int a = (int) (Math.random() * 174);
            int b = (int) (Math.random() * 254);
            beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
        }

        //generates squirtles
        for (int i=0; i<7; i++) {
            int randomx = (int) (Math.random()*251);
            int randomy = (int) (Math.random()*161);
            squirtleLoc.add(new Structure5(randomy, randomx, randomy, randomx, (int) (Math.random()*41)+60));
        }

        //generates gastly
        for (int i=0; i<4; i++) {
            int randomx = (int) (Math.random() * 254);
            int randomy = (int) (Math.random() * 174);
            gasLoc.add(new Structure5(randomy, randomx, 90, 135, (int) (Math.random()*21)+80));
        }

        //generates default grid
        grid = new Colour[180][270];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                grid[row][col] = new Colour();
                grid[row][col].setGrass();
            }
        }

        //creates a random number of randomly sized lakes
        int num = (int) (Math.random() * 5);
        int x, y;
        for (int i=0; i<5; i++){
            int w = (int) (Math.random()*23+30);
            x = (int) (Math.random()*(270-2*w-1));
            y = (int) (Math.random()*(180-2*w-1));
            Lake(y, x, 2*w+1);
        }

        //tree generation
        for (int i=0; i<p; i++){
            avgx=0;
            avgy=0;

            //stores the trees into an ArrayList
            if (!treeLoc.isEmpty()){
                for (Structure2 e: treeLoc){
                    avgy+=e.first;
                    avgx+=e.first;
                }
            }

            avgx/=treeLoc.size();
            avgy/=treeLoc.size();
            int xtree = (int) (Math.random()*259);
            int ytree = (int) (Math.random()*169);
            int w = (int) (Math.random()*70-35);
            int z = (int) (Math.random()*70-35);

            //clusters trees near each other on the map
            if (!treeLoc.isEmpty() && treeLoc.size() < 15 && i>=12) {
                xtree = (int) ((int) (Math.log10(treeLoc.size()) * avgx + xtree) / (Math.log10(treeLoc.size()) + 1))+z;
                ytree = (int) ((int) (Math.log10(treeLoc.size()) * avgy + ytree) / (Math.log10(treeLoc.size()) + 1))+w;
            }
            else if (!treeLoc.isEmpty() && treeLoc.size() >= 15 && i >= 12) {
                xtree = (int) ((int) (Math.log10(15) * avgx + xtree) / (Math.log10(15) + 1))+z;
                ytree = (int) ((int) (Math.log10(15) * avgy + ytree) / (Math.log10(15) + 1))+w;
            }

            //ensures the trees don't appear over water
            try {
                if (!grid[ytree][xtree].isLake() && !grid[ytree + 10][xtree + 10].isLake() &&
                        !grid[ytree + 10][xtree].isLake() && !grid[ytree][xtree + 10].isLake()) {
                    //g.drawImage(tree, 4 * xtree, 4 * ytree, 40, 40, null);
                    treeLoc.add(new Structure2(ytree,xtree));
                }
            } catch (ArrayIndexOutOfBoundsException e){
            }
        }
        //loads images
        try {
            water = ImageIO.read(new File("Graphics\\watertile.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            tree = ImageIO.read(new File("Graphics\\tree.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            beedrill = ImageIO.read(new File("Graphics\\beedrill.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            squirtle = ImageIO.read(new File("Graphics\\squirtle.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            growlithe = ImageIO.read(new File("Graphics\\growlithe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            gastly = ImageIO.read(new File("Graphics\\gastly.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            backgroundImg = ImageIO.read(new File("graphics\\grassBackground.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //generates growlithes
        int growi = 0;
        while (growi < 10){
            //ensures surrounding area is not a lake
            int a = (int) (Math.random() * 134);
            int b = (int) (Math.random() * 214);
            boolean not = true;
            for (int i=-3; i<3; i++) {
                for (int j=-3; j<3; j++) {
                    try {
                        if (grid[a + i][b + j].isLake() || grid[a + i][b + j].isBlue()) {
                            not = false;
                            break;
                        }
                    } catch (Exception e){}
                }
            }
            if (not){
                growLoc.add(new Structure7(a, b, (int) (Math.random() * 2+2), (int) (Math.random() * 2 + 2), 0, 3, (int) (Math.random()*81)+20));
                growi++;
            }
        }

    }

    //creates new growlithe on the map
    public void genGrow(){
        int growi = 0;
        while (growi < 3){
            int a = (int) (Math.random() * 305-20);
            int b = (int) (Math.random() * 215-20);
            //ensures the growlithe appear outside the edge of the screen
            try{
                if (a < -5 && b >= 90){
                    growLoc.add(new Structure7(b, a, -(int) (Math.random() * 3+2), (int) (Math.random() * 3 + 2), 0, 2, (int) (Math.random()*81)+20));
                    growi++;
                }
                else if (a < -5 && b < 90){
                    growLoc.add(new Structure7(b, a, (int) (Math.random() * 3+2), (int) (Math.random() * 3 + 2), 0, 1, (int) (Math.random()*81)+20));
                    growi++;
                }
                else if (b < -5 && a >= 135){
                    growLoc.add(new Structure7(b, a, (int) (Math.random() * 3+2), -(int) (Math.random() * 3 + 2), 0, 0, (int) (Math.random()*81)+20));
                    growi++;
                }
                else if (b < -5 && a < 135){
                    growLoc.add(new Structure7(b, a, (int) (Math.random() * 3+2), (int) (Math.random() * 3 + 2), 0, 1, (int) (Math.random()*81)+20));
                    growi++;
                }
                else if (a > 270 && b >=90){
                    growLoc.add(new Structure7(b, a, -(int) (Math.random() * 3+2), -(int) (Math.random() * 3 + 2), 0, 3, (int) (Math.random()*81)+20));
                    growi++;
                }
                else if (a > 270 && b < 90){
                    growLoc.add(new Structure7(b, a, (int) (Math.random() * 3+2), -(int) (Math.random() * 3 + 2), 0, 0, (int) (Math.random()*81)+20));
                    growi++;

                }
                else if (b > 180 && a >= 135){
                    growLoc.add(new Structure7(b, a, -(int) (Math.random() * 3+2), -(int) (Math.random() * 3 + 2), 0, 3, (int) (Math.random()*81)+20));
                    growi++;

                }
                else if (b > 180 && a < 135){
                    growLoc.add(new Structure7(b, a, -(int) (Math.random() * 3+2), (int) (Math.random() * 3 + 2), 0, 2, (int) (Math.random()*81)+20));
                    growi++;

                }
            } catch (Exception e){}
        }
    }

    public void genSquir(){
        int cont = 0;
        while (cont < 3){
            int a = (int) (Math.random() * 305-20);
            int b = (int) (Math.random() * 215-20);
            int randomx = (int) (Math.random()*251);
            int randomy = (int) (Math.random()*161);
            //ensures the growlithe appear outside the edge of the screen
            try{
                if (a < -5 && b >= 90){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;
                }
                else if (a < -5 && b < 90){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;
                }
                else if (b < -5 && a >= 135){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;
                }
                else if (b < -5 && a < 135){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;
                }
                else if (a > 270 && b >=90){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;
                }
                else if (a > 270 && b < 90){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;

                }
                else if (b > 180 && a >= 135){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;

                }
                else if (b > 180 && a < 135){
                    squirtleLoc.add(new Structure5(b, a, randomy, randomx, (int) (Math.random()*21)+80));
                    cont++;

                }
            } catch (Exception e){}
        }
    }

    public void genBee(){
        int cont = 0;
        while (cont < 3){
            int a = (int) (Math.random() * 305-20);
            int b = (int) (Math.random() * 215-20);
            //ensures the growlithe appear outside the edge of the screen
            try{
                if (a < -5 && b >= 90){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;
                }
                else if (a < -5 && b < 90){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;
                }
                else if (b < -5 && a >= 135){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;
                }
                else if (b < -5 && a < 135){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;
                }
                else if (a > 270 && b >=90){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;
                }
                else if (a > 270 && b < 90){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;

                }
                else if (b > 180 && a >= 135){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;

                }
                else if (b > 180 && a < 135){
                    beeLoc.add(new Structure6(a, b, a, b, true, (int) (Math.random()*81)+20));
                    cont++;

                }
            } catch (Exception e){}
        }
    }

    public void show(Graphics g) { //updates the pokemon
        //0.5% chance it will generate new growlithe, squirtle or beedrill
        int pp = (int) (Math.random()*1000);
        if (pp > 995){
            genGrow();
        }

        int gg = (int) (Math.random()*1000);
        if (gg > 995){
            genSquir();
        }

        int hh = (int) (Math.random()*1000);
        if (hh > 995){
            genBee();
        }


        //entire 720 x 1080 colony
        Color cur;
        g.drawImage(backgroundImg, 0, 0, null);
        //water = water.getSubimage(10, 10, 100, 100);
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                cur = new Color(grid[row][col].red, grid[row][col].green, grid[row][col].blue);
                g.setColor(cur);
                if (!cur.equals(Color.BLACK) && !cur.equals(new Color(144, 245, 0))) {
                    grid[row][col].setBlue();
                    g.drawImage(water, 4 * col, 4 * row, 4, 4, null);
                }
            }
        }
        //displays trees
        for (Structure2 treeArr : treeLoc) {
            try {
                g.drawImage(tree, 4 * treeArr.second, 4 * treeArr.first, 40, 40, null);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }

        //generates a border around the lakes
        int check[] = {-1, 0, 1};
        g.setColor(new Color (0, 0, 128));
        for (int row = 0; row < grid.length; row++){
            for (int col = 0; col < grid[0].length; col++){
                try {
                    //checks horizontal borders
                    if ((grid[row][col].blue == 255 && grid[row][col].green == 191
                            && grid[row][col+1].blue!=255 && grid[row][col+1].green != 191) ||
                            (grid[row][col].blue == 255 && grid[row][col].green == 191
                                    && grid[row][col-1].blue!=255 && grid[row][col-1].green != 191)){
                        for (int r: check){
                            for (int w: check) {
                                try{
                                    g.fillRect(4*(col+r), 4*(row+w), 4, 4);
                                } catch (Exception e){}
                            }
                        }
                    }
                    //checks vertical borders
                    if ((grid[row][col].blue == 255 && grid[row][col].green == 191
                            && grid[row+1][col].blue!=255 && grid[row+1][col].green != 191) ||
                            (grid[row][col].blue == 255 && grid[row][col].green == 191
                                    && grid[row-1][col].blue!=255 && grid[row-1][col].green != 191)){
                        for (int r: check){
                            for (int w: check) {
                                try{
                                    g.fillRect(4*(col+r), 4*(row+w), 4, 4);
                                } catch (Exception e){}
                            }
                        }
                    }
                } catch (Exception e) {}
            }
        }

        //updates beedrill
        //loops through each beedrill in ArrayList
        for (Structure6 bees: beeLoc) {
            int bestw = 0, bestv = 0;
            int xx = treeLoc.get(0).second, yy = treeLoc.get(0).first;

            //if the beedrill is close to its destination, it moves away to another tree
            if (Math.abs(bees.fourth-bees.second) + Math.abs(bees.third-bees.first)<10){
                bees.fifth = false;
            }

            //if the bee is far from a tree, it looks for any closest tree
            if (Math.abs(bees.fourth-bees.second) + Math.abs(bees.third-bees.first)>50){
                bees.fifth = true;
            }

            if (!bees.fifth){
                //finds new closest tree that is not the one it was approaching
                for (Structure2 tt : treeLoc) {
                    if (Math.abs(tt.second - bees.second) + Math.abs(tt.first - bees.first) <
                            Math.abs(xx - bees.second) + Math.abs(yy - bees.first) && tt.second!=bees.fourth &&
                            tt.first!=bees.third) {
                        xx = tt.second;
                        yy = tt.first;
                    }
                }

                //finds direction to move closer to tree
                for (int v = 0; v < 3; v++) {
                    for (int w = 0; w < 3; w++) {
                        int tempx, tempy;
                        tempy = bees.first + rand[w];
                        tempx = bees.second + rand[v];
                        if ((Math.pow(xx - (bees.second + bestv), 2) + Math.pow(yy - (bees.first + bestw), 2)) >=
                                (Math.pow(xx - tempx, 2) + Math.pow(yy - tempy, 2))) {
                            bestw = rand[w];
                            bestv = rand[v];
                        }
                    }
                }
                if (bestw == 0 && bestv == 0){
                    bees.third = yy;
                    bees.fourth = xx;
                }

                //moves in appropriate direction
                bees.first += bestw;
                bees.second += bestv;
            }
            else {
                //finds closest tree
                for (Structure2 tt : treeLoc) {
                    if (Math.abs(tt.second - bees.second) + Math.abs(tt.first - bees.first) <
                            Math.abs(xx - bees.second) + Math.abs(yy - bees.first)) {
                        xx = tt.second;
                        yy = tt.first;
                    }
                }

                //sets the cloest tree to its destination tree
                bees.third = yy;
                bees.fourth = xx;

                //finds direction to move in
                for (int v = 0; v < 3; v++) {
                    for (int w = 0; w < 3; w++) {
                        int tempx, tempy;
                        tempy = bees.first + rand[w];
                        tempx = bees.second + rand[v];
                        if ((Math.pow(xx - (bees.second + bestv), 2) + Math.pow(yy - (bees.first + bestw), 2)) >=
                                (Math.pow(xx - tempx, 2) + Math.pow(yy - tempy, 2))) {
                            bestw = rand[w];
                            bestv = rand[v];

                        }
                    }
                }

                //moves in appropriate direction
                bees.first += bestw;
                bees.second += bestv;
            }

            //some random movement
            int randMovey = (int) (Math.random()*3-1);
            int randMovex = (int) (Math.random()*3-1);
            bees.first+=randMovey;
            bees.second+=randMovex;

        }

        //updates squirtle
        //loops through each squirtle in ArrayList
        for (Structure5 sq : squirtleLoc) {
            int randomx, randomy;
            //if it is close to its destination, it finds a new location
            if (Math.abs(sq.second - sq.fourth)+Math.abs(sq.first-sq.third) < 10) {
                //randomly finds a location with water and sets as new destination
                while (true){
                    randomx = (int) (Math.random()*251);
                    randomy = (int) (Math.random()*161);
                    if (grid[randomy][randomx].isBlue()){
                        break;
                    }
                }
                sq.third = randomy;
                sq.fourth = randomx;
            }

            //moves in appropriate direction
            int bestv = 0, bestw = 0;
            for (int v = 0; v < 3; v++) {
                for (int w = 0; w < 3; w++) {
                    int tempx, tempy;
                    tempy = sq.first + rand[w];
                    tempx = sq.second + rand[v];
                    if ((Math.pow(sq.fourth - (sq.second + bestv), 2) + Math.pow(sq.third - (sq.first + bestw), 2)) >=
                            (Math.pow(sq.fourth - tempx, 2) + Math.pow(sq.third - tempy, 2))) {
                        bestw = rand[w];
                        bestv = rand[v];
                    }
                }
            }

            sq.first += bestw;
            sq.second += bestv;
        }


        //updates growlithe
        //loops through each growlithe in ArrayList
        int removei = -1;
        for (Structure7 grow : growLoc) {
            removei++;
            int avgx=0, avgy=0, speedx=0, speedy=0;
            int cont = 0;

            //finds average location of growlithe
            for (Structure7 tt : growLoc) {
                if (Math.abs(tt.second-grow.second)+Math.abs(tt.first-grow.first)<100){
                    avgx+=tt.second;
                    avgy+=tt.first;
                    cont++;
                }
            }
            avgx/=cont;
            avgy/=cont;

            //moves in appropriate direction
            int bestv = 0, bestw = 0;
            for (int v = 0; v < 3; v++) {
                for (int w = 0; w < 3; w++) {
                    int tempx, tempy;
                    tempy = grow.first + rand[w];
                    tempx = grow.second + rand[v];
                    if ((Math.pow(avgx - (grow.second + bestv), 2) + Math.pow(avgy - (grow.first + bestw), 2)) >=
                            (Math.pow(avgx - tempx, 2) + Math.pow(avgy - tempy, 2))) {
                        bestw = rand[w];
                        bestv = rand[v];

                    }
                }
            }
            grow.first += bestw;
            grow.second += bestv;

            //prevents from moving too close to other growlithe
            double xshift=0, yshift=0;
            for (Structure7 tt : growLoc) {
                if (Math.abs(tt.second-grow.second)+Math.abs(tt.first-grow.first)<15){
                    if (grow.second > tt.second) xshift+=5/(grow.second-tt.second+1);
                    if (grow.second < tt.second) xshift-=5/(tt.second-grow.second+1);
                    if (grow.first > tt.first) yshift+=5/(grow.first-tt.first+1);
                    if (grow.first < tt.first) yshift-=5/(tt.first-grow.first+1);
                }
            }

            grow.first += Math.round(yshift);
            grow.second += Math.round(xshift);

            //random movement
            int randMovey = (int) (Math.random()*430-200);
            int randMovex = (int) (Math.random()*430-200);
            grow.first+=Math.round((randMovey+0.0)/100);
            grow.second+=Math.round((randMovex+0.0)/100);

            //prevents from moving into a lake
            int lakexavg, lakeyavg;
            lakexavg=0; lakeyavg=0;
            try {
                for (int i = -10; i <= 10; i++) {
                    for (int j = -10; j <= 10; j++) {
                        //finds average of the lake tiles in the area around the growlithe
                        if (!grid[grow.first + i][grow.second + j].isBlue() && !grid[grow.first + i][grow.second + j].isLake()) {
                            if (i > 0) lakeyavg++;
                            if (i < 0) lakeyavg--;
                            if (j > 0) lakexavg++;
                            if (j < 0) lakexavg--;
                        }
                    }
                }
            } catch (Exception ignored){}

            //moves away accordingly
            int xx=0; int yy=0;
            for (int v = 0; v < 3; v++) {
                for (int w = 0; w < 3; w++) {
                    int tempx, tempy;
                    tempy = rand[w];
                    tempx = rand[v];
                    if ((Math.pow(xx - lakexavg, 2) + Math.pow(yy - (lakeyavg), 2)) >
                            (Math.pow(lakexavg - tempx, 2) + Math.pow(lakeyavg - tempy, 2))) {
                        yy = rand[w];
                        xx = rand[v];
                    }
                }
            }

            grow.first+=yy*Math.pow(Math.abs(lakeyavg), 0.25);
            grow.second+=xx*Math.pow(Math.abs(lakeyavg), 0.25);
            grow.diry+=yy*Math.pow(Math.abs(lakeyavg), 0.25)*1/3;
            grow.dirx+=xx*Math.pow(Math.abs(lakeyavg), 0.25)*1/3;

            //updates a wait counter if it is near a lake
            if (Math.abs(lakeyavg) > 4 && Math.abs(lakexavg) > 4){
                grow.wait++;
            }

            //updates loc based on direction values
            grow.first+=grow.diry;
            grow.second+=grow.dirx;

            //based on general direction it is moving in, update the direction/speed values
            int xxxx, yyyy;
            if (grow.quad-1==0 || grow.quad-1==-1) {
                yyyy = (int) (Math.random() * 4);
            }
            else{
                yyyy = -(int) (Math.random() * 4);
            }
            if (grow.quad-1 == 0 || grow.quad-1==1) {
                xxxx = (int) (Math.random() * 4);
            }
            else{
                xxxx = -((int) (Math.random() * 4));
            }

            grow.diry = Math.round((yyyy+grow.diry)/2);
            grow.dirx = Math.round((xxxx+grow.dirx)/2);


            //if it has been waiting by a lake, change direction values
            if (grow.wait > 10){
                grow.wait = 0;
                if (grow.quad == 1 || grow.quad == 3){
                    grow.diry=-grow.diry;
                }
                else if (grow.quad == 2 || grow.quad == 0){
                    grow.dirx=-grow.dirx;
                }
                grow.quad++;
                grow.quad%=4;
            }

            //if it is outside the screen, assign it to be removed
            if (grow.second < -20 || grow.second > 285){
                removee.add(removei);
            }
            else if (grow.first < -20 || grow.second > 195){
                removee.add(removei);
            }
        }

        //remove growlithe outside the screen
        for (int i=removee.size()-1; i>=0; i--){
            growLoc.remove(removee.get(i));
        }

        for (int i=removee.size()-1; i>=0; i--){
            removee.remove(i);
        }


        //updates gastly
        //loops through each gastly in the ArrayList
        for (Structure5 gas : gasLoc) {
            int genRandom;
            //gastly are assigned a random location to move to
            if (Math.abs(gas.second-gas.fourth) + Math.abs(gas.first - gas.third) < 12){
                genRandom = (int) (Math.random()*5);
                gas.third = gasMove[genRandom][0];
                gas.fourth = gasMove[genRandom][1];
            }

            //moves in the appropriate direction
            int xx = 0, yy = 0;
            for (int v = 0; v < 3; v++) {
                for (int w = 0; w < 3; w++) {
                    int tempx, tempy;
                    tempy = rand[w];
                    tempx = rand[v];
                    if ((Math.pow(gas.second + xx - gas.fourth, 2) + Math.pow(gas.first + yy - gas.third, 2)) >
                            (Math.pow(tempx+gas.second - gas.fourth, 2) + Math.pow(gas.first + tempy - gas.third, 2))) {
                        yy = rand[w];
                        xx = rand[v];
                    }
                }
            }

            gas.first += yy;
            gas.second += xx;

            //finds average of all pokemon within a certain range
            int avgtotx=0, avgtoty=0, cont = 0;
            for (Structure6 bees: beeLoc){
                if (Math.sqrt(Math.pow(gas.first-bees.first, 2)+Math.pow(gas.second-bees.second, 2)) < 20){
                    cont++;
                    avgtotx+=bees.second;
                    avgtoty+=bees.first;
                }
            }
            for (Structure5 sq: squirtleLoc){
                if (Math.sqrt(Math.pow(gas.first-sq.first, 2)+Math.pow(gas.second-sq.second, 2)) < 20){
                    cont++;
                    avgtotx+=sq.second;
                    avgtoty+=sq.first;
                }
            }
            for (Structure7 gr: growLoc){
                if (Math.sqrt(Math.pow(gas.first-gr.first, 2)+Math.pow(gas.second-gr.second, 2)) < 20){
                    cont++;
                    avgtotx+=gr.second;
                    avgtoty+=gr.first;
                }
            }
            //moves away from the average
            if (cont != 0) {
                avgtotx /= cont;
                avgtoty /= cont;
                gas.second -= (avgtotx - gas.second)/5;
                gas.first -= (avgtoty - gas.first)/5;
            }

            //random movement
            int randMovey = (int) (Math.random()*430-200);
            int randMovex = (int) (Math.random()*430-200);
            gas.first+=Math.round((randMovey+0.0)/200);
            gas.second+=Math.round((randMovex+0.0)/200);

            //if it is extremely close to any pokemon, move away
            for (Structure6 bees: beeLoc){
                if (Math.sqrt(Math.pow(gas.first-bees.first, 2)+Math.pow(gas.second-bees.second, 2)) < 5){
                    gas.second-=(bees.second-gas.second)*(5/(Math.sqrt(Math.pow(gas.first-bees.first, 2)+Math.pow(gas.second-bees.second, 2))+1));
                    gas.first-=(bees.first-gas.first)*(5/(Math.sqrt(Math.pow(gas.first-bees.first, 2)+Math.pow(gas.second-bees.second, 2))+1));
                }
            }
            for (Structure5 sq: squirtleLoc){
                if (Math.sqrt(Math.pow(gas.first-sq.first, 2)+Math.pow(gas.second-sq.second, 2)) < 5){
                    gas.second-=(sq.second-gas.second)*(5/(Math.sqrt(Math.pow(gas.first-sq.first, 2)+Math.pow(gas.second-sq.second, 2))+1));
                    gas.first-=(sq.first-gas.first)*(5/(Math.sqrt(Math.pow(gas.first-sq.first, 2)+Math.pow(gas.second-sq.second, 2))+1));
                }
            }
            for (Structure7 gr: growLoc) {
                if (Math.sqrt(Math.pow(gas.first - gr.first, 2) + Math.pow(gas.second - gr.second, 2)) < 5) {
                    gas.second -= (gr.second - gas.second) * (5 / (Math.sqrt(Math.pow(gas.first - gr.first, 2) + Math.pow(gas.second - gr.second, 2)) + 1));
                    gas.first -= (gr.first - gas.first) * (5 / (Math.sqrt(Math.pow(gas.first - gr.first, 2) + Math.pow(gas.second - gr.second, 2)) + 1));
                }
            }
            for (Structure5 gass: gasLoc) {
                if (Math.sqrt(Math.pow(gas.first - gass.first, 2) + Math.pow(gas.second - gass.second, 2)) < 5) {
                    gas.second -= (gass.second - gas.second) * (5 / (Math.sqrt(Math.pow(gas.first - gass.first, 2) + Math.pow(gas.second - gass.second, 2)) + 1));
                    gas.first -= (gass.first - gas.first) * (5 / (Math.sqrt(Math.pow(gas.first - gass.first, 2) + Math.pow(gas.second - gass.second, 2)) + 1));
                }
            }
        }
        counter++;

        for (Structure2 treeArr : treeLoc) {
            try {
                g.drawImage(tree, 4 * treeArr.second, 4 * treeArr.first, 40, 40, null);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }

        ArrayList<Integer> deadBeedrills = new ArrayList<>();
        ArrayList<Integer> deadSquirtle = new ArrayList<>();
        ArrayList<Integer> deadGrow = new ArrayList<>();
        ArrayList<Integer> deadGas = new ArrayList<>();

        //catching pokemon
        //gets location of user
        int xPlayer = Pokemon.player.getX()/4;
        int yPlayer = Pokemon.player.getY()/4;

        //checks all pokemon within a small distance of the player
        int cont = 0;
        int random;
        for (Structure6 bees: beeLoc) {
            //have a certain percentage of catching the pokemon based on health
            random = (int) (Math.random()*11)*(int)(Math.sqrt(bees.health));
            if (random < 5 && Math.abs(bees.first-yPlayer) <= 4 && Math.abs(bees.second-xPlayer) <= 4){
                if (Pokemon.inventoryWindow.addPokeToInventory(3, bees.health)){
                    deadBeedrills.add(cont);
                }
            }
            //records indices of pokemon to be removed as they are now in the inventory
            cont++;
        }
        cont = 0;

        //repeat for each pokemon
        for (Structure5 sq: squirtleLoc) {
            random = (int) (Math.random()*11)*(int)(Math.sqrt(sq.health));
            if (random < 5 && Math.abs(sq.first-yPlayer) <= 4 && Math.abs(sq.second-xPlayer) <= 4){
                if (Pokemon.inventoryWindow.addPokeToInventory(2, sq.health)){
                    deadSquirtle.add(cont);
                }
            }
            cont++;
        }
        cont = 0;
        for (Structure7 grow: growLoc) {
            random = (int) (Math.random()*11)*(int)(Math.sqrt(grow.health));
            if (random < 5 && Math.abs(grow.first-yPlayer) <= 4 && Math.abs(grow.second-xPlayer) <= 4){
                if (Pokemon.inventoryWindow.addPokeToInventory(4, grow.health)){
                    deadGrow.add(cont);
                }
            }
            cont++;
        }
        cont = 0;
        for (Structure5 gas: gasLoc) {
            random = (int) (Math.random()*11)*(int)(Math.sqrt(gas.health));
            if (random < 5 && Math.abs(gas.first-yPlayer) <= 4 && Math.abs(gas.second-xPlayer) <= 4){
                if (Pokemon.inventoryWindow.addPokeToInventory(6, gas.health)){
                    deadGas.add(cont);
                }
            }
            cont++;
        }


        //removes pokemon from stored values in ArrayList
        for (int i=deadBeedrills.size()-1; i>=0; i--){
            int p = deadBeedrills.get(i);
            g.setColor(Color.GREEN);
            g.fillRect(beeLoc.get(p).second * 4, beeLoc.get(p).first * 4, 25, 25);
            beeLoc.remove(p);
        }
        for (int i=deadSquirtle.size()-1; i>=0; i--){
            int p = deadSquirtle.get(i);
            g.setColor(Color.GREEN);
            g.fillRect(squirtleLoc.get(p).second * 4, squirtleLoc.get(p).first * 4, 25, 25);
            squirtleLoc.remove(p);
        }
        for (int i=deadGrow.size()-1; i>=0; i--){
            int p = deadGrow.get(i);
            g.setColor(Color.GREEN);
            g.fillRect(growLoc.get(p).second * 4, growLoc.get(p).first * 4, 25, 25);
            growLoc.remove(p);
        }
        //System.out.println(growLoc.size());
        for (int i=deadGas.size()-1; i>=0; i--){
            int p = deadGas.get(i);
            g.setColor(Color.GREEN);
            g.fillRect(gasLoc.get(p).second * 4, gasLoc.get(p).first * 4, 25, 25);
            gasLoc.remove(p);
        }

        for (int i=deadBeedrills.size()-1; i>=0; i--){
            deadBeedrills.remove(i);
        }
        for (int i=deadSquirtle.size()-1; i>=0; i--){
            deadSquirtle.remove(i);
        }
        for (int i=deadGrow.size()-1; i>=0; i--){
            deadGrow.remove(i);
        }
        for (int i=deadGas.size()-1; i>=0; i--){
            deadGas.remove(i);
        }


        //paints all the animals
        //reset the array that stores the position of all pokemon
        for (int i = 0; i < pos.length; i++)
            for (int j = 0; j < pos[i].length; j++)
                Arrays.fill(pos[i][j], false);

        //update to find all of the positions of current the pokemon
        //need to try catch to ignore pokemon off the screen
        for (Structure6 bees: beeLoc) {
            try {
                pos[bees.second][bees.first][0] = true;
            }catch (ArrayIndexOutOfBoundsException ignored) {}
        }
        for (Structure5 sq: squirtleLoc) {
            try {
                pos[sq.second][sq.first][1] = true;
            }catch (ArrayIndexOutOfBoundsException ignored) {}
        }
        for (Structure7 grow: growLoc) {
            try {
                pos[grow.second][grow.first][2] = true;
            }catch (ArrayIndexOutOfBoundsException ignored) {}
        }
        for (Structure5 gas: gasLoc) {
            try {
                //System.out.println(gas.second+", "+gas.first);
                pos[gas.second][gas.first][3] = true;
            }catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        //stores the indexes of dead pokemon
        int ind = 0; //index of the for each loop
        for (Structure6 bees: beeLoc) {
            for (int i = 1; i < 4; i++) { //all other pokemon besides bees
                try { //if other pokemon occupy the same square
                    if (pos[bees.second - 1][bees.first + 1][i]||pos[bees.second][bees.first + 1][i]||pos[bees.second + 1][bees.first + 1][i]||
                            pos[bees.second - 1][bees.first    ][i]||pos[bees.second][bees.first    ][i]||pos[bees.second + 1][bees.first    ][i]||
                            pos[bees.second - 1][bees.first - 1][i]||pos[bees.second][bees.first - 1][i]||pos[bees.second + 1][bees.first - 1][i]) {
                        bees.health -= 50;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored){}
            }
            if (bees.health <= 0) {
                deadBeedrills.add(ind); //list of dead beedrills
            }
            else {
                g.drawImage(beedrill, 4 * bees.second, 4 * bees.first, 20, 20, null);
                if (bees.health > 70) g.setColor(new Color(50, 245, 101)); //healthy
                else if (bees.health <= 70 && bees.health >= 30) g.setColor(new Color(232, 164, 45));
                else g.setColor(new Color(232, 75, 45)); //lowlife
                g.fillRect(4 * bees.second - (bees.health / 100), 4 * bees.first - 5, bees.health * 20 / 100, 3);
            }
            ind++;
        }
        //remove all of the beedrills that had less 0 health
        for (int i1 = deadBeedrills.size() - 1; i1 >= 0; i1--) {
            int i = deadBeedrills.get(i1);//remove the dead pokemon
            g.setColor(Color.RED);
            g.fillRect(beeLoc.get(i).second * 4, beeLoc.get(i).first * 4, 25, 25);
            beeLoc.remove(i);
        }

        //repeat the same for squirtles
        ind  = 0;
        for (Structure5 sq: squirtleLoc) {
            for (int i = 0; i < 4; i++) {
                try { //if other pokemon occupy the same square
                    if (pos[sq.second - 1][sq.first + 1][i]||pos[sq.second][sq.first + 1][i]||pos[sq.second + 1][sq.first + 1][i]||
                            pos[sq.second - 1][sq.first    ][i]||pos[sq.second][sq.first    ][i]||pos[sq.second + 1][sq.first    ][i]||
                            pos[sq.second - 1][sq.first - 1][i]||pos[sq.second][sq.first - 1][i]||pos[sq.second + 1][sq.first - 1][i]) {
                        if (i != 1) sq.health -= 50;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            }
            if (sq.health <= 0) {
                deadSquirtle.add(ind); //list of dead squirtles
            }
            else {
                g.drawImage(squirtle, 4 * sq.second, 4 * sq.first, 20, 20, null);
                if (sq.health > 70) g.setColor(new Color(50, 245, 101)); //healthy
                else if (sq.health <= 70 && sq.health >= 30) g.setColor(new Color(232, 164, 45));
                else g.setColor(new Color(232, 75, 45)); //lowlife
                g.fillRect(4 * sq.second - (sq.health / 100), 4 * sq.first - 5, sq.health * 20 / 100, 3);
            }
            ind++;
        }
        //remove all of the squirtles with less than 0 health
        for (int i1 = deadSquirtle.size() - 1; i1 >= 0; i1--) {
            int i = deadSquirtle.get(i1);//remove the dead pokemon
            g.setColor(Color.RED);
            g.fillRect(squirtleLoc.get(i).second * 4, squirtleLoc.get(i).first * 4, 25, 25);
            squirtleLoc.remove(i);
        }

        //repeat the same for growlithes
        ind = 0;
        for (Structure7 grow: growLoc) {
            for (int i = 0; i < 4; i++) {
                try { //if other pokemon occupy the same square
                    if (pos[grow.second - 1][grow.first + 1][i]||pos[grow.second][grow.first + 1][i]||pos[grow.second + 1][grow.first + 1][i]||
                            pos[grow.second - 1][grow.first    ][i]||pos[grow.second][grow.first    ][i]||pos[grow.second + 1][grow.first    ][i]||
                            pos[grow.second - 1][grow.first - 1][i]||pos[grow.second][grow.first - 1][i]||pos[grow.second + 1][grow.first - 1][i]) {
                        if (i != 2) grow.health -= 50;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            }
            if (grow.health <= 0) {
                deadGrow.add(ind); //add to a list of dead growlithes
            }
            else {
                g.drawImage(growlithe, 4 * grow.second, 4 * grow.first, 25, 25, null);
                if (grow.health > 70) g.setColor(new Color(50, 245, 101)); //healthy
                else if (grow.health <= 70 && grow.health >= 30) g.setColor(new Color(232, 164, 45));
                else g.setColor(new Color(232, 75, 45)); //lowlife
                g.fillRect(4 * grow.second - (grow.health / 100), 4 * grow.first - 5, grow.health * 20 / 100, 3);
            }
            ind++;
        }
        //remove all of the growlithes with less than 0 health
        for (int i1 = deadGrow.size() - 1; i1 >= 0; i1--) {
            int i = deadGrow.get(i1);//remove the dead pokemon
            g.setColor(Color.RED);
            g.fillRect(growLoc.get(i).second * 4, growLoc.get(i).first * 4, 25, 25);
            growLoc.remove(i);
        }

        //repeat the same thing for gastlys
        ind = 0;
        for (Structure5 gas: gasLoc) {
            for (int i = 0; i < 4; i++) {
                try {//if other pokemon occupy the same square
                    if (pos[gas.second - 1][gas.first + 1][i]||pos[gas.second][gas.first + 1][i]||pos[gas.second + 1][gas.first + 1][i]||
                            pos[gas.second - 1][gas.first    ][i]||pos[gas.second][gas.first    ][i]||pos[gas.second + 1][gas.first    ][i]||
                            pos[gas.second - 1][gas.first - 1][i]||pos[gas.second][gas.first - 1][i]||pos[gas.second + 1][gas.first - 1][i]) {
                        if(i != 3) gas.health -= 50;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored){}
            }

            if (gas.health <= 0) {
                deadGas.add(ind); //add to list of dead gastlys
            }
            else {
                g.drawImage(gastly, 4 * gas.second, 4 * gas.first, 20, 20, null);
                if (gas.health > 70) g.setColor(new Color(50, 245, 101)); //healthy
                else if (gas.health <= 70 && gas.health >= 30) g.setColor(new Color(232, 164, 45));
                else g.setColor(new Color(232, 75, 45)); //lowlife
                g.fillRect(4 * gas.second - (gas.health / 100), 4 * gas.first - 5, gas.health * 20 / 100, 3);
            }
            ind++;
        }
        //remove all of the gastly with less than 0 health
        for (int i1 = deadGas.size() - 1; i1 >= 0; i1--) {
            int i = deadGas.get(i1);//remove the dead pokemon
            g.setColor(Color.RED);
            g.fillRect(gasLoc.get(i).second * 4, gasLoc.get(i).first * 4, 25, 25);
            gasLoc.remove(i);
        }
    }


    //lake generation
    public void Lake(int q, int w, int d){
        //creates temporary colour array
        Colour temp[][] = new Colour [d][d];
        int loc[][] = new int[d][d];

        //default values
        for (int i=0; i<d; i++){
            for (int j=0; j<d; j++){
                temp[i][j] = new Colour();
                temp[i][j].setGrass();
            }
        }

        //divides the lake into four quadrants
        //randomly generates the width of the centre of the lake
        int b, c;
        int a = (int) (Math.random()*(d+1)/4+3*d/4);
        c=a;
        int tat;
        int size;
        temp[a][d/2].blue = 255;
        temp[a][d/2].green = 191;
        loc[a][d/2]++;

        //top right
        for (int i=d/2+1; i<d; i++){
            //randomly shifts next lake column up or down
            tat = (int) (Math.random()*100);
            if (tat < 80 && a>=d/2){ a--; }
            else if (tat < 90){ loc[a][i]++; }
            else if (tat < 100 && a<d-1){ a++; }

            size = (int) (Math.random()*4);
            for (int s=a-size; s<=a+size; s++) {
                for (int p=i-size; p<=i+size; p++) {
                    try {
                        temp[s][p].blue = 255;
                        temp[s][p].green = 191;
                        loc[s][p]++;
                    } catch (Exception e) {
                    }
                }
            }
        }
        a = (int) (Math.random()*(d+1)/4);
        b = a;
        temp[a][d/2].blue = 255;
        temp[a][d/2].green = 191;
        loc[a][d/2]++;

        //bottom right
        for (int i=d/2+1; i<d; i++){
            //randomly shifts next lake column up or down
            tat = (int) (Math.random()*100);
            if (tat < 80 && a<=d/2){ a++; }
            else if (tat < 90){ }
            else if (tat < 100 && a>0){ a--; }
            else{ }

            size = (int) (Math.random()*4);
            for (int s=a-size; s<=a+size; s++) {
                for (int p=i-size; p<=i+size; p++) {
                    try {
                        temp[s][p].blue = 255;
                        temp[s][p].green = 191;
                        loc[s][p]++;
                    } catch (Exception e) {
                    }
                }
            }
        }

        //top left
        for (int i=d/2-1; i>=0; i--){
            //randomly shifts next lake column up or down
            tat = (int) (Math.random()*100);
            if (tat < 80 && c>=d/2){ c--; }
            else if (tat < 90) {}
            else if (tat < 100 && c<d-1){ c++; }
            size = (int) (Math.random()*4);
            for (int s=c-size; s<=c+size; s++) {
                for (int p=i-size; p<=i+size; p++) {
                    try {
                        temp[s][p].blue = 255;
                        temp[s][p].green = 191;
                        loc[s][p]++;
                    } catch (Exception e) {
                    }
                }
            }
        }

        //bottom left
        for (int i=d/2-1; i>=0; i--){
            //randomly shifts next lake column up or down
            tat = (int) (Math.random()*100);
            if (tat < 80 && b<=d/2){ b++; }
            else if (tat < 90){}
            else if (tat < 100 && b>0) { b--; }
            size = (int) (Math.random()*4);
            for (int s=b-size; s<=b+size; s++) {
                for (int p=i-size; p<=i+size; p++) {
                    try {
                        temp[s][p].blue = 255;
                        temp[s][p].green = 191;
                        loc[s][p]++;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        //assigns colour to the part between the created border
        for (int i=0; i<d; i++){
            int j = 0;
            while (loc[j][i]==0){
                j++;
            }
            j++;
            while (j<d) {
                temp[j][i].blue = 255;
                temp[j][i].green = 191;
                j++;
            }
            j=d-1;
            while (j>=0 && loc[j][i]==0){
                temp[j][i].setGrass();
                j--;
            }
        }

        //sets the colour of the appropriate section of the original grid
        for (int i=0; i<d; i++){
            for (int j=0; j<d; j++){
                if (grid[i+q][j+w].blue == 255 && grid[i+q][j+w].green==191){ }
                else {
                    grid[i + q][j + w].blue = temp[i][j].blue;
                    grid[i + q][j + w].green = temp[i][j].green;
                    grid[i + q][j + w].red = temp[i][j].red;
                }
            }
        }
    }

    //returns position of a lake
    public boolean[][] lakePos() {
        boolean[][] ret = new boolean[180][270];
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                ret[i][j] = grid[i][j].isLake() || grid[i][j].isBlue();
        return ret;
    }
}

//colour class
//elements in the environment/background are color coded
class Colour {
    int red, green, blue;

    //sets rgb values for different cases
    public Colour(){
        red = (int) (Math.random()*256);
        green = (int) (Math.random()*256);
        blue = (int) (Math.random()*256);
    }

    public void setGrass(){
        red = 144;
        green = 245;
        blue = 0;
    }

    public void setBlue(){
        red = 0;
        green = 191;
        blue = 255;
    }

    //checks if the given location is a lake
    public boolean isBlue(){
        return blue == 255 && green == 191 && red == 0;
    }
    public boolean isLake(){
        return blue == 255 && green == 191 && red == 144;
    }
}

//help store and manage two data fields together,
//used for the properties of the tree
class Structure2 {
    int first, second; //y pos, x pos
    public Structure2(){
        first = 0;
        second = 0;
    }
    public Structure2(int a, int b){
        first = a;
        second = b;
    }
}

//help store and manage five data fields together
class Structure5 {
    int first, second, third, fourth, health; //y pos, x pos, next y pos, next x pos, and the health
    public Structure5(){
        first = 0;
        second = 0;
    }
    public Structure5(int a, int b, int c, int d, int hp){
        first = a;
        second = b;
        third = c;
        fourth = d;
        health = hp;
    }
}

//help store and manage six data fields together
//used for beedrills
class Structure6 {
    int first, second, third, fourth, health; //y pos, x pos, next y pos, next x pos, and the health
    boolean fifth; //if a beedrill should be approaching closest tree or should be looking for another tree
    public Structure6(){
        first = 0;
        second = 0;
        third = 0;
        fourth = 0;
        fifth = true;
        health = 100;
    }
    public Structure6(int a, int b, int c, int d, boolean e, int hp){
        first = a;
        second = b;
        third = c;
        fourth = d;
        fifth = e;
        health = hp;
    }
}

//stores seven variables together
//used for growlithes
class Structure7 {
    //y pos, x, pos, delta y, delta x, how long a growlithe has been near water for, which quadrant its in, and its health
    int first, second, diry, dirx, wait, quad, health;
    public Structure7(){
        first = 0;
        second = 0;
        diry = 0;
        dirx = 0;
        wait = 0;
        quad = 1;
        health = 100;
    }
    public Structure7(int a, int b, int c, int d, int e, int f, int g){
        first = a;
        second = b;
        diry = c;
        dirx = d;
        wait = e;
        quad = f;
        health = g;
    }
}