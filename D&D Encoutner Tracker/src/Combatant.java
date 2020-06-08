public class Combatant
{
    //Variables
    private String name;
    private int initRoll;

    public Combatant()
    {
        name = "";
        initRoll = 0;
    }

    public Combatant(String n, int i)
    {
        name = n;
        initRoll = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInitRoll() {
        return initRoll;
    }

    public void setInitRoll(int initRoll) {
        this.initRoll = initRoll;
    }

    public boolean isSame(Combatant c)
    {
        if(name.equals(c.getName()))
        {
            return true;
        }
        return false;
    }

    public String toString()
    {
        return name + " : " + initRoll;
    }

}
