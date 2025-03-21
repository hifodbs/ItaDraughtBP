package units.berettapillinini.draught.bean;

public class Position{

    int x,y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(Position p) {
        x += p.getX();
        y += p.getY();
    }

    public static Position add(Position p1, Position p2){
        return new Position(p1.getX()+ p2.getX(), p1.getY()+ p2.getY());
    }
}
