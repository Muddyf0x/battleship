import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Ship {
    int startX, startY;
    int length;
    boolean horizontal;
    int id;

    Set<String> hits = new HashSet<>();

    public Ship(int id, int x, int y, int length, boolean horizontal) {
        this.id = id;
        this.startX = x;
        this.startY = y;
        this.length = length;
        this.horizontal = horizontal;
    }

    public List<int[]> getCoordinates() {
        List<int[]> coords = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int x = startX + (horizontal ? i : 0);
            int y = startY + (horizontal ? 0 : i);
            coords.add(new int[]{x, y});
        }
        return coords;
    }

    public List<int[]> getBufferZone(int boardSize) {
        Set<String> buffer = new HashSet<>();
        for (int[] c : getCoordinates()) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = c[0] + dx;
                    int ny = c[1] + dy;
                    if (nx >= 0 && ny >= 0 && nx < boardSize && ny < boardSize) {
                        buffer.add(nx + "," + ny);
                    }
                }
            }
        }
        List<int[]> result = new ArrayList<>();
        for (String s : buffer) {
            String[] parts = s.split(",");
            result.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
        }
        return result;
    }

    public boolean isHit(int x, int y) {
        for (int[] coord : getCoordinates()) {
            if (coord[0] == x && coord[1] == y) {
                hits.add(x + "," + y);
                return true;
            }
        }
        return false;
    }

    public boolean isSunk() {
        return hits.size() >= length;
    }
}