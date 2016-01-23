package org.cmyers.games.threes.console;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.cmyers.games.threes.state.GameState;

public class DrawBoard {

    public static String drawBoard(GameState g) {
        int width = g.get(GameState.WIDTH);
        int height = g.get(GameState.HEIGHT);
        ImmutableList<Integer> board = g.get(GameState.BOARD);
        ImmutableList<Integer> next = g.peekNext();
        StringBuilder sb = new StringBuilder();
        sb.append("Next: " + Joiner.on(", ").join(next) + "\n");
        ArrayList<String> parts = Lists.newArrayList();
        for(int i = 0; i < width; i++) {
            parts.add("-----");
        }
        String horizontalLine = "|" + Joiner.on("-").join(parts) + "|\n";
        sb.append(horizontalLine);
        for(int i = 0; i < height; i++) {
            StringBuilder row = new StringBuilder();
            ArrayList<Integer> rowItems = Lists.newArrayList();
            for(int j = 0; j < width; j++) {
                rowItems.add(board.get(g.getCoord(j, i)));
            }
            row.append("|");
            row.append(Joiner.on("|").join(Iterables.transform(rowItems, DrawBoard::padNumber)));
            row.append("|\n");
            sb.append(row.toString());
            sb.append(horizontalLine);
        }
        return sb.toString();
    }

    private static String padNumber(Integer i) {
        String istr = String.valueOf(i);
        if(istr.length() == 1) {
            return "  " + istr + "  ";
        }
        if(istr.length() == 2) {
            return " " + istr + "  ";
        }
        if(istr.length() == 3) {
            return " " + istr + " ";
        }
        if(istr.length() == 4) {
            return istr + " ";
        }
        return istr;
    }
}
