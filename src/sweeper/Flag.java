package sweeper;

public class Flag
{
    private Matrix flagMap;             //матрица флагов
    private int countOfclosedBoxes;     //количество закрытых клеток

    void start ()
    {
        flagMap = new Matrix(Box.closed);
        countOfclosedBoxes = Ranges.getSize().x * Ranges.getSize().y;
    }

    Box get (Coord coord)
    {
        return flagMap.get(coord);
    }       //получение картинки

    public void setOpenedToBox(Coord coord)                     //установка открытой клетки
    {
        flagMap.set(coord, Box.opened);
        countOfclosedBoxes--;
    }

    void toggleFlagedToBox (Coord coord)                    //переключатель флагов
    {
        switch (flagMap.get(coord))
        {
            case flagged:
                setClosedToBox (coord);
                break;

            case closed :
                setFlagedToBomb(coord);
                break;
        }
    }

    private void setClosedToBox(Coord coord)
    {
        flagMap.set(coord, Box.closed);
    }   //установка закрытой клетки

    public void setFlagedToBomb(Coord coord)
    {
        flagMap.set(coord, Box.flagged);
    }  //установка флага на бомбу

    int getCountClosedBoxes()
    {
        return countOfclosedBoxes;
    }

    void setBombedToBox(Coord coord)
    {
        flagMap.set(coord, Box.bombed);
    }    //установка бомбы

    void setOpenedToClosedBombBox(Coord coord)
    {
        if (flagMap.get(coord) == Box.closed)
            flagMap.set(coord, Box.opened);
    }

    void setNobombToFlagedSafeBox(Coord coord)
    {
        if (flagMap.get(coord) == Box.flagged)
            flagMap.set(coord, Box.nobomb);
    }

    int getCountOfFlagedBoxesAround (Coord coord)
    {
        int count = 0;

        for (Coord around : Ranges.getCoordsArround(coord))
            if (flagMap.get(around) == Box.flagged)
                count++;

        return count;
    }

    public void setAdvised(Coord coord){
        flagMap.set(coord, Box.advised);
    }

    public void setClosed(Coord coord){
        flagMap.set(coord, Box.closed);
    }
}