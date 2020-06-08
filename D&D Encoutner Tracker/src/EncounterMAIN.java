import javax.swing.*;
import java.awt.*;

public class EncounterMAIN
{
    //Public Variables

    public static void main(String[] args)
    {
        //Set up the JFrame
        TrackerFrame eGui = new TrackerFrame();
        eGui.setTitle("D&D Encounter Tracker v" + eGui.VERSION_NAME);
        eGui.setSize(400,400);
        eGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        eGui.setResizable(false);
        eGui.setVisible(true);
    }
}
