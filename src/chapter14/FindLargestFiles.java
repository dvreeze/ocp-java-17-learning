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
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Program that finds the largest 30 files in a directory tree, optionally taking a file extension into account.
 * Symbolic links are not followed.
 *
 * @author Chris de Vreeze
 */
public class FindLargestFiles {

    public record SearchConfig(int numberOfResults, int maxSearchDepth) {
    }

    private static final SearchConfig config = new SearchConfig(
            Integer.parseInt(System.getProperty("numberOfResults", "30")),
            Integer.parseInt(System.getProperty("maxSearchDepth", "25"))
    );

    private static boolean hasOptionalExtension(Path path, Optional<String> optionalExtension) {
        return optionalExtension.stream().allMatch(ext -> path.getFileName().toString().endsWith(ext));
    }

    private static long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<Path> findLargestFiles(Path startDir, Optional<String> optionalExtension, SearchConfig config) {
        try (var pathStream =
                     Files.find(
                             startDir,
                             config.maxSearchDepth(),
                             (p, a) -> a.isRegularFile() && hasOptionalExtension(p, optionalExtension))) {
            return pathStream
                    .parallel()
                    .sorted(Comparator.comparingLong(FindLargestFiles::getFileSize).reversed())
                    .limit(config.numberOfResults())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);

        Path startDir = Path.of(args[0]);
        Optional<String> optionalExtension = (args.length >= 2) ? Optional.of(args[1]) : Optional.empty();

        List<Path> largestFiles = findLargestFiles(startDir, optionalExtension, config);

        try {
            NumberFormat formatter = NumberFormat.getCompactNumberInstance();

            for (Path path : largestFiles) {
                long size = Files.size(path);
                System.out.printf("File '%s'. Size: %s%n", path, formatter.format(size));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
