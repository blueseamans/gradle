/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.launcher.daemon.testing;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.gradle.launcher.daemon.context.DaemonContext;
import org.gradle.launcher.daemon.context.DefaultDaemonContext;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * by Szczepan Faber, created at: 2/13/12
 */
public class DaemonContextParser {
    public static DaemonContext parseFrom(String source) {
        Pattern pattern = Pattern.compile("^.*DefaultDaemonContext\\[javaHome=([^\\n]+),daemonRegistryDir=([^\\n]+),pid=([^\\n]+),idleTimeout=(.+?),daemonOpts=([^\\n]+)].*",
                Pattern.MULTILINE + Pattern.DOTALL);
        Matcher matcher = pattern.matcher(source);

        if (matcher.matches()) {
            String javaHome = matcher.group(1);
            String daemonRegistryDir = matcher.group(2);
            Long pid = Long.parseLong(matcher.group(3));
            Integer idleTimeout = Integer.decode(matcher.group(4));
            //below won't work if some jvm opt contain comma but for testing purposes it should do
            List<String> jvmOpts = Lists.newArrayList(Splitter.on(',').split(matcher.group(5)));
            return new DefaultDaemonContext(new File(javaHome), new File(daemonRegistryDir), pid, idleTimeout, jvmOpts);
        } else {
            throw new IllegalStateException("unable to parse DefaultDaemonContext from source: [" + source + "].");
        }
    }
}
