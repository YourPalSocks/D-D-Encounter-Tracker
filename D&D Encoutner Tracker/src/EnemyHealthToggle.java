import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EnemyHealthToggle extends JPanel
{
    private int enemyHP = 0;
    private JLabel hpField;
    public EnemyHealthToggle()
    {
        Initialize();
    }

    public EnemyHealthToggle(int hp)
    {
        enemyHP = hp;
        Initialize();
    }

    public void Initialize()
    {
        //Add minus button
        JButton minusButton = new JButton("-");
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeHpValue(-1);
            }
        });
        this.add(minusButton);
        //Add textField inbetween
        hpField = new JLabel();
        hpField.setText(String.valueOf(enemyHP));
        hpField.setHorizontalAlignment(JTextField.CENTER);
        this.add(hpField, BorderLayout.CENTER);
        //Add plus button
        JButton plusButton = new JButton("+");
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeHpValue(1);
            }
        });
        this.add(plusButton);

        //Action listener to hpField for hpcalculations
        hpField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2)
                {
                    int amt = Integer.parseInt(JOptionPane.showInputDialog("How much hp added (- for reduction)?"));
                    changeHpValue(amt);
                }
            }
        });
    }

    public void changeHpValue(int amt)
    {
        if(enemyHP + amt < 0) //Error check, below 0
        {
            enemyHP = 0;
            hpField.setText(String.valueOf(enemyHP));
            repaint();
            return;
        }

        if(enemyHP + amt > 1000) //Error check, above 999
        {
            enemyHP = 1000;
            hpField.setText(String.valueOf(enemyHP));
            repaint();
            return;
        }

        enemyHP += amt;
        hpField.setText(String.valueOf(enemyHP));
        repaint();
    }
}
