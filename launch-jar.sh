java -XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -Dsun.zip.disableMemoryMapping=true -Xms128M -Xmx1024M \
--module-path ./lib/openjfx/ --add-modules javafx.controls,javafx.fxml \
-jar ./Android-Game-Controller-Server.jar