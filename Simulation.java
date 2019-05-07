import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Simulation {

    public static void main(String[] args) {
        new Simulation();
    }

    public Simulation() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Simulation");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {
    	private Timer repaintTimer;
        private final Set<Integer> pressed = new HashSet<Integer>();
        Block rob = new Block(Color.RED, 110, 200, 60, 100, 0);
        List<Block> sensors = new LinkedList<>();
        List<Block> lines = new LinkedList<>();
        List<Block> allBlocks = new LinkedList<>();
       
        public TestPane() {
//        	lines.add(new Block(Color.BLACK, 400, 400, 400, 15, 0));
        	lines.add(new Block(Color.BLACK, 239, 401, 12, 81, -271));
        	lines.add(new Block(Color.BLACK, 321, 371, 15, 107, 304));
        	lines.add(new Block(Color.BLACK, 399, 340, 15, 81, 89));
        	lines.add(new Block(Color.BLACK, 460, 370, 15, 79, 41));
        	lines.add(new Block(Color.BLACK, 540, 401, 15, 117, 90));
        	
        	
        	
        	lines.add(new Block(Color.BLACK, 400, 100, 400, 15, 0));

        	lines.add(new Block(Color.BLACK, 174, 111, 64, 13, 24));
        	lines.add(new Block(Color.BLACK, 129, 155, 12, 73, -30)); 
        	lines.add(new Block(Color.BLACK, 110, 229, 12, 94, 0));
        	lines.add(new Block(Color.BLACK, 119, 307, 12, 75, 18));
        	lines.add(new Block(Color.BLACK, 167, 370, 12, 97, 49)); 
        	
        	lines.add(new Block(Color.BLACK, 628, 131, 15, 93, 46));
        	lines.add(new Block(Color.BLACK, 671, 209, 12, 97, 193)); 
        	lines.add(new Block(Color.BLACK, 670, 305, 15, 107, -13));
        	lines.add(new Block(Color.BLACK, 628, 379, 12, 81, 128));
        	
        	sensors.add(new Block(Color.BLUE, 20, 20, 3, 3, -15));
        	sensors.add(new Block(Color.BLUE, 25, 25, 3, 3, 15));
        	
        	allBlocks.add(rob);
        	allBlocks.addAll(sensors);
        	allBlocks.addAll(lines);
        	
        	this.addMouseListener(new MouseListener() {
        		int startx = 0;
        		int starty = 0;
        		
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() != 2)
						return;
					
					int endx = e.getX();
					int endy = (e.getY() - 600) * -1;

		        	int dist = getDistanceBetween(startx, starty, endx, endy);
		        	int rot = getAngleBetween(startx, starty, endx, endy);
		        	
					Block newLine = new Block(Color.BLACK, startx, starty, 15, dist, rot);
		        	lines.add(newLine);
					allBlocks.add(newLine);
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					startx = e.getX();
					starty = (e.getY() - 600) * -1;
					System.out.println(startx + ", " + starty);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {}
				
				@Override
				public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
        	
        	this.addKeyListener(new KeyListener() {
                @Override
                public synchronized void keyPressed(KeyEvent e) {
                    pressed.add(e.getKeyCode());
                }

                @Override
                public synchronized void keyReleased(KeyEvent e) {
                    pressed.remove(e.getKeyCode());
                }

                @Override
                public void keyTyped(KeyEvent e) {}
			});
            this.setFocusable(true);

            repaintTimer = new Timer(60, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	checkKeys();
                	move();
                    repaint();
                }
            });
            repaintTimer.setInitialDelay(0);
            repaintTimer.setRepeats(true);
            repaintTimer.setCoalesce(true);

            repaintTimer.start();
        }
        
    	@Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 600);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // origin at bottom left
            g2d.scale(1.0, -1.0);
            g2d.translate(0, -getHeight());
            
            for (int i = 0; i < allBlocks.size(); i++) {
            	Block elem = allBlocks.get(i);
                g2d.setColor(elem.color);
                g2d.rotate(Math.toRadians(-elem.rot), elem.x, elem.y);
            	g2d.fillRect(elem.x - elem.w/2, elem.y - elem.h/2, elem.w, elem.h);
            	g2d.rotate(Math.toRadians(elem.rot), elem.x, elem.y);
            	
                g2d.setColor(Color.GREEN);
            	g2d.fillRect(elem.x, elem.y, 2, 2);
            }
            
            // restore origin
//            g2d.translate(0, getHeight());
//            g2d.scale(1.0, -1.0);

            g2d.dispose();
        }
        
        public void move() {
    		Block leftSensor = sensors.get(0);
    		Block rightSensor = sensors.get(1);
    		
    		boolean left = false;
    		boolean right = false;
    		
        	for (Block line : lines) {
//        		for (Block sensor : sensors) {
//        			if (line.isPointInside(sensor.x, sensor.y)) {
//                		rob.vely = 0;
//                    }
//				}
        		
    			if (line.isPointInside(leftSensor.x, leftSensor.y))
	        		left = true;
	            
    			if (line.isPointInside(rightSensor.x, rightSensor.y))
	        		right = true;
        		
        	}
        	
        	if (!left && !right) {
        		Point p2 = getPointFromDistanceAndRotation(0, 0, rob.vely, rob.rot);
              	rob.x += p2.x;
              	rob.y += p2.y;
        	} else if (left) {
        		Point p2 = getPointFromDistanceAndRotation(0, 0, rob.vely, rob.rot);
              	rob.x += p2.x;
              	rob.y += p2.y;
              	rob.rot -= rob.velrot;
        	} else if (right) {
        		Point p2 = getPointFromDistanceAndRotation(0, 0, rob.vely, rob.rot);
              	rob.x += p2.x;
              	rob.y += p2.y;
              	rob.rot += rob.velrot;
        	}
        		
            
    		for (Block sensor : sensors) {
    			Point p = getPointFromDistanceAndRotation(rob.x, rob.y, 60, rob.rot + sensor.rot);
    			sensor.x = (int) p.x;
    			sensor.y = (int) p.y;
			}
        }
        
        public void checkKeys() {
        	Block elem = rob;
        	int factor = 2;
        	if (pressed.contains(KeyEvent.VK_ALT))
        		factor = 1;
        	
        	if (pressed.contains(KeyEvent.VK_SHIFT))
        		factor = 3;
        	
        	if (pressed.contains(KeyEvent.VK_SPACE))
        		elem = lines.get(lines.size() - 1);
        	
        	
        	if (pressed.contains(KeyEvent.VK_A))
        		elem.velx = -3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_D))
        		elem.velx = 3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_S))
        		elem.vely = -3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_W))
        		elem.vely = 3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_LEFT))
        		elem.velrot = -3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_RIGHT))
        		elem.velrot = 3 * factor;
        	
        	
        	if (pressed.contains(KeyEvent.VK_MINUS))
        		elem.w += -3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_PLUS))
        		elem.w += 3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_COMMA))
        		elem.h += -3 * factor;
        	
        	if (pressed.contains(KeyEvent.VK_PERIOD))
        		elem.h += 3 * factor;
        	
        	
        	if (pressed.contains(KeyEvent.VK_I))
        		System.out.println(" x:" + elem.x
        				+ " y: " + elem.y
        				+ " w:" + elem.w
        				+ " h:" + elem.h
        				+ " rot:" + elem.rot
        				+ " velx:" + elem.velx
        				+ " vely:" + elem.vely
        				+ " velrot:" + elem.velrot);
        	
        }
        
    }
    
    public Point getPointFromDistanceAndRotation(int x, int y, int dist, int rot) {
    	double newx = x + dist*Math.cos(Math.toRadians(-rot) + Math.toRadians(90));
    	double newy = y + dist*Math.sin(Math.toRadians(-rot) + Math.toRadians(90));
    	return new Point(newx, newy);
    }
    
    public int getDistanceBetween(int x1, int y1, int x2, int y2) {
    	return (int) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
    
    public int getAngleBetween(int x1, int y1, int x2, int y2) {
    	double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
    	angle = (angle + 720) % 360;
    	return (int) angle;
    }
    
    public class Block {
    	Color color;
    	int x;
    	int y;
    	int w;
    	int h;
    	int rot;
    	
    	int velx;
    	int vely;
    	int velrot;
    	
    	Block(Color color, int x, int y, int w, int h, int r) {
    		this.color = color;
    		this.x = x;
    		this.y = y;
    		this.w = w;
    		this.h = h;
    		this.rot = r;
    	}
    	
    	public boolean isPointInside(int x, int y) {
        	int dist = getDistanceBetween(this.x, this.y, x, y);
        	int rot = getAngleBetween(this.x, this.y, x, y);

            Point p2 = getPointFromDistanceAndRotation(this.x, this.y, dist, rot - this.rot);
    		
    		return isPointInsideWithoutAngle((int) p2.x, (int) p2.y);
    	}
    	
    	public boolean isPointInsideWithoutAngle(int x, int y) {
    		if ((x > this.x - this.w/2 && x < this.x + this.w/2) &&
    			(y > this.y - this.h/2 && y < this.y + this.h/2))
    			return true;
    		
    		return false;
    	}
    }
    
    public class Point {
    	double x;
    	double y;
    	
    	public Point(double x, double y) {
    		this.x = x;
    		this.y = y;
    	}
    }
    
}
