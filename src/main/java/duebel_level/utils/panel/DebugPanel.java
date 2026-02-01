package duebel_level.utils.panel;

import KAGO_framework.control.ViewController;
import duebel_level.Config;
import duebel_level.Wrapper;
import duebel_level.model.debug.VisualModel;
import duebel_level.model.scene.impl.GameScene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class DebugPanel extends JPanel {

    private JLabel label = new JLabel("Screen to display:");
    private JComboBox screenToDisplay = new JComboBox();

    private JLabel label2 = new JLabel("Buffers:");
    private JComboBox buffers = new JComboBox();

    private JLabel label3 = new JLabel("Show Hitboxes:");
    private JToggleButton hitboxes = new JToggleButton("OFF");

    private JLabel label4 = new JLabel("Show FPS:");
    private JToggleButton fps = new JToggleButton("OFF");

    public DebugPanel(JFrame frame, ViewController canvas) {
        this.setLayout(new GridLayout(0, 2, 10, 8));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.requestFocus();
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = env.getDefaultScreenDevice();
        Map<String, GraphicsDevice> screenDevices = Arrays.stream(env.getScreenDevices()).collect(Collectors.toMap(GraphicsDevice::getIDstring, _gd -> _gd));

        Point loc = this.getCenteredLocationOn(gd, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        frame.setLocation(loc.x, loc.y + 100);

        this.screenToDisplay.setModel(new DefaultComboBoxModel(screenDevices.keySet().toArray(new String[0])));
        this.screenToDisplay.setSelectedItem(gd.getIDstring());
        this.screenToDisplay.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) return;

            String selected = (String) this.screenToDisplay.getSelectedItem();
            GraphicsDevice target = screenDevices.get(selected);
            Point p = this.getCenteredLocationOn(target, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

            canvas.getDrawFrame().setVisible(false);
            canvas.getDrawFrame().setLocation(p);
            canvas.recreateBackBuffer();
            canvas.getDrawFrame().setVisible(true);
            frame.setLocation(p.x, p.y + 100);
        });

        this.buffers.setModel(new DefaultComboBoxModel(new String[] { "x2", "x3" }));
        this.buffers.setSelectedItem(Config.NUM_BUFFERS == 3 ? "x3" : "x2");
        this.buffers.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) return;

            switch ((String) e.getItem()) {
                case "x2" -> Config.NUM_BUFFERS = 2;
                case "x3" -> Config.NUM_BUFFERS = 3;
            }

            canvas.getDrawFrame().setVisible(false);
            canvas.recreateBackBuffer();
            canvas.getDrawFrame().setVisible(true);
        });

        this.hitboxes.setSelected(false);
        this.hitboxes.addItemListener(e -> {
            boolean on = (e.getStateChange() == ItemEvent.SELECTED);
            Wrapper.getLocalPlayer().setShowHitbox(on);
            GameScene.getInstance().setDrawHitboxes(on);
            this.hitboxes.setText(on ? "ON" : "OFF");
        });

        this.fps.setSelected(false);
        this.fps.addItemListener(e -> {
            boolean on = (e.getStateChange() == ItemEvent.SELECTED);
            VisualModel vis = GameScene.getInstance().getVisual("fps-component");
            if (vis != null) vis.toggleVisible(on);
            this.fps.setText(on ? "ON" : "OFF");
        });

        this.add(this.label);
        this.add(this.screenToDisplay);

        this.add(this.label2);
        this.add(this.buffers);

        this.add(this.label3);
        this.add(this.hitboxes);

        this.add(this.label4);
        this.add(this.fps);
    }

    private Point getCenteredLocationOn(GraphicsDevice device, int w, int h) {
        Rectangle b = device.getDefaultConfiguration().getBounds();
        int x = b.x + (b.width - w) / 2;
        int y = b.y + (b.height - h) / 2;
        return new Point(x, y);
    }
}
