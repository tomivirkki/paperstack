package org.vaadin.virkki.paperstack.client;

import org.vaadin.virkki.paperstack.client.gwt.PaperStackWidgetListener;

import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;

/**
 * PaperStack rpc interfaces.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
public abstract class Rpc {
    public interface PaperStackServerRpc extends ServerRpc,
            PaperStackWidgetListener {
    }

    public interface PaperStackClientRpc extends ClientRpc {
        void navigate(Boolean forward);
    }
}
