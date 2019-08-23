package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class WorkingInventory extends JFrame {
    static Inventory inventory = new Inventory ();
    static Image grassBackground, inventoryHeader, pokeballIcon, remainingHeader; //images needed for display
    static Image [] digitPNGs = new Image [10]; //array of 10 digits, and number 10 (in the specific font)
    BtnListener btnListener = new BtnListener ();
    private int positionToView;
    private int loc[] = {-5, -4, -3, -2, 2, 3, 4, 5};

    //======================================================== constructor
    public WorkingInventory () {

        //loading images for display
        try {
            inventoryHeader = ImageIO.read (new File ("Graphics\\inventory text.png"));
            grassBackground = ImageIO.read (new File ("Graphics\\grassBackground.png"));
            pokeballIcon = ImageIO.read (new File ("Graphics\\pokeball icon.png"));
            // load file into Image object
            for (int i = 0; i<10; i++) {
                digitPNGs [i] = ImageIO.read (new File ("Graphics\\"+ i + " text.png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 1... Create/initialize components
        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BorderLayout ()); // Use BorderLayout for panel

        DrawArea board = new DrawArea (80, 600); //900, 450
        content.add(board, "South");

        HandlerClass handler = new HandlerClass ();
        content.addMouseListener(handler);

        // 4... Set this window's attributes.
        setContentPane (content);
        pack();
        setTitle ("");
        setSize (800, 650); //given is 800, 600
        setDefaultCloseOperation (JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo (null);           // Center window.

    }

    // put ActionListener class for your buttons here
    class BtnListener implements ActionListener // Button menu
    {
        public void actionPerformed (ActionEvent e)
        {
            if (e.getActionCommand ().equals ("Release")){
                int xPlayer = Pokemon.player.getX()/4;
                int yPlayer = Pokemon.player.getY()/4;
                int px = loc[(int) (Math.random()*8)];
                int py = loc[(int) (Math.random()*8)];

                //adds specified pokemon back into map next to the player with a few randomly specified characteristics
                if (inventory.getName(positionToView).equals("Growlithe")){
                    LifeSimulation.colony.growLoc.add(new Structure7(yPlayer+py, xPlayer+px, (int) (Math.random() * 2+2), (int) (Math.random() * 2 + 2), 0, 3, inventory.getHealth(positionToView)));
                }
                else if (inventory.getName(positionToView).equals("Squirtle")){
                    int randomx = (int) (Math.random()*251);
                    int randomy = (int) (Math.random()*161);
                    LifeSimulation.colony.squirtleLoc.add(new Structure5(yPlayer+py, xPlayer+px, randomy, randomx, inventory.getHealth(positionToView)));
                }
                else if (inventory.getName(positionToView).equals("Beedrill")){
                    LifeSimulation.colony.beeLoc.add(new Structure6(yPlayer+py, xPlayer+px, yPlayer+py, xPlayer+px, true, inventory.getHealth(positionToView)));
                }
                else if (inventory.getName(positionToView).equals("Gastly")){
                    LifeSimulation.colony.gasLoc.add(new Structure5(yPlayer+py, xPlayer+px, 90, 135, inventory.getHealth(positionToView)));
                }

                //repaints map and invntory
                Pokemon.environment.repaint();
                removePokeFromInventory(positionToView);
                repaint();

                //closes JOptionPane
                JOptionPane.getRootFrame().dispose();
            }
            repaint (); // do after each action taken to update deck
        }
    }
    class HandlerClass implements MouseListener //reacting to mouse actions
    {
        private int mouseX, mouseY, mouseXrelease, mouseYrelease;
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed (MouseEvent event) {
            mouseX = event.getX();
            mouseY = event.getY();
        }
        @Override
        public void mouseReleased(MouseEvent event) {
            mouseXrelease = event.getX();
            mouseYrelease = event.getY();

            positionToView = 10; //default number is 10; limit on array size is 0-5
            boolean validChoice = false;
            if (mouseX==mouseXrelease && mouseY==mouseYrelease) {
                //upper level
                if (mouseX>=100&&mouseX<=220&&mouseY>=140&&mouseY<290) positionToView = 0;
                else if (mouseX>=325&&mouseX<=465&&mouseY>=140&&mouseY<290) positionToView = 1;
                else if (mouseX>=550&&mouseX<=670&&mouseY>=140&&mouseY<290) positionToView = 2;
                    //lower level
                else if (mouseX>=100&&mouseX<=220&&mouseY>=345 && mouseY<480) positionToView = 3;
                else if (mouseX>=325&&mouseX<=465&&mouseY>=345 && mouseY<480) positionToView = 4;
                else if (mouseX>=550&&mouseX<=670&&mouseY>=345 && mouseY<480) positionToView = 5;

                if (positionToView>=0&&positionToView<inventory.inventorySize()) validChoice = true;
            }

            if (validChoice) {
                JPanel message = new JPanel ();
                message.setLayout(new BorderLayout());

                DrawArea2 display = new DrawArea2 (100, 20);

                JButton release = new JButton ("Release");
                release.addActionListener(btnListener);
                JButton life = inventory.showLife(positionToView);
                display.setLayout(new FlowLayout());
                display.add(release, "North");
                display.add(life, "South");
                life.setEnabled(false);

                JOptionPane.showOptionDialog(null, display,inventory.getName (positionToView), JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            }

        }
        @Override
        public void mouseEntered(MouseEvent e) {

        }
        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
    public boolean addPokeToInventory(int type, int health) {
        if (inventory.inventorySize() >= 6)
            return false;
        inventory.addNew(new CaughtPokemon(type, health));
        return true;
    }

    public boolean removePokeFromInventory(int ind) {
        if (inventory.inventorySize() > 0) {
            inventory.remove(ind);
            return true;
        }
        return false;
    }

    class DrawArea extends JPanel {
        public DrawArea (int width, int height) {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g) {
            super.paintComponent (g);
            //pokeball.paintIcon (this, g, 100, 100);
            g.drawImage (grassBackground, 0, 0, 1600, 1200, null); //grass background
            g.drawImage (inventoryHeader, 205, 30, 365, 75, null); //inventory titel at top  **original image (pixel) size: 994x185

            //for pokeballs remaining at bottom
            g.drawImage (pokeballIcon, 350, 545, 45, 45, null); //pokeball icon for pokeballs remaining, bottom

            //numbering the pokemon
            for (int i = 0; i<6; i++) {
                int y;
                if (i<=2) {
                    y = 250;
                    g.drawImage(digitPNGs[i + 1], i*225 + 100, y, 38, 57, null);
                }
                else if (i>2) {
                    y = 460;
                    g.drawImage(digitPNGs[i + 1], (i-3)*225 + 100, y, 38, 57, null);
                }
            }

            //draw the actual pokemon in inventory
            inventory.show(g); //print each pokemon and their life status (lifeLine bar)
        }
    }
    class DrawArea2 extends JPanel {
        public DrawArea2 (int width, int height) {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g) {
            super.paintComponent (g);

        }
    }
}
class Inventory {
    private ArrayList<CaughtPokemon> pokemon;
    Random random = new Random();
    public Inventory () {
        pokemon = new ArrayList <> ();
    }
    public void remove (int placement) {//placement will be 0-5
        pokemon.remove(placement);
    }
    public void addNew (CaughtPokemon add) { //type 1 to 6
        pokemon.add(add);
    }
    public void switchPokemon (int pokemon1, int pokemon2){ //two numbers, 0-5 representing PLACEMENT in indexes
        CaughtPokemon temp = pokemon.get(pokemon1);
        pokemon.set(pokemon1, pokemon.get(pokemon2));
        pokemon.set(pokemon2, temp);
    }
    public int inventorySize () { //return how many pokemon in inventory
        return pokemon.size();
    }
    public String getName (int position) { //return name of the pokemon in x position
        return pokemon.get(position).getName();
    }
    public int getHealth (int position) { //health of the pokemon in x position
        return pokemon.get(position).getLife();
    }
    public JButton showLife (int positionToView) { //color and healthbar
        JButton returnButton = new JButton ("HP: " + pokemon.get(positionToView).getLife());

        if (pokemon.get(positionToView).getLife()>70) returnButton.setBackground(new Color (50, 245, 101)); //healthy
        else if (pokemon.get(positionToView).getLife()<=70 && pokemon.get(positionToView).getLife()>=30) returnButton.setBackground (new Color (232, 164, 45));
        else returnButton.setBackground(new Color (232, 75, 45)); //low life

        return returnButton;
    }
    public Image image (int positionToView) {
        return pokemon.get(positionToView).getFace();
    }
    public void show (Graphics g) {
        for (int i = 0; i<pokemon.size(); i++) {
            int y;
            if (i<=2) {
                y = 40;
                pokemon.get(i).show(g, i*225 + 100, y);
            }
            else if (i>2) {
                y = 245;
                pokemon.get(i).show(g, (i-3)*225 + 100, y);
            }
        }
    }
}
class CaughtPokemon {
    private int life, type; //based on numbered pictures
    private boolean fly;
    private Image face;
    private String name;

    public CaughtPokemon (int type, int health) {
        this.type = type;
        life = health;

        //label the pokemon types with numbers, some types are unused
        if (type == 1) name = "Dragonair";
        else if (type == 2) name = "Squirtle";
        else if (type == 3) name = "Beedrill";
        else if (type == 4) name = "Growlithe";
        else if (type == 5) name = "Nidoran";
        else if (type == 6) name = "Gastly";

        face = null;
        try {
            face = ImageIO.read (new File ("Graphics\\" +type + ".png")); // load file into Image object
        }
        catch (IOException e) {}

        if (type == 1||type==2||type==4||type==5) { //which pokemon can fly over water
            fly = false;
        } else if (type == 3||type == 6)
        {
            fly = true;
        }
    }

    //for access, self explanatory
    public int getLife () {
        return life;
    }
    public int getType () {
        return type;
    }
    public boolean flyOrNo () {
        return fly;
    }
    public String getName () {
        return name;
    }
    public String getName (int type) {
        return new CaughtPokemon (type, 100).getName();
    }
    public Image getFace () {
        return face;
    }
    // drawing pictures
    public void show (Graphics g, int x, int y) {
        //also draw life
        if (life>70) g.setColor(new Color (50, 245, 101)); //healthy
        else if (life<=70 && life>=30) g.setColor (new Color (232, 164, 45));
        else g.setColor(new Color (232, 75, 45)); //lowlife
        g.fillRect(x, y+85, life*120/100, 7); //lifeLine representing life status of each pokemon
        g.drawImage (face, x, y+110, 120, 108, null);
    }
}