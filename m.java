


import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;


import java.awt.Color;
import javalib.worldimages.*;


//this is the world class of the game
class MinesweeperWorld extends World {
  int cellSize = 20;

  ArrayList<ArrayList<Cell>> board;
  int width;
  int height;
  int mines;
  Random rand;
  boolean lost;

  // use for testing 
  MinesweeperWorld(ArrayList<ArrayList<Cell>> board, int width, int height, int mines) {
    this.board = board;
    this.width = width;
    this.height = height;
    this.mines = mines;
    this.rand = new Random();
    this.lost = false;
  }


  // use for testing 
  MinesweeperWorld(int width, int height, int mines, int seed) {

    this.width = width;
    this.height = height;
    this.mines = mines;
    this.rand = new Random(seed);
    this.board = generateBoard();
    this.lost = false;

  }


  // making the games 
  //number of rows, the number of columns and the number of mines
  MinesweeperWorld(int width, int height, int mines) { 
    this.width = width;
    this.height = height;
    this.mines = mines;
    this.rand = new Random();
    this.board = generateBoard();
    this.lost = false;

  }







  //generate random positions for the mines
  ArrayList<Corrdinates> generateMines() {
    int mineRemaining = this.mines; 


    ArrayList<Corrdinates> minePosition = new ArrayList<Corrdinates>();
    while (mineRemaining > 0) {
      //add on the x and y coordinates
      Corrdinates coordinates = new Corrdinates(this.rand.nextInt(this.width), 
          this.rand.nextInt(this.height));

      if (!minePosition.contains(coordinates)) {
        minePosition.add(coordinates);

        mineRemaining-- ;
      }

    }

    //return list of mines and their positions
    return minePosition;

  }



  //generate random board
  ArrayList<ArrayList<Cell>> generateBoard() {

    ArrayList<ArrayList<Cell>> newBoard = new ArrayList<ArrayList<Cell>>();
    ArrayList<Corrdinates> loc = generateMines(); // has locations of mines 


    for (int i = 0; i < this.height; i++) {
      ArrayList<Cell> row = new ArrayList<Cell>(); // to add coridnates 
      for (int j = 0; j < this.width; j++) {



        Cell cell = new Cell(new ArrayList<Cell>(), false, 0); // Create cell without mine

        row.add(cell);

      }


      newBoard.add(row);
    }



    for (Corrdinates minePos : loc) {
      int x = minePos.x;
      int y = minePos.y;
      newBoard.get(y).get(x).makeMine(); // Set cell at mine position as mine
    }


    // Connect neighboring cells
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        Cell currentCell = newBoard.get(i).get(j);

        if (i > 0) {
          currentCell.addNeighbor(newBoard.get(i - 1).get(j));
        }
        if (i < this.height - 1) {
          currentCell.addNeighbor(newBoard.get(i + 1).get(j));
        }
        if (j > 0) {
          currentCell.addNeighbor(newBoard.get(i).get(j - 1));
        }
        if (j < this.width - 1) {
          currentCell.addNeighbor(newBoard.get(i).get(j + 1));
        }
        //diagonals
        if (i > 0 && j > 0) {
          currentCell.addNeighbor(newBoard.get(i - 1).get(j - 1));
        }

        if (i < this.height - 1 && j < this.width - 1) {
          currentCell.addNeighbor(newBoard.get(i + 1).get(j + 1));
        }

        if (i > 0 && j < this.width - 1) {
          currentCell.addNeighbor(newBoard.get(i - 1).get(j + 1));
        }

        if (i < this.height - 1 && j > 0) {
          currentCell.addNeighbor(newBoard.get(i + 1).get(j - 1));
        }
        currentCell.numTotalMines();
      }
    }

    return newBoard;
  }




  // creates the image of the game
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(this.width * cellSize, this.height * cellSize);

    // Iterate over each cell in the board and draw it
    for (int i = 0; i < this.height; i++) {
      if (i < board.size()) {
        ArrayList<Cell> row = board.get(i);
        for (int j = 0; j < this.width; j++) {
          if (j < row.size()) {



            Cell cell = this.board.get(i).get(j);

            WorldImage cellImage = cell.drawCell(); // Draw the cell




            // Calculate the position to draw the cell
            int x = i * cellSize + cellSize / 2;
            int y = j * cellSize + cellSize / 2;


            // Place the cell image on the scene
            scene.placeImageXY(cellImage, x, y);


          }
        }
      }
    }



    return scene;
  }



  ///  right button handles to reieve the flag and/ unflaged
  /// leftButton = flooding the cells 
  public void onMouseClicked(Posn pos, String buttonName) {
    int col = Math.floorDiv(pos.x, 20);
    int row = Math.floorDiv(pos.y, 20);

    Cell clickedCell = board.get(col).get(row);


    if (buttonName.equals("LeftButton")) {
      clickedCell.flood();
    }


    // Handle right button click
    if (buttonName.equals("RightButton")) {
      if (clickedCell.flagged) {
        clickedCell.flagged = false;
      } 
      else {
        clickedCell.flagged = true;
      }
    }


  }

  //returns the final scene of game
  public WorldScene lastScene(String msg) {
    WorldScene finalScene = makeScene(); 

    TextImage gameOverText = new TextImage(msg, 20, FontStyle.BOLD, Color.RED);
    finalScene.placeImageXY(gameOverText, width * cellSize / 2, height * cellSize / 2);

    return finalScene;
  }


  //keeps checking through the board to see whether the user won
  public boolean userWon() {
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        Cell currCen = board.get(i).get(j); 


        if (currCen.mine && !currCen.flagged) {
          return false; 
        }
        if (!currCen.mine && currCen.flagged) {
          return false;
        }
      }
    }

    return true; 
  }

  //produces end of the game
  public void onTick() {
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        Cell currCen = board.get(i).get(j); 
        if (currCen.mine && !currCen.hidden) {

          currCen.hidden = false; 
          this.lost = true;
        }
      }
    }
    if (this.lost) {
      this.endOfWorld("GAME OVER, PRESS r to play again");
    }

    if (this.userWon()) {
      this.endOfWorld("YOU WON, PRESS r to play again");
    }
  }


  //resets game after winning or losing
  public void resetGame() {
    // Reset all necessary fields to their initial values
    this.board = generateBoard();
    this.lost = false;

    this.makeScene();
  }

  //key for restarting the game
  public void onKeyEvent(String key) {

    if (key.equals("r")) {
      this.resetGame();
    }

  }


}  





// cell on the grid 
class Cell {
  ArrayList<Cell> neighbors;
  boolean mine; //is the cell a mine
  int numMines;// does any have any mines around me 
  boolean hidden; //is the cell hidden
  boolean flagged; //is the cell flagged


  int cellSize = 20;


  // orginal one with all the fields 
  Cell(ArrayList<Cell> neighbors, boolean mine, int numMines, boolean hidden, boolean flagged) {
    this.neighbors = neighbors;
    this.mine = mine;
    this.numMines = numMines;
    this.hidden = hidden; // it is hidden
    this.flagged = flagged; 
  }


  // to make a cell with only its neightbors, mine, and to neighboring mines 
  // so and the cless are hidden 
  Cell(ArrayList<Cell> neighbors, boolean mine, int numMines) {
    this.neighbors = neighbors;
    this.mine = mine;
    this.numMines = numMines;
    this.hidden = true; // it is hidden
  }


  //adds nightbors to the current cell
  void addNeighbor(Cell nb) {
    this.neighbors.add(nb);


  }

  //change a cell to a cell with a mine 
  void makeMine() {
    this.mine = true;
  }


  // check if this cell is a mine
  boolean isMine() {
    return mine;
  }



  //keeps up with total mine being added 
  void numTotalMines() {
    int totalMines = 0;

    if (!mine) {
      for (Cell neighbor : neighbors) {
        if (neighbor.mine) {
          totalMines++;
        }
      }

    }

    this.numMines = totalMines;
  }




  // to draw the overlaying image the ones that have one don't 
  public WorldImage drawCell() {
    if (this.flagged) {
      return new OverlayImage(
          new EquilateralTriangleImage(10,  OutlineMode.SOLID, Color.RED),
          new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, Color.LIGHT_GRAY));
    } else {
      if (this.hidden) {
        return new OverlayImage(
            new RectangleImage(cellSize, cellSize, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, Color.LIGHT_GRAY)
            );
      } else if (this.mine) {
        return new OverlayImage(
            new StarImage(10, 8, 2, OutlineMode.SOLID, Color.BLACK),
            new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, Color.RED)
            );
      } else {
        // Draw a revealed cell (for now, a plain white rectangle)
        return new OverlayImage(this.drawNeighborMine(), 
            (new OverlayImage(new RectangleImage(cellSize, cellSize, 
                OutlineMode.OUTLINE, Color.BLACK),  // the neightboring cells 
                new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, Color.WHITE))));
      }
    }
  }







  // draw the neighbor mine once it has been flood
  public WorldImage drawNighbor() {
    return new OverlayImage(this.drawNeighborMine(), 
        (new OverlayImage(new RectangleImage(cellSize, cellSize, 
            OutlineMode.OUTLINE, Color.BLACK),  // the neightboring cells
            new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, Color.WHITE))));
  }




  //draws onto the cell how many mines neighbor it 
  public WorldImage drawNeighborMine() {
    //int totalMines = this.numMines; // Calculate total number of mines
    if (this.numMines == 8 ) {
      return new TextImage("8", 10, Color.YELLOW);

    }
    if (this.numMines == 7) {
      return new TextImage("7", 10, Color.ORANGE);
    }

    if (this.numMines == 6) {
      return new TextImage("6", 10, Color.MAGENTA); 
    }

    else if (this.numMines == 5) {
      return new TextImage("5", 10, Color.RED); 
    }

    else if (this.numMines == 4) {
      return new TextImage("4", 10, Color.CYAN); 
    }

    else if (this.numMines == 3) {
      return new TextImage("3", 10, Color.BLACK); 
    }

    else if (this.numMines == 2) {

      return new TextImage("2", 10, Color.GREEN); // Draw "2" with green color

    } 

    else if (this.numMines == 1) {
      return new TextImage("1", 10, Color.RED); // Draw "1" with red color
    } 

    else {
      return new EmptyImage(); // Return an empty image if no neighboring mines
    }

  }



  //initializes the hidden boolean
  public void intiHidden() {
    hidden = false; 
  }


  //when clicking on a cell that's not a mine, reveals the cells around it also not mines
  public void flood() {

    if (!hidden || flagged) {
      return;
    }

    // Revealing the current cell
    hidden = false;

    // Recursively reveal neighboring cells
    for (Cell neighbor : neighbors) {
      // Recursively flood neighbors if they are not mines and have no neighboring mines
      if (!neighbor.mine && neighbor.numMines == 0 && neighbor.hidden) {
        neighbor.flood();
      }

      // Reveal the neighboring cell if it's not a mine
      if (!neighbor.mine && neighbor.hidden) {
        neighbor.hidden = false;
        // If the neighboring cell has neighboring mines, reveal its number
        if (neighbor.numMines > 0) {
          neighbor.hidden = false;
        }
      }
    }
  }

}





// position on the grid
class Corrdinates {
  int x;
  int y;

  Corrdinates(int x, int y) {
    this.x = x;
    this.y = y;
  }
}


// example os what is used to make a game 
class MinesweeperExamples {

  // for row1 
  Cell oneCellM;
  Cell twoCell1M; 
  Cell threeCell1M;
  Cell fourCell2M;

  //for row2  
  Cell first; 
  Cell second;
  Cell third; 
  Cell fourth; 

  // for row3 
  Cell mineFirst; 
  Cell mineSecond; 
  Cell mineThird; 
  Cell mineFourth; 

  //for row 4
  Cell fifth;
  Cell sixth;
  Cell seventh;
  Cell eighth;


  // test for make mine 
  Cell nine;
  Cell ten;
  Cell eleven;


  //Cells for row 5
  Cell mineFifth;
  Cell mineSixth;
  Cell mineSeventh;
  Cell mineEighth;

  // test celling but not adding them to rows 
  Cell mineNineth;
  Cell mineTenth;
  Cell mineEleventh;
  Cell mineTwentieth;

  // cells for game2Row1
  Cell apple; 
  Cell mango; 
  Cell grape; 
  Cell blueBerry;

  //cells for game2Row2
  Cell green; 
  Cell blue; 
  Cell purple; 
  Cell crayon;

  // for test
  Cell greenTest; 
  Cell blueTest; 
  Cell purpleTest; 
  Cell crayonTest;

  // Colors to help for test 
  TextImage one; 
  TextImage two; 
  TextImage three; 
  TextImage four; 
  TextImage five; 
  TextImage six; 
  TextImage seven;
  TextImage eight;
  OverlayImage mineImage;

  WorldImage rect;





  MinesweeperWorld game1; // 3 cells on 4 by 4 grid with 2 mines 
  MinesweeperWorld game2;


  ArrayList<Cell> row1;
  ArrayList<Cell> row2;
  ArrayList<Cell> row3;
  ArrayList<Cell> row4;
  ArrayList<Cell> row5;

  ArrayList<Cell> test;

  ArrayList<Cell> game2Row1;
  ArrayList<Cell> game2Row2;



  ArrayList<ArrayList<Cell>> board1;
  ArrayList<ArrayList<Cell>> board2;

  // intiales data so it can be mutated 
  void initalData() {

    // one single cell with a one mine
    this.oneCellM = new Cell(new ArrayList<Cell>(), true, 1, false, false); // not hidden or

    // two cells with only one mine
    this.twoCell1M = new Cell(new ArrayList<Cell>(Arrays.asList(oneCellM)), false, 1); 

    // three cells with 1 mines on its neightbor // its neighht bors are OneCell, TweCell
    this.threeCell1M = new Cell(new ArrayList<Cell>(Arrays.asList(twoCell1M)), true, 1);

    //three cells with 1 mines on its neightbor // its neighht bors are OneCell, TweCell, ThreeCell
    this.fourCell2M = new Cell(new ArrayList<Cell>(Arrays.asList(threeCell1M)), true, 2);




    this.first = new Cell(new ArrayList<Cell>(), false, 2);
    this.second = new Cell(new ArrayList<Cell>(Arrays.asList(first)), false, 1);
    this.third = new Cell(new ArrayList<Cell>(Arrays.asList(second)), false, 2);
    this.fourth = new Cell(new ArrayList<Cell>(Arrays.asList(third)), false, 2);


    this.mineFirst = new Cell(new ArrayList<Cell>(), true, 1);
    this.mineSecond = new Cell(new ArrayList<Cell>(Arrays.asList(mineFirst)), true, 2);
    this.mineThird = new Cell(new ArrayList<Cell>(Arrays.asList(mineSecond)), true, 3);
    this.mineFourth = new Cell(new ArrayList<Cell>(Arrays.asList(mineThird)), true, 4);
    this.mineEighth = new Cell(new ArrayList<Cell>(Arrays.asList(mineFourth)), true, 4);
    this.mineEighth = new Cell(new ArrayList<Cell>(Arrays.asList(mineFourth)), true, 4);


    this.fifth = new Cell(new ArrayList<Cell>(), false, 2);
    this.sixth = new Cell(new ArrayList<Cell>(Arrays.asList(fifth)), false, 2);
    this.seventh = new Cell(new ArrayList<Cell>(Arrays.asList(sixth)), false, 2);
    this.eighth = new Cell(new ArrayList<Cell>(Arrays.asList(seventh)), false, 2);

    // tetsing only 
    this.nine = new Cell(new ArrayList<Cell>(Arrays.asList()), false, 0); 
    this.ten = new Cell(new ArrayList<Cell>(Arrays.asList(nine)), false, 0); 
    this.eleven = new Cell(new ArrayList<Cell>(Arrays.asList(ten)), false, 0); 

    this.mineFifth = new Cell(new ArrayList<Cell>(), true, 1);
    this.mineSixth = new Cell(new ArrayList<Cell>(Arrays.asList(mineFifth)), true, 2);
    this.mineSeventh = new Cell(new ArrayList<Cell>(Arrays.asList(mineSixth)), true, 3);
    this.mineEighth = new Cell(new ArrayList<Cell>(Arrays.asList(mineSeventh)), true, 4);


    // testing only 
    this.mineNineth = new Cell(new ArrayList<Cell>(Arrays.asList(mineEighth)), true, 5);
    this.mineTenth = new Cell(new ArrayList<Cell>(Arrays.asList(mineNineth)), true, 6);
    this.mineEleventh = new Cell(new ArrayList<Cell>(Arrays.asList(mineTenth)), true, 7);
    this.mineTwentieth  = new Cell(new ArrayList<Cell>(Arrays.asList(mineEleventh )), true, 8);


    this.apple = new Cell(new ArrayList<Cell>(), true, 1);
    this.mango =  new Cell(new ArrayList<Cell>(Arrays.asList(apple)), true, 1);
    this.grape = new Cell(new ArrayList<Cell>(Arrays.asList(mango)), false, 2);
    this.blueBerry = new Cell(new ArrayList<Cell>(Arrays.asList(grape)), true, 1);

    //cells for game2Row2
    this.green = new Cell(new ArrayList<Cell>(), true, 1);
    this.blue = new Cell(new ArrayList<Cell>(Arrays.asList(green)), false, 1); 
    this.purple = new Cell(new ArrayList<Cell>(Arrays.asList(blue)), false, 1);
    this.crayon = new Cell(new ArrayList<Cell>(Arrays.asList(purple)), true, 1);


    // for tests 
    this.greenTest = new Cell(new ArrayList<Cell>(), true, 1);
    this.blueTest = new Cell(new ArrayList<Cell>(Arrays.asList(green)), false, 1); 
    this.purpleTest = new Cell(new ArrayList<Cell>(Arrays.asList(blue)), false, 1);
    this.crayonTest = new Cell(new ArrayList<Cell>(Arrays.asList(purple)), true, 1);




    this.row1 = new ArrayList<Cell>(); 
    row1.add(oneCellM);
    row1.add(twoCell1M);
    row1.add(threeCell1M);
    row1.add(fourCell2M);

    this.row2 = new ArrayList<Cell>();
    row2.add(first);
    row2.add(second);
    row2.add(third);
    row2.add(fourth);

    this.row3 = new ArrayList<Cell>(); 
    row3.add(mineFirst);
    row3.add(mineSecond);
    row3.add(mineThird);
    row3.add(mineFourth);

    this.row4 = new ArrayList<Cell>();
    row4.add(fifth);
    row4.add(sixth);
    row4.add(seventh);
    row4.add(eighth);

    this.row5 = new ArrayList<Cell>(); 
    row5.add(mineFifth);
    row5.add(mineSixth);
    row5.add(mineSeventh);
    row5.add(mineEighth);


    this.board1 = new ArrayList<ArrayList<Cell>>();
    board1.add(row1);
    board1.add(row2);
    board1.add(row3);
    board1.add(row4);
    board1.add(row5);


    this.game2Row1 = new ArrayList<Cell>();
    game2Row1.add(apple);
    game2Row1.add(mango);
    game2Row1.add(grape);
    game2Row1.add(blueBerry);

    this.game2Row2 = new ArrayList<Cell>();
    game2Row2.add(green);
    game2Row2.add(blue);
    game2Row2.add(purple);
    game2Row2.add(crayon);


    this.test = new ArrayList<Cell>();
    game2Row2.add(greenTest);
    game2Row2.add(blueTest);
    game2Row2.add(purpleTest);
    game2Row2.add(crayonTest);

    this.board2 = new ArrayList<ArrayList<Cell>>();
    board2.add(game2Row1);
    board2.add(game2Row2);


    this.game1 = new MinesweeperWorld(4, 3, 4);
    this.game2 =   new MinesweeperWorld(10, 10, 2);


    // colors 
    this.one = new TextImage("1", 10, Color.RED);
    this.two = new TextImage("2", 10, Color.GREEN);
    this.three = new TextImage("3", 10, Color.BLACK);
    this.four = new TextImage("4", 10, Color.CYAN);
    this.five = new TextImage("5", 10, Color.RED);
    this.six = new TextImage("6", 10, Color.MAGENTA);
    this.seven = new TextImage("7", 10, Color.ORANGE); 
    this.eight = new TextImage("8", 10, Color.YELLOW);

    this.rect = (new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.LIGHT_GRAY))); 

    this.mineImage = new OverlayImage(new StarImage(10, 8, 2, OutlineMode.SOLID, Color.BLACK), 
        (new RectangleImage(20, 20, OutlineMode.SOLID, Color.RED)));
  }



  // test for generateboard 
  void testGenerateBoard(Tester t) {
    initalData();
    // For example, 10x10 grid with 10 mines
    MinesweeperWorld game = new MinesweeperWorld(10, 10, 10); 

    MinesweeperWorld gameTest = new MinesweeperWorld(4, 4, 3, 1);

    // Generate the board
    ArrayList<ArrayList<Cell>> board = game.generateBoard();

    ArrayList<ArrayList<Cell>> testBoard = gameTest.generateBoard();

    t.checkExpect(gameTest.mines, 3);

    // Check if the board has been generated correctly
    t.checkExpect(board.size(), 10); // Check if the board has 10 rows
    for (ArrayList<Cell> row : board) {
      t.checkExpect(row.size(), 10); // Check if each row has 10 cells
    }

    t.checkExpect(game.width, 10);
    t.checkExpect(game.height, 10);


  }


  //testing for gererate mines 
  void testGenerateMines(Tester t) {
    initalData();
    MinesweeperWorld game = new MinesweeperWorld(new ArrayList<>(), 10, 10, 14); 
    MinesweeperWorld testGame = new MinesweeperWorld( 2, 2, 1, 1); // test for a small grid

    MinesweeperWorld largeGame = new MinesweeperWorld( 40, 40, 3, 1); // large one
    Corrdinates pos1 = new Corrdinates(34,6);
    Corrdinates pos2 = new Corrdinates(18,28);
    Corrdinates pos3 = new Corrdinates(9,33);
    Corrdinates pos4 = new Corrdinates(0,0);

    t.checkExpect(testGame.generateMines(), new ArrayList<Corrdinates>(Arrays.asList(pos4)));

    t.checkExpect(largeGame.generateMines(), 
        new ArrayList<Corrdinates>(Arrays.asList(pos1, pos2, pos3)));

    ArrayList<Corrdinates> mines = game.generateMines();

    t.checkExpect(mines.size(), 14);
    // Check if all mine positions are unique
    for (int i = 0; i < mines.size(); i++) {
      for (int j = i + 1; j < mines.size(); j++) {
        t.checkExpect(mines.get(i).equals(mines.get(j)), false); 
      }
    }

  }


  // test for drawiing cell
  void testDrawCell(Tester t) {
    initalData();
    Cell mineFifth = new Cell(new ArrayList<Cell>(), true, 1);



    t.checkExpect(blue.drawCell(), rect);
    t.checkExpect(purple.drawCell(), rect); 
    t.checkExpect(crayon.drawCell(),  rect); 

    t.checkExpect(mineFifth.drawCell(), rect); 
  }


  void testDrawNeighborMine(Tester t) {
    initalData();


    t.checkExpect(this.oneCellM.drawNeighborMine(), one); // a test for one mine
    t.checkExpect(this.mineSeventh.drawNeighborMine(), three); // A test for 3 mines
    t.checkExpect(this.mineEighth.drawNeighborMine(), four);

    t.checkExpect(this.mineNineth.drawNeighborMine(), five);
    t.checkExpect(this.mineTenth.drawNeighborMine(), six);
    t.checkExpect(this.mineEleventh.drawNeighborMine(), seven);
    t.checkExpect(this.mineTwentieth.drawNeighborMine(), eight);
  }



  void testmakeScene(Tester t) {
    initalData();
    MinesweeperWorld testWorld = new MinesweeperWorld(1, 1, 1, 0);
    WorldScene expectedScene = new WorldScene(20, 20); 

    expectedScene.placeImageXY(
        new OverlayImage(
            rect,
            new RectangleImage(20, 20, OutlineMode.SOLID, Color.RED)), 10, 10);

    t.checkExpect(testWorld.makeScene(), expectedScene);


  }

  void makeMine(Tester t ) {
    initalData();


    // expected 
    Cell expectNine = new Cell(new ArrayList<Cell>(Arrays.asList()), true, 0); 
    Cell expectTen = new Cell(new ArrayList<Cell>(Arrays.asList(nine)), true, 1); 
    Cell expectEleven = new Cell(new ArrayList<Cell>(Arrays.asList(ten)), true, 2); 


    nine.makeMine();
    ten.makeMine();
    eleven.makeMine();

    t.checkExpect(nine, expectNine); 
    t.checkExpect(ten, expectTen); 
    t.checkExpect(eleven, expectEleven); 
  }





  void testaddNeighbor(Tester t ) {
    initalData();

    ArrayList<Cell> expectList = new ArrayList<Cell>(Arrays.asList(ten, mineNineth)); 

    // check its neighrobs
    t.checkExpect(eleven.neighbors, new ArrayList<Cell>(Arrays.asList(ten))); 

    // adding new one 
    eleven.addNeighbor(mineNineth);

    // makign sure its now in the list 
    t.checkExpect(eleven.neighbors, expectList); 


    // another test when adding a nieghtboor to one cell 
    // would not allow use to do empty case threw a a null expection
    oneCellM.addNeighbor(twoCell1M);

    ArrayList<Cell> neighbor = new ArrayList<Cell>();

    neighbor.add(twoCell1M);

    t.checkExpect(this.oneCellM.neighbors, neighbor);



  }



  // test for num mines 
  void testNumMine(Tester t) {
    initalData();

    t.checkExpect(this.threeCell1M.numMines, 1); 
    t.checkExpect(this.twoCell1M.numMines, 1);
    t.checkExpect(this.fourCell2M.numMines, 2);


  }

  // test for total nume mines 
  void testNumTotalMines(Tester t) {
    Cell yesMine = new Cell(new ArrayList<Cell>(Arrays.asList()), true, 0);

    Cell noMine = new Cell(new ArrayList<Cell>(Arrays.asList()), false, 0);
    Cell one = new Cell(new ArrayList<Cell>(Arrays.asList(yesMine)), false, 0);
    Cell makeOne = new Cell(new ArrayList<Cell>(Arrays.asList()), false, 0);
    Cell zero = new Cell(new ArrayList<Cell>(Arrays.asList()), false, 0);

    //this  is the case of adding a neighoor that has a nieghtboring mine
    zero.addNeighbor(one); 
    zero.addNeighbor(noMine);



    // Set up neighbors for the one cell
    one.addNeighbor(noMine);
    makeOne.makeMine(); // make this a mine

    // Set up neighbors for the cell 
    noMine.addNeighbor(zero);
    noMine.makeMine(); // make this also a mine


    // Call the method on each cell
    zero.numTotalMines();
    one.numTotalMines();
    noMine.numTotalMines();

    // Assertion: Verify the updated numMines field
    t.checkExpect(zero.numMines, 1);
    t.checkExpect(one.numMines, 2);
    t.checkExpect(noMine.numMines, 0);
  }


  void testonMouseClicked(Tester t) {
    initalData();

    MinesweeperWorld testWorld = new MinesweeperWorld(4, 4, 3, 1);

    testWorld.generateBoard();
    Cell noMine = new Cell(new ArrayList<Cell>(Arrays.asList()), false, 0, false, false);

    noMine = testWorld.board.get(0).get(3);  

    t.checkExpect(noMine.flagged, false); 


    testWorld.onMouseClicked(new Posn(15, 65), "RightButton"); 

    t.checkExpect(testWorld.board.get(0).get(3).flagged, true); 



    testWorld.onMouseClicked(new Posn(15, 65), "RightButton"); 
    t.checkExpect(testWorld.board.get(0).get(3).flagged, false); 




    // Choose a cell that is not a mine
    Cell nonMineCell = game1.board.get(0).get(0); // Assuming the top-left cell is not a mine

    // Verify that the cell is initially hidden
    t.checkExpect(nonMineCell.hidden, true);

    // Simulate left mouse button click on the cell
    testWorld.onMouseClicked(new Posn(1, 1), "LeftButton");

    // Verify that the cell is no longer hidden (i.e., it's been "uncovered")
    t.checkExpect(testWorld.board.get(0).get(0).hidden, false);



    // Now going to chnage the cell back to hidden
    testWorld.board.get(0).get(0).hidden = true; 
    t.checkExpect(testWorld.board.get(0).get(0).hidden, true);


  }


  void testLastScene(Tester t) {
    // Create an instance of MinesweeperWorld with some parameters
    MinesweeperWorld world = new MinesweeperWorld(10, 10, 10);

    // Create a message to display in the final scene
    String message = "Game Over!";

    // Call the lastScene method to generate the final scene
    WorldScene scene = world.lastScene(message);

    // Check if the final scene has been generated
    t.checkExpect(scene, scene);

    
  }



  void testFlood(Tester t) {

    MinesweeperWorld testWorld = new MinesweeperWorld(4, 4, 3, 1);

    testWorld.generateBoard();
    Cell noMine = new Cell(new ArrayList<Cell>(Arrays.asList()), false, 0, false, false);

    noMine = testWorld.board.get(0).get(3);  

    t.checkExpect(noMine.hidden, true); 
    noMine.flood();

    t.checkExpect(noMine.hidden, false); 

    //
    MinesweeperWorld fakeWorld = new MinesweeperWorld(3, 2, 0, 1);

    fakeWorld.generateBoard();

    Cell first = fakeWorld.board.get(0).get(0);

    Cell second = fakeWorld.board.get(0).get(1);

    first.flood();

    t.checkExpect(second.hidden, false);


  }


  void testUserWon(Tester t) {
    MinesweeperWorld testWorld = new MinesweeperWorld(3, 3, 1, 0);
    testWorld.generateBoard();

    // Make sure all cells are initially hidden and not flagged
    for (ArrayList<Cell> row : testWorld.board) {
      for (Cell cell : row) {
        cell.hidden = false;
        cell.flagged = false;
        cell.mine = false;
      }
    }


    t.checkExpect(testWorld.userWon(), true);
    
    MinesweeperWorld fakeWorld = new MinesweeperWorld(5, 5, 1, 0);
    fakeWorld.generateBoard();

    
    for (ArrayList<Cell> row : fakeWorld.board) {
      for (Cell cell : row) {
        cell.hidden = true;
        cell.flagged = false;
        cell.mine = true;
      }
    }
    
    t.checkExpect(fakeWorld.userWon(), false);
    
  }



  void testOnKeyEvent(Tester t) {

    MinesweeperWorld testWorld = new MinesweeperWorld(5, 5, 5); 

    testWorld.board.get(0).get(0).hidden = false; 

    testWorld.onKeyEvent("r");
    t.checkExpect(testWorld.board.get(0).get(0).hidden, true); 


    // tets for differnt key pressed
    testWorld.board.get(0).get(1).hidden = false; 
    // Simulate key events to flag a cell
    testWorld.onKeyEvent("f");

    t.checkExpect(testWorld.board.get(0).get(1).hidden, false); 


  }


  void testBigBang(Tester t) {

    initalData();
    MinesweeperWorld world = new MinesweeperWorld(30, 30, 100);
    int worldWidth = 600;
    int worldHeight = 600;
    double tickRate = .01;


    world.bigBang(worldWidth, worldHeight, tickRate);
  }




}
