set JPACKAGE_PATH="jpackage"
set INPUT_PATH="D:\Dev\kitchenreceiptprint\target\"
set OUTPUT_PATH="D:\Dev\kitchenreceiptprint\out\"
set MAIN_JAR="kitchenreceiptprint-1.0-SNAPSHOT-shaded.jar"
set MAIN_CLASS="com.kitchenreceiptprint.Main"
set APP_NAME="kitchenreceiptprint"
set ICON_PATH="D:\Dev\kitchenreceiptprint\src\main\resources\icon.png"

%JPACKAGE_PATH% ^
  --input %INPUT_PATH% ^
  --dest %OUTPUT_PATH% ^
  --name %APP_NAME% ^
  --main-jar %MAIN_JAR% ^
  --main-class %MAIN_CLASS% ^
  --type exe ^
  --icon %ICON_PATH% ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut

pause
