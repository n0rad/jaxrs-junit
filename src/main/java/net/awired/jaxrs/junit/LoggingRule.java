/**
 *
 *     Copyright (C) Awired.net
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package net.awired.jaxrs.junit;

import java.io.OutputStream;
import java.io.PrintStream;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public abstract class LoggingRule implements TestRule {

    protected Description description;

    private PrintStream out = System.out;
    private PrintStream err = System.err;

    private OutputStream byteOut;
    private OutputStream byteErr;

    private PrintStream testOut;
    private PrintStream testErr;

    protected SynchronizedTempOutputStreams synchronizedOutputStreams;
    protected boolean bufferedOut = false;

    @Override
    public Statement apply(Statement base, Description description) {
        this.description = description;
        return statement(base);
    }

    protected Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    if (bufferedOut) {
                        synchronizedOutputStreams.writeToStreams();
                    }
                    throw t;
                } finally {
                    after();
                }
            }
        };
    }

    public void before() throws Throwable {
        if (bufferedOut) {
            synchronizedOutputStreams = new SynchronizedTempOutputStreams();
            byteOut = synchronizedOutputStreams.buildStream(out);
            byteErr = synchronizedOutputStreams.buildStream(err);

            testOut = new PrintStream(byteOut);
            testErr = new PrintStream(byteErr);

            System.setErr(testErr);
            System.setOut(testOut);

            out.println("Running test: " + description.getDisplayName());
        }
    }

    protected void after() {
        System.setErr(err);
        System.setOut(out);
    }

    public boolean isBufferedOut() {
        return bufferedOut;
    }

    public void setBufferedOut(boolean bufferedOut) {
        this.bufferedOut = false;
        //        this.bufferedOut = bufferedOut;
    }

}
