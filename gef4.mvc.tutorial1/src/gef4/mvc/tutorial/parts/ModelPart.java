package gef4.mvc.tutorial.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import gef4.mvc.tutorial.model.Model;
import javafx.scene.paint.Color;

public class ModelPart extends AbstractFXContentPart<FXGeometryNode<RoundedRectangle>> {

	@Override
	public Model getContent() {
		return (Model)super.getContent();
	}

	@Override
	protected FXGeometryNode<RoundedRectangle> createVisual() {
		return new FXGeometryNode<>();
	}

	@Override
	protected void doRefreshVisual(FXGeometryNode<RoundedRectangle> visual) {
		Model model = getContent();
		visual.setGeometry(new RoundedRectangle(model.getRect(), 10, 10 ));
		visual.setFill( model.getColor() );
		visual.setStroke( Color.BLACK );
		visual.setStrokeWidth(2);
	}

	@Override
	public SetMultimap<? extends Object, String> getContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	public List<? extends Object> getContentChildren() {
		return Collections.emptyList();
	}

	
}
