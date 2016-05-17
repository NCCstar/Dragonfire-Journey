/*
todo short: cards: room-, combat
todo long: room rand
todo maybe: music/sounds
*/
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
public class QuestBoard extends JPanel implements MouseListener
{
   private static SparseMatrix<Tile> grid;
   private static final int DIM=50;
   private Hero[] players;
   private int p=0;
   private int mode=0;//0=board-movement, 1=cards, 2=battle
   public QuestBoard(int pNum)
   {
      addMouseListener(this);
   
      grid=new SparseMatrix(10,13);
      
      grid.add(4,6,new Tile(true,new boolean[]{true,true,true,true}));
      grid.add(5,6,new Tile(true,new boolean[]{true,true,true,true}));
      grid.add(0,0,new Tile(true,new boolean[]{false,true,true,false}));
      grid.add(0,12,new Tile(true,new boolean[]{false,false,true,true}));
      grid.add(9,0,new Tile(true,new boolean[]{true,true,false,false}));
      grid.add(9,12,new Tile(true,new boolean[]{true,false,false,true}));
      
      players=new Hero[pNum];
      for(int i=0;i<players.length;i++)
      {
         //choose players - switch
         switch(i)
         {
            case 1:
               players[i]=new SteveBob(12,0);
               break;
            case 2:
               players[i]=new SteveBob(12,9);
               break;
            case 3:
               players[i]=new SteveBob(0,9);
               break;
            default:
               players[i]=new SteveBob(0,0);
               break;
         }
      }
   }
   
   public void mouseClicked(MouseEvent e)
   {
      int x=e.getX()/DIM;
      int y=e.getY()/DIM;
      if(x<13&&y<10)
         if(e.getButton()==MouseEvent.BUTTON1)
         {
            grid.add(y,x,new Tile(false,new boolean[]{u.ranB(.6),u.ranB(.6),u.ranB(.6),u.ranB(.6)}));
         }
         else
         {
            grid.get(y,x).rotate();
         }
         
      if(x==0&&y==11)//left
      {
         if(grid.get(players[p].getY(),players[p].getX()).getExits()[3])
         {
            players[p].moveX(false);
            if(grid.get(players[p].getY(),players[p].getX())==null)
            {
               grid.add(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               while(!grid.get(players[p].getY(),players[p].getX()).getExits()[1])
               {
                  grid.set(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               }
            }
            drawRoomCard(); 
            p++;p%=players.length;
         }
      }
      if(x==1&&y==10)//up
      {
         if(grid.get(players[p].getY(),players[p].getX()).getExits()[0])
         {
            players[p].moveY(false);
            if(grid.get(players[p].getY(),players[p].getX())==null)
            {
               grid.add(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               while(!grid.get(players[p].getY(),players[p].getX()).getExits()[2])
               {
                  grid.set(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               }
            }
            drawRoomCard(); 
            p++;p%=players.length;
         }
      }
      if(x==2&&y==11)//right
      {
         if(grid.get(players[p].getY(),players[p].getX()).getExits()[1])
         {
            players[p].moveX(true);
            if(grid.get(players[p].getY(),players[p].getX())==null)
            {
               grid.add(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               while(!grid.get(players[p].getY(),players[p].getX()).getExits()[3])
               {
                  grid.set(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               }
            }
            drawRoomCard(); 
            p++;p%=players.length;
         }
      }
      if(x==1&&y==12)//down
      {
         if(grid.get(players[p].getY(),players[p].getX()).getExits()[2])
         {
            players[p].moveY(true);
            if(grid.get(players[p].getY(),players[p].getX())==null)
            {
               grid.add(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               while(!grid.get(players[p].getY(),players[p].getX()).getExits()[0])
               {
                  grid.set(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
               }
            }
            drawRoomCard();   
            p++;p%=players.length;
         }
      }
      repaint();
   }
   private void drawRoomCard()
   {
      Card drawn = Card.ranRoom();
   }
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      
      for(int r=0;r<grid.numRow();r++)
      {
         for(int c=0;c<grid.numCol();c++)
         {
            drawTile(g,grid.get(r,c),r,c);
            for(int i=0;i<players.length;i++)
            {
               if(players[i].getX()==c && players[i].getY()==r)
               {
                  g.setColor(Color.red);
                  g.fillRect(c*DIM+DIM/3,r*DIM+DIM/3,(int)(DIM-DIM/1.5),(int)(DIM-DIM/1.5));
               }
            }
         }
      }
      
      g.setColor(Color.blue);
      g.fillRect(0,DIM*11,DIM,DIM);
      g.fillRect(DIM,DIM*10,DIM,DIM);
      g.fillRect(DIM*2,DIM*11,DIM,DIM);
      g.fillRect(DIM,DIM*12,DIM,DIM);
      
      g.drawString(p+1+"",0,DIM*10+DIM/2);
   }
   private void drawTile(Graphics g,Tile t,int y,int x)
   {
      
      if(y==10&&x==0)
         Math.random();
      g.setColor(Color.black);
      g.fillRect(x*DIM,y*DIM,DIM,DIM);//Rect(l,t,wid,hei)
      g.setColor(Color.white);
      if((y==4||y==5)&&(x==6))
         g.setColor(Color.yellow);
      if((x==0&&y==0)||(x==0&&y==9)||(x==12&&y==9)||(x==12&&y==0))
         g.setColor(Color.yellow.darker());
      if(t!=null)
      {
         //draw center
         g.fillRect(x*DIM+DIM/3,y*DIM+DIM/3,DIM/3,DIM/3);
         if(t.isRoom())
         {
            //draw 4 corners
            g.fillRect(x*DIM+DIM/6,y*DIM+DIM/6,DIM/6+1,DIM/6+1);
            g.fillRect(x*DIM+DIM/6,y*DIM+DIM*2/3,DIM/6+1,DIM/6);
            g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/6,DIM/6,DIM/6+1);
            g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM*2/3,DIM/6,DIM/6);
            
            if(t.getExits()[0])
            {
               g.fillRect(x*DIM+DIM/3,y*DIM,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM/3,y*DIM+DIM/6,DIM/3,DIM/6+1);
            }
            if(t.getExits()[1])
            {
               g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/6,DIM/3);
            }
            if(t.getExits()[2])
            {
               g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/6);
            }
            if(t.getExits()[3])
            {
               g.fillRect(x*DIM,y*DIM+DIM/3,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM/6,y*DIM+DIM/3,DIM/6+1,DIM/3);
            }
         }
         if(t.getExits()[0])
         {
            g.fillRect(x*DIM+DIM/3,y*DIM,DIM/3,DIM/3);
         }
         else
         {
            //g.fillRect(x*DIM+DIM/3,y*DIM+DIM/6,DIM/3,DIM/6+1);
         }
         if(t.getExits()[1])
         {
            g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/3,DIM/3);
         }
         else
         {
            //g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/6,DIM/3);
         }
         if(t.getExits()[2])
         {
            g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/3);
         }
         else
         {
            //g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/6);
         }
         if(t.getExits()[3])
         {
            g.fillRect(x*DIM,y*DIM+DIM/3,DIM/3,DIM/3);
         }
         else
         {
            //g.fillRect(x*DIM+DIM/6,y*DIM+DIM/3,DIM/6+1,DIM/3);
         }
      }
   }
   public void mouseDragged( MouseEvent e){}
   public void mouseExited( MouseEvent e ){}
   public void mousePressed( MouseEvent e ){}
   public void mouseReleased( MouseEvent e ){}
   public void mouseEntered( MouseEvent e ){}
   public void mouseMoved( MouseEvent e){}
}