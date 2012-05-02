package org.vaadin.virkki.paperstack.client;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.Connector;

/**
 * PaperStack state class.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
@SuppressWarnings("serial")
public class PaperStackState extends ComponentState {
    private Connector previousComponent;
    private Connector currentComponent;
    private Connector nextComponent;
    private String previousBackgroundColor;
    private String currentBackgroundColor;
    private String nextBackgroundColor;

    private String paperBackColor = "#00B4F0";
    private String paperEdgeColor = "#000000";
    private int cloneElementCount = 7;

    private int initialCornerX = 5;
    private int initialCornerY = 10;
    private int initialCanvasWidth = 35;
    private int initialCanvasHeight = 55;

    public final Connector getPreviousComponent() {
        return previousComponent;
    }

    public final void setPreviousComponent(final Connector previousComponent) {
        this.previousComponent = previousComponent;
    }

    public final Connector getCurrentComponent() {
        return currentComponent;
    }

    public final void setCurrentComponent(final Connector currentComponent) {
        this.currentComponent = currentComponent;
    }

    public final Connector getNextComponent() {
        return nextComponent;
    }

    public final void setNextComponent(final Connector nextComponent) {
        this.nextComponent = nextComponent;
    }

    public final String getPreviousBackgroundColor() {
        return previousBackgroundColor;
    }

    public final void setPreviousBackgroundColor(final String previousBackgroundColor) {
        this.previousBackgroundColor = previousBackgroundColor;
    }

    public final String getCurrentBackgroundColor() {
        return currentBackgroundColor;
    }

    public final void setCurrentBackgroundColor(final String currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }

    public final String getNextBackgroundColor() {
        return nextBackgroundColor;
    }

    public final void setNextBackgroundColor(final String nextBackgroundColor) {
        this.nextBackgroundColor = nextBackgroundColor;
    }

    public final String getPaperBackColor() {
        return paperBackColor;
    }

    public final void setPaperBackColor(final String paperBackColor) {
        this.paperBackColor = paperBackColor;
    }

    public final String getPaperEdgeColor() {
        return paperEdgeColor;
    }

    public final void setPaperEdgeColor(final String paperEdgeColor) {
        this.paperEdgeColor = paperEdgeColor;
    }

    public final int getCloneElementCount() {
        return cloneElementCount;
    }

    public final void setCloneElementCount(final int cloneElementCount) {
        this.cloneElementCount = cloneElementCount;
    }

    public final int getInitialCornerX() {
        return initialCornerX;
    }

    public final void setInitialCornerX(final int initialCornerX) {
        this.initialCornerX = initialCornerX;
    }

    public final int getInitialCornerY() {
        return initialCornerY;
    }

    public final void setInitialCornerY(final int initialCornerY) {
        this.initialCornerY = initialCornerY;
    }

    public final int getInitialCanvasWidth() {
        return initialCanvasWidth;
    }

    public final void setInitialCanvasWidth(final int initialCanvasWidth) {
        this.initialCanvasWidth = initialCanvasWidth;
    }

    public final int getInitialCanvasHeight() {
        return initialCanvasHeight;
    }

    public final void setInitialCanvasHeight(final int initialCanvasHeight) {
        this.initialCanvasHeight = initialCanvasHeight;
    }

    public final void setComponents(final Connector previous, final Connector current, final Connector next) {
        setPreviousComponent(previous);
        setCurrentComponent(current);
        setNextComponent(next);
    }

    public final void setBackgroundColors(final String previous, final String current, final String next) {
        setPreviousBackgroundColor(previous);
        setCurrentBackgroundColor(current);
        setNextBackgroundColor(next);
    }

}
