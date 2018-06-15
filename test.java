/*
ICS 4U FSE - LINEAR VERSION
Navidur Rahman, Hamza Saqib, Tailai Wang
Minecraft
3-D vanilla Minecraft created from scratch with no graphics assistance from GDX.
All classes were combined into one file for logistical purposes (too hard to share multiple GDX files)
User must install entire GRADLE and run via IntelliJ or Sublime to play the game.
Mouse Input sucks in GDX, so user must use arrow keys to navigate menu

 */
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter; //default
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input; //mouse and keyboard
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*; //drawing
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Arrays;
import java.io.*; //used for save/load
import java.nio.file.*;
import java.util.*;

public class test extends ApplicationAdapter { //declaring variables
	SpriteBatch batch; //drawing
	ShapeRenderer shapeRenderer; //used for shapedrawing
	OrthographicCamera camera; //the only GDX thing we used
	Texture[] grass; //grassblock
	Texture[] title; //anything in the main menu
	Texture[] menu; //menu items NOT in the main menu
	Texture[] worldText; //text in the load world menu
	ButtonRect play; //play button in main menu
	ButtonRect settings; //settings button in main menu
	ButtonRect musicButt; //music button in settins menu
	ButtonRect[] worldButts; //all buttons on load world menu
	ButtonRect[] pauseButts; //all buttons on pause menu
	boolean skip; //used in load world screen
	boolean musicPlay; //music is on or off

	String screen  = "Title"; //default
	Player p1;
	int screenWidth,screenHeight;
	int file; //VERY IMPORTANT: FILE NUMBER OF .WORLD FILE
	int[][][] world = new int[500][100][500]; //world is z,y,x
	Music music;
	@Override //messes with java.util
	public void create () { //create is self explanatory: you create stuff
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		//Loading all textures, they should be stored in the correct locations.
		title = new Texture[3];
		title[0] = new Texture("gui/minec.png");
		title[1] = new Texture("gui/raft.png");
		title[2] = new Texture("gui/sky.png");
		menu = new Texture[14];
		menu[0] = new Texture("gui/play.png");
		menu[1] = new Texture("gui/settings.png");
		menu[2] = new Texture("gui/bar.png");
		menu[3] = new Texture("gui/LoadWorld.png");
		menu[4] = new Texture("gui/background1.jpg");
		menu[5] = new Texture("gui/back.png");
		menu[6] = new Texture("gui/border2.png");
		menu[7] = new Texture("gui/paused.png");
		menu[8] = new Texture("gui/SaveExit.png");
		menu[9] = new Texture("gui/unpause.png");
		menu[10] = new Texture("gui/overwriteExit.png");
		menu[11] = new Texture("gui/musicOn.png");
		menu[12] = new Texture("gui/musicOff.png");
		menu[13] = new Texture("gui/settingsText.png");
		grass = new Texture[3];
		grass[0] = new Texture("blocks/grass_top.jpg");
		grass[1] = new Texture("blocks/grass_side.png");
		grass[2] = new Texture("blocks/grass_bottom.jpg");
		worldText = new Texture[11];
		for (int i = 0; i < 11; i++){
			String in = "text_" + i+".png";
			worldText[i] = new Texture("gui/" + in);
		}//end input for

		music = Gdx.audio.newMusic(Gdx.files.internal("sound/minecraftmusic.mp3"));
		music.setLooping(true);
		music.setVolume(0.5f); //50% volume
		music.play();
		camera = new OrthographicCamera(800, 800); //we wanted adjustable screen... too much work
		screenHeight = 800;
		screenWidth = 800;


		p1 = new Player(250 * 100, 80 * 100, 250 * 100); //creating all objects
		play = new ButtonRect(Math.round((screenWidth/2) - menu[0].getWidth()/2), 266, menu[0].getWidth(), menu[0].getHeight());
		settings = new ButtonRect(Math.round((screenWidth/2) - menu[1].getWidth()/2),200, menu[1].getWidth(), menu[1].getHeight());
		play.setHighlighted(true); //default highlighted
		skip = false;
		musicPlay = true; //default unmuted
	}//end create

	public void render () { //all drawing is done here... this is the "event loop"
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		if (musicPlay){ //mutes music based on user input (located in settings menu)
			music.setVolume(0.5f);
		}
		else{
			music.setVolume(0);
		}

		if (screen == "Title"){ //the screen String is used to indicate location within screens
			drawTitle(screenWidth,screenHeight);
			drawButtons(screenWidth,screenHeight);
			if (Gdx.input.isKeyJustPressed(Input.Keys.UP)){ //arrow key scrolling through menu buttons
				if(play.getHighlighted() == true){
					play.setHighlighted(false);
					settings.setHighlighted(true);
					drawButtons(screenWidth,screenHeight);
				}
				else{
					play.setHighlighted(true);
					settings.setHighlighted(false);
					drawButtons(screenWidth,screenHeight);
				}
			}//end Scrolling Up
			else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){ //arrow key scrolling through menu buttons
				if(play.getHighlighted() == true){
					play.setHighlighted(false);
					settings.setHighlighted(true);
					drawButtons(screenWidth,screenHeight);
				}
				else{
					play.setHighlighted(true);
					settings.setHighlighted(false);
					drawButtons(screenWidth,screenHeight);
				}
			}//end scrolling Down

			if(play.isPressed()){ //isPressed always refers to a menu button
				screen = "World";
				skip = false;
				try{
					Scanner inFile = new Scanner(new File("worlds/worlds.txt")); //worlds.txt is the directory of all current .WORLD files.
					int n = 0;
					while (inFile.hasNextInt()){
						n = inFile.nextInt(); //n represents the number of .WORLD files
					}
					worldButts = new ButtonRect[n]; //array is as long as number of .WORLD files
					int count =0;
					for(int i = 25; i < (screenHeight - 100); i+= (menu[2].getHeight() + 25)){ //creates ButtonRect based on actual location on screen
						if (count < worldButts.length){
							worldButts[count] = new ButtonRect(Math.round(screenWidth/2) - menu[2].getWidth()/2, i, menu[2].getWidth(), menu[2].getHeight());
							count++;
						}
					}//end for
				}//end try
				catch (IOException e){
					System.out.println("Worlds.txt is missing... this program is worthless");
				}
				worldButts[0].setHighlighted(true); //default highlighted
			}//end if playPressed

			if(settings.isPressed()) {
				screen = "Settings";
				musicButt = new ButtonRect(screenWidth/2 - menu[2].getWidth()/2,400, menu[2].getWidth(), menu[2].getHeight());
			}//end settingsPressed
		}//end title

		if (screen == "World"){
			drawWorldMenu();
			if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){ //ESCAPE is just back
				screen = "Title";
				skip = false;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.UP)){
				int c = 0;
				skip = true;
				while (true){
					if (worldButts[c].getHighlighted() == true){ //scrolling through menu buttons
						if ((c + 1) < worldButts.length){
							worldButts[c+1].setHighlighted(true); //sets next one as highlighted
							worldButts[c].setHighlighted(false);
							break;
						}
					}
					if (c + 1 <worldButts.length){
						c++; //for the culture
					}
					else{
						break;
					}
				}//end while
			}//end scrolling up
			else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){ //opposite of scrolling up
				int c = worldButts.length - 1;
				skip = true;
				while (true){
					if (worldButts[c].getHighlighted() == true){
						if ((c - 1) >= 0){
							worldButts[c-1].setHighlighted(true); //sets one below as highlighted
							worldButts[c].setHighlighted(false);
							break;
						}
					}
					if (c - 1 > 0){
						c--;
					}
					else{
						break;
					}
				}//end while
			}//end scrolling down

			for (int i = 0; i < worldButts.length; i++){ //checking for pressing
				if (worldButts[i].isPressed() && skip == true){
					file = i; //THE IMPORTANT VARIABLE FOR OVERWRITING
					String run = "worlds/world_" + i +".world";
					System.out.println("Clicked" + i);
					loadWorld(run);
					screen = "Game";
				}
			}//end for
		}//end World -> end of the world... get it? HAHAHA I'm a loser

		if (screen == "Settings"){ //not many settings here...
			drawSettingsMenu(musicPlay);
			if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
				screen = "Title";
				musicButt.setHighlighted(false);
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.UP)){
				musicButt.setHighlighted(true);
			}//only highlighted after a keyboard movement.
			if (musicButt.isPressed()){ //turns off highlight after pressed
				musicButt.setHighlighted(false);
				if (musicPlay){
					musicPlay = false;
				}
				else{
					musicPlay = true;
				}
			}
		}//end settings

		if (screen == "Game"){ //most of this is just how the player interacts with the world
			int x = p1.getX(); //SEE GETTERS AND SETTERS ARE USEFUL
			int y = p1.getY();
			int z = p1.getZ();
			if(p1.onGround(world) == false){
				p1.applyGravity(); //sets vy for gravity
			}
			batch.draw(title[2], 0,0);
			p1.click(world);
			p1.move(p1.onGround(world),p1.checkCollision(world)); //applying movement
			if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){ //we create buttons here because this statement only hits once: no unnecessary repetition.
				screen = "Pause";
				pauseButts = new ButtonRect[3]; //we need a list because of scrolling with arrow keys
				pauseButts[0] = new ButtonRect(screenWidth/2 - menu[2].getWidth()/2, 380, menu[2].getWidth(), menu[2].getHeight()); //unpause
				pauseButts[1] = new ButtonRect(screenWidth/2 - menu[2].getWidth()/2, 300, menu[2].getWidth(), menu[2].getHeight()); //saveExit
				pauseButts[2] = new ButtonRect(screenWidth/2 - menu[2].getWidth()/2, 220, menu[2].getWidth(), menu[2].getHeight()); //overwriteExit
				pauseButts[0].setHighlighted(true);
			}
         /*This project was the "Pen and Paper" project. The vast majority of the first 3 weeks was spent doing math to figure these 3 loops out. The next 10
           lines of code were the product of much struggle. The loops below go through each of the 250 000 000 blocks that are in range and associates them
           with a px, py, and pz. The px,py,and pz are then checked through the world[][][] to verify that a block is there. If there is a block,
           the loop calls drawBlock().
          */
			for (int pz = (int)Math.floor((z + 3000)/100)*100; pz > (int)Math.ceil((z + 100)/100)*100; pz -= 100) { //fancy black magic for drawing the blocks
				for (int px = (int)Math.ceil((x - 0.4*(pz-z))/100)*100 - 100; px <= Math.floor((x + 0.4*(pz-z))/100)*100 + 100; px += 100) {
					for (int py = (int)Math.ceil((y - 0.4*(pz-z))/100)*100 - 100; py <= Math.floor((y + 0.4*(pz-z))/100)*100 + 100; py += 100) {
						if (pz/100 >= 0 && pz/100 < 500 && py/100 >= 0 && py/100 < 100 && px/100 >= 0 && px/100 < 500 && world[pz/100][py/100][px/100] == 1){
							drawBlock(px, py, pz, x, y, z);
						}//end if
					}//end for py
				}//end for px
			}//end for pz

			p1.drawGUI(batch);	//drawing all the gui
			if(p1.getInvOpen()){	//if inventory is open then allows moving of items
				p1.moveItems(batch);
			}
		}//end Game

		if (screen == "Pause"){
			drawPauseMenu();
			if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
				int c = 0;
				while (true){
					if (pauseButts[c].getHighlighted() == true){ //scrolls through the 3 buttons
						if ((c + 1) < pauseButts.length){
							pauseButts[c+1].setHighlighted(true); //sets one above as highlighted
							pauseButts[c].setHighlighted(false);
							break;
						}
					}
					if (c + 1 <pauseButts.length){
						c++;
					}
					else{
						break;
					}
				}//end while
			}//end scrolling down
			else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)){ //else if to avoid double clicking
				int c = pauseButts.length - 1;
				while (true){
					if (pauseButts[c].getHighlighted() == true){
						if ((c - 1) >= 0){
							pauseButts[c-1].setHighlighted(true); //sets one below as highlighted
							pauseButts[c].setHighlighted(false);
							break;
						}
					}
					if (c - 1 > 0){
						c--;
					}
					else{
						break;
					}
				}//end while
			}//end scrolling up

			if (pauseButts[0].isPressed()){ //no need for else if here because there is only one highlighted at a time.
				screen = "Game";
			}
			if (pauseButts[1].isPressed()){
				saveWorld();
				screen = "Title";
			}
			if (pauseButts[2].isPressed()){
				overwriteWorld();
				screen = "Title";
			}
		}//end pause
		batch.end();
	}//end render

	public void drawBlock(int px, int py, int pz, int x, int y, int z){ //RAW BLACK MAGIC
		/*This section of code takes in the x,y,z of the player and the px, py, and pz in question. The math being done is what limits
		the screen size. All the math is assuming that we have a 800x800 screen. The program finds each block that needs to be created
		and calculates its relative distance in all 3 direction (x,y,z). We then use the GDX sprite batch to draw the opaque
		image. The image skewing on GDX is slightly messed up, but it is still very clean.
		 */

		int x2, y2;
		double rad = 100000 / (pz - z);
		batch.draw(grass[1], 400 +  1000 * (x - px) / (pz - z), 400 + 1000 * (y - py) / (pz - z), (int)rad , (int)rad); //front

		if (px - 100 > x) { //right
			x2 = 400 + 1000 * (x - px) / (pz + 100 - z) + 100000 / (pz + 100 - z);
			y2 = 400 + 1000 * (y - py) / (pz + 100 - z);
			batch.draw(grass[1], new float[]{400 + 1000 * (x - px) / (pz - z) + (int) rad, 400 + 1000 * (y - py) / (pz - z), Color.toFloatBits(255, 255, 255, 255), 0, 1,
					400 + 1000 * (x - px) / (pz - z) + (int) rad, 400 + 1000 * (y - py) / (pz - z) + (int) rad, Color.toFloatBits(255, 255, 255, 255), 0, 0,
					x2, y2 + 100000 / (pz + 100 - z), Color.toFloatBits(255, 255, 255, 255), 1, 0,
					x2, y2, Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}
		else if (px < x){ //left
			x2 = 400 + 1000 * (x - px) / (pz + 100 - z);
			y2 = 400 + 1000 * (y - py) / (pz + 100 - z);
			batch.draw(grass[1], new float[]{400 + 1000 * (x - px) / (pz - z), 400 + 1000 * (y - py) / (pz - z), Color.toFloatBits(255, 255, 255, 255), 0, 1,
					400 + 1000 * (x - px) / (pz - z), 400 + 1000 * (y - py) / (pz - z) + (int) rad, Color.toFloatBits(255, 255, 255, 255), 0, 0,
					x2, y2 + 100000 / (pz + 100 - z), Color.toFloatBits(255, 255, 255, 255), 1, 0,
					x2, y2, Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}

		if (py - 100 > y) { // top
			x2 = 400 + 1000 * (x - px) / (pz + 100 - z);
			y2 = 400 + 1000 * (y - py) / (pz + 100 - z) + 100000 / (pz + 100 - z);
			batch.draw(grass[0], new float[]{400 + 1000 * (x - px) / (pz - z), 400 + 1000 * (y - py) / (pz - z) + (int) rad, Color.toFloatBits(255, 255, 255, 255), 0, 1,
					400 + 1000 * (x - px) / (pz - z) + (int) rad, 400 + 1000 * (y - py) / (pz - z) + (int) rad, Color.toFloatBits(255, 255, 255, 255), 0, 0,
					x2 + 100000 / (pz + 100 - z), y2, Color.toFloatBits(255, 255, 255, 255), 1, 0,
					x2, y2, Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}
		else if (py < y){ // bottom
			x2 = 400 + 1000 * (x - px) / (pz + 100 - z);
			y2 = 400 + 1000 * (y - py) / (pz + 100 - z);
			batch.draw(grass[2], new float[]{400 + 1000 * (x - px) / (pz - z), 400 + 1000 * (y - py) / (pz - z), Color.toFloatBits(255, 255, 255, 255), 0, 1,
					400 + 1000 * (x - px) / (pz - z) + (int) rad, 400 + 1000 * (y - py) / (pz - z), Color.toFloatBits(255, 255, 255, 255), 0, 0,
					x2 + 100000 / (pz + 100 - z), y2, Color.toFloatBits(255, 255, 255, 255), 1, 0,
					x2, y2, Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}
	}//end drawBlock

	public void overwriteWorld(){ //overwrites the current .WORLD file, saves the world.
		byte[] allBytes = new byte[500*100*500];
		int c = 0;

		for (int zI = 0; zI < 500; zI++) {
			for (int yI = 0; yI < 100; yI++) {
				for (int xI = 0; xI < 500; xI++) {
					allBytes[c++] = (byte)world[zI][yI][xI]; //creates a new byte for each block
				}//end for
			}//end for
		}//end for

		String fileName = "worlds/world_" +file + ".world"; //THIS IS WHERE FILE COMES THROUGH
		try{
			Files.write(Paths.get(fileName), allBytes);
		}
		catch (IOException ex){
			System.out.println("Your program is worthless");
		}
	}

	public void saveWorld(){ //saves world as new .WORLD file
		byte[] allBytes = new byte[500*100*500]; //250 000 000 blocks in one Byte Array? Sounds good
		int c = 0;

		for (int zI = 0; zI < 500; zI++) {
			for (int yI = 0; yI < 100; yI++) {
				for (int xI = 0; xI < 500; xI++) {
					allBytes[c++] = (byte)world[zI][yI][xI]; //each block is converted into a byte
				}//end for
			}//end for
		}//end for

		try {
			Scanner inFile = new Scanner(new File("worlds/worlds.txt")); //we need to use the current directory
			int n = 0;
			while (inFile.hasNextInt()){
				n = inFile.nextInt(); //finds last n -> the last file saved
			}
			String fileName = "worlds/world" + "_" + n + ".world"; //creates new .WORLD file
			Files.write(Paths.get(fileName), allBytes);

			inFile.close(); //closes the reader
			n = n +1;
			String in = "" + n; //worlds.txt is always present in the assets folder, along with world_0.world (it's the default)
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("worlds.txt",true)));
			writer.append("");
			writer.println("\n" + in); //adds file to the directory
			writer.close();
		} //end try
		catch (IOException ex) {
			ex.printStackTrace();
		}//end catch
	}//end saveWorld()

	public void loadWorld(String fileName){ //loads a .world file
		try {
			long start = System.currentTimeMillis();

			byte[] allBytes = Files.readAllBytes(Paths.get(fileName)); //forces everything onto the world[][][]
			int c = 0;
			for(int z = 0; z<500; z++){
				for(int y = 0; y<100; y++){
					for(int x=0; x<500;x++){
						world[z][y][x] = allBytes[c++];
					}
				}
			}

			long end = System.currentTimeMillis();
			System.out.println("Loaded in " + (end - start) + " ms"); //kept the timers, just to see the difference with more blocks :)
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}//end loadWorld

	/* Below are all the UI methods... GDX UI sucks. Tailai had to hard code for 8 hours straight because of the mouse problem. Hopefully
    this hard coded UI is good :)
     */
	public void drawTitle(int screenWidth, int screenHeight){
		batch.draw(menu[4], 0, 0); //background
		batch.draw(title[0], (Math.round(screenWidth/2) - (Math.round(title[0].getWidth()))) + 50, Math.round(screenHeight/2)); //MINEC
		batch.draw(title[1], Math.round(screenWidth/2) + (Math.round(title[1].getWidth()/4)) - 20, Math.round(screenHeight/2)); //RAFT
	}//end drawTitle

	public void drawButtons(int screenWidth, int screenHeight){ //mainMenu Buttons
		batch.draw(menu[0], Math.round(screenWidth/2) - menu[0].getWidth()/2 , 266); //play
		if (play.getHighlighted() == true){
			batch.draw(menu[6], play.getX(), play.getY()); //draws highlight
		}
		batch.draw(menu[1], Math.round(screenWidth/2) - menu[1].getWidth()/2 , 200); //settings
		if (settings.getHighlighted() == true){
			batch.draw(menu[6], settings.getX(), settings.getY()); //draws highlight
		}
	}//end drawButtons

	public void drawWorldMenu(){
		batch.draw(menu[4], 0, 0); //background
		batch.draw(menu[5], 10, 700); //title text
		batch.draw(menu[3], Math.round(screenWidth/2) - (menu[3].getWidth()/2),screenHeight - 100);
		for(int i = 25; i < (screenHeight - 100); i+= (menu[2].getHeight() + 25)){
			batch.draw(menu[2],Math.round(screenWidth/2) - menu[2].getWidth()/2, i);
		}
		for (int i = 0; i < worldButts.length; i++){
			batch.draw(worldText[i], worldButts[i].getX() + (worldButts[i].getWidth()/2 - worldText[i].getWidth()/2), worldButts[i].getY() + (worldButts[i].getHeight()/2 - worldText[i].getHeight()/2));
			if (worldButts[i].getHighlighted()){
				batch.draw(menu[6], worldButts[i].getX(), worldButts[i].getY()); //highlight
			}
		}
	}//end draw worldMenu

	public void drawSettingsMenu(boolean music){ //not much here yet
		batch.draw(menu[4], 0, 0);
		batch.draw(menu[5], 10, 700);
		batch.draw(menu[13], screenWidth/2 - menu[13].getWidth()/2, 700);
		batch.draw(menu[2], musicButt.getX(), musicButt.getY());
		if (music){ //draws appropriate button.
			batch.draw(menu[11], musicButt.getX(), musicButt.getY());
		}
		else{
			batch.draw(menu[12], musicButt.getX(), musicButt.getY());
		}

		if (musicButt.getHighlighted()){
			batch.draw(menu[6], musicButt.getX(), musicButt.getY());
		}
	}//end drawSettingsMenu

	public void drawPauseMenu(){
		batch.draw(menu[4], 0, 0);
		batch.draw(menu[7], screenWidth/2 - menu[7].getWidth()/2, 700); //Paused
		batch.draw(menu[2], screenWidth/2 - menu[2].getWidth()/2, 380); //Bar
		batch.draw(menu[9], screenWidth/2 - menu[2].getWidth()/2, 380); //Unpause
		batch.draw(menu[2], screenWidth/2 - menu[2].getWidth()/2, 300); //Bar
		batch.draw(menu[8], screenWidth/2 - menu[2].getWidth()/2, 300); //Save and Exit
		batch.draw(menu[2], screenWidth/2 - menu[2].getWidth()/2, 220); //bar
		batch.draw(menu[10], screenWidth/2 - menu[2].getWidth()/2, 220); //OverWrite and Exit
		if (pauseButts[0].getHighlighted()){
			batch.draw(menu[6], pauseButts[0].getX(), pauseButts[0].getY());
		}
		if (pauseButts[1].getHighlighted()){
			batch.draw(menu[6], pauseButts[1].getX(), pauseButts[1].getY());
		}
		if (pauseButts[2].getHighlighted()){
			batch.draw(menu[6], pauseButts[2].getX(), pauseButts[2].getY());
		}
	}//end drawPauseMenu

	@Override
	public void dispose () {
		batch.dispose();
		music.dispose();
	}//end dispose
}//end class test


class Player{
	private int vx,vy,vz; //speed
	private int x, y, z; //position
	private int bx,by;
	private long lastW; //sprinting variables
	private boolean sprint,w,invOpen;
	private Sound[] dig; //sound effects
	private Inventory I;	//player inventory
	private Texture hotbar, tabItems, chosen, crosshair, border;	//all the textures needed for GUI
	private Item clickedItem;	//variable used for moving items in inventory
	public Player(int xPos, int yPos, int zPos){
		vx = 0;
		vy = 0;
		vz = 0;
		x = xPos; //position in array
		y = yPos;
		z = zPos;
		w = false; //used for sprinting
		lastW = 1000; //1000 is just an arbitrary large number
		sprint = false;
		dig = new Sound[2];
		dig[0] = Gdx.audio.newSound(Gdx.files.internal("sound/dig/cloth1.ogg")); //breaking
		dig[1] = Gdx.audio.newSound(Gdx.files.internal("sound/dig/cloth2.ogg")); //placing
		I = new Inventory();	//constructing inventory
		invOpen = false;	//inventory starts closed
		clickedItem = new Item();	//constructing "empty" item
		hotbar = new Texture("Gui/hotbar.png");
		tabItems = new Texture("Gui/tab_items.png");
		chosen = new Texture("Gui/chosen.png");
		crosshair = new Texture("Gui/crosshair.png");
		border = new Texture("Gui/Border.png");
	}
	public void hotbarScroll(){		//method used for switching current items using numpad
		//Getting Input
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){	//sets the chosen num to the number inputted
			I.setChosenNum(1);
			I.setChosenItem(1);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
			I.setChosenNum(2);
			I.setChosenItem(2);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
			I.setChosenNum(3);
			I.setChosenItem(3);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)){
			I.setChosenNum(4);
			I.setChosenItem(4);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)){
			I.setChosenNum(5);
			I.setChosenItem(5);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)){
			I.setChosenNum(6);
			I.setChosenItem(6);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_7)){
			I.setChosenNum(7);
			I.setChosenItem(7);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_8)){
			I.setChosenNum(8);
			I.setChosenItem(8);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_9)){
			I.setChosenNum(9);
			I.setChosenItem(9);
		}
	}
	public void drawGUI(SpriteBatch batch){	//method used to draw all the GUI
		batch.draw(hotbar,130,0,540,60);	//draws the hotbar texture at the bottom of the screen
		for(int i = 0; i < 9; i++){		//loops through items of hotbar checking if it contains an item
			if(!I.getHotbar()[i].isNothing()){	//if item is not "nothing" item
				Item curItem = I.getHotbar()[i];
				curItem.drawItem(batch,137 + 60*i, 5, 46, 46);	//if item exists draws it at correct place
			}
		}
		batch.draw(chosen,73 + 60*(I.getChosenNum()),-2,60,60);		//draws the indicator of chosen item

		if(invOpen){	//if inventory is open
			batch.draw(tabItems, 107, 196,585,408);	//draws the tabbed items texture in the middle of the screen
			for(int i = 0; i < 9; i++){		//loops through each item in the tabbed hotbar checking for items
				if(!I.getHotbar()[i].isNothing()){
					Item curItem = I.getHotbar()[i];
					curItem.drawItem(batch, 134 + 54*i, 220, 48, 48);
				}
			}
			for(int i = 0; i < 9; i ++){	//loops through each item in tabbed inventory
				for(int j = 0; j < 5; j++){
					if(!I.getItems()[j][i].isNothing()){
						Item curItem = I.getItems()[j][i];
						curItem.drawItem(batch, 134 + 54*i, 286 + 54*j, 48, 48);
					}
				}
			}
		}
		else{	//if inventory is closed
			batch.draw(crosshair,386,386,27,27);	//draws the player's crosshair
		}

	}
	public void moveItems(SpriteBatch batch){	//method used for the moving of items in the inventory
		com.mygdx.game.ButtonRect2[][] buttons = I.getButtons();	//2D ButtonRect array of the players inventory buttons
		buttons[by][bx].setHighlighted(true);	//the default selected item is highlighted

		//Moving the highlighted button
		if(Gdx.input.isKeyJustPressed((Input.Keys.RIGHT))){	//if right
			buttons[by][bx].setHighlighted(false);	//sets old button to false
			if(bx < 8){	//going back to the start
				bx++;
			}
			else{
				bx = 0;
			}
			buttons[by][bx].setHighlighted(true);	//sets new button to true
		}
		if(Gdx.input.isKeyJustPressed((Input.Keys.LEFT))){	//if left
			buttons[by][bx].setHighlighted(false);
			if(bx > 0){
				bx--;
			}
			else{
				bx = 8;
			}
			buttons[by][bx].setHighlighted(true);
		}
		if(Gdx.input.isKeyJustPressed((Input.Keys.UP))){	//if up
			buttons[by][bx].setHighlighted(false);
			if(by < 5){
				by++;
			}
			else{
				by = 0;
			}
			buttons[by][bx].setHighlighted(true);
		}
		if(Gdx.input.isKeyJustPressed((Input.Keys.DOWN))){	//if down
			buttons[by][bx].setHighlighted(false);
			if(by > 0){
				by--;
			}
			else{
				by = 5;
			}
			buttons[by][bx].setHighlighted(true);
		}

		for(int i = 0; i < 9; i++){		//loops through each button displaying the highlighted button
			for(int j = 0; j < 6; j++){
				if(buttons[j][i].getHighlighted()){
					buttons[j][i].drawBox(batch, border);
				}
			}
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){	//user selects highlighted item
			if(by == 0){	//if item is in the hotbar
				Item holder = clickedItem;	//used to swap the selected item and highlighted item
				clickedItem = I.getHotbar()[bx];
				I.getHotbar()[bx] = holder;
			}

			else{	//if item is in the inventory
				Item holder = clickedItem;
				clickedItem = I.getItems()[by-1][bx];
				I.getItems()[by-1][bx] = holder;
			}
		}

		if(!clickedItem.isNothing()){	//if clicked item is not a "nothing" item it displays it slightly above the box
			if(by == 0){	//item is in hotbar
				clickedItem.drawItem(batch, 134 + 54*bx, 235, 48, 48);
			}

			else {	//item is in inventory
				clickedItem.drawItem(batch, 134 + 54*bx,301 + 54*(by-1), 48, 48);
			}
		}
	}
	public void move(boolean onGround,int[]collisions){
		if (w && !Gdx.input.isKeyPressed(Input.Keys.W) && collisions[0] ==0){ //double tapping w allows you to sprint
			w = false;
			sprint = false;
			lastW = System.currentTimeMillis();
		}//end if
		else if(Gdx.input.isKeyPressed(Input.Keys.W) && collisions[0] == 0){
			w = true;
			long current = System.currentTimeMillis();
			System.out.println(current - lastW);
			if (current - lastW < 100){ //only sprints if double tapped
				sprint = true;
			}
			if (sprint == false){
				vz = 10;
			}
			else {
				vz = 20;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S) && collisions[1] == 0){ //applying movement based on collisions and key
			vz = -10;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A) && collisions[2] == 0){
			vx = 5;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D) && collisions[3] == 0){
			vx = -5;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && onGround && collisions[4] == 0) {
			vy = -10 ;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.E)){	//toggles inventory
			invOpen = !invOpen;
		}
		x += vx; //adding velocity values
		y += vy;
		z += vz;
		vx = 0;
		vz = 0;
	}//end move

	public void click(int[][][]world){ //this checks
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			int count = 1; //breaks blocks up to 4 away.
			int xI = Math.round(x/100) + 1;
			int yI = Math.round(y/100) + 1;
			int zI = Math.round(z/100) + 1;
			while (true){
				if (world[zI + count][yI][xI] == 1){
					world[zI + count][yI][xI] = 0;
					System.out.println("Broken!");
					dig[0].play();
					break;
				}
				else if (count == 4){
					System.out.println("Not in Range");
					break;
				}
				else{
					count+= 1;
				}
			}
		}//left click for breaking
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
			int xI = Math.round(x/100) + 1; //places block in front of you
			int yI = Math.round(y/100) + 1;
			int zI = Math.round(z/100) + 1;
			if (world[zI +1][yI][xI] == 0){
				System.out.println("Placed!");
				dig[1].play();
				world[zI + 1][yI][xI] = 1;
			}
		}//right click for placing
	}//end click

	public void applyGravity(){ //apply gravity is called if not onGround
		if (vy < 20){
			vy += 1;
		}
	}//end applyGravity

	public boolean onGround(int[][][] world){ //onGround is seperate from checkCollision because of movement methods.
		int xI = Math.round(x/100) + 1;
		int yI = Math.round(y/100) + 1;
		int zI = Math.round(z/100) + 1;
		if ((zI > 0 && zI <500 ) && (xI > 0 && xI < 500) && (yI > 0 && yI <500)) { //if there's a block underneath
			if (world[zI][yI + 1][xI] != 0) {
				vy = 0;
				return true;
			}
		}
		return false;
	}//end onGround

	public int[] checkCollision(int[][][]world){
		int[] output = new int[5];
		output[0] = 0;
		output[1] = 0;
		output[2] = 0;
		output[3] = 0;
		output[4] = 0;
		int xI = Math.round(x/100) + 1; //the exact pixel position on the screen
		int yI = Math.round(y/100) + 1;
		int zI = Math.round(z/100) + 1;
		if ((zI > 0 && zI <500 ) && (xI > 0 && xI < 500) && (yI > 0 && yI <500)) { //only if the player is oin the world
			if (world[zI + 1][yI][xI] != 0) { //collision checking in all directions, self-explanatory
				System.out.println("front");
				output[0] = 1; //front
			}
			if (world[zI - 1][yI][xI] != 0){
				System.out.println("back");
				output[1] = 1; //back
			}
			if (world[zI][yI][xI + 1] != 0){
				System.out.println("left");
				output[2] = 1; //left
			}
			if (world[zI][yI][xI - 1] != 0){
				System.out.println("right");
				output[3] = 1; //right
			}
			if (world[zI][yI - 1][xI] != 0){
				System.out.println("up");
				output[4] = 1; //up
			}
		}
		return output; //returns int[] to indicate collisions: 1 is a collision, 0 is free
	}

	public int getX(){
		return x;
	} //getters and setters
	public int getY(){
		return y;
	}
	public int getZ() { return z; }
	public long getLastW(){return lastW;}
	public int getVX(){ return vx; }
	public int getVY(){ return vy; }
	public int getVZ(){ return vz; }
	public boolean getInvOpen(){return invOpen;}
}//end Player Class

class Inventory {    //inventory class storing all the items
	private Item[][] items;        //2D array storing all items in inventory
	private ButtonRect2[][] buttons;        //2D array storing all the buttons of the inventory
	private Item[] hotbar;    //array storing all the items in the hotbar
	private int chosenNum = 1;    //current selected item in hotbar
	private Item chosenItem;    //the item at the chosen place in hotbar

	public Inventory() {        //constructer class
		items = new Item[5][9];
		buttons = new ButtonRect2[6][9];
		hotbar = new Item[9];
		Arrays.fill(hotbar, new Item());    //filling the hotbar array with "nothing" items
		for (Item[] itemArray : items) {
			Arrays.fill(itemArray, new Item());    //filling the inventory with "nothing" items
		}
		for (int i = 0; i < 9; i++) {        //creating the buttons for the hotbar
			buttons[0][i] = new ButtonRect2(131 + 54 * i, 220, 50, 50);
		}
		for (int i = 0; i < 9; i++) {    //creating the buttons for the inventory
			for (int j = 1; j < 6; j++) {
				buttons[j][i] = new ButtonRect2(131 + 54 * i, 229 + 54 * j, 50, 50);
			}
		}
		chosenItem = hotbar[chosenNum - 1];

		//Opening all the needed block textures
		hotbar[0] = new Item("Items/Oak Log.png");
		hotbar[2] = new Item("Items/Oak Log.png");
		items[0][8] = new Item("Items/Stone.png");
		items[1][3] = new Item("Items/Cobblestone.png");
		items[2][5] = new Item("Items/Sand.png");
		items[3][0] = new Item("Items/Dirt.png");
		items[4][7] = new Item("Items/Grass.png");

	}
	//Getters and Setters
	public void setChosenNum(int v){
		chosenNum = v;
	}

	public int getChosenNum(){
		return chosenNum;
	}

	public Item[] getHotbar(){
		return hotbar;
	}

	public Item[][] getItems(){
		return items;
	}

	public ButtonRect2[][] getButtons() {
		return buttons;
	}

	public void setChosenItem(int v){
		chosenItem = hotbar[v-1];
	}
}//end Inventory

class Item{	//item classed for each item in the inventory
	private Texture icon;	//texture of the item
	private boolean nothing = false;	//boolean variable if its a "nothing" item

	public Item(String s){	//constructer class for actual item
		icon = new Texture(s);
	}
	public Item(){	//constructer class for "nothing" item
		icon = null;
		nothing = true;
	}

	public void drawItem(SpriteBatch batch, int x, int y, int w, int h){	//method to draw the item
		batch.draw(icon, x, y, w, h);
	}

	public boolean isNothing(){		//getter for nothing variable
		return nothing;
	}
}

class ButtonRect{
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean highlighted;
	public ButtonRect(int xPos, int yPos, int sizeWidth, int sizeHeight){
		x = xPos;
		y = yPos;
		width = sizeWidth;
		height = sizeHeight;
		highlighted = false;
	}//end constuctor

	public boolean isPressed(){ //you can press it only if it's highlighted
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
			if (highlighted){
				return true;
			}
		}
		return false;
	}


	public void setHighlighted(boolean var){
		highlighted = var;
	} //getters and setters
	public boolean getHighlighted(){
		return highlighted;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
}//end ButtonRect Class

class ButtonRect2{
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean highlighted;
	public ButtonRect2(int xPos, int yPos, int sizeWidth, int sizeHeight){
		x = xPos;
		y = yPos;
		width = sizeWidth;
		height = sizeHeight;
		highlighted = false;
	}//end constuctor

	public boolean isPressed(){ //you can press it only if it's highlighted
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
			if (highlighted){
				return true;
			}
		}
		return false;
	}
	public void drawBox(SpriteBatch batch, Texture t){
		batch.draw(t, x, y, width, height);
	}

	public void setHighlighted(boolean var){
		highlighted = var;
	} //getters and setters
	public boolean getHighlighted(){
		return highlighted;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
}//end ButtonRect2 Class