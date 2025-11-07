### Ein Image/Component dursichtig machen
````java
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
````

### Ein Image/etc. richtig skalieren 
````java
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
````