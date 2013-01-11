package org.vaadin.virkki.paperstack.client;

import org.vaadin.virkki.paperstack.PaperStack;
import org.vaadin.virkki.paperstack.client.Rpc.PaperStackClientRpc;
import org.vaadin.virkki.paperstack.client.Rpc.PaperStackServerRpc;
import org.vaadin.virkki.paperstack.client.gwt.PaperStackWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;

/**
 * Client side PaperStack connector.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
@SuppressWarnings("serial")
@Connect(PaperStack.class)
public final class PaperStackConnector extends
        AbstractComponentContainerConnector {

    private final PaperStackServerRpc rpc = RpcProxy.create(
            PaperStackServerRpc.class, this);

    public PaperStackConnector() {
        registerRpc(PaperStackClientRpc.class, new PaperStackClientRpc() {
            @Override
            public void navigate(final Boolean forward) {
                getWidget().navigate(forward);
            }
        });
    }

    @Override
    protected Widget createWidget() {
        final PaperStackWidget widget = GWT.create(PaperStackWidget.class);
        widget.setListener(rpc);
        return widget;
    }

    @Override
    public void onStateChanged(final StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        final PaperStackState state = getState();
        final PaperStackWidget widget = getWidget();

        widget.setCloneCount(state.cloneElementCount);
        widget.setPaperColors(state.paperBackColor, state.paperEdgeColor);
        widget.setFoldCoordinates(state.initialCanvasWidth,
                state.initialCanvasHeight, state.initialCornerX,
                state.initialCornerY);

        widget.setPreviousWidget(extractWidget(state.previousComponent),
                state.previousBackgroundColor);
        widget.setCurrentWidget(extractWidget(state.currentComponent),
                state.currentBackgroundColor);
        widget.setNextWidget(extractWidget(state.nextComponent),
                state.nextBackgroundColor);
    }

    private Widget extractWidget(final Connector connector) {
        Widget widget = null;
        if (connector != null) {
            widget = ((ComponentConnector) connector).getWidget();
        }
        return widget;
    }

    @Override
    public void onConnectorHierarchyChange(
            final ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {

    }

    @Override
    public void updateCaption(final ComponentConnector connector) {
        // Captions not supported
    }

    @Override
    public PaperStackState getState() {
        return (PaperStackState) super.getState();
    }

    @Override
    public PaperStackWidget getWidget() {
        return (PaperStackWidget) super.getWidget();
    }

}
