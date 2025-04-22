public class UI {
    public static void main(String[] args) {
        BGE game = BGE.createGame();
        int boardSize = game.getDEFAULT_BOARD_SIZE();

        IO.printWelcomeScreen();

        while (true) {
            int gamemode = IO.showGameModeSelection(); // exits on 5

            game.startGame(gamemode);

            int gameOver = 0;
            while (gameOver == 0) {
                int boardToDisplay = (game.rounds % game.numOfPlayers == 0 || game.numOfPlayers == 1) ? 2 : 1;
                char[] board = game.getBoard(boardToDisplay);
                IO.printBoard(board, boardSize);

                int[] coords = IO.readCoordinate(boardSize);
                game.shoot(coords[0], coords[1]);

                gameOver = game.isGameWon();
            }

            System.out.println("Game Over! Someone has won!");
        }
    }
}
/*
        while (true) {
            int[] coords = IO.readCoordinate(boardSize);
            boolean hit = game.shoot(coords[0], coords[1], 1);
            if (hit)
                System.out.println("Nice shoot!");
            else
                System.out.println("Miss");
            board = game.getBoard(1);
            printBoard(board, boardSize);
        }

 */