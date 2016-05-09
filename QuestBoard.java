import java.util.*;
import javax.swing.*;
import java.awt.*;
public class QuestBoard extends JPanel
{
   private static SparseMatrix<Tile> grid;
   private static final int DIM=75;
   public QuestBoard()
   {
      grid=new SparseMatrix(10,13);
      grid.add(4,6,new Tile(true,new boolean[]{true,true,true,true}));
      grid.add(5,6,new Tile(true,new boolean[]{true,true,true,true}));
      grid.add(0,0,new Tile(true,new boolean[]{false,true,true,false}));
      grid.add(0,12,new Tile(true,new boolean[]{false,false,true,true}));
      grid.add(9,0,new Tile(true,new boolean[]{true,true,false,false}));
      grid.add(9,12,new Tile(true,new boolean[]{true,false,false,true}));
   }
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      for(int r=0;r<grid.numRow();r++)
      {
         for(int c=0;c<grid.numCol();c++)
         {
            drawTile(g,grid.get(r,c),r,c);
         }
      }
   }
   private void drawTile(Graphics g,Tile t,int y,int x)
   {
      if(y==1&&x==1)
      Math.random();
      g.setColor(Color.black);
      g.fillRect(x*DIM,y*DIM,DIM,DIM);//Rect(l,t,wid,hei)
      g.setColor(Color.white);
      if((y==4||y==5)&&(x==6))
         g.setColor(Color.yellow);
      if(t!=null)
      {
         if(t.isRoom())
         {
            g.fillRect(x*DIM+DIM/5,y*DIM+DIM/5,DIM*3/5,DIM*3/5);
         }
         if(t.getExits()[0])
         {
            g.fillRect(x*DIM+DIM*2/5,y*DIM,DIM/5,DIM/2);
         }
         if(t.getExits()[1])
         {
            g.fillRect(x*DIM+DIM/2,y*DIM+DIM*2/5,DIM/2,DIM/5);
         }
         if(t.getExits()[2])
         {
            g.fillRect(x*DIM+DIM*2/5,y*DIM+DIM/2,DIM/5,DIM/2);
         }
         if(t.getExits()[3])
         {
            g.fillRect(x*DIM,y*DIM+DIM*2/5,DIM/2,DIM/5);
         }
      }
   }
}