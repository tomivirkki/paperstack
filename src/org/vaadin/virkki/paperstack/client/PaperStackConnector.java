package org.vaadin.virkki.paperstack.client;

import org.vaadin.virkki.paperstack.PaperStack;
import org.vaadin.virkki.paperstack.client.Rpc.PaperStackClientRpc;
import org.vaadin.virkki.paperstack.client.Rpc.PaperStackServerRpc;
import org.vaadin.virkki.paperstack.client.gwt.PaperStackWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;

/**
 * Client side PaperStack connector.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
@SuppressWarnings("serial")
@Connect(PaperStack.class)
public final class PaperStackConnector extends AbstractComponentContainerConnector {

    private final PaperStackServerRpc rpc = RpcProxy.create(PaperStackServerRpc.class, this);

    public PaperStackConnector() {
        registerRpc(PaperStackClientRpc.class, new PaperStackClientRpc() {
            @Override
            public void navigate(final Boolean forward) {
                ((PaperStackWidget) getWidget()).navigate(forward);
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
    public void updateCaption(final ComponentConnector connector) {
        // Captions not supported
    }

    @Override
    public void onStateChanged(final StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        final PaperStackState state = (PaperStackState) getState();
        final PaperStackWidget widget = (PaperStackWidget) getWidget();

        widget.setCloneCount(state.getCloneElementCount());
        widget.setPaperColors(state.getPaperBackColor(), state.getPaperEdgeColor());
        widget.setFoldCoordinates(state.getInitialCanvasWidth(), state.getInitialCanvasHeight(),
                state.getInitialCornerX(), state.getInitialCornerY());

        widget.setPreviousWidget(extractWidget(state.getPreviousComponent()),
                state.getPreviousBackgroundColor());
        widget.setCurrentWidget(extractWidget(state.getCurrentComponent()), state.getCurrentBackgroundColor());
        widget.setNextWidget(extractWidget(state.getNextComponent()), state.getNextBackgroundColor());
    }

    private Widget extractWidget(final Connector connector) {
        Widget widget = null;
        if (connector != null) {
            widget = ((ComponentConnector) connector).getWidget();
        }
        return widget;
    }

}
