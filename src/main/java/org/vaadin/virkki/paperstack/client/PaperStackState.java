package org.vaadin.virkki.paperstack.client;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;

/**
 * PaperStack state class.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
@SuppressWarnings("serial")
public class PaperStackState extends AbstractComponentState {
    public Connector previousComponent;
    public Connector currentComponent;
    public Connector nextComponent;
    public String previousBackgroundColor;
    public String currentBackgroundColor;
    public String nextBackgroundColor;

    public String paperBackColor = "#00B4F0";
    public String paperEdgeColor = "#000000";
    public int cloneElementCount = 7;

    public int initialCornerX = 5;
    public int initialCornerY = 10;
    public int initialCanvasWidth = 35;
    public int initialCanvasHeight = 55;

    public final void setComponents(final Connector previous,
            final Connector current, final Connector next) {
        previousComponent = previous;
        currentComponent = current;
        nextComponent = next;
    }

    public final void setBackgroundColors(final String previous,
            final String current, final String next) {
        previousBackgroundColor = previous;
        currentBackgroundColor = current;
        nextBackgroundColor = next;
    }

}
