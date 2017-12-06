/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotools.geotools_swing.tools;

import java.awt.Cursor;

import org.geotools.geotools_swing.MapListener;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapMouseAdapter;

/**
 * The base class for map pane cursor tools. Simply adds a getCursor
 * method to the MapToolAdapter
 * 
 * @author Michael Bedward
 * @since 2.6
 *
 *
 * @source $URL$
 * @version $Id$
 */
public abstract class CursorTool extends MapMouseAdapter implements MapListener {

    private MapPane mapPane;
    
    /**
     * Get the map pane that this tool is servicing
     *
     * @return the map pane
     */
    @Override
    public MapPane getMapPane() {
    	return mapPane;
    }

    /**
     * Set the map pane that this cursor tool is associated with
     * @param pane the map pane
     * @throws IllegalArgumentException if mapPane is null
     */
    @Override
    public void setMapPane(MapPane pane) {
        if (pane == null) {
            throw new IllegalArgumentException("pane arg must not be null");
        }

        this.mapPane = pane;
        this.mapPane.addMouseListener(this);
    }
    
    /**
     * 
     *  when change to another CursorTool's call
     */
    public void unUsed(){
    	mapPane.removeMouseListener(this);
    }

    /**
     * Get the cursor for this tool. Sub-classes should override this
     * method to provide a custom cursor.
     *
     * @return the default cursor
     */
    public Cursor getCursor() {
        return Cursor.getDefaultCursor();
    }
    
}
