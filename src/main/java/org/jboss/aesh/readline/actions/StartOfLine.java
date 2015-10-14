/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.aesh.readline.actions;

import org.jboss.aesh.console.InputProcessor;
import org.jboss.aesh.readline.Action;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class StartOfLine implements Action {
    @Override
    public String name() {
        return "start-of-line";
    }

    @Override
    public void apply(InputProcessor inputProcessor) {
        inputProcessor.getBuffer().moveCursor(-inputProcessor.getBuffer().getBuffer().totalLength());
    }
}
