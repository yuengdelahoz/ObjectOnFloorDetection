package delahoz.ObjectOnFloor.structured;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.util.Log;

public class ObjectDetection {

	// Andrew's Private variables for changing the pixels to red below:

	/*
	 * Precondition: None Post Condition: Declares a reference point of class
	 * Point from openCV Point will be the reference point that is the highest
	 * point on the "Y" axis
	 */
	private Point referencePoint;

	/*
	 * Iterates through the image boundary and changes pixel to red based on the
	 * properties
	 * 
	 * Precondition: Two defined lines are passed, along with img.
	 * 
	 * PostCondition: Calculates reference point and stores it in the private
	 * instance variable Iterates through row and column ignoring any pixel
	 * above the reference point Performs calculations on pixel (row,col) based
	 * on whether it is inside the boundary of the floor
	 * 
	 * Turns pixel at (row,col) red if it satisfies the boundary condition
	 */
	public Mat ObjectDetection(Line ObliqueLeft, Line ObliqueRight, Mat image) {
		// TODO Auto-generated method stub

		Pixel imagePixel[][] = new Pixel[image.height()][image.width()];

		ArrayList<ArrayList<Pixel>> clusterList = new ArrayList<ArrayList<Pixel>>();

		// First find the reference point by checking the Y value
		if (ObliqueLeft.getStart().y < ObliqueRight.getStart().y) {
			referencePoint = ObliqueLeft.getStart();
		}

		else {
			referencePoint = ObliqueRight.getStart();
		}

		// Get the slope and y-intercept from lines
		double m1 = ObliqueLeft.getSlope(), m2 = ObliqueRight.getSlope();
		double b1 = ObliqueLeft.yIntercept(), b2 = ObliqueRight.yIntercept();

		// Iterate through the rows and colums to process each pixel
		for (int y = 0; y < image.height(); y++) {
			for (int x = 0; x < image.width(); x++) {

				if (y > referencePoint.y ) {

					// Get the x value on the line
					double x1 = (y - b1) / m1;
					double x2 = (y - b2) / m2;

					if (x >= x1+30 && x <= x2-10) {

						double color[] = image.get(y, x);
						double r, g, b;
						r = color[0];
						b = color[1];
						g = color[2];
						double avg = (r + b + g) / 3;
						if (avg > 90) {
							// White
							avg = 255;
						} else {
							// Black
							avg = 0;
							imagePixel[y][x] = new Pixel(y, x, image);
						}
						// double[] data = { avg, avg, avg};
						// image.put(y, x, data);
					}
				}

			}
		}

		for (int y = 0; y < image.height(); y++) {
			if (y == image.height() - 1)
				break;
			for (int x = 0; x < image.width(); x++) {
				if (x == image.width() - 1)
					break;

				if (imagePixel[y][x] == null || imagePixel[y][x].visited || imagePixel[y][x].isInStack) {
					continue;
				}
				ArrayList<Pixel> cluster = new ArrayList<Pixel>();

				Pixel temp = imagePixel[y][x];
				// add temp pixel to cluster

				Stack<Pixel> p = new Stack<Pixel>();
				temp.visited = true;
				cluster.add(temp);

				// are the neighbors black?
				ArrayList<Pixel> Neighbors = temp.getNeighbors(imagePixel);
				for (int i = 0; i < Neighbors.size(); i++) {
					Pixel tempN = Neighbors.get(i);
					if (!tempN.visited)
						if (!tempN.isInStack)
							p.push(tempN);

				}
				while (!p.empty()) {
					Pixel tempP = p.pop();
					tempP.isInStack = true;
					tempP.visited = true;
					cluster.add(tempP);

					Neighbors = tempP.getNeighbors(imagePixel);
					for (int i = 0; i < Neighbors.size(); i++) {
						Pixel tempN = Neighbors.get(i);
						if (!tempN.visited)
							if (!tempN.isInStack)
								p.push(tempN);
					}

				}

				// add to clusterList
				clusterList.add(cluster);
			}

		}

		boolean isReady = false;
		for (int i = 0; i < clusterList.size(); i++) {
			ArrayList<Pixel> tempCluster = clusterList.get(i);
			if (tempCluster.size() < 1000)
				continue;
		
			for (int j = 0; j < tempCluster.size(); j++) {
				Pixel pix = tempCluster.get(j);
				int center = image.width() / 2;
				if (Math.abs(pix.x - center) < 50) {
					isReady=true;
					break;
				}
			}
			if (!isReady)
				continue;
			
			int r = new Random().nextInt(256);
			int g = new Random().nextInt(256);
			int b = new Random().nextInt(256);
			int xCoor =0;
			int yCoor = 0;
 			for (int j = 0; j < tempCluster.size(); j++) {
 				
				Pixel pix = tempCluster.get(j);
				xCoor+=pix.x;
				yCoor+=pix.y;
				double[] data = { r, g, b, 0};
				image.put(pix.y, pix.x, data);
			}
 			Log.i("Centroid", xCoor/tempCluster.size() +", " + yCoor/tempCluster.size());
 			Point ClusterC = new Point(xCoor/tempCluster.size(),yCoor/tempCluster.size());
			Core.line(image, ClusterC, ClusterC, new Scalar(255, 255, 255), 5);
		}

		return image;

	}

}
