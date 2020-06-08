import java.util.ArrayList;

public class EnemyCombatant extends Combatant
{
    /*
    Use this class to be a combatant, but store more information to open in the enemy manager
     */
    private ArrayList<EnemyHealthToggle> storedToggles = null;
    private int basicHP;
    private int ac;

    //Private variables for EnemyManager

    public EnemyCombatant()
    {
        super();
    }

    public EnemyCombatant(String name, int roll)
    {
        super(name, roll);
    }

    public void sendToEnemyManager(EnemyManager em, String ac, int hp)
    {
        em.setDefaultValues(this.getName(), ac, hp);
        if(storedToggles != null)
        {
            em.setCounters(storedToggles);
        }
    }

    public void storeToggles(ArrayList<EnemyHealthToggle> togs)
    {
        storedToggles = togs;
    }

    public ArrayList<EnemyHealthToggle> sendToggles()
    {
        return storedToggles;
    }

    public int getBasicHP() {
        return basicHP;
    }

    public void setBasicHP(int basicHP) {
        this.basicHP = basicHP;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }
}
