package assignment1;

// Skeletal program for the "Image Histogram" assignment
// Written by:  Minglun Gong

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class ImageHistogram extends Frame implements ActionListener {
	BufferedImage input;
	int width, height;
	TextField texRad, texThres;
	ImageCanvas source, target;
	PlotCanvas plot;
	// Constructor
	public ImageHistogram(String name) {
		super("Image Histogram");
		// load image
		try {
			input = ImageIO.read(getClass().getResource("/images/" + name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		// prepare the panel for image canvas.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		plot = new PlotCanvas();
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 3, 10, 10));
		main.add(source);
		main.add(plot);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Display Histogram");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Histogram Stretch");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Cutoff fraction:"));
		texThres = new TextField("10", 2);
		controls.add(texThres);
		button = new Button("Aggressive Stretch");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Histogram Equalization");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(width*2+400, height+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
                int[] redMinMax = {255, 0};
                int[] blueMinMax = {255, 0};
                int[] greenMinMax = {255, 0};
                int max = 0;
		// example -- compute the average color for the image
		if ( ((Button)e.getSource()).getLabel().equals("Display Histogram") ) {
			int[] red = new int[256];
                        int[] blue = new int[256];
                        int[] green = new int[256];
			for ( int y=0 ; y<height ; y++ )
				for ( int x=0 ; x<width ; x++ ) {
					Color clr = new Color(input.getRGB(x, y));
                                        red[clr.getRed()] += 1;
                                        blue[clr.getBlue()] += 1;
                                        green[clr.getGreen()] += 1;
				}
			plot.setMeanColor(red, green, blue);
		} else if ( ((Button)e.getSource()).getLabel().equals("Histogram Stretch") ) {
			int[] red = new int[256];
                        int[] blue = new int[256];
                        int[] green = new int[256];
			for ( int y=0 ; y<height ; y++ )
				for ( int x=0 ; x<width ; x++ ) {
					Color clr = new Color(input.getRGB(x, y));
                                        int currentRed = clr.getRed();
                                        if (currentRed < redMinMax[0]) {
                                            redMinMax[0] = currentRed;
                                        }
                                        if (currentRed > redMinMax[1]) {
                                            redMinMax[1] = currentRed;
                                        }
                                        red[currentRed] += 1;
                                        
                                        int currentBlue = clr.getBlue();
                                        if (currentBlue < blueMinMax[0]) {
                                            blueMinMax[0] = currentBlue;
                                        }
                                        if (currentBlue > blueMinMax[1]) {
                                            blueMinMax[1] = currentBlue;
                                        }
                                        blue[currentBlue] += 1;
                                        
                                        int currentGreen = clr.getGreen();
                                        if (currentGreen < greenMinMax[0]) {
                                            greenMinMax[0] = currentGreen;
                                        }
                                        if (currentGreen > greenMinMax[1]) {
                                            greenMinMax[1] = currentGreen;
                                        }
                                        green[currentGreen] += 1;
				};
			plot.setMeanColor(this.stretchTo256(Arrays.copyOfRange(red, redMinMax[0], redMinMax[1])), this.stretchTo256(Arrays.copyOfRange(green, greenMinMax[0], greenMinMax[1])), this.stretchTo256(Arrays.copyOfRange(blue, blueMinMax[0], blueMinMax[1])));
		}
	}
        
        public int[] stretchTo256(int[] toStretch) {
                int[] newArray = new int[256];
                newArray[0] = toStretch[0];
                newArray[255] = toStretch[toStretch.length - 1];
                for (int i = 1; i < 255; i++) {
                    newArray[i] = toStretch[(int) ((i / 256.0) * toStretch.length)];
                }
                return newArray;
        }
        
	public static void main(String[] args) {
		new ImageHistogram(args.length==1 ? args[0] : "baboon.png");
	}
}

// Canvas for plotting histogram
class PlotCanvas extends Canvas {
	// lines for plotting axes and mean color locations
	LineSegment x_axis, y_axis;
	LineSegment[] red = new LineSegment[255];
        LineSegment[] green = new LineSegment[255];
        LineSegment[] blue = new LineSegment[255];
        
	boolean showMean = false;
        
        public int maxOfArr(int[] arr) {
            int max = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] > max) {
                    max = arr[i];
                }
            }
            return max;
        }

	public PlotCanvas() {
		x_axis = new LineSegment(Color.BLACK, -10, 0, 256+10, 0);
		y_axis = new LineSegment(Color.BLACK, 0, -10, 0, 200+10);
	}
	// set mean image color for plot
	public void setMeanColor(int[] redArr, int[] greenArr, int[] blueArr) {
                int max = Math.max(this.maxOfArr(redArr), Math.max(this.maxOfArr(blueArr), this.maxOfArr(greenArr)));
                System.out.println(max);
                for (int i = 1; i < 256; i++) {
                        System.out.println((int)(600 * (redArr[i - 1] / (float)(max))));
                        red[i-1] = new LineSegment(Color.RED, i - 1, (int)(200 * (redArr[i - 1] / (float)(max))), i, (int)(200 * (redArr[i] / (float)(max))));
                        blue[i-1] = new LineSegment(Color.BLUE, i - 1, (int)(200 * (blueArr[i - 1] / (float)(max))), i, (int)(200 * (blueArr[i] / (float)(max))));
                        green[i-1] = new LineSegment(Color.GREEN, i - 1, (int)(200 * (greenArr[i - 1] / (float)(max))), i, (int)(200 * (greenArr[i] / (float)(max))));
                        showMean = true;
                }
		repaint();
	}
	// redraw the canvas
	public void paint(Graphics g) {
		// draw axis
		int xoffset = (getWidth() - 256) / 2;
		int yoffset = (getHeight() - 200) / 2;
		x_axis.draw(g, xoffset, yoffset, getHeight());
		y_axis.draw(g, xoffset, yoffset, getHeight());
		if ( showMean ) {
                        for (int i = 0; i < 255; i++) {
                                red[i].draw(g, xoffset, yoffset, getHeight());
                                green[i].draw(g, xoffset, yoffset, getHeight());
                                blue[i].draw(g, xoffset, yoffset, getHeight());
                        };
		}
	}
}

// LineSegment class defines line segments to be plotted
class LineSegment {
	// location and color of the line segment
	int x0, y0, x1, y1;
	Color color;
	// Constructor
	public LineSegment(Color clr, int x0, int y0, int x1, int y1) {
		color = clr;
		this.x0 = x0; this.x1 = x1;
		this.y0 = y0; this.y1 = y1;
	}
	public void draw(Graphics g, int xoffset, int yoffset, int height) {
		g.setColor(color);
		g.drawLine(x0+xoffset, height-y0-yoffset, x1+xoffset, height-y1-yoffset);
	}
}
