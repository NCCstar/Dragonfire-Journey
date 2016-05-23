import javax.swing.*;
public class QuestDriver
{
   private static QuestBoard board;
   public static void main(String[] args)
   {
      Object[] options = {1,2,3,4};
      board = new QuestBoard(2);
      JFrame frame = new JFrame("Dragonfire Journey");
      frame.setSize((int)(board.getDIM()*13.3), (int)(board.getDIM()*13));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(board);
      frame.setVisible(true);
   }
}