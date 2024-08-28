package parserDebugger;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * This class defines the drawing pane object in the component. It is used for
 * drawing the table objects representing the database table row before/after a
 * transformation.
 * 
 * @author eheitur
 * 
 */
public class ParserDebuggerDrawingPane extends JPanel {

	private static final long serialVersionUID = 1L;

	Vector<Route> routes = null;
	ParserDebuggerComponent myComp = null;

	enum directions {
		DOWN, LEFT, RIGHT
	};

	/**
	 * Constructor.
	 * 
	 * @param comp
	 */
	public ParserDebuggerDrawingPane(ParserDebuggerComponent comp) {

		// This panel must not be opaque
		setOpaque(false);

		this.myComp = comp;
		routes = new Vector<Route>();
	}

	protected void paintComponent(Graphics g) {

		// Iterate through all the routes and draw them
		Iterator<Route> iter = routes.iterator();
		while (iter.hasNext()) {
			Route currentElem = iter.next();
			paintRoute(currentElem, g);
		}

		// Test lines
		// Graphics2D gfx2d = (Graphics2D) g;
		// gfx2d.draw(new Line2D.Double(10, 10, 50, 800));
	}

	/**
	 * Recalculates all routes when the contents or the size of the panel below
	 * changes.
	 */
	public void reCalculateRoutes() {
		//System.out.println("reCalculateRoutes()");

		// Clear all current routes
		this.routes.clear();

		// System.out.println("--- RECALCULATE ---");

		// Get an iterator for the transformations
		Iterator<Transformation> transIter = myComp.getTransformations();

		// Recalculate the routes
		//
		// Create iteration variables
		TableData fromTable = null;
		TableData toTable = null;
		String[] fromColumns = null;
		String[] toColumns = null;
		Transformation currentTrans = null;

		// Iterate through the visible tables and draw transformation arrows
		// between them.
		Iterator<TableData> iter = myComp.getVisibleTables();
		while (iter.hasNext()) {
			if (fromTable == null) {
				// This is the first table, so no connections will be drawn.
				fromTable = iter.next();
			} else {

				// Debug:
				// System.out.println("parent: " + fromTable.getParent()
				// + ", Parent: " + fromTable.getParent().getParent()
				// + ", Parent: "
				// + fromTable.getParent().getParent().getParent());

				// This is not the first iteration, so the to-table is set.
				toTable = iter.next();

				// Get the next transformation
				currentTrans = transIter.next();

				// Get the list of from and to columns
				fromColumns = currentTrans.getFromColumns();
				toColumns = currentTrans.getToColumns();

				// Go through from columns and draw the connections to all to
				// columns
				for (int i = 0; i < fromColumns.length; i++) {
					for (int j = 0; j < toColumns.length; j++) {
						// Calculate the from and to point
						TransformationPoint[] points = calculateEndPoints(
								fromTable, toTable, fromColumns[i],
								toColumns[j]);

						// Add the route
						// System.out.println("adding route from " + points[0]
						// + " to: " + points[1] + ".");
						addRoute(new Route(points[0], points[1]));
					}
				}
				// Set the from table for the next iteration
				fromTable = toTable;
			}
		}

		// Paint the component
		repaint();
	}

	/**
	 * Calculates the location of a table column. The exact location depends on
	 * the type of the point, whether it is a start or an end point.
	 * 
	 * @param table
	 * @param column
	 * @param pointType
	 * @return the point
	 */
	private TransformationPoint calculatePoint(TableData table, String column,
			TransformationPoint.pointTypes pointType) {

		TransformationPoint.pointTypes pType = pointType;

		// System.out.println("calculatePoint(): --- POINT ---");

		// Get cell location relative in the table.
		// The column index is calculated from the visible columns in the model.
		// If the column does not exist, then
		int columnIndex;
		try {
			columnIndex = ((XTableColumnModel) table.getColumnModel())
					.getColumnIndex(column, true);
		} catch (IllegalArgumentException e) {
			// The column does not exist. Set the column to the first column.
			//System.out.println("calculatePoint(): The column " + column+ " does not exist! Setting to column zero.");
			if (pType == TransformationPoint.pointTypes.START) {
				pType = TransformationPoint.pointTypes.INVALID_START;
			} else {
				pType = TransformationPoint.pointTypes.INVALID_END;
			}
			columnIndex = 0;
		}
		Rectangle cellRect = table.getCellRect(1, columnIndex, true);
		// System.out.println("Cell location (in table):" + cellRect);

		// Get the table's relative location in the table pane
		Point tableLocation = table.getParent().getParent().getLocation();
		// System.out.println("Table location (in table pane): " +
		// tableLocation);

		// // Get the table pane location in the component
		// Point paneLocation =
		// table.getParent().getParent().getParent().getLocation();
		// System.out.println("Table pane location (in component): "
		// + paneLocation);

		// Convert table location in the table pane as relative location in the
		// component.
		// tableLocation = new Point(tableLocation.x + paneLocation.x,
		// tableLocation.y + paneLocation.y);
		tableLocation = new Point(tableLocation.x, tableLocation.y);
		// System.out.println("Table location (in component): " +
		// tableLocation);

		// NOTE: Viewport position does not affect the coordinates anymore.

		// // Get the view port for the component
		// JViewport compViewPort = (JViewport) myComp.getParent();
		// System.out.println("Viewport location (in component): "
		// + compViewPort.getViewPosition());
		//
		// // Convert table location as relative location in the
		// // components current view port position
		// tableLocation = new Point(tableLocation.x
		// - compViewPort.getViewPosition().x, tableLocation.y
		// - compViewPort.getViewPosition().y);
		// System.out.println("table location (viewport): " + tableLocation);

		// Add cell location to the table location, so we get the cell relative
		// location on the panel. The y-coordinate depends on if this is a start
		// point or an end point.

		// TODO: Figure out why the +5 points are needed in the start point
		// y-coordinate.

		if (pType == TransformationPoint.pointTypes.START) {
			cellRect.setLocation(cellRect.x + tableLocation.x + cellRect.width
					/ 2, cellRect.y + tableLocation.y + table.getRowHeight()
					+ 5);
		} else if (pType == TransformationPoint.pointTypes.END) {
			cellRect.setLocation(cellRect.x + tableLocation.x + cellRect.width
					/ 2, cellRect.y + tableLocation.y - table.getRowHeight());

		} else if (pType == TransformationPoint.pointTypes.INVALID_START) {
			cellRect.setLocation(cellRect.x + tableLocation.x + cellRect.width
					/ 2, cellRect.y + tableLocation.y + table.getRowHeight()
					+ 25);
		} else {
			cellRect.setLocation(cellRect.x + tableLocation.x + cellRect.width
					/ 2, cellRect.y + tableLocation.y - table.getRowHeight()
					- 20);
		}
		// System.out.println("TableRowHeight:" + table.getRowHeight());
		// System.out.println("TableHeight:" + table.getHeight());
		// System.out.println("Cell location (on view port):" + cellRect);
		TransformationPoint finalLocation = new TransformationPoint(cellRect.x,
				cellRect.y, pType);
		// System.out.println("point:" + tableLocation);

		return finalLocation;
	}

	/**
	 * Calculate the end points for one transformation between table columns.
	 * 
	 * @param fromTable
	 * @param toTable
	 * @param fromColumn
	 * @param toColumn
	 * @return a point array with the from point and the to point.
	 */
	private TransformationPoint[] calculateEndPoints(TableData fromTable,
			TableData toTable, String fromColumn, String toColumn) {

		TransformationPoint[] points = {
				calculatePoint(fromTable, fromColumn,
						TransformationPoint.pointTypes.START),
				calculatePoint(toTable, toColumn,
						TransformationPoint.pointTypes.END) };
		return points;
	}

	/**
	 * Adds one route.
	 * 
	 * @param route
	 */
	private void addRoute(Route route) {
		routes.add(route);
	}

	/**
	 * Paints one route.
	 * 
	 * @param route
	 * @param g
	 */
	private void paintRoute(Route route, Graphics g) {
		TransformationPoint point1, point2;
		g.setColor(Color.black);

		point1 = route.getStartPoint();
		point2 = route.getEndPoint();
		TransformationPoint tmpPoint = null;

		directions currentDirection = directions.DOWN;
		// Check if the start point is invalid. If yes, the draw a red
		// circle as the starting point to show it is invalid.
		if (point1.pointType
				.equals(TransformationPoint.pointTypes.INVALID_START)) {
			Color oldColor = g.getColor();
			g.setColor(Color.RED);
			g.fillOval(point1.x - 4, point1.y - 9, 9, 9);
			g.setColor(oldColor);
			g.drawOval(point1.x - 4, point1.y - 9, 9, 9);
		}

		// System.out.println("start:" + point1 + ", end: " + point2);
		while (!point1.equals(point2)) {

			tmpPoint = new TransformationPoint(point2, point2.pointType);

			if (currentDirection == directions.DOWN) {
				// Going DOWN
				if (point1.getX() == tmpPoint.getX()) {
					// Straight down to the end
					// System.out.println("Going straight down");
				} else {
					if (point1.getY() != tmpPoint.getY()) {
						// Half way down
						// System.out.println("Going halfway down");
						tmpPoint.setLocation(point1.getX(), point1.getY()
								+ ((tmpPoint.getY() - point1.getY()) / 2));
					}
					if (tmpPoint.getX() < point1.getX()) {
						currentDirection = directions.LEFT;
						// System.out.println("Next to the left...");
					} else {
						currentDirection = directions.RIGHT;
						// System.out.println("Next to the right...");
					}
				}
			} else if (currentDirection == directions.LEFT) {
				// Going LEFT
				// System.out.println("Going left");
				tmpPoint.setLocation(tmpPoint.getX(), point1.getY());
				currentDirection = directions.DOWN;
			} else if (currentDirection == directions.RIGHT) {
				// Going RIGHT
				// System.out.println("Going right");
				tmpPoint.setLocation(tmpPoint.getX(), point1.getY());
				currentDirection = directions.DOWN;
			} else {
				// should never get here
			}
			g.drawLine((int) point1.getX(), (int) point1.getY(), (int) tmpPoint
					.getX(), (int) tmpPoint.getY());
			point1 = tmpPoint;
			// System.out.println("New from point: " + point1);
		}

		// Check if the end point is invalid. If yes, the draw a red
		// circle as the ending point to show it is invalid.
		if (point2.pointType.equals(TransformationPoint.pointTypes.INVALID_END)) {
			Color oldColor = g.getColor();
			g.setColor(Color.RED);
			g.fillOval(point1.x - 4, point1.y, 9, 9);
			g.setColor(oldColor);
			g.drawOval(point1.x - 4, point1.y, 9, 9);
		}

		// System.out.println("done.");
	}
}
