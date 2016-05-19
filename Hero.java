public abstract class Hero
{
   private int HP;
   private int x;
   private int y;
   public Hero(int hp,int x,int y)
   {
      HP=hp;
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
   public void moveX(boolean right)
   {
      if(right)
         x++;
      else
         x--;
   }
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
   public void changeHP(int dif)
   {
      HP+=dif;
   }
}