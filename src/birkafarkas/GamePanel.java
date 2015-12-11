package birkafarkas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    Color[] colors = new Color[16];

    Node[][] map;

    Image corgi;
    Image kitten;
    Image hatter;

    Point selected;
    Point current;

    Who currentWho;

    long t;

    int sheepCount = 20;

    /**
     * Constructor.
     */
    public GamePanel() {
        /** Timer for Game Logic Tick */
        new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameLoop();
            }

            private void gameLoop() {
                t = System.currentTimeMillis();
                repaint();
            }
        }).start();

        /** Game Colors */
        colors[0] = new Color(104, 0, 105);     // Selected
        colors[1] = new Color(46, 162, 42);     // Cursor
        colors[2] = new Color(0, 0, 0);         // Border
        colors[3] = new Color(191, 40, 105);    // Jump Target
        colors[4] = new Color(89, 191, 11);     // Move Target

        /** Add Listeners */
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        /** Init Game */
        loadImages();
        initMap();
        initEntities();
    }

    /**
     * Main Paint Method.
     *
     * @param g Graphics
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(hatter, 0, 0, this.getWidth(), this.getHeight(), null);
        g2d.setStroke(new BasicStroke(7));

        int margin = 50;
        int padding = (this.getHeight() + this.getWidth()) / 20;
        int radius = (int) (padding / 1.5);

        g2d.setColor(colors[2]);

        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                if (map[x][y] != null) {
                    if (map[x][y].dir[2] == 1) {
                        g2d.draw(new Line2D.Float(margin + padding + (padding * x), margin + padding + (padding * y), margin + padding + padding + (padding * x), margin + padding + (padding * y)));
                    }
                    if (map[x][y].dir[3] == 1) {
                        g2d.draw(new Line2D.Float(margin + padding + (padding * x), margin + padding + (padding * y), margin + padding + padding + (padding * x), margin + padding + padding + (padding * y)));
                    }
                    if (map[x][y].dir[4] == 1) {
                        g2d.draw(new Line2D.Float(margin + padding + (padding * x), margin + padding + (padding * y), margin + padding + (padding * x), margin + padding + padding + (padding * y)));
                    }
                    if (map[x][y].dir[5] == 1) {
                        g2d.draw(new Line2D.Float(margin + padding + (padding * x), margin + padding + (padding * y), margin + (padding * x), margin + padding + padding + (padding * y)));
                    }

                    g2d.drawOval(margin + padding - 6 + (padding * x), margin + padding - 6 + (padding * y), 12, 12);
                    if (nextIsEmpty(x, y)) {
                        g2d.setColor(colors[4]);
                    }
                    if (isJumpable(x, y)) {
                        g2d.setColor(colors[3]);
                    }
                    g2d.fillOval(margin + padding - 6 + (padding * x), margin + padding - 6 + (padding * y), 12, 12);
                    g2d.setColor(colors[2]);


                    switch (map[x][y].who) {
                        case Nobody: {
                            break;
                        }
                        case Sheep: {
                            g2d.drawImage(kitten, margin + radius + (padding * x), margin + radius + (padding * y), radius, radius, null);
                            break;
                        }
                        case Wolf: {
                            g2d.drawImage(corgi, margin + radius + (padding * x), margin + radius + (padding * y), radius, radius, null);
                            break;
                        }
                    }
                }
            }
        }

        g2d.setStroke(new BasicStroke(((float) (0.12f * (2.0f + Math.sin(2.0f * Math.PI * t / 10.0f % 100.0f))) * 10.0f) + 4.0f));

        g2d.setColor(colors[0]);
        g2d.drawOval(margin + radius + (padding * current.x), margin + radius + (padding * current.y), radius, radius);
        g2d.setColor(colors[1]);
        if (map[selected.x][selected.y].who == Who.Nobody) {
            g2d.drawOval(margin + padding - 6 + (padding * selected.x), margin + padding - 6 + (padding * selected.y), 12, 12);
        } else {
            g2d.drawOval(margin + radius + (padding * selected.x), margin + radius + (padding * selected.y), radius, radius);
        }
    }

    /**
     * Wolf Won?
     *
     * @return boolean
     */
    private boolean wolfWon() {
        return (sheepCount == 0);
    }

    /**
     * Sheep Won?
     *
     * @return boolean
     */
    private boolean sheepWon() {
        boolean columnIsSolid = true;
        int wanderingSheep = sheepCount;

        for (int x = 6; x >= 0 && columnIsSolid; x--) {
            columnIsSolid = true;
            for (int y = 0; y <= 6 && wanderingSheep > 0; y++) {
                if (map[x][y] != null) {
                    if (map[x][y].who == Who.Sheep) {
                        wanderingSheep--;
                    } else {
                        columnIsSolid = false;
                    }
                }
            }
            if (wanderingSheep == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Initializes the Map.
     */
    private void initMap() {
        map = new Node[7][7];

        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                if ((y > 1 && y < 5) || (x > 1 && x < 5)) {
                    map[y][x] = new Node();
                    map[y][x].who = Who.Nobody;
                }
            }
        }

        fillRectangle(1, 3, true);
        fillRectangle(3, 1, true);
        fillRectangle(3, 5, true);
        fillRectangle(5, 3, true);
        fillRectangle(3, 3, false);
    }

    /**
     * Loads Images.
     */
    private void loadImages() {
        try {
            corgi = ImageIO.read(new File("corgi.png"));
            kitten = ImageIO.read(new File("kitten.png"));
            hatter = ImageIO.read(new File("table.jpg"));
        } catch (IOException e) {
            System.err.println("rekt: " + e.getMessage());
        }
    }

    /**
     * Initializes entities.
     */
    private void initEntities() {
        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                if (map[x][y] != null && (x < 4)) {
                    map[x][y].who = Who.Sheep;
                }
            }
        }

        map[5][3].who = Who.Wolf;
        selected = new Point(5, 3);
        current = new Point(5, 3);
        currentWho = map[5][3].who;
    }

    /**
     * Fills a 3*3 rectangle.
     *
     * @param x     int
     * @param y     int
     * @param atlos boolean
     */
    private void fillRectangle(int x, int y, boolean atlos) {
        map[x][y].dir[0] = 1;
        map[x][y].dir[2] = 1;
        map[x][y].dir[4] = 1;
        map[x][y].dir[6] = 1;         //     o
        map[x][y + 1].dir[0] = 1;     //     |
        map[x - 1][y].dir[2] = 1;     //   o-o-o
        map[x][y - 1].dir[4] = 1;     //     |
        map[x + 1][y].dir[6] = 1;     //     o

        /********************************/

        map[x][y - 1].dir[6] = 1;
        map[x][y - 1].dir[2] = 1;
        map[x][y + 1].dir[6] = 1;
        map[x][y + 1].dir[2] = 1;
        map[x - 1][y].dir[0] = 1;
        map[x - 1][y].dir[4] = 1;
        map[x + 1][y].dir[0] = 1;
        map[x + 1][y].dir[4] = 1;

        map[x - 1][y - 1].dir[2] = 1;
        map[x - 1][y - 1].dir[4] = 1;
        map[x + 1][y - 1].dir[6] = 1;
        map[x + 1][y - 1].dir[4] = 1;  //   o-o-o
        map[x - 1][y + 1].dir[0] = 1;  //   | | |
        map[x - 1][y + 1].dir[2] = 1;  //   o-o-o
        map[x + 1][y + 1].dir[0] = 1;  //   | | |
        map[x + 1][y + 1].dir[6] = 1;  //   o-o-o

        /********************************/

        if (atlos) {
            map[x][y].dir[1] = 1;
            map[x][y].dir[3] = 1;
            map[x][y].dir[5] = 1;
            map[x][y].dir[7] = 1;          //  o-o-o
            map[x - 1][y + 1].dir[1] = 1;  //  |\|/|
            map[x - 1][y - 1].dir[3] = 1;  //  o-o-o
            map[x + 1][y - 1].dir[5] = 1;  //  |/|\|
            map[x + 1][y + 1].dir[7] = 1;  //  o-o-o
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int margin = 50;
        int padding = (this.getHeight() + this.getWidth()) / 20;
        int a = (int) (padding / 1.5);
        int x = (e.getX() - margin - a) / padding;
        int y = (e.getY() - margin - a) / padding;

        go(x, y);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int margin = 50;
        int padding = (this.getHeight() + this.getWidth()) / 20;
        int a = (int) (padding / 1.5);
        int x = (e.getX() - margin - a) / padding;
        int y = (e.getY() - margin - a) / padding;

        select(x, y);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * GO!
     *
     * @param x int
     * @param y int
     */
    private void go(int x, int y) {
        if ((x >= 0 && x <= 6 && y >= 0 && y <= 6 && map[x][y] != null)) {
            if (nextIsEmpty(x, y)) {
                map[current.x][current.y].who = Who.Nobody;
                map[x][y].who = currentWho;
                current = new Point(x, y);
            } else {
                if (isJumpable(x, y)) {
                    map[current.x][current.y].who = Who.Nobody;
                    map[x][y].who = currentWho;
                    map[(current.x + ((x - current.x) / 2))][(current.y + ((y - current.y) / 2))].who = Who.Nobody;
                    current = new Point(x, y);
                    sheepCount--;
                } else {
                    if (map[x][y].who != Who.Nobody) {
                        current = new Point(x, y);
                        currentWho = map[x][y].who;
                    }
                }
            }

            Window.score.setText(" Remaining Sheep: " + sheepCount);

            if (sheepWon()) {
                Window.score.setText("Sheep Won!");
            }

            if (wolfWon()) {
                Window.score.setText("Wolf Won!");
            }

            repaint();
        }
    }

    /**
     * Select!
     *
     * @param x int
     * @param y int
     */
    private void select(int x, int y) {
        if ((x >= 0 && x <= 6 && y >= 0 && y <= 6 && map[x][y] != null)) {
            selected = new Point(x, y);
            repaint();
        }
    }

    /**
     * Is the next field Empty?
     *
     * @param x int
     * @param y int
     * @return boolean
     */
    private boolean nextIsEmpty(int x, int y) {
        int wx = current.x;
        int wy = current.y;
        int tardir = 0;

        if (x == wx && y < wy) {
            tardir = 0;
        }
        if (x > wx && y < wy) {
            tardir = 1;
        }
        if (x > wx && y == wy) {
            tardir = 2;
        }
        if (x > wx && y > wy) {
            tardir = 3;
        }
        if (x == wx && y > wy) {
            tardir = 4;
        }
        if (x < wx && y > wy) {
            tardir = 5;
        }
        if (x < wx && y == wy) {
            tardir = 6;
        }
        if (x < wx && y < wy) {
            tardir = 7;
        }

        return (Math.abs(x - wx) <= 1)
                && (Math.abs(y - wy) <= 1)
                && (map[wx][wy].dir[tardir] == 1)
                && (map[x][y].who == Who.Nobody);
    }

    /**
     * Is the next field Jumpable?
     *
     * @param x int
     * @param y int
     * @return boolean
     */
    private boolean isJumpable(int x, int y) {
        int wX = current.x;
        int wY = current.y;
        int betweenX = 0;
        int betweenY = 0;
        int tarDir = 0;

        if (x == wX && y < wY) {
            betweenX = 0;
            betweenY = -1;
            tarDir = 0;
        }
        if (x > wX && y < wY) {
            betweenX = 1;
            betweenY = -1;
            tarDir = 1;
        }
        if (x > wX && y == wY) {
            betweenX = 1;
            betweenY = 0;
            tarDir = 2;
        }
        if (x > wX && y > wY) {
            betweenX = 1;
            betweenY = 1;
            tarDir = 3;
        }
        if (x == wX && y > wY) {
            betweenX = 0;
            betweenY = 1;
            tarDir = 4;
        }
        if (x < wX && y > wY) {
            betweenX = -1;
            betweenY = 1;
            tarDir = 5;
        }
        if (x < wX && y == wY) {
            betweenX = -1;
            betweenY = 0;
            tarDir = 6;
        }
        if (x < wX && y < wY) {
            betweenX = -1;
            betweenY = -1;
            tarDir = 7;
        }

        return (map[current.x][current.y].who == Who.Wolf
                && (x >= 0)
                && (x <= 6)
                && (y >= 0)
                && (y <= 6)
                && map[x][y] != null
                && map[wX + betweenX][wY + betweenY] != null
                && (((Math.abs(x - wX) == 2) && (Math.abs(y - wY) == 0))
                || ((Math.abs(x - wX) == 0) && (Math.abs(y - wY) == 2))
                || ((Math.abs(x - wX) == 2) && (Math.abs(y - wY) == 2)))
                && (map[wX + betweenX][wY + betweenY].who == Who.Sheep)
                && (map[x][y].who == Who.Nobody)
                && (map[wX][wY].dir[tarDir] == 1)
        );
    }
}
