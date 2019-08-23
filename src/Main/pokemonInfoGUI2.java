package Main;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

class pokemonInfoGUI2 extends JFrame {

	static Image damageText, rulesText, spawnHealthText, pokeballIcon, backButton, backArrow;
	
	public pokemonInfoGUI2 () {
		try {
			//infoText = ImageIO.read(new File("infoText.png")); 
			//load necessary images
			damageText = ImageIO.read(new File ("Graphics\\damage text.png"));
			rulesText = ImageIO.read(new File ("Graphics\\rules.png"));
			spawnHealthText = ImageIO.read(new File ("Graphics\\spawn health text.png"));
			pokeballIcon = ImageIO.read(new File ("Graphics\\pokeball icon.png"));
			backButton = ImageIO.read(new File("Graphics\\main.png"));
			backArrow = ImageIO.read(new File ("Graphics\\backArrow.png"));
		} catch (IOException e) {
		    e.printStackTrace();
        }
		JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BorderLayout ()); // Use BorderLayout for panel
        
        DrawArea board = new DrawArea (80, 600); //900, 450
        content.add(board, "South");
        board.setBackground(new Color(50, 50, 50)); //background is dark grey
        
        HandlerClass handler = new HandlerClass (); //to listen to mouse actions (eg clicks)
        content.addMouseListener(handler); //add to content panel
        
        setContentPane (content);
        pack ();
        setTitle ("");
        setSize (800, 650); //given is 800, 600
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);  
	}
	
	class DrawArea extends JPanel {
		public DrawArea (int width, int height)
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)
        {
        	super.paintComponent (g);
        	int [][] leftFolderXY = {{30, 250, 300, 750, 750, 30}, {30, 30, 100, 100, 560, 560}}; //background folder coordinates
        	g.setColor(new Color (250, 222, 151)); //original - (255, 233, 153)
        	g.fillPolygon(new Polygon(leftFolderXY[0], leftFolderXY[1], leftFolderXY[0].length)); //draw background folder
        	
        	g.drawImage(rulesText, 85, 55, 110, 35, null); //"RULES"
        	g.drawImage(pokeballIcon, 125, 132, 32, 32, null); //next to "SPAWN HEALTH"
        	g.drawImage(spawnHealthText, 170, 132, 320, 40, null); //"SPAWN HEALTH"
        	g.drawImage(pokeballIcon, 125, 350, 32, 32, null); //next to "DAMAGE"	
        	g.drawImage(damageText, 170, 350, 180, 40, null); //"DAMAGE"	
        	g.drawImage(backButton, 600, 30, 150, 50, null); //blue back button (exits to main menu page)
        	g.drawImage(backArrow, 50, 510, 30, 30, null);
        	
        	//info text
        	//g.drawImage(infoText, 80, 55, 670, 545, null);
        	g.setColor(Color.BLACK);
        	g.setFont(new Font("Verdana", Font.PLAIN, 25));
        	
        	//circles for bullet points (6x6 circles, on left of text - each new sentence); line up at 195
        	//start of text lines up at 210 (second lines start at 225)
        	
        	//initial health section
        	g.drawString("Initial health is randomized between:", 170, 195);
        	g.fillOval(155, 180, 6, 6);
        	g.drawString("Squirtle: 60-100 HP", 195, 225);
        	g.fillOval(180, 210, 6, 6);
        	g.drawString("Beedrill: 20-100 HP", 195, 255);
        	g.fillOval(180, 240, 6, 6);
        	g.drawString("Gastly: 80-100 HP", 195, 285);
        	g.fillOval(180, 270, 6, 6);
        	g.drawString("Growlithe: 20-100 HP", 195, 315);
        	g.fillOval(180, 300, 6, 6);
        	
        	//losing health section
        	g.drawString("Contact with another type: -50 HP", 170, 413);
        	g.fillOval(155, 398, 6, 6);
        	/*g.drawString("Contact with same type: ", 170, 443);
        	g.fillOval(155, 428, 6, 6);
        	g.drawString("Beedrills: -1 HP, Squirtles: -10 HP,", 195, 473);
        	g.fillOval(180, 458, 6, 6);
        	g.drawString("Growlithes: -5 HP, Gastly: lose all HP", 195, 503);*/
        	
        }
	}
	class HandlerClass implements MouseListener //reacting to mouse actions
    {
		private int x, y;
    	public void mouseClicked (MouseEvent event) 
    	{
    		x = event.getX(); //get click x coordinate
    		y = event.getY(); //get click y coordinate
    		
    		if (x>=600&&x<=750&&y>=30&&y<=80) //dimensions of button : 600, 30, 150, 50
    		{
    			setVisible(false); //"closes" the tab if user asks to return to user menu
    		}
    		else if (x>=40&&x<=90&&y>=500&&y<=550) //image - 50, 510, 30, 30
    		{
    			pokemonInfoGUI firstPage = new pokemonInfoGUI (); //can create new page because it is always identical (doesn't change)
    			firstPage.setVisible(true);
    			setVisible(false);
    		}
    	}
    	public void mousePressed (MouseEvent event) 
    	{
    		
    	}
    	public void mouseReleased (MouseEvent event) 
    	{
    		
    	}
    	public void mouseEntered (MouseEvent event)
    	{
    		
    	}
    	public void mouseExited (MouseEvent event)
    	{
    		
    	}
    }

}
