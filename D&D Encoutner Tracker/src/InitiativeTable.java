import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class InitiativeTable extends JTable
{
    //For sorting, storing, and whatnot
    private static ArrayList<Combatant> players = new ArrayList<Combatant>();
    private static JPopupMenu popMenu;
    int currentTurn = 0;

    public InitiativeTable(TableModel tab)
    {
        players = new ArrayList<Combatant>();
        ((DefaultTableModel)tab).setRowCount(0);
        ((DefaultTableModel)tab).setColumnCount(1);
        setPreferredSize(new Dimension(80,200));
        setModel(tab);
        //Add init count 20
        players.add(new Combatant("Count 20", 20));
        sortPlayers();
        setupPopupMenu();
        this.repaint();
    }

    private void setupPopupMenu() //Sets up the JPopupMenu for misc. click events
    {
        JMenuItem enemyOpener = new JMenuItem("Open in Enemy Manager");
        enemyOpener.setVisible(false);
        JMenuItem rollOpener = new JMenuItem("Show roll #");
        popMenu = new JPopupMenu();
        popMenu.add(enemyOpener);
        popMenu.add(rollOpener);
        this.addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent me)
            {
                if(SwingUtilities.isRightMouseButton(me) && getSelectedRow() > -1)
                {
                    popMenu.show(me.getComponent(), me.getX(), me.getY());
                    if((players.get(getSelectedRow()) instanceof EnemyCombatant)) //Error check: selected not enemy
                    {
                        enemyOpener.setVisible(true);
                    }
                    else
                    {
                        enemyOpener.setVisible(false);
                    }
                }
            }
        });
        //Action listeners
        rollOpener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                showInitRoll();
            }
        });
        enemyOpener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Check if already filled
                EnemyCombatant temp = (EnemyCombatant) players.get(getSelectedRow());
                EnemyManager em;
                if(temp.sendToggles() == null) //If no toggles -FIRST TIME OPEN-
                {
                    //Variables for EnemyManager
                    int defHp = Integer.parseInt(JOptionPane.showInputDialog("Enter average enemy hp"));
                    int ac = Integer.parseInt(JOptionPane.showInputDialog("Enter enemy AC"));
                    temp.setAc(ac);
                    temp.setBasicHP(defHp);
                    em = new EnemyManager(temp);
                    temp.sendToEnemyManager(em, String.valueOf(ac), defHp);
                    return;
                }
                else
                {
                    //Send to manager
                    em = new EnemyManager(temp);
                    temp.sendToEnemyManager(em, String.valueOf(temp.getAc()), temp.getBasicHP());
                }
            }
        });
    }

    //Functions
    public void addToInitiative(String toAdd)
    {
        //Double check for duplication
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).getName().equals(toAdd))
            {
                return;
            }
        }
        try
        { //Test for null variable entries, just in case
            int initRoll = -1;
            while (initRoll < 0)
            {
                String num = JOptionPane.showInputDialog(null, "Enter Initiative roll:");
                if (!num.isEmpty())
                {
                    initRoll = Integer.parseInt(num);
                }
                else //Vibe check part 2
                {
                    throw new NullPointerException();
                }
            }
            //Check if this is player or enemy
            Combatant c;
            String[] options = {"player", "enemy"};
            int choice = JOptionPane.showOptionDialog(null, "Is this an enemy or player?", "Type Selection", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if(String.valueOf(choice).isEmpty()) //Vibe check
            {
                throw new NullPointerException();
            }
            if (choice == 0) //Make enemy or player
            {
                c = new Combatant(toAdd, initRoll);
            }
            else
            {
                c = new EnemyCombatant(toAdd, initRoll);
            }

            players.add(c);
            sortPlayers();
        }
        catch (NullPointerException n)
        {
            return;
        }
    }

    public void removeAt(int row)
    {
        if(getValueAt(row, 0).toString().equals("Count 20"))
        {
            JOptionPane.showMessageDialog(null, "Cannot remove count 20", "Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        DefaultTableModel mod = (DefaultTableModel) getModel();
        mod.removeRow(row);
        players.remove(row);
    }

    public void changeCurInit()
    {
        //Error check
        if(getModel().getRowCount() <= 0)
        {
            return;
        }

        if(currentTurn >= getModel().getRowCount() - 1) //Reset turn order (back to top)
        {
            String fixedName = getValueAt(currentTurn, 0).toString().replaceAll("<-", "");
            setValueAt(fixedName, currentTurn, 0);
            currentTurn = 0;
            setValueAt(getValueAt(currentTurn,0) + "<-", currentTurn, 0);

        }
        else
        {
            String fixedName = getValueAt(currentTurn, 0).toString().replaceAll("<-", "");
            setValueAt(fixedName, currentTurn, 0);
            currentTurn++;
            setValueAt(getValueAt(currentTurn,0) + "<-", currentTurn, 0);
        }
    }

    private void sortPlayers() //Sorts and place players
    {
        //Store who's turn it is for later
        String turnName = getSelectedPlayerName();
        //Sort function to rearrange arraylist
        Combatant temp;
        for(int x = 0; x < players.size(); x++)
        {
            for(int y = 0; y < players.size(); y++)
            {
                if(players.get(y).getInitRoll() < players.get(x).getInitRoll())
                {
                    temp = players.get(x);
                    players.set(x, players.get(y));
                    players.set(y, temp);
                }
            }
        }
        //place into table
        DefaultTableModel newMod = new DefaultTableModel();
        newMod.setRowCount(players.size());
        newMod.setColumnCount(1);

        for(int i = 0; i < newMod.getRowCount(); i++)
        {
            newMod.setValueAt(players.get(i).getName(), i, 0);

            if(newMod.getValueAt(i, 0).equals(turnName) && !turnName.isEmpty() && players.size() > 2) //Add arrow
            {
                currentTurn = i;
                newMod.setValueAt(newMod.getValueAt(i,0) + "<-", i,0);
            }
        }

        if (turnName.isEmpty()) //No other players
        {
            newMod.setValueAt(newMod.getValueAt(0,0) + "<-", 0, 0);
            currentTurn = 0;
        }

        if(!turnName.isEmpty() && players.size() <= 2) //Only two players, arrow at highest
        {
            newMod.setValueAt(newMod.getValueAt(0,0) + "<-", 0, 0);
            currentTurn = 0;
        }
        this.setModel(newMod);
        repaint();
    }

    @Override
    public boolean isCellEditable(int rows, int cols)
    {
        return false;
    }

    public void clearContents()
    {
        DefaultTableModel mod = (DefaultTableModel) getModel();
        for(int i = 0; i < mod.getRowCount(); i++) //Clear table
        {
            mod.removeRow(i);
        }
        players.clear(); //Clear arraylist
        //Add init 20 back to the roll
        Combatant countTwenty = new Combatant("Count 20<-", 20);
        currentTurn = 0;
        mod.setRowCount(1);
        mod.setValueAt(countTwenty.getName(), 0, 0);
        players.add(countTwenty);
        repaint();
    }

    private void showInitRoll()
    {
        //Error check
        if(getSelectedRow() == -1)
        {
            return;
        }
        String name = players.get(getSelectedRow()).getName();
        JOptionPane.showMessageDialog(null, "Rolled: " + players.get(getSelectedRow()).getInitRoll(), name, JOptionPane.PLAIN_MESSAGE);
    }

    private String getSelectedPlayerName()
    {
        for(int i = 0; i < getRowCount(); i++)
        {
            if(getValueAt(i, 0).toString().contains("<-"))
            {
                return getValueAt(i, 0).toString().replaceAll("<-", "");
            }
        }
        return "";
    }
}
