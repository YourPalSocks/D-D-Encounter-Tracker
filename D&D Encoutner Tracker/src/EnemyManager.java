import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class EnemyManager extends JFrame
{
    private JTextField numEnemies;
    private JLabel enemyType;
    private JLabel armorClass;
    private JScrollPane rightScroll;
    private final static Font buttonFont = new Font("Arial", Font.PLAIN, 25);
    private int enemiesInHoard = 0;
    private ArrayList<EnemyHealthToggle> toggles = new ArrayList<EnemyHealthToggle>(); //Keep track of things
    private int defaultHP = 0;

    EnemyCombatant thisEm;

    public EnemyManager(EnemyCombatant em) //Default
    {
        Initialize();
        thisEm = em;
    }

    public EnemyManager(EnemyCombatant em, String name, String ac, int defHp) //Fill
    {
        Initialize();
        thisEm = em;
        setEnemyName(name);
        setEnemyAC(ac);
        defaultHP = defHp;
    }

    public void setDefaultValues(String name, String ac, int defHp)
    {
        setEnemyName(name);
        setEnemyAC(ac);
        defaultHP = defHp;
    }

    public void Initialize()
    {
        this.setName("Enemy Manager");
        this.setSize(300,300);
        this.setResizable(false);
        this.setVisible(true);
        //region RightPanel
        JPanel rightPanel = new JPanel();
        rightPanel.setBorder(BorderFactory.createTitledBorder("Enemies"));
        rightPanel.setPreferredSize(new Dimension(150,290));
        rightScroll = new JScrollPane();
        rightScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightScroll.setPreferredSize(new Dimension(135, 230));
        rightScroll.setSize(rightPanel.getSize());
        rightPanel.add(rightScroll);
        this.add(rightPanel, BorderLayout.EAST);
        //endregion

        //region left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(5,3));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Info."));
        leftPanel.setPreferredSize(new Dimension(130, 290));
        enemyType = new JLabel();
        enemyType.setText("NAME: GOBLINS");
        armorClass = new JLabel();
        armorClass.setText("AC: 13");
        leftPanel.add(enemyType);
        leftPanel.add(armorClass);
        this.add(leftPanel, BorderLayout.WEST);
        //Add buttons and textarea
        JButton plusButton = new JButton("+");
        plusButton.setFont(buttonFont);
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEnemyCoutner(1);
            }
        });
        leftPanel.add(plusButton);
        numEnemies = new JTextField();
        numEnemies.setHorizontalAlignment(JTextField.CENTER);
        numEnemies.setText("0");
        numEnemies.setEditable(false);
        leftPanel.add(numEnemies);
        JButton minusButton = new JButton("-");
        minusButton.setFont(buttonFont);
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEnemyCoutner(-1);
            }
        });
        leftPanel.add(minusButton);
        //endregion
        revalidate();
        repaint();

        //Add event to closing window
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                thisEm.storeToggles(toggles);
            }
        });
    }

    public void setEnemyName(String n)
    {
        enemyType.setText("NAME: " + n);
        repaint();
    }

    public void setEnemyAC(String a)
    {
        armorClass.setText("AC:" + a);
        repaint();
    }

    public void setCounters(ArrayList<EnemyHealthToggle> togs)
    {
        //Convert them over
        toggles = togs;
        enemiesInHoard = togs.size();
        numEnemies.setText(String.valueOf(enemiesInHoard));
        rightScroll.setViewportView(updateViewportView());
        repaint();
    }

    public void updateEnemyCoutner(int change)
    {
        if(enemiesInHoard + change < 0 || enemiesInHoard + change > 50) //Error check: negative or too many enemies
        {
            return;
        }
        enemiesInHoard += change;
        numEnemies.setText(String.valueOf(enemiesInHoard));
        //Create or Remove HealthToggle
        if(change > 0) //Addition
        {
            EnemyHealthToggle newTog = new EnemyHealthToggle(defaultHP);
            toggles.add(newTog);
        }
        else if (change < 0) //Reduction
        {
            toggles.remove(toggles.size() - 1); //Removes last added
        }
        rightScroll.setViewportView(updateViewportView());
        repaint();
    }

    private JPanel updateViewportView()
    {
        JPanel toRet = new JPanel();
        toRet.setLayout(new GridLayout(toggles.size(),1));
        //Add all contents of toggles
        for(EnemyHealthToggle tg: toggles)
        {
            toRet.add(tg);
        }
        return toRet;
    }

    /*
    ==============================================================================
                                    REMOVE ME LATER
                                 TEMPORARY TESTER METHOD
    ==============================================================================
     */

    public static void main(String[] args)
    {
        EnemyManager em = new EnemyManager(null);
        em.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
