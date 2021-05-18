package sweeper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


//Класс, хранящий группу клеток и количество бомб в ней
public class Group {
    private int numbBombs; //количество бомб
    private Set<Coord> boxes; // множество клеток
    private boolean wrongFlags;

    public void setNumbBombs(int numbBombs) {
        this.numbBombs = numbBombs;
    }

    public void setBoxes(Set<Coord> boxes) {
        this.boxes = boxes;
    }

    public int getNumbBombs() {
        return numbBombs;
    }

    public Set<Coord> getBoxes() {
        return boxes;
    }


    @Override
    public int hashCode() {
        int hash = 0;

        hash = 1001*numbBombs;

        for(Coord i: boxes)
            hash -= i.hashCode();

        return hash;
    }

    public Group(){
        numbBombs = 0;
        boxes = null;
        wrongFlags = false;
    }

    //Конструктор, создающий группу соответствующую клетке, координаты которой были переданы
    public Group(Coord coord, Game game){
        assert (coord != null);
        assert (game != null);

        wrongFlags = false;

        //Прерываем поток, если клетка соответствующая группе закрыта или открыта, но рядом с ней нет бомб
        if( game.getFlag().get(coord) != Box.opened || game.getBomb().get(coord).getNumber() == 0) {
            Thread.currentThread().interrupt();
            return;
        }

        numbBombs = game.getBomb().get(coord).getNumber();

        boxes = new HashSet<>();

        ArrayList<Coord> around = Ranges.getCoordsArround(coord);

        //Добавляем клетки вокруг опорной в группу, если где-то флаг - то уменьшаем количество мин в группе и
        //игнорируем эту клетку
        for (Coord i: around){
            assert(i != null);

            if (game.getFlag().get(i) == Box.flagged){
                numbBombs--;
            }
            if(game.getFlag().get(i) == Box.closed){
                boxes.add(i);
            }
        }

        if(numbBombs < boxes.size()){
            wrongFlags = true;
        }
    }

    //сравнивает 2 группы: возвращает:
    //0 - не пересекаются
    //1 - this содержит правую группу
    //-1 - правая группа содержит this
    //2 - пересекаются
    public int compare(Group right){

        //right - подмножество this
        if(boxes.containsAll(right.getBoxes()))
            return 1;

        // this - подмножество right
        if(right.getBoxes().containsAll(boxes))
            return -1;

        //пересекаются
        Set<Coord> p = new HashSet<>(right.getBoxes());
        p.retainAll(boxes);

        if(p.isEmpty())
            return 0;

        return 2;
    }

    //Вычитает из этого множество другое
    public void subtract(Group right){
        boxes.removeAll(right.getBoxes());

        numbBombs -= right.numbBombs;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj instanceof Group) {
            return numbBombs == ((Group) obj).getNumbBombs() && boxes.equals(((Group) obj).getBoxes());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append(numbBombs).append(" - (");

        for (Coord coord: boxes)
            b.append(coord.toString()).append("; ");

        return b.toString() + ")";
    }
}
