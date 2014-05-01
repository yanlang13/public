package com.example.examplelist;

/**
 * A list of all the demos we have available.
 */
public final class ExampleDetailsList {

	/** This class should not be instantiated. */
	private ExampleDetailsList() {
	}

	public static final ExampleListDetails[] EXAMPLES = {
			new ExampleListDetails(R.string.example1_label,
					R.string.example1_description, Example1Activity.class),
			new ExampleListDetails(R.string.camera_label,
					R.string.camera_description, CameraActivity.class), };

} // end of ExampleDetialsList{}
