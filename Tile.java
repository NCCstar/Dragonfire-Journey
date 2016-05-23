public class Tile
{
   private final boolean isRoom;
   private final boolean[] exits;//0=up,1=r,2=d,3=l
   private boolean search1=true;
   private boolean search2=true;
   public Tile(boolean isRoom,boolean[] exits)
   {
      this.isRoom=isRoom;
      this.exits=exits;
   }
   public void search()
   {
      if(search1)
      {
         search1=false;
         u.SOP("Searched once.");
      }
      else
      {
         search2=false;
         u.SOP("Searched twice");
      }
   }
   public boolean canSearch()
   {
      return search1||search2;
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
   public void rotate()
   {
      boolean[] temp=new boolean[4];
      for(int i=0;i<temp.length;i++)
      {
         temp[i]=exits[i];
      }
      exits[0]=temp[3];
      exits[3]=temp[2];
      exits[2]=temp[1];
      exits[1]=temp[0];
   }
}