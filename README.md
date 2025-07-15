# ITALIAN DRAUGHT

---

## - INTRODUCTION
> The Italian Draught in a game played in a chessboard 8x8 in which every player has 12 pawn. The main objective is to capture all the opponent's pieces or to block them so that they have no legal moves left.

---

## - RULES
> 1. The game starts with the player controlling the white pieces.
> 2. Players take turns making one move at a time: white, black, white and so on. 
> 3. Pieces always move forward and diagonally by one square on the dark square.
> 4. When a piece reaches the opponent's back row, it gets promoted to king.
> 5. A king is marked with a K on the top of the piece.
> 6. The King can move either forward or backward by one square, always on the dark squares.
> 7. Capturing is mandatory: when a piece encounters an opponent's piece with a free square behind it, along the same diagonal, it is required to capture it. If after the first capture, the piece finds itself in a position to capture again, it must continue capturing.
> 8. A regular piece can capture only forward, along the two diagonals; it can capture only other regular pieces, not kings.
> 9. A king can capture along all four diagonals it can capture other kins.
> 10. A player Wins when the opponent has no more pieces to move and therefore cannot make any legal moves because all their pieces are blocked.

---

## - INSTALLATION
### Prerequisites
> 1. Make sure you have Java 17 or higher installed on your pc.
> 2. Make sure you have gradle installed on your pc.

### Steps
> 1. Clone the repository from GitHub using your terminal o directly from your IDE.
> 2. If you used the terminal you have to build the project before running it.


### Execution
> 1. You can run the application from the terminal using gradle: ./gradlew.run.
> 2. You can run the application directly from your IDE.

---

## - HOW TO PLAY
### From console
> Once you start the application you have to choose if you want to play against the CPU (type 1 on the console) or against your friends (type 2 on the console)
> If you choose to play against the CPU you have to select the difficulty level choosing from 1, the easiest, to 5, the hardest, typing the number on the console.
> At this point you can start playing, for moving the pieces you have to type the coordinates of the piece you want to move and the coordinates of destination.
> The coordinates must be written in this format: x1,y1;x2,y2.
> Once the move is made, the game will automatically switch turn if the move is valid; otherwise, a message will be displayed explaining the error.
> For capturing a piece of your opponent you have to insert the coordinates as said before.
> For multiple captures, you must enter the coordinates of the piece to move, the first destination, and then the second destination in this way: x1,y1;x2,y2;x3,y3.
> If you want to quit the game in the middle of a match you only need to type "exit" instead of a command.

### From GUI
>Once you start the application it spawns a panel prompting the player to select the game mode (vs CPU or 1 vs 1).
> If you choose to play against the CPU you have to select the difficulty level choosing from 1, the easiest, to 5, the hardest, typing the number in the specified spot.
> At this point you can start playing, for moving the pieces you must click on top of the piece you want to move and the click on the square of destination.
> In the case you are trying to make an illegal move a message will be displayed to explain the problem.
> For capturing a piece of your opponent you have to act like said up here for the movement.
> For multiple capture you must click on the piece you want to move, click on the first square of destination and the click on the second square of destination.
> If you want to quit the game in the middle of a match you only need to click on the red X.