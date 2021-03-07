/**
 * Class which is used to represent vectors for Q3 gradient rendering
 * 
 * @author s0s100
 *
 */
public class Vector {
	private double dx, dy, dz; // Differences between vector points
	private double length; // Length of the array

	// Constructor which will calculate required vector data
	public Vector(double x1, double y1, double z1, double x2, double y2, double z2) {
		dx = x2 - x1;
		dy = y2 - y1;
		dz = z2 - z1;
		length = Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	// Constructor with already calculated values
	public Vector(double dx, double dy, double dz) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		length = Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	// Returns normalized version of the vector
	public Vector getNormalizedVector() {
		Vector result = new Vector(dx / length, dy / length, dz / length);
		return result;
	}

	// Calculate dot product of 2 vectors
	public double dotProduct(Vector anotherVector) {
		double result;
		result = getDX() * anotherVector.getDX() + getDY() * anotherVector.getDY() + getDZ() * anotherVector.getDZ();
		return result;
	}

	// Multiply the double on every element of the vector
	public Vector multiplyDouble(Double value) {
		Vector result;
		double xRes = dx * value;
		double yRes = dy * value;
		double zRes = dz * value;
		result = new Vector(xRes, yRes, zRes);

		return result;
	}

	// Divides every value of one vector from another
	public Vector minusVector(Vector vector) {
		Vector result;
		double xRes = dx - vector.dx;
		double yRes = dy - vector.dy;
		double zRes = dz - vector.dz;
		result = new Vector(xRes, yRes, zRes);

		return result;
	}

	/**
	 * Getters
	 */

	public double getDX() {
		return dx;
	}

	public double getDY() {
		return dy;
	}

	public double getDZ() {
		return dz;
	}

	public double getLength() {
		return length;
	}
}
