/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package gef4.mvc.tutorial.policies;

import org.eclipse.gef4.geometry.planar.IShape;

import gef4.mvc.tutorial.model.TextNode;
import gef4.mvc.tutorial.parts.TextNodePart;

// only applicable for FXGeometricShapePart
public class CloneShapePolicy extends AbstractCloneContentPolicy {

	@Override
	public Object cloneContent() {
		TextNode originalShape = getHost().getContent();
		TextNode shape = new TextNode(originalShape.getPosition().x, originalShape.getPosition().y,
				originalShape.getText());

		return shape;
	}

	@Override
	public TextNodePart getHost() {
		return (TextNodePart) super.getHost();
	}

}
