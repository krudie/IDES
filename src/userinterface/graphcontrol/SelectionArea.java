/*
 * Created on Dec 2, 2004
 */
package userinterface.graphcontrol;


import ides2.SystemVariables;

import java.util.StringTokenizer;

import userinterface.GraphingPlatform;
import userinterface.ResourceManager;
import userinterface.geometric.Box;
import userinterface.geometric.Point;

/**
 * This class represents a crosshairs, and dotted line rectangle area. It is used both to specify the print area
 * and to generate groupings.  The two behaviours require some speeration in some of the methods, but the both 
 * have a lot in common.
 * 
 * @author Michael Wood
 */
public class SelectionArea 
{

    /**
     * The platform in which this GraphObject exists.
     */
    protected GraphingPlatform gp = null;

	/**
     * The padding for minimum size bounding rectangles.
     */
	public static final int PADDING = 6;
	
    /**
     * Records the location of the selection area.
     * values: {first_x,first_y,second_x,second_y,state,cursor,active_index}.
     * state = 0 -> invisible.
     * state = 1 -> visible.
     * state = 2 -> in mid modify (ie mouse is down).
     * cursor = 0 -> cross.
     * cursor = 1 -> lr.
     * cursor = 2 -> ud.
     * cursor = 3 -> hand.
     */
	private int[] selection_area = null;
	
	/**
     * Specifies the behaviour of the SelectionArea
     */
	private int option = 0;
	
	/**
     * Specifies behaviour of this SelectionArea.
     */
	public static final int MARKING_OUT_AN_AREA = 0,
							SELECTING_OBJECTS_INSIDE_AN_AREA = 1;
	
	/**
     * Used in moving a group of graph parts.
     */
	private Point previous = new Point(0,0);
	
	/**
	 * Optionally force the area to draw as solid black.
	 */
	public boolean draw_solid = false;
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SelectionArea construction /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
	
    /**
     * Construct the SelectionArea.
     *
     * @param	gp		The platform in which this SelectionArea will exist.
     * @param	option	Specifies the behaviour of this SelectionArea.
     */
	public SelectionArea(GraphingPlatform gp,int option)
	{
	    this.gp = gp;
		this.option = option;
		selection_area = new int[] {0,0,0,0,0,0,0};
	}
	
    /**
     * Construct the SelectionArea from a co-ordinate string.
     *
     * @param	gp		The platform in which this SelectionArea will exist.
     * @param	option	Specifies the behaviour of this SelectionArea.
     * @param	coords	The starting co-ordinates in the form: "x1,y1,x2,y2"
     */
	public SelectionArea(GraphingPlatform gp, int option, String coords)
	{
        this.gp = gp;
		this.option = option;		
		selection_area = new int[] {0,0,0,0,0,0,0};
		StringTokenizer s = new StringTokenizer(coords,",");
		if (s.hasMoreTokens()) { selection_area[0] = Integer.parseInt(s.nextToken()); }	else { selection_area[0] = 0; }
		if (s.hasMoreTokens()) { selection_area[1] = Integer.parseInt(s.nextToken()); }	else { selection_area[1] = 0; }
		if (s.hasMoreTokens()) { selection_area[2] = Integer.parseInt(s.nextToken()); }	else { selection_area[2] = 0; }
		if (s.hasMoreTokens()) { selection_area[3] = Integer.parseInt(s.nextToken()); }	else { selection_area[3] = 0; }
		selection_area[4] = 1;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Mousing ////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
     * Handle the mouse-up event.
     */
	public void mouseUp()
	{
		if (option == MARKING_OUT_AN_AREA)
		{
			// print area
			if (selection_area[5] == 3)
			{
				// moving the area
				selection_area[4] = 1; // set as visible, hence, no longer in mid modify
			}
			else
			{
				// finishing modification or creation
				setVisibleBySize();
				// clean the coords so that 01 are top left
				int x1, y1, x2, y2;
				if (selection_area[0] > selection_area[2]) { x2 = selection_area[0]; x1 = selection_area[2]; } 
				else { x2 = selection_area[2]; x1 = selection_area[0]; }
				if (selection_area[1] > selection_area[3]) { y2 = selection_area[1]; y1 = selection_area[3]; } 
				else { y2 = selection_area[3]; y1 = selection_area[1]; }
				selection_area[0] = x1;
				selection_area[1] = y1;
				selection_area[2] = x2;
				selection_area[3] = y2;
			}
			gp.gc.io.markUnsavedChanges();
		}
		else
		{
			// group area
			if (selection_area[5] == 3)
			{
				// moving a selected group
				gp.gc.gpc.terminateNodeMovement();
				selection_area[4] = 1; // set as visible, hence, no longer in mid modify
				gp.gc.io.markUnsavedChanges();
			}
			else
			{
				// creating the area
				gp.gc.group_area.setVisible(false);
				Box area = gp.gc.group_area.getBox();
				gp.gc.gpc.abandonGroupHistory();
				gp.gc.group_area.highlite(gp.gc.gm.addToGrouping(area));
			}
		}		
	}
	
	/**
     * Handle the mouse-down event.
     * 
     * @param	mouse	The co-ordinates of the mouse.
     */
	public void mouseDown(Point mouse)
	{
		if (selection_area[5] == 0)
		{
			// creating a new selection area
			selection_area[0] = mouse.x;
			selection_area[1] = mouse.y;
			selection_area[2] = mouse.x;
			selection_area[3] = mouse.y;
		}
		else if (selection_area[5] == 1)
		{
			// modifying an existing selection area in the lr direction
			// record which index we will be modifying
			if (Math.abs(mouse.x-selection_area[0]) > Math.abs(mouse.x-selection_area[2]))
			{ selection_area[6] = 2; }
			else
			{ selection_area[6] = 0; }
		}
		else if (selection_area[5] == 2)
		{
			// modifying an existing selection area in the ud direction
			// record which index we will be modifying
			if (Math.abs(mouse.y-selection_area[1]) > Math.abs(mouse.y-selection_area[3]))
			{ selection_area[6] = 3; }
			else
			{ selection_area[6] = 1; }
		}
		else if (selection_area[5] == 3)
		{
			// moving a selected group
			previous.copy(mouse);
			if (option == SELECTING_OBJECTS_INSIDE_AN_AREA)
			{ gp.gc.gpc.initiateNodeMovement(mouse); }
		}
		selection_area[4] = 2;  // mark as "in mid modify"		
	}
	
	/**
     * Handle the mouse-down event.
     * 
     * @param	mouse	The co-ordinates of the mouse.
     */
	public void mouseMove(Point mouse)
	{
		if (selection_area[4] == 2)
		{
			// marked as "in mid modify"
			if (selection_area[5] == 0)
			{
				selection_area[2] = mouse.x;
				selection_area[3] = mouse.y;
			}
			else if (selection_area[5] == 1) { selection_area[selection_area[6]] = mouse.x;  }
			else if (selection_area[5] == 2) { selection_area[selection_area[6]] = mouse.y; }

			else if (selection_area[5] == 3)
			{
				Point displacement = new Point(mouse.x-previous.x,mouse.y-previous.y);
				if (option == MARKING_OUT_AN_AREA)
				{
					translateAll(displacement);
					previous = previous.plus(displacement);					
				}
				else if (option == SELECTING_OBJECTS_INSIDE_AN_AREA)
				{
					boolean move_area = true;
					// if there is a grid, only perform translations in grid increments.
					if (SystemVariables.grid > 0)
					{
						if (Math.abs(mouse.x-previous.x) >= SystemVariables.grid || Math.abs(mouse.y-previous.y) >= SystemVariables.grid)
						{ 
							Point origin = gp.gc.gm.snapToGrid(previous.getCopy()); 
							Point destination = gp.gc.gm.snapToGrid(mouse.getCopy()); 
							displacement = destination.minus(origin);
						}
						else { move_area = false; }
					}
					if (move_area)
					{
						gp.gc.gpc.translateAll(displacement.x,displacement.y,false);
						translateAll(displacement);
						gp.gc.gpc.updateNodeMovement(mouse);
						previous = previous.plus(displacement);
					}
				}
			}
			gp.gc.repaint();
		}
		else
		{
			// update the cursor depending on what it is mousing over
			if (selection_area[4] == 1)
			{
				// marked as "visible"

				if (option == MARKING_OUT_AN_AREA)
				{
					if (isOnHorizontal(mouse.x,mouse.y))
					{
						// mousing over horizontal border
						if (selection_area[5] != 2)
						{ 
							selection_area[5] = 2;
							gp.gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.UPDOWN_CURSOR)); 
						}
					}
					else if (isOnVerticle(mouse.x,mouse.y))
					{
						// mousing over vertical border
						if (selection_area[5] != 1)
						{ 
							selection_area[5] = 1;
							gp.gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.LEFTRIGHT_CURSOR)); 
						}
					}
					else if (isInBounds(mouse.x,mouse.y))
					{
						// mousing over the area
						if (selection_area[5] != 3)	
						{ 
							selection_area[5] = 3;
							gp.gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.GRAB_CURSOR)); 
						}
					}
					else
					{
						// mousing over empty space
						if (selection_area[5] != 0)
						{ 
							selection_area[5] = 0;
							gp.gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.MODIFY_CURSOR)); 
						}
					}
				}
				else if (option == SELECTING_OBJECTS_INSIDE_AN_AREA)
				{
					if (mouse.x > selection_area[0] && mouse.x < selection_area[2] && mouse.y > selection_area[1] && mouse.y < selection_area[3])
					{
						if (selection_area[5] != 3)	
						{ 
							selection_area[5] = 3;
							gp.gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.GRAB_CURSOR)); 
						}
					}
					else
					{
						if (selection_area[5] != 0)	
						{ 
							selection_area[5] = 0;
							gp.gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.MODIFY_CURSOR)); 
						}						
					}
				}
			}
		}		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// miscellaneous //////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

	/**
	 * Tests if the given co-ordinates lie inside this SelectionArea.
	 */
	public boolean isInBounds(int x, int y)
	{
		if (x > selection_area[0] && x < selection_area[2] && y > selection_area[1] && y < selection_area[3])
		{ return true; }
		else
		{ return false; }
	}
	
	private void setVisibleBySize()
	{
		if (Math.abs(selection_area[0]-selection_area[2]) > 20 && Math.abs(selection_area[1]-selection_area[3]) > 20)
		{ selection_area[4] = 1; } // mark as "visible" 
		else 
		{ selection_area[4] = 0; } // mark as "invisible" 
	}
	
	public void highlite(Box area)
	{
		if (area != null)
		{
			selection_area[0] = area.x1();
			selection_area[1] = area.y1();
			selection_area[2] = area.x2();
			selection_area[3] = area.y2();
			setVisibleBySize();
		}
		else
		{ selection_area[4] = 0; }
	}
	
    /**
     * Reset all variables to the initial state.
     */
	public void resetState()
	{
		selection_area[4]=0;
	}	
	
    /**
     * Draw this SelectionArea
     *
     * @param	drawer		The Drawer that will handle the drawing.
     */
	public void draw(Drawer drawer) 
	{
		if (selection_area[4] > 0)
		{
			int x, y, w, h;
			if (selection_area[0] < selection_area[2]) { x = selection_area[0]; w = selection_area[2]-selection_area[0]; } 
			else { x = selection_area[2]; w = selection_area[0]-selection_area[2]; }
			if (selection_area[1] < selection_area[3]) { y = selection_area[1]; h = selection_area[3]-selection_area[1]; } 
			else { y = selection_area[3]; h = selection_area[1]-selection_area[3]; }

			if (draw_solid)
			{ 
				drawer.setColor(GraphModel.NORMAL);
				drawer.drawBoxWH(x,y,w,h,Drawer.SOLID); 
			}
			else
			{ drawer.drawBoxWH(x,y,w,h,Drawer.DASHED); }
		}
	}
	
    /**
     * Translate this SelectionArea
     *
     * @param	displacement	The translation to be performed.
     */
	public void translateAll(Point displacement)
	{
		selection_area[0] = selection_area[0] + displacement.x;
		selection_area[1] = selection_area[1] + displacement.y;
		selection_area[2] = selection_area[2] + displacement.x;
		selection_area[3] = selection_area[3] + displacement.y;
	}

    /**
     * Output the string representation of this Object
     *
     * @return	A comma delimited string of the four points of this SelectionArea
     */
	public String toString()
	{
		return selection_area[0] + "," +selection_area[1] + "," + selection_area[2] + "," + selection_area[3];
	}

    /**
     * Get a Box representation of this SelectionArea.
     *
     * @return	A Box representing this SelectionArea.
     */
	public Box getBox()
	{
		int x1, y1, x2, y2;
		
		if (selection_area[0] < selection_area[2]) { x1 = selection_area[0]; x2 = selection_area[2]; }
		else { x1 = selection_area[2]; x2 = selection_area[0]; }
		if (selection_area[1] < selection_area[3]) { y1 = selection_area[1]; y2 = selection_area[3]; }
		else { y1 = selection_area[3]; y2 = selection_area[1]; }

		return new Box(x1,y1,x2,y2);
	}	
	
	/**
	 * Test if the cursor is currently hovering over the selection area interior.
	 * 
	 * @return	true If the cursor is currently hovering over the selection area interior.
	 */
	public boolean cursorIsOverArea()
	{
		return (selection_area[5] == 3);
	}
	
    /**
     * Check if this SelectionArea is visible.
     *
     * @return	true if this SelectionArea is visible.
     */
	public boolean isVisible()
	{
		if (selection_area[4] == 1) { return true; }
		else { return false; }
	}

    /**
     * Set the visibility of this SelectionArea.
     * If it is set to invisible, then the cursor bit (remembers state) is also reset.
     *
     * @param	visible		The new visiblity status.
     */
	public void setVisible(boolean visible)
	{
		if (visible) { selection_area[4] = 1; }
		else 
		{ 
			selection_area[4] = 0; 
			// make sure that now special cursor remain.
			// this happens for instance when we delete a group.
			if (selection_area[5] != 0)	
			{ 
				selection_area[5] = 0;
				gp.gc.j2dcanvas.setCursor(ResourceManager.getCursor(ResourceManager.MODIFY_CURSOR)); 
			}	
		}
	}	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// private helper methods /////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////				
	
	private boolean isOnVerticle(int x, int y)
	{
		int factor = 4;
		boolean answer = false;
				
		// check if inside bounds
		if (y < selection_area[3]+factor && y > selection_area[1]-factor)
		{
			// check if near a line
			if (Math.abs(x-selection_area[2]) < factor || Math.abs(x-selection_area[0]) < factor) { answer = true; }
		}
		
		return answer;
	}
	
	private boolean isOnHorizontal(int x, int y)
	{
		int factor = 4;
		boolean answer = false;

		// check if inside bounds
		if (x < selection_area[2]+factor && x > selection_area[0]-factor)
		{
			// check if near a line
			if (Math.abs(y-selection_area[3]) < factor || Math.abs(y-selection_area[1]) < factor) { answer = true; }
		}		
		
		return answer;
	}
}
