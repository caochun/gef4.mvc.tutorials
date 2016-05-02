/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package gef4.mvc.tutorial.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

public class FXRelocateLinkedOnDragPolicy extends FXRelocateOnDragPolicy {

	@SuppressWarnings({ "unchecked", "restriction" })
	@Override
	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		List<IContentPart<Node, ? extends Node>> selected = super.getTargetParts();
		List<IContentPart<Node, ? extends Node>> linked = new ArrayList<IContentPart<Node, ? extends Node>>();
		for (@SuppressWarnings("restriction") IContentPart<Node, ? extends Node> cp : selected) {
			// ensure that linked parts are moved with us during dragging
			linked.addAll(
					(Collection<? extends IContentPart<Node, ? extends Node>>) new ArrayList<>(
							PartUtils.filterParts(
									PartUtils.getAnchoreds(cp, "link"),
									IContentPart.class)));
		}
		return linked;
	}

}
