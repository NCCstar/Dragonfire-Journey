import javax.swing.*;
public class QuestDriver
{
   private static QuestBoard board;
   public static void main(String[] args)
   {
      board = new QuestBoard(3);
      JFrame frame = new JFrame("Dragonfire Journey");
      frame.setSize(900, 900);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(board);
      frame.setVisible(true);
   }
}