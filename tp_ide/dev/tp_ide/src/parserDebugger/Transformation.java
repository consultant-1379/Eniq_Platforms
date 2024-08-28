package parserDebugger;

/**
 * Defines one transformation as a connection between the source and destination
 * columns.
 * 
 * @author eheitur
 * 
 */
public class Transformation {

	private String transformationId = null;
	private String[] fromColumns = null;
	private String[] toColumns = null;

	/**
	 * Constructor.
	 * 
	 * @param transformationId
	 * @param fromColumn
	 * @param toColumn
	 */
	public Transformation(String transformationId, String[] fromColumns,
			String[] toColumns) {
		super();
		this.transformationId = transformationId;
		this.fromColumns = fromColumns;
		this.toColumns = toColumns;
        /*
		System.out
				.println("Transformation(): new transformation created from: "
						+ fromColumns + " to: " + toColumns + ".");
        */
	}

	/**
	 * @return the transformation identifier
	 */
	public String getTransformationId() {
		return transformationId;
	}

	/**
	 * @return the from column name
	 */
	public String[] getFromColumns() {
		return fromColumns;
	}

	/**
	 * @return the to column name
	 */
	public String[] getToColumns() {
		return toColumns;
	}

}
