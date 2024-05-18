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

package chapter07;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Much simplified XML DOM tree support, to illustrate sealed interfaces and records. It is extremely inefficient,
 * and immutable thread-safe collections should be used instead, but that's not the point here.
 *
 * @author Chris de Vreeze
 */
public class Xml {

    // The interfaces and records below are implicitly static members

    public sealed interface Node permits Element, Text {

        static Node deepCopy(Node node) {
            if (node instanceof Element elem) {
                // Recursive
                return new Element(
                        elem.name(),
                        new HashMap<>(elem.attributes()),
                        elem.children().stream().map(Node::deepCopy).collect(Collectors.toList())
                );
            } else if (node instanceof Text text) {
                return text;
            } else {
                throw new RuntimeException();
            }
        }
    }

    public record Element(
            QName name,
            Map<QName, String> attributes,
            List<Node> children
    ) implements Node {

        // Defensive copies all over the place (extremely inefficient, and not even thread-safe)

        public Element {
            attributes = new HashMap<>(attributes);
            children = children.stream().map(Node::deepCopy).collect(Collectors.toList());
        }

        @Override
        public Map<QName, String> attributes() {
            return new HashMap<>(attributes);
        }

        @Override
        public List<Node> children() {
            return children.stream().map(Node::deepCopy).collect(Collectors.toList());
        }

        public List<Element> childElements() {
            return children().stream().filter(n -> n instanceof Element).map(n -> (Element) n).collect(Collectors.toList());
        }

        public List<Element> filterDescendantElementsOrSelf(Predicate<Element> p) {
            // Extremely inefficient
            var copiedElem = (Element) Node.deepCopy(this);
            // Recursive
            return Stream.concat(
                    Stream.of(copiedElem).filter(p),
                    copiedElem.childElements().stream().flatMap(che -> che.filterDescendantElementsOrSelf(p).stream())
            ).collect(Collectors.toList());
        }

        public List<Element> descendantElementsOrSelf() {
            return filterDescendantElementsOrSelf(e -> true);
        }

        public String elemText() {
            List<String> textStrings =
                    children.stream()
                            .filter(n -> n instanceof Text)
                            .map(n -> ((Text) n).text())
                            .toList();
            return String.join("", textStrings);
        }
    }

    public record Text(String text) implements Node {
    }

    public static void main(String[] args) {
        // "Test" program
        var elemTree =
                new Element(
                        new QName("root"),
                        new HashMap<>(),
                        List.of(
                                new Element(
                                        new QName("childElem"),
                                        Collections.emptyMap(),
                                        List.of(
                                                new Element(
                                                        new QName("grandChildElem"),
                                                        Collections.emptyMap(),
                                                        List.of(new Text("text"))
                                                )
                                        )
                                ),
                                new Element(
                                        new QName("textElem"),
                                        Collections.emptyMap(),
                                        List.of(new Text("text"))),
                                new Element(
                                        new QName("emptyElem"),
                                        Collections.emptyMap(),
                                        Collections.emptyList()))
                );
        System.out.println(elemTree);
        System.out.println();

        var resultElems1 = elemTree.filterDescendantElementsOrSelf(e -> e.name().getLocalPart().equals("grandChildElem"));
        System.out.printf(
                "grandChild elements: %s%n",
                resultElems1);
        System.out.println();

        var resultElems2 = elemTree.descendantElementsOrSelf().stream().filter(e -> e.name().getLocalPart().equals("grandChildElem")).collect(Collectors.toList());
        System.out.printf(
                "grandChild elements (again): %s%n",
                resultElems2);

        System.out.println();
        System.out.printf("resultElems1 == resultElems2: %b%n", resultElems1 == resultElems2);
        System.out.printf("resultElems1.equals(resultElems2): %b%n", resultElems1.equals(resultElems2));

        List<String> textStrings =
                elemTree.descendantElementsOrSelf().stream()
                        .map(Element::elemText)
                        .distinct()
                        .toList();
        String combinedText = String.join("", textStrings);

        System.out.println();
        System.out.printf("Combined deduplicated element text: %s%n", combinedText);
    }
}
