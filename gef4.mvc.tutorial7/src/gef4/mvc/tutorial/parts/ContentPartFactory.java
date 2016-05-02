package gef4.mvc.tutorial.parts;

import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import gef4.mvc.tutorial.model.Model;
import gef4.mvc.tutorial.model.TextNode;
import javafx.scene.Node;

@SuppressWarnings("restriction")
public class ContentPartFactory implements IContentPartFactory<Node> {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<Node, ? extends Node> createContentPart(Object content, IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {

		if (content instanceof Model) {
			return injector.getInstance(ModelPart.class);
		} else if (content instanceof TextNode) {
			return injector.getInstance(TextNodePart.class);
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	};

}
