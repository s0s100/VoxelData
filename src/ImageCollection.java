import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Generates required image array with standard rendering and volume rendering
 * (Includes Q1 and Q2 solutions)
 * 
 * @author s0s100
 *
 */

public class ImageCollection {
	private static final Color boneColor = new Color(1d, 1d, 1d, 0.8d); // Color of the bone element
	public static final int SKIP_OPACITY_SCALE = 1; // Max skin opacity value

	private final ArrayList<WritableImage> images; // Set of generated images

	// Constructor which generates slice image collection using provided information
	public ImageCollection(SliceWay sliceWay, SliceType sliceType, VolumeData data) {
		images = new ArrayList<WritableImage>();
		switch (sliceType) {

		// Q1 solutions
		case NormalSlice: {
			switch (sliceWay) {
			case X_AXIS: {
				generateImagesX(data);
				break;
			}
			case Y_AXIS: {
				generateImagesY(data);
				break;
			}
			case Z_AXIS: {
				generateImagesZ(data);
				break;
			}
			}
			break;
		}

		// Q2 solutions
		case VolumeRender: {
			switch (sliceWay) {
			case X_AXIS: {
				generateVolumeRenderX(data);
				break;
			}
			case Y_AXIS: {
				generateVolumeRenderY(data);
				break;
			}
			case Z_AXIS: {
				generateVolumeRenderZ(data);
				break;
			}
			}
			break;
		}
		}
	}

	// Gets image with following index
	public WritableImage getImageWithIndex(int index) {
		return images.get(index);
	}

	// Gets an array with all generated images
	public ArrayList<WritableImage> getImages() {
		return images;
	}

	// Checks if the value is between following boundaries
	private static boolean isBetween(int num, int lowerValue, int higherValue) {
		return (num > lowerValue) && (num < higherValue);
	}

	// Creates images for X axis using pixel writer with volume data (Q1)
	private void generateImagesX(VolumeData data) {
		int imageWidth = VolumeData.CT_Y_AXIS;
		int imageHeight = VolumeData.CT_Z_AXIS;
		short min = data.getMin();
		short max = data.getMax();
		short dataPiece;
		float colorNum;
		Color color;

		// Create image for every possible X value
		for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
			WritableImage image = new WritableImage(imageWidth, imageHeight);
			PixelWriter image_writer = image.getPixelWriter();

			for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
				for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
					dataPiece = data.getVolume()[i][j][k];// Get pixel data
					colorNum = (float) (dataPiece - min) / (max - min); // Color to set the pixel
					color = new Color(colorNum, colorNum, colorNum, 1.0f);
					image_writer.setColor(j, k, color);
				}
			}

			images.add(image);
		}

		System.out.println("Q1 X array is rendered with a size = " + images.size());
	}

	// Creates images for Y axis using pixel writer with volume data
	private void generateImagesY(VolumeData data) {
		int imageWidth = VolumeData.CT_X_AXIS;
		int imageHeight = VolumeData.CT_Z_AXIS;
		short min = data.getMin();
		short max = data.getMax();
		short dataPiece;
		float colorNum;
		Color color;

		// Create image for every possible Y value
		for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
			WritableImage image = new WritableImage(imageWidth, imageHeight);
			PixelWriter image_writer = image.getPixelWriter();

			for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
				for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
					dataPiece = data.getVolume()[i][j][k]; // Get pixel data
					colorNum = (float) (dataPiece - min) / (max - min); // Color to set the pixel
					color = new Color(colorNum, colorNum, colorNum, 1.0f);
					image_writer.setColor(i, k, color);
				}
			}

			images.add(image);
		}

		System.out.println("Q1 Y array is rendered with a size = " + images.size());
	}

	// Creates images for Z axis using pixel writer with volume data
	private void generateImagesZ(VolumeData data) {
		int imageWidth = VolumeData.CT_X_AXIS;
		int imageHeight = VolumeData.CT_Y_AXIS;
		short min = data.getMin();
		short max = data.getMax();
		short dataPiece;
		float colorNum;
		Color color;

		// Create image for every possible Z value
		for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
			WritableImage image = new WritableImage(imageWidth, imageHeight);
			PixelWriter image_writer = image.getPixelWriter();

			for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
				for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
					dataPiece = data.getVolume()[i][j][k]; // Get pixel data
					colorNum = (float) (dataPiece - min) / (max - min); // Color to set the pixel
					color = new Color(colorNum, colorNum, colorNum, 1.0f);
					image_writer.setColor(i, j, color);
				}
			}

			images.add(image);
		}

		System.out.println("Q1 Z array is rendered with a size = " + images.size());
	}

	// Creates images for X axis using pixel writer with generated volume render
	private void generateVolumeRenderX(VolumeData data) {
		int imageWidth = VolumeData.CT_Y_AXIS;
		int imageHeight = VolumeData.CT_Z_AXIS;

		WritableImage image; // Result image
		PixelWriter image_writer; // Pixel writer to make the image
		Color currentColor = null; // Will be used to determine current color change
		Color skinColor = null; // Will be defined through the loop
		Color colorToUse; // Color which will be used at pixel writer
		double r, g, b, transp; // final color and accumulating transparency
		double skinOpacity;
		int curElem;

		// Also include skin opacity calculations
		for (int s = 0; s < SKIP_OPACITY_SCALE; s++) {
			image = new WritableImage(imageWidth, imageHeight);
			image_writer = image.getPixelWriter();

			skinOpacity = (double) s / 100;
			skinColor = new Color(1d, 0.79d, 0.6d, skinOpacity);

			// Loop through every pixel
			for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
				for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
					transp = 1d;
					r = 0d;
					g = 0d;
					b = 0d;

					// Parse through every value of the X axis
					for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
						curElem = data.getVolume()[i][j][k];

						if (isBetween(curElem, Integer.MIN_VALUE, -300)) {
							currentColor = null;
						} else if (isBetween(curElem, -301, 50)) {
							currentColor = skinColor;
						} else if (isBetween(curElem, 49, 300)) {
							currentColor = null;
						} else if (isBetween(curElem, 299, 4097)) {
							currentColor = boneColor;
						}

						if (currentColor != null) {
							r += transp * currentColor.getOpacity() * currentColor.getRed();
							g += transp * currentColor.getOpacity() * currentColor.getGreen();
							b += transp * currentColor.getOpacity() * currentColor.getBlue();
							transp *= (1d - currentColor.getOpacity());
						}
					}

					// Fixes the problem then r goes out of bounds
					r = Math.min(1.0, r);
					g = Math.min(1.0, g);
					b = Math.min(1.0, b);

					colorToUse = new Color(r, g, b, 1d);
					image_writer.setColor(j, k, colorToUse);
				}
			}

			images.add(image);
		}
		System.out.println("Q2 X array is rendered with a size = " + images.size());
	}

	// Creates images for Y axis using pixel writer with generated volume render
	private void generateVolumeRenderY(VolumeData data) {
		int imageWidth = VolumeData.CT_X_AXIS;
		int imageHeight = VolumeData.CT_Z_AXIS;

		WritableImage image; // Result image
		PixelWriter image_writer; // Pixel writer to make the image
		Color currentColor = null; // Will be used to determine current color change
		Color skinColor = null; // Will be defined through the loop
		Color colorToUse; // Color which will be used at pixel writer
		double r, g, b, transp; // final color and accumulating transparency
		double skinOpacity;
		int curElem;

		// Also include skin opacity calculations
		for (int s = 0; s < SKIP_OPACITY_SCALE; s++) {
			image = new WritableImage(imageWidth, imageHeight);
			image_writer = image.getPixelWriter();

			skinOpacity = (double) s / 100;
			skinColor = new Color(1d, 0.79d, 0.6d, skinOpacity);

			// Loop through every pixel
			for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
				for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
					transp = 1d;
					r = 0d;
					g = 0d;
					b = 0d;

					// Parse through every value of the X axis
					for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
						curElem = data.getVolume()[i][j][k];

						if (isBetween(curElem, Integer.MIN_VALUE, -300)) {
							currentColor = null;
						} else if (isBetween(curElem, -301, 50)) {
							currentColor = skinColor;
						} else if (isBetween(curElem, 49, 300)) {
							currentColor = null;
						} else if (isBetween(curElem, 299, 4097)) {
							currentColor = boneColor;
						}

						// Change values according to the lecture
						if (currentColor != null) {
							r += transp * currentColor.getOpacity() * currentColor.getRed();
							g += transp * currentColor.getOpacity() * currentColor.getGreen();
							b += transp * currentColor.getOpacity() * currentColor.getBlue();
							transp *= (1d - currentColor.getOpacity());
						}
					}

					// Fixes the problem then r goes out of bounds
					r = Math.min(1.0, r);
					g = Math.min(1.0, g);
					b = Math.min(1.0, b);

					colorToUse = new Color(r, g, b, 1d);
					image_writer.setColor(i, k, colorToUse);
				}
			}

			images.add(image);
		}
		System.out.println("Q2 Y array is rendered with a size = " + images.size());
	}

	// Creates images for Z axis using pixel writer with generated volume render
	private void generateVolumeRenderZ(VolumeData data) {
		int imageWidth = VolumeData.CT_X_AXIS;
		int imageHeight = VolumeData.CT_Y_AXIS;

		WritableImage image; // Result image
		PixelWriter image_writer; // Pixel writer to make the image
		Color currentColor = null; // Will be used to determine current color change
		Color skinColor = null; // Will be defined through the loop
		Color colorToUse; // Color which will be used at pixel writer
		double r, g, b, transp; // final color and accumulating transparency
		double skinOpacity;
		int curElem;

		// Also include skin opacity calculations
		for (int s = 0; s < SKIP_OPACITY_SCALE; s++) {
			image = new WritableImage(imageWidth, imageHeight);
			image_writer = image.getPixelWriter();

			skinOpacity = (double) s / 100;
			skinColor = new Color(1d, 0.79d, 0.6d, skinOpacity);

			// Loop through every pixel
			for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
				for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
					transp = 1d;
					r = 0d;
					g = 0d;
					b = 0d;

					// Parse through every value of the X axis
					for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
						curElem = data.getVolume()[i][j][k];

						if (isBetween(curElem, Integer.MIN_VALUE, -300)) {
							currentColor = null;
						} else if (isBetween(curElem, -301, 50)) {
							currentColor = skinColor;
						} else if (isBetween(curElem, 49, 300)) {
							currentColor = null;
						} else if (isBetween(curElem, 299, 4097)) {
							currentColor = boneColor;
						}

						// Change values according to the lecture
						if (currentColor != null) {
							r += transp * currentColor.getOpacity() * currentColor.getRed();
							g += transp * currentColor.getOpacity() * currentColor.getGreen();
							b += transp * currentColor.getOpacity() * currentColor.getBlue();
							transp *= (1d - currentColor.getOpacity());
						}
					}

					// Fixes the problem then r goes out of bounds
					r = Math.min(1.0, r);
					g = Math.min(1.0, g);
					b = Math.min(1.0, b);

					colorToUse = new Color(r, g, b, 1d);
					image_writer.setColor(i, j, colorToUse);
				}
			}

			images.add(image);
		}
		System.out.println("Q2 Z array is rendered with a size = " + images.size());
	}
}
