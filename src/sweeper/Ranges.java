package sweeper;

import java.util.ArrayList;
import java.util.Random;

public class Ranges
{
    private static Coord size;                          //в этой переменной храним координаты
    private static ArrayList<Coord> allCoords;          //список всех координат
    private static final Random random = new Random();  //генератор рандомных чисел для заполнения поля


    public static void setSize (Coord _size)            //сеттер с package доступом
    {
        assert (_size != null);

        size = _size;
        allCoords = new ArrayList<Coord>();

        for (int y = 0; y < size.y; y++)
            for (int x = 0; x < size.x; x++)
                allCoords.add(new Coord(x,y));
    }

    public static Coord getSize()
    {
        return size;
    }               //получение координат

    public static ArrayList<Coord> getAllCoords ()
    {
        return allCoords;
    } //получение списка координата

    static boolean inRange (Coord coord)                         //вспомогательная функция, определяющая, находится ли клетка в нашем поле
    {
        assert (coord != null);

        return coord.x >= 0 && coord.x < size.x &&
                coord.y >= 0 && coord.y < size.y;
    }

    //получение рандомной координаты
    static Coord getRandomCoord ()
    {
        return new Coord(random.nextInt(size.x), random.nextInt(size.y));
    }

    static ArrayList<Coord> getCoordsArround (Coord coord)      //получение координат вокруг какой-либо клетки
    {
        assert (coord != null);

        Coord around;
        ArrayList<Coord> list = new ArrayList<Coord>();

        for (int x = coord.x - 1; x <= coord.x + 1; x++)
            for (int y = coord.y - 1; y <= coord.y + 1; y++)

                if (inRange(around = new Coord(x, y)))
                    if (!around.equals(coord))
                        list.add(around);
        return list;
    }


}