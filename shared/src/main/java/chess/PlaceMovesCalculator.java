package chess;

import java.util.Collection;

public interface PlaceMovesCalculator {
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position);
}
