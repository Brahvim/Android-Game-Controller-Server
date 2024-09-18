package com.brahvim.agc.server.front;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class StageProfileChooser {

	// region Fields.
	public static final String STRING_TABLE_SECTION = "ProfileChooser";

	private static Stage stage;
	private static Scene scene;
	private static Pane paneRoot;
	private static Pane paneRow1;
	private static Pane paneRow2;

	@SuppressWarnings("unused")
	private static Button buttonSeparator;
	private static Label labelOptionsList;
	private static Label labelProfilesList;

	@SuppressWarnings("unused")
	private static ListView<String> listViewOptions;
	private static ListView<String> listViewProfiles;
	// endregion

	// Will do this for two types of data:
	// static final ArrayList<Stage> stages = new ArrayList<>();
	// - Data that needs to be shared,
	// - Data that is **optional**.

	private StageProfileChooser() {
		throw new IllegalAccessError();
	}

	public static synchronized void show() {
		if (StageProfileChooser.stage == null)
			StageProfileChooser.init();

		final var localStage = StageProfileChooser.stage;

		if (localStage.isShowing()) {
			StageProfileChooser.listViewProfiles.requestFocus();
		} else {
			localStage.show();
		}
	}

	public static synchronized void close() {
		if (StageProfileChooser.stage == null)
			return;

		StageProfileChooser.stage.hide();
	}

	public static String getFromStringTable(final String p_property) {
		return App.STRINGS.getString(StageProfileChooser.STRING_TABLE_SECTION, p_property);
	}

	public static void showStageFocusedAndCentered() {
		Platform.runLater(() -> {
			StageProfileChooser.show();
			StageProfileChooser.stage.requestFocus();
			App.centerStage(StageProfileChooser.stage);
		});
	}

	@SuppressWarnings("unused")
	private static void init() {
		final var localStage = StageProfileChooser.stage = new Stage();

		final var localLabelProfilesChooser = StageProfileChooser.labelOptionsList = new Label(

				StageProfileChooser.getFromStringTable("label")

		);

		final var localListViewProfiles = StageProfileChooser.listViewProfiles = new ListView<>();
		final var localPaneRoot = StageProfileChooser.paneRoot = new VBox(

				localLabelProfilesChooser,
				localListViewProfiles

		);

		final var localScene = StageProfileChooser.scene = new Scene(localPaneRoot);

		StageProfileChooser.initStage();
		StageProfileChooser.initTopLabel();
		StageProfileChooser.initRootPane();
		StageProfileChooser.initProfilesList();
	}

	private static void initStage() {
		final var localStage = StageProfileChooser.stage;

		localStage.setWidth(300);
		localStage.setHeight(300);

		localStage.setMinWidth(120);
		localStage.setMinHeight(248);

		localStage.setMaxWidth(App.PRIMARY_SCREEN_WIDTH);
		localStage.setMaxHeight(App.PRIMARY_SCREEN_HEIGHT);

		localStage.getIcons().add(App.AGC_ICON_IMAGE);
		localStage.setScene(StageProfileChooser.scene);
		localStage.setTitle(App.STRINGS.getString("StageTitles", "profiles"));
	}

	private static void initRootPane() {
		final var localPaneRoot = StageProfileChooser.paneRoot;
		final var localListView = StageProfileChooser.listViewProfiles;

		localPaneRoot.setStyle("-fx-background-color: rgb(0, 0, 0);"); // NOSONAR! Dis CSS!

		localPaneRoot.setOnKeyPressed(p_event -> {
			switch (p_event.getCode()) {

				case ESCAPE -> {
					StageProfileChooser.close();
				}

				default -> {
					// No defaults!
				}

			}
		});

		localPaneRoot.widthProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			final double half = side / 2;
		});

		localPaneRoot.heightProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			final double half = side / 2;

			localListView.setPrefHeight(half);
		});
	}

	private static void initTopLabel() {
		final var localLabel = StageProfileChooser.labelOptionsList;

		localLabel.setAlignment(Pos.CENTER);
		localLabel.setFont(App.FONT_LARGE);
		localLabel.setPrefWidth(App.PRIMARY_SCREEN_WIDTH);
		localLabel.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255)");
	}

	private static void initProfilesList() {
		final var localListView = StageProfileChooser.listViewProfiles;

		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
	}

}
