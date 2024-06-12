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

import java.io.*;
import java.util.List;

/**
 * Java serialization example.
 *
 * @author Chris de Vreeze
 */
public class SerializationExample {

    public record Quote(String attributedTo, String text, List<String> subjects) implements Serializable {
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        var quote = new Quote(
                "Wim Hof",
                "If you can learn how to use your mind, anything is possible.",
                List.of("Willpower", "Strength", "Health")
        );

        var bos = new ByteArrayOutputStream();

        try (var oos = new ObjectOutputStream(new BufferedOutputStream(bos))) {
            oos.writeObject(quote);
        }

        var bytes = bos.toByteArray();

        Quote readQuote;
        try (var ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes)))) {
            readQuote = (Quote) ois.readObject();
        }

        System.out.println(readQuote);

        if (!readQuote.equals(quote)) {
            throw new IllegalStateException("Deserialization result different from serialized Quote");
        }
    }
}
