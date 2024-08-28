package parserDebugger;

import java.awt.Point;

/**
 * Defines a point (with x and y coordinates) with a definition for the type
 * of the point, e.g. START for a point where the transformation connection
 * begins.
 * 
 * @author eheitur
 * 
 */
public class TransformationPoint extends Point {

	private static final long serialVersionUID = 1L;

	public enum pointTypes {
		START, END, INVALID_START, INVALID_END
	};

	pointTypes pointType = pointTypes.START;

	public TransformationPoint(Point p, pointTypes type) {
		super(p);
		this.pointType = type;
	}

	public TransformationPoint(int x, int y, pointTypes type) {
		super(x, y);
		this.pointType = type;
	}

}
