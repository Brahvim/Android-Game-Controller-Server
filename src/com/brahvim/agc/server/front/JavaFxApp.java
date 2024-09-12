package com.brahvim.agc.server.front;

import java.util.ArrayList;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.EventAwaitOneClient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public final class JavaFxApp extends Application {

	// region Fields.
	public static final Rectangle2D PRIMARY_SCREEN_RECT = Screen.getPrimary().getBounds();

	private static final EventHandler<KeyEvent> cbckKeyPressedForUndo = JavaFxApp::cbckKeyPressedForUndo;

	private final Font fontForButtons = new Font(15);

	// NOSONAR, these are to be used *anywhere* in this class!

	private HBox row1 = null; // NOSONAR!
	private VBox col1 = null; // NOSONAR!
	private Stage stage = null; // NOSONAR!
	private Scene scene = null; // NOSONAR!
	private Pane paneRoot = null; // NOSONAR!
	private Menu menuClick = null; // NOSONAR!
	private MenuBar menuBar = null; // NOSONAR!
	private Button buttonLetClientConnect = null; // NOSONAR!
	private Button buttonShowClientDisplay = null; // NOSONAR!
	private ListView<String> listViewClientList = null; // NOSONAR!
	// endregion

	@Override
	public void stop() {
		App.exit(ExitCode.OKAY);
	}

	@Override
	public void start(final Stage p_stage) {
		final var localStage = this.stage = p_stage;

		final var localButtonShowClientDisplay
		/* */ = this.buttonShowClientDisplay
		/* */ /* */ = new Button("Open controls window");

		final var localButtonLetClientConnect
		/* */ = this.buttonLetClientConnect
		/* */ /* */ = new Button("Add client");

		final var localListViewForClients = this.listViewClientList = new ListView<>();
		final var localCol1 = this.col1 = new VBox(localButtonShowClientDisplay, localButtonLetClientConnect);
		final var localRow1 = this.row1 = new HBox(localListViewForClients, localCol1);

		final var localPaneRoot = this.paneRoot = new VBox(localRow1);
		final var localMenuClick = this.menuClick = new Menu("Click!");
		final var localMenuBar = this.menuBar = new MenuBar(localMenuClick);

		localListViewForClients.getItems().addAll(this.createFakeData()); // Can't pass to constructor. Weird.

		localStage.widthProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();

			localListViewForClients.setPrefWidth(side - (side / 1.5));
			localButtonShowClientDisplay.setTranslateX(side / 4);
			localButtonLetClientConnect.setTranslateX(side / 4);
		});

		localStage.heightProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();

			localListViewForClients.setPrefHeight(side - (side / 6));
			localButtonShowClientDisplay.setTranslateY(side / 4);
			localButtonLetClientConnect.setTranslateY(side / 4);
		});

		final var localScene = this.scene = new Scene(localPaneRoot);
		localStage.setScene(localScene);

		this.initStage();
		this.initClientList();
		this.initControlDisplayButton();
		this.initLetClientConnectButton();
		this.initButton(localButtonLetClientConnect, localButtonShowClientDisplay);

		localStage.show();
	}

	private ArrayList<String> createFakeData() {
		final ArrayList<String> toRet = new ArrayList<>();

		for (int i = 1; i <= 5; i++) {
			toRet.add("Client " + i);
		}

		return toRet;
	}

	// region `init*()` methods.
	private void initStage() {
		final double screenHeight = JavaFxApp.PRIMARY_SCREEN_RECT.getHeight();
		final double screenWidth = JavaFxApp.PRIMARY_SCREEN_RECT.getWidth();
		final double height = screenHeight / 2;
		final double width = screenWidth / 2;

		// final var dialog = WaitingDialogBuilder.open();
		this.stage.setTitle("AndroidGameController - Home");
		// this.stage.initStyle(StageStyle.TRANSPARENT);
		this.stage.setResizable(true);

		this.stage.setMinHeight(height);
		this.stage.setMinWidth(width);
		this.stage.setHeight(height);
		this.stage.setWidth(width);

		// this.stage.widthProperty().addListener((p_observable, p_oldValue, p_newValue)
		// -> {
		this.stage.setX((screenWidth - this.stage.getWidth()) / 2);
		this.stage.setY((screenHeight - this.stage.getHeight()) / 2);
		// });

		this.paneRoot.getChildren().forEach(c -> {
			final var cbckKeyPress = c.getOnKeyPressed();

			c.setOnKeyPressed(cbckKeyPress == null

					? JavaFxApp.cbckKeyPressedForUndo::handle

					: p_keyEvent -> {
						JavaFxApp.cbckKeyPressedForUndo.handle(p_keyEvent);
						cbckKeyPress.handle(p_keyEvent);
					}

			);
		});
	}

	private void initClientList() {
		this.listViewClientList.setOnKeyPressed(p_keyEvent -> {
			// System.out.println("Key pressed with list-view in focus!");
			switch (p_keyEvent.getCode()) {

				case DELETE ->
					this.listViewClientList.getItems()
							.removeAll(this.listViewClientList.getSelectionModel().getSelectedItems());

				default -> {
					//
				}

			}
		});

		this.listViewClientList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.listViewClientList.setCellFactory(p_toGenFor -> new ListCell<String>() {

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

	private void initControlDisplayButton() {
		final Button button = this.buttonShowClientDisplay;

		button.setOnAction(p_event -> {
			final var selections = this.listViewClientList.getSelectionModel().getSelectedItems();

			// for (final String s : selections) { }

			System.out.printf(

					"Controls button pressed for clients \"%s\".%n",
					selections.toString()

			);
		});
	}

	private void initLetClientConnectButton() {
		final Button button = this.buttonLetClientConnect;

		button.setOnAction(p_event -> {
			Backend.EDT.publish(EventAwaitOneClient.create());
		});
	}

	private void initButton(final Button... p_button) {
		for (final Button b : p_button)
			b.setFont(this.fontForButtons);
	}
	// endregion

	private static void cbckKeyPressedForUndo(final KeyEvent p_keyEvent) {
		final boolean alt = p_keyEvent.isAltDown();
		final boolean meta = p_keyEvent.isMetaDown();
		final boolean shift = p_keyEvent.isShiftDown();
		final boolean ctrl = p_keyEvent.isControlDown();

		final boolean onlyCtrl = ctrl && !(alt || meta || shift);

		// TODO: Typing bugs?! Check *this out!:*

		switch (p_keyEvent.getCode()) {

			case Y -> {
				if (!onlyCtrl)
					return;

				System.out.println("`Ctrl` + `Y` seen.");
			}

			case Z -> {
				if (!ctrl)
					return;

				if (onlyCtrl)
					System.out.println("`Ctrl` + `Z` seen.");

				if (shift)
					System.out.println("`Ctrl` + `Shift` + `Z` seen.");
			}

			default -> {
			}

		}
	}

}
