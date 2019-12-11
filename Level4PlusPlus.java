import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

/* Class Name: Level4PlusPlus
 * Version: 1.0
 * Author: Paula Yuan
 * Date: October 24, 2019
 * Description: my Duber Eatz Level 4++ solution which works only some of the time, but I'm busy and don't have the
 * time to figure it out :( Also I can't name the class something ending with ++ or + so that's why the file name and
 * the class name is like this. (Because they're supposed to match.)
 */

class Level4PlusPlus {
  
  // beginning of inner classes
  /**
   * PartialPath
   * This class represents the a partial path that changes as the map is solved. It can store the total amount of tips
   * received after all the deliveries are made and the path, which is the directions of the steps to reach all 
   * remaining delivery locations, in reverse. No parameters.
   */
  static class PartialPath {
    public int tips = 0;
    public ArrayList<Integer> path = new ArrayList<Integer>();
  }
  // end of inner classes
  
  //stores all cardinal directions as pairs representing horizontal or vertical movement
  private static int[][] DIRECTIONS = {
    {1, 0},
    {-1, 0},
    {0, 1},
    {0, -1}
  };
  
  // methods
  
  /**
   * readTestCase
   * This method accepts a String file name and reads the associated file. The method returns a 2D character array
   * on the success of the algorithm.
   * @param fileName, a String that holds data representing a file
   * @return a 2D character array if the file is valid, throws exception otherwise.
   */
  public static char[][] readTestCase(String fileName) throws Exception{
    File map = new File(fileName);
    Scanner read = new Scanner(map);
    int rows = read.nextInt();
    read.nextLine();                    
    int columns = read.nextInt();
    read.nextLine();
    char[][] mapArray = new char[rows][columns];
    for (int i = 0; i < rows; i++) {
      mapArray[i] = read.nextLine().trim().toCharArray();
    }
    return mapArray;
  }
  
  /**
   * createPPM
   * This method acepts a map as a parameter and creates a PPM image file based off of it. The method returns void.
   * @param map, a 2D character array that holds data representing a map.
   * @returns void, may throw exception
   */
  public static void createPPM(char[][] map) throws Exception{
    int imgColumns = map[0].length;
    int imgRows = map.length;
    PrintWriter writePPM = new PrintWriter(new File("solvedMap.ppm"));
    writePPM.println("P3");
    writePPM.print(imgColumns*20);
    writePPM.println(" " + imgRows*20);
    writePPM.println("255");
    
    for (int i = 0; i < map.length; i++) {
      int rowCount = 0;
      while (rowCount < 20) {
        for (int j = 0; j < map[i].length; j++) { 
          int columnCount = 0;
          while (columnCount < 20) {
            if (map[i][j] == '#') {
              writePPM.print("0 0 0 ");
            } else if (map[i][j] == 'x') { // red
              writePPM.print("255 0 0 ");
            } else if (map[i][j] == 'X') { // blue
              writePPM.print("0 0 255 ");
            } else if (map[i][j] == 'S') { // green
              writePPM.print("0 255 0 ");
            } else if (map[i][j] == ' ') { // white 
              writePPM.print("255 255 255 ");
            } else if (map[i][j] == 'M') {
              writePPM.print("255 255 0 ");
            }
            columnCount++;
          } 
        }
        rowCount++;
      }
    }
    writePPM.close();
  }
  
  /**
   * findPath
   * This method accepts a map, a backtracking map, a row, a column, an amount of deliveries, an amount of deliveries
   * made, an amount of steps and an amount of tips. The solution path that maximizes tips and reaches all delivery
   * locations is found recurisvely and a PartialPath object is returned. This path is built backwards.
   * @param map, a 2D array of characters with delivered locations marked with 'X'.
   * @param backtrackMap, a boolean array where locations we have visited since the last delivery are marked true.
   * @param row, the current row of the map being accessed
   * @param column, the current column of the map being accessed
   * @param deliveries, amount of total deliveries that need to be made
   * @param delivered, the amount of deliveries made thus far
   * @param steps, the total steps taken thus far
   * @param tips, total tips received from the deliveries made thus far
   * @return an instance of the PartialPath class
   */
  public static PartialPath findPath(char[][] map, boolean[][] backtrackMap, int row, int column, int deliveries, int delivered, int steps, int tips) {
    
    // make sure current location is valid
    if (row >= map.length || row < 0 || column < 0 || column >= map[0].length || map[row][column] == '#' || backtrackMap[row][column]) { 
      return null;
    }
    
    char tipChar = map[row][column];
    int tipNumber = Character.getNumericValue(tipChar);
    boolean justDelivered = tipNumber >= 0 && tipNumber <= 9;
    if (justDelivered) {
      delivered += 1;
      if (tipNumber-steps > 0) {
        tips += (tipNumber-steps)*10;
      } else {
        tips += tipNumber-steps;
      }
      map[row][column] = 'X';  // mark delivery location as delivered
      backtrackMap = new boolean[map.length][map[0].length];
      if (delivered == deliveries) {
        map[row][column] = tipChar;
        PartialPath path = new PartialPath();
        path.tips = tips;
        return path;
      }
    } else { 
      backtrackMap[row][column] = true;
    }
    
    PartialPath bestPath = null;
    if (map[row][column] == 'M') {
      // trying to use a microwave -- recurse with steps cut in half
      map[row][column] = 'm';
      for (int i = 0; i < 4; i++) {
        PartialPath path = findPath(map, backtrackMap, row + DIRECTIONS[i][0], column + DIRECTIONS[i][1], deliveries, 
                                    delivered, steps/2 + 1, tips);
        if (path == null) continue;
        path.path.add(i);  // Add the direction to the end of the path. Path is built in reverse and later read backwards.
        if (bestPath == null || path.tips > bestPath.tips) {
          bestPath = path;
        }
      }
      map[row][column] = 'M';
    }
    // recurse in situation of no microwave or chose not to use microwave
    for (int i = 0; i < 4; i++) {
      PartialPath path = findPath(map, backtrackMap, row + DIRECTIONS[i][0], column + DIRECTIONS[i][1], deliveries, 
                                  delivered, steps + 1, tips);
      if (path == null) continue;
      path.path.add(i); 
      if (bestPath == null || path.tips > bestPath.tips) {
        bestPath = path;
      }
    }
    
    // reset the state for our caller
    
    if (justDelivered) {
      map[row][column] = tipChar;
    } else {
      backtrackMap[row][column] = false;
    }
    
    return bestPath;
  }
  // end of method definitions
  
  public static void main(String[] args) throws Exception {
    Scanner input = new Scanner(System.in); 
    System.out.print("Enter the test case name: ");
    String mapName = input.nextLine();
    char[][] mapArray = readTestCase(mapName);
    boolean[][] backtrackMap = new boolean[mapArray.length][mapArray[0].length];
    
    int startRow = 0;
    int startColumn = 0;
    int deliveries = 0;
    
    // find starting position and count number of deliveries to be made
    for (int i = 0; i < mapArray.length; i++) {
      for (int j = 0; j < mapArray[i].length; j++) {
        int locationNum = Character.getNumericValue(mapArray[i][j]);
        if (mapArray[i][j] == 'S') {
          startRow = i;
          startColumn = j;
        } else if (locationNum >= 0 && locationNum <= 9) {
          deliveries += 1;
        }
      }
    }
    
    int row = startRow;
    int column = startColumn;
    PartialPath solution = new PartialPath();
    solution = findPath(mapArray, backtrackMap, startRow, startColumn, deliveries, 0, 0, 0);
    int count = 0;
    
    // we mark the final map with the final solution according to the ArrayList path returned
    for (int i = solution.path.size()-1; i >= 0; i--) {
      int[] direction = DIRECTIONS[solution.path.get(i)]; // assigns current direction
      row += direction[0];
      column += direction[1];
      int locationNum = Character.getNumericValue(mapArray[row][column]);
      if (locationNum >= 0 && locationNum <= 9) {
        mapArray[row][column] = 'X';
      } else if (mapArray[row][column] != 'S' && mapArray[row][column] != 'X' && mapArray[row][column] != 'M') {
        mapArray[row][column] = 'x';
      }
    }
    
    // output
    System.out.println("Solved map with path:");
    for (int i = 0 ; i < mapArray.length; i++) {
      System.out.println(mapArray[i]);
    }
    System.out.println("Number of steps it took: " + solution.path.size());
    System.out.println("Max number of tips: " + solution.tips);
    createPPM(mapArray);
  }
}



