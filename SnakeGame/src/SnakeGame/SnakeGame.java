package SnakeGame;
    import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private final int TILE_SIZE = 25;
    private int WIDTH;
    private int HEIGHT;

    private final int DELAY = 120;
    private Timer timer;

    private ArrayList<Point> snake;
    private Point chicken;
    private int direction; // 0=up,1=right,2=down,3=left
    private boolean gameOver = false;
    private int score = 0;

    private Image chickenImg;
    private Image snakeHeadImg;
    private Image backgroundImg;

    private final int BORDER = 1; // ขอบรอบสนาม
    private boolean showStartScreen = true; // เริ่มเกมเมื่อกด ENTER

    public SnakeGame(int screenWidth, int screenHeight) {
        WIDTH = screenWidth / TILE_SIZE;
        HEIGHT = screenHeight / TILE_SIZE;

        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        snake = new ArrayList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = 1; // เริ่มเดินขวา

        spawnChicken();

        // โหลดภาพ
        chickenImg = new ImageIcon("src/SnakeGame/chicken.png").getImage();
        snakeHeadImg = new ImageIcon("src/SnakeGame/snakehead.png").getImage();
        backgroundImg = new ImageIcon("src/SnakeGame/BG.jpg").getImage();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void spawnChicken() {
        Random rand = new Random();
        int x = BORDER + rand.nextInt(WIDTH - BORDER * 2);
        int y = BORDER + rand.nextInt(HEIGHT - BORDER * 2);
        chicken = new Point(x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดพื้นหลัง
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE, this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        }

        // หน้าจอเริ่มต้น
        if (showStartScreen) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String msg = "Press ENTER to Start";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (WIDTH * TILE_SIZE - strWidth) / 2, HEIGHT * TILE_SIZE / 2);
            return;
        }

        // วาดเส้นตาราง
        g.setColor(new Color(50, 50, 50));
        for (int x = 0; x <= WIDTH; x++) {
            g.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, HEIGHT * TILE_SIZE);
        }
        for (int y = 0; y <= HEIGHT; y++) {
            g.drawLine(0, y * TILE_SIZE, WIDTH * TILE_SIZE, y * TILE_SIZE);
        }

        // วาดกรอบ
        g.setColor(Color.RED);
        g.drawRect(BORDER * TILE_SIZE, BORDER * TILE_SIZE,
                (WIDTH - BORDER * 2) * TILE_SIZE, (HEIGHT - BORDER * 2) * TILE_SIZE);

        // วาดงู
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == 0 && snakeHeadImg != null) {
                g.setColor(new Color(144, 196, 60));
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                int headSize = TILE_SIZE + 10;
                int headOffset = (headSize - TILE_SIZE) / 2;
                g.drawImage(snakeHeadImg,
                        p.x * TILE_SIZE - headOffset,
                        p.y * TILE_SIZE - headOffset,
                        headSize, headSize, this);
            } else {
                g.setColor(new Color(144, 196, 60)); // เขียวเข้ม
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // วาดไก่
        if (chickenImg != null) {
            int chickenSize = TILE_SIZE + 30;
            int chickenOffset = (chickenSize - TILE_SIZE) / 2;
            g.drawImage(chickenImg,
                    chicken.x * TILE_SIZE - chickenOffset,
                    chicken.y * TILE_SIZE - chickenOffset,
                    chickenSize, chickenSize, this);
        }

        // คะแนน
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);

        // จบเกม
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", WIDTH * TILE_SIZE / 2 - 90, HEIGHT * TILE_SIZE / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press ENTER to Restart", WIDTH * TILE_SIZE / 2 - 130, HEIGHT * TILE_SIZE / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !showStartScreen) {
            move();
            checkCollision();
            repaint();
        }
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        switch (direction) {
            case 0: newHead.y--; break;
            case 1: newHead.x++; break;
            case 2: newHead.y++; break;
            case 3: newHead.x--; break;
        }

        snake.add(0, newHead);

        if (newHead.equals(chicken)) {
            score += 10;
            spawnChicken();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollision() {
        Point head = snake.get(0);

        // ชนขอบสนาม
        if (head.x < BORDER || head.y < BORDER || head.x >= WIDTH - BORDER || head.y >= HEIGHT - BORDER) {
            gameOver = true;
            timer.stop();
        }

        // ชนตัวเอง
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                timer.stop();
            }
        }
    }

    // 🔁 ฟังก์ชันรีสตาร์ทเกม
    private void restartGame() {
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = 1;
        score = 0;
        gameOver = false;
        spawnChicken();
        timer.start();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // ESC เพื่อออก
        if (key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        // ENTER เพื่อเริ่มเกม / รีสตาร์ท
        if (showStartScreen && key == KeyEvent.VK_ENTER) {
            showStartScreen = false;
            repaint();
            return;
        }
        if (gameOver && key == KeyEvent.VK_ENTER) {
            restartGame();
            return;
        }

        // เปลี่ยนทิศทางถ้าไม่อยู่ในหน้าจอเริ่มและยังไม่จบ
        if (!showStartScreen && !gameOver) {
            if (key == KeyEvent.VK_UP && direction != 2) direction = 0;
            if (key == KeyEvent.VK_RIGHT && direction != 3) direction = 1;
            if (key == KeyEvent.VK_DOWN && direction != 0) direction = 2;
            if (key == KeyEvent.VK_LEFT && direction != 1) direction = 3;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        JFrame frame = new JFrame("Snake Game");
        SnakeGame gamePanel = new SnakeGame(screenWidth, screenHeight);

        frame.setUndecorated(true); // เต็มหน้าจอ ไม่มีขอบ
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // เต็มหน้าจอ
        frame.setVisible(true);
    }
}

