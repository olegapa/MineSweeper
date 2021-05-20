package sweeper;

public enum Box   //перечисление всех видов состояния ячейки
{
    zero,
    num1,
    num2,
    num3,
    num4,
    num5,
    num6,
    num7,
    num8,
    bomb,
    opened,
    closed,
    flagged,
    bombed,
    nobomb,
    advised;

    //здесь храним изображение
    public Object image;

    Box getNextNumberBox ()
    {
        return Box.values() [this.ordinal() + 1];
    } //

    int getNumber ()
    {
        return this.ordinal();
    }


}