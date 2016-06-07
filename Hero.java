import java.util.ArrayList;
public class Hero
{
   private int HP;
   private int x;
   private int y;
   private final String name;
   private final int strength;
   private final int agility;
   private final int armour;
   private final int luck;
   private ArrayList<String> bag = new ArrayList();
   public Hero(String name,int x,int y)
   {
      this.name=name;
      switch(name)
      {//character stat set depending on name
         case "Ulv Grimhand":
            strength=7;
            agility=5;
            armour=6;
            luck=5;
            HP=16;
            break;
         case "El-Adoran Sureshot":
            strength=3;
            agility=8;
            armour=5;
            luck=7;
            HP=11;
            break;
         case "Volrik the Brave":
            strength=4;
            agility=7;
            armour=4;
            luck=8;
            HP=15;
            break;
         case "Sir Rohan":
            strength=6;
            agility=4;
            armour=9;
            luck=4;
            HP=17;
            break;
         default://default character (sucks)
            strength=5;
            agility=5;
            armour=5;
            luck=5;
            break;
      }
      this.x=x;
      this.y=y;
   }
   public int getX()
   {
      return x;
   }
   public int getY()
   {
      return y;
   }
   public ArrayList<String> getBag()
   {
      return bag;
   }
   //if right is true, move right, else move left
   public void moveX(boolean right)
   {
      if(right)
         x++;
      else
         x--;
   }
   //if down is true, move down, else move up
   public void moveY(boolean down)
   {
      if(down)
         y++;
      else
         y--;
   }
   public int getHP()
   {
      return HP;
   }
   //post: changes HP by dif amount. 
   public void changeHP(int dif)
   {
      HP+=dif;
   }
   public String getName()
   {
      return name;
   }
   public int getStrength()
   {
      return strength;
   }
   public int getAgility()
   {
      return agility;
   }
   public int getArmour()
   {
      return armour;
   }
   public int getLuck()
   {
      return luck;
   }
}