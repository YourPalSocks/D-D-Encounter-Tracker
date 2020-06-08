import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Objects;

public class TrackerFrame extends JFrame
{
    public final String VERSION_NAME = "2.1";
    //Private Variables
    JMenuBar menuBar = new JMenuBar();
    JMenu fileKey = new JMenu("File");
    JMenu editKey = new JMenu("Edit");
    static InitiativeTable initTable;
    static JList<String> charList;

    public TrackerFrame() //Default
    {
        Initialize();
        ImageIcon icon = new ImageIcon(getClass().getResource("dnd.jpg"));
        setIconImage(icon.getImage());
    }

    public void Initialize()
    {
        //region Save&Load
        menuBar.add(fileKey);
        menuBar.add(editKey);
        JMenuItem saveKey = new JMenuItem("Save");
        saveKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SaveFile();
            }
        });
        JMenuItem loadKey = new JMenuItem("Load");
        loadKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                LoadFile();
            }
        });
        fileKey.add(saveKey);
        fileKey.add(loadKey);

        //Edit key
        JMenuItem clearKey = new JMenuItem("Clear");
        clearKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClearItem();
            }
        });
        editKey.add(clearKey);
        add(menuBar, BorderLayout.NORTH);
        //endregion

        //region Character Selector
        JPanel LeftPanel = new JPanel();
        LeftPanel.setPreferredSize(new Dimension(110,300));
        LeftPanel.setBorder(BorderFactory.createTitledBorder("Characters"));
        //Add in the JList
        DefaultListModel<String> lModel = new DefaultListModel<>();
        charList = new JList<String>(lModel);
        LeftPanel.add(charList);
        charList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(LeftPanel, BorderLayout.WEST);
        //Add button at bottom of this panel to allow to create another JList item
        JPanel buttonPanel = new JPanel();
        LeftPanel.add(buttonPanel, BorderLayout.SOUTH);
        JButton createChar = new JButton("Add");
        createChar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try { //Error catch, empty name (CANCEL BUTTON)
                    charList.setModel(addCharacter((DefaultListModel) charList.getModel()));
                    repaint();
                }
                catch (NullPointerException n)
                {
                    System.out.println(n);
                }
            }
        });
        JButton removeChar = new JButton("Remove At");
        removeChar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int selected = charList.getSelectedIndex();
                charList.setModel(removeCharacter((DefaultListModel) charList.getModel(), selected));
            }
        });
        buttonPanel.setLayout(new GridLayout(0,1));
        buttonPanel.add(createChar);
        buttonPanel.add(removeChar);
        //endregion

        //region Initiative Counter
        //Create right panel
        JPanel RightPanel = new JPanel();
        RightPanel.setBorder(BorderFactory.createTitledBorder("Initiative Tracker"));
        RightPanel.setPreferredSize(new Dimension(150,320));
        //Add in the table to the scrollpane
        initTable = new InitiativeTable(new DefaultTableModel());
        add(RightPanel, BorderLayout.EAST);
        RightPanel.add(initTable, BorderLayout.NORTH);
        //Add actionlistener to JList to transfer over to initiative order
        charList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() == 2)
                {
                    //Transfer to JTable
                    initTable.addToInitiative(charList.getSelectedValue());
                    repaint();
                }
            }
        });
        //Remove button
        JButton removeInit = new JButton("Remove At");
        RightPanel.add(removeInit, BorderLayout.SOUTH);
        removeInit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                initTable.removeAt(initTable.getSelectedRow());
            }
        });
        //Next character button
        JButton changeInit = new JButton("Next Turn");
        add(changeInit);
        changeInit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                initTable.changeCurInit();
            }
        });
        pack();
    }

    public DefaultListModel addCharacter(DefaultListModel curMod)
    {
        DefaultListModel<String> newMod = new DefaultListModel<>();
        //Add current elements
        for(int i = 0; i < curMod.getSize(); i++)
        {
            newMod.addElement(curMod.getElementAt(i).toString());
        }
        String charName = JOptionPane.showInputDialog("Input Character Name");
        if(charName.isEmpty())
        {
            return null;
        }
        newMod.addElement(charName);
        return newMod;
    }

    public DefaultListModel addCharacter(DefaultListModel curMod, String name)
    {
        DefaultListModel<String> newMod = new DefaultListModel<>();
        //Add current elements
        for(int i = 0; i < curMod.getSize(); i++)
        {
            newMod.addElement(curMod.getElementAt(i).toString());
        }
        newMod.addElement(name);
        return newMod;
    }

    public DefaultListModel removeCharacter(DefaultListModel curMod, int index)
    {
        DefaultListModel<String> newMod = new DefaultListModel<>();
        //Add all elements that aren't this one
        for(int i = 0; i < curMod.getSize(); i++)
        {
            if(i != index)
            {
                newMod.addElement(curMod.getElementAt(i).toString());
            }
        }
        return newMod;
    }

    //Save and Load features
    public void SaveFile()
    {
        //Get file name
        String name = ChangeFilePath(true);

        //Error check
        if(name == null)
        {
           return;
        }

        try
        {
            //Check if need to overwrite
            if(new File(name).exists())
            {
                String[] options = {"Yes", "No"};
                int i = JOptionPane.showOptionDialog(null, "Overwrite file?", "Overwrite", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if(i == 1)
                {
                    return;
                }
            }
            //Open file
            FileWriter writer = new FileWriter(new File(name));
            //Write to file
            for(int i = 0; i < charList.getModel().getSize(); i++)
            {
                //Check if has arrow
                if(charList.getModel().getElementAt(i).contains("<-"))
                {
                    writer.write(charList.getModel().getElementAt(i).replaceAll("<-", "") + "\n");
                }
                else
                {
                    writer.write(charList.getModel().getElementAt(i) + "\n");
                }
            }
            writer.close();
            JOptionPane.showMessageDialog(null, "Save complete");
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "IOException occurred", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
            return;
        }
    }

    public void LoadFile()
    {
        String fp = ChangeFilePath(false);
        //Check for null
        if(fp == null)
        {
            return;
        }

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fp)));
            String line = "";
            while((line = reader.readLine()) != null)
            {
                charList.setModel(addCharacter((DefaultListModel) charList.getModel(), line));
                repaint();
            }
            reader.close();
        }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(null, "Error reading file", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
            return;
        }
    }

    private String ChangeFilePath(boolean isSave)
    {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ENC Files", "enc");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Select a folder");
        int userSelection = 0;
        if(!isSave)
        {
            userSelection = fileChooser.showOpenDialog(this); //Open
        }
        else
        {
            userSelection = fileChooser.showSaveDialog(this); //Save
        }


        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            return fileToSave.getAbsolutePath();
        }
        return null;
    }

    public void ClearItem()
    {
        String[] options = {"JList", "JTable", "Both", "Cancel"};
        int i = JOptionPane.showOptionDialog(null, "Clear what?", "Clear", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        switch(i)
        {
            case 0: //Clear JList
                charList.setModel(new DefaultListModel<String>());
                break;

            case 1: //Clear JTable
                initTable.clearContents();
                break;

            case 2: //Clear both
                charList.setModel(new DefaultListModel<String>());
                initTable.clearContents();
                break;
        }
        this.repaint();
    }
}
