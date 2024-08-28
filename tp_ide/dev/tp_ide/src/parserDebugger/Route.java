package parserDebugger;

/**
 * This class is used for defining a connection between two points. One route
 * corresponds to a connection between two table columns involved in a
 * transformation made by the parser.
 * 
 * @author eheitur
 * 
 */
public class Route {

	private static final long serialVersionUID = 1L;

	private TransformationPoint  startPoint = null;
	private TransformationPoint endPoint = null;
	
	private headTypes startType = headTypes.SOURCE;
	private headTypes endType = headTypes.TARGET;

	public enum headTypes {
	 SOURCE, TARGET, MISSING_SOURCE, MISSING_TARGET
	 };

	public Route(TransformationPoint startPoint, TransformationPoint endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	/**
	 * @return the startPoint
	 */
	public TransformationPoint getStartPoint() {
		return startPoint;
	}

	/**
	 * @param startPoint the startPoint to set
	 */
	public void setStartPoint(TransformationPoint startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * @return the endPoint
	 */
	public TransformationPoint getEndPoint() {
		return endPoint;
	}

	/**
	 * @param endPoint the endPoint to set
	 */
	public void setEndPoint(TransformationPoint endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * @return the startType
	 */
	public headTypes getStartType() {
		return startType;
	}

	/**
	 * @param startType the startType to set
	 */
	public void setStartType(headTypes startType) {
		this.startType = startType;
	}

	/**
	 * @return the endType
	 */
	public headTypes getEndType() {
		return endType;
	}

	/**
	 * @param endType the endType to set
	 */
	public void setEndType(headTypes endType) {
		this.endType = endType;
	}
}
