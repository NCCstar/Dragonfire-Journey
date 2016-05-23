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
   private static final byte DIM=60;//unversal size of one grid tile
   private Hero[] players;//array of the player Heroes, .length for number of players
   private int p=0;//player number active
   private boolean secTun=false;
   private byte dragonLeft = 8;//dragon cards left
   public int getDIM(){
      return DIM;}
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
            //add new random tile
            boolean[] doors;
            do
            {
               doors = new boolean[]{u.ranB(.6),u.ranB(.6),u.ranB(.6),u.ranB(.6)};//re-random
            }while(!(doors[0]||doors[1]||doors[2]||doors[3]));//while not at least one exit
            if(oneTrue(doors))
               grid.add(players[p].getY(),players[p].getX(),new Tile(true,doors));
            else
               grid.add(players[p].getY(),players[p].getX(),new Tile(u.ranB(.9),doors));//add tile
            
            while(!grid.get(players[p].getY(),players[p].getX()).getExits()[opp])//while no exit on this side
            {
               grid.get(players[p].getY(),players[p].getX()).rotate();//rotate the tile
            }
         }
      }
   }
   private boolean oneTrue(boolean[] array)
   {
      int count=0;
      for(boolean bool:array)
      {
         if(bool)
            count++;
      }
      return count==1;
   }
   public void mouseClicked(MouseEvent e)
   {
      int x=e.getX()/DIM;//get translate coordinates of click
      int y=e.getY()/DIM;
         //curr pos = current position
      if(x>=0&&x<13&&y>=0&&y<10)
      {
         boolean legit=false;//should change turn
         if(x==players[p].getX()&&y==players[p].getY()&&grid.get(y,x).canSearch())
         {
            if(x==6&&(y==4||y==5))
               exeCard("dragon");
            else
            {
               exeCard(drawCard((byte)1));
               grid.get(y,x).search();
            }
            p=(p+1)%players.length;
         }
         if(x==players[p].getX()&&y==players[p].getY()-1)//if clicked above player
         {
            newTile(0);
            legit=true;
         }
         if(x==players[p].getX()+1&&y==players[p].getY())//if clicked right of player
         {
            newTile(1);
            legit=true;
         }
         if(x==players[p].getX()&&y==players[p].getY()+1)//if clicked below player
         {
            newTile(2);
            legit=true;
         }
         if(x==players[p].getX()-1&&y==players[p].getY())//if clicked to left of curr pos
         {
            newTile(3);
            legit=true;
         }
         if(grid.get(players[p].getY(),players[p].getX()).isRoom()&&legit)//if this is not a corridor
         {
            if(x==6&&(y==4||y==5))
               exeCard("dragon");
            else
               exeCard(drawCard((byte)0));//draw and execute a room card
            p=(p+1)%players.length;//next player.
         }//else keep same player
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
            //transition
            repaint();
            battle("Goblin",(byte)u.ranI(2,4));
            break;
         case "orc":
            //transition
            repaint();
            battle("Orc",(byte)u.ranI(3,5));
            break;
         case "dragon":
            if(u.ranI(0,dragonLeft)==0)
            {
               JOptionPane.showMessageDialog(null,"The dragon awakes!","ROAAAR!!",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(u.ranI(-10,-20));
            }
            else
            {
               dragonLeft--;
               int gold = u.ranI(10,100)*100;
               JOptionPane.showMessageDialog(null,"You snatch "+gold+"G of the dragon's treasure.","ZZzzzz",JOptionPane.INFORMATION_MESSAGE);
               players[p].getBag().add(gold+"G");
            }
            break;
         case "gold":
            int gold = u.ranI(1,90)*10;
            JOptionPane.showMessageDialog(null,"You find gold.","Eureka!",JOptionPane.INFORMATION_MESSAGE);
            players[p].getBag().add(gold+"G");
            break;
         case "secret tunnel":
            JOptionPane.showMessageDialog(null,"You find a secret passage. Move to an adjecent space.","Secret Tunnel!",JOptionPane.INFORMATION_MESSAGE);
            
            break;
         case "nothing":
            JOptionPane.showMessageDialog(null,"Nothing happens.","Phew",JOptionPane.INFORMATION_MESSAGE); 
            secTun=true;
            break;
      }
   }
   private void battle(String enemyName,byte enemyHP)
   {
      while(players[p].getHP()>0&&enemyHP>0)
      {
         Object[] options = {"Leap Aside","Slash","Mighty Blow"};
         byte exe=0;//0=leap aside, 1=slash, 2=Mighty Blow
         try
         {
            switch((String)JOptionPane.showInputDialog(null,"Choose an action to take.",players[p].getName()+" Vs. "+enemyName,JOptionPane.INFORMATION_MESSAGE, null,options, options[0]))
            {
               case "Slash":
                  exe=1;
                  break;
               case "Mighty Blow":
                  exe=2;
                  break;
            }
         }
         catch(Exception e)
         {
            exe=0;
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
               if(exe==2)
               {
                  enemyHP-=2;
                  JOptionPane.showMessageDialog(null,"2 damage to the "+enemyName,"Uber-Damage!",JOptionPane.INFORMATION_MESSAGE);
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
   private String drawCard(byte type)//0=room,1==search
   {
      if(type==0)
      {
         int ran=u.ranI(0,45);
         if(ran<35)
         {
            return "nothing";
         }
         if(ran<40)
         {
            return "goblin";
         }
         return "orc";
      }
      if(type==1)
      {
         int ran=u.ranI(0,45);
         if(ran<35)
         {
            return "nothing";
         }
         if(ran<40)
         {
            return "secret tunnel";
         }
         return "gold";
      }
      return "";
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
                  switch(i)
                  {
                     case 0:
                        g.setColor(Color.red);
                        break;
                     case 1:
                        g.setColor(Color.blue);
                        break;
                     case 2:
                        g.setColor(Color.green);
                        break;
                     case 3:
                        g.setColor(Color.orange);
                        break;
                  }
                  if(i!=p)
                     g.setColor(g.getColor().darker());
                  g.fillRect(c*DIM+DIM/3,r*DIM+DIM/3,(int)(DIM-DIM/1.5),(int)(DIM-DIM/1.5));
                  g.setColor(Color.black);
                  g.drawString(""+players[i].getHP(),c*DIM+DIM/3+2,r*DIM+DIM/3+(int)(DIM-DIM/1.5)-2);
               }
            }
         }
      }
      drawPlayers(g);
   }
   private void drawPlayers(Graphics g)
   {
      for(int i=0;i<players.length;i++)
      {
         switch(i)
         {
            case 0:
               g.setColor(Color.red);
               break;
            case 1:
               g.setColor(Color.blue);
               break;
            case 2:
               g.setColor(Color.green);
               break;
            case 3:
               g.setColor(Color.orange);
               break;
         }
         if(i==p)
         {
            Color temp=g.getColor();
            g.setColor(Color.yellow);
            g.fillRect(i*DIM*2,DIM*12,DIM*2,DIM/2);
            g.setColor(temp);
         }
         else
            g.setColor(g.getColor().darker());
         g.fillRect(i*DIM*2,DIM*10,DIM*2,DIM*2);
         g=setTextColor(g);
         g.drawString("HP:"+players[i].getHP(),i*DIM*2+5,DIM*11);
         g.drawString("Bag:"+players[i].getBag(),i*DIM*2+5,DIM*11+11);
      }
   }
   private Graphics setTextColor(Graphics x)
   {
      int r=x.getColor().getRed();
      int g=x.getColor().getGreen();
      int b=x.getColor().getBlue();
      r=255-r;
      g=255-g;
      b=255-b;
      x.setColor(new Color(r,g,b));
      return x;
   }
   private void drawTile(Graphics g,Tile t,int y,int x)
   {
      if(y==10&&x==0)
         Math.random();
      g.setColor(Color.black);
      g.fillRect(x*DIM,y*DIM,DIM,DIM);//Rect(l,t,wid,hei)
      if(grid.get(y,x)!=null&&grid.get(y,x).canSearch())
         g.setColor(Color.white);
      else
         g.setColor(Color.white.darker());
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