import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;



public class GameController implements ActionListener {    private GameView gameView;
    private GameModel gameModel;

    private LinkedStack<Object> undoStack = new LinkedStack<>();
    private LinkedStack<Object> redoStack = new LinkedStack<>();
    public GameController(int size) {
        gameModel = new GameModel(size);
        gameView = new GameView(gameModel, this);
        gameView.update();
    }

    public void reset(){
        undoStack=new LinkedStack<>();
        redoStack=new LinkedStack<>();
        checkButton();
        gameModel.reset();
        gameView.update();
    }

    private void undo() {
        if(!undoStack.isEmpty()) {
                GameModel temp = (GameModel) undoStack.pop();
            try {
                GameModel temp2= (GameModel)gameModel.clone();
                redoStack.push(temp2);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            gameModel.setCurrentDot(temp.getCurrentDot().getX(), temp.getCurrentDot().getY());
                for (int i = 0; i < gameModel.getSize(); i++) {
                    for (int j = 0; j < gameModel.getSize(); j++) {
                        gameModel.getModel()[i][j] = temp.getModel()[i][j];
                    }
                }
                gameModel.numberOfSteps = temp.getNumberOfSteps();
                gameView.update();

    }
    }

    private void redo(){
        if(!redoStack.isEmpty()){
            GameModel temp1=(GameModel)redoStack.pop();
            try {
                GameModel temp2= (GameModel)gameModel.clone();
                undoStack.push(temp2);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            gameModel.setCurrentDot(temp1.getCurrentDot().getX(), temp1.getCurrentDot().getY());
            for (int i = 0; i < gameModel.getSize(); i++) {
                for (int j = 0; j < gameModel.getSize(); j++) {
                    gameModel.getModel()[i][j] = temp1.getModel()[i][j];
                }
            }
            gameModel.numberOfSteps = temp1.getNumberOfSteps();
            gameView.update();


        }
    }
    private void checkButton(){
        if(undoStack.isEmpty()){
            gameView.buttonUndo.setEnabled(false);
        }

        if(!undoStack.isEmpty()){
            gameView.buttonUndo.setEnabled(true);
        }

        if(redoStack.isEmpty()){
            gameView.buttonRedo.setEnabled(false);
        }

        if(!redoStack.isEmpty()){
            gameView.buttonRedo.setEnabled(true);
        }
    }


    public void actionPerformed(ActionEvent e) {



        if (e.getSource() instanceof DotButton) {
            DotButton clicked = (DotButton)(e.getSource());
            try {
                undoStack.push(gameModel.clone());
            } catch (NullPointerException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (CloneNotSupportedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            checkButton();



            if (gameModel.getCurrentStatus(clicked.getColumn(),clicked.getRow()) ==
                    GameModel.AVAILABLE){
                gameModel.select(clicked.getColumn(),clicked.getRow());
                oneStep();
            }
        } else if (e.getSource() instanceof JButton) {
            JButton clicked = (JButton)(e.getSource());

            if (clicked.getText().equals("Quit")) {
                System.exit(0);
            } else if (clicked.getText().equals("Reset")){
                reset();
            } else if (clicked.getText().equals("Undo")){
                undo();
                checkButton();
            } else if (clicked.getText().equals("Redo")){
                redo();
                checkButton();
            }
        }


    }
    private void oneStep(){
        Point currentDot = gameModel.getCurrentDot();
        if(isOnBorder(currentDot)) {
            gameModel.setCurrentDot(-1,-1);
            gameView.update();
 
            Object[] options = {"Play Again",
                    "Quit"};
            int n = JOptionPane.showOptionDialog(gameView,
                    "You lost! Would you like to play again?",
                    "Lost",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if(n == 0){
                reset();
            } else{
                System.exit(0);
            }
        }
        else{
            Point direction = findDirection();
            if(direction.getX() == -1){
                gameView.update();
                Object[] options = {"Play Again",
                        "Quit"};
                int n = JOptionPane.showOptionDialog(gameView,
                        "Congratualtions, you won in " + gameModel.getNumberOfSteps() 
                            +" steps!\n Would you like to play again?",
                        "Won",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if(n == 0){
                    reset();
                } else{
                    System.exit(0);
                }
            }
            else{
                gameModel.setCurrentDot(direction.getX(), direction.getY());
                gameView.update();
            }
        }
 
    }    private Point findDirection(){
        boolean[][] blocked = new boolean[gameModel.getSize()][gameModel.getSize()];

        for(int i = 0; i < gameModel.getSize(); i ++){
            for (int j = 0; j < gameModel.getSize(); j ++){
                blocked[i][j] = 
                    !(gameModel.getCurrentStatus(i,j) == GameModel.AVAILABLE);
            }
        }

        Queue<Pair<Point>> myQueue = new LinkedQueue<Pair<Point>>();
        
        LinkedList<Point> possibleNeighbours = new  LinkedList<Point>();        Point currentDot = gameModel.getCurrentDot();

        possibleNeighbours = findPossibleNeighbours(currentDot, blocked);        java.util.Collections.shuffle(possibleNeighbours);

        for(int i = 0; i < possibleNeighbours.size() ; i++){
            Point p = possibleNeighbours.get(i);
            if(isOnBorder(p)){
                return p;                
            }
            myQueue.enqueue(new Pair<Point>(p,p));
            blocked[p.getX()][p.getY()] = true;
        }        while(!myQueue.isEmpty()){
            Pair<Point> pointPair = myQueue.dequeue();
            possibleNeighbours = findPossibleNeighbours(pointPair.getFirst(), blocked);
             
            for(int i = 0; i < possibleNeighbours.size() ; i++){
                Point p = possibleNeighbours.get(i);
                if(isOnBorder(p)){
                    return pointPair.getSecond();                
                }
                myQueue.enqueue(new Pair<Point>(p,pointPair.getSecond()));
                blocked[p.getX()][p.getY()]=true;
            }

       }        return new Point(-1,-1);

    }private boolean isOnBorder(Point p){
        return (p.getX() == 0 || p.getX() == gameModel.getSize() - 1 ||
                p.getY() == 0 || p.getY() == gameModel.getSize() - 1 );
    }    private LinkedList<Point> findPossibleNeighbours(Point point, 
            boolean[][] blocked){

        LinkedList<Point> list = new LinkedList<Point>();
        int delta = (point.getY() %2 == 0) ? 1 : 0;
        if(!blocked[point.getX()-delta][point.getY()-1]){
            list.add(new Point(point.getX()-delta, point.getY()-1));
        }
        if(!blocked[point.getX()-delta+1][point.getY()-1]){
            list.add(new Point(point.getX()-delta+1, point.getY()-1));
        }
        if(!blocked[point.getX()-1][point.getY()]){
            list.add(new Point(point.getX()-1, point.getY()));
        }
        if(!blocked[point.getX()+1][point.getY()]){
            list.add(new Point(point.getX()+1, point.getY()));
        }
        if(!blocked[point.getX()-delta][point.getY()+1]){
            list.add(new Point(point.getX()-delta, point.getY()+1));
        }
        if(!blocked[point.getX()-delta+1][point.getY()+1]){
            list.add(new Point(point.getX()-delta+1, point.getY()+1));
        }
        return list;
    }


}