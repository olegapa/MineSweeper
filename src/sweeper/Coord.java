package sweeper;

public class Coord
{
    public int x;
    public int y;

    public Coord (int x, int y)             //конструктор
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj)       //сравниваем координаты друг с другом
    {
        if(this == obj)
            return true;

        if (obj instanceof Coord)
        {
            return ((Coord) obj).x == x && ((Coord) obj).y == y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x*17 + y*51;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}