/*
 * Copyright (c) 2016-2019 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.farm.guts;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.zerocracy.Farm;
import com.zerocracy.Item;
import com.zerocracy.ItemFrom;
import com.zerocracy.Project;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.cactoos.Scalar;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.IoCheckedScalar;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Guts.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class GsProject implements Project {

    /**
     * Farm.
     */
    private final Farm farm;

    /**
     * Query.
     */
    private final String query;

    /**
     * Dirs to add.
     */
    private final IoCheckedScalar<Iterable<Directive>> dirs;

    /**
     * Ctor.
     * @param frm Farm
     * @param qry The query
     * @param anex Dirs to add
     */
    GsProject(final Farm frm, final String qry,
        final Scalar<Iterable<Directive>> anex) {
        this(frm, qry, new IoCheckedScalar<>(anex));
    }

    /**
     * Ctor.
     * @param frm Farm
     * @param qry The query
     * @param anex Dirs to add
     */
    GsProject(final Farm frm, final String qry,
        final IoCheckedScalar<Iterable<Directive>> anex) {
        this.farm = frm;
        this.query = qry;
        this.dirs = anex;
    }

    @Override
    public String pid() {
        return "";
    }

    @Override
    public Item acq(final String file) throws IOException {
        final Iterator<Project> pkts = this.farm.find(this.query).iterator();
        final XML start = new XMLDocument(
            new Xembler(GsProject.start()).xmlQuietly()
        );
        final XML before;
        if (pkts.hasNext()) {
            before = pkts.next().acq(file).read(
                path -> {
                    final XML res;
                    if (path.toFile().exists()
                        && path.toFile().length() != 0L) {
                        res = new XMLDocument(path);
                    } else {
                        res = start;
                    }
                    return res;
                }
            );
        } else {
            before = start;
        }
        final String res = new XMLDocument(
            new Xembler(this.dirs.value()).applyQuietly(
                before.node()
            )
        ).toString();
        return new ItemFrom(res);
    }

    /**
     * Start XML.
     * @return Dirs
     */
    private static Iterable<Directive> start() {
        final Map<String, Object> attrs = new HashMap<>(0);
        attrs.put(
            "availableProcessors",
            Runtime.getRuntime().availableProcessors()
        );
        attrs.put(
            "freeMemory",
            Runtime.getRuntime().freeMemory()
        );
        attrs.put(
            "maxMemory",
            Runtime.getRuntime().maxMemory()
        );
        attrs.put(
            "totalMemory",
            Runtime.getRuntime().totalMemory()
        );
        attrs.put(
            "totalThreads",
            Thread.getAllStackTraces().size()
        );
        final int process = GsProject.process();
        if (process > 0) {
            attrs.put("pid", process);
        }
        return new Directives()
            .pi("xml-stylesheet", "href='/xsl/guts.xsl' type='text/xsl'")
            .add("guts")
            .add("jvm")
            .add("attrs")
            .append(
                new Joined<>(
                    new Mapped<Map.Entry<String, Object>, Iterable<Directive>>(
                        ent -> new Directives().add("attr")
                            .attr("id", ent.getKey())
                            .set(ent.getValue()).up(),
                        attrs.entrySet()
                    )
                )
            )
            .up()
            .add("threads")
            .append(
                new Joined<>(
                    new Mapped<Thread, Iterable<Directive>>(
                        thread -> new Directives()
                            .add("thread")
                            .attr("id", thread.getName())
                            .attr("state", thread.getState())
                            .attr("daemon", thread.isDaemon())
                            .attr("alive", thread.isAlive())
                            .up(),
                        Thread.getAllStackTraces().keySet()
                    )
                )
            )
            .up()
            .up();
    }

    /**
     * Return process ID (PID).
     * @return PID number
     */
    private static int process() {
        final String rmxn = ManagementFactory.getRuntimeMXBean().getName();
        final int res;
        if (rmxn.isEmpty() || !rmxn.contains("@")) {
            res = -1;
        } else {
            final String part = rmxn.split("@")[0];
            res = Integer.parseInt(part);
        }
        return res;
    }
}
