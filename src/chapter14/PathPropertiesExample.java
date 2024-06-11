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
import java.util.List;
import java.util.Optional;

/**
 * This program tries to check some properties about NIO.2 type Path, on a Linux system.
 *
 * @author Chris de Vreeze
 */
public class PathPropertiesExample {

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

    private static boolean isAbsolutePropertyHolds(Path path) {
        boolean isAbsolute = path.isAbsolute();
        boolean hasRoot = path.getRoot() != null;
        return isAbsolute == hasRoot;
    }

    private static boolean getFileNamePropertyHolds(Path path) {
        Optional<Path> optionalFileName = Optional.ofNullable(path.getFileName());

        int nameCount = path.getNameCount();
        Optional<Path> optionalComputedFileName =
                (nameCount == 0) ? Optional.empty() : Optional.of(path.subpath(nameCount - 1, nameCount));

        return optionalFileName.equals(optionalComputedFileName);
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
    }
}
