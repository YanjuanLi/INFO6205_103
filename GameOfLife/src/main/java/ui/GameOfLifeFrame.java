package ui;

import model.EvolutionaryAgent;
import model.Individual;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class GameOfLifeFrame extends JFrame {

    private JButton startGameBtn = new JButton("Start Game");
    private JButton generationBtn = new JButton();


    /**
     * flag to note whether game is started
     */
    private boolean isStart = false;

    /**
     * stop flag
     */
    private boolean stop = false;



    private JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
    private JPanel gridPanel = new JPanel();

    private JPanel[][] pnMatrix;

    private Individual bestPattern;
    private EvolutionaryAgent myAgent;

    private HashMap<String,Integer> generationTable;
    private static final Logger LOGGER = Logger.getLogger(GameOfLifeFrame.class);



    public GameOfLifeFrame() {

        setTitle("Game Of Life");
        startGameBtn.addActionListener(new StartGameActioner());
        buttonPanel.add(startGameBtn);
        buttonPanel.setBackground(Color.WHITE);
        generationBtn.setHorizontalAlignment(JTextField.CENTER);
        buttonPanel.add(generationBtn);
        getContentPane().add("North", buttonPanel);


        myAgent = new EvolutionaryAgent();
        bestPattern = myAgent.evolvePattern();
        try{
            Thread.sleep(5000);
        }catch(Exception e){
            e.getStackTrace();
        }
        LOGGER.info("Best pattern of this population:");
        myAgent.myGrid.printMatrix(bestPattern.getIndividualMatrix());

        initGridLayout();
        showMatrix();
        gridPanel.updateUI();
        generationBtn.setText("Total generation: 0");
        this.setSize(1000, 1200);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private void showMatrix() {

        boolean[][] matrix = bestPattern.getIndividualMatrix();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                if (matrix[y][x]) {
                    pnMatrix[y][x].setBackground(Color.PINK);
                } else {
                    pnMatrix[y][x].setBackground(Color.WHITE);
                    pnMatrix[y][x].setBorder(BorderFactory.createLineBorder(Color.PINK));
                }
            }
        }
    }

    /**
     * Show grid layout
     */
    private void initGridLayout() {

        int rows = bestPattern.getMatrixHeight();
        int cols = bestPattern.getMatrixWidth();
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(rows, cols));
        pnMatrix = new JPanel[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JPanel panel = new JPanel();
                pnMatrix[y][x] = panel;
                gridPanel.add(panel);
            }
        }
        add("Center", gridPanel);
    }


    private class StartGameActioner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isStart) {

                new Thread(new GameControlTask()).start();
                isStart = true;
                stop = false;
                startGameBtn.setEnabled(false);
            }
        }
    }

    private class GameControlTask implements Runnable {

        int count = 0;
        @Override
        public void run() {


            myAgent.myGrid.setStartingIndividual(bestPattern);

            while(!stop){
                boolean[][] oldMatrix = myAgent.myGrid.getCellMatrix();

                showMatrix2();
                myAgent.myGrid.nextGen();
                boolean[][] newMatrix= myAgent.myGrid.nextGen();
                int stopStatus = myAgent.myGrid.stopCheck(oldMatrix,newMatrix);
                if(stopStatus==1){
                    stop=true;

                    JOptionPane.showMessageDialog(
                            gridPanel,
                            "There is no cell any more.",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    break;
                }else if(stopStatus==2){
                    stop=true;
                    JOptionPane.showMessageDialog(
                            gridPanel,
                            "The generation number is larger than the maximum number. ",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
                }else if(stopStatus==3){
                    stop=true;
                    JOptionPane.showMessageDialog(
                            gridPanel,
                            "The status is the same as the previous one. ",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE
                    );


                    break;
                }

                myAgent.myGrid.updateCellMatrix(newMatrix);
                count++;
                myAgent.myGrid.setGenCount(count);
                generationBtn.setText("Total generations: " + myAgent.myGrid.getGenCount());
                LOGGER.info("Generation "+myAgent.myGrid.getGenCount());
                myAgent.myGrid.printMatrix(newMatrix);





               try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void showMatrix2(){
        boolean[][] matrix = myAgent.myGrid.getCellMatrix();
        for (int row = 1; row < matrix.length -1; row++) {
            for (int col = 1; col < matrix.length-1; col++) {
                if (matrix[row][col]) {
                    pnMatrix[row-1][col-1].setBackground(Color.PINK);
                } else {
                    pnMatrix[row-1][col-1].setBackground(Color.WHITE);
                    pnMatrix[row-1][col-1].setBorder(BorderFactory.createLineBorder(Color.PINK));
                }
            }
        }
    }

}
