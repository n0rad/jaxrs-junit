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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class SynchronizedTempOutputStreams {

    private final class SyncsOutputStream extends OutputStream {

        private PrintStream real;

        private SyncsOutputStream(PrintStream realStream) {

            real = realStream;
        }

        @Override
        public synchronized void write(int b) throws IOException {
            int newcount = count + 1;
            if (newcount > buf.length) {
                buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
                sbuf = Arrays.copyOf(sbuf, Math.max(sbuf.length << 1, newcount));
            }
            buf[count] = (byte) b;
            sbuf[count] = this;
            count = newcount;
        }
    }

    protected byte buf[] = new byte[128];
    protected SyncsOutputStream sbuf[] = new SyncsOutputStream[128];
    protected int count;

    public OutputStream buildStream(final PrintStream realStream) {
        return new SyncsOutputStream(realStream);
    }

    public void writeToStreams() {
        for (int i = 0; i < count; i++) {
            byte b = buf[i];
            sbuf[i].real.write(b);
            if (b == '\n') {
                sbuf[i].real.println();
                sbuf[i].real.flush();
                try {
                    Thread.sleep(1); // let stream flush by system
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buffer.append((char) buf[i]); // TODO cast is bad here for accent char
        }
        return buffer.toString();
    }

}
