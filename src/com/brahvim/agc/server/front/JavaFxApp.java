package com.brahvim.agc.server.front;

import java.util.ArrayList;
import java.util.List;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.back.BackendNotification;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class JavaFxApp extends Application {

	public static final Rectangle2D PRIMARY_SCREEN_RECT = Screen.getPrimary().getBounds();

	@Override
	public void stop() throws Exception {
	}

	@Override
	public void start(final Stage p_stage) throws Exception {
		final Button closeButton = new Button("Press to close.");

		closeButton.setOnAction(event -> {
			System.out.println("Button press detected.");
			App.exit(ExitCode.OKAY);
		});

		final var clientsList = new ListView<String>(); // Only one selection by default. Phew!
		final var row1 = new HBox(clientsList, closeButton);
		final var rootPane = new VBox(new MenuBar(

				new Menu("Click!")

		), row1);

		final List<String> fakeData = new ArrayList<>();

		for (int i = 0; i < 20; i++)
			fakeData.add("Client " + (Double.hashCode(i)));

		clientsList.getItems().addAll(fakeData);
		clientsList.setCellFactory(p_listView -> new ListCell<String>() {

			@Override
			protected void updateItem(final String p_item, final boolean p_isEmpty) {
				super.updateItem(p_item, p_isEmpty);
				super.setText(p_item);
				super.getHeight();

				super.setOnKeyPressed(p_keyEvent -> {
					System.out.println("Key pressed with list cell focused.");
					if (p_keyEvent.getCode() == KeyCode.DELETE) {
						super.getListView().getItems().remove(p_item);
					}
				});
			}

		});

		clientsList.setOnKeyPressed(p_keyEvent -> {
			// System.out.println("Key pressed with list-view in focus!");
			if (p_keyEvent.getCode() != KeyCode.DELETE)
				return;

			clientsList.getItems().remove(clientsList.getSelectionModel().selectedItemProperty().get());
		});

		clientsList.getSelectionModel().selectedItemProperty().addListener(
				(p_observable2, p_oldValue2, p_newValue2) -> System.out.printf("Item `%s` selected!%n", p_newValue2));

		row1.heightProperty().addListener(
				(p_observable1, p_oldValue1, p_newValue1) -> row1.setPrefHeight(p_newValue1.doubleValue()));

		// final var dialog = WaitingDialogBuilder.open();
		p_stage.setTitle("AndroidGameController - Home");
		// p_stage.initStyle(StageStyle.TRANSPARENT);
		p_stage.setScene(new Scene(rootPane));
		p_stage.setResizable(true);
		p_stage.setMinHeight(240);
		p_stage.setMinWidth(480);
		p_stage.setHeight(240);
		p_stage.setWidth(480);
		p_stage.show();

		p_stage.widthProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			p_stage.setX((JavaFxApp.PRIMARY_SCREEN_RECT.getWidth() - p_stage.getWidth()) / 2);
			p_stage.setY((JavaFxApp.PRIMARY_SCREEN_RECT.getHeight() - p_stage.getHeight()) / 2);
		});

		// p_stage.hide();
		// dialog.show();

		BackendNotification.START_BACKEND.fire();
		System.out.println("Frontend now awaiting backend...");

		// // This will run on the JavaFX thread.
		// FrontendNotification.BACKEND_STARTED.onUiThreadWhenFired(() -> {
		// dialog.close();
		// p_stage.show();
		// });
	}

}
