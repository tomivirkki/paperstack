package org.vaadin.virkki.paperstack;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.vaadin.virkki.paperstack.client.PaperStackState;
import org.vaadin.virkki.paperstack.client.Rpc.PaperStackClientRpc;
import org.vaadin.virkki.paperstack.client.Rpc.PaperStackServerRpc;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

/**
 * PaperStack is a component container whose subcomponents are presented
 * sequentially, one subcomponent at a time. User can switch between the
 * subcomponents by mouse dragging the upper right corner of a view revealing
 * the underlying subcomponent simultaneously. The transition effect simulates
 * leafing through a stack of papers.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 * 
 */
@SuppressWarnings("serial")
public class PaperStack extends AbstractComponentContainer implements PaperStackServerRpc {
    public static final String DEFAULT_BACKGROUND_COLOR = "#fff";
    public static final Pattern HEX_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    private final List<Component> components = new ArrayList<Component>();
    private final Map<Component, String> backgroundColors = new HashMap<Component, String>();

    public PaperStack() {
        setWidth(600.0f, Unit.PIXELS);
        setHeight(400.0f, Unit.PIXELS);
        registerRpc(this);
    }

    @Override
    public PaperStackState getState() {
        return (PaperStackState) super.getState();
    }

    @Override
    public void pageChanged(final boolean forwards) {
        final int currentComponentIndex = components.indexOf(getState().getCurrentComponent());
        final Component newCurrent = components.get(currentComponentIndex + (forwards ? 1 : -1));
        getState().setCurrentComponent(newCurrent);
        updateStateInternal();

        fireEvent(new PageChangeEvent(this));
    }

    @Override
    public int getComponentCount() {
        return components.size();
    }

    @Override
    public Iterator<Component> getComponentIterator() {
        return Collections.unmodifiableList(components).iterator();
    }

    private void updateStateInternal() {
        final Component current = (Component) getState().getCurrentComponent();
        Component previous = null;
        Component next = null;

        if (current != null) {
            if (!components.contains(current)) {
                throw new IllegalArgumentException("Component not in the component list");
            }
            final Iterator<Component> iterator = getComponentIterator();
            while (iterator.hasNext()) {
                final Component component = iterator.next();
                component.setVisible(false);
                if (component.equals(current) && iterator.hasNext()) {
                    next = iterator.next();
                    next.setVisible(true);
                }
                if ((next == null) && iterator.hasNext()) {
                    previous = component;
                }
            }

            if (previous != null) {
                previous.setVisible(true);
            }

            current.setVisible(true);

        }
        getState().setComponents(previous, current, next);
        getState().setBackgroundColors(backgroundColors.get(previous), backgroundColors.get(current),
                backgroundColors.get(next));

        requestRepaint();
    }

    /**
     * Adds a component with a pre-specified background color. The color must
     * comply with hexadecimal notation to have effect.
     * 
     * @param c
     *            the component to be added.
     * @param backgroundColorHex
     *            the background color of the component.
     */
    public void addComponent(final Component component, final String backgroundColorHex) {
        if (components.contains(component)) {
            throw new IllegalArgumentException("Component already added");
        }
        components.add(component);
        backgroundColors.put(component, backgroundColorHex);
        super.addComponent(component);
        if (getState().getCurrentComponent() == null) {
            getState().setCurrentComponent(component);
        }
        updateStateInternal();

    }

    @Override
    public void addComponent(final Component component) {
        addComponent(component, DEFAULT_BACKGROUND_COLOR);
    }

    @Override
    public void removeComponent(final Component component) {
        super.removeComponent(component);
        components.remove(component);
        backgroundColors.remove(component);
        if ((component == getState().getCurrentComponent()) && !components.isEmpty()) {
            getState().setCurrentComponent(components.get(0));
        }
        updateStateInternal();
    }

    @Override
    public void replaceComponent(final Component oldComponent, final Component newComponent) {
        final int oldLocation = components.indexOf(oldComponent);
        final int newLocation = components.indexOf(newComponent);

        if (oldLocation == -1) {
            components.remove(newComponent);
            addComponent(newComponent);
        } else if (newLocation == -1) {
            removeComponent(oldComponent);
            components.remove(newComponent);
            components.add(oldLocation, newComponent);
            updateStateInternal();
        } else {
            Collections.swap(components, oldLocation, newLocation);
            updateStateInternal();
        }
    }

    /**
     * Sets the colors for the paper and the paper's edge. The colors must
     * comply with hexadecimal notation to have effect.
     * 
     * @param paperBackColorHex
     *            color of the back side of the paper.
     * @param paperEdgeColorHex
     *            color of the paper edge.
     * 
     */
    public void setPaperColor(final String paperBackColorHex, final String paperEdgeColorHex) {
        if ((paperBackColorHex != null) && HEX_PATTERN.matcher(paperBackColorHex).matches()) {
            getState().setPaperBackColor(paperBackColorHex);
        }
        if ((paperEdgeColorHex != null) && HEX_PATTERN.matcher(paperEdgeColorHex).matches()) {
            getState().setPaperEdgeColor(paperEdgeColorHex);
        }
        requestRepaint();
    }

    /**
     * Defines the number of "clone elements" (a client side member) that are
     * positioned in a certain way to simulate the (diagonal) borderline of two
     * subcomponents. Increasing the count results in more natural appearance
     * but reduces performance.
     * 
     * @param cloneElementCount
     */
    public void setCloneElementCount(final int cloneElementCount) {
        if (cloneElementCount >= 4) {
            getState().setCloneElementCount(cloneElementCount);
            requestRepaint();
        }
    }

    public void setFoldCoordinates(final int initialCanvasWidth, final int initialCanvasHeight,
            final int initialCornerX, final int initialCornerY) {
        getState().setInitialCanvasHeight(initialCanvasHeight);
        getState().setInitialCanvasWidth(initialCanvasWidth);
        getState().setInitialCornerX(initialCornerX);
        getState().setInitialCornerY(initialCornerY);
        requestRepaint();
    }

    /**
     * An event received by PageChangeListeners when the visible subcomponent is
     * changed to the next one.
     */
    public class PageChangeEvent extends Event {
        private static final long serialVersionUID = 6834729362872065434L;

        public PageChangeEvent(final Component source) {
            super(source);
        }

        @Override
        public final PaperStack getSource() {
            return (PaperStack) super.getSource();
        }
    }

    /**
     * A listener that receives PageChangeEvents.
     */
    public interface PageChangeListener extends Serializable {
        void pageChange(PageChangeEvent event);
    }

    /**
     * Adds a PageChangeListener to this PaperStack.
     * 
     * @param listener
     *            the PageChangeListener to be added.
     */
    public void addListener(final PageChangeListener listener) {
        addListener(PageChangeEvent.class, listener, PAGE_CHANGE_METHOD);
    }

    private static final Method PAGE_CHANGE_METHOD;
    static {
        try {
            PAGE_CHANGE_METHOD = PageChangeListener.class.getDeclaredMethod("pageChange",
                    new Class[] { PageChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException("Internal error finding methods in PaperStack");
        }
    }

    /**
     * Navigates the views forward/backward.
     * 
     * @param forward
     */
    public void navigate(final boolean forward) {
        getRpcProxy(PaperStackClientRpc.class).navigate(forward);
    }

}
