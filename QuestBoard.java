/*
todo short: Add more room cards
todo long: Other types of tiles
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
   private byte sunLeft=30;//game timer. game over when this hits 0
   
   //post: returns DIM, for driver
   public byte getDIM(){
      return DIM;}
   //pre: pNum-number of players, decided by QuestDriver
   //post: creates QuestBoard w/ appropriate values.
   public QuestBoard(int pNum)
   {
      addMouseListener(this);
      //create master board
      grid=new SparseMatrix(10,13);
      //add starting tiles: dragon lair
      grid.add(4,6,new Tile(true,new boolean[]{true,true,true,true},"lair"));
      grid.add(5,6,new Tile(true,new boolean[]{true,true,true,true},"lair"));
      //add starting corners
      grid.add(0,0,new Tile(true,new boolean[]{false,true,true,false},"start"));
      grid.add(0,12,new Tile(true,new boolean[]{false,false,true,true},"start"));
      grid.add(9,0,new Tile(true,new boolean[]{true,true,false,false},"start"));
      grid.add(9,12,new Tile(true,new boolean[]{true,false,false,true},"start"));
      //define players
      players=new Hero[pNum];
      ArrayList<Object> listHeroes = new ArrayList();
      //list of availible heroes
      listHeroes.add("Ulv Grimhand");
      listHeroes.add("El-Adoran Sureshot");
      listHeroes.add("Volrik the Brave");
      listHeroes.add("Sir Rohan");
      for(int i=0;i<players.length;i++)
      {
         Object[] options = listHeroes.toArray();//display to screen options
         Object obj = JOptionPane.showInputDialog(null,"Player "+(i+1)+", choose a hero.","Hero Choice",JOptionPane.INFORMATION_MESSAGE, null,options, options[0]);
         listHeroes.remove(obj);//remove chosen hero
         switch(i)
         {//create hero at appropriate location
            case 1:
               players[i]=new Hero((String)obj,12,0);
               break;
            case 2:
               players[i]=new Hero((String)obj,12,9);
               break;
            case 3:
               players[i]=new Hero((String)obj,0,9);
               break;
            default:
               players[i]=new Hero((String)obj,0,0);
               break;
         }
      }
   }
   //pre: dir is direction of movement, 0=up,1=right,2=down,3=left
   //post: moves player, adds tile if needed
   private boolean newTile(int dir)
   {
      int opp=(dir+2)%4;//opposite direction
      switch(dir)
      {
         case 0:
            if(players[p].getY()<=0)
               return false;
            break;
         case 1:
            if(players[p].getX()>=12)
               return false;
            break;
         case 2:
            if(players[p].getY()>=9)
               return false;
            break;
         case 3:
            if(players[p].getX()<=0)
               return false;
            break;
      }
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
            }
            while(!(doors[0]||doors[1]||doors[2]||doors[3]));//while not at least one exit
            boolean isRoom=u.ranB(.9);
            String effect=null;
            int ranEffect=u.ranI(0,20);
            if(isRoom)
            {
               if(ranEffect>18&&oneTrue(doors))
                  effect="rotate";
               else
                  if(ranEffect>17)
                     effect="trap";
                  else
                     if(ranEffect>16)
                        effect="dark";
                        
            }
            if(oneTrue(doors))
            {
               grid.add(players[p].getY(),players[p].getX(),new Tile(true,doors,effect));
            }
            else
               grid.add(players[p].getY(),players[p].getX(),new Tile(isRoom,doors,effect));//add tile
            
            while(!grid.get(players[p].getY(),players[p].getX()).getExits()[opp])//while no exit on this side
            {
               grid.get(players[p].getY(),players[p].getX()).rotate();//rotate the tile
            }
         }
         String effect=grid.get(players[p].getY(),players[p].getX()).getEffect();
         if(effect!=null)
         {
            repaint();
            switch(effect)
            {
               case "rotate":
                  JOptionPane.showMessageDialog(null,"The room rotates.","What!?!",JOptionPane.INFORMATION_MESSAGE);
                  grid.get(players[p].getY(),players[p].getX()).rotate().rotate();
                  break;
               case "dark":
                  JOptionPane.showMessageDialog(null,"You can't see through the dark fog in the room.","*Waves hand in front of face*",JOptionPane.INFORMATION_MESSAGE);
                  break;
               case "trap":
                  JOptionPane.showMessageDialog(null,"A trap activates in the room!","Dangnabbit",JOptionPane.INFORMATION_MESSAGE);
                  exeCard(drawCard((byte)2));
                  break;
            }
         }
         return true;
      }
      return false;
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
   //pre: e: contains location of the click
   //post: called whenever mouse is clicked. controls movement, searching, and passing turn
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
               int pX=players[p].getX();
               int pY=players[p].getY();
               if(grid.get(pY,pX)!=null&&grid.get(pY,pX).getEffect()!=null&&grid.get(pY,pX).getEffect().equals("dark"))
               {
                  int ranDir=u.ranI(0,3);
                  while(!newTile(ranDir))
                  {
                     ranDir=u.ranI(0,3);
                  }
                  repaint();
                  legit=true;
                  if(grid.get(players[p].getY(),players[p].getX()).isRoom())//if this is not a corridor
                  {
                     if(x==6&&(y==4||y==5))
                        exeCard("dragon");//if in chamber, dragon card
                     else
                        if(grid.get(players[p].getY(),players[p].getX()).getEffect()==null)//if normal space
                           exeCard(drawCard((byte)0));//draw and execute a room card
                  }
                  else
                     if(!grid.get(players[p].getY(),players[p].getX()).isRoom())//if corridor
                     {
                        legit=false;
                     }
               
                  break;
               }
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
               if(x==players[p].getX()&&y==players[p].getY())//if where user pointed
                  if(grid.get(players[p].getY(),players[p].getX()).isRoom())//if this is not a corridor
                  {
                     if(x==6&&(y==4||y==5))
                        exeCard("dragon");//if in chamber, dragon card
                     else
                        if(grid.get(players[p].getY(),players[p].getX()).getEffect()==null)//if normal space
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
               passTurn();
            }//else keep same player
         }
         else
         {
            if(x>=players.length*2&&x<players.length*2+3&&y>=10&&y<12)
            {
               passTurn();
            }
            if(x>=p*2&&x<p+3&&y>=10&&y<12)//click on own portrait
            {
               ArrayList<Object> preop = new ArrayList();
               preop.add("Nevermind");
               for(String ele:players[p].getBag())
               {
                  try
                  {
                     Integer.parseInt(ele.substring(0,ele.length()-1));
                  }
                  catch(Exception ex)
                  {
                     preop.add(ele);
                  }
               }
               if(preop.size()<=1)
               {
                  JOptionPane.showMessageDialog(null,"You have no items to use.","Has no items in item bag.",JOptionPane.INFORMATION_MESSAGE);
               }
               else
               {
                  Object[] options=preop.toArray();
                  String toUse=(String)JOptionPane.showInputDialog(null,"Choose an item to use.","Items in item bag.",JOptionPane.INFORMATION_MESSAGE, null,options, options[0]);
                  if(toUse.equals("Potion"))
                  {
                     players[p].changeHP(5);
                  }
                  players[p].getBag().remove(toUse);
               }
            }
         }  
         repaint();
      }
   }
   //post: changes turn- skips dead players and increments sun
   private void passTurn()
   {
      do
      {
         p=(p+1)%players.length;//next player.
         if(p==0)
         {
            sunLeft--;
         }
      }while(players[p].getHP()<=0);
      if(sunLeft<=0)//end of game execution
      {
         int[] scores = new int[players.length];
         for(int i=0;i<players.length;i++)
         {
            for(String ele:players[i].getBag())
            {
               try
               {
                  scores[i]+=Integer.parseInt(ele.substring(0,ele.length()-1));
               }
               catch(Exception e){}
            }
            int pY=players[i].getY();
            int pX=players[i].getX();
            if(grid.get(pY,pX).getEffect()==null||!grid.get(pY,pX).getEffect().equals("start"))
            {
               scores[i]=-1;
            }
            if(scores[i]>scores[p])
               p=i;
         }
      }
   }
   //pre: card - identity of card to be processed
   //post: executes given card
   private void exeCard(String card)
   {
      int damage;
      switch(card)
      {
         case "goblin surprise":
         //attack damage
            damage=u.ranI(1,12)-players[p].getLuck();
            if(damage>0)
            {
               JOptionPane.showMessageDialog(null,"A goblin performs a sneak attack!\n"+players[p].getName()+" takes "+damage+" damage.","Sneak Attack!",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(damage*-1);
            }
            else
            {
               if(damage>=-3)
                  JOptionPane.showMessageDialog(null,"A goblin performs a sneak attack!\nBut it misses.","Sneak Attack!",JOptionPane.INFORMATION_MESSAGE);
               else
               {
                  JOptionPane.showMessageDialog(null,"A goblin performs a sneak attack!\nBut trips and hits himself with his club.\nThe goblin stops moving. You win!","Sneak Attack!",JOptionPane.INFORMATION_MESSAGE);
                  break;
               }
            }
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
               damage=u.ranI(10,20);
               JOptionPane.showMessageDialog(null,"The dragon awakes!\n"+players[p].getName()+"takes "+damage+" damage.","ROAAAR!!",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(-1*damage);//deal 10-20 damage
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
         case "potion":
            JOptionPane.showMessageDialog(null,"You find a potion.","Delicous!",JOptionPane.INFORMATION_MESSAGE);
            players[p].getBag().add("Potion");
            break;
         case "secret tunnel":
            JOptionPane.showMessageDialog(null,"You find a secret passage. Move to an adjecent space.","Secret Tunnel!",JOptionPane.INFORMATION_MESSAGE);
            secTun=true;
            break;
         case "centipede":
            damage=u.ranI(1,12);
            JOptionPane.showMessageDialog(null,"A giant centipede attacks!\n"+players[p].getName()+" loses "+damage+" life.","Munch!",JOptionPane.INFORMATION_MESSAGE);
            players[p].changeHP(-1*damage);
            break;
         case "cave-in":
            damage=u.ranI(1,10)-players[p].getLuck();
            if(damage>0)
            {
               JOptionPane.showMessageDialog(null,"A cave-in is triggered!\n"+players[p].getName()+" loses "+damage+" life.","Crash!",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(-1*damage);
            }
            else
               if(damage<=-3)
               {
                  JOptionPane.showMessageDialog(null,"A cave-in is triggered!\n"+players[p].getName()+" freezes with fear and is miraculously untouched.\nThe cave-in reveals something.","Crash!",JOptionPane.INFORMATION_MESSAGE);
                  exeCard("gold");
               }
               else
               {
                  JOptionPane.showMessageDialog(null,"A cave-in is triggered!\n"+players[p].getName()+" freezes with fear and is miraculously untouched.","Crash!",JOptionPane.INFORMATION_MESSAGE);
               }
            break;
         case "darts":
            damage=u.ranI(1,10)-players[p].getAgility();
            if(damage>0)
            {
               JOptionPane.showMessageDialog(null,"Darts shoot out from the walls!\n"+players[p].getName()+" loses "+damage+" life.","Thwoop!",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(-1*damage);
            }
            else
               if(damage<=-3)
               {
                  JOptionPane.showMessageDialog(null,"Darts shoot out from the walls!\n"+players[p].getName()+" jumps out of the way!\nThe hero salvages something from the darts.","Thwoop!",JOptionPane.INFORMATION_MESSAGE);
                  exeCard("potion");
               }
               else
               {
                  JOptionPane.showMessageDialog(null,"Darts shoot out from the walls!\n"+players[p].getName()+" jumps out of the way!","Thwoop!",JOptionPane.INFORMATION_MESSAGE);
               }
            break;
         case "explosion":
            damage=u.ranI(1,10)-players[p].getAgility();
            if(damage>0)
            {
               JOptionPane.showMessageDialog(null,"An explosion suddenly rocks the room.\n"+players[p].getName()+" loses "+damage+" life.","Bang!",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(-1*damage);
            }
            else
               if(damage<=-3)
               {
                  JOptionPane.showMessageDialog(null,"An explosion suddenly rocks the room.\n"+players[p].getName()+"'s armour blocks the blast.\nThe explosion reveals a secret door!","Bang!",JOptionPane.INFORMATION_MESSAGE);
                  exeCard("secret tunnel");
               }
               else
               {
                  JOptionPane.showMessageDialog(null,"An explosion suddenly rocks the room.\n"+players[p].getName()+"'s armour blocks the blast.","Bang!",JOptionPane.INFORMATION_MESSAGE);
               }
            break;
         case "golem":
            JOptionPane.showMessageDialog(null,"A golem appears from the floor of the room!\n"+players[p].getName()+" attacks it.","Rock Monster!",JOptionPane.INFORMATION_MESSAGE);
            damage=u.ranI(1,10)-players[p].getStrength();
            if(damage>0)
            {
               JOptionPane.showMessageDialog(null,"The golem, with the last of its strength, punches back.\n"+players[p].getName()+" loses "+damage+" life.\nThe golem crumbles to dust.","Crumble...",JOptionPane.INFORMATION_MESSAGE);
               players[p].changeHP(-1*damage);
            }
            else
               if(damage<=-3)
               {
                  JOptionPane.showMessageDialog(null,"The golem is destroyed with one punch.\nThe golem crumbles to dust, revealing something.","Crumble...",JOptionPane.INFORMATION_MESSAGE);
                  if(Math.random()<.5)
                     exeCard("gold");
                  else
                     exeCard("potion");
               }
               else
               {
                  JOptionPane.showMessageDialog(null,"The golem is destroyed with one punch.\nThe golem crumbles to dust.","Crumble...",JOptionPane.INFORMATION_MESSAGE);
               }
            break;
         //"golem";//strength
         case "nothing"://blank room/search card
            JOptionPane.showMessageDialog(null,"Nothing happens.","Phew",JOptionPane.INFORMATION_MESSAGE); 
            break;
      }
   }
   //pre: enemyName: name of creature fighting, enemyHP: inital health of enemy
   //post: battle loop, will end once player or monster dies
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
   //pre: type: type of card to be returned. 
   //post: returns random card indicated by type
   private String drawCard(byte type)//0=room,1=search,2=trap
   {
      if(type==0)
      {
         int ran=u.ranI(0,40);
         if(ran<34)
         {
            return "nothing";
         }
         if(ran<36)
         {
            return "goblin";
         }
         if(ran<37)
         {
            return "champion";
         }
         if(ran<39)
         {
            return "goblin surprise";
         }
         return "orc";
      }
      if(type==1)
      {
         int ran=u.ranI(0,45);
         if(ran<25)
         {
            return "nothing";
         }
         if(ran<28)
         {
            return "centipede";
         }
         if(ran<30)
         {
            return "potion";
         }
         if(ran<40)
         {
            return "secret tunnel";
         }
         return "gold";
      }
      if(type==2)
      {
         int ran=u.ranI(0,3);
         if(ran>2)
            return "cave-in";//luck
         if(ran>1)
            return "darts";//agility
         if(ran>0)
            return "explosion";//armour
         return "golem";//strength
      }
      return "nothing";
   }
   //post: master painting method - draws grid and info to screen
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
                  if(players[i].getHP()<=0)
                  {
                     g.setColor(Color.red.brighter());
                     g.drawLine(c*DIM+DIM/4,r*DIM+DIM/4,(c+1)*DIM-DIM/4,(r+1)*DIM-DIM/4);
                     g.drawLine(c*DIM+DIM/4,(r+1)*DIM-DIM/4,(c+1)*DIM-DIM/4,r*DIM+DIM/4);
                  }
               }
            }
         }
      }
      drawPlayers(g);
      if(sunLeft>0)
      {
         drawSun(g);
      }
      else
      {
         drawWinner(g);
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
   //post: draws sun box with number turn left
   private void drawSun(Graphics g)
   {
      g.setColor(Color.yellow);
      g.fillRect(DIM*(players.length*2),DIM*10,DIM*3,DIM*2);
      g=setTextColor(g);
      g.setFont(new Font(null,0,DIM));
      g.drawString(sunLeft+"",DIM*(players.length*2+1),(int)(DIM*11.5));
      //g.drawString("See This",0,0);
   }
   private void drawWinner(Graphics g)
   {
      g.setColor(Color.yellow);
      g.fillRect(DIM*(players.length*2),DIM*10,DIM*3,DIM*2);
      g=setTextColor(g);
      g.setFont(new Font(null,0,DIM));
      g.drawString("Player "+(p+1)+" wins!",DIM*(players.length*2),(int)(DIM*11.5));
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
   //pre: tile-a tile to be drawn, y and x - top-left corner of where to draw
   //post: draw given tile to screen at given coordinates
   private void drawTile(Graphics g,Tile tile,int y,int x)
   {
      g.setColor(Color.black);
      g.fillRect(x*DIM,y*DIM,DIM,DIM);//Rect(l,t,wid,hei)
      if(tile!=null)
      {
         if(tile.getEffect()==null)
         {
            if(tile.canSearch())
               g.setColor(Color.white);
            else
               g.setColor(Color.white.darker());
         }
         else
            switch(tile.getEffect())
            {
               case "lair":
                  g.setColor(Color.yellow);
                  break;
               case "start":
                  g.setColor(Color.yellow.darker());
                  break;
               case "trap":
                  g.setColor(Color.pink.darker());
                  break;
               case "dark":
                  g.setColor(Color.darkGray);
                  break;
               default:
                  if(tile.canSearch())
                     g.setColor(Color.white);
                  else
                     g.setColor(Color.white.darker());
                  break;
            }
      
         //draw center
         g.fillRect(x*DIM+DIM/3,y*DIM+DIM/3,DIM/3,DIM/3);
         if(tile.isRoom())
         {
            //draw 4 corners
            if(tile.getEffect()==null)
            {
               g.fillRect(x*DIM+DIM/6,y*DIM+DIM/6,DIM/6+1,DIM/6+1);
               g.fillRect(x*DIM+DIM/6,y*DIM+DIM*2/3,DIM/6+1,DIM/6);
               g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/6,DIM/6,DIM/6+1);
               g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM*2/3,DIM/6,DIM/6);
            }
            else
               if(tile.getEffect().equals("rotate"))
               {
                  g.fillOval(x*DIM+DIM/10,y*DIM+DIM/10,DIM-DIM/5,DIM-DIM/5);
               }
               else
               {
                  g.fillRect(x*DIM+DIM/6,y*DIM+DIM/6,DIM/6+1,DIM/6+1);
                  g.fillRect(x*DIM+DIM/6,y*DIM+DIM*2/3,DIM/6+1,DIM/6);
                  g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/6,DIM/6,DIM/6+1);
                  g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM*2/3,DIM/6,DIM/6);
               }
            if(tile.getExits()[0])
            {
               g.fillRect(x*DIM+DIM/3,y*DIM,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM/3,y*DIM+DIM/6,DIM/3,DIM/6+1);
            }
            if(tile.getExits()[1])
            {
               g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/6,DIM/3);
            }
            if(tile.getExits()[2])
            {
               g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/6);
            }
            if(tile.getExits()[3])
            {
               g.fillRect(x*DIM,y*DIM+DIM/3,DIM/3,DIM/3);
            }
            else
            {
               g.fillRect(x*DIM+DIM/6,y*DIM+DIM/3,DIM/6+1,DIM/3);
            }
         }
         if(tile.getExits()[0])
         {
            g.fillRect(x*DIM+DIM/3,y*DIM,DIM/3,DIM/3);
         }
         else
         {
            //g.fillRect(x*DIM+DIM/3,y*DIM+DIM/6,DIM/3,DIM/6+1);
         }
         if(tile.getExits()[1])
         {
            g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/3,DIM/3);
         }
         else
         {
            //g.fillRect(x*DIM+DIM*2/3,y*DIM+DIM/3,DIM/6,DIM/3);
         }
         if(tile.getExits()[2])
         {
            g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/3);
         }
         else
         {
            //g.fillRect(x*DIM+DIM/3,y*DIM+DIM*2/3,DIM/3,DIM/6);
         }
         if(tile.getExits()[3])
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