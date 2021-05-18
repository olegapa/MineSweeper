import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

import sweeper.*;
import sweeper.Box;


public class MineSweeper extends JFrame
{
    private final Game game;
    private JPanel panel;
    private JLabel label;

    private final int COLS = 30; // столбцы
    private final int ROWS = 30; // строки
    private final int BOMBS = 150; // количество бомб
    private final int IMAGE_SIZE = 30; // размер картинки одинаковый по x и по y

    // main...
    public static void main(String[] args)
    {
        new MineSweeper();
    }

    //Конструктор, вызываем сразу из main
    private MineSweeper()
    {
        game = new Game(COLS, ROWS, BOMBS);
        game.start();

        // установка окна, панели, картинок
        setImages();
        initLabel();
        initPanel();
        initFrame();
    }

    private void initLabel ()
    {
        label = new JLabel("Найди все бомбы!!!");
        add (label, BorderLayout.SOUTH);
    }

    //Установка панели
    private void initPanel (){
        panel = new JPanel() // при инициализации выводим картинки
        {
            //Метод, вызавающаяся при рисовании картинки
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                for (Coord coord : Ranges.getAllCoords())
                    //g.drawImage((Image) game.getBox(coord).image, coord.x * IMAGE_SIZE, coord.y * IMAGE_SIZE, this); //приведение типа к Image
                    g.drawImage((Image) game.getBox(coord).image,
                            coord.x * IMAGE_SIZE, coord.y * IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE, this); //приведение типа к Image

            }
        };

        //устанавливаем мышечный адаптор
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / IMAGE_SIZE;
                int y = e.getY() / IMAGE_SIZE;
                Coord coord = new Coord(x, y);

                if (e.getButton() == MouseEvent.BUTTON1) // левая кнопка мыши
                    game.pressLeftButton (coord);

                if (e.getButton() == MouseEvent.BUTTON3) // правая кнока мыши
                    game.pressRightButton (coord);

                if (e.getButton() == MouseEvent.BUTTON2) { // средняя кнопка мыши
                    Helper h = new Helper();
                    h.start();

                    try {
                        if(!h.isInterrupted())
                            h.join();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                label.setText(getessage ());
                panel.repaint(); // после каждого действия мыши перерисовываем панель игры
            }
        });

        panel.setPreferredSize(new Dimension(Ranges.getSize().x * IMAGE_SIZE, Ranges.getSize().y * IMAGE_SIZE));
        add (panel);
    }

    private String getessage()
    {
        switch (game.getState())
        {
            case PLAYED: return "Еще есть бомбы";
            case BOMBED:return "Бабах - ты взорвался";
            case WINNER: return "Примите поздравления";
            default: return "Welcome";
        }
    }


    // Настройка фрейма, в котором будет происходить активность.
    private void initFrame ()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //заканчивает работу программы при закрытии окна
        setTitle("Java Sweeper 2000 Volosatov"); //установка заголовка
        setResizable(false); // окно не будет менять размер
        setVisible(true); // устанавливаем, что окно будет видно
        pack(); //метод из класса JFrame устанавливает размер окна достаточный для отображения
        setIconImage(getImage("icon")); //устанавливаем иконку
        setLocationRelativeTo(null); // Размещаем окно по центру

    }

    //Устанавливаем все картинки
    private void setImages ()
    {
        for (Box box : Box.values())
            box.image = getImage(box.name().toLowerCase());
    }

    // Получаем картинку по имени
    private Image getImage (String name){
        assert (name != null);

        String filename = "img/" + name + ".png";

        ImageIcon icon = new ImageIcon(getClass().getResource(filename)); //использование ресурсов (подключить папку с ресурсами)
        return icon.getImage();

    }

    public class Helper extends Thread{

        Set<Group> groups;
        boolean helped;

        public Helper(){
            groups = new HashSet<>();
            helped = false;
        }

        @Override
        public void run() {
            System.out.println("helper started to find solution");
            if(!help()) {
                if(!Thread.currentThread().isInterrupted())
                    bestProbability();
            }
        }

        private void deleteEmpty(){

            Group empty = new Group();
            empty.setNumbBombs(0);
            empty.setBoxes(new HashSet<Coord>());

            while (groups.contains(empty))
                groups.remove(empty);
        }

        private boolean help(){
            makeGroups();

            if(!Thread.currentThread().isInterrupted()) {
                deleteEmpty();
                while (true){
                    int changes = 0;

                    for(Iterator<Group> it1 = groups.iterator(); it1.hasNext();) {
                        Group gr1 = it1.next();
                        for (Iterator<Group> it2 = groups.iterator(); it2.hasNext(); ) {
                            Group gr2 = it2.next();

                            if (gr1 == gr2)
                                continue;

                            switch (gr1.compare(gr2)) {
                                case 1:
                                    groups.remove(gr1);
                                    gr1.subtract(gr2);
                                    if(gr1.getBoxes().size() != 0)
                                        groups.add(gr1);

                                    changes++;

                                    it1 = groups.iterator();
                                    gr1 = it1.next();
                                    it2 = groups.iterator();
                                    break;

                                case 2:
/*                                    crossedGroups(gr1, gr2);
                                    changes++;

                                    it1 = groups.iterator();
                                    gr1 = it1.next();
                                    it2 = groups.iterator();*/
                                    break;
                                case -1:
                                    groups.remove(gr2);
                                    gr2.subtract(gr1);
                                    if(gr2.getBoxes().size() != 0)
                                        groups.add(gr2);
                                    changes++;

                                    it1 = groups.iterator();
                                    gr1 = it1.next();
                                    it2 = groups.iterator();
                                    break;

                                default: break;

                            }

                        }
                    }
                    if(changes == 0)
                        break;
                }

                for(Group i: groups){
                    doEvent(i);
                }
            }

            System.out.println("helper finished");
            return helped;
        }

        private void doEvent(Group i){
            if (i.getBoxes().size() == 0)
                return;
            if(i.getNumbBombs() == 0){
                for(Coord c: i.getBoxes()){
                    helped = true;
                    game.pressLeftButton(c);
                }
                return;
            }

            if(i.getNumbBombs() == i.getBoxes().size()){
                for(Coord c: i.getBoxes()){
                    helped = true;
                    game.pressRightButton(c);
                }
            }
        }
        private void crossedGroups(Group gr1, Group gr2) {
            assert (gr1 != null);
            assert (gr2 != null);

            Group nGr = new Group();
            nGr.setBoxes(crossCoords(gr1.getBoxes(), gr2.getBoxes()));

            if (gr1.getNumbBombs() < gr2.getNumbBombs()) {
                nGr.setNumbBombs(gr2.getNumbBombs() - gr1.getBoxes().size() + nGr.getBoxes().size());
                if (nGr.getNumbBombs() == gr1.getNumbBombs()) {
                    groups.remove(gr1);
                    groups.remove(gr2);

                    gr1.subtract(nGr);
                    gr2.subtract(nGr);

                    groups.add(gr1);
                    groups.add(gr2);
                }
            } else {
                nGr.setNumbBombs(gr1.getNumbBombs() - gr2.getBoxes().size() + nGr.getBoxes().size());
                if (nGr.getNumbBombs() == gr2.getNumbBombs()) {
                    groups.remove(gr1);
                    groups.remove(gr2);

                    gr1.subtract(nGr);
                    gr2.subtract(nGr);

                    groups.add(gr1);
                    groups.add(gr2);
                }

            }
            groups.add(nGr);
        }

        private Set<Coord> crossCoords(Set<Coord> c1, Set<Coord> c2){
            Set<Coord> ans = new HashSet<>(c1);
            ans.retainAll(c2);
            return ans;
        }

        private void bestProbability(){
            class Probability{
                private LinkedList<Double> probabilities;
                Coord coord;

                public LinkedList<Double> getProbabilities() {
                    return probabilities;
                }

                public Coord getCoord() {
                    return coord;
                }

                Probability(Coord coord){
                    this.coord = coord;
                    probabilities = new LinkedList<>();
                }

                Probability(Coord coord, double prob){
                    this.coord = coord;
                    probabilities = new LinkedList<>();
                    probabilities.add(prob);
                }

                public void addProbability(Double p){
                    probabilities.add(p);
                }

                public double getSummaryProbability(){
                    Double ans = (double) 1;
                    for(Double i: probabilities){
                        ans *= (1 - i);
                    }
                    return ((double) 1) - ans;
                }


            }

            deleteEmpty();

            ArrayList<Probability> probabilities = new ArrayList<>();

            for(Group i: groups){
                boolean exists = false;
                for(Coord c: i.getBoxes()){
                    for (Probability probability : probabilities) {
                        if (probability.getCoord() == c) {
                            exists =true;
                            probability.addProbability(((double) i.getNumbBombs() / (double) i.getBoxes().size()));
                            break;
                        }
                    }
                    if(!exists)
                        probabilities.add(new Probability(c, ((double) i.getNumbBombs() / (double) i.getBoxes().size())));

                }
            }

            Probability temp = probabilities.get(0);
            for(Probability i: probabilities){
                if(i.getSummaryProbability() < temp.getSummaryProbability())
                    temp = i;
            }

            game.getFlag().setAdvised(temp.coord);
            System.out.println("Try yellow box");;

        }

        private void makeGroups() {
            for(int i = 0; i < COLS; i++){
                for (int j = 0; j < ROWS; j++){
                    int finalI = i;
                    int finalJ = j;

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Group group = new Group(new Coord(finalI, finalJ), game);
                            if(!Thread.currentThread().isInterrupted())
                                groups.add(group);
                        }
                    });

                    thread.start();
                }
            }
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e){
                System.out.println("exeption");
            }

            for(Group gr: groups){
                if(gr.getNumbBombs() < 0){
                    System.out.println("Smth wrong with flags!");
                    Thread.currentThread().interrupt();
                }
            }
        }

     }

}