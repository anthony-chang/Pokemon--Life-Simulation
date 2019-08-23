package Main;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PokemonMenuGUI extends JFrame{
    static Pokemon simulation;
    static Image pokemonLogo, pokeballIcon, lifeSimulationText; //images needed for display (text not changed)
    static Color defaultGrey = new Color (180, 180, 180);
    static Color [] textColours = {defaultGrey, defaultGrey, defaultGrey, defaultGrey}; //array of colours, one for each displayed "folder"
    public PokemonMenuGUI ()
    {
        try { //try to find images and load into image files
            pokemonLogo = ImageIO.read(new File ("Graphics\\pokemonLogo.png")); //POKEMON title text, pokemon font
            pokeballIcon = ImageIO.read(new File ("Graphics\\pokeball icon.png")); //pokeball (heavily pixelated)
            lifeSimulationText = ImageIO.read(new File ("Graphics\\lifeSimulation.png")); //LIFE SIMULATION subtitle text, normal font
        } catch (IOException e) {
            e.printStackTrace();
        }
        simulation = new Pokemon();

        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BorderLayout ()); // Use BorderLayout for panel

        DrawArea board = new DrawArea (80, 600); //900, 450
        content.add(board, "South");
        board.setBackground(new Color(50, 50, 50)); //background is dark grey

        HandlerClass handler = new HandlerClass (); //to listen to mouse actions (eg clicks)
        content.addMouseListener(handler); //add to content panel

        // 4... Set this window's attributes.
        setContentPane (content);
        pack ();
        setTitle ("");
        setSize (800, 650); //given is 800, 600
        setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo (null);           // Center window.

    }

    // put ActionListener class for your buttons here
    class BtnListener implements ActionListener // Button menu
    {
        public void actionPerformed (ActionEvent e)
        {
            repaint (); // do after each action taken to update deck
        }
    }
    class HandlerClass implements MouseListener //reacting to mouse actions
    {
        private int x, y;
        public void mouseClicked (MouseEvent event)
        {
            x = event.getX(); //get x coordinates of mouse when clicked
            y = event.getY();//get y coordinates of mouse when clicked

            if (x>=80&&x<=365&&y>=275&&y<=355) //simulation box (80, 275, 285, 80) as a rectangle - top left
            {
                textColours[0] = Color.WHITE; //set the colours as white
                textColours[1] = defaultGrey;
                textColours[2] = defaultGrey;
                textColours[3] = defaultGrey;
                repaint(); //repaint with white text and outline
                setVisible(false);
                simulation.setVis();
                simulation.startSimulation();
                setAlwaysOnTop(false);

            } else if (x>=80&&x<=365&&y>=400&&y<=480) { //information box (80, 400, 285, 80) as a rectangle - bottom left
                textColours[1] = Color.WHITE; //set the colours as white
                textColours[0] = defaultGrey;
                textColours[2] = defaultGrey;
                textColours[3] = defaultGrey;
                new pokemonInfoGUI().setVisible(true);
                setAlwaysOnTop(false);
                repaint(); //repaint with white text and outline
            } else if (x>=405&&x<=690&&y>=325&&y<=405) { //inventory box (405, 325, 285, 80) as a rectangle - top right
                //setVisible(false); //close current (starting menu) window
                textColours[2] = Color.WHITE; //set the colours as white
                textColours[0] = defaultGrey;
                textColours[1] = defaultGrey;
                textColours[3] = defaultGrey;
                repaint(); //repaint with white text and outline
                setAlwaysOnTop(false);

                simulation.setInventory(true); //set visible by user

            } else if (x>=405&&x<=690&&y>=450&&y<=530) { //quit box (405, 450, 285, 80) as a rectangle - bottom right
                textColours[3] = Color.WHITE; //set the colours as white
                textColours[0] = defaultGrey;
                textColours[1] = defaultGrey;
                textColours[2] = defaultGrey;
                repaint(); //repaint with white text and outline
                System.exit(0);
            }

        }
        public void mousePressed (MouseEvent event) {}
        public void mouseReleased (MouseEvent event) {
            //set all the colors of the "buttons" to grey
            textColours[0] = defaultGrey;
            textColours[1] = defaultGrey;
            textColours[2] = defaultGrey;
            textColours[3] = defaultGrey;
        }
        public void mouseEntered (MouseEvent event){}
        public void mouseExited (MouseEvent event) {}

    }
    class DrawArea extends JPanel
    {
        public DrawArea (int width, int height)
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)
        {
            super.paintComponent (g);
            //draw all images
            g.drawImage(pokemonLogo, 50, 40, 485, 140, null); //POKEMON title text, pokemon font
            g.drawImage(pokeballIcon, 605, 50, 120, 120, null); //pokeball pixelated icon
            g.drawImage(lifeSimulationText, 70, 190, 635, 55, null); //LIFE SIMULATION subtitle text, normal sans-serif font

            //file shapes (6-vertex polygons): include x and y coordinates, first array is x coordinates, second is y coordinates
            int [][] topLeftFileXY = {{80, 180, 195, 365, 365, 80}, {275, 275, 290, 290, 355, 355}}; //simulation
            int [][] topRightFileXY = {{405, 505, 520, 690, 690, 405}, {335, 335, 350, 350, 415, 415}}; //inventory
            int [][] bottomLeftFileXY = {{80, 180, 195, 365, 365, 80}, {400, 400, 415, 415, 480, 480}}; //information
            int [][] bottomRightFileXY = {{405, 505, 520, 690, 690, 405}, {460, 460, 475, 475, 540, 540}}; //options

            g.setColor(Color.BLACK); //black background of the file shapes
            g.fillPolygon(new Polygon(topLeftFileXY[0], topLeftFileXY[1], topLeftFileXY[0].length)); //simulation
            g.fillPolygon(new Polygon(topRightFileXY[0], topRightFileXY[1], topRightFileXY[0].length)); //inventory
            g.fillPolygon(new Polygon(bottomLeftFileXY[0], bottomLeftFileXY[1], bottomLeftFileXY[0].length)); //information
            g.fillPolygon(new Polygon(bottomRightFileXY[0], bottomRightFileXY[1], bottomRightFileXY[0].length)); //options

            Font smallFont = new Font("Verdana", Font.PLAIN, 35); //draw text in arial, size 35 font
            g.setFont(smallFont);
            /*Original - lined up
             *
            g.fillRect(80, 275, 285, 80); //top left
            g.fillRect(80, 400, 285, 80); //bottom left
            g.fillRect(405, 325, 285, 80); //top right
            g.fillRect(405, 450, 285, 80); //bottom right
            int [][] topLeftFileXY = {{70, 170, 185, 355, 355, 70}, {300, 300, 315, 315, 380, 380}};
            int [][] topRightFileXY = {{415, 515, 530, 700, 700, 415}, {300, 300, 315, 315, 380, 380}};
            int [][] bottomLeftFileXY = {{70, 170, 185, 355, 355, 70}, {425, 425, 440, 440, 505, 505}};
            int [][] bottomRightFileXY = {{415, 515, 530, 700, 700, 415}, {425, 425, 440, 440, 505, 505}};
            */

            g.setColor(textColours[0]); //default is grey (180, 180, 180)
            g.drawPolygon(new Polygon(topLeftFileXY[0], topLeftFileXY[1], topLeftFileXY[0].length));
            g.drawString("SIMULATE", 130, 335);

            g.setColor(textColours[1]); //default is grey (180, 180, 180)
            g.drawPolygon(new Polygon(bottomLeftFileXY[0], bottomLeftFileXY[1], bottomLeftFileXY[0].length));
            g.drawString("INFORMATION", 90, 460);

            g.setColor(textColours[2]); //default is grey (180, 180, 180)
            g.drawPolygon(new Polygon(topRightFileXY[0], topRightFileXY[1], topRightFileXY[0].length));
            g.drawString("INVENTORY", 445, 395);

            g.setColor(textColours[3]); //default is grey (180, 180, 180)
            g.drawPolygon(new Polygon(bottomRightFileXY[0], bottomRightFileXY[1], bottomRightFileXY[0].length));
            g.drawString("QUIT", 505, 520);

        }
    }
}
