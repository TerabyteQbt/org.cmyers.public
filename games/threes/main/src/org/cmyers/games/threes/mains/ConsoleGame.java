package org.cmyers.games.threes.mains;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.IOException;
import org.cmyers.games.threes.console.DrawBoard;
import org.cmyers.games.threes.state.GameState;

public class ConsoleGame {

    public static void main(String[] args) throws IOException {
        GameState g = GameState.TYPE.builder().emptyBoard().emptyNext().random().build().init();
        System.out.println("Gamestate:\n" + DrawBoard.drawBoard(g));
        System.out.println("Move? (w,a,s,d,q?): ");
        int quit = "q".getBytes()[0];
        int in;
        while((in = System.in.read()) != quit) {
            GameState.MoveDirection md;
            switch(in) {
            case 'w':
                md = GameState.MoveDirection.UP;
                break;
            case 'a':
                md = GameState.MoveDirection.LEFT;
                break;
            case 's':
                md = GameState.MoveDirection.DOWN;
                break;
            case 'd':
                md = GameState.MoveDirection.RIGHT;
                break;
            default:
                // ignore
                continue;
            }
            if(!g.canMove(md)) {
                System.out.println("Cannot move " + md + "!  Try again");
                continue;
            }
            System.out.println("Moving " + md + "\n");
            g = g.move(md);
            {
                // silly lambdas, you only work with final variables. Why can't the compiler do this implicitly for you?
                final GameState gf = g;
                if(Iterables.frequency(Iterables.transform(ImmutableList.copyOf(GameState.MoveDirection.values()), (GameState.MoveDirection md2) -> gf.canMove(md2)), true) == 0) {
                    System.out.println("No more moves! Score: " + g.calculateBoardScore());
                    System.exit(0);
                }
            }
            System.out.println("Gamestate:\n" + DrawBoard.drawBoard(g));
            System.out.println("Move? (w,a,s,d,q?): ");
        }
        System.out.println("Quitting! Score so far: " + g.calculateBoardScore());
        System.exit(0);
    }
}
