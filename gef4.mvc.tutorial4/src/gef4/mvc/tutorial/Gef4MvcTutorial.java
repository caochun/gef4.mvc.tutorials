package gef4.mvc.tutorial;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.VisualBoundsGeometryProvider;
import org.eclipse.gef4.mvc.fx.parts.VisualOutlineGeometryProvider;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;

import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import gef4.mvc.tutorial.model.Model;
import gef4.mvc.tutorial.model.TextNode;
import gef4.mvc.tutorial.parts.ModelPart;
import gef4.mvc.tutorial.parts.ModelPartFactory;
import gef4.mvc.tutorial.parts.TextNodePart;
import gef4.mvc.tutorial.policies.AbstractCloneContentPolicy;
import gef4.mvc.tutorial.policies.CloneShapePolicy;
import gef4.mvc.tutorial.policies.FXCloneRelocateOnDragPolicy;
import gef4.mvc.tutorial.policies.FXRelocateLinkedOnDragPolicy;
import gef4.mvc.tutorial.policies.FXTransformShapePolicy;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@SuppressWarnings("serial")
public class Gef4MvcTutorial extends Application {

	private Model model;
	private JAXBContext jaxbContext;

	public static void main(String[] args) {
		Application.launch(args);
	}

	public void start(final Stage primaryStage) throws Exception {
		jaxbContext = JAXBContext.newInstance(Model.class, TextNode.class);

		Injector injector = Guice.createInjector(createGuiceModule());

		FXDomain domain = injector.getInstance(FXDomain.class);

		FXViewer viewer = domain.getAdapter(IViewer.class);

		AnchorPane paneCtrl = new AnchorPane();
		AnchorPane paneDraw = new AnchorPane();
		VBox vbox = new VBox(paneCtrl, paneDraw);
		vbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		Button btnUpdateModel = new Button("update model");
		btnUpdateModel.setOnAction(e -> model.doChanges());
		btnUpdateModel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		paneCtrl.getChildren().add(btnUpdateModel);
		AnchorPane.setTopAnchor(btnUpdateModel, 10d);
		AnchorPane.setLeftAnchor(btnUpdateModel, 10d);
		AnchorPane.setRightAnchor(btnUpdateModel, 10d);

		Node drawingPane = viewer.getScrollPane();
		paneDraw.getChildren().add(drawingPane);
		paneDraw.setPrefHeight(2000);
		AnchorPane.setTopAnchor(drawingPane, 10d);
		AnchorPane.setLeftAnchor(drawingPane, 10d);
		AnchorPane.setRightAnchor(drawingPane, 10d);
		AnchorPane.setBottomAnchor(drawingPane, 10d);

		primaryStage.setScene(new Scene(vbox));

		primaryStage.setResizable(true);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);
		primaryStage.setTitle("GEF4 MVC Tutorial 4 - Drag and persist");

		primaryStage.show();

		domain.activate();

		viewer.getAdapter(ContentModel.class).setContents(createContents());
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(model, new File("model.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected List<? extends Object> createContents() {
		if (Files.isReadable(Paths.get("model.xml"))) {
			try {
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				model = (Model) jaxbUnmarshaller.unmarshal(new File("model.xml"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (model == null) {
			model = new Model();
			model.addNode(new TextNode(20, 20, "First"));
			model.addNode(new TextNode(20, 120, "Second"));

		}

		return Collections.singletonList(model);
	}

	protected Module createGuiceModule() {
		return new MvcFxModule() {

			@SuppressWarnings("restriction")
			@Override
			protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
				super.bindAbstractContentPartAdapters(adapterMapBinder);
				// register (default) interaction policies (which are based on
				// viewer
				// models and do not depend on transaction policies)

				adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY))
						.to(FXFocusAndSelectOnClickPolicy.class);

				adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(FXHoverOnHoverPolicy.class);

				// geometry provider for selection feedback
				adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
				}, FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
						.to(VisualBoundsGeometryProvider.class);

				// geometry provider for hover feedback
				adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
				}, FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
						.to(VisualBoundsGeometryProvider.class);
			}

			@SuppressWarnings("restriction")
			protected void bindTextNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
				adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
				}, FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
						.to(VisualOutlineGeometryProvider.class);

				// geometry provider for selection handles
				adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
				}, FXDefaultHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
						.to(VisualOutlineGeometryProvider.class);

				adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
				}, FXDefaultFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
						.to(VisualOutlineGeometryProvider.class);

				// geometry provider for hover feedback
//				adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
//				}, FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
//						.to(VisualOutlineGeometryProvider.class);

				// register resize/transform policies (writing changes also to
				// model)

				adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class)).to(FXTransformShapePolicy.class);

				// interaction policies to relocate on drag (including anchored
				// elements, which are linked)


				adapterMapBinder
				.addBinding((AdapterKey.get(FXResizeRelocatePolicy.class)))
				.to(FXResizeRelocatePolicy.class);
				
				
				
//				adapterMapBinder
//				.addBinding(
//						AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY))
//				.to(FXCloneRelocateOnDragPolicy.class);
				
				adapterMapBinder
				.addBinding(
						AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY))
				.to(FXRelocateOnDragPolicy.class);
				
//				adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "relocateLinked"))
//						.to(FXRelocateLinkedOnDragPolicy.class);

//				adapterMapBinder.addBinding(AdapterKey.get(AbstractCloneContentPolicy.class))
//						.to(CloneShapePolicy.class);
			}

			@Override
			protected void configure() {
				super.configure();

				binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
				}).toInstance(new ModelPartFactory());

				bindTextNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), TextNodePart.class));
			}
		};
	}
}
