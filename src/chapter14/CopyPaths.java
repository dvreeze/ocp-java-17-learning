/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chapter14;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This program copies a given source path to a given target path. The code originates from the OCP study guide.
 *
 * @author Chris de Vreeze
 */
public class CopyPaths {

    public static void copyPath(Path source, Path target) {
        try {
            Files.copy(source, target);
            if (Files.isDirectory(source)) {
                // Symbolic links are not followed
                try (Stream<Path> childStream = Files.list(source)) {
                    // Recursion
                    childStream.forEach(ch -> copyPath(ch, target.resolve(ch.getFileName())));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Objects.checkIndex(1, args.length);
        Path source = Path.of(args[0]);
        Path target = Path.of(args[1]);

        Files.createDirectories(target.getParent());

        copyPath(source, target);
    }
}
