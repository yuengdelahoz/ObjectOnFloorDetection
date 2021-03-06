package delahoz.ObjectOnFloor.structured;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class ImageTools {

	private final String TAG_S = "SAVING";
	private final String TAG_R = "READING";
	public static int kval = 0;

	public Mat ReadImage(File path, String name) {

		File file = new File(path, name);
		Mat src = null;
		String filename = file.toString();
		// 2.4.11
		src = Highgui.imread(filename, Highgui.CV_LOAD_IMAGE_COLOR);
		// 3.0.0
		// src = Imgcodecs.imread(filename, Imgcodecs.CV_LOAD_IMAGE_COLOR);

		if (!src.empty()) {
			Log.i(TAG_R, "SUCCESS Reading the image " + name);
			Imgproc.resize(src, src, new Size(320, 240));
		} else {
			Log.d(TAG_R, "Fail Reading the image " + name);
			return null;
		}
		return src;

	}

	public void SaveImage(Mat img, String name) {
		File ph = new File("storage/sdcard0/Floor" + "/Output");
		Mat img2 = new Mat(img.size(), img.type());
		String val = "";
		if (kval < 10)
			val = "000" + kval;
		else if (kval >= 10 && kval < 100)
			val = "00" + kval;
		else if (kval >= 100 && kval < 1000)
			val = "0" + kval;
		else
			val = "" + kval;
		String filename = name + val + ".png";
		kval++;
		File file = new File(ph, filename);
		if (file.exists())
			file.delete();
		Boolean bool = null;
		String filenm = file.toString();

		Imgproc.cvtColor(img, img2, Imgproc.COLOR_RGBA2BGR);

		// 2.4.11
		bool = Highgui.imwrite(filenm, img2);

		// 3.0.0
		//bool = Imgcodecs.imwrite(filenm, img);

		if (bool == true) {
			Log.i(TAG_S, "SUCCESS writing image " + name);
		} else
			Log.d(TAG_S, "Fail writing image");

	}

	public double Distance(Point one, Point two) {
		double theDistance = 0;

		theDistance = Math.sqrt((Math.pow((two.x - one.x), 2)) + (Math.pow((two.y - one.y), 2)));

		return Math.abs(theDistance);
	}

}
