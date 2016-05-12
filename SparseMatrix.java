import java.util.*;
public class SparseMatrix<anyType>
{
   private LinkedList<Cell<anyType>> list;
   private int rows;
   private int cols;
   
   public SparseMatrix(int r,int c)
   {
      rows=r;
      cols=c;
      list=new LinkedList();
   }
   public anyType get(int r, int c)
   {
      int i=0;
      while(i<list.size()&&getKey(list.get(i).row,list.get(i).col)<getKey(r,c))
      {
         i++;
      }
      if(i<list.size()&&getKey(list.get(i).row,list.get(i).col)==getKey(r,c))
         return list.get(i).value;
      return null;
   }			
   public anyType set(int r, int c, Object x)
   {
      Cell cell=new Cell(r,c,x);
      int i=0;
      while(i<list.size()&&getKey(list.get(i).row,list.get(i).col)<getKey(r,c))
      {
         i++;
      }
      if(i<list.size())
         return (anyType)list.set(i,cell).value;
      //list.add(cell);
      return null;
   }	
   public void add(int r, int c, Object x)
   {
      Cell cell=new Cell(r,c,x);
      int i=0;
      while(i<list.size()&&getKey(list.get(i).row,list.get(i).col)<getKey(r,c))
      {
         i++;
      }
      list.add(i,cell);
   }	
   private int getKey(int r,int c)
   {
      return r*cols+c;
   }   
   public anyType remove(int r, int c)//prob
   {
      int i=0;
      while(i<list.size()&&getKey(list.get(i).row,list.get(i).col)<getKey(r,c))
      {
         i++;
      }
      if(list.get(i).row==r&&list.get(i).col==c)
         return list.remove(i).value;
      return null;
   }
   public int size()
   {
      return list.size();
   }			
   public int numRow()
   {
      return rows;
   }		
   public int numCol()
   {
      return cols;
   }
   public String toString()
   {
      String ans="";
      for(int i=0;i<rows;i++)
      {
         for(int j=0;j<cols;j++)
         {
            anyType val=get(i,j);
            if(val!=null)
            {
               ans+=" "+val+" ";
            }
            else
               ans+=" - ";
         }
         ans+="\n";
      }
      return ans;
   }
}
class Cell<anyType>
{
   protected int row;
   protected int col;
   protected anyType value;
   public Cell(int r,int c,anyType o)
   {
      row=r;
      col=c;
      value=o;
   }
   public String toString()
   {
      return row+":"+col;//(String)value;
   }
}