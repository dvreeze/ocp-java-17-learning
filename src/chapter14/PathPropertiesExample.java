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

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This program tries to check some properties about NIO.2 type Path, on a Linux system.
 *
 * @author Chris de Vreeze
 */
public class PathPropertiesExample {

    public record PathPair(Path path1, Path path2) {

        public boolean bothAbsoluteOrRelative() {
            return (path1.isAbsolute() && path2.isAbsolute()) || (!path1.isAbsolute() && !path2.isAbsolute());
        }

        public boolean normalizedPathsHaveNoPathSymbols() {
            return Stream.of(path1, path2)
                    .map(Path::normalize)
                    .allMatch(p -> IntStream.range(0, p.getNameCount()).mapToObj(p::getName).noneMatch(nm -> nm.equals(Path.of(".."))));
        }
    }

    private static String quote(Path p) {
        return (p == null) ? null : String.format("\"%s\"", p);
    }

    private static String writeInfoAboutPath(Path path) {
        return String.format(
                "Path: %s. Root: %s. File name: %s. Name count: %d",
                quote(path),
                quote(path.getRoot()),
                quote(path.getFileName()),
                path.getNameCount()
        );
    }

    private static boolean computeIsAbsolute(Path path) {
        return path.getRoot() != null;
    }

    private static boolean isAbsolutePropertyHolds(Path path) {
        return path.isAbsolute() == computeIsAbsolute(path);
    }

    private static Optional<Path> computeOptionalFileName(Path path) {
        int nameCount = path.getNameCount();

        return (nameCount == 0) ? Optional.empty() : Optional.of(path.subpath(nameCount - 1, nameCount));
    }

    private static boolean getFileNamePropertyHolds(Path path) {
        return Optional.ofNullable(path.getFileName()).equals(computeOptionalFileName(path));
    }

    private static Path reconstructPath(Path path) {
        Optional<Path> rootOption = Optional.ofNullable(path.getRoot());
        Optional<Path> firstNameOption =
                (path.getNameCount() == 0) ? Optional.empty() : Optional.of(path.getName(0));
        List<Path> remainingPathNames =
                (path.getNameCount() <= 1) ? List.of() : IntStream.range(1, path.getNameCount()).mapToObj(path::getName).toList();

        Path firstName = Path.of(
                rootOption.map(Path::toString).orElse("") + firstNameOption.map(Path::toString).orElse("")
        );
        Path[] more = remainingPathNames.toArray(new Path[0]);
        String[] moreStrings = Arrays.stream(more).map(Path::toString).toArray(String[]::new);

        return Path.of(firstName.toString(), moreStrings);
    }

    private static boolean pathReconstructionPropertyHolds(Path path) {
        return path.equals(reconstructPath(path));
    }

    private static Path reconstructPathByGettingParents(Path path) {
        List<Path> elems =
                Stream.iterate(path, Objects::nonNull, Path::getParent)
                        .collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), xs -> {
                            Collections.reverse(xs);
                            return List.copyOf(xs);
                        }));
        Path[] elements = elems.toArray(Path[]::new);

        if (elements.length == 0) {
            throw new RuntimeException("A path must at least have either a root or a name element");
        }

        return Path.of(
                elements[0].toString(),
                Arrays.stream(elements).skip(1).map(Path::getFileName).map(Path::toString).toArray(String[]::new)
        );
    }

    private static boolean reversePathReconstructionPropertyHolds(Path path) {
        return path.equals(reconstructPathByGettingParents(path));
    }

    private static Path reconstructNormalizedSecondPath(Path thisPath, Path otherPath) {
        return thisPath.resolve(thisPath.relativize(otherPath)).normalize();
    }

    private static boolean pathReconstructionPropertyUsingRelativizeHolds(Path thisPath, Path otherPath) {
        return otherPath.normalize().equals(reconstructNormalizedSecondPath(thisPath, otherPath));
    }

    public static void main(String[] args) {
        // Note that we cannot create a Path without any "Path.of" parameters
        List<Path> examplePaths = List.of(
                Path.of(""),
                Path.of("/"),
                Path.of("////"),
                Path.of("home"),
                Path.of("/home"),
                Path.of("home/user/xml/test.xml"),
                Path.of("/home/user/xml/test.xml"),
                Path.of("home/user/.././user/xml/../xml/test.xml"),
                Path.of("/home/user/.././user/xml/../xml/test.xml"),
                Path.of("//home/user/.././user/xml/../xml/test.xml"),
                Path.of(".."),
                Path.of("."),
                Path.of("././test"),
                Path.of("/.././home"), // weird path
                Path.of("/home/jane/../../../../../../../home/jane/test.xml"), // security risk
                Path.of("../../../.")
        );

        System.out.println();
        examplePaths.stream().map(PathPropertiesExample::writeInfoAboutPath).forEach(System.out::println);

        System.out.println();
        boolean isAbsolutePropertyHolds = examplePaths.stream().allMatch(PathPropertiesExample::isAbsolutePropertyHolds);
        System.out.printf("Property about isAbsolute holds: %b%n", isAbsolutePropertyHolds);

        System.out.println();
        boolean getFileNamePropertyHolds = examplePaths.stream().allMatch(PathPropertiesExample::getFileNamePropertyHolds);
        System.out.printf("Property about getFileName holds: %b%n", getFileNamePropertyHolds);

        System.out.println();
        boolean pathReconstructionPropertyHolds = examplePaths.stream().allMatch(PathPropertiesExample::pathReconstructionPropertyHolds);
        System.out.printf("Property about path reconstruction holds: %b%n", pathReconstructionPropertyHolds);

        System.out.println();
        boolean reversePathReconstructionPropertyHolds = examplePaths.stream().allMatch(PathPropertiesExample::reversePathReconstructionPropertyHolds);
        System.out.printf("Property about reverse path reconstruction holds: %b%n", reversePathReconstructionPropertyHolds);

        System.out.println();
        List<PathPair> pathPairs =
                examplePaths.stream().flatMap(p1 -> examplePaths.stream().map(p2 -> new PathPair(p1, p2)))
                        .filter(PathPair::bothAbsoluteOrRelative)
                        .filter(pair -> !List.of(pair.path1, pair.path2).contains(Path.of("")))
                        .filter(PathPair::normalizedPathsHaveNoPathSymbols)
                        .toList();
        boolean pathReconstructionPropertyUsingRelativizeHolds =
                pathPairs.stream().allMatch(pair -> pathReconstructionPropertyUsingRelativizeHolds(pair.path1, pair.path2));
        System.out.printf(
                "Property about path reconstruction using relativization holds: %b%n",
                pathReconstructionPropertyUsingRelativizeHolds);
    }
}
