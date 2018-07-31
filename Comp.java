package Bot.Bot;
/*
 * Modified by Sidney Tan
 */
import java.util.Comparator;


public class Comp<E> implements Comparator<E>
{
    public int compare(E x, E y)
    {
        return ((Comparable<E>) x).compareTo(y);
    }
}
