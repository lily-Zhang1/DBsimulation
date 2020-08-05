/**
 * use commad
 *
 * @author lily
 * */
public class UseCommand extends Command
{
    private String[] input;

    public UseCommand(String[] input)
    {
        this.input = input;
    }

    /** Parsing grammar is correct or not */
    public boolean parser()
    {
        if (input.length != 2) { return false; }
        return true;
    }

}
