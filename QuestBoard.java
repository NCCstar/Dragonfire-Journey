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
   private static SparseMatrix<Tile> grid;//game board
   private static final int DIM=60;//unversal size of one grid tile
   private Hero[] players;//array of the player Heroes, .length for number of players
   private int p=0;//player number active
   private int mode=0;//0=board-movement, 1=cards, 2=battle
   private int enemyHP=0;//amount of enemy hp remaining
   private String enemyName=null;//String of the enemy's name - used to randomize enemy hp
   //pre: pNum-number of players, decided by QuestDriver
   //post: creates QuestBoard w/ appropriate values.
   public QuestBoard(int pNum)
   {
      addMouseListener(this);
   
      grid=new SparseMatrix(10,13);
      //add starting tiles: dragon lair
      grid.add(4,6,new Tile(true,new boolean[]{true,true,true,true}));
      grid.add(5,6,new Tile(true,new boolean[]{true,true,true,true}));
      //add starting corners
      grid.add(0,0,new Tile(true,new boolean[]{false,true,true,false}));
      grid.add(0,12,new Tile(true,new boolean[]{false,false,true,true}));
      grid.add(9,0,new Tile(true,new boolean[]{true,true,false,false}));
      grid.add(9,12,new Tile(true,new boolean[]{true,false,false,true}));
      //define players
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
   //pre: dir is direction of movement, 0=up,1=right,2=down,3=left
   //post: moves player, adds tile if needed
   private void newTile(int dir)
   {
      int opp=(dir+2)%4;//opposite direction
      if(grid.get(players[p].getY(),players[p].getX()).getExits()[dir])//if this room has dir exit
      {
         switch(dir)
         {
            case 0:
               players[p].moveY(false);
               break;
            case 1:
               players[p].moveX(true);//move the palyer
               break;
            case 2:
               players[p].moveY(true);
               break;
            default:
               players[p].moveX(false);
               break;
         }
         if(grid.get(players[p].getY(),players[p].getX())==null)//if new space is empty
         {
            //add new random tile - has small chance of infinite while loop.
            grid.add(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),new boolean[]{u.ranB(),u.ranB(),u.ranB(),u.ranB()}));
            while(!grid.get(players[p].getY(),players[p].getX()).getExits()[opp])//while no exit on this side
            {
               grid.get(players[p].getY(),players[p].getX()).rotate();//rotate the square
            }
         }
      }
   
   }
   public void mouseClicked(MouseEvent e)
   {
      if(mode==0)//if drawing main board
      {
         int x=e.getX()/DIM;//get translate coordinates of click
         int y=e.getY()/DIM;
         //curr pos = current position
         if(x==players[p].getX()&&y==players[p].getY()-1)//if clicked above player
         {
            newTile(0);
         }
         if(x==players[p].getX()+1&&y==players[p].getY())//if clicked right of player
         {
            newTile(1);
         }
         if(x==players[p].getX()&&y==players[p].getY()+1)//if clicked below player
         {
            newTile(2);
         }
         if(x==players[p].getX()-1&&y==players[p].getY())//if clicked to left of curr pos
         {
            newTile(3);
         }
         if(grid.get(players[p].getY(),players[p].getX()).isRoom())//if this is not a corridor
         {
            exeCard(drawRoomCard());//draw and execute a room card
            p++;p%=players.length;//next player.
         }//else keep same player
      }
      else
         if(mode==2)//if in battle mode
         {
            
         }
      repaint();
   }
   //pre: card - identity of card to be processed
   //post: 
   private void exeCard(String card)
   {
      u.SOP(card);//DEBUG
      switch(card)
      {
         case "goblin suprise":
         //attack damage
         case "goblin"://if goblin monster
            enemyName="Goblin";
            enemyHP = u.ranI(2,4);
            //transition
            repaint();
            battle();
            break;
         case "orc":
            enemyName="Orc";
            enemyHP = u.ranI(3,5);
            //transition
            repaint();
            battle();
            break;
      }
   }
   private void battle()
   {
      while(players[p].getHP()>0&&enemyHP>0)
      {
         Object[] options = {"Leap Aside","Slash","Mighty Blow"};
         byte exe=0;//0=leap aside, 1=slash, 2=Mighty Blow
         switch((String)JOptionPane.showInputDialog(null,"Choose and action to take.",players[p].getName()+" Vs. "+enemyName,JOptionPane.INFORMATION_MESSAGE, null,options, options[0]))
         {
            case "Slash":
               exe=1;
               break;
            case "Mighty Blow":
               exe=2;
               break;
         }
         byte otherExe=0;
         float ran=(float)Math.random();
         switch(enemyName)
         {
            case "Goblin":
               if(ran<.3)
               {
                  otherExe=2;
               }
               else
                  if(ran<.7)
                  {
                     otherExe=1;
                  }
                  else
                  {
                     otherExe=0;
                  }
               break;
            default:
               if(ran<(1.0/3))
               {
                  otherExe=2;
               }
               else
                  if(ran<(2.0/3))
                  {
                     otherExe=1;
                  }
                  else
                  {
                     otherExe=0;
                  }
               break;
         }
         switch(otherExe)
         {
            case 1:
               JOptionPane.showMessageDialog(null,"The "+enemyName+" slashes!",enemyName,JOptionPane.INFORMATION_MESSAGE);
               break;
            case 2:
               JOptionPane.showMessageDialog(null,"The "+enemyName+" attempts a Mighty Blow",enemyName,JOptionPane.INFORMATION_MESSAGE);
               break;
            default:
               JOptionPane.showMessageDialog(null,"The "+enemyName+" leaps aside!",enemyName,JOptionPane.INFORMATION_MESSAGE);
               break;
         }
         if(exe==otherExe)
         {
            enemyHP--;
            players[p].changeHP(-1);
            JOptionPane.showMessageDialog(null,"1 damage to "+players[p].getName()+" and to the "+enemyName,"Damage!",JOptionPane.INFORMATION_MESSAGE);
         }
         else//0=la,1=sl,2=mb
            if((exe+1)%3==otherExe)
            {
               players[p].changeHP(-1);
               JOptionPane.showMessageDialog(null,"1 damage to "+players[p].getName(),"Damage!",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
               enemyHP--;
               JOptionPane.showMessageDialog(null,"1 damage to the "+enemyName,"Damage!",JOptionPane.INFORMATION_MESSAGE);
            }
         repaint();
      }
   }
   //post: returns random room card
   private String drawRoomCard()
   {
      int ran=u.ranI(0,50);
      if(ran<30)
      {
         return "nothing";
      }
      if(ran<40)
      {
         return "goblin";
      }
      return "orc";
   }
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      if(mode==0)
      {
         for(int r=0;r<grid.numRow();r++)
         {
            for(int c=0;c<grid.numCol();c++)
            {
               drawTile(g,grid.get(r,c),r,c);
               for(int i=0;i<players.length;i++)
               {
                  if(players[i].getX()==c && players[i].getY()==r)
                  {
                     if(i==p)
                        g.setColor(Color.red);
                     else
                        g.setColor(Color.blue);
                     g.fillRect(c*DIM+DIM/3,r*DIM+DIM/3,(int)(DIM-DIM/1.5),(int)(DIM-DIM/1.5));
                     g.setColor(Color.black);
                     g.drawString(""+players[i].getHP(),c*DIM+DIM/3+2,r*DIM+DIM/3+(int)(DIM-DIM/1.5)-2);
                  }
               }
            }
         }
      }
      else
         if(mode==2)
         {
            
         }
      
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