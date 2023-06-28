import java.util.Arrays;

public class Map {
    private Room[][] map;
    private Room head = null;
    private Coordinate origin;

    public Map() {}

    public Map(int size) {
        map = new Room[size][size];
    }
    
    public Map(int width, int height) {
        map = new Room[width][height];
    }

    public void initMap() {
        if (map == null) {
            System.out.println("Init Failed");
            System.exit(2);
        }
        map[0][0] = new Room();
        origin = new Coordinate(0, 0);
    }

    public void initMap(int x, int y) {
        if (map == null || x >= map.length || y >= map[x].length) {
            System.out.println("Init Failed");
            System.exit(2);
        }
        map[x][y] = new Room();
        origin = new Coordinate(x / 2, y / 2);
    }

    public Room[][] getMap() { return map; }

    public void generate(int size) { 
        sizeup(size * 2 + 2);
        origin = new Coordinate(size, size);
        head = new Room(origin, map);
        map[0][0] = null;
        generate(size, 1, head);
    }

    private void generate(int size, int depth, Room current) {
        if (size == 0 || current == null || current.visited || current.getLocation() == null) {
            return;
        }
        
        int num_sides = (int) (((3 / depth) + 1) - Math.round(Math.random()));
        if (num_sides == 0 && size <= 2) {
            return;
        } else if (num_sides == 0) {
            while (num_sides == 0) {
                num_sides = (int) (((3 / depth) + 1) - Math.round(Math.random()));
            }
        }

        String[] side_list = {"up", "left", "down", "right"};
        int[] inverse = {2, 3, 0, 1};
        
        for (int i = 0; i < num_sides; i++) {
            int side_choice = (int)(Math.random() * side_list.length);
            while (side_list[side_choice] == null) {
                side_choice = (int)(Math.random() * side_list.length);
            }
            
            Coordinate new_location = new Coordinate(current.getLocation());
            if      (side_choice == 0) { new_location.incY(-1); } //up
            else if (side_choice == 1) { new_location.incX(-1); } //left
            else if (side_choice == 2) { new_location.incY(1); } //down
            else if (side_choice == 3) { new_location.incX(1); } //right
            
            if (!current.addSide(new Room(current, inverse[side_choice], new_location), side_choice)) {
                i--;
                continue;
            } else {
                side_list[side_choice] = null;
            }
        }
        
        current.visited = true;

        Room[] currentSides = current.getSidesExcept(current.getParentRoom());
        if(currentSides.length == 0) { return; }
        for (Room r : currentSides) {
            if (r != null) { generate(size - 1, depth + 1, r); }
        }
    }

    public void sizeup() {
        int x = map.length * 2;
        int y = map[0].length * 2;
        Room[][] temp = new Room[x][y];
        for (int i = 0, left = map.length / 2; i < map.length; i++, left++) {
            for (int j = 0, top = map[0].length / 2; j < map[0].length; j++, top++) {
                if (map[i][j] != null) {
                    temp[left][top] = map[i][j];
                }
            }
        }
        map = temp;
    }

    public void sizeup(int s) {
        int x = map.length + s;
        int y = map[0].length + s;
        Room[][] temp = new Room[x][y];
        for (int i = 0, left = map.length / 2; i < map.length; i++, left++) {
            for (int j = 0, top = map[0].length / 2; j < map[0].length; j++, top++) {
                if (map[i][j] != null) {
                    temp[left][top] = map[i][j];
                }
            }
        }
        map = temp;
    }

    public void correctSides() {
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[x][y] != null) {
                    if (y != 0 && map[x][y - 1] != null) map[x][y].addSide(map[x][y - 1], 0);
                    if (x != 0 && map[x - 1][y] != null) map[x][y].addSide(map[x - 1][y], 1);
                    if (y != map[x].length - 1 && map[x][y + 1] != null) map[x][y].addSide(map[x][y + 1], 2);
                    if (x != map.length - 1 && map[x + 1][y] != null) map[x][y].addSide(map[x + 1][y], 3);
                }
            }
        }
    }

    public void trim() {
        int xmin = map.length;
        int xmax = -1;
        int ymin = map[0].length;
        int ymax = -1;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[x][y] != null) {
                    if (xmin > x) xmin = x;
                    if (xmax < x) xmax = x;
                    if (ymin > y) ymin = y;
                    if (ymin < y) ymax = y;
               }
            }
        }
        Room[][] result = new Room[xmax - xmin + 1][ymax - ymin + 1];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = map[xmin + i][ymin + j];
                if (result[i][j] != null) {
                    result[i][j].setLocation(i, j);
                }
            }
        }
        map = result;
    }

    public String toString() {
        String result = "";
        for (Room[] row : map) {
            for (Room cell : row) {
                if (cell == null) { result += "0 "; }
                else { result += "1 "; }
            }
            result += "\n";
        }
        return result;
    }
}

/* Room Class */

class Room {
    private Room parent = null;
    private String[] side_names = {"up", "left", "down", "right"};
    private Room[] sides = {null, null, null, null}; //up left down right
    private Coordinate location = null;
    public boolean visited = false;
    private Room[][] map;

    public Room() {}

    public Room(Coordinate d, Room[][] map) { 
        location = d; 
        this.map = map;
    }

    public Room(Room parent, int side, Coordinate location) {
        this.parent = parent;
        this.location = location;
        this.map = parent.getMap();
        addSide(parent, side);
    }

    public boolean addSide(Room room, int side) {
        if (sides[side] != null) { return false; }
        int x = room.getLocation().getX();
        int y = room.getLocation().getY();
        if (map[x][y] == null) { map[x][y] = room; }
        sides[side] = room;
        return (sides[side] == null) ? false : true;
    }

    public Room getSide(int s) { 
        return sides[s];
    }

    public Room[] getSides() { return sides; }

    public Room[] getSidesExcept(Room room) {
        Room[] r = new Room[(int)(Arrays.stream(sides).filter(e -> e != null && e != room).count())];
        for (int i = 0, j = 0; i < sides.length; i++) {
            if (sides[i] != room && sides[i] != null) { r[j] = sides[i]; j++; }
        }
        return (r.length == 0 || r[0] == null) ? null : r;
    }

    public Room getParentRoom() { return parent; }

    public void setLocation(int x, int y) { location = new Coordinate(x, y); }

    public void setLocation(Coordinate d) { location = d; }

    public Coordinate getLocation() { return location; }

    public Room[][] getMap() { return map; }

    public String[] getStringSides() {
        String[] side_list = new String[(int)(Arrays.stream(sides).filter(e -> e != null).count())];
        for (int i = 0, j = 0; i < sides.length; i++) {
            if (sides[i] != null) { side_list[j] = side_names[i]; j++; }
        }
        return (side_list.length == 0) ? null : side_list;
    }
    
    public String toString() {
        return "[" + location.getX() + ", " + location.getY() + "]"; 
    }
}