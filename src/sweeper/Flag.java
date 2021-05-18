package sweeper;

public class Flag
{
    private Matrix flagMap;
    private int countOfclosedBoxes;

    void start ()
    {
        flagMap = new Matrix(Box.closed);
        countOfclosedBoxes = Ranges.getSize().x * Ranges.getSize().y;
    }

    Box get (Coord coord)
    {
        return flagMap.get(coord);
    }

    public void setOpenedToBox(Coord coord)
    {
        flagMap.set(coord, Box.opened);
        countOfclosedBoxes--;
    }

    void toggleFlagedToBox (Coord coord)
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
    }

    public void setFlagedToBomb(Coord coord)
    {
        flagMap.set(coord, Box.flagged);
    }

    int getCountClosedBoxes()
    {
        return countOfclosedBoxes;
    }

    void setBombedToBox(Coord coord)
    {
        flagMap.set(coord, Box.bombed);
    }

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