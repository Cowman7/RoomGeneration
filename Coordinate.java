public class Coordinate {
    private int x = Integer.MIN_VALUE;
    private int y = Integer.MIN_VALUE;
    private int z = Integer.MIN_VALUE;

    public Coordinate() {}

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinate(Coordinate c) {
        x = c.getX();
        y = c.getY();
        try {
            z = c.getZ();
        } catch (IllegalArgumentException iae) {
            z = Integer.MIN_VALUE;
        }
    }

    public int getX() { return c(x, 'x'); }

    public int getY() { return c(y, 'y'); }

    public int getZ() { return c(z, 'z'); }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }

    public void setZ(int z) { this.z = z; }

    public void incX() { x++; }

    public void incX(int i) { x += i; }

    public void incY() { y++; }

    public void incY(int i) { y += i; }

    public void incZ() { z++; }

    public void incZ(int i) { z += i; }

    private int c(int i, char c) {
        if (i == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Integer " + c + " not Initialized");
        }
        return i;
    }

    public String toString() {
        String result = "Coordinate [x: " + x + " y: " + y; 
        return (z == Integer.MIN_VALUE) ?  result + "]" : result + " z: " + z + "]";
    }
}
