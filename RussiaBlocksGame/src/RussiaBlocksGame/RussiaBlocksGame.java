package RussiaBlocksGame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * 游戏主类，继承自JFrame类，负责游戏的全局控制。 内含： 1.一个GameCanvas画布类的实例对象，
 * 2.一个保存当前活动块（RussiaBlock）实例的对象； 3.一个保存当前控制面板（ControlPanel）实例的对象；
 */
public class RussiaBlocksGame extends JFrame {

	private static final long serialVersionUID = -7332245439279674749L;
	/**
     * 每填满一行计多少分
     */
    public final static int PER_LINE_SCORE = 100;
    /**
     * 积多少分以后能升级
     */
    public final static int PER_LEVEL_SCORE = PER_LINE_SCORE * 20;
    /**
     * 最大级数是10级
     */
    public final static int MAX_LEVEL = 10;
    /**
     * 默认级数是2
     */
    public final static int DEFAULT_LEVEL = 2;
    private GameCanvas canvas;
    private ErsBlock block;
    private boolean playing = false;
    private ControlPanel ctrlPanel;
    //初始化菜单栏
    private JMenuBar bar = new JMenuBar();
    private JMenu mGame = new JMenu(" 游戏"),
            mControl = new JMenu(" 控制"),
            mInfo = new JMenu("帮助");
    private JMenuItem miNewGame = new JMenuItem("新游戏"),
            miSetBlockColor = new JMenuItem("设置方块颜色..."),
            miSetBackColor = new JMenuItem("设置背景颜色..."),
            miTurnHarder = new JMenuItem("升高游戏难度"),
            miTurnEasier = new JMenuItem("降低游戏难度"),
            miExit = new JMenuItem("退出"),
            miPlay = new JMenuItem("开始"),
            miPause = new JMenuItem("暂停"),
            miResume = new JMenuItem("恢复"),
            miStop = new JMenuItem("终止游戏"),
            miRule = new JMenuItem("游戏规则"),
            miAuthor = new JMenuItem("关于本游戏");
    		
    
    /**
     * 建立并设置窗口菜单
     */
    private void creatMenu() {
        bar.add(mGame);
        bar.add(mControl);
        bar.add(mInfo);
        mGame.add(miNewGame);
        mGame.addSeparator();
        mGame.add(miSetBlockColor);
        mGame.add(miSetBackColor);
        mGame.addSeparator();
        mGame.add(miTurnHarder);
        mGame.add(miTurnEasier);
        mGame.addSeparator();
        mGame.add(miExit);
        mControl.add(miPlay);
        miPlay.setEnabled(true);
        mControl.add(miPause);
        miPause.setEnabled(false);
        mControl.add(miResume);
        miResume.setEnabled(false);
        mControl.add(miStop);
        miStop.setEnabled(false);
        mInfo.add(miRule);
        mInfo.add(miAuthor);
        setJMenuBar(bar);

        miNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGame();
                reset();
                setLevel(DEFAULT_LEVEL);
            }
        });
        
        //设置方块颜色
        miSetBlockColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newFrontColor =
                        JColorChooser.showDialog(RussiaBlocksGame.this, "设置方块颜色", canvas.getBlockColor());
                if (newFrontColor != null) {
                    canvas.setBlockColor(newFrontColor);
                }
            }
        });
        
        //设置背景颜色
        miSetBackColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newBackColor =
                        JColorChooser.showDialog(RussiaBlocksGame.this, "设置背景颜色", canvas.getBackgroundColor());
                if (newBackColor != null) {
                    canvas.setBackgroundColor(newBackColor);
                }
            }
        });
        
        //定义菜单栏"关于"的功能，弹出确认框。
        miAuthor.addActionListener(new ActionListener() {                        
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "软件工程(4)班\n3115005372\n杨宇杰\n©一切解释权归杨宇杰所有", "关于俄罗斯方块 - 2016", 1);
            }
        });
        
        //游戏规则说明
        miRule.addActionListener(new ActionListener() {                        
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "由小方块组成的不同形状的板块陆续从屏幕上方落下来，\n玩家通过调整板块的位置和方向，使它们在屏幕底部拼\n出完整的一条或几条。这些完整的横条会随即消失，给新\n落下来的板块腾出空间，与此同时，玩家得到分数奖励。\n没有被消除掉的方块不断堆积起来，一旦堆到屏幕顶端，\n玩家便告输，游戏结束。", "游戏规则", 1);
            }
        });
        
        //增加难度
        miTurnHarder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int curLevel = getLevel();
                if (!playing && curLevel < MAX_LEVEL) {
                    setLevel(curLevel + 1);
                }
            }
        });
        
        //减少难度
        miTurnEasier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int curLevel = getLevel();
                if (!playing && curLevel > 1) {
                    setLevel(curLevel - 1);
                }
            }
        });
        
        //退出按钮动作响应
        miExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

    }
    
    /**
     * 主游戏类的构造方法
     *
     * @param title String ,窗口标题
     */
    public RussiaBlocksGame(String title) {
        super(title);                                          //设置标题
        setSize(500, 600);                                     //设置窗口大小                  
        setLocationRelativeTo(null);                             //设置窗口居中

        creatMenu();
        Container container = getContentPane();					  //创建菜单栏
        container.setLayout(new BorderLayout(6, 0));              //设置窗口的布局管理器
        canvas = new GameCanvas(20, 15);                          //新建游戏画布
        ctrlPanel = new ControlPanel(this);                        //新建控制面板
        container.add(canvas, BorderLayout.CENTER);                //左边加上画布
        container.add(ctrlPanel, BorderLayout.EAST);               //右边加上控制面板

        //注册窗口事件。当点击关闭按钮时，结束游戏，系统退出。
        addWindowListener(new WindowAdapter() {                    
            @Override
            public void windowClosing(WindowEvent we) {
                stopGame();
                System.exit(0);
            }
        });
        
        //根据窗口大小，自动调节方格的尺寸
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                canvas.adjust();
            }
        });

        setVisible(true);
        canvas.adjust();
    }

    /**
     * 让游戏复位
     */
    public void reset() {                            //画布复位，控制面板复位
        ctrlPanel.setPlayButtonEnable(true);
        ctrlPanel.setPauseButtonEnable(false);
        ctrlPanel.setPauseButtonLabel(true);
        ctrlPanel.setStopButtonEnable(false);
        ctrlPanel.setTurnLevelDownButtonEnable(true);
        ctrlPanel.setTurnLevelUpButtonEnable(true);
        miPlay.setEnabled(true);
        miPause.setEnabled(false);
        miResume.setEnabled(false);
        miStop.setEnabled(false);
        ctrlPanel.reset();
        canvas.reset();
    }

    /**
     * 判断游戏是否还在进行
     *
     * @return boolean,true -还在运行，false-已经停止
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * 得到当前活动的块
     *
     * @return ErsBlock,当前活动块的引用
     */
    public ErsBlock getCurBlock() {
        return block;
    }

    /**
     * 得到当前画布
     *
     * @return GameCanvas,当前画布的引用
     */
    public GameCanvas getCanvas() {
        return canvas;
    }

    /**
     * 开始游戏
     */
    public void playGame() {
        play();
        ctrlPanel.setPlayButtonEnable(false);
        ctrlPanel.setPauseButtonEnable(true);
        ctrlPanel.setPauseButtonLabel(true);
        ctrlPanel.setStopButtonEnable(true);
        ctrlPanel.setTurnLevelDownButtonEnable(false);
        ctrlPanel.setTurnLevelUpButtonEnable(false);
        miStop.setEnabled(true);
        miTurnHarder.setEnabled(false);
        miTurnEasier.setEnabled(false);
        ctrlPanel.requestFocus();              //设置焦点
    }

    /**
     * 游戏暂停
     */
    public void pauseGame() {
        if (block != null) {
            block.pauseMove();
        }
        ctrlPanel.setPlayButtonEnable(false);
        ctrlPanel.setPauseButtonLabel(false);
        ctrlPanel.setStopButtonEnable(true);
        miPlay.setEnabled(false);
        miPause.setEnabled(false);
        miResume.setEnabled(true);
        miStop.setEnabled(true);
    }

    /**
     * 让暂停中的游戏继续
     */
    public void resumeGame() {
        if (block != null) {
            block.resumeMove();
        }
        ctrlPanel.setPlayButtonEnable(false);
        ctrlPanel.setPauseButtonEnable(true);
        ctrlPanel.setPauseButtonLabel(true);
        miPause.setEnabled(true);
        miResume.setEnabled(false);
        ctrlPanel.requestFocus();
    }

    /**
     * 用户停止游戏
     */
    public void stopGame() {
        playing = false;
        if (block != null) {
            block.stopMove();
        }
        ctrlPanel.setPlayButtonEnable(true);
        ctrlPanel.setPauseButtonEnable(false);
        ctrlPanel.setPauseButtonLabel(true);
        ctrlPanel.setStopButtonEnable(false);
        ctrlPanel.setTurnLevelDownButtonEnable(true);
        ctrlPanel.setTurnLevelUpButtonEnable(true);
        miPlay.setEnabled(true);
        miPause.setEnabled(false);
        miResume.setEnabled(false);
        miStop.setEnabled(false);
        miTurnHarder.setEnabled(true);
        miTurnEasier.setEnabled(true);
        reset();//重置画布和控制面板
    }


    /**
     * 得到游戏者设置的难度
     *
     * @return int ，游戏难度1-MAX_LEVEL
     */
    public int getLevel() {
        return ctrlPanel.getLevel();
    }

    /**
     * 用户设置游戏难度
     *
     * @param level int ，游戏难度1-MAX_LEVEL
     */
    public void setLevel(int level) {
        if (level < 11 && level > 0) {
            ctrlPanel.setLevel(level);
        }
    }

    /**
     * 得到游戏积分
     *
     * @return int，积分
     */
    public int getScore() {
        if (canvas != null) {
            return canvas.getScore();
        }
        return 0;
    }

    /**
     * 得到自上次升级以来的游戏积分，升级以后，此积分清零
     *
     * @return int,积分
     */
    public int getScoreForLevelUpdate() {
        if (canvas != null) {
            return canvas.getScoreForLevelUpdate();
        }
        return 0;
    }

    /**
     * 当积分累积到一定数值时，升一次级
     *
     * @return Boolean，true-update succeed，false-update fail
     */
    public boolean levelUpdate() {
        int curLevel = getLevel();
        if (curLevel < MAX_LEVEL) {
            setLevel(curLevel + 1);
            canvas.resetScoreForLevelUpdate();
            return true;
        }
        return false;
    }

    /**
     * 游戏开始
     */
    private void play() {
        reset();
        playing = true;
        Thread thread = new Thread(new Game());//启动游戏线程
        thread.start();
    }

    /**
     * 报告游戏结束了
     */
    private void reportGameOver() {
        new gameOverDialog(this, "俄罗斯方块", "游戏结束，您的得分为" + canvas.getScore());
    }


    /**
     * 一轮游戏过程，实现了Runnable接口 一轮游戏是一个大循环，在这个循环中，每隔100毫秒， 检查游戏中的当前块是否已经到底了，如果没有，
     * 就继续等待。如果到底了，就看有没有全填满的行， 如果有就删除它，并为游戏者加分，同时随机产生一个新的当前块并让它自动落下。
     * 当新产生一个块时，先检查画布最顶上的一行是否已经被占了，如果是，可以判断Game Over 了。
     */
private class Game implements Runnable {
       @Override
        public void run() {
            int col = (int) (Math.random() * (canvas.getCols() - 3));
            int style = ErsBlock.STYLES[ (int) (Math.random() * 7)][(int) (Math.random() * 4)];

            while (playing) {
                if (block != null) {   //第一次循环时，block为空
                    if (block.isAlive()) {
                        try {
                            Thread.currentThread();
    						Thread.sleep(500);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                        continue;
                    }
                }

                checkFullLine();    //检查是否有全填满的行

                if (isGameOver()) {
                    reportGameOver();
                    miPlay.setEnabled(true);
                    miPause.setEnabled(false);
                    miResume.setEnabled(false);
                    miStop.setEnabled(false);
                    ctrlPanel.setPlayButtonEnable(true);
                    ctrlPanel.setPauseButtonLabel(false);
                    ctrlPanel.setStopButtonEnable(false);
                    return;
                }

                block = new ErsBlock(style, -1, col, getLevel(), canvas);
                block.start();

                col = (int) (Math.random() * (canvas.getCols() - 3));
                style = ErsBlock.STYLES[ (int) (Math.random() * 7)][(int) (Math.random() * 4)];

                ctrlPanel.setTipStyle(style);
            }
        }

        //检查画布中是否有全填满的行，如果有就删之
        public void checkFullLine() {
            for (int i = 0; i < canvas.getRows(); i++) {
                int row = -1;
                boolean fullLineColorBox = true;
                for (int j = 0; j < canvas.getCols(); j++) {
                    if (!canvas.getBox(i, j).isColorBox()) {
                        fullLineColorBox = false;
                        break;
                    }
                }
                if (fullLineColorBox) {
                    row = i--;
                    canvas.removeLine(row);
                }
            }
        }

        //根据最顶行是否被占，判断游戏是否已经结束了
        //@return boolean ，true-游戏结束了，false-游戏未结束
        private boolean isGameOver() {
            for (int i = 0; i < canvas.getCols(); i++) {
                ErsBox box = canvas.getBox(0, i);
                if (box.isColorBox()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 定义GameOver对话框。
     */
    @SuppressWarnings("serial")
    private class gameOverDialog extends JDialog implements ActionListener {

        private JButton againButton, exitButton;
        private Border border = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));

        public gameOverDialog(JFrame parent, String title, String message) {
            super(parent, title, true);
            if (parent != null) {
                setSize(240, 120);
                this.setLocationRelativeTo(parent);
                JPanel messagePanel = new JPanel();
                messagePanel.add(new JLabel(message));
                messagePanel.setBorder(border);
                Container container = this.getContentPane();
                container.setLayout(new GridLayout(2, 0, 0, 10));
                container.add(messagePanel);
                JPanel choosePanel = new JPanel();
                choosePanel.setLayout(new GridLayout(0, 2, 4, 0));
                container.add(choosePanel);
                againButton = new JButton("再玩一局");
                exitButton = new JButton("退出游戏");
                choosePanel.add(new JPanel().add(againButton));
                choosePanel.add(new JPanel().add(exitButton));
                choosePanel.setBorder(border);
            }
            againButton.addActionListener(this);
            exitButton.addActionListener(this);
            this.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == againButton) {
                this.setVisible(false);
                reset();
            } else if (e.getSource() == exitButton) {
                stopGame();
                System.exit(0);

            }
        }
    }
}

