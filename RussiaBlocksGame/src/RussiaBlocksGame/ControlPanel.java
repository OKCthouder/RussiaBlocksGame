package RussiaBlocksGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * 控制面板类，继承自JPanel。 上边安放预显窗口，等级，得分，控制按钮 主要用来控制游戏进程。
 */
class ControlPanel extends JPanel {

	private static final long serialVersionUID = 3900659640646175724L;
	private JTextField tfLevel = new JTextField("" + RussiaBlocksGame.DEFAULT_LEVEL),
            tfScore = new JTextField(" 0"),
            tfTime = new JTextField(" ");
    private JButton btPlay = new JButton(" 开始"),
            btPause = new JButton(" 暂停"),
            btStop = new JButton("终止游戏"),
            btTurnLevelUp = new JButton(" 增加难度"),
            btTurnLevelDown = new JButton(" 降低难度");
    private JPanel plTip = new JPanel(new BorderLayout());
    private TipPanel plTipBlock = new TipPanel();
    private JPanel plInfo = new JPanel(new GridLayout(4, 1));
    private JPanel plButton = new JPanel(new GridLayout(6, 1));
    private Timer timer;
    private Border border = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));

    /**
     * 控制面板类的构造函数
     *
     * @param game ErsBlocksGame,ErsBlocksGame 类的一个实例引用 方便直接控制ErsBlocksGame类的行为。
     */
    public ControlPanel(final RussiaBlocksGame game) {
        setLayout(new GridLayout(3, 1, 0, 2));
        plTip.add(new JLabel(" 下一个方块"), BorderLayout.NORTH);               //添加组件
        plTip.add(plTipBlock);
        plTip.setBorder(border);

        plInfo.add(new JLabel(" 难度系数"));
        plInfo.add(tfLevel);
        plInfo.add(new JLabel(" 得分"));
        plInfo.add(tfScore);
        plInfo.setBorder(border);

        plButton.add(btPlay);
        btPlay.setEnabled(true);
        plButton.add(btPause);
        btPause.setEnabled(false);
        plButton.add(btStop);
        btStop.setEnabled(false);
        plButton.add(btTurnLevelUp);
        plButton.add(btTurnLevelDown);
        plButton.add(tfTime);
        plButton.setBorder(border);

        tfLevel.setEditable(false);
        tfScore.setEditable(false);
        tfTime.setEditable(false);

        add(plTip);
        add(plInfo);
        add(plButton);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (!game.isPlaying()) {
                    return;
                }

                ErsBlock block = game.getCurBlock();
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        block.moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        block.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        block.moveRight();
                        break;
                    case KeyEvent.VK_UP:
                        block.turnNext();
                        break;
                    default:
                        break;
                }
            }
        });

        btPlay.addActionListener(new ActionListener() {                         //开始游戏
            @Override
            public void actionPerformed(ActionEvent ae) {
                game.playGame();
            }
        });
        btPause.addActionListener(new ActionListener() {                        //暂停游戏
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (btPause.getText().equals(" 暂停")) {
                    game.pauseGame();
                } else {
                    game.resumeGame();
                }
            }
        });
        btStop.addActionListener(new ActionListener() {                         //停止游戏
            @Override
            public void actionPerformed(ActionEvent ae) {
                game.stopGame();
            }
        });
        btTurnLevelUp.addActionListener(new ActionListener() {                  //升高难度
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int level = Integer.parseInt(tfLevel.getText());
                    if (level < RussiaBlocksGame.MAX_LEVEL) {
                        tfLevel.setText("" + (level + 1));
                    }
                } catch (NumberFormatException e) {
                }
                requestFocus();
            }
        });
        btTurnLevelDown.addActionListener(new ActionListener() {                //降低游戏难度
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int level = Integer.parseInt(tfLevel.getText());
                    if (level > 1) {
                        tfLevel.setText("" + (level - 1));
                    }
                } catch (NumberFormatException e) {
                }
                requestFocus();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                plTipBlock.fanning();
            }
        });

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DateFormat format = new SimpleDateFormat("时间:HH:mm:ss");      //系统获得时间
                Date date = new Date();
                tfTime.setText(format.format(date));

                tfScore.setText("" + game.getScore());
                int ScoreForLevelUpdate = //判断当前分数是否能升级
                        game.getScoreForLevelUpdate();
                if (ScoreForLevelUpdate >= RussiaBlocksGame.PER_LEVEL_SCORE
                        && ScoreForLevelUpdate > 0) {
                    game.levelUpdate();
                }
            }
        });
        timer.start();
    }

    /**
     * 设置预显窗口的样式
     *
     * @param style int,对应ErsBlock类的STYLES中的28个值
     */
    public void setTipStyle(int style) {
        plTipBlock.setStyle(style);
    }

    /**
     * 取得用户设置的游戏等级。
     *
     * @return int ,难度等级，1-ErsBlocksGame.MAX_LEVEL
     */
    public int getLevel() {
        int level = 0;
        try {
            level = Integer.parseInt(tfLevel.getText());
        } catch (NumberFormatException e) {
        }
        return level;
    }

    /**
     * 让用户修改游戏难度等级。
     *
     * @param level 修改后的游戏难度等级
     */
    public void setLevel(int level) {
        if (level > 0 && level < 11) {
            tfLevel.setText("" + level);
        }
    }

    /**
     * 设置“开始”按钮的状态。
     */
    public void setPlayButtonEnable(boolean enable) {
        btPlay.setEnabled(enable);
    }

    public void setPauseButtonEnable(boolean enable) {
        btPause.setEnabled(enable);
    }

    public void setPauseButtonLabel(boolean pause) {
        btPause.setText(pause ? " 暂停" : " 继续");
    }

    public void setStopButtonEnable(boolean enable) {
        btStop.setEnabled(enable);
    }

    public void setTurnLevelUpButtonEnable(boolean enable) {
        btTurnLevelUp.setEnabled(enable);
    }

    public void setTurnLevelDownButtonEnable(boolean enable) {
        btTurnLevelDown.setEnabled(enable);
    }

    /**
     * 重置控制面板
     */
    public void reset() {
        tfScore.setText(" 0");
        plTipBlock.setStyle(0);
    }

    /**
     * 重新计算TipPanel里的boxes[][]里的小框的大小
     */
    public void fanning() {
        plTipBlock.fanning();
    }

    /**
     * 预显窗口的实现细节类
     */
	public class TipPanel extends JPanel {                                    //TipPanel用来显示下一个将要出现方块的形状

		private static final long serialVersionUID = 5160553671436997616L;
		private Color backColor = Color.darkGray, frontColor = Color.WHITE;
        private ErsBox[][] boxes = new ErsBox[ErsBlock.BOXES_ROWS][ErsBlock.BOXES_COLS];
        private int style, boxWidth, boxHeight;
        private boolean isTiled = false;

        /**
         * 预显示窗口类构造函数
         */
        public TipPanel() {
            for (int i = 0; i < boxes.length; i++) {
                for (int j = 0; j < boxes[i].length; j++) {
                    boxes[i][j] = new ErsBox(false);
                }
            }
        }

        /**
         * 设置预显示窗口的方块样式
         *
         * @param style int，对应ErsBlock类的STYLES中的28个值
         */
        public void setStyle(int style) {
            this.style = style;
            repaint();
        }

        /**
         * 覆盖JComponent类的函数，画组件。
         *
         * @param g 图形设备环境
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (!isTiled) {
                fanning();
            }

            int key = 0x8000;
            for (int i = 0; i < boxes.length; i++) {
                for (int j = 0; j < boxes[i].length; j++) {
                    Color color = ((key & style) != 0 ? frontColor : backColor);
                    g.setColor(color);
                    g.fill3DRect(j * boxWidth, i * boxHeight,
                            boxWidth, boxHeight, true);
                    key >>= 1;
                }
            }
        }

        /**
         * g根据窗口的大小，自动调整方格的尺寸
         */
        public void fanning() {
            boxWidth = getSize().width / ErsBlock.BOXES_COLS;
            boxHeight = getSize().height / ErsBlock.BOXES_ROWS;
            isTiled = true;
        }
    }
}

