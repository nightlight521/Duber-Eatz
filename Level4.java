import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

/* Class Name: Level4
 * Version: 1.0
 * Author: Paula Yuan
 * Date: October 24, 2019
 * Description: Level 1 to 4 solution for Duber Eatz (does everything required from level 1 to 4)
 */

class Level4 {
  
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
    read.close();
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
   * made The solution path that minimizes steps taken is found recurisvely and an ArrayList is returned. 
   * This path is built backwards.
   * @param map, a 2D array of characters with delivered locations marked with 'X'.
   * @param backtrackMap, a boolean array where locations we have visited since the last delivery are marked true.
   * @param row, the current row of the map being accessed
   * @param column, the current column of the map being accessed
   * @param deliveries, amount of total deliveries that need to be made
   * @param delivered, the amount of deliveries made thus far
   * @return an ArrayList of integers representing the minimum path
   */
  public static ArrayList<Integer> findPath(char[][] map, boolean[][] backtrackMap, int row, int column, int deliveries, int delivered) {
    
    // make sure current location is valid
    if (backtrackMap[row][column] || row >= map.length || row < 0 || column < 0 || column >= map[0].length ||
        map[row][column] == '#') { 
      return null;
    }
    
    boolean justDelivered = map[row][column] == '1';
    if (justDelivered) {
      delivered += 1;
      map[row][column] = 'X';  // mark delivery location as delivered
      backtrackMap = new boolean[map.length][map[0].length];
      if (delivered == deliveries) {
        map[row][column] = '1';
        ArrayList<Integer> path = new ArrayList<Integer>();
        return path;
      }
    } else {
      backtrackMap[row][column] = true;
    }
    
    ArrayList<Integer> minPath = null;
    for (int i = 0; i < 4; i++) {
      ArrayList<Integer> path = findPath(map, backtrackMap, row + DIRECTIONS[i][0], column + DIRECTIONS[i][1], deliveries, delivered);
      if (path == null) continue;
      path.add(i);  // Add the direction to the end of the path - we build it in reverse, and then read it backwards later
      if (minPath == null || path.size() < minPath.size()) {
        minPath = path;
      }
    }
    
    // reset the state for our caller
    if (justDelivered) {
      map[row][column] = '1';
    } else {
      backtrackMap[row][column] = false;
    }
    
    return minPath;
  }
  // end of method definitions
  
  public static void main(String[] args) throws Exception {
    Scanner input = new Scanner(System.in); 
    System.out.print("Enter the test case name: ");
    String mapName = input.nextLine();
    input.close();
    char[][] mapArray = readTestCase(mapName);
    boolean[][] backtrackMap = new boolean[mapArray.length][mapArray[0].length];
    
    int startRow = 0;
    int startColumn = 0;
    int deliveries = 0;
    
    // find starting position and count number of deliveries to be made
    for (int i = 0; i < mapArray.length; i++) {
      for (int j = 0; j < mapArray[i].length; j++) {
        if (mapArray[i][j] == 'S') {
          startRow = i;
          startColumn = j;
        } if (mapArray[i][j] == '1') {
          deliveries += 1;
        }
      }
    }
    
    int row = startRow;
    int column = startColumn;
    ArrayList<Integer> path = new ArrayList<Integer>(findPath(mapArray, backtrackMap, startRow, startColumn, deliveries, 0));
    
    // we mark the final map with the final solution according to the ArrayList path returned
    for (int i = path.size()-1; i >= 0; i--) {
      int[] direction = DIRECTIONS[path.get(i)]; // assigns current direction
      row += direction[0];
      column += direction[1];
      if (mapArray[row][column] == '1') {
        mapArray[row][column] = 'X';
      } else if (mapArray[row][column] != 'S') {
        mapArray[row][column] = 'x';
      }
    }
    
    // output
    System.out.println("Solved map with path:");
    for (int i = 0 ; i < mapArray.length; i++) {
      System.out.println(mapArray[i]);
    }
    System.out.println("Number of steps it took: " + path.size());
    createPPM(mapArray);
  }
}


