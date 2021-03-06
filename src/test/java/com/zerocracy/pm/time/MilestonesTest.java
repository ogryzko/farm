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
package com.zerocracy.pm.time;

import com.zerocracy.FkProject;
import java.time.LocalDate;
import java.time.Month;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link Milestones}.
 *
 * @since 1.0
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class MilestonesTest {
    @Test
    public void iterateAllAddedMilestones() throws Exception {
        final Milestones milestones = new Milestones(new FkProject())
            .bootstrap();
        final String target = "gh:ML-test";
        milestones.add(
            target,
            // @checkstyle MagicNumber (1 line)
            LocalDate.of(2018, Month.JANUARY, 23)
        );
        MatcherAssert.assertThat(
            milestones.iterate(),
            Matchers.hasItem(Matchers.equalTo(target))
        );
    }

    /**
     * Test if milestones with spaces can be added.
     * @todo #2051:30m/DEV Adding milestones which doesn't match the pattern
     *  '[a-z]{2}:[A-Z0-9a-z.\-#/]+' fail with exception on XML validation
     *  Validation happens inside com.jcabi.xml.StrictXML constructor,
     *  outside of this project's codebase. Un-ignore this test and remove
     *  comment after impediments are resolved.
     */
    @Test
    @Ignore
    public void addMilestoneWithSpace() throws Exception {
        final Milestones milestones = new Milestones(new FkProject())
            .bootstrap();
        final String target = "gh:V 1.1";
        milestones.add(
            target,
            // @checkstyle MagicNumber (1 line)
            LocalDate.of(2018, Month.JANUARY, 23)
        );
        MatcherAssert.assertThat(
            milestones.iterate(),
            Matchers.hasItem(Matchers.equalTo(target))
        );
    }
}
