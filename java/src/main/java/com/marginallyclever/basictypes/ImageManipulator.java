package com.marginallyclever.basictypes;


import java.io.IOException;
import java.io.Writer;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import com.marginallyclever.drawingtools.DrawingTool;
import com.marginallyclever.makelangelo.DrawPanel;
import com.marginallyclever.makelangeloRobot.MakelangeloRobot;
import com.marginallyclever.makelangeloRobot.MakelangeloRobotSettings;


/**
 * shared methods for image manipulation (generating, converting, or filtering)
 * @author Dan
 */
public abstract class ImageManipulator {
	// helpers
	protected float w2, h2, scale;
	protected DrawingTool tool;

	// file properties
	protected String dest;
	// pen position optimizing
	protected boolean lastUp;
	protected float previousX, previousY;
	
	// threading
	protected ProgressMonitor pm;
	protected SwingWorker<Void, Void> parent;

	protected MakelangeloRobotSettings machine;

	protected float sampleValue;
	protected float sampleSum;

	protected DrawPanel drawPanel;


	public void setParent(SwingWorker<Void, Void> p) {
		parent = p;
	}

	public void setProgressMonitor(ProgressMonitor p) {
		pm = p;
	}
	
	public void setMachine(MakelangeloRobot robot) {
		machine = robot.settings;
	}

	public void setDrawPanel(DrawPanel drawPanel) {
		this.drawPanel = drawPanel;
	}

	/**
	 * @return the translated name of the manipulator.
	 */
	public String getName() {
		return "Unnamed";
	}


	protected void liftPen(Writer out) throws IOException {
		tool.writeOff(out);
		lastUp = true;
	}


	protected void lowerPen(Writer out) throws IOException {
		tool.writeOn(out);
		lastUp = false;
	}

	protected void setAbsoluteMode(Writer out) throws IOException {
		out.write("G90;\n");
	}

	protected void setRelativeMode(Writer out) throws IOException {
		out.write("G91;\n");
	}

	protected void setupTransform() {
		double imageHeight = machine.getPaperHeight()*machine.getPaperMargin();
		double imageWidth = machine.getPaperWidth()*machine.getPaperMargin();
		h2 = (float)imageHeight / 2.0f;
		w2 = (float)imageWidth / 2.0f;

		scale = 1;  // 10mm = 1cm

		double newHeight = imageHeight;

		if (imageWidth > machine.getPaperWidth()) {
			float resize = (float) machine.getPaperWidth() / (float) imageWidth;
			scale *= resize;
			newHeight *= resize;
		}
		if (newHeight > machine.getPaperHeight()) {
			float resize = (float) machine.getPaperHeight() / (float) newHeight;
			scale *= resize;
		}
	}

	protected float SX(float x) {
		return x * scale;
	}

	protected float SY(float y) {
		return y * scale;
	}

	protected float PX(float x) {
		return x - w2;
	}

	protected float PY(float y) {
		return h2 - y;
	}

	protected float TX(float x) {
		return SX(PX(x));
	}

	protected float TY(float y) {
		return SY(PY(y));
	}

	/**
	 * Create the gcode that will move the robot to a new position.
	 * @param out where to write the gcode
	 * @param x new coordinate
	 * @param y new coordinate
	 * @param up new pen state
	 * @throws IOException on write failure
	 */
	protected void moveTo(Writer out, float x, float y, boolean up) throws IOException {
		tool.writeMoveTo(out, TX(x), TY(y));
		if (lastUp != up) {
			if (up) liftPen(out);
			else lowerPen(out);
			lastUp = up;
		}
	}

	/**
	 * This is a special case of moveTo() that only works when every line on the paper is a straight line.
	 * @param out where to write the gcode
	 * @param x new coordinate
	 * @param y new coordinate
	 * @param up new pen state
	 * @throws IOException on write failure
	 */
	protected void lineTo(Writer out, float x, float y, boolean up) throws IOException {
		if (up != lastUp) {
			moveTo(out,x,y,up);
		}
	}

	protected void moveToPaper(Writer out, double x, double y, boolean up) throws IOException {
		tool.writeMoveTo(out, (float) x, (float) y);
		if(lastUp != up) {
			if (up) liftPen(out);
			else lowerPen(out);
			lastUp = up;
		}
	}
}

/**
 * This file is part of Makelangelo.
 * <p>
 * Makelangelo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Makelangelo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Makelangelo.  If not, see <http://www.gnu.org/licenses/>.
 */
