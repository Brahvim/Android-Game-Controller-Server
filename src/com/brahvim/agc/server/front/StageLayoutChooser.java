package com.brahvim.agc.server.front;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class StageLayoutChooser {

	// region Fields.
	public static final String STRING_TABLE_SECTION = "LayoutChooser";

	private static Stage stage;
	private static Scene scene;
	private static Pane paneRoot;
	private static Pane paneButtons;

	@SuppressWarnings("unused")
	private static Button buttonSeparator;
	private static Button buttonAddLayout;
	private static Button buttonDelLayout;
	private static Label labelLayoutChooser;

	@SuppressWarnings("unused")
	private static Label labelLayoutOptions;
	private static ListView<String> listViewLayouts;

	@SuppressWarnings("unused")
	private static ListView<String> listViewOptions;
	// endregion

	// Will do this for two types of data:
	// static final ArrayList<Stage> stages = new ArrayList<>();
	// - Data that needs to be shared,
	// - Data that is **optional**.

	private StageLayoutChooser() {
		throw new IllegalAccessError();
	}

	public static synchronized void show() {
		if (StageLayoutChooser.stage == null)
			StageLayoutChooser.init();

		final var localStage = StageLayoutChooser.stage;

		if (localStage.isShowing()) {
			StageLayoutChooser.listViewLayouts.requestFocus();
		} else {
			localStage.show();
		}
	}

	public static synchronized void close() {
		if (StageLayoutChooser.stage == null)
			return;

		StageLayoutChooser.stage.hide();
	}

	public static String getFromStringTable(final String p_property) {
		return StageHome.STRINGS.getString(StageLayoutChooser.STRING_TABLE_SECTION, p_property);
	}

	public static void showStageFocusedAndCentered() {
		Platform.runLater(() -> {
			StageLayoutChooser.show();
			StageLayoutChooser.stage.requestFocus();
			StageHome.centerStage(StageLayoutChooser.stage);
		});
	}

	@SuppressWarnings("unused")
	private static void init() {
		final var localStage = StageLayoutChooser.stage = new Stage();

		final var localButtonAddLayout = StageLayoutChooser.buttonAddLayout = new Button(

				StageLayoutChooser.getFromStringTable("add")

		);

		final var localButtonDelLayout = StageLayoutChooser.buttonDelLayout = new Button(

				StageLayoutChooser.getFromStringTable("del")

		);

		final var localLabelLayoutChooser = StageLayoutChooser.labelLayoutChooser = new Label(

				StageLayoutChooser.getFromStringTable("label")

		);

		final var localListViewLayouts = StageLayoutChooser.listViewLayouts = new ListView<>();

		final var localPaneButtons = StageLayoutChooser.paneButtons = new HBox(

				localButtonAddLayout,
				localButtonDelLayout

		);

		final var localPaneRoot = StageLayoutChooser.paneRoot = new VBox(

				localLabelLayoutChooser,
				localListViewLayouts,
				localPaneButtons

		);

		final var localScene = StageLayoutChooser.scene = new Scene(localPaneRoot);

		StageLayoutChooser.initStage();
		StageLayoutChooser.initTopLabel();
		StageLayoutChooser.initRootPane();
		StageLayoutChooser.initAddButton();
		StageLayoutChooser.initDelButton();
		StageLayoutChooser.initLayoutsList();
	}

	private static void initStage() {
		final var localStage = StageLayoutChooser.stage;

		localStage.setWidth(300);
		localStage.setHeight(300);

		localStage.setMinWidth(120);
		localStage.setMinHeight(248);

		localStage.setMaxWidth(StageHome.PRIMARY_SCREEN_WIDTH);
		localStage.setMaxHeight(StageHome.PRIMARY_SCREEN_HEIGHT);

		localStage.setScene(StageLayoutChooser.scene);
		localStage.getIcons().add(StageHome.AGC_ICON_IMAGE);
		localStage.setTitle(StageHome.STRINGS.getString("StageTitles", "showLayout"));
	}

	private static void initRootPane() {
		final var localPaneRoot = StageLayoutChooser.paneRoot;
		final var localListView = StageLayoutChooser.listViewLayouts;
		final var localButtonAdd = StageLayoutChooser.buttonAddLayout;
		final var localButtonDel = StageLayoutChooser.buttonDelLayout;

		localPaneRoot.setStyle("-fx-background-color: rgb(0, 0, 0);"); // NOSONAR! Dis CSS!

		localPaneRoot.setOnKeyPressed(p_event -> {
			switch (p_event.getCode()) {

				case ESCAPE -> {
					StageLayoutChooser.close();
				}

				default -> {
					// No defaults!
				}

			}
		});

		localPaneRoot.widthProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			final double half = side / 2;

			localButtonAdd.setPrefWidth(half);
			localButtonDel.setPrefWidth(half);
		});

		localPaneRoot.heightProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			final double half = side / 2;

			localListView.setPrefHeight(half);
		});
	}

	private static void initTopLabel() {
		final var localLabel = StageLayoutChooser.labelLayoutChooser;

		localLabel.setAlignment(Pos.CENTER);
		localLabel.setFont(StageHome.FONT_LARGE);
		localLabel.setPrefWidth(StageHome.PRIMARY_SCREEN_WIDTH);
		localLabel.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255)");
	}

	private static void initAddButton() {
		final var localButton = StageLayoutChooser.buttonAddLayout;

		localButton.setCursor(Cursor.HAND);
		localButton.setFont(StageHome.FONT_LARGE);
		localButton.setLayoutY(StageLayoutChooser.paneButtons.getTranslateY());
		localButton.setStyle("-fx-background-color: #808080; -fx-text-fill: darkblue");
	}

	private static void initDelButton() {
		final var localButton = StageLayoutChooser.buttonDelLayout;

		localButton.setCursor(Cursor.HAND);
		localButton.setFont(StageHome.FONT_LARGE);
		localButton.setLayoutY(StageLayoutChooser.paneButtons.getTranslateY());
		localButton.setStyle("-fx-background-color: #808080; -fx-text-fill: darkred");
	}

	private static void initLayoutsList() {
		final var localListView = StageLayoutChooser.listViewLayouts;

		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
	}

}
