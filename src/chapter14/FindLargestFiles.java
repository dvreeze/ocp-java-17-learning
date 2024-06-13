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
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
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

    private static BasicFileAttributes getBasicFileAttributes(Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static long getFileSize(Path path) {
        return getBasicFileAttributes(path).size();
    }

    private static Optional<String> probeContentType(Path path) {
        try {
            return Optional.ofNullable(Files.probeContentType(path));
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

        NumberFormat formatter = NumberFormat.getCompactNumberInstance();

        for (Path path : largestFiles) {
            var fileAttributes = getBasicFileAttributes(path);
            long size = fileAttributes.size();
            System.out.printf("File '%s'. Size: %s%n", path, formatter.format(size));
            System.out.printf("\tByte size: %d%n", size);
            System.out.printf("\tIs regular file: %b%n", fileAttributes.isRegularFile());
            System.out.printf("\tIs directory: %b%n", fileAttributes.isDirectory());
            System.out.printf("\tIs symbolic link: %b%n", fileAttributes.isSymbolicLink());
            System.out.printf("\tIs other kind of file: %b%n", fileAttributes.isOther());
            System.out.printf("\tProbed content type: %s%n", probeContentType(path).orElse("<unknown>"));
            System.out.printf("\tCreation time: %s%n", fileAttributes.creationTime());
            System.out.printf("\tLast modified time: %s%n", fileAttributes.lastModifiedTime());
            System.out.printf("\tLast access time: %s%n", fileAttributes.lastAccessTime());

            if (fileAttributes instanceof PosixFileAttributes posixFileAttributes) {
                System.out.printf("\tOwner: %s%n", posixFileAttributes.owner());
                System.out.printf("\tGroup: %s%n", posixFileAttributes.group());
                System.out.printf(
                        "\tOwner can read: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.OWNER_READ)
                );
                System.out.printf(
                        "\tOwner can write: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.OWNER_WRITE)
                );
                System.out.printf(
                        "\tOwner can execute: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.OWNER_EXECUTE)
                );
                System.out.printf(
                        "\tGroup can read: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.GROUP_READ)
                );
                System.out.printf(
                        "\tGroup can write: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.GROUP_WRITE)
                );
                System.out.printf(
                        "\tGroup can execute: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.GROUP_EXECUTE)
                );
                System.out.printf(
                        "\tOthers can read: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.OTHERS_READ)
                );
                System.out.printf(
                        "\tOthers can write: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.OTHERS_WRITE)
                );
                System.out.printf(
                        "\tOthers can execute: %s%n",
                        posixFileAttributes.permissions().contains(PosixFilePermission.OTHERS_EXECUTE)
                );
            }
        }
    }
}
