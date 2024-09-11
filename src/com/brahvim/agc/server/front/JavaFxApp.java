package com.brahvim.agc.server.front;

import java.util.ArrayList;
import java.util.List;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.WelcomeSockEvent;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class JavaFxApp extends Application {

	public static final Rectangle2D PRIMARY_SCREEN_RECT = Screen.getPrimary().getBounds();

	@Override
	public void stop() throws Exception {
		App.exit(ExitCode.OKAY);
	}

	@Override
	public void start(final Stage p_stage) throws Exception {
		final Button closeButton = new Button("Press to close.");
		this.initCloseButton(closeButton);

		final var clientsList = new ListView<String>(); // Only one selection by default. Phew!
		final var row1 = new HBox(clientsList, closeButton);
		final var rootPane = new VBox(new MenuBar(

				new Menu("Click!")

		), row1);

		final List<String> fakeData = new ArrayList<>();
		this.createFakeData(fakeData);
		this.initClientList(clientsList, fakeData);

		p_stage.heightProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double height = p_newValue.doubleValue();
			clientsList.setPrefHeight(height - (height / 4));
		});
		p_stage.setScene(new Scene(rootPane));
		this.initStage(p_stage);
		p_stage.show();

		Backend.EDT.publish(WelcomeSockEvent.create());
	}

	private void initStage(final Stage p_stage) {
		// final var dialog = WaitingDialogBuilder.open();
		p_stage.setTitle("AndroidGameController - Home");
		// p_stage.initStyle(StageStyle.TRANSPARENT);
		p_stage.setResizable(true);
		p_stage.setMinHeight(240);
		p_stage.setMinWidth(480);
		p_stage.setHeight(240);
		p_stage.setWidth(480);

		p_stage.widthProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			p_stage.setX((JavaFxApp.PRIMARY_SCREEN_RECT.getWidth() - p_stage.getWidth()) / 2);
			p_stage.setY((JavaFxApp.PRIMARY_SCREEN_RECT.getHeight() - p_stage.getHeight()) / 2);
		});
	}

	private void initCloseButton(final Button p_button) {
		p_button.setOnAction(p_event -> {
			System.out.println("Button press detected.");
			App.exit(ExitCode.OKAY);
		});
	}

	private void createFakeData(final List<String> p_list) {
		for (int i = 0; i < 20; i++)
			p_list.add("Client " + (Double.hashCode(i)));
	}

	private void initClientList(final ListView<String> p_listView, final List<String> p_dataList) {
		p_listView.getItems().addAll(p_dataList);
		p_listView.setOnKeyPressed(p_keyEvent -> {
			// System.out.println("Key pressed with list-view in focus!");
			switch (p_keyEvent.getCode()) {
				case DELETE -> p_listView.getItems().removeAll(p_listView.getSelectionModel().getSelectedItems());
				default -> {
					//
				}
			}
		});

		p_listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		p_listView.setCellFactory(p_toGenFor -> new ListCell<String>() {

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

		// clientsList.getSelectionModel().selectedItemProperty().addListener(
		// (p_observable, p_oldValue, p_newValue) ->
		// System.out.printf("Item `%s` selected!%n", p_newValue));
	}

}
