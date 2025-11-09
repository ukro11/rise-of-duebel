# Kago4 Base Rework

---

## Tooltip Manager
Im KeyManagerModel ein Attribut erstellen:
```java
public static KeyManagerModel KEY_TAKE_ITEM = new KeyManagerModel(KeyEvent.VK_SPACE, "Gegenstände und Essen aufheben/fallen lassen");
```

Dann Tooltip registieren lassen:
```java
Wrapper.getTooltipManager().register(
    new Tooltip(
        KeyManagerModel.KEY_TAKE_ITEM,
        (keyManager) -> {
            if (TableSpawner.isCurrentlyFocused()) {
                if (TableSpawner.getCurrentFocusedTable() instanceof TableItemIntegration) {
                    var t = ((TableItemIntegration) TableSpawner.getCurrentFocusedTable());
                    if (t == null) return null;

                    if (!t.getItems().isEmpty() && Wrapper.getLocalPlayer().getInventory().getItemInHand() == null)
                        return "Gegenstand aufheben";

                    else if (t.getItems().isEmpty() && Wrapper.getLocalPlayer().getInventory().getItemInHand() != null)
                        return "Gegenstand fallen lassen";
                
                } else if (TableSpawner.getCurrentFocusedTable() instanceof TableStorageSpawner) {
                    if (Wrapper.getLocalPlayer().getInventory().getItemInHand() != null) return null;
                    return "Gegenstand aufheben";
                }
            }

            return null;
        }
    )
);
```

---

## SoundManager

Attribut erstellen in SoundConstants:
```java
public final SoundSource SOUND_BACKGROUND;
```

Die Referenz dann instanziieren mit einer ***id*** und einem ***filename***<br>
### Wichtig: Sound-Datei muss im "sound" Ordner drin sein
*Wenn es im einem Unterordner drinne ist, dann einfach den relativen Pfad angeben*
```java
this.SOUND_BACKGROUND = new SoundSource("background", "background.wav");
```

Bei einem Event oder bestimmten Situation den Sound abspielen/pausieren/...
```java
public void onOpen(Scene last) {
    Wrapper.getSoundConstants().SOUND_BACKGROUND.setVolume(0.5);
    SoundManager.playSound(Wrapper.getSoundConstants().SOUND_BACKGROUND, true);
}
```

---

## Graphics

### Ein Image/Component dursichtig machen
```java
import KAGO_framework.view.DrawTool;

import java.awt.*;

@Override
public void draw(DrawTool drawTool) {
    // opacity von 0 bis 1 (0 = unsichtbar, 1 = sichtbar)
    drawTool.push();
    drawTool.getGraphics2D().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) this.opacity));
    drawTool.drawImage(this.image, 10, 10);
    drawTool.pop();
}
```


### Ein Image/etc. richtig skalieren
```java
import KAGO_framework.view.DrawTool;

import java.awt.*;

@Override
public void draw(DrawTool drawTool) {
    // Skaliert nicht vom Ursprung (0, 0) aus, sondern vom Zentrum des Bildes
    double centerX = this.x + this.width / 2;
    double centerY = this.y + this.height / 2;
    
    drawTool.push();
    drawTool.getGraphics2D().translate(centerX, centerY);
    drawTool.getGraphics2D().scale(0.5, 0.5);
    drawTool.getGraphics2D().translate(-centerX, -centerY);
    drawTool.drawImage(this.image, 10, 10);
    drawTool.pop();
}
```