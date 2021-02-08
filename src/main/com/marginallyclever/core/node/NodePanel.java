package com.marginallyclever.core.node;

import java.awt.Dimension;

import com.marginallyclever.core.select.SelectBoolean;
import com.marginallyclever.core.select.SelectDouble;
import com.marginallyclever.core.select.SelectInteger;
import com.marginallyclever.core.select.SelectPanel;
import com.marginallyclever.core.select.SelectReadOnlyText;
import com.marginallyclever.core.select.SelectString;

/**
 * All generators have a panel with options.  This is their shared root.
 * @author Dan Royer
 *
 */
public class NodePanel extends SelectPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Node myNode;
	
	protected NodePanel() {
		super();
	}

	public NodePanel(Node node) {
		super();
		setMyNode(node);
	}

	public Node getMyNode() {
		return myNode;
	}

	private void setMyNode(Node myNode) {
		this.myNode = myNode;
	}
	
	public void buildInputPanel() {
		System.out.println("buildInputPanel "+myNode.getName());
		
		getInteriorPanel().removeAll();

		for(NodeConnector<?> nc : myNode.inputs ) {
			System.out.println("Node input "+nc.getType());
			String typeName = nc.getType();
			String inputName = nc.getName();
			
			if(typeName.equals("Boolean")) {
				this.add(new SelectBoolean(inputName, ((NodeConnector<Boolean>)nc).getValue()));
			}
			if(typeName.equals("Integer")) {
				this.add(new SelectInteger(inputName, ((NodeConnector<Integer>)nc).getValue()));
			}
			if(typeName.equals("Double")) {
				this.add(new SelectDouble(inputName, ((NodeConnector<Double>)nc).getValue()));
			}
			if(typeName.equals("String")) {
				this.add(new SelectString(inputName, ((NodeConnector<String>)nc).getValue()));
			}
			
			if(typeName.equals("TransformedImage")) {
				this.add(new SelectReadOnlyText("TransformedImage "+inputName));
			};
			if(typeName.equals("Turtle")) {
				this.add(new SelectReadOnlyText("Turtle "+inputName));
			};
		}
		
		this.finish();
	}
}
