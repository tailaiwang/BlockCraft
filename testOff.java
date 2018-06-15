/*
ICS 4U FSE - ROTATION VERSION
Navidur Rahman, Hamza Saqib, Tailai Wang
Minecraft
3-D vanilla Minecraft created from scratch with no graphics assistance from GDX.
All classes were combined into one file for logistical purposes (too hard to share multiple GDX files)
User must install entire GRADLE and run via IntelliJ or Sublime to play the game.

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

import java.io.*; //used for save/load
import java.nio.file.*;
import java.util.*;

public class test extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	Texture[] grass; //grassblock
	Texture[] title; //anything in the main menu
	Texture[] menu; //menu items NOT in the main menu
	Texture[] worldText; //text in the load world menu
	ButtonRect play; //play button in main menu
	ButtonRect settings; //settings button in main menu
	ButtonRect musicButt;
	ButtonRect[] worldButts; //all buttons on load world menu
	ButtonRect[] pauseButts; //all buttons on pause menu
	boolean skip; //used in load world screen
	boolean musicPlay; //music is on or off

	String screen  = "Title"; //default
	Player p1;
	int screenWidth,screenHeight;
	int file; //VERY IMPORTANT: FILE NUMBER OF .WORLD FILE
	Block[][][] world = new Block[500][100][500]; //world is z,y,x
	Music music;
	@Override //messes with java.util
	public void create () { //create is self explanatory: you create stuff
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

		title = new Texture[2];
		title[0] = new Texture("minecraft/textures/gui/title/minec.png");
		title[1] = new Texture("minecraft/textures/gui/title/raft.png");
		menu = new Texture[14];
		menu[0] = new Texture("minecraft/textures/gui/title/play.png");
		menu[1] = new Texture("minecraft/textures/gui/title/settings.png");
		menu[2] = new Texture("minecraft/textures/gui/title/bar.png");
		menu[3] = new Texture("minecraft/textures/gui/title/LoadWorld.png");
		menu[4] = new Texture("minecraft/textures/gui/title/background1.jpg");
		menu[5] = new Texture("minecraft/textures/gui/title/back.png");
		menu[6] = new Texture("minecraft/textures/gui/title/border.png");
		menu[7] = new Texture("minecraft/textures/gui/title/paused.png");
		menu[8] = new Texture("minecraft/textures/gui/title/SaveExit.png");
		menu[9] = new Texture("minecraft/textures/gui/title/unpause.png");
		menu[10] = new Texture("minecraft/textures/gui/title/overwriteExit.png");
		menu[11] = new Texture("minecraft/textures/gui/title/musicOn.png");
		menu[12] = new Texture("minecraft/textures/gui/title/musicOff.png");
		menu[13] = new Texture("minecraft/textures/gui/title/settingsText.png");
		grass = new Texture[3];
		grass[0] = new Texture("grass_top.jpg");
		grass[1] = new Texture("grass_side.png");
		grass[2] = new Texture("grass_bottom.jpg");
		worldText = new Texture[11];
		for (int i = 0; i < 11; i++){
			String in = "text_" + i+".png";
			worldText[i] = new Texture("minecraft/textures/gui/title/" + in);
		}//end input for

		music = Gdx.audio.newMusic(Gdx.files.internal("minecraftmusic.mp3"));
		music.setLooping(true);
		music.setVolume(0.5f);
		music.play();
		camera = new OrthographicCamera(800, 800); //we wanted adjustable screen... too much work
		screenHeight = 800;
		screenWidth = 800;


		p1 = new Player(250 * 100, 7 * 100, 250 * 100, grass); //creating all objects
		play = new ButtonRect(Math.round((screenWidth/2) - menu[0].getWidth()/2), 266, menu[0].getWidth(), menu[0].getHeight());
		settings = new ButtonRect(Math.round((screenWidth/2) - menu[1].getWidth()/2),200, menu[1].getWidth(), menu[1].getHeight());
		play.setHighlighted(true); //default highlighted
		skip = false;
		musicPlay = true;
	}//end create

	public void render () { //all drawing is done here... this is the "event loop"
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		if (musicPlay){ //mutes music based on user input
			music.setVolume(0.5f);
		}
		else{
			music.setVolume(0);
		}

		if (screen == "Title"){ //the screen String is used to indicate location within screens
			drawTitle(screenWidth,screenHeight);
			drawButtons(screenWidth,screenHeight);
			if (Gdx.input.isKeyJustPressed(Input.Keys.UP)){
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
			else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
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

			if(play.isPressed()){
				screen = "World";
				skip = false;
				try{
					Scanner inFile = new Scanner(new File("worlds.txt")); //worlds.txt is the directory of all current .WORLD files.
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
			}

			if(settings.isPressed()) {
				screen = "Settings";
				musicButt = new ButtonRect(screenWidth/2 - menu[2].getWidth()/2,400, menu[2].getWidth(), menu[2].getHeight());
			}
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
					if (worldButts[c].getHighlighted() == true){
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
					String world = "world_" + i +".world";
					System.out.println("Clicked" + i);
					loadWorld(world);
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
			drawBlocks(p1);
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

	public void overwriteWorld(){ //overwrites the current .WORLD file, saves the world.
		byte[] allBytes = new byte[500*100*500];
		int c = 0;

		for (int zI = 0; zI < 500; zI++) {
			for (int yI = 0; yI < 100; yI++) {
				for (int xI = 0; xI < 500; xI++) {
					allBytes[c++] = (byte)world[zI][yI][xI].getType(); //creates a new byte for each block
				}//end for
			}//end for
		}//end for

		String fileName = "world_" +file + ".world"; //THIS IS WHERE FILE COMES THROUGH
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
					allBytes[c++] = (byte)world[zI][yI][xI].getType(); //each block is converted into a byte
				}//end for
			}//end for
		}//end for

		try {
			Scanner inFile = new Scanner(new File("worlds.txt")); //we need to use the current directory
			int n = 0;
			while (inFile.hasNextInt()){
				n = inFile.nextInt(); //finds last n -> the last file saved
			}
			String fileName = "world" + "_" + n + ".world"; //creates new .WORLD file
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
						world[z][y][x] = new Block(x, y, z, allBytes[c++], grass);
					}
				}
			}

			long end = System.currentTimeMillis();
			System.out.println("Loaded in " + (end - start) + " ms"); //kept the timers, just to see the difference with more blocks :)
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		for (int zI = 0; zI < 500; zI++){
			for (int yI = 0; yI < 100; yI++){
				for (int xI = 0; xI < 500; xI++){
					if (yI <= 5){
						world[zI][yI][xI] = new Block(xI, yI, zI, 1, grass);
					}
					else{
						world[zI][yI][xI] = null;
					}
				}
			}
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
		if (music){
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

	public void drawBlocks(Player p1){
		/*This project was the "Pen and Paper" project. The vast majority of the first 3 weeks was spent doing math to figure these 3 loops out. The next 10
           lines of code were the product of much struggle.
          */
		int x = p1.getX();
		int y = p1.getY();
		int z = p1.getZ();
		double angX = p1.getAngX();
		int maxZ = (int) Math.ceil(Math.max(z, Math.max(z + 2154*Math.cos(angX + 21.8*(Math.PI)/180), z + 2154*Math.cos(angX - 21.8*(Math.PI)/180))) / 100);
		int minZ = (int) Math.floor(Math.min(z, Math.min(z + 2154*Math.cos(angX + 21.8*(Math.PI)/180), z + 2154*Math.cos(angX - 21.8*(Math.PI)/180))) / 100);
		int maxX = (int) Math.ceil(Math.max(x, Math.max(x + 2154*Math.sin(angX + 21.8*(Math.PI)/180), x + 2154*Math.sin(angX - 21.8*(Math.PI)/180))) / 100);
		int minX = (int) Math.floor(Math.min(x, Math.min(x + 2154*Math.sin(angX + 21.8*(Math.PI)/180), x + 2154*Math.sin(angX - 21.8*(Math.PI)/180))) / 100);
		int maxY = (int) Math.ceil(y / 100 + 8);
		int minY = (int) Math.floor(y / 100 - 8);
		TreeMap<Double, ArrayList<Block>> blocksMap = new TreeMap<Double, ArrayList<Block>>();
		for (int pz = maxZ; pz > minZ; pz --) {
			for (int px = minX; px <= maxX; px ++) {
				for (int py = minY; py <= maxY; py ++) {
					if (pz >= 0 && pz < 500 && py >= 0 && py < 500 && px >= 0 && px < 500 && pz*100 - z != 0 && world[pz][py][px] != null){
						//drawBlock(px*100, py*100, pz*100, grass, p1);
						Double key = -world[pz][py][px].getDist(p1);
						if (blocksMap.get(key) == null){
							blocksMap.put(key, new ArrayList<Block>());
						}
						blocksMap.get(key).add(world[pz][py][px]);
					}
				}
			}
		}

		for (ArrayList<Block> blocks : blocksMap.values()){
			if (blocks != null){
				for (Block block : blocks){
					block.draw(p1, batch);
				}
			}
		}

	}

	@Override
	public void dispose () {
		batch.dispose();
		music.dispose();
	}//end dispose
}//end class test

class Block{
	int x, y, z, type;
	Texture[] images;
	public Block(int x, int y, int z, int type, Texture[] images){
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.images = images;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getZ(){
		return z;
	}
	public Texture[] getImages(){
		return images;
	}
	public Double getDist(Player p1){
		return Math.sqrt(Math.pow(p1.getX() - x*100, 2) + Math.pow(p1.getY() - y*100, 2) + Math.pow(p1.getZ() - z*100, 2));
	}
	public void draw(Player p1, SpriteBatch batch){
		int px = p1.getX();
		int py = p1.getY();
		int pz = p1.getZ();
		float[][] points = new float[][] {getPoint(x*100 - 50, y*100 - 50, z*100 - 50, p1), getPoint(x*100 - 50, y*100 + 50, z*100 - 50, p1), getPoint(x*100 + 50, y*100 + 50, z*100 - 50, p1), getPoint(x*100 + 50, y*100 - 50, z*100 - 50, p1),
				getPoint(x*100 - 50, y*100 - 50, z*100 + 50, p1), getPoint(x*100 - 50, y*100 + 50, z*100 + 50, p1), getPoint(x*100 + 50, y*100 + 50, z*100 + 50, p1), getPoint(x*100 + 50, y*100 - 50, z*100 + 50, p1)};
		//double rad = 100000 / (pz - z);
		//batch.draw(grass[1], (int) points[0], (int) points[1], (int) rad, (int)rad); //front

		if (z*100 + 50 < pz) {
			drawBack(points, batch);
		}
		else if (z*100 - 50 > pz) {
			drawFront(points, batch);
		}

		if (x*100 + 50 < px) { //right
			batch.draw(images[1], new float[]{points[3][0], points[3][1], Color.toFloatBits(255, 255, 255, 255), 0, 1,
					points[2][0], points[2][1], Color.toFloatBits(255, 255, 255, 255), 0, 0,
					points[6][0], points[6][1], Color.toFloatBits(255, 255, 255, 255), 1, 0,
					points[7][0], points[7][1], Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}
		else if (x*100 - 50 > px) { //left
			batch.draw(images[1], new float[]{points[4][0], points[4][1], Color.toFloatBits(255, 255, 255, 255), 0, 1,
					points[5][0], points[5][1], Color.toFloatBits(255, 255, 255, 255), 0, 0,
					points[1][0], points[1][1], Color.toFloatBits(255, 255, 255, 255), 1, 0,
					points[0][0], points[0][1], Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}

		if (y*100 + 50 < py) { // top
			batch.draw(images[0], new float[]{points[1][0], points[1][1], Color.toFloatBits(255, 255, 255, 255), 0, 1,
					points[5][0], points[5][1], Color.toFloatBits(255, 255, 255, 255), 0, 0,
					points[6][0], points[6][1], Color.toFloatBits(255, 255, 255, 255), 1, 0,
					points[2][0], points[2][1], Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}
		else if (y*100 - 50 >  py){ // bottom
			batch.draw(images[2], new float[]{points[4][0], points[4][1], Color.toFloatBits(255, 255, 255, 255), 0, 1,
					points[0][0], points[0][1], Color.toFloatBits(255, 255, 255, 255), 0, 0,
					points[3][0], points[3][1], Color.toFloatBits(255, 255, 255, 255), 1, 0,
					points[7][0], points[7][1], Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
		}
	}

	public void drawFront(float[][] points, SpriteBatch batch){
		batch.draw(images[1], new float[]{points[0][0], points[0][1], Color.toFloatBits(255, 255, 255, 255), 0, 1,
				points[1][0], points[1][1], Color.toFloatBits(255, 255, 255, 255), 0, 0,
				points[2][0], points[2][1], Color.toFloatBits(255, 255, 255, 255), 1, 0,
				points[3][0], points[3][1], Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
	}

	public void drawBack(float[][] points, SpriteBatch batch){
		batch.draw(images[1], new float[]{points[4][0], points[4][1], Color.toFloatBits(255, 255, 255, 255), 0, 1,
				points[5][0], points[5][1], Color.toFloatBits(255, 255, 255, 255), 0, 0,
				points[6][0], points[6][1], Color.toFloatBits(255, 255, 255, 255), 1, 0,
				points[7][0], points[7][1], Color.toFloatBits(255, 255, 255, 255), 1, 1}, 0, 20);
	}
	public float[] getPoint(int bx, int by, int bz, Player p1){
		int px = p1.getX();
		int py = p1.getY();
		int pz = p1.getZ();
		double a = Math.pow(bx - px, 2) + Math.pow(bz - pz, 2);
		double b = Math.tan(Math.atan((double)(bx - px) / (double)(bz - pz)) - p1.getAngX());
		double z2 = Math.sqrt(a / (Math.pow(b, 2) + 1));
		double x2 = b * z2;

		//double rad = 10000 / (pz - z);
		return new float[] {(float) (400 + 1000 * x2 / z2), (float) (400 + 1000 * (by - py) / z2)};
		//shapeRenderer.circle((float) (400 + 1000 * x2 / z2), (float) (400 + 1000 * (y - py) / z2), (float) rad);
	}

	public int getType() {return type;}
}

class Player{
	private int vx,vy,vz; //speed
	private int x, y, z; //position
	private long lastW; //sprinting variables
	private boolean sprint,w;
	private Sound[] dig; //sound effects
	private double angX;
	private Texture[] grass;
	public Player(int xPos, int yPos, int zPos, Texture[] grass){
		this.grass = grass;
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
		dig[0] = Gdx.audio.newSound(Gdx.files.internal("dig/cloth1.ogg")); //breaking
		dig[1] = Gdx.audio.newSound(Gdx.files.internal("dig/cloth2.ogg")); //placing
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
				vz = (int)(10*Math.cos(angX));
				vx = (int)(10*Math.sin(angX));
			}
			else {
				vz = (int)(20*Math.cos(angX));
				vx = (int)(20 * Math.sin(angX));
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S) && collisions[1] == 0){ //applying movement based on collisions and key
			vz = (int)(-10*Math.cos(angX));
			vx = (int)(-10*Math.sin(angX));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A) && collisions[2] == 0){
			vz = (int)(5*Math.cos(angX - Math.PI/2));
			vx = (int)(5*Math.sin(angX - Math.PI/2));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D) && collisions[3] == 0){
			vz = (int)(-5*Math.cos(angX - Math.PI/2));
			vx = (int)(-5*Math.sin(angX - Math.PI/2));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && onGround && collisions[4] == 0) {
			vy = 10 ;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			angX += 0.03;
			if (angX > 2*Math.PI){
				angX = 0;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			angX -= 0.03;
			if (angX < 0){
				angX = 2*Math.PI;
			}
		}

		x += vx; //adding velocity values
		y += vy;
		z += vz;
		vx = 0;
		vz = 0;
	}//end move

	public void click(Block[][][]world){ //this checks
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			int count = 1; //breaks blocks up to 4 away.
			int xI = Math.round(x/100) + 1;
			int yI = Math.round(y/100) + 1;
			int zI = Math.round(z/100) + 1;
			while (true){
				if (world[zI + count][yI][xI] != null){
					world[zI + count][yI][xI] = null;
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
			if (world[zI +1][yI][xI] == null){
				System.out.println("Placed!");
				dig[1].play();
				world[zI + 1][yI][xI] = new Block(xI, yI, zI + 1, 1, grass);
			}
		}//right click for placing
	}//end click

	public void applyGravity(){ //apply gravity is called if not onGround
		if (vy < 20){
			vy -= 1;
		}
	}//end applyGravity

	public boolean onGround(Block[][][] world){ //onGround is seperate from checkCollision because of movement methods.
		int xI = Math.round(x/100) + 1;
		int yI = Math.round(y/100) + 1;
		int zI = Math.round(z/100) + 1;
		if ((zI > 0 && zI <500 ) && (xI > 0 && xI < 500) && (yI > 0 && yI <500)) { //if there's a block underneath
			if (world[zI][yI - 1][xI] != null) {
				vy = 0;
				return true;
			}
		}
		return false;
	}//end onGround

	public int[] checkCollision(Block[][][]world){
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
			if (world[zI + 1][yI][xI] != null) { //collision checking in all directions, self-explanatory
				System.out.println("front");
				output[0] = 1; //front
			}
			if (world[zI - 1][yI][xI] != null){
				System.out.println("back");
				output[1] = 1; //back
			}
			if (world[zI][yI][xI - 1] != null){
				System.out.println("left");
				output[2] = 1; //left
			}
			if (world[zI][yI][xI + 1] != null){
				System.out.println("right");
				output[3] = 1; //right
			}
			if (world[zI][yI + 1][xI] != null){
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
	public double getAngX() {return angX; }
	public long getLastW(){return lastW;}
	public int getVX(){ return vx; }
	public int getVY(){ return vy; }
	public int getVZ(){ return vz; }
}//end Player Class

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