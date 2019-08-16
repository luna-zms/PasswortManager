# Projekt 1 im Sopra 19B

In diesem Repository entsteht euer erstes Projekt im Sopra. Für Planung, Modellierung und generelle gruppeninterne Angelegenheiten, verwendet das Wiki des Projektes. 

## PMD-Bericht

Wenn programmiert wird, vergesst nicht, regelmäßig den PMD-Bericht hier oder direkt in Eclipse zu überprüfen.

[PMD-Bericht](https://sopra.cs.tu-dortmund.de/bin/pmd.py?XXY=19B&GROUPNUMBER=1&PROJECT=1)

## Setting up Intellij

1. [Download](https://gluonhq.com/products/javafx/) JavaFX SDK for your platform and unzip it
2. In IntelliJ, press `CTRL` + `ALT` + `SHIFT` + `S`, go to `Project Settings -> Libraries` and click the `+` in the top left. 
3. Select `Java` from the dropdown and navigate to the `lib` directory of the SDK you just downloaded. Click `Ok`.
4. Go to the menu entry `Run -> Edit Configurations`. To your _run_ configuration, add `--module-path "<path-to-sdk-lib-directory>" --add-modules javafx.controls,javafx.fxml` in the `VM Options` field.
5. It should work now