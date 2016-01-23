package org.cmyers.games.threes.mains;

import java.io.IOException;
import org.cmyers.games.threes.console.DrawBoard;
import org.cmyers.games.threes.state.GameState;
import org.cmyers.games.threes.state.GameState.MoveDirection;

public class ConsoleGame {

    public static void main(String[] args) throws IOException {
        GameState g = GameState.TYPE.builder().emptyBoard().emptyNext().build().init();
        System.out.println("Gamestate:\n" + DrawBoard.drawBoard(g));
        System.out.println("Move? (w,a,s,d,q?): ");
        int quit = "q".getBytes()[0];
        int in;
        while((in = System.in.read()) != quit) {
            MoveDirection md;
            switch(in) {
            case 'w':
                if(!g.canMoveY()) {
                    System.out.println("Cannot move UP!  Try again");
                    continue;
                }
                md = MoveDirection.UP;
                break;
            case 'a':
                if(!g.canMoveX()) {
                    System.out.println("Cannot move LEFT!  Try again");
                    continue;
                }
                md = MoveDirection.LEFT;
                break;
            case 's':
                if(!g.canMoveY()) {
                    System.out.println("Cannot move DOWN!  Try again");
                    continue;
                }
                md = MoveDirection.DOWN;
                break;
            case 'd':
                if(!g.canMoveX()) {
                    System.out.println("Cannot move RIGHT!  Try again");
                    continue;
                }
                md = MoveDirection.RIGHT;
                break;
            default:
                // ignore
                continue;
            }
            System.out.println("Moving " + md + "\n");
            g = g.move(md);
            if(!g.canMoveX() && !g.canMoveY()) {
                System.out.println("No more moves! Score: " + g.calculateBoardScore());
                System.exit(0);
            }
            System.out.println("Gamestate:\n" + DrawBoard.drawBoard(g));
            System.out.println("Move? (w,a,s,d,q?): ");
        }
    }
}
