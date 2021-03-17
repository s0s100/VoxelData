import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.*;

/**
 * Main window to interact with a volume data. By default first renders Q1 and
 * Q2 image arrays to reduce response time. Q3 uses real-time rendering.
 * 
 * @author s0s100
 *
 */

public class MainWindow extends Application {
	// Default screen size and default distance between different elements
	private static final int SCREEN_WIDTH = 1300;
	private static final int SCREEN_HEIGHT = 720;
	private static final int SHIFT_VALUE = 20;

	// Main launch element
	@Override
	public void start(Stage stage) throws Exception {

		// Dialog via console
		// System.out.print("Select file path:");
		// Scanner in = new Scanner(System.in);
		// String path = in.next();
		// in.close();

		// Set default path
		System.out.println();
		String defaultPath = "volume data/CThead";
		//String defaultPath = "volume data/extra/present492x492x442.dat";

		// Read data
		VolumeData volumeData = new VolumeData();
		try {
			volumeData.readData(defaultPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Q1 elements
		 */

		// X axis

		ImageCollection xQ1ImageCollection = new ImageCollection(SliceWay.X_AXIS, SliceType.NormalSlice, volumeData);
		ImageView xImageView = new ImageView(xQ1ImageCollection.getImages().get(0));
		Slider xSlider = new Slider(0, VolumeData.CT_X_AXIS - 1, 0);

		// Create and add listener
		ChangeListener<Number> xListener = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				xImageView.setImage(xQ1ImageCollection.getImageWithIndex(newValue.intValue()));
			}

		};
		xSlider.valueProperty().addListener(xListener);

		// Set elements position
		xImageView.setTranslateX(SHIFT_VALUE);
		xImageView.setTranslateY(SHIFT_VALUE);
		xSlider.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_Y_AXIS);
		xSlider.setTranslateY(SHIFT_VALUE + VolumeData.CT_Z_AXIS / 2);

		// Y axis
		ImageCollection yQ1ImageCollection = new ImageCollection(SliceWay.Y_AXIS, SliceType.NormalSlice, volumeData);
		ImageView yImageView = new ImageView(yQ1ImageCollection.getImages().get(0));
		Slider ySlider = new Slider(0, VolumeData.CT_Y_AXIS - 1, 0);

		// Create and add listener
		ChangeListener<Number> yListener = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				yImageView.setImage(yQ1ImageCollection.getImageWithIndex(newValue.intValue()));
			}

		};
		ySlider.valueProperty().addListener(yListener);

		// Set elements position
		yImageView.setTranslateX(SHIFT_VALUE);
		yImageView.setTranslateY(2 * SHIFT_VALUE + VolumeData.CT_Z_AXIS);
		ySlider.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_Y_AXIS);
		ySlider.setTranslateY(2 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 3 / 2);

		// Z axis
		ImageCollection zQ1ImageCollection = new ImageCollection(SliceWay.Z_AXIS, SliceType.NormalSlice, volumeData);
		ImageView zImageView = new ImageView(zQ1ImageCollection.getImages().get(0));
		Slider zSlider = new Slider(0, VolumeData.CT_Z_AXIS - 1, 0);

		// Create and add listener
		ChangeListener<Number> zListener = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				zImageView.setImage(zQ1ImageCollection.getImageWithIndex(newValue.intValue()));
			}

		};
		zSlider.valueProperty().addListener(zListener);

		// Set elements position
		zImageView.setTranslateX(SHIFT_VALUE);
		zImageView.setTranslateY(SHIFT_VALUE * 3 + VolumeData.CT_Z_AXIS * 2);
		zSlider.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_Y_AXIS);
		zSlider.setTranslateY(3 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 2 + VolumeData.CT_Y_AXIS / 2);

		/*
		 * Q2 elements
		 */

		// Volume render slider and rendered image collections
		ImageCollection xQ2ImageCollection = new ImageCollection(SliceWay.X_AXIS, SliceType.VolumeRender, volumeData);
		ImageCollection yQ2ImageCollection = new ImageCollection(SliceWay.Y_AXIS, SliceType.VolumeRender, volumeData);
		ImageCollection zQ2ImageCollection = new ImageCollection(SliceWay.Z_AXIS, SliceType.VolumeRender, volumeData);
		Slider vSlider = new Slider(0, ImageCollection.SKIP_OPACITY_SCALE - 1, 0);
		ChangeListener<Number> vListener = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				xImageView.setImage(xQ2ImageCollection.getImageWithIndex(newValue.intValue()));
				yImageView.setImage(yQ2ImageCollection.getImageWithIndex(newValue.intValue()));
				zImageView.setImage(zQ2ImageCollection.getImageWithIndex(newValue.intValue()));
			}

		};

		// Set elements position
		vSlider.setTranslateX(SHIFT_VALUE);
		vSlider.setTranslateY(5 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 2 + VolumeData.CT_Y_AXIS + 35);

		// Toggle switch for the Q2
		ToggleButton q1State = new ToggleButton("Q1");
		ToggleButton q2State = new ToggleButton("Q2");

		final ToggleGroup toggleGroup = new ToggleGroup();
		q1State.setToggleGroup(toggleGroup);
		q2State.setToggleGroup(toggleGroup);

		toggleGroup.selectToggle(q1State);
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle toggle, Toggle selectedToggle) {
				if (selectedToggle != null) {

					// Debug
					// System.out.println("Clicked " + selectedToggle.toString());

					if (((ToggleButton) selectedToggle).getText() == "Q1") {
						// Add and remove listeners to avoid Q2 toggling
						xSlider.valueProperty().addListener(xListener);
						ySlider.valueProperty().addListener(yListener);
						zSlider.valueProperty().addListener(zListener);
						vSlider.valueProperty().removeListener(vListener);

						// Select current Q1 image
						xImageView.setImage(xQ1ImageCollection.getImageWithIndex((int) xSlider.getValue()));
						yImageView.setImage(yQ1ImageCollection.getImageWithIndex((int) ySlider.getValue()));
						zImageView.setImage(zQ1ImageCollection.getImageWithIndex((int) zSlider.getValue()));
					} else {
						// Add and remove listeners to avoid Q1 toggling
						xSlider.valueProperty().removeListener(xListener);
						ySlider.valueProperty().removeListener(yListener);
						zSlider.valueProperty().removeListener(zListener);
						vSlider.valueProperty().addListener(vListener);

						// Select current Q2 image
						xImageView.setImage(xQ2ImageCollection.getImageWithIndex((int) vSlider.getValue()));
						yImageView.setImage(yQ2ImageCollection.getImageWithIndex((int) vSlider.getValue()));
						zImageView.setImage(zQ2ImageCollection.getImageWithIndex((int) vSlider.getValue()));
					}
				}
			}
		});

		// Set elements position
		q1State.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_X_AXIS);
		q1State.setTranslateY(4 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 2 + VolumeData.CT_X_AXIS);
		q2State.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_X_AXIS + 50);
		q2State.setTranslateY(4 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 2 + VolumeData.CT_X_AXIS);

		/**
		 * Q3 elements
		 */

		ColorPicker colorPicker = new ColorPicker(); // Allows to pick a color of the light
		Slider lSlider = new Slider(GradientRenderer.MIN_LIGHT_X, GradientRenderer.MAX_LIGHT_X, 0); // Slider to change the angle of the light
		GradientRenderer gradientRenderer = new GradientRenderer(volumeData, (int) lSlider.getValue(),
				colorPicker.getValue()); // Class which allows us to interact with a skull using rotation and light rendering
		ImageView gImageView = new ImageView(gradientRenderer.renderImage());

		// Create mouse dragging handler
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
			// Values before and after clicking the mouse button
			int x1, y1, x2, y2, dx, dy;

			@Override
			public void handle(MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
					// Remember the values when the mouse is pressed
					x1 = (int) event.getX();
					y1 = (int) event.getY();

					// Debug
					// System.out.println(String.format("Mouse pressed: [%d,%d]", x1, y1));

				} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
					// Calculate value distinction
					x2 = (int) event.getX();
					y2 = (int) event.getY();
					dx = x2 - x1;
					dy = -(y2 - y1);
					x1 = x2;
					y1 = y2;

					// Change an volume data set by rotating it using value distinction
					gradientRenderer.changePointOfView(dx, dy);
					gImageView.setImage(gradientRenderer.renderImage());

					// Debug
					// System.out.println(String.format("Mouse dragged: [%d,%d]", x1, y1));
					// System.out.println(String.format("Shift: [%d,%d]", dx, dy));
				}
			}
		};
		gImageView.addEventHandler(MouseEvent.ANY, eventHandler);

		// Light X position slider listener
		lSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				gradientRenderer.setLightAngle(newValue.intValue());
				gImageView.setImage(gradientRenderer.renderImage());
			}
		});

		// Color picker listener
		colorPicker.setOnAction(t -> {
			gradientRenderer.setLightColor(colorPicker.getValue());
			gImageView.setImage(gradientRenderer.renderImage());
		});

		// Set elements position
		gImageView.setTranslateX(SCREEN_WIDTH - VolumeData.CT_X_AXIS - SHIFT_VALUE);
		gImageView.setTranslateY(2 * SHIFT_VALUE + 35);
		colorPicker.setTranslateX(SCREEN_WIDTH - SHIFT_VALUE - VolumeData.CT_X_AXIS);
		colorPicker.setTranslateY(3 * SHIFT_VALUE + 35 * 3 + VolumeData.CT_Y_AXIS);
		lSlider.setTranslateX(SCREEN_WIDTH - VolumeData.CT_X_AXIS - SHIFT_VALUE);
		lSlider.setTranslateY(3 * SHIFT_VALUE + 35 * 2 + VolumeData.CT_Y_AXIS);

		/*
		 * Adding text fields to provide more information
		 */

		TextField xSliderText = new TextField("X Axis Q1 slider");
		xSliderText.setDisable(true);
		xSliderText.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_Y_AXIS);
		xSliderText.setTranslateY(SHIFT_VALUE + VolumeData.CT_Z_AXIS / 2 - 35);

		TextField ySliderText = new TextField("Y Axis Q1 slider");
		ySliderText.setDisable(true);
		ySliderText.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_X_AXIS);
		ySliderText.setTranslateY(2 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 3 / 2 - 35);

		TextField zSliderText = new TextField("Z Axis Q1 slider");
		zSliderText.setDisable(true);
		zSliderText.setTranslateX(2 * SHIFT_VALUE + VolumeData.CT_X_AXIS);
		zSliderText.setTranslateY(3 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 2 + VolumeData.CT_Y_AXIS / 2 - 35);

		TextField vSliderText = new TextField("Skin opacity Q2 slider");
		vSliderText.setDisable(true);
		vSliderText.setTranslateX(SHIFT_VALUE);
		vSliderText.setTranslateY(4 * SHIFT_VALUE + VolumeData.CT_Z_AXIS * 2 + VolumeData.CT_X_AXIS);

		TextField lSliderText = new TextField("X light position");
		lSliderText.setDisable(true);
		lSliderText.setTranslateX(SCREEN_WIDTH - SHIFT_VALUE - VolumeData.CT_X_AXIS);
		lSliderText.setTranslateY(3 * SHIFT_VALUE + 35 + VolumeData.CT_Y_AXIS);

		TextField q3Text = new TextField("Q3 solution with mouse interaction");
		q3Text.setMinWidth(VolumeData.CT_X_AXIS);
		q3Text.setDisable(true);
		q3Text.setTranslateX(SCREEN_WIDTH - SHIFT_VALUE - VolumeData.CT_X_AXIS);
		q3Text.setTranslateY(SHIFT_VALUE);

		/*
		 * Final setup
		 */

		Pane root = new Pane();
		root.getChildren().addAll(xSliderText, ySliderText, zSliderText, vSliderText, lSliderText, q3Text); // Text info
		root.getChildren().addAll(xImageView, yImageView, zImageView, xSlider, ySlider, zSlider); // Q1 elements
		root.getChildren().addAll(q1State, q2State, vSlider); // Q2 elements
		root.getChildren().addAll(gImageView, lSlider, colorPicker); // Q3 elements

		Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
		stage.setTitle("Main screen");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
