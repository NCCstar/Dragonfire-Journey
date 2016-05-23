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
   private boolean secTun=false;//if in secret tunnel mode
   private byte dragonLeft = 8;//dragon cards left
   private byte sunLeft=27;
   public byte getDIM(){
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
      if(grid.get(players[p].getY(),players[p].getX()).getExits()[dir]||secTun)//if this room has dir exit or in secTun mode
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
   //post: returns true if one and only one in array is true
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
      if(sunLeft>0)
      {
         int x=e.getX()/DIM;//get translate coordinates of click
         int y=e.getY()/DIM;
         //curr pos = current position
         if(x>=0&&x<13&&y>=0&&y<10)
         {
            boolean legit=false;//if should change turn
            do//loop to break out of
            {
               if(x==players[p].getX()&&y==players[p].getY()&&grid.get(y,x).canSearch())//if searching
               {
                  if(x==6&&(y==4||y==5))
                     exeCard("dragon");//if in dragon room, get dragon loot
                  else
                  {
                     exeCard(drawCard((byte)1));//random search card
                     grid.get(y,x).search();//increment tile's search
                  }
                  if(!secTun)
                     legit=true;
                  break;//don't check the rest
               }
               if(x==players[p].getX()&&y==players[p].getY()&&((x==0&&y==0)||(x==12&&y==0)||(x==0&&y==9)||(x==12&&y==9)))
               {
                  legit=true;
                  break;
               }
               if(x==players[p].getX()&&y==players[p].getY()-1)//if clicked above player
               {
                  newTile(0);//go up
                  if(secTun)
                  {//un-secret tunnel
                     secTun=false;
                  }
                  legit=true;//next turn
               }
               if(x==players[p].getX()+1&&y==players[p].getY())//if clicked right of player
               {
                  newTile(1);//go right
                  if(secTun)
                  {//un-secret tunnel
                     secTun=false;
                  }
                  legit=true;//next turn
               }
               if(x==players[p].getX()&&y==players[p].getY()+1)//if clicked below player
               {
                  newTile(2);//go down
                  if(secTun)
                  {//un-secret tunnel
                     secTun=false;
                  }
                  legit=true;//next turn
               }
               if(x==players[p].getX()-1&&y==players[p].getY())//if clicked to left of curr pos
               {
                  newTile(3);//go left
                  if(secTun)
                  {//un-secret tunnel
                     secTun=false;
                  }
                  legit=true;//next turn
               }
               repaint();
               if(x==players[p].getX()&&y==players[p].getY())
                  if(grid.get(players[p].getY(),players[p].getX()).isRoom())//if this is not a corridor
                  {
                     if(x==6&&(y==4||y==5))
                        exeCard("dragon");//if in chamber, dragon card
                     else
                        exeCard(drawCard((byte)0));//draw and execute a room card
                  }
                  else
                     if(!grid.get(players[p].getY(),players[p].getX()).isRoom())//if corridor
                     {
                        legit=false;
                     }
            }while(false);
            if(legit)
            {
               p=(p+1)%players.length;//next player.
               if(p==0)
               {
                  sunLeft--;
               }
            }//else keep same player
         }
         repaint();
      }
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
         case "champion":
            //transition
            repaint();
            battle("Champion of Chaos",(byte)u.ranI(6,8));
            break;
         case "dragon":
            if(u.ranI(0,dragonLeft)==0)//if random between 0 and num of dragon cards left is 0
            {//wake up dragon
               JOptionPane.showMessageDialog(null,"The dragon awakes!","ROAAAR!!",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(u.ranI(-10,-20));//deal 10-20 damage
            }
            else
            {//else
               dragonLeft--;//decrese num of dragon cards left
               int gold = u.ranI(10,100)*100;//random gold
               JOptionPane.showMessageDialog(null,"You snatch "+gold+"G of the dragon's treasure.","ZZzzzz",JOptionPane.INFORMATION_MESSAGE);
               players[p].getBag().add(gold+"G");//add gold to bag
            }
            break;
         case "gold":
            int gold = u.ranI(1,90)*10;
            JOptionPane.showMessageDialog(null,"You find gold.","Eureka!",JOptionPane.INFORMATION_MESSAGE);
            players[p].getBag().add(gold+"G");
            break;
         case "secret tunnel":
            JOptionPane.showMessageDialog(null,"You find a secret passage. Move to an adjecent space.","Secret Tunnel!",JOptionPane.INFORMATION_MESSAGE);
            secTun=true;
            break;
         case "nothing"://blank room/search card
            JOptionPane.showMessageDialog(null,"Nothing happens.","Phew",JOptionPane.INFORMATION_MESSAGE); 
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
         catch(Exception e)//if select cancel
         {
            exe=0;//leap aside
         }
         byte otherExe=0;
         float ran=(float)Math.random();
         switch(enemyName)
         {
            case "Goblin":
               if(ran<.3)
               {
                  otherExe=2;//30% mighty blow
               }
               else
                  if(ran<.7)
                  {
                     otherExe=1;//40% slash
                  }
                  else
                  {
                     otherExe=0;//30%leap aside
                  }
               break;
            case "Champion of Chaos":
               if(ran<.3)
               {
                  otherExe=1;//30% slash
               }
               else
                  if(ran<.8)
                  {
                     otherExe=2;//50% mighty blow
                  }
                  else
                  {
                     otherExe=0;//20% leap aside
                  }
               break;
            default:
               if(ran<(1.0/3))//33% all
               {
                  otherExe=2;
               }
               else
                  if(ran<(2.0/3))//33% all
                  {
                     otherExe=1;
                  }
                  else
                  {
                     otherExe=0;//33% all
                  }
               break;
         }
         switch(otherExe)
         {//print out what enemy does
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
         if(exe==otherExe)//if same option
         {
            enemyHP--;//deal 1 to each other
            players[p].changeHP(-1);
            JOptionPane.showMessageDialog(null,"1 damage to "+players[p].getName()+" and to the "+enemyName,"Damage!",JOptionPane.INFORMATION_MESSAGE);
         }
         else//0=la,1=sl,2=mb
            if((exe+1)%3==otherExe)//if monster beat human
            {
               players[p].changeHP(-1);//human -1 HP
               JOptionPane.showMessageDialog(null,"1 damage to "+players[p].getName(),"Damage!",JOptionPane.INFORMATION_MESSAGE);
            }
            else//Human has beaten monster at this point
               if(exe==2)
               {//if mighty blow, deal 2 damage to monster
                  enemyHP-=2;
                  JOptionPane.showMessageDialog(null,"2 damage to the "+enemyName,"Uber-Damage!",JOptionPane.INFORMATION_MESSAGE);
               }
               else
               {//if leap aside/slash deal 1 damage to monster
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
         if(ran<42)
         {
            return "champion";
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
      return "nothing";
   }
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      if(sunLeft>0)
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
                     switch(i)
                     {
                        case 0:
                           g.setColor(Color.red);//player 1
                           break;
                        case 1:
                           g.setColor(Color.blue);//player 2
                           break;
                        case 2:
                           g.setColor(Color.green);//player 3
                           break;
                        case 3:
                           g.setColor(Color.orange);//player 4
                           break;
                     }
                     if(i!=p)//if not curr player
                        g.setColor(g.getColor().darker());
                     g.fillRect(c*DIM+DIM/3,r*DIM+DIM/3,(int)(DIM-DIM/1.5),(int)(DIM-DIM/1.5));
                  }
               }
            }
         }
         drawPlayers(g);
         drawSun(g);
      }
      //end game scene
   }
   //post: draws the player cards at bottom of screen. Shows HP and items obtained.
   private void drawPlayers(Graphics g)
   {
      for(int i=0;i<players.length;i++)
      {
         switch(i)
         {
            case 0:
               g.setColor(Color.red);//player 1
               break;
            case 1:
               g.setColor(Color.blue);//player 2
               break;
            case 2:
               g.setColor(Color.green);//player 3
               break;
            case 3:
               g.setColor(Color.orange);//player 4
               break;
         }
         if(i==p)//if current player
         {
            Color temp=g.getColor();//save color
            g.setColor(Color.yellow);//draw yellow indicator below portrait
            g.fillRect(i*DIM*2,DIM*12,DIM*2,DIM/2);
            g.setColor(temp);//return color
         }
         else//if not curr player
            g.setColor(g.getColor().darker());//make dimmer
         g.fillRect(i*DIM*2,DIM*10,DIM*2,DIM*2);
         g=setTextColor(g);
         g.drawString("HP:"+players[i].getHP(),i*DIM*2+5,DIM*10+15);
         g.drawString("Bag:",i*DIM*2+5,DIM*10+30);
         for(int j=0;j<players[i].getBag().size();j++)
         {
            g.drawString(players[i].getBag().get(j),i*DIM*2,DIM*10+30*(j+2));
         }
      }
   }
   private void drawSun(Graphics g)
   {
      g.setColor(Color.yellow);
      g.fillRect(DIM*(players.length*2),DIM*10,DIM*3,DIM*2);
      g=setTextColor(g);
      g.setFont(new Font(null,0,DIM));
      g.drawString(sunLeft+"",DIM*(players.length*2+1),(int)(DIM*11.5));
   }
   //pre: x is Graphics with color to be inverted
   //post: x has a rgb-inverted color
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
   
   //these do absolutely nothing.
   public void mouseDragged( MouseEvent e){}
   public void mouseExited( MouseEvent e ){}
   public void mousePressed( MouseEvent e ){}
   public void mouseReleased( MouseEvent e ){}
   public void mouseEntered( MouseEvent e ){}
   public void mouseMoved( MouseEvent e){}
}