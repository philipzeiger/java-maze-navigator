import java.util.*;
import java.util.Scanner;

//Program:	MazeNavigator.java
//Course:	COSC470
//Description:	This program reads a text file maze (encoded as ones and zeros)
//              and successfully navigates the maze from a specified starting point. It uses a breadth-first
//              search algorithm to search all possible paths to an exit. It compares these paths and determines 
//              the shortest path out of the maze, displaying the results.
//Author:	Philip Zeiger
//Date:	        3/3/2014
//Revised:      7/10/2021
//Language:	Java
//IDE:		NetBeans 7.4
//Notes:	
//		
//*******************************************************************************
//*******************************************************************************

public class MazeNavigator {
    //**********************************************************************************************************************
	//Method:		main
	//Description:	This is the main method that runs the maze navigator.
	//Parameters:	None.
	//Returns:      Nothing.
	//Throws:       Nothing.
	//Calls:	populateMaze, navigateMaze, buildSolution, TextFileClass
    
    public static void main(String[] args) {
    
    TextFileClass textFile = new TextFileClass();             //this creates an instance of the provided 
    textFile.getFileName("Specify the text file to be read"); //TextFileClass and sets the fileName to the 
                                                              //user input
        
   
    Scanner scanner = new Scanner(System.in);  //initiating Scanner for user input
    System.out.println("Would you like to see step-by-step solution (1) or immediate solve (2)?");
    String userInput = scanner.nextLine();
    while (!userInput.equals("1") && !userInput.equals("2")){
        System.out.println("User input not accepted, please try again.");
        System.out.println("Would you like to see step-by-step solution (1) or immediate solve (2)?");
        userInput = scanner.nextLine();
    }

    boolean stepByStep =false;      //checking if user wants step-by-step display
    if (userInput.equals("1")){
        stepByStep=true;
    }
    
    boolean userPrompted = false; 
    if(stepByStep){         //checking if user wants keyboard-prompted step-by-step display
        System.out.println("Step-by-step automatically (1), or prompted by keystroke (2)?");
        userInput = scanner.nextLine();
            
    if(userInput.equals("2"))
        userPrompted = true;
    }
    
    char[][] maze = populateMaze(textFile.fileName);  //first I call this method to set up the maze as a double array
    
    char [][] mazeCopy = populateMaze(textFile.fileName); //then I do the same thing so that this clean copy can be used to
                                                          //display the resulting solution
    char [] shortestPath = navigateMaze(maze,textFile.fileName, stepByStep, userPrompted);  //the majority of the work is done in this
                                                                              //method call. The shortest path is returned.
    buildSolution(shortestPath, mazeCopy, textFile.fileName);       //this method call builds the solution maze and displays it
    scanner.close();
    } 
    //**********************************************************************************************************************
	
        //Method        populateMaze
	//Description:	This method creates a 2D character array that stores the maze from the indicated textfile
	//Parameters:	fileName   -  this string is the user input fileName that will be used to retrieve the textfile maze
	//Returns:      maze       -  the 2D character array built from the textfile maze
	//Throws:       Nothing.
	//Calls:	TextFileClass
    
    public static char[][] populateMaze(String fileName){
        
    TextFileClass textFile = new TextFileClass();             //this creates an instance of the provided 
    textFile.fileName=fileName;                           //TextFileClass and sets the fileName to the 
                                                              //previous user input            
        
    if (textFile.fileName.length()>0)                        //checks to see if the user has supplied an input
    {                                                        //and launches into maze construction and navigation 
        
        textFile.getFileContents();                         //this method call will make the maze available
        int numberOfRows = Integer.parseInt(textFile.text[0]);
        int numberOfColumns = Integer.parseInt(textFile.text[1]);
        int rowStartPosition = Integer.parseInt(textFile.text[2]);
        int columnStartPosition = Integer.parseInt(textFile.text[3]);
        
        char maze [][];
        maze = new char [numberOfRows][numberOfColumns];        
        
        
        for(int rows = 4; rows < numberOfRows+4; rows++){   //this loop runs through the rows of the maze
                                                            //from the text file
            String mazeRow= textFile.text[rows];      
               
            for (int columns = 0; columns <numberOfColumns; columns++) //this loops through the columns
            {
                char mazeEntryChar = mazeRow.charAt(columns);
                    
                maze [rows-4] [columns] = mazeEntryChar;    //populate maze array with numbers                   
               }
        }
            
        if (maze[rowStartPosition][columnStartPosition]=='0'){ //start position placed
            maze[rowStartPosition][columnStartPosition] = '*';
        }
            
        else System.out.println("Invalid start position");           
            return maze;
    }
        
        else{ System.out.println("No text file name.");  //noninput contengency
            return null;
        }
    }
    
    //**********************************************************************************************************************
	//Method         navigateMaze
	//Description:	 This method does most of the work in this program. It takes the maze in array form and begins putting
        //                  down marks and explores to find all the exits. As it goes through this depth-first algorithm, it
        //                  saves intersection locations so that it can backtrack when it runs into a dead end or the edge of
        //                  map. It saves the path taken as a stack and every time it finds an exit, it saves it and compares
        //                  it to the previously shortest exit to find the shortest possible exit. It has the capability
        //                  of showing this step-by-step, or immediately.
	//Parameters:   maze  - the 2D character array that stores the maze as 1s and 0s.
        //              fileName   -  this string is the user input fileName that will be used to retrieve the textfile maze
	//              stepByStep -  the boolean that tells whether the user wants a step-by-step display
        //              userPrompted - the boolean that tells whether to wait on keystroke for each step
        //Returns:      shortestPath  -  the char array that contains the letters denoting the directions taken 
        //                                  for the shortest path found.
	//Throws:       Nothing.
	//Calls:	TextFileClass, isIntersection, findIntersectionIndex
    
    public static char[] navigateMaze(char [][] maze, String fileName, boolean stepByStep, boolean userPrompted){
        
        TextFileClass textFile = new TextFileClass();       //this creates an instance of the provided 
        textFile.fileName=fileName;
        
        textFile.getFileContents();
        int rowStartPosition = Integer.parseInt(textFile.text[2]);
        int columnStartPosition = Integer.parseInt(textFile.text[3]);
        
        int [] currentPosition = new int [2];
        
        currentPosition[0] = rowStartPosition;
        currentPosition[1] = columnStartPosition;
        
        boolean shortestPathFound = false;   //this variable controls the primary maze-navigation loop
        boolean backUp;
        boolean isExit;
        boolean intersection;
        boolean intersectionErase;
        
        Stack path = new java.util.Stack<>();  //the path stack is simply used to keep a character record
                                               //of the current path and save the shortest exit paths.
        
        int numberOfRows = Integer.parseInt(textFile.text[0]);
        int numberOfColumns = Integer.parseInt(textFile.text[1]);
        
        int [][] intersections = new int[numberOfRows*numberOfColumns][2];
        int intersectionNumber =0;  //this variable will keep track of the intersections in order
                                    //that they are discovered
        
        char [] recentPath = new char [numberOfRows*numberOfColumns];
        char [] shortestPath = new char [numberOfRows*numberOfColumns];
        shortestPath[0] = '1';
        
        char [][] attemptedDirections = new char [1000000] [5];  //this keeps track of the intersection row and column
        int directionIndex = 1;                                 //as well as the already attempted directions for each intersection
        
        int encounteredIntersection = 1;
        
        
        
     while (shortestPathFound==false){   //this loop reiterates until all paths have been explored
         
         if(userPrompted){    //if the user wants to prompt each advance, this takes care of that
         KeyboardInputClass keystroke = new KeyboardInputClass();
         String key = keystroke.getKeyboardInput("");
         
         if (key.isEmpty()){   //looks for keystrokes to print next move
            for (int rows =0; rows<numberOfRows; rows++)
            { 
                for (int columns =0; columns < numberOfColumns; columns++)
                {
                    if(maze[rows][columns]=='1')
                        System.out.print((char)219);
                    else if(maze[rows][columns]=='0')
                        System.out.print((char)32);
                    
                    else System.out.print(maze[rows][columns]);
                }
            System.out.println("");           
            } 
          }
          }
            
         else if(stepByStep){  //prints after each move in the maze if user says to
                System.out.println("");
            for (int rows =0; rows<numberOfRows; rows++)
            {
                for (int columns =0; columns < numberOfColumns; columns++)
                {
                    if(maze[rows][columns]=='1')
                        System.out.print((char)219);
                    else if(maze[rows][columns]=='0')
                        System.out.print((char)32);
                    
                    else System.out.print(maze[rows][columns]);
                }
            System.out.println("");           
            } 
            }
            
            intersection = false;
            
            backUp = true;    //program will default to back up to intersection unless
                              //there is a path previously untaken
            isExit = false;
            
            intersectionErase = false;
            
            if(currentPosition[0]==0||currentPosition[1]==0||currentPosition[0]==numberOfRows-1
                    ||currentPosition[1]==numberOfColumns-1){    //checks if it has found an exit
                isExit=true;
            }
            
            
            
            directionIndex = encounteredIntersection;
                
            if (isIntersection(maze,currentPosition[0],currentPosition[1],numberOfRows, numberOfColumns)){
                intersections [intersectionNumber][0] =currentPosition[0];        //if it's an intersection, the                          
                intersections [intersectionNumber][1] =currentPosition[1];        //row and column indexes are saved                         
                intersection=true;
                intersectionNumber++;
                 
                if (findIntersectionIndex(attemptedDirections, currentPosition[0], currentPosition[1])==0){
                    encounteredIntersection++;               //if it is a new intersection, this index value increases to save it in the next array row
                }
                
                else{                                    //if it's not a new intersection, the directionIndex (used to determine previously tried directions)
                                                         //is set to proper value to recall direction characters from array
                    directionIndex = findIntersectionIndex(attemptedDirections, currentPosition[0], currentPosition[1]);
                }
                
                attemptedDirections[directionIndex][0] = (char) currentPosition[0];
                attemptedDirections[directionIndex][1] = (char) currentPosition[1]; 
             }
            
                                                                                  //searching in this order: right, up, left, down                  
         
         if(!intersection){
            if(!isExit && maze [currentPosition[0]][currentPosition[1]+1]=='0'){   //if it's not an exit and there's a path to right
            maze[currentPosition[0]][currentPosition[1]+1] = '*';                 //move to the right
            currentPosition[1]++;
            path.push('E');
            backUp = false;
            }
                
            else if (!isExit &&maze[currentPosition[0]-1][currentPosition[1]]=='0'){  //if not exit and path up,    
            maze[currentPosition[0]-1][currentPosition[1]] = '*';                    //move up
            currentPosition[0]--;
            path.push('N');
            backUp = false;
            }

            else if (!isExit && maze[currentPosition[0]][currentPosition[1]-1]=='0'  //if not exit and path left
            ){
            
            maze[currentPosition[0]][currentPosition[1]-1]='*';                       //move left
            currentPosition[1]--;
            path.push('W');
            
            backUp = false;
            }

            else if (!isExit && maze[currentPosition[0]+1][currentPosition[1]]=='0'  //if not exit and there's path down and if has not
            ){    
            maze[currentPosition[0]+1][currentPosition[1]]='*';                                         //then move down
            currentPosition[0]++;
            path.push('S');
            backUp = false;
            }
            
            else{
                backUp = true;
                
            }
           
         }
            
            
         else if (intersection) {          //if is intersection, include recently-explored direction checks specific for the intersection
             if(!isExit && maze [currentPosition[0]][currentPosition[1]+1]=='0' &&  //if not exit and path to right and program has not
             attemptedDirections[directionIndex][2]!='E' &&                           //recently gone right at this intersection
             attemptedDirections[directionIndex][3]!='E' && attemptedDirections[directionIndex][4]!='E'){    
                maze[currentPosition[0]][currentPosition[1]+1] = '*';                
                currentPosition[1]++;
                path.push('E');
                backUp = false;
            }
                
            else if (!isExit &&maze[currentPosition[0]-1][currentPosition[1]]=='0' &&
            attemptedDirections[directionIndex][2]!='N' && 
            attemptedDirections[directionIndex][3]!='N' && attemptedDirections[directionIndex][4]!='N'){
                maze[currentPosition[0]-1][currentPosition[1]] = '*';                    
                currentPosition[0]--;
                path.push('N');
                backUp = false;
            }

            else if (!isExit && maze[currentPosition[0]][currentPosition[1]-1]=='0' && //if not exit and path left
            attemptedDirections[directionIndex][2]!='W' &&
            attemptedDirections[directionIndex][3]!='W' && attemptedDirections[directionIndex][4]!='W'){
                maze[currentPosition[0]][currentPosition[1]-1]='*';                       //move left
                currentPosition[1]--;
                path.push('W');
                backUp = false;
            }

            else if (!isExit && maze[currentPosition[0]+1][currentPosition[1]]=='0' && //if not exit and there's path down and if has not
            attemptedDirections[directionIndex][2]!='S' &&
            attemptedDirections[directionIndex][3]!='S' && attemptedDirections[directionIndex][4]!='S'){
                maze[currentPosition[0]+1][currentPosition[1]]='*';                                         //then move down
                currentPosition[0]++;
                path.push('S');
                backUp = false;
            }
            
            else{
                backUp = true;            //if at an intersection the program can move nowhere, it must back up
                intersectionErase = true; //to previous intersection while erasing the record of the intersection-specific
            }                             //directions that have been tried- that way when the program returns with a different
         }                                //path, there will be no data telling the program not to explore a direction at the intersection.
 
         if(intersectionErase){
            int rowErase = intersections[intersectionNumber-1][0];
            int columnErase = intersections[intersectionNumber-1][1];
            int indexToErase = findIntersectionIndex(attemptedDirections, rowErase, columnErase);
            if(indexToErase!=0){
                for (int columnEraser = 0; columnEraser < 5; columnEraser++) { //loops through and erases values
                    attemptedDirections[indexToErase][columnEraser] = ' ';     //in proper row of attemptedDirections array
                }                                                              //so that when that intersection is returned to, data about
            }                                                                  //previously tried directions will not keep the program from
                                                                               //full exploration.
            intersectionNumber--;   
            encounteredIntersection--;
         }
            
         if (isExit){                          
             recentPath = path.clone().toString().toCharArray(); //saves exit path as character array                     
             if(shortestPath[0]=='1'){
                shortestPath = recentPath;    //first exit path is saved as shortest path
             }
         }
     
         if (recentPath.length<shortestPath.length){  //comparison of lengths to determine shortest path
             shortestPath = recentPath;  
         }
                     
         if(backUp==true){                               //if the maze is trapped, back up to
                                                         //last intersection
           intersectionNumber--;
           char lastDirection;
                
           if (intersectionNumber==-1){       //here is the check to see if all paths have been explored. When the program has backed up
                    break;                    //all the way to the start and has nowhere left to go, intersectionNumber becomes negative
           }                                  //and it breaks out of the loop, returning shortestPath.
          
                
           while ((currentPosition[0]!=intersections[intersectionNumber][0]||
                  currentPosition[1]!=intersections[intersectionNumber][1])){ //this loops through until the program has backtracked
                                                                             //to former intersection.
                maze[currentPosition[0]][currentPosition[1]]='0';    //replacing the 0s in the maze               
            
                Object removedDirection = path.pop();       //as the while loop backs up to previous intersection,
                                                                                 //the direction characters are popped off of path
                lastDirection = removedDirection.toString().charAt(0); 
                
                if(removedDirection.toString().equals("E")){  //these just determine how to retrace steps
                    currentPosition[1]--;
                }

                if(removedDirection.toString().equals("N")){
                    currentPosition[0]++;
                }

                if(removedDirection.toString().equals("W")){
                    currentPosition[1]++;
                }

                if(removedDirection.toString().equals("S")){
                    currentPosition[0]--;
                }
               
                
                if ((currentPosition[0]==intersections[intersectionNumber][0]&&               //
                        currentPosition[1]==intersections[intersectionNumber][1])){
                   
                   
                    if (findIntersectionIndex(attemptedDirections, currentPosition[0], currentPosition[1])==0){
                    directionIndex++;
                    }
                    else{
                    directionIndex = findIntersectionIndex(attemptedDirections, currentPosition[0], currentPosition[1]);
                    }
                 
                      
                    for (int emptySearch = 2; emptySearch<5; emptySearch++){
                        if(attemptedDirections[directionIndex][emptySearch]!='E'&&attemptedDirections[directionIndex][emptySearch]!='W'&&
                           attemptedDirections[directionIndex][emptySearch]!='N'&&attemptedDirections[directionIndex][emptySearch]!='S'){
                               attemptedDirections[directionIndex][emptySearch] = lastDirection;
                               break;
                        }
                        
                    }
                }             
                }   
            }
                
     }  //end shortestPath loop
            
      return shortestPath; 
    
    }
    
    //**********************************************************************************************************************
	//Method        isIntersection
	//Description:	Determines if the current location is an intersection. It does this by testing all possible
        //                  configurations of 2 or more open paths.
	//Parameters:   maze  - the 2D character array that stores the maze as 1s and 0s.
        //              currentRowPosition - an integer that tells what row index of the maze the program is currently examining
	//              currentColumnPosition - an integer of the column index of the maze the program is currently examining
        //              numberOfRows  -  tells the total number of rows in the maze
        //              numberOfColumns  -  holds the total number of columns in the maze

        //Returns:      isIntersection  -  a boolean that tells whether the current position is an intersection
	//Throws:       Nothing.
	//Calls:	None.
    
    
    public static boolean isIntersection(char [][] maze, int currentRowPosition, int currentColumnPosition, int numberOfRows, int numberOfColumns){
        
        if(currentRowPosition==0||currentColumnPosition==0||currentRowPosition==numberOfRows-1||   //this checks if it is at a maze exit
                currentColumnPosition==numberOfColumns-1){          
            return false;
        }
        
        
        if(maze[currentRowPosition+1][currentColumnPosition]=='0'&&maze[currentRowPosition][currentColumnPosition+1]=='0')
            return true;
        if(maze[currentRowPosition+1][currentColumnPosition]=='0'&&maze[currentRowPosition][currentColumnPosition-1]=='0')
            return true;
        if(maze[currentRowPosition+1][currentColumnPosition]=='0'&&maze[currentRowPosition-1][currentColumnPosition]=='0')
            return true;
        
        if(maze[currentRowPosition-1][currentColumnPosition]=='0'&&maze[currentRowPosition][currentColumnPosition+1]=='0')
            return true;
        if(maze[currentRowPosition-1][currentColumnPosition]=='0'&&maze[currentRowPosition][currentColumnPosition-1]=='0')
            return true;
        if(maze[currentRowPosition-1][currentColumnPosition]=='0'&&maze[currentRowPosition+1][currentColumnPosition]=='0')
            return true;
        
        if(maze[currentRowPosition][currentColumnPosition+1]=='0'&&maze[currentRowPosition+1][currentColumnPosition]=='0')
            return true;
        if(maze[currentRowPosition][currentColumnPosition+1]=='0'&&maze[currentRowPosition-1][currentColumnPosition]=='0')
            return true;
        if(maze[currentRowPosition][currentColumnPosition+1]=='0'&&maze[currentRowPosition][currentColumnPosition-1]=='0')
            return true;

        if(maze[currentRowPosition][currentColumnPosition-1]=='0'&&maze[currentRowPosition+1][currentColumnPosition]=='0')
            return true;
        if(maze[currentRowPosition][currentColumnPosition-1]=='0'&&maze[currentRowPosition-1][currentColumnPosition]=='0')
            return true;
        if(maze[currentRowPosition][currentColumnPosition-1]=='0'&&maze[currentRowPosition][currentColumnPosition+1]=='0')
            return true;
        
       
        return false;
    }
    
    //**********************************************************************************************************************
	//Method        findIntersectionIndex
	//Description:	searches through the attemptedDirections 2D char array to find the row value
        //              and column value that have been passed in. If they are found, the corresponding index value for the 
        //              attemptedDirections array is returned. Otherwise, 0 is returned.
	//Parameters:   attemptedDirections - a 2D char array that has intersection coordinates in the first and second columns
        //                                    and attempted directions in the other 3 columns.
        //              intersectionRow - an int that is the row coordinate for the current intersection being explored
        //              intersectionColumn - an int that is the column coordinate for the current intersection being explored
        //Returns:      indexOfAttemptedDirections - the index of the row in attemptedDirections array that contains data about the 
        //                                           current intersection being explored (namely, which directions have been tried)
	//Throws:       Nothing.
	//Calls:	None.
     
    
    public static int findIntersectionIndex(char [][] attemptedDirections, int intersectionRow, int intersectionColumn){
        
        for (int indexOfAttemptedDirections = 0; indexOfAttemptedDirections < attemptedDirections.length; indexOfAttemptedDirections++) {
            
          if (attemptedDirections[indexOfAttemptedDirections][0] == (char) intersectionRow && attemptedDirections[indexOfAttemptedDirections][1] == (char) intersectionColumn){
          
              return indexOfAttemptedDirections;
              
          }
            
        }
        
        return 0;
        
    }
    
    
    //**********************************************************************************************************************
	//Method        buildSolution
	//Description:	This method is in charge of creating the final solution output.
	//Parameters:   path  - this char array contains the characters denoting the shortest path to an exit
        //              maze  - the 2D character array that stores the maze as 1s and 0s.
        //              fileName  -  this String contians the user-given filename for the text file maze
        //Returns:      None
	//Throws:       Nothing.
	//Calls:	TextFileClass
    
    public static void buildSolution(char [] path, char[][] maze, String fileName){
        TextFileClass textFile = new TextFileClass();             
        textFile.fileName=fileName;
        
        textFile.getFileContents(); 
        
        int numberOfRows = Integer.parseInt(textFile.text[0]);
        int numberOfColumns = Integer.parseInt(textFile.text[1]);
        
        int rowCurrentPosition = Integer.parseInt(textFile.text[2]);
        int columnCurrentPosition = Integer.parseInt(textFile.text[3]);
        
        int spacesCount = 1;
        
        for (int pathStep = 0; pathStep < path.length; pathStep++) { //this loops through the path and marks
                                                                     //the maze to show where shortest path is.
            maze[rowCurrentPosition][columnCurrentPosition] = '*';
            
            if (path[pathStep]=='E'){
                columnCurrentPosition++;
                spacesCount++;
            }
            if (path[pathStep]=='N'){
                rowCurrentPosition--;
                spacesCount++;
            }
            if (path[pathStep]=='W'){
                columnCurrentPosition--;
                spacesCount++;
            }
            if (path[pathStep]=='S'){
                rowCurrentPosition++;
                spacesCount++;
            }
            }
        System.out.println("");
        System.out.println("Shortest route is " + spacesCount + " spaces long");
        System.out.println("");
        
        for (int rows =0; rows<numberOfRows; rows++){
            
            for (int columns =0; columns < numberOfColumns; columns++)
           {
               if(maze[rows][columns]=='1')
                        System.out.print((char)219);
                    else if(maze[rows][columns]=='0')
                        System.out.print((char)32);
                    else System.out.print(maze[rows][columns]);
           }
           System.out.println("");
        }
        
    }
    //***********************************************************************************************************************
}
//***************************************************************************************************************************
//***************************************************************************************************************************
