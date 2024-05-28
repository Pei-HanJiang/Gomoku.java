import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.font.GlyphMetrics;
import java.util.Stack;
import javax.swing.*;


class Gomoku extends JFrame{
    int frame_width = 900, frame_height = 1100;
    static GomokuBtn btn[][] = new GomokuBtn[9][9];
    static GomokuHandler handler = new GomokuHandler();
    JPanel btnPanel, infoPanel;

    public Gomoku(){
        //Initialize the frame
        super("GOMOKU");
        this.setLocationRelativeTo(null);
        this.setSize(frame_width, frame_height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = this.getContentPane();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());

        //set GridLayout
        btnPanel = new JPanel(new GridLayout(9,9));
        infoPanel = new JPanel();

        //set Button
        for(int i = 0 ; i < 9 ; i++){
            for(int j = 0 ; j < 9 ; j++){
                btn[i][j] = new GomokuBtn(i,j);
                //btn[i][j].setText(i+","+j);
                btn[i][j].setFocusable(false);
                btn[i][j].addActionListener(handler);
                btnPanel.add(btn[i][j]);
            }

        }
        contentPane.add(btnPanel);
        //resetbtn
        JButton resetbtn = new JButton("Reset");
        resetbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == resetbtn){
                    Gomoku.resetGame();
                }

            }
        });
        infoPanel.add(resetbtn);

        //retractbtn
        JButton retractbtn = new JButton("Retract");
        retractbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == retractbtn){
                    Gomoku.retractGame();
                }

            }
        });
        infoPanel.add(retractbtn);



        contentPane.add(infoPanel, BorderLayout.SOUTH);


    }

    public static void retractGame(){
        //make sure it's not empty or else do nothing
        if(handler.ableToRetract()){
            int x = handler.getStackX();
            int y = handler.getStackY();
            btn[x][y].setText("");
            btn[x][y].setState(0);
            handler.doRetract();
        }
    }



    public static void resetGame(){
        for(int i = 0 ; i < 9 ; i++){
            for(int j = 0 ; j < 9 ; j++){
                btn[i][j].setText("");
                btn[i][j].state = 0;
                handler.resetTrack();
            }

        }
    }

    public static void disableGame(){
        for(int i = 0 ; i < 9 ; i++){
            for(int j = 0 ; j < 9 ; j++){
                btn[i][j].setEnabled(false);
            }

        }
    }

    public static void enableGame(){
        for(int i = 0 ; i < 9 ; i++){
            for(int j = 0 ; j < 9 ; j++){
                btn[i][j].setEnabled(true);
            }

        }
    }



    public static void main(String[] args) {
        Gomoku GameLaunch = new Gomoku();
        GameLaunch.setVisible(true);
    }
}


class GomokuBtn extends JButton{
    int x, y;
    int state; //0:blank
    public GomokuBtn(int x, int y){
        super();
        this.x = x;
        this.y = y;
        this.state = 0;
    }

    public int getState(){
        return state;
    }

    public void setState(int i){
        // 1 : player1, 2 : player2
        this.state = i;
    }
    public int getXpos(){
        return x;
    }
    public int getYpos(){
        return y;
    }

}

class GomokuHandler implements ActionListener{
    static int player = 1; //player1
    static int[][] track; // 0 : blank cell, 1 : O cell, 2 : X cell
    static Stack<Integer> Xrecord = new Stack<Integer>();
    static Stack<Integer> Yrecord = new Stack<Integer>();

    static int GameState = 0; // 0 : continue, 1 : player1 wins, 2: player2 wins, -1 :tie

    public GomokuHandler(){
        //intialize
        track = new int[9][9];
        for(int i = 0 ; i < 9 ; i++){
            for(int j = 0 ; j < 9 ; j++){
                track[i][j] = 0;
            }
        }
    }

    public void resetTrack(){
        for(int i = 0 ; i < 9 ; i++){
            for(int j = 0 ; j < 9 ; j++){
                track[i][j] = 0;
                GameState = 0;
                player = 1;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GomokuBtn checkedbtn = (GomokuBtn)e.getSource();
        int x = checkedbtn.getXpos();
        int y = checkedbtn.getYpos();
        JOptionPane message;

        if(checkedbtn.getState() == 0) {
            Xrecord.push(x);
            Yrecord.push(y);
            if (player == 1) {
                checkedbtn.setFont(checkedbtn.getFont().deriveFont(20.0f));
                checkedbtn.setText("○");
                checkedbtn.setState(player);
                track[x][y] = player;
                this.evaluation(x, y);
                player = 2;
            } else if (player == 2) {
                checkedbtn.setFont(checkedbtn.getFont().deriveFont(20.0f));
                checkedbtn.setText("●");
                checkedbtn.setState(player);
                track[x][y] = player;
                this.evaluation(x, y);
                player = 1;
            }
        }
        System.out.println("currentstate: "+ GameState);

        if(GameState != 0) {
            if(GameState == 1 || GameState == 2) {
                if (player == 2) player = 1;
                else if (player == 1) player = 2;
                Gomoku.disableGame();
                message = new JOptionPane();
                int option = message.showOptionDialog(null, "player " + player + " wins!\nClick ok to RESET the game\nClick cancel to EXIT", "GameOver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                if (option == JOptionPane.OK_OPTION) {
                    //reset track
                    resetTrack();
                    GameState = 0;
                    player = 1;
                    Gomoku.resetGame();
                    Gomoku.enableGame();
                }

                if (option == JOptionPane.CANCEL_OPTION) {
                    System.exit(0);
                }

            }else if(GameState == -1){
                System.out.println("TIE");
                message = new JOptionPane();
                int option = message.showOptionDialog(null, "It's a tie Game\nClick ok to RESET the game\nClick cancel to EXIT", "GameOver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                if (option == JOptionPane.OK_OPTION) {
                    //reset track
                    resetTrack();
                    GameState = 0;
                    player = 1;
                    Gomoku.resetGame();
                    Gomoku.enableGame();
                }

                if (option == JOptionPane.CANCEL_OPTION) {
                    System.exit(0);
                }

            }
        }


    }

    public void evaluation(int x, int y){
        Boolean hasblank = false;
        Boolean hasWinner = false;

        //check row
        //以剛下的棋子為基準點 檢查左右
        int i = 1, count = 1;
        while( y+i < 9 && track[x][y+i] == player ){
            i++;
            count++;
        }
        while( y-i >= 0 && track[x][y-i] == player ){
            i++;
            count++;
        }
        System.out.println(count);
        if(count >= 5){
            GameState = player;
            hasWinner = true;
        }

        //check col
        //以剛下的棋子為基準點 檢查上下
        i = 1;count = 1;
        while( x+i < 9 && track[x+i][y] == player ){
            i++;
            count++;
        }
        while( x-i >= 0 && track[x-i][y] == player ){
            i++;
            count++;
        }
        if(count >= 5){
            GameState = player;
            hasWinner = true;
        }


        //check 右上左下斜線
        i = 1;count = 1;
        while( y-i >= 0 && x+i < 9 && track[x+i][y-i] == player ){
            i++;
            count++;
        }
        while( x-i >= 0 && y+i < 9 && track[x-i][y+i] == player ){
            i++;
            count++;
        }
        if(count >= 5){
            GameState = player;
            hasWinner = true;
        }

        //check 左上右下斜線
        i = 1;count = 1;
        while( x+i < 9 && y+i < 9 && track[x+i][y+i] == player ){
            i++;
            count++;
        }
        while( x-i >= 0 && y-i >= 0 && track[x-i][y-i] == player ){
            i++;
            count++;
        }
        if(count >= 5){
            GameState = player;
            hasWinner = true;
        }


        //check blank
        for(int k = 0 ; k < 9 ; k++){
            for(int j = 0 ; j < 9 ; j ++){
                if(track[k][j] == 0){
                    hasblank = true;
                    break;
                }
            }
            if(hasblank) break;
        }
        //if no blank && no winner => a tie
        if(!hasblank && !hasWinner) GameState = -1;

        for(int k = 0 ; k < 9 ; k++){
            for(int f = 0 ; f < 9 ; f++){
                System.out.print(track[k][f]+" ");
            }
            System.out.println("");
        }
        System.out.println("");


    }

    public int getStackX(){
        //技術債
        if(!Xrecord.isEmpty()){
            return Xrecord.peek();
        }

        return 0;
    }

    public int getStackY(){
        //技術債
        if(!Yrecord.isEmpty()){
            return Yrecord.peek();
        }

        return 0;
    }

    public boolean ableToRetract(){
        if(!Xrecord.isEmpty() && !Yrecord.isEmpty()){

            return true;
        }
        System.out.println("NOT able to RETRACT");


        return false;
    }

    public void doRetract(){
        System.out.println("Doing RETRACT");
        int X = getStackX();
        int Y = getStackY();
        track[X][Y] = 0;
        //go back to the last player
        if(player == 2) player = 1;
        else player = 2;
        Xrecord.pop();
        Yrecord.pop();
    }
}