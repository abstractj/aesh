/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.aesh.console;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ConsoleTest extends BaseConsoleTest {


    @Test
    @Ignore
    public void multiLine() throws Throwable {
        invokeTestConsole(new Setup() {
            @Override
            public void call(Console console, OutputStream out) throws IOException {
                out.write(("ls \\").getBytes());
                out.write((Config.getLineSeparator()).getBytes());
                out.write(("foo \\").getBytes());
                out.write((Config.getLineSeparator()).getBytes());
                out.write(("bar"+Config.getLineSeparator()).getBytes());
                out.flush();
            }
        }, new Verify() {
            @Override
            public int call(Console console, ConsoleOperation op) {
                assertEquals("ls foo bar", op.getBuffer());
                return 0;
            }
        });
    }

    @Test
    @Ignore
    public void testPrintWriter() throws Throwable {
        invokeTestConsole(new Setup() {
            @Override
            public void call(Console console, OutputStream out) throws IOException {
                PrintStream pout = console.getShell().out();
                pout.write(("ls \\").getBytes());
                pout.write((Config.getLineSeparator()).getBytes());
                pout.write(("foo \\").getBytes());
                pout.write((Config.getLineSeparator()).getBytes());
                pout.write(("bar"+Config.getLineSeparator()).getBytes());
                out.flush();
            }
        }, new Verify() {
            @Override
            public int call(Console console, ConsoleOperation op) {
                assertEquals("ls foo bar", op.getBuffer());
                return 0;
            }
        });
    }
}
