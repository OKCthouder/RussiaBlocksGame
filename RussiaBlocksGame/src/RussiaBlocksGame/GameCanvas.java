package RussiaBlocksGame;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * 画布类，内有<行数>*<列数> 个方格类实例。 继承自JPanel类。 ErsBlock线程类动态改变画布类的方格颜色，画布类通过
 * 检查方格颜色来体现ErsBlock块的移动情况。
 */
public class GameCanvas extends JPanel {
   
	private static final long serialVersionUID = 6732901391026089276L;
	private Color backColor = Color.darkGray, frontColor = Color.WHITE;
    private int rows, cols, score = 0, scoreForLevelUpdate = 0;
    private ErsBox[][] boxes;
    private int boxWidth, boxHeight;

    /**
     * 画布类的构造函数
     *
     * @param rows int,画布的行数
     * @param cols int,画布的列数 行数和列数决定着画布拥有方格的数目
     */
    public GameCanvas(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        boxes = new ErsBox[rows][cols];
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                boxes[i][j] = new ErsBox(false);
            }
        }

        setBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140)));
    }

    /**
     * 画布类的构造函数
     *
     * @param rows
     * @param cols
     * @param backColor
     * @param frontColor
     */
    public GameCanvas(int rows, int cols,
            Color backColor, Color frontColor) {
        this(rows, cols);
        this.backColor = backColor;
        this.frontColor = frontColor;
    }

    /**
     * 设置游戏背景色彩
     *
     * @param backColor Color，背景色彩
     */
    public void setBackgroundColor(Color backColor) {
        this.backColor = backColor;
    }

    /**
     * 取得游戏背景色彩
     *
     * @return Color ,背景色彩
     */
    public Color getBackgroundColor() {
        return backColor;
    }

    /**
     * 设置游戏方块颜色
     *
     * @param frontColor Color，方块颜色
     */
    public void setBlockColor(Color frontColor) {
        this.frontColor = frontColor;
    }

    /**
     * 取得游戏方块色彩
     *
     * @return Color,方块颜色
     */
    public Color getBlockColor() {
        return frontColor;
    }

    /**
     * 取得画布中方格的列数
     *
     * @return
     */
    public int getRows() {
        return rows;
    }

    /**
     * 取得画布中方格的行数
     *
     * @return int,方格的行数
     */
    public int getCols() {
        return cols;
    }

    /**
     * 取得游戏成绩
     *
     * @return int, 分数
     */
    public int getScore() {
        return score;
    }

    /**
     * 取得自上一次升级后的积分
     *
     * @return int ,上一次升级后的积分
     */
    public int getScoreForLevelUpdate() {
        return scoreForLevelUpdate;
    }

    /**
     * 升级后，将上一次升级以来的积分清零
     */
    public void resetScoreForLevelUpdate() {
        scoreForLevelUpdate -= RussiaBlocksGame.PER_LEVEL_SCORE;
    }

    /**
     * 得到某一行某一列的方格引用
     *
     * @return row int ,要引用的方格所在的行
     * @param col int, 要引用的方格所在的行
     * @return ErsBox,在row行col列的方格的引用
     */
    public ErsBox getBox(int row, int col) {
        if (row < 0 || row > boxes.length - 1 || col < 0 || col > boxes[0].length - 1) {
            return null;
        }
        return (boxes[row][col]);
    }

    /**
     * 覆盖JComponent类的函数，画组件。
     *
     * @param g 图形设备环境
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(frontColor);
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                g.setColor(boxes[i][j].isColorBox() ? frontColor : backColor);
                g.fill3DRect(j * boxWidth, i * boxHeight,
                        boxWidth, boxHeight, true);
            }
        }
    }

    /**
     * 根据窗口大小，自动调节方格的尺寸
     */
    public void adjust() {
        boxWidth = getSize().width / cols;
        boxHeight = getSize().height / rows;
    }

    /**
     * 当一行被游戏者叠满后，将此行清除，并为游戏者加分
     *
     * @param row int，要清除的行，是由ErsBoxesGame类计算的
     */
    public synchronized void removeLine(int row) {
        for (int i = row; i > 0; i--) {
            for (int j = 0; j < cols; j++) {
                boxes[i][j] = (ErsBox) boxes[i - 1][j].clone();                 //将上一行的方块颜色克隆下来，
            }                                                                   //即消去一行方块
        }

        score += RussiaBlocksGame.PER_LEVEL_SCORE;
        scoreForLevelUpdate += RussiaBlocksGame.PER_LEVEL_SCORE;
        repaint();
    }

    /**
     * 重置画布，置积分为零
     */
    public void reset() {
        score = 0;
        scoreForLevelUpdate = 0;
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                boxes[i][j].setColor(false);
            }
        }

        repaint();
    }
}