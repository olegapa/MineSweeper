package sweeper;

public class Game           //фасадный класс, занимается всеми внутренними разборками "классов "
{
    private final Bomb bomb;  //матрица с бомбами
    private final Flag flag;  //матрица с флагами
    private GameStat state;

    public GameStat getState()  //состояние игры
    {
        return state;
    }

    public Bomb getBomb() {   //количество бомб
        return bomb;
    }

    public Flag getFlag() {   //количество флагов
        return flag;
    }

    public Game (int cols, int rows, int bombs)  //конструктор, передаем размер экрана и количество бомб
    {
        Ranges.setSize(new Coord(cols, rows));
        bomb = new Bomb(bombs);
        flag = new Flag();
    }

    public void start ()   //метод для запуска игры
    {
        bomb.start();
        flag.start();
        state = GameStat.PLAYED;
    }

    public Box getBox (Coord coord)  //показывает, что нужно изобразить в том или ином месте экрана
    {
        if (flag.get(coord) == Box.opened)
            return bomb.get(coord);
        else
            return flag.get(coord);
    }

    public void pressLeftButton (Coord coord)  //нажатие левой кнопки мыши
    {
        if (gameOver ()) return;

        openBox (coord);
        checkWinner();
    }

    private void checkWinner ()
    {
        if (state == GameStat.PLAYED)
            if (flag.getCountClosedBoxes() == bomb.getTotalBombs())
                state = GameStat.WINNER;
    }

    private void openBox(Coord coord)
    {
        switch (flag.get(coord))
        {
            case advised:
            case closed :
                switch (bomb.get(coord))
                {
                    case zero: openBoxesAround (coord); return;
                    case bomb: openBombs (coord); return;
                    default: flag.setOpenedToBox(coord);  return;
                }

            case opened :
                setOpenedToClosedBoxesAroundNumber(coord);
                return;

            case flagged: return;



        }
    }

    private void openBombs(Coord bombed)
    {
        state = GameStat.BOMBED;
        flag.setBombedToBox(bombed);

        for (Coord coord : Ranges.getAllCoords())
            if (bomb.get(coord) == Box.bomb)
                flag.setOpenedToClosedBombBox (coord);
            else
                flag.setNobombToFlagedSafeBox (coord);
    }

    private void openBoxesAround(Coord coord)
    {
        flag.setOpenedToBox(coord);

        for (Coord around : Ranges.getCoordsArround(coord))
            openBox(around);
    }

    public void pressRightButton(Coord coord)   //нажатие правой кнопки мыши
    {
        if (gameOver ()) return;
        flag.toggleFlagedToBox (coord);
    }

    private boolean gameOver()
    {
        if (state == GameStat.PLAYED)
            return false;
        //start();
        return true;
    }


    //Если рядом с цифрой n поставлено n флажков, то автоматически открывает остальные граничные к
    //нажатой цифре клетки
    private void setOpenedToClosedBoxesAroundNumber (Coord coord)
    {
        if (bomb.get(coord) != Box.bomb)
            if (flag.getCountOfFlagedBoxesAround(coord) == bomb.get(coord).getNumber())

                for (Coord around : Ranges.getCoordsArround(coord))
                    if (flag.get(around) == Box.closed)
                        openBox(around);
    }

}