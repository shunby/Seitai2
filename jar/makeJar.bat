javac -encoding UTF-8 -d ../jar/ ../src/seitai/world/tile/*.java ../src/seitai/living/spawner/*.java ../src/seitai/*.java ../src/seitai/world/*.java ../src/seitai/living/*.java ../src/seitai/living/ai/*.java ../src/seitai/living/eater/*.java ../src/seitai/living/plant/*.java
pause
jar cvfm excutable\seitai2.jar seitai\MANIFEST.MF seitai\world\tile\*.class seitai\living\spawner\*.class seitai\*.class seitai\living\*.class seitai\living\ai\*.class seitai\living\eater\*.class seitai\living\plant\*.class seitai\world\*.class ../res\fxml\*.fxml ../res\image\living\*.png ../res\image\tile\*.png
pause
