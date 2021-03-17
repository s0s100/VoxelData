import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class with rendering Q3 elements. Renders only bones (with predefined
 * boundary). Allows to rotate the object and change color / angle of the light
 * 
 * @author s0s100
 *
 */

public class GradientRenderer {
	private static final int IMAGE_SIDE_LENGTH = 256;

	// Boundaries of the light
	public static final int MIN_LIGHT_X = -IMAGE_SIDE_LENGTH / 2;
	public static final int MAX_LIGHT_X = IMAGE_SIDE_LENGTH / 2;
	private static final int Z_LIGHT_DISTANCE = -IMAGE_SIDE_LENGTH / 2; // Distance from the light to the skull center
	private static final int Y_LIGHT_DISTANCE = IMAGE_SIDE_LENGTH / 2; // Distance from the light to the skull center
	private static final double AMBIENT_COEFFICIET = 0.2d;
	private static final double DIFFUSE_COEFFICIET = 0.5d;
	private static final double SPECULAR_COEFFICIET = 1d;
	private static final int SHININGNESS_COEFFICIET = 10;

	// Bone color
	private static final Color BONE_COLOR = Color.WHITE;
	private static final Color AMBIENT_COLOR = Color.WHITESMOKE;

	// Bone boundary
	private static final int MIN_BONE_BOUNDARY = 300;
	private static final int MAX_BONE_BOUNDARY = 1000;

	// Length of every side of the image
	//private static final int IMAGE_SIDE_LENGTH = 256;

	// Skull center coordinates
	private static final int X_CENTER = IMAGE_SIDE_LENGTH / 2;
	private static final int Y_CENTER = IMAGE_SIDE_LENGTH / 2;
	private static final int Z_CENTER = IMAGE_SIDE_LENGTH / 2;

	private VolumeData data; // Data with a skull
	private int xLightDistance; // Light x position according to the light center
	private Color lightColor; // Color of the light source
	private int xAngle, yAngle; // Rotation angle of the image (0 - 360)

	// Matrix with rotation of the skull (4x4, every value is between -1 and 1)(some
	// sort of unit vectors)
	private double[][] rotationMatrix;

	// Used matrixes to rotate around X and Y
	double[][] dxRotation;
	double[][] dyRotation;

	private int[][][] unrotatedData; // Transformed data to work with
	private int[][][] rotatedData; // Transformed data including rotation

	// Constructor which sets default values and fills unrotated and rotated data
	public GradientRenderer(VolumeData data, int xLightDistance, Color lightColor) {
		this.data = data;
		transformData();

		// Generate rotation scale matrices and angles
		xAngle = 0;
		yAngle = 0;
		rotationMatrix = new double[4][4];
		dxRotation = new double[4][4];
		dyRotation = new double[4][4];

		this.xLightDistance = xLightDistance;
		this.lightColor = lightColor;
	}

	// Removes everything except bone elements (does it optimize?)
	private void transformData() {
		unrotatedData = new int[IMAGE_SIDE_LENGTH][IMAGE_SIDE_LENGTH][IMAGE_SIDE_LENGTH];
		rotatedData = new int[IMAGE_SIDE_LENGTH][IMAGE_SIDE_LENGTH][IMAGE_SIDE_LENGTH];

		//  Generate data set to work with image and doubles values at Z axis
		// generateUnrotatedData();
		generateUnrotatedDataDoubledZ();
		// generateNewUnrotatedData();
		rotatedData = unrotatedData;
	}

	// Generates unrotated data for different type
	private void generateNewUnrotatedData() {
		int xShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_X_AXIS) / 2;
		int yShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_Y_AXIS) / 2;
		int zShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_Z_AXIS) / 2;
		int element;

		// Standart transformation without changing Z size (but moving it in the middle)
		for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
			for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
				for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
					element = data.getVolume()[i][j][k];
					if (element > MIN_BONE_BOUNDARY && element < MAX_BONE_BOUNDARY) {
						unrotatedData[i + xShift][j + yShift][k + zShift] = element;
					}
				}
			}
		}
	}

	// Generates default unrotated data
	private void generateUnrotatedData() {
		int xShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_X_AXIS) / 2;
		int yShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_Y_AXIS) / 2;
		int zShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_Z_AXIS) / 2;
		int element;

		// Standart transformation without changing Z size (but moving it in the middle)
		for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
			for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
				for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
					element = data.getVolume()[i][j][k];
					if (element > MIN_BONE_BOUNDARY && element < MAX_BONE_BOUNDARY) {
						unrotatedData[i + xShift][j + yShift][k + zShift] = element;
					}
				}
			}
		}
	}

	// Generates default unrotated data with doubled Z (not optimized for the other volume data
	private void generateUnrotatedDataDoubledZ() {
		int xShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_X_AXIS) / 2;
		int yShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_Y_AXIS) / 2;
		//int zShift = (IMAGE_SIDE_LENGTH - VolumeData.CT_Z_AXIS) / 2;
		int zShift = 15;

		int element;
		// Standart transformation with doubling Z
		for (int i = 0; i < VolumeData.CT_X_AXIS; i++) {
			for (int j = 0; j < VolumeData.CT_Y_AXIS; j++) {
				for (int k = 0; k < VolumeData.CT_Z_AXIS; k++) {
					element = data.getVolume()[i][j][k];
					if (element > MIN_BONE_BOUNDARY && element < MAX_BONE_BOUNDARY) {
						unrotatedData[i + xShift][j + yShift][k + zShift] = element;
						unrotatedData[i + xShift][j + yShift][k + zShift * 2] = element;
					}
				}
			}
		}
	}

	/*
	 * Rendering functions
	 */

	// Renders an image
	public WritableImage renderImage() {
		WritableImage image = null;
		//image = q2Rendering();
		image = generateLightImage();
		return image;
	}

	// Rendering for the image using Q2 volume rendering
	public WritableImage q2Rendering() {
		WritableImage result = new WritableImage(VolumeData.CT_X_AXIS, VolumeData.CT_Y_AXIS);
		PixelWriter writer = result.getPixelWriter();
		int element;
		double boneTransp = 0.8d; // Default bone transparency
		double transpAccum; // Transparency accumulator
		Color colorToUse; // End pixel color

		for (int i = 0; i < IMAGE_SIDE_LENGTH; i++) {
			for (int j = 0; j < IMAGE_SIDE_LENGTH; j++) {
				transpAccum = 1d;

				for (int k = 0; k < IMAGE_SIDE_LENGTH; k++) {
					element = rotatedData[i][j][k];

					// Finds result transparency
					if (element != 0) {
						transpAccum *= boneTransp;
					}
				}

				// Draw a pixel if it has found at least one required elements
				if (transpAccum != 1d) {
					transpAccum = 1d - transpAccum;
					colorToUse = new Color(lightColor.getRed() * transpAccum, lightColor.getGreen() * transpAccum,
							lightColor.getBlue() * transpAccum, 1d);
					writer.setColor(i, j, colorToUse);
				}

				if (transpAccum == 1d) {
					writer.setColor(i, j, Color.BLACK);
				}
			}
		}
		return result;
	}

	// Gradient rendering with light implementation
	private WritableImage generateLightImage() {
		WritableImage result = new WritableImage(VolumeData.CT_X_AXIS, VolumeData.CT_Y_AXIS);
		PixelWriter writer = result.getPixelWriter();

		// Finds pixels which user can see
		int[][] pixelsToCalculate = findRequiredPixels();

		Vector gradientVector; // Vector of the skull surface
		Vector lightVector; // Vector from the light source to surface pixel
		Vector specularEyeVector; // Vector from the pixel to the screen position
		Vector reflectedVector; // Vector of the reflected ray from the surface
		int zPos; // z position of pixel in the users view
		int lx, ly, lz; // Light source position
		double ar, ag, ab; // Ambient colors
		double dr, dg, db; // Diffuse colors
		double sr, sg, sb; // Specular colors
		double r, g, b; // Result colors
		double dotResult; // Result of the dot product for diffuse calculations
		double specularDotResult; // Result of the dot product for specular calculations
		Color colorToUse; // Color which will be used by pixel writer

		// Calculate data for all required pixels
		for (int i = 0; i < IMAGE_SIDE_LENGTH; i++) {
			for (int j = 0; j < IMAGE_SIDE_LENGTH; j++) {
				zPos = pixelsToCalculate[i][j];

				if (zPos != -1) {
					// Finds gradient value for the pixels
					gradientVector = calculateGradient(i, j, zPos);
					gradientVector = gradientVector.getNormalizedVector();

					// Debug
					// System.out.println(String.format("Generated vector (%1.3f, %1.3f, %1.3f)", gradientVector.getDX(),
					//		gradientVector.getDX(), gradientVector.getDX()));

					// Position of the light source
					lx = X_CENTER + xLightDistance;
					ly = Y_CENTER + Y_LIGHT_DISTANCE;
					lz = Z_CENTER + Z_LIGHT_DISTANCE;

					// Generate normalized light vector
					// Is it the right direction?
					lightVector = new Vector(lx, ly, lz, i, j, zPos);
					lightVector = lightVector.getNormalizedVector();

					// Calculate dot product
					dotResult = gradientVector.dotProduct(lightVector);

					if (dotResult < 0) {
						dotResult = 0;
					}

					// Finds required data for the specular dot product
					specularEyeVector = new Vector(i, j, zPos, IMAGE_SIDE_LENGTH / 2, IMAGE_SIDE_LENGTH / 2, 0);
					specularEyeVector = specularEyeVector.getNormalizedVector();

					// Reflected ray vector required as well
					// R = 2 * (N x L) * N - L, where L - Starting ray, R - reflected ray and N - normal vector
					reflectedVector = gradientVector.multiplyDouble(2 * gradientVector.dotProduct(lightVector))
							.minusVector(lightVector);

					// Calculate specular dot product
					specularDotResult = specularEyeVector.dotProduct(reflectedVector);

					if (specularDotResult < 0) {
						specularDotResult = 0;
					}

					// Calculate ambient color
					ar = BONE_COLOR.getRed() * AMBIENT_COEFFICIET * AMBIENT_COLOR.getRed();
					ag = BONE_COLOR.getGreen() * AMBIENT_COEFFICIET * AMBIENT_COLOR.getGreen();
					ab = BONE_COLOR.getBlue() * AMBIENT_COEFFICIET * AMBIENT_COLOR.getBlue();

					// Calculate diffuse color
					dr = BONE_COLOR.getRed() * DIFFUSE_COEFFICIET * lightColor.getRed() * dotResult;
					dg = BONE_COLOR.getGreen() * DIFFUSE_COEFFICIET * lightColor.getGreen() * dotResult;
					db = BONE_COLOR.getBlue() * DIFFUSE_COEFFICIET * lightColor.getBlue() * dotResult;

					// Calculate specular color					
					sr = BONE_COLOR.getRed() * SPECULAR_COEFFICIET * lightColor.getRed()
							* Math.pow(specularDotResult, SHININGNESS_COEFFICIET);
					sg = BONE_COLOR.getGreen() * SPECULAR_COEFFICIET * lightColor.getGreen()
							* Math.pow(specularDotResult, SHININGNESS_COEFFICIET);
					sb = BONE_COLOR.getBlue() * SPECULAR_COEFFICIET * lightColor.getBlue()
							* Math.pow(specularDotResult, SHININGNESS_COEFFICIET);

					// Calculate result color and check the bounds
					r = ar + dr + sr;
					g = ag + dg + sg;
					b = ab + db + sb;

					// Fixes out of bounds problem
					r = Math.min(1.0, r);
					g = Math.min(1.0, g);
					b = Math.min(1.0, b);

					// Set the pixel to required color
					colorToUse = new Color(r, g, b, 1d);
					writer.setColor(i, j, colorToUse);

				} else {
					// Background color
					writer.setColor(i, j, Color.BLACK);
				}
			}
		}

		return result;
	}

	// Calculates gradient Vector for the selected position
	private Vector calculateGradient(int x, int y, int z) {
		Vector result = null;

		int x1, y1, z1, x2, y2, z2; // Elements values
		int element = rotatedData[x][y][z]; // middle element value

		// Finds x values
		switch (x) {
		case 0: {
			x1 = element;
			x2 = rotatedData[x + 1][y][z];
			break;
		}
		case IMAGE_SIDE_LENGTH - 1: {
			x1 = rotatedData[x - 1][y][z];
			x2 = element;
			break;
		}
		default: {
			x1 = rotatedData[x - 1][y][z];
			x2 = rotatedData[x + 1][y][z];
		}
		}

		// Finds y values
		switch (y) {
		case 0: {
			y1 = element;
			y2 = rotatedData[x][y + 1][z];
			break;
		}
		case IMAGE_SIDE_LENGTH - 1: {
			y1 = rotatedData[x][y - 1][z];
			y2 = element;
			break;
		}
		default: {
			y1 = rotatedData[x][y - 1][z];
			y2 = rotatedData[x][y + 1][z];
		}
		}

		// Finds z values
		switch (z) {
		case 0: {
			z1 = element;
			z2 = rotatedData[x][y][z + 1];
			break;
		}
		case IMAGE_SIDE_LENGTH - 1: {
			z1 = rotatedData[x][y][z - 1];
			z2 = element;
			break;
		}
		default: {
			z1 = rotatedData[x][y][z - 1];
			z2 = rotatedData[x][y][z + 1];
		}
		}

		result = new Vector(x1, y1, z1, x2, y2, z2);
		return result;
	}

	// Finds pixels for which is required to make rendering
	// Matrix with integer value which contains z pixel position
	private int[][] findRequiredPixels() {
		int[][] result = new int[IMAGE_SIDE_LENGTH][IMAGE_SIDE_LENGTH];

		int element;
		boolean valueFound;
		for (int i = 0; i < IMAGE_SIDE_LENGTH; i++) {
			for (int j = 0; j < IMAGE_SIDE_LENGTH; j++) {
				valueFound = false;
				for (int k = 0; k < IMAGE_SIDE_LENGTH; k++) {
					element = rotatedData[i][j][k];

					// Found bone value so save it's z coordinate
					if (element != 0) {
						result[i][j] = k;
						valueFound = true;
						break;
					}
				}

				// Not found required element so set it to -1
				if (!valueFound) {
					result[i][j] = -1;
				}
			}
		}

		return result;
	}

	// Sets the color of the light source
	public void setLightColor(Color color) {
		// System.out.println("New color is: " + color.toString());
		this.lightColor = color;
	}

	// Sets the X position for the light
	public void setLightAngle(int xValue) {
		// System.out.println("Light value is changed to :" + xValue);
		this.xLightDistance = xValue;
	}

	// Rotates data according to current state of the data (dx, dy - degree change)
	public void changePointOfView(int dx, int dy) {
		// First change xAngle, yAngle
		xAngle = addAngle(xAngle, dx);
		yAngle = addAngle(yAngle, dy);

		// Debug
		// System.out.println(String.format("New angles are: %d, %d", xAngle, yAngle));

		// Set rotation matrices values according to angles
		// Z axis rotation matrix
		/*dxRotation[1][1] = 1;
		dxRotation[3][3] = 1;
		dxRotation[0][0] = Math.cos(Math.toRadians(xAngle));
		dxRotation[2][2] = Math.cos(Math.toRadians(xAngle));
		dxRotation[2][0] = Math.sin(Math.toRadians(xAngle));
		dxRotation[0][2] = -Math.sin(Math.toRadians(xAngle));*/

		// X axis rotation
		dxRotation[2][2] = 1;
		dxRotation[3][3] = 1;
		dxRotation[0][0] = Math.cos(Math.toRadians(xAngle));
		dxRotation[1][1] = Math.cos(Math.toRadians(xAngle));
		dxRotation[1][0] = -Math.sin(Math.toRadians(xAngle));
		dxRotation[0][1] = Math.sin(Math.toRadians(xAngle));

		// Y axis rotation
		dyRotation[0][0] = 1;
		dyRotation[3][3] = 1;
		dyRotation[1][1] = Math.cos(Math.toRadians(yAngle));
		dyRotation[2][2] = Math.cos(Math.toRadians(yAngle));
		dyRotation[1][2] = -Math.sin(Math.toRadians(yAngle));
		dyRotation[2][1] = Math.sin(Math.toRadians(yAngle));

		// Change values of the rotation matrix and rotate data using rotation matrix
		rotationMatrix = matrixMultiplication(dxRotation, dyRotation);
		rotateData();
	}

	// Rotates data according to x, y and z rotation
	private void rotateData() {
		int element;
		double[][] transpMatrix = new double[4][4]; // Matrix containing voxel data
		int xc, yc, zc; // Coordinates according to the center
		double newX, newY, newZ; // New possible coordinates including values to the right of the decimal point

		// Generate new rotate data and fill it
		rotatedData = new int[IMAGE_SIDE_LENGTH][IMAGE_SIDE_LENGTH][IMAGE_SIDE_LENGTH];
		for (int i = 0; i < IMAGE_SIDE_LENGTH; i++) {
			for (int j = 0; j < IMAGE_SIDE_LENGTH; j++) {
				for (int k = 0; k < IMAGE_SIDE_LENGTH; k++) {
					element = unrotatedData[i][j][k];

					if (element != 0) {
						// First find element position according to the data center
						xc = i - X_CENTER;
						yc = j - Y_CENTER;
						zc = k - Z_CENTER;

						// Fill matrix with data to multiply with rotation matrix
						transpMatrix[0][0] = 1;
						transpMatrix[1][1] = 1;
						transpMatrix[2][2] = 1;
						transpMatrix[3][3] = 1;
						transpMatrix[3][0] = xc;
						transpMatrix[3][1] = yc;
						transpMatrix[3][2] = zc;

						// Multiplication itself
						transpMatrix = matrixMultiplication(transpMatrix, rotationMatrix);

						// Get new values after rotation using rotation matrix
						newX = transpMatrix[3][0] + X_CENTER;
						newY = transpMatrix[3][1] + Y_CENTER;
						newZ = transpMatrix[3][2] + Z_CENTER;

						// Interpolation implementation for voxels to set their values correctly
						interpVoxelSetter(newX, newY, newZ, element);
						// addToRotatedVoxel((int) newX, (int) newY, (int) newZ, element);

					}
				}
			}
		}
	}

	// After the data was rotated it should be interpolated correctly
	private void interpVoxelSetter(double newX, double newY, double newZ, int element) {
		int x, y, z; // Starting pixel position
		double dx, dy, dz; // Value for generating correct vector

		// Get pixel position by removing values after decimal point
		x = (int) newX;
		y = (int) newY;
		z = (int) newZ;

		// Calculate coordinate difference
		dx = x - newX;
		dy = y - newY;
		dz = z - newZ;

		double resX, resY, resZ; // Final vector values
		Vector calcVector; // Vector to calculate voxel value
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < 2; k++) {

					// Set x, y and z vector values
					if (i == 0) {
						resX = 1 - dx;
					} else {
						resX = dx;
					}
					if (j == 0) {
						resY = 1 - dy;
					} else {
						resY = dy;
					}
					if (k == 0) {
						resZ = 1 - dz;
					} else {
						resZ = dz;
					}

					// Generate and calculate vector
					calcVector = new Vector(resX, resY, resZ);
					//calcVector = calcVector.getNormalizedVector();
					addToRotatedVoxel(x + i, y + j, z + k, (int) (element * calcVector.getLength()));
				}
			}
		}
	}

	// Sets voxel data at the following coordinate to selected element
	private void addToRotatedVoxel(int x, int y, int z, int element) {
		// Check if out of bounds
		boolean xCheck = x >= 0 && x < IMAGE_SIDE_LENGTH;
		boolean yCheck = y >= 0 && y < IMAGE_SIDE_LENGTH;
		boolean zCheck = z >= 0 && z < IMAGE_SIDE_LENGTH;
		if (xCheck && yCheck && zCheck) {
			rotatedData[x][y][z] += element;
		}
	}

	/**
	 * Additional methods
	 */

	// Adds value to the angle and check it bounds
	private static int addAngle(int angle, int value) {
		angle += value;
		if (angle >= 360) {
			return angle - 360;
		}
		if (angle < 0) {
			return angle + 360;
		}
		return angle;
	}

	// Function to multiply 4x4 matrices
	private static double[][] matrixMultiplication(double[][] firstMatrix, double[][] secondMatrix) {
		double[][] resultMatrix = new double[4][4];
		double result;

		// Standard matrix multiplication
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result = 0;

				for (int k = 0; k < 4; k++) {
					result += firstMatrix[i][k] * secondMatrix[k][j];
				}
				resultMatrix[i][j] = result;
			}
		}

		// Debug
		/*
		System.out.println("Matrix 1: ");
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(String.format("%2.1f ", firstMatrix[i][j]));
			}
			System.out.println();
		}
		System.out.println("Matrix 2: ");
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(String.format("%2.1f ", secondMatrix[i][j]));
			}
			System.out.println();
		}
		System.out.println("Result after multiplication r: ");
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(String.format("%2.1f ", resultMatrix[i][j]));
			}
			System.out.println();
		}
		*/

		return resultMatrix;
	}
}
