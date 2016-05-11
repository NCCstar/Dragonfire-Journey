public abstract class Hero
{
   private int HP;
   private int x;
   private int y;
   private int pNum;
   public Hero(int hp,int x,int y,int p)
   {
      HP=hp;
      this.x=x;
      this.y=y;
      pNum=p;
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