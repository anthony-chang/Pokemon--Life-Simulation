package Main;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

class pokemonInfoGUI extends JFrame {

	static Image movementPatternsText, rulesText, restrictionsText, pokeballIcon, backButton, arrow;
	
	public pokemonInfoGUI () {
		try {
			//infoText = ImageIO.read(new File("infoText.png")); 
			//load necessary images
			movementPatternsText = ImageIO.read(new File ("Graphics\\movement patterns.png"));
			rulesText = ImageIO.read(new File ("Graphics\\rules.png"));
			restrictionsText = ImageIO.read(new File ("Graphics\\restrictions.png"));
			pokeballIcon = ImageIO.read(new File ("Graphics\\pokeball icon.png"));
			backButton = ImageIO.read(new File("Graphics\\main.png"));
			arrow = ImageIO.read(new File ("Graphics\\arrow.png"));
		} catch (IOException e) {};
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
        	//int [][] backgroundFolderXY = {{325, 500, 550, 275}, {30, 30, 100, 100}};
        	
        	//g.setColor(new Color(200, 172, 101));
        	//g.fillPolygon(new Polygon(backgroundFolderXY[0], backgroundFolderXY[1], backgroundFolderXY[0].length));
        	
        	g.setColor(new Color (250, 222, 151)); //original - (255, 233, 153)
        	g.fillPolygon(new Polygon(leftFolderXY[0], leftFolderXY[1], leftFolderXY[0].length)); //draw background folder
        	
        	g.drawImage(rulesText, 85, 55, 110, 35, null); //"RULES"
        	
        	g.drawImage(pokeballIcon, 125, 132, 32, 32, null); //next to "MOVEMENT PATTERNS"
        	g.drawImage(movementPatternsText, 170, 132, 380, 40, null); //"MOVEMENT PATTERNS"
        	g.drawImage(pokeballIcon, 125, 350, 32, 32, null); //next to "RESTRICTIONS"	
        	g.drawImage(restrictionsText, 170, 350, 250, 40, null); //"RESTRICTIONS"	
        	g.drawImage(backButton, 600, 30, 150, 50, null); //blue back button (exits to main menu page)
        	g.drawImage(arrow, 695, 510, 30, 30, null);
        	
        	//info text
        	//g.drawImage(infoText, 80, 55, 670, 545, null);
        	g.setColor(Color.BLACK);
        	g.setFont(new Font("Verdana", Font.PLAIN, 25));
        	
        	//circles for bullet points (6x6 circles, on left of text - each new sentence); line up at 155
        	//start of text lines up at 170 (second lines start at 185)
        	
        	//rules section
        	g.drawString("Squirtles move towards water", 170, 195);
        	g.fillOval(155, 180, 6, 6);
        	g.drawString("Beedrills move towards trees", 170, 225);
        	g.fillOval(155, 210, 6, 6);
        	g.drawString("Gastlys move away from other Pokemon", 170, 255);
        	g.fillOval(155, 240, 6, 6);
        	g.drawString("Growlithes try to form herds with other", 170, 285);
        	g.fillOval(155, 270, 6, 6);
        	g.drawString("Growlithes", 185, 315);
        	
        	//restrictions section
        	g.drawString("Only Beedrills and Squirtles can travel", 170, 413);
        	g.fillOval(155, 398, 6, 6);
        	g.drawString("over water", 185, 443);
        	g.drawString("The user character can�t travel in lakes", 170, 473);
        	g.fillOval(155, 458, 6, 6);
        	g.drawString("(over water tiles)", 185, 503);
        	
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
    		else if (x>=690&&x<=730&&y>=500&&y<=550) //image - 695, 510, 30, 30
    		{
    			pokemonInfoGUI2 secondPage = new pokemonInfoGUI2();
    	        secondPage.setVisible(true);
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
