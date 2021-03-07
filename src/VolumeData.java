import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class which contains 3D volume data with min and max values in the data set
 * 
 * @author s0s100
 *
 */

public class VolumeData {
	// Skull default size of the image
	//public static final int CT_X_AXIS = 256;// X axis length
	//public static final int CT_Y_AXIS = 256;// Y axis length
	//public static final int CT_Z_AXIS = 113;// Z axis length

	public static final int CT_X_AXIS = 492;// X axis length
	public static final int CT_Y_AXIS = 492;// Y axis length
	public static final int CT_Z_AXIS = 442;// Z axis length

	private short cthead[][][]; // 3D volume data set
	private short min, max; // min, max value in the 3D volume data set

	// Basic constructor with variable initialization
	public VolumeData() {
		cthead = new short[CT_X_AXIS][CT_Y_AXIS][CT_Z_AXIS];
		this.min = Short.MAX_VALUE;
		this.max = Short.MIN_VALUE;
	}

	// Reads data in the following path
	public void readData(String path) throws IOException {
		DataInputStream in;
		File file;

		// Open input stream and store all the data from 3D volume data set
		try {
			file = new File(path);
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			readInfoSet(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Goes through every element in the dataset and then put the value at the required position
	private void readInfoSet(DataInputStream in) throws IOException {

		for (int k = 0; k < CT_Z_AXIS; k++) {
			for (int j = 0; j < CT_Y_AXIS; j++) {
				for (int i = 0; i < CT_X_AXIS; i++) {
					short nextElement = readInfoByte(in);
					cthead[i][j][k] = nextElement;
				}
			}
		}

		System.out.println("Min and max dataset values: " + min + " " + max);
	}

	// Reads 1 element of the data
	private short readInfoByte(DataInputStream in) throws IOException {
		short bytePos1, bytePos2;
		short result;

		// Data element saved using wrong Endianness, so transform the data into short value
		bytePos1 = (short) ((in.readByte()) & 0xff);
		bytePos2 = (short) ((in.readByte()) & 0xff);
		result = (short) ((bytePos2 << 8) | bytePos1);

		// Set min and max value of the file after reading the element value
		if (result < min) {
			min = result;
		} else if (result > max) {
			max = result;
		}

		// Debug
		// System.out.print(byteInfo1 + " " + byteInfo2 + " and positive:");
		// System.out.println(bytePos1 + " " + bytePos1);
		// System.out.println("Result value is: " + result);

		return result;
	}

	// Reads data from the other source
	public void readData2(String path) throws IOException {
		DataInputStream in;
		File file;

		// Open input stream and store all the data from 3D volume data set
		try {
			file = new File(path);
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			readInfoSet(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Goes through every element in the dataset and then put the value at the required position
	private void readInfoSet2(DataInputStream in) throws IOException {
		short sizeX, sizeY, sizeZ;
		sizeX = (short) ((in.readByte()) & 0xff);
		sizeY = (short) ((in.readByte()) & 0xff);
		sizeZ = (short) ((in.readByte()) & 0xff);
		// Haven't used them :|

		for (int k = 0; k < CT_Z_AXIS; k++) {
			for (int j = 0; j < CT_Y_AXIS; j++) {
				for (int i = 0; i < CT_X_AXIS; i++) {
					short nextElement = readInfoByte(in);
					cthead[i][j][k] = nextElement;
				}
			}
		}

		System.out.println("Min and max dataset values: " + min + " " + max);
	}

	// Reads 1 element of the data
	private short readInfoByte2(DataInputStream in) throws IOException {
		short bytePos1, bytePos2, bytePos3;
		short result = 0;

		/*
		  FILE *fp = fopen("filename.dat","rb");
		
			unsigned short vuSize[3];
			fread((void*)vuSize,3,sizeof(unsigned short),fp);
		
			int uCount = int(vuSize[0])*int(vuSize[1])*int(vuSize[2]);
			unsigned short *pData = new unsigned short[uCount];
			fread((void*)pData,uCount,sizeof(unsigned short),fp);
		
			fclose(fp);
		 */

		bytePos1 = (short) ((in.readByte()) & 0xffff);
		//bytePos2 = (short) ((in.readByte()) & 0xffff);
		//bytePos3 = (short) ((in.readByte()) & 0xffff);
		result = bytePos1;

		// Data element saved using wrong Endianness, so transform the data into short value
		/*bytePos1 = (short) ((in.readByte()) & 0xff);
		bytePos2 = (short) ((in.readByte()) & 0xff);
		result = (short) ((bytePos2 << 8) | bytePos1);*/
		// bytePos1 = (short) (in.readByte() & 0xff);

		// Set min and max value of the file after reading the element value
		if (result < min) {
			min = result;
		} else if (result > max) {
			max = result;
		}

		// Debug
		// System.out.print(byteInfo1 + " " + byteInfo2 + " and positive:");
		// System.out.println(bytePos1 + " " + bytePos1);
		// System.out.println("Result value is: " + result);

		return result;
	}

	/*
	 * Getters
	 */

	public short[][][] getVolume() {
		return cthead;
	}

	public short getMin() {
		return min;
	}

	public short getMax() {
		return max;
	}
}
