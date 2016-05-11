public class Tile
{
   private final boolean isRoom;
   private final boolean[] exits;//0=up,1=r,2=d,3=l
   public Tile(boolean isRoom,boolean[] exits)
   {
      this.isRoom=isRoom;
      this.exits=exits;
   }
   public boolean isRoom()
   {
      return isRoom;
   }
   public boolean[] getExits()
   {
      return exits;
   }
   //param: n: number of times to be rotated
   public static void rotate(Tile t,int n)
   {
      boolean[] bool=t.getExits();//parady
      for(int i=0;i<n;i++)
      {
         //FINISH
      }
   }
}