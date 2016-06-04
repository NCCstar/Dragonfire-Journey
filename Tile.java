public class Tile
{
   private final boolean isRoom;
   private final boolean[] exits;//0=up,1=r,2=d,3=l
   private boolean search1=true;//has been searched once
   private boolean search2=true;//has been searhced twice
   private String effect;
   public Tile(boolean isRoom,boolean[] exits,String eff)
   {
      this.isRoom=isRoom;
      this.exits=exits;
      effect=eff;
      if(effect!=null&&!effect.equals("rotate"))
      {//special rooms can't be searched
         search1=false;
         search2=false;
      }
   }
   public String getEffect()
   {
      return effect;
   }
   public void search()//increments search counter
   {
      if(search1)
      {
         search1=false;
      }
      else
      {
         search2=false;
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
   //post: rotates the exits of the tile once.
   public Tile rotate()
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
      return this;//for rotating twice. :-)
   }
}