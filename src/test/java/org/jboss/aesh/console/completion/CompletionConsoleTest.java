/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aesh.console.completion;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.jboss.aesh.cl.internal.ProcessedCommandBuilder;
import org.jboss.aesh.cl.internal.ProcessedOptionBuilder;
import org.jboss.aesh.cl.parser.CommandLineParserException;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.internal.ProcessedOption;
import org.jboss.aesh.cl.parser.AeshCommandLineParser;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.complete.CompletionRegistration;
import org.jboss.aesh.console.AeshConsoleCallback;
import org.jboss.aesh.console.BaseConsoleTest;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOperation;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.terminal.Key;
import org.junit.Test;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class CompletionConsoleTest extends BaseConsoleTest {

    private final Key completeChar =  Key.CTRL_I;

    private static final byte[] LINE_SEPARATOR = Config.getLineSeparator().getBytes();

    @Test
    public void completion() throws Exception {
        invokeTestConsole(3, new Setup() {
            @Override
            public void call(Console console, OutputStream out) throws IOException {
                Completion completion = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("foo"))
                            co.addCompletionCandidate("foobar");
                    }
                };
                console.addCompletion(completion);


                Completion completion2 = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("bar")) {
                            co.addCompletionCandidate("barfoo");
                            co.doAppendSeparator(false);
                        }
                    }
                };
                console.addCompletion(completion2);

                Completion completion3 = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("le")) {
                            co.addCompletionCandidate("less");
                            co.setSeparator(':');
                        }
                    }
                };
                console.addCompletion(completion3);

                out.write("foo".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();

                out.write("bar".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();

                out.write("le".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();
            }
        }, new Verify() {
           private int count = 0;
           @Override
           public int call(Console console, ConsoleOperation op) {
               if(count == 0) {
                   assertEquals("foobar ", op.getBuffer());
               }
               else if(count == 1)
                   assertEquals("barfoo", op.getBuffer());
               else if(count == 2) {
                   assertEquals("less:", op.getBuffer());
               }
               count++;
               return 0;
           }
        });
    }

    @Test
    public void removeCompletion() throws Exception {
        invokeTestConsole(3, new Setup() {
            @Override
            public void call(Console console, OutputStream out) throws IOException {
                Completion completion = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("foo"))
                            co.addCompletionCandidate("foobar");
                    }
                };
                CompletionRegistration completionRegistration = console.addCompletion(completion);

                Completion completion2 = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("bar")) {
                            co.addCompletionCandidate("barfoo");
                            co.doAppendSeparator(false);
                        }
                    }
                };
                console.addCompletion(completion2);

                Completion completion3 = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("le")) {
                            co.addCompletionCandidate("less");
                            co.setSeparator(':');
                        }
                    }
                };
                console.addCompletion(completion3);
                completionRegistration.removeCompletion();

                out.write("foo".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();

                out.write("bar".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();

                out.write("le".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();
            }
        }, new Verify() {
           private int count = 0;
           @Override
           public int call(Console console, ConsoleOperation op) {
               if(count == 0) {
                   assertEquals("foo", op.getBuffer());
               }
               else if(count == 1)
                   assertEquals("barfoo", op.getBuffer());
               else if(count == 2) {
                   assertEquals("less:", op.getBuffer());
               }
               count++;
               return 0;
           }
        });
    }

    @Test
    public void disableCompletion() throws Exception {
        invokeTestConsole(3, new Setup() {
            @Override
            public void call(Console console, OutputStream out) throws IOException {
                Completion completion = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("foo"))
                            co.addCompletionCandidate("foobar");
                    }
                };
                console.addCompletion(completion);

                Completion completion2 = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("bar")) {
                            co.addCompletionCandidate("barfoo");
                            co.doAppendSeparator(false);
                        }
                    }
                };
                console.addCompletion(completion2);

                Completion completion3 = new Completion() {
                    @Override
                    public void complete(CompleteOperation co) {
                        if(co.getBuffer().equals("le")) {
                            co.addCompletionCandidate("less");
                            co.setSeparator(':');
                        }
                    }
                };
                console.addCompletion(completion3);

                out.write("foo".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();


                out.write("bar".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();


                out.write("le".getBytes());
                out.write(completeChar.getFirstValue());
                out.write(LINE_SEPARATOR);
                out.flush();
            }
        }, new Verify() {
            private int count = 0;

            @Override
            public int call(Console console, ConsoleOperation op) {
                if (count == 0){
                    assertEquals("foobar ", op.getBuffer());
                    console.setCompletionEnabled(false);
                }
                else if (count == 1){
                    assertEquals("bar", op.getBuffer());
                    console.setCompletionEnabled(true);
                }
                else if(count == 2) {
                    assertEquals("less:", op.getBuffer());
                }
                count++;
                return 0;
            }
        });
    }

    @Test
    public void completionWithOptions() throws IOException, InterruptedException, CommandLineParserException {

        final ProcessedCommand param = new ProcessedCommandBuilder().name("less")
                .description("less -options <files>")
                .create();

        param.addOption(new ProcessedOptionBuilder().shortName('f').name("foo").hasValue(true).type(String.class).create());

        final CommandLineParser<Command> parser = new AeshCommandLineParser(param);
        final StringBuilder builder = new StringBuilder();

        Completion completion = new Completion() {
            @Override
            public void complete(CompleteOperation co) {
                if(parser.getProcessedCommand().getName().startsWith(co.getBuffer())) {
                    co.addCompletionCandidate(parser.getProcessedCommand().getName());
                }
                // commandline longer than the name
                else if(co.getBuffer().startsWith(parser.getProcessedCommand().getName())){
                   if(co.getBuffer().length() > parser.getProcessedCommand().getName().length())  {
                      if(co.getBuffer().endsWith(" --")) {
                         for(ProcessedOption o : parser.getProcessedCommand().getOptions()) {
                             co.addCompletionCandidate("--"+o.getName());
                             builder.append(o.getName()+" ");
                         }
                          co.setOffset(co.getOffset());
                      }
                      else if(co.getBuffer().endsWith(" -")) {
                          for(ProcessedOption o : parser.getProcessedCommand().getOptions()) {
                              co.addCompletionCandidate("-"+o.getShortName());
                              builder.append("-"+o.getShortName()+" ");
                          }
                      }
                   }
                }
            }
        };
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(outputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Settings settings = new SettingsBuilder()
                .inputStream(pipedInputStream)
                .outputStream(new PrintStream(byteArrayOutputStream))
                .logging(true)
                .create();

        Console console = new Console(settings);

        console.addCompletion(completion);

        console.setConsoleCallback(new CompletionConsoleCallback2(console));
        console.start();

        outputStream.write("le".getBytes());
        outputStream.write(completeChar.getFirstValue());
        outputStream.flush();
        Thread.sleep(150);

        assertEquals("less ", console.getBuffer());

        outputStream.write(LINE_SEPARATOR);
        outputStream.write("less --".getBytes());
        outputStream.write(completeChar.getFirstValue());
        outputStream.flush();

        Thread.sleep(200);

        console.stop();
    }

    class CompletionConsoleCallback extends AeshConsoleCallback {
        private transient int count = 0;
        final Console console;
        final OutputStream outputStream;
        CompletionConsoleCallback(Console console, OutputStream outputStream) {
            this.outputStream = outputStream;
            this.console = console;
        }
        @Override
        public int execute(ConsoleOperation output) throws InterruptedException {
            if(count == 0) {
                assertEquals("foobar ", output.getBuffer());
            }
            else if(count == 1)
                assertEquals("barfoo", output.getBuffer());
            else if(count == 2) {
                assertEquals("less:", output.getBuffer());
                console.stop();
            }

            count++;
            return 0;
        }
    }

    class CompletionConsoleCallback2 extends AeshConsoleCallback {
        private int count = 0;
        Console console;
        CompletionConsoleCallback2(Console console) {
            this.console = console;
        }
        @Override
        public int execute(ConsoleOperation output) throws InterruptedException {
            if(count == 0) {
                assertEquals("less ", output.getBuffer());
            }

            count++;
            return 0;
        }
    }

    class CompletionConsoleCallback3 extends AeshConsoleCallback {
        private int count = 0;
        final Console console;
        final OutputStream outputStream;
        CompletionConsoleCallback3(Console console, OutputStream outputStream) {
            this.outputStream = outputStream;
            this.console = console;
        }
        @Override
        public int execute(ConsoleOperation output) throws InterruptedException {
            if(count == 0) {
                assertEquals("foo", output.getBuffer());
            }
            else if(count == 1)
                assertEquals("barfoo", output.getBuffer());
            else if(count == 2) {
                assertEquals("less:", output.getBuffer());
                console.stop();
            }

            count++;
            return 0;
        }
    }

}
