package net.mcforge.iomodel.bot;

public class Vector3 {
    private int x;
    private int y;
    private int z;
    public Vector3(int x, int y, int z) { this.setX(x); this.setY(y); this.setZ(z); }
    /**
     * @return the x
     */
    public int getX() {
        return x;
    }
    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }
    /**
     * @return the y
     */
    public int getY() {
        return y;
    }
    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }
    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }
    /**
     * @param z the z to set
     */
    public void setZ(int z) {
        this.z = z;
    }
    
    public int getHashCode() {
        return x + z * 500 + y * 500 * 500;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Vector3) {
            Vector3 v3 = (Vector3)obj;
            return v3.x == x && v3.y == y && v3.z == z;
        }
        return false;
    }
}
