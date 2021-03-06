/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

/**
 * 
 */
package org.easysoa.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author jguillemotte
 *
 */
class CopyOutputStream extends OutputStream {

    //StringBuilder copy = new StringBuilder();
    private ByteArrayOutputStream copyOut = new ByteArrayOutputStream();
    private OutputStream out;

    public CopyOutputStream(OutputStream out) {
        super();
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        super.flush(); // ?
        copyOut.write(b);
    }

    public byte[] getCopy() {
        return copyOut.toByteArray();
    }
}