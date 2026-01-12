/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *     and the following disclaimer in the documentation and/or other materials provided with the
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or
 *     promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package rise_of_duebel.dyn4j;

import KAGO_framework.view.DrawTool;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class ColliderBody extends Body {

    public static final long MASK_ALL = -1L;
    public static final long MASK_ENTITY = 1L << 0;
    public static final long MASK_ENTITY_PLAYER = 1L << 1;
    public static final long MASK_PLATFORM = 1L << 2;
    public static final long MASK_PLATFORM_MOVING = 1L << 3;
    public static final long MASK_SPIKE = 1L << 4;
    public static final long MASK_SPIKE_MOVING = 1L << 5;

    public static final long FILTER_DEFAULT = MASK_ALL;
    public static final long FILTER_PLATFORM = MASK_ALL & ~MASK_PLATFORM_MOVING;
    public static final long FILTER_MOVING_PLATFORM = MASK_ALL & ~MASK_PLATFORM;
    public static final long FILTER_SPIKE = MASK_ALL & ~MASK_SPIKE_MOVING;
    public static final long FILTER_SPIKE_PLATFORM = MASK_ALL & ~MASK_SPIKE;
    private static final Logger log = LoggerFactory.getLogger(ColliderBody.class);

    /** The color of the object */
    protected Color color;

    /**
     * Default constructor.
     */
    public ColliderBody() {
        this(PhysicsRenderer.getRandomColor());
    }

    /**
     * Constructor.
     * @param color a set color
     */
    public ColliderBody(Color color) {
        this.color = color;
    }

    /**
     * Draws the body.
     * <p>
     * Only coded for polygons and circles.
     * @param g the graphics object to render to
     * @param scale the scaling factor
     */
    public void render(DrawTool g, double scale) {
        this.render(g, scale, this.color);
    }

    /**
     * Draws the body.
     * <p>
     * Only coded for polygons and circles.
     * @param drawTool the graphics object to render to
     * @param scale the scaling factor
     * @param color the color to render the body
     */
    public void render(DrawTool drawTool, double scale, Color color) {
        // point radius
        final int pr = 4;

        // save the original transform
        Graphics2D g = drawTool.getGraphics2D();
        AffineTransform ot = g.getTransform();

        // transform the coordinate system from world coordinates to local coordinates
        AffineTransform lt = new AffineTransform();
        lt.translate(this.transform.getTranslationX() * scale, this.transform.getTranslationY() * scale);
        lt.rotate(this.transform.getRotationAngle());

        // apply the transform
        g.transform(lt);

        // loop over all the body fixtures for this body
        for (BodyFixture fixture : this.fixtures) {
            this.renderFixture(g, scale, fixture, color);
        }

        // draw a center point
        Ellipse2D.Double ce = new Ellipse2D.Double(
                this.getLocalCenter().x * scale - pr * 0.5,
                this.getLocalCenter().y * scale - pr * 0.5,
                pr,
                pr);
        g.setColor(Color.WHITE);
        g.fill(ce);
        g.setColor(Color.DARK_GRAY);
        g.draw(ce);

        // set the original transform
        g.setTransform(ot);
    }

    /**
     * Renders the given fixture.
     * @param g the graphics object to render to
     * @param scale the scaling factor
     * @param fixture the fixture to render
     * @param color the color to render the fixture
     */
    protected void renderFixture(Graphics2D g, double scale, BodyFixture fixture, Color color) {
        // get the shape on the fixture
        Convex convex = fixture.getShape();

        // brighten the color if asleep
        if (this.isAtRest()) {
            color = color.brighter();
        }

        // render the fixture
        PhysicsRenderer.render(g, convex, scale, color);
    }

    /**
     * Returns this body's color.
     * @return Color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the body's color
     * @param color the color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    public void setUserData(String userData) {
        super.setUserData(userData);
    }

    public String getUserData() {
        return (String) super.getUserData();
    }

    /*public void setPosition(Vector2 vec) {
        this.getTransform().setTranslationX(vec.x);
        this.getTransform().setTranslationX(vec.y);
    }*/

    public Vector2 getPosition() {
        BodyFixture fixture = this.getFixture();
        Convex shape = fixture.getShape();
        if (shape instanceof Polygon) {
            // LEFT-TOP
            return ((Polygon) shape).getVertices()[0].copy().add(this.getTransform().getTranslationX(), this.getTransform().getTranslationY());

        } else {
            // KA
            return new Vector2(this.getTransform().getTranslationX(), this.getTransform().getTranslationY());
        }
    }

    public double getX() {
        return this.getPosition().x;
    }

    public double getY() {
        return this.getPosition().y;
    }

    public Vector2 getSize() {
        BodyFixture fixture = this.getFixture();
        Convex shape = fixture.getShape();
        if (shape instanceof Ellipse) {
            return new Vector2(((Ellipse) shape).getWidth(), ((Ellipse) shape).getHeight());

        } else {
            // Funktioniert vielleicht nicht bei allen shape
            AABB aabb = ((Polygon) shape).createAABB(this.getTransform());
            return new Vector2(aabb.getWidth(), aabb.getHeight());
        }
    }

    public double getWidth() {
        return this.getSize().x;
    }

    public double getHeight() {
        return this.getSize().y;
    }

    public BodyFixture getFixture() {
        return this.fixtures.get(0);
    }
}
