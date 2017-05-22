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
 * ��������࣬�̳���JPanel�� �ϱ߰���Ԥ�Դ��ڣ��ȼ����÷֣����ư�ť ��Ҫ����������Ϸ���̡�
 */
class ControlPanel extends JPanel {

	private static final long serialVersionUID = 3900659640646175724L;
	private JTextField tfLevel = new JTextField("" + RussiaBlocksGame.DEFAULT_LEVEL),
            tfScore = new JTextField(" 0"),
            tfTime = new JTextField(" ");
    private JButton btPlay = new JButton(" ��ʼ"),
            btPause = new JButton(" ��ͣ"),
            btStop = new JButton("��ֹ��Ϸ"),
            btTurnLevelUp = new JButton(" �����Ѷ�"),
            btTurnLevelDown = new JButton(" �����Ѷ�");
    private JPanel plTip = new JPanel(new BorderLayout());
    private TipPanel plTipBlock = new TipPanel();
    private JPanel plInfo = new JPanel(new GridLayout(4, 1));
    private JPanel plButton = new JPanel(new GridLayout(6, 1));
    private Timer timer;
    private Border border = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));

    /**
     * ���������Ĺ��캯��
     *
     * @param game ErsBlocksGame,ErsBlocksGame ���һ��ʵ������ ����ֱ�ӿ���ErsBlocksGame�����Ϊ��
     */
    public ControlPanel(final RussiaBlocksGame game) {
        setLayout(new GridLayout(3, 1, 0, 2));
        plTip.add(new JLabel(" ��һ������"), BorderLayout.NORTH);               //������
        plTip.add(plTipBlock);
        plTip.setBorder(border);

        plInfo.add(new JLabel(" �Ѷ�ϵ��"));
        plInfo.add(tfLevel);
        plInfo.add(new JLabel(" �÷�"));
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

        btPlay.addActionListener(new ActionListener() {                         //��ʼ��Ϸ
            @Override
            public void actionPerformed(ActionEvent ae) {
                game.playGame();
            }
        });
        btPause.addActionListener(new ActionListener() {                        //��ͣ��Ϸ
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (btPause.getText().equals(" ��ͣ")) {
                    game.pauseGame();
                } else {
                    game.resumeGame();
                }
            }
        });
        btStop.addActionListener(new ActionListener() {                         //ֹͣ��Ϸ
            @Override
            public void actionPerformed(ActionEvent ae) {
                game.stopGame();
            }
        });
        btTurnLevelUp.addActionListener(new ActionListener() {                  //�����Ѷ�
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
        btTurnLevelDown.addActionListener(new ActionListener() {                //������Ϸ�Ѷ�
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
                DateFormat format = new SimpleDateFormat("ʱ��:HH:mm:ss");      //ϵͳ���ʱ��
                Date date = new Date();
                tfTime.setText(format.format(date));

                tfScore.setText("" + game.getScore());
                int ScoreForLevelUpdate = //�жϵ�ǰ�����Ƿ�������
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
     * ����Ԥ�Դ��ڵ���ʽ
     *
     * @param style int,��ӦErsBlock���STYLES�е�28��ֵ
     */
    public void setTipStyle(int style) {
        plTipBlock.setStyle(style);
    }

    /**
     * ȡ���û����õ���Ϸ�ȼ���
     *
     * @return int ,�Ѷȵȼ���1-ErsBlocksGame.MAX_LEVEL
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
     * ���û��޸���Ϸ�Ѷȵȼ���
     *
     * @param level �޸ĺ����Ϸ�Ѷȵȼ�
     */
    public void setLevel(int level) {
        if (level > 0 && level < 11) {
            tfLevel.setText("" + level);
        }
    }

    /**
     * ���á���ʼ����ť��״̬��
     */
    public void setPlayButtonEnable(boolean enable) {
        btPlay.setEnabled(enable);
    }

    public void setPauseButtonEnable(boolean enable) {
        btPause.setEnabled(enable);
    }

    public void setPauseButtonLabel(boolean pause) {
        btPause.setText(pause ? " ��ͣ" : " ����");
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
     * ���ÿ������
     */
    public void reset() {
        tfScore.setText(" 0");
        plTipBlock.setStyle(0);
    }

    /**
     * ���¼���TipPanel���boxes[][]���С��Ĵ�С
     */
    public void fanning() {
        plTipBlock.fanning();
    }

    /**
     * Ԥ�Դ��ڵ�ʵ��ϸ����
     */
	public class TipPanel extends JPanel {                                    //TipPanel������ʾ��һ����Ҫ���ַ������״

		private static final long serialVersionUID = 5160553671436997616L;
		private Color backColor = Color.darkGray, frontColor = Color.WHITE;
        private ErsBox[][] boxes = new ErsBox[ErsBlock.BOXES_ROWS][ErsBlock.BOXES_COLS];
        private int style, boxWidth, boxHeight;
        private boolean isTiled = false;

        /**
         * Ԥ��ʾ�����๹�캯��
         */
        public TipPanel() {
            for (int i = 0; i < boxes.length; i++) {
                for (int j = 0; j < boxes[i].length; j++) {
                    boxes[i][j] = new ErsBox(false);
                }
            }
        }

        /**
         * ����Ԥ��ʾ���ڵķ�����ʽ
         *
         * @param style int����ӦErsBlock���STYLES�е�28��ֵ
         */
        public void setStyle(int style) {
            this.style = style;
            repaint();
        }

        /**
         * ����JComponent��ĺ������������
         *
         * @param g ͼ���豸����
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
         * g���ݴ��ڵĴ�С���Զ���������ĳߴ�
         */
        public void fanning() {
            boxWidth = getSize().width / ErsBlock.BOXES_COLS;
            boxHeight = getSize().height / ErsBlock.BOXES_ROWS;
            isTiled = true;
        }
    }
}

