package sweeper;

class Bomb
{
    private Matrix bombMap;
    private int totalBombs;     //общее количество бомб

    Bomb (int totalBombs)
    {
        this.totalBombs = totalBombs;
        fixBombsCount();
    }

    void start()                            //расставляет бомбы
    {
        bombMap = new Matrix(Box.zero);
        for (int j = 0; j < totalBombs; j++)
            placeBomb ();
    }

    Box get (Coord coord)
    {
        return bombMap.get(coord);
    }

    private void fixBombsCount ()                                   //ограничили количество бомб
    {
        int maxBombs = Ranges.getSize().x * Ranges.getSize().y /3;
        if (totalBombs > maxBombs)
            totalBombs = maxBombs;
    }

    private void placeBomb ()
    {
        while (true)
        {
            Coord coord = Ranges.getRandomCoord();

            if (Box.bomb == bombMap.get(coord))
                continue;

            bombMap.set(new Coord(coord.x, coord.y), Box.bomb);
            incNumbersAroundBomb(coord);

            break;
        }

    }

    private void incNumbersAroundBomb (Coord coord)                     //увеличение чисел вокруг бомбы
    {
        for (Coord around : Ranges.getCoordsArround(coord))

            if (Box.bomb != bombMap.get(around))                        //перебираем все клетки вокруг
                bombMap.set(around, bombMap.get(around).getNextNumberBox());
    }

    int getTotalBombs()
    {
        return totalBombs;
    }
}