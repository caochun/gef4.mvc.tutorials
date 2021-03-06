package gef4.mvc.tutorial;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.VisualOutlineGeometryProvider;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import gef4.mvc.tutorial.parts.ModelPartFactory;
import gef4.mvc.tutorial.parts.TextNodePart;
import gef4.mvc.tutorial.policies.TextNodeOnDoubleClickPolicy;
import gef4.mvc.tutorial.policies.TextNodeOnTypePolicy;
import gef4.mvc.tutorial.policies.TextNodeTransformPolicy;
import javafx.scene.Node;

public final class GuiceModule extends MvcFxModule {
	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)

		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY))
				.to(FXFocusAndSelectOnClickPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(FXHoverOnHoverPolicy.class);

	}

	protected void bindTextNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {

		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER)).to(VisualOutlineGeometryProvider.class);

		// geometry provider for selection handles
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER)).to(VisualOutlineGeometryProvider.class);

		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualOutlineGeometryProvider.class);

		// register resize/transform policies (writing changes also to model)
		adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class)).to(TextNodeTransformPolicy.class);
		// .to(FXTransformPolicy.class);

		// interaction policies to relocate on drag (including anchored
		// elements, which are linked)
		adapterMapBinder.addBinding((AdapterKey.get(FXResizeRelocatePolicy.class))).to(FXResizeRelocatePolicy.class);

		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY))
				.to(FXRelocateOnDragPolicy.class);

		// edit node label policies
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "TextNodeOnDoubleClickPolicy"))
				.to(TextNodeOnDoubleClickPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY)).to(TextNodeOnTypePolicy.class);

	}

	@Override
	protected void configure() {
		super.configure();

		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).toInstance(new ModelPartFactory());

		bindTextNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), TextNodePart.class));

	}
}