
public class Point implements Cloneable{

    private int x;
    private int y;
        public Point(int x, int y){
        reset(x,y);
    }

    public Point(Point p){
        reset(p.getX(),p.getY());
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException{
 	   return super.clone();   
    }    public int getX(){
        return x;
    }
        public int getY(){
        return y;
    }
        public void reset(int x, int y){
        this.x = x;
        this.y = y;
    }

 }
